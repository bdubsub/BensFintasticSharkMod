#!/usr/bin/env python3
"""
Bake illegal element rotations in Blockbench block/item model JSON down to
vanilla-legal ones.

WHY: Vanilla 1.20.1 BlockElement.Deserializer only accepts element rotation angles
in {-45, -22.5, 0, 22.5, 45} about a single axis; ANY other angle throws a
JsonParseException and the whole model fails to load. Ben's Blockbench "generic
model" exports carry arbitrary angles (-67.5, 112.5, -75.32, ...).

TWO KINDS OF INPUT ROTATION
---------------------------
1. Plain vanilla `rotation` (single axis) with an illegal angle. Handled by the
   original quarter-turn path: decompose angle = 90*k + r with r in [-45, 45],
   bake the 90*k into the geometry, snap r to the nearest legal angle and keep
   it as the JSON rotation (a 0 remainder drops the key).

2. Blockbench's NONSTANDARD per-element `"rotated": [rx, ry, rz]` array: the
   FULL (possibly multi-axis) Euler rotation. When present, the vanilla
   `rotation` on the same element is only a lossy single-axis projection of it
   (in Ben's corpus it is always the x-component, snapped or clamped; y/z are
   silently dropped). `rotated` is therefore treated as rotation ground truth:

     R      = Rz(rz) . Ry(ry) . Rx(rx)          (Euler order ZYX, see below)
     pivot  = rotation.origin  (fallback: element "origin" key, then [8,8,8];
              in Ben's corpus every rotated element has rotation.origin)
     Q      = the closest of the 24 axis-aligned orientation matrices,
              argmax trace(Q^T R)  (== nearest in geodesic rotation distance)
     R_res  = Q^T . R
     axis,a = single-axis rotation nearest to R_res (optimal angle per axis via
              atan2 of the matrix elements, best axis by geodesic distance),
              angle snapped to {-45, -22.5, 0, 22.5, 45}

   Q is baked into the geometry as a sequence of at most three exact 90-degree
   axis quarter-turns about the pivot (shortest sequence found by BFS over the
   rotation group), each applied with the SAME verified single-90 machinery as
   path 1 (from/to rotation + face direction remap + UV re-encoding). The
   snapped remainder is emitted as the vanilla `rotation` about the pivot
   (dropped when 0). The per-element angular error is the geodesic rotation
   distance between the intended R and the achieved Q . Rot(axis, a).

   CENTROID CORRECTION: the angular residual is unavoidable, but applied about
   a distant pivot it would also TRANSLATE the element bodily (elements ended up
   floating off the model). Translation is gauge freedom in vanilla - it can be
   baked straight into from/to - so after baking Q the box is translated such
   that, once the game applies the emitted vanilla rotation, the element's
   CENTROID lands exactly where the intended R would have put it. The achieved
   transform is then the intended one composed with a pure rotation error about
   the element's own center: worst-case vertex drift is (half-diagonal x
   2 sin(err/2)) instead of (pivot distance x 2 sin(err/2)).

   A cheap exhaustive scan over all 24 Q x 3 axes x 5 legal angles double-checks
   the decomposition and would override it if strictly better; empirically (2000
   random rotations + Ben's whole corpus) the max-trace decomposition is already
   jointly optimal (gap ~1e-13), so this is a guard, not a behavior change.

   ALL `rotated` keys are stripped from the output (they are not vanilla and
   leaving them would let future tooling mistake stale data for truth).

EULER ORDER (empirical): Ben's corpus contains no element where `rotated` has
exactly one nonzero component, so the single-axis cross-check against vanilla
`rotation` is vacuous. Order was chosen by corpus-wide residual minimization:
composing 'ZYX' (x innermost/applied first) gives total residual 4378.2 deg over
247 multi-axis elements vs 4498.6 deg for 'XYZ', winning on 3 of 4 models
(thresher a 0.2% tie). Independent evidence: 26 shared-pivot element families
have fixed (ry, rz) with only rx varying - i.e. x is the per-element local axis
inside a group rotation, exactly ZYX composition - and Blockbench itself renders
element meshes with THREE.js rotation order 'ZYX'. The exporter's lossy vanilla
projection always picking the x-component (the innermost axis) corroborates.

Elements without rotation, or with an already-legal angle, pass through
untouched. All other keys (credit, texture_size, textures, display, groups) are
preserved.

Vanilla also requires every from/to coordinate within [-16, 32]. Quarter-turns
about distant origins can push geometry outside that box; if that happens the
WHOLE model is recentred by a uniform translation (applied to all from/to and
rotation origins - display is deliberately NOT touched) and the shift is
reported loudly so the caller can decide what to do about display transforms.

Usage: python3 tools/bake_blockitem_rotations.py IN.json OUT.json
"""
import json
import math
import sys

LEGAL_ANGLES = (-45.0, -22.5, 0.0, 22.5, 45.0)
COORD_MIN, COORD_MAX = -16.0, 32.0
FACE_ORDER = ("north", "east", "south", "west", "up", "down")
EULER_ORDER = "ZYX"  # empirically determined; see module docstring
DEFAULT_PIVOT = (8.0, 8.0, 8.0)

DIR_VEC = {
    "north": (0, 0, -1), "south": (0, 0, 1),
    "west": (-1, 0, 0), "east": (1, 0, 0),
    "up": (0, 1, 0), "down": (0, -1, 0),
}
VEC_DIR = {v: k for k, v in DIR_VEC.items()}

# Vanilla net.minecraft.client.renderer.FaceInfo vertex order per face.
# Selector triple (sx, sy, sz): 0 -> take "from", 1 -> take "to" on that axis.
FACE_CORNERS = {
    "down":  ((0, 0, 1), (0, 0, 0), (1, 0, 0), (1, 0, 1)),
    "up":    ((0, 1, 0), (0, 1, 1), (1, 1, 1), (1, 1, 0)),
    "north": ((1, 1, 0), (1, 0, 0), (0, 0, 0), (0, 1, 0)),
    "south": ((0, 1, 1), (0, 0, 1), (1, 0, 1), (1, 1, 1)),
    "west":  ((0, 1, 0), (0, 0, 0), (0, 0, 1), (0, 1, 1)),
    "east":  ((1, 1, 1), (1, 0, 1), (1, 0, 0), (1, 1, 0)),
}


def _rot90(p, axis, k):
    """Rotate point/vector p by k exact 90-degree steps about +axis (right-hand rule)."""
    x, y, z = p
    for _ in range(k % 4):
        if axis == "x":
            x, y, z = x, -z, y
        elif axis == "y":
            x, y, z = z, y, -x
        else:  # z
            x, y, z = -y, x, z
    return (x, y, z)


def _num(v):
    """Round to 5 decimals, kill -0.0, emit int when integral (clean JSON)."""
    r = round(float(v), 5)
    if r == 0:
        r = 0.0
    return int(r) if r == int(r) else r


def _corner(sel, lo, hi):
    return tuple(hi[i] if sel[i] else lo[i] for i in range(3))


def _default_uv(direction, lo, hi):
    """Vanilla BlockElement.uvsByFace - auto UVs for faces without an explicit rect."""
    x1, y1, z1 = lo
    x2, y2, z2 = hi
    return {
        "down":  [x1, 16 - z2, x2, 16 - z1],
        "up":    [x1, z1, x2, z2],
        "north": [16 - x2, 16 - y2, 16 - x1, 16 - y1],
        "south": [x1, 16 - y2, x2, 16 - y1],
        "west":  [z1, 16 - y2, z2, 16 - y1],
        "east":  [16 - z2, 16 - y2, 16 - z1, 16 - y1],
    }[direction]


def _snap(angle):
    """angle -> (k quarter-turns, snapped remainder, abs snap error in degrees)."""
    k = int(round(angle / 90.0))
    r = angle - 90.0 * k
    snapped = min(LEGAL_ANGLES, key=lambda a: abs(a - r))
    return k, snapped, abs(snapped - r)


# --------------------------------------------------------------------------
# General rotation-matrix helpers (multi-axis `rotated` support)
# --------------------------------------------------------------------------

def _rot_matrix(axis, deg):
    """3x3 right-handed rotation about +axis (matches vanilla Quaternionf.rotationAxis)."""
    r = math.radians(deg)
    c, s = math.cos(r), math.sin(r)
    if axis == "x":
        return ((1, 0, 0), (0, c, -s), (0, s, c))
    if axis == "y":
        return ((c, 0, s), (0, 1, 0), (-s, 0, c))
    return ((c, -s, 0), (s, c, 0), (0, 0, 1))


def _mat_mul(a, b):
    return tuple(tuple(sum(a[i][k] * b[k][j] for k in range(3)) for j in range(3))
                 for i in range(3))


def _mat_t(a):
    return tuple(tuple(a[j][i] for j in range(3)) for i in range(3))


def _mat_vec(m, v):
    return tuple(m[i][0] * v[0] + m[i][1] * v[1] + m[i][2] * v[2] for i in range(3))


def _rotate_about(m, p, o):
    """Apply rotation matrix m to point p about origin o."""
    return tuple(_mat_vec(m, (p[0] - o[0], p[1] - o[1], p[2] - o[2]))[i] + o[i]
                 for i in range(3))


def _trace_qt_r(q, r):
    """trace(Q^T R) - similarity of two rotation matrices."""
    return sum(q[i][j] * r[i][j] for i in range(3) for j in range(3))


def _geodesic_deg(a, b):
    """Geodesic rotation distance between two rotation matrices, in degrees."""
    c = max(-1.0, min(1.0, (_trace_qt_r(a, b) - 1.0) / 2.0))
    return math.degrees(math.acos(c))


def _euler_matrix(rd, order=EULER_ORDER):
    """Full rotation matrix of Blockbench `rotated` [rx, ry, rz] degrees."""
    mx = _rot_matrix("x", rd[0])
    my = _rot_matrix("y", rd[1])
    mz = _rot_matrix("z", rd[2])
    if order == "XYZ":
        return _mat_mul(mx, _mat_mul(my, mz))
    if order == "ZYX":
        return _mat_mul(mz, _mat_mul(my, mx))
    raise ValueError(f"unsupported Euler order {order!r}")


def _build_orientations():
    """The 24 axis-aligned orientation matrices, each with a shortest sequence of
    exact +-90-degree quarter-turn steps [(axis, k), ...] (k in {1, 3}, applied
    left-to-right) that composes to it. BFS over the rotation group; max len 3."""
    def gen_matrix(axis, k):
        cols = [_rot90(e, axis, k) for e in ((1, 0, 0), (0, 1, 0), (0, 0, 1))]
        return tuple(tuple(cols[j][i] for j in range(3)) for i in range(3))

    gens = [(ax, k) for ax in "xyz" for k in (1, 3)]
    gen_m = {g: gen_matrix(*g) for g in gens}
    ident = ((1, 0, 0), (0, 1, 0), (0, 0, 1))
    seen = {ident: []}
    frontier = [ident]
    while frontier:
        nxt = []
        for q in frontier:
            for g, gm in gen_m.items():
                q2 = _mat_mul(gm, q)  # g applied AFTER the sequence for q
                if q2 not in seen:
                    seen[q2] = seen[q] + [g]
                    nxt.append(q2)
        frontier = nxt
    assert len(seen) == 24
    return seen


ORIENTATIONS = _build_orientations()


def _best_axis_fit(m):
    """Best single-axis approximation of rotation matrix m -> (axis, angle_deg).

    Per axis the optimal angle is atan2 over the appropriate matrix elements;
    the best axis is the one whose fit is geodesically nearest to m."""
    best = None
    for axis, num, den in (
        ("x", m[2][1] - m[1][2], m[1][1] + m[2][2]),
        ("y", m[0][2] - m[2][0], m[0][0] + m[2][2]),
        ("z", m[1][0] - m[0][1], m[0][0] + m[1][1]),
    ):
        ang = math.degrees(math.atan2(num, den))
        d = _geodesic_deg(m, _rot_matrix(axis, ang))
        if best is None or d < best[0]:
            best = (d, axis, ang)
    return best[1], best[2]


def _choose_bake(r):
    """Decompose intended rotation r -> (steps, axis, snapped_angle, err_deg).

    steps: quarter-turn sequence for the baked orientation Q.
    axis/snapped_angle: residual vanilla rotation (snapped legal; may be 0).
    err_deg: geodesic distance between r and Q . Rot(axis, snapped_angle)."""
    q = max(ORIENTATIONS, key=lambda o: _trace_qt_r(o, r))
    r_res = _mat_mul(_mat_t(q), r)
    axis, ang = _best_axis_fit(r_res)
    snapped = min(LEGAL_ANGLES, key=lambda a: abs(a - ang))
    err = _geodesic_deg(r, _mat_mul(q, _rot_matrix(axis, snapped)))

    # Guard: exhaustive joint scan; overrides only if strictly better (in
    # practice it never is - the max-trace decomposition is jointly optimal).
    for q2 in ORIENTATIONS:
        for axis2 in "xyz":
            for a2 in LEGAL_ANGLES:
                e2 = _geodesic_deg(r, _mat_mul(q2, _rot_matrix(axis2, a2)))
                if e2 < err - 1e-9:
                    q, axis, snapped, err = q2, axis2, a2, e2
    return ORIENTATIONS[q], axis, snapped, err


def _remap_face(direction, face, lo, hi, nlo, nhi, axis, k, origin):
    """Quarter-turn one face: returns (new_direction, new_face_dict)."""
    uv = face.get("uv")
    if uv is None:
        uv = _default_uv(direction, lo, hi)
    uv_rot = int(face.get("rotation", 0))

    new_dir = VEC_DIR[_rot90(DIR_VEC[direction], axis, k)]
    rotated = [
        tuple(_rot90((c[0] - origin[0], c[1] - origin[1], c[2] - origin[2]), axis, k)[i]
              + origin[i] for i in range(3))
        for c in (_corner(s, lo, hi) for s in FACE_CORNERS[direction])
    ]
    new_corners = [_corner(s, nlo, nhi) for s in FACE_CORNERS[new_dir]]

    shift = None
    for s in range(4):
        if all(
            max(abs(rotated[(j + s) % 4][i] - new_corners[j][i]) for i in range(3)) < 1e-5
            for j in range(4)
        ):
            shift = s
            break
    if shift is None:
        raise ValueError(
            f"face {direction}: rotated corners do not cyclically match "
            f"FaceInfo order of {new_dir} (winding bug?)"
        )

    new_face = {}
    for key, value in face.items():
        if key == "uv":
            new_face["uv"] = [_num(c) for c in uv]
        elif key == "rotation":
            pass  # re-inserted below iff nonzero
        elif key == "cullface":
            new_face["cullface"] = VEC_DIR[_rot90(DIR_VEC[value], axis, k)]
        else:
            new_face[key] = value
    if "uv" not in new_face:  # face had no uv: rect is now mandatory
        new_face["uv"] = [_num(c) for c in uv]
    new_uv_rot = (uv_rot + 90 * shift) % 360
    if new_uv_rot:
        new_face["rotation"] = new_uv_rot
    return new_dir, new_face


def _quarter_turn_geom(lo, hi, faces, axis, k, origin):
    """Apply k exact 90-degree turns about +axis/origin to a box + its faces.

    Returns (new_lo, new_hi, new_faces). This is the verified single-axis
    machinery; multi-axis orientations chain it once per quarter-turn step."""
    corners = [
        tuple(_rot90((x - origin[0], y - origin[1], z - origin[2]), axis, k)[i] + origin[i]
              for i in range(3))
        for x in (lo[0], hi[0]) for y in (lo[1], hi[1]) for z in (lo[2], hi[2])
    ]
    new_lo = [min(c[i] for c in corners) for i in range(3)]
    new_hi = [max(c[i] for c in corners) for i in range(3)]

    remapped = {}
    for direction, face in faces.items():
        new_dir, new_face = _remap_face(direction, face, lo, hi, new_lo, new_hi,
                                        axis, k, origin)
        remapped[new_dir] = new_face
    new_faces = {d: remapped[d] for d in FACE_ORDER if d in remapped}
    return new_lo, new_hi, new_faces


def _element_pivot(el):
    """Pivot for `rotated`: rotation.origin, else element 'origin', else [8,8,8]."""
    rot = el.get("rotation")
    if isinstance(rot, dict) and "origin" in rot:
        return [float(c) for c in rot["origin"]]
    if "origin" in el:
        return [float(c) for c in el["origin"]]
    return list(DEFAULT_PIVOT)


def _rebuild_element(el, new_lo=None, new_hi=None, new_faces=None,
                     new_rot=..., drop_rotated=True):
    """Copy el preserving key order with the given replacements.

    new_rot: ... = keep original rotation key, None = drop it, dict = replace."""
    out = {}
    placed_rot = False
    for key, value in el.items():
        if key == "rotated" and drop_rotated:
            continue
        if key == "from" and new_lo is not None:
            out[key] = [_num(c) for c in new_lo]
        elif key == "to" and new_hi is not None:
            out[key] = [_num(c) for c in new_hi]
        elif key == "faces" and new_faces is not None:
            out[key] = new_faces
        elif key == "rotation" and new_rot is not ...:
            if new_rot is not None:
                out[key] = new_rot
                placed_rot = True
        else:
            out[key] = value
    if new_rot is not ... and new_rot is not None and not placed_rot:
        out["rotation"] = new_rot  # element had no rotation key: append
    return out


def bake_multi_element(el, rd):
    """Bake an element whose Blockbench `rotated` [rx,ry,rz] is rotation truth.

    Returns (new_element, kind, quarter_steps, err_deg, correction_dist)."""
    pivot = _element_pivot(el)
    r = _euler_matrix([float(c) for c in rd])
    steps, axis, snapped, err = _choose_bake(r)

    lo = [float(c) for c in el["from"]]
    hi = [float(c) for c in el["to"]]
    c0 = [(lo[i] + hi[i]) / 2.0 for i in range(3)]
    faces = el.get("faces", {})
    for st_axis, st_k in steps:
        lo, hi, faces = _quarter_turn_geom(lo, hi, faces, st_axis, st_k, pivot)
    c1 = [(lo[i] + hi[i]) / 2.0 for i in range(3)]

    # Centroid correction (see module docstring): translate the baked box so
    # that after the game applies the emitted vanilla rotation about the pivot,
    # the element's centroid lands exactly at R's intended position.
    c_target = _rotate_about(r, c0, pivot)
    if snapped != 0.0:
        s_t = _mat_t(_rot_matrix(axis, snapped))
        pre = _rotate_about(s_t, c_target, pivot)  # S^-1(c_target - pivot) + pivot
    else:
        pre = c_target
    d = [pre[i] - c1[i] for i in range(3)]
    lo = [lo[i] + d[i] for i in range(3)]
    hi = [hi[i] + d[i] for i in range(3)]

    if snapped != 0.0:
        new_rot = {"angle": _num(snapped), "axis": axis,
                   "origin": [_num(c) for c in pivot]}
    else:
        new_rot = None
    new_el = _rebuild_element(el, lo, hi, faces, new_rot)
    return new_el, "multi", len(steps), err, math.sqrt(sum(c * c for c in d))


def bake_element(el):
    """Return (new_element, kind, quarter_steps, err_deg, correction_dist).

    kind: 'untouched' | 'snapped' (angle rewritten in place) | 'rotated'
    (single-axis quarter-turns baked) | 'multi' (full `rotated` Euler baked)
    """
    rd = el.get("rotated")
    if isinstance(rd, (list, tuple)) and len(rd) == 3:
        return bake_multi_element(el, rd)

    rot = el.get("rotation")
    if not isinstance(rot, dict):
        return el, "untouched", 0, 0.0, 0.0
    angle = float(rot.get("angle", 0))
    if any(abs(angle - a) < 1e-9 for a in LEGAL_ANGLES):
        return el, "untouched", 0, 0.0, 0.0

    axis = rot["axis"]
    origin = [float(c) for c in rot.get("origin", (0, 0, 0))]
    k, snapped, err = _snap(angle)
    k %= 4

    if snapped != 0.0:
        new_rot = dict(rot)
        new_rot["angle"] = _num(snapped)
    else:
        new_rot = None  # snapped remainder of 0 -> drop the rotation key

    if k == 0:
        return _rebuild_element(el, new_rot=new_rot), "snapped", 0, err, 0.0

    lo = [float(c) for c in el["from"]]
    hi = [float(c) for c in el["to"]]
    new_lo, new_hi, new_faces = _quarter_turn_geom(lo, hi, el.get("faces", {}),
                                                   axis, k, origin)
    return (_rebuild_element(el, new_lo, new_hi, new_faces, new_rot),
            "rotated", k, err, 0.0)


def order_evidence(model):
    """Corpus evidence for the Euler order: total residual error per candidate
    order over this model's multi-axis elements. Returns {order: total_deg}."""
    totals = {"XYZ": 0.0, "ZYX": 0.0}
    n = 0
    for el in model.get("elements", []):
        rd = el.get("rotated")
        if not (isinstance(rd, (list, tuple)) and len(rd) == 3):
            continue
        n += 1
        for order in totals:
            r = _euler_matrix([float(c) for c in rd], order)
            totals[order] += _choose_bake(r)[3]
    return totals, n


def bake_model(model):
    """Bake all elements (returns new model dict) + report dict."""
    counts = {"untouched": 0, "snapped": 0, "rotated": 0, "multi": 0}
    steps = 0
    errors = []          # single-axis snap errors ('snapped'/'rotated' kinds)
    multi_errors = []    # geodesic errors of 'multi' kind
    corrections = []     # centroid-correction distances of 'multi' kind
    details = []         # (index, kind, quarter_steps, err_deg, corr_dist)
    new_elements = []
    for idx, el in enumerate(model.get("elements", [])):
        new_el, kind, k, err, corr = bake_element(el)
        counts[kind] += 1
        steps += k
        if kind == "multi":
            multi_errors.append(err)
            corrections.append(corr)
        elif kind != "untouched":
            errors.append(err)
        details.append((idx, kind, k, err, corr))
        new_elements.append(new_el)

    out = {}
    for key, value in model.items():
        out[key] = new_elements if key == "elements" else value

    lo = [min((min(e["from"][i], e["to"][i]) for e in new_elements), default=0) for i in range(3)]
    hi = [max((max(e["from"][i], e["to"][i]) for e in new_elements), default=0) for i in range(3)]
    shift = [0.0, 0.0, 0.0]
    for i in range(3):
        if hi[i] - lo[i] > COORD_MAX - COORD_MIN:
            raise ValueError(
                f"model spans {hi[i] - lo[i]} on axis {'xyz'[i]}; cannot fit in "
                f"[{COORD_MIN}, {COORD_MAX}] by translation"
            )
        if lo[i] < COORD_MIN:
            shift[i] = COORD_MIN - lo[i]
        elif hi[i] > COORD_MAX:
            shift[i] = COORD_MAX - hi[i]
    if any(shift):
        for el in new_elements:
            el["from"] = [_num(el["from"][i] + shift[i]) for i in range(3)]
            el["to"] = [_num(el["to"][i] + shift[i]) for i in range(3)]
            rot = el.get("rotation")
            if isinstance(rot, dict) and "origin" in rot:
                rot["origin"] = [_num(rot["origin"][i] + shift[i]) for i in range(3)]

    report = {
        "elements": len(new_elements),
        "untouched": counts["untouched"],
        "snapped": counts["snapped"],
        "rotated": counts["rotated"],
        "multi": counts["multi"],
        "quarter_steps": steps,
        "max_err": max(errors, default=0.0),
        "mean_err": sum(errors) / len(errors) if errors else 0.0,
        "multi_max_err": max(multi_errors, default=0.0),
        "multi_mean_err": sum(multi_errors) / len(multi_errors) if multi_errors else 0.0,
        "corr_max": max(corrections, default=0.0),
        "corr_mean": sum(corrections) / len(corrections) if corrections else 0.0,
        "details": details,
        "bounds_lo": lo,
        "bounds_hi": hi,
        "shift": shift,
    }
    return out, report


def main():
    if len(sys.argv) != 3:
        sys.exit("usage: bake_blockitem_rotations.py IN.json OUT.json")
    in_path, out_path = sys.argv[1], sys.argv[2]
    with open(in_path, "r", encoding="utf-8") as f:
        model = json.load(f)
    evidence, n_multi = order_evidence(model)
    baked, rep = bake_model(model)
    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(baked, f, indent="\t", ensure_ascii=False)
        f.write("\n")

    print(f"== {in_path}")
    print(
        f"   elements: {rep['elements']} total | {rep['untouched']} untouched | "
        f"{rep['snapped']} angle-snapped in place | {rep['rotated']} quarter-turned "
        f"single-axis | {rep['multi']} multi-axis (`rotated`) baked "
        f"({rep['quarter_steps']} x 90deg total)"
    )
    print(f"   single-axis snap error: max {rep['max_err']:.4f} deg, "
          f"mean {rep['mean_err']:.4f} deg")
    if rep["multi"]:
        print(
            f"   multi-axis geodesic error: max {rep['multi_max_err']:.4f} deg, "
            f"mean {rep['multi_mean_err']:.4f} deg over {rep['multi']} elements "
            f"(Euler order {EULER_ORDER}; this-model residual totals: "
            + ", ".join(f"{o}={t:.1f}deg" for o, t in sorted(evidence.items()))
            + ")"
        )
        print(f"   centroid correction: max {rep['corr_max']:.3f}, "
              f"mean {rep['corr_mean']:.3f} model units")
        worst = sorted((d for d in rep["details"] if d[1] == "multi"),
                       key=lambda d: -d[3])[:5]
        print("   worst multi-axis elements: "
              + ", ".join(f"#{i} {e:.1f}deg" for i, _, _, e, _ in worst))
    lo, hi = rep["bounds_lo"], rep["bounds_hi"]
    print(
        f"   coord range: [{min(lo):.3f}, {max(hi):.3f}]"
        + (" OK" if not any(rep["shift"]) else "")
    )
    if any(rep["shift"]):
        print(
            "   *** WARNING: geometry exceeded vanilla [-16, 32] box; whole model "
            f"recentred by uniform shift {[_num(s) for s in rep['shift']]} "
            "(from/to and rotation origins moved; display transforms NOT adjusted - "
            "review gui/hand translations if the icon looks offset) ***"
        )
    print(f"   -> {out_path}")


if __name__ == "__main__":
    main()
