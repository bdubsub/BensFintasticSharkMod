#!/usr/bin/env python3
"""
Bake single-keyframe MoLang channels into explicit multi-keyframe maps.

WHY: GeckoLib 4.4.7's runtime turns a bare-array animation channel whose
components are MoLang expressions (e.g. "rotation": [0, "math.sin(q.anim_time*90)*3", 0])
into ONE keyframe at t=0 with zero frame length, so the channel is sampled once and
the bone FREEZES. Blockbench's previewer hides this because it re-evaluates the
expression every frame. The proven-working mobs instead use time-keyed keyframe maps
("rotation": {"0.0":[...], "0.5":[...], ...}) which interpolate and loop.

A MoLang channel appears in TWO single-keyframe shapes, both of which freeze:
  1. bare array         "rotation": [0, "math.sin(q.anim_time*90)*3", 0]
  2. vector wrapper      "rotation": {"vector": [0, "math.sin(...)*3", 0], "easing": "linear"}
Blockbench emits shape 2 whenever a keyframe carries an easing; the 0.13 pass only
recognised shape 1, so the Shortfin Mako's move.swim/move.fast_swim (and the Harbor
Seal's bask3) stayed frozen. Both shapes are handled here.

This script samples every single-keyframe MoLang channel across [0, animation_length]
and emits a real keyframe map, producing exactly the motion Blockbench previews. Channels
that are already keyframe maps (time-keyed dicts), or hold only plain numbers (static
poses, in either shape), are left byte-identical. The MoLang vocabulary in this project is
only math.sin / math.cos (in DEGREES) of q.anim_time / query.anim_time, so every such
channel is a pure function of time and is fully bakeable (no dynamic queries exist). The
single keyframe's own "easing" is discarded: dense linear samples reproduce the curve.

Idempotent: re-running does nothing, because baked channels become time-keyed dicts with
no "vector" key, matched by neither detector.

Usage: python3 tools/bake_molang_animations.py [anim_dir]
"""
import json
import math
import os
import sys

# Samples per loop. The loop is one animation_length; ~20 linear segments of a sine
# keeps interpolation error ~1% of amplitude -> visually smooth.
STEPS = 20


class _Time:
    __slots__ = ("anim_time",)

    def __init__(self, t):
        self.anim_time = t


class _Math:
    # MoLang trig is in DEGREES.
    sin = staticmethod(lambda d: math.sin(math.radians(d)))
    cos = staticmethod(lambda d: math.cos(math.radians(d)))


def _eval_component(comp, t):
    """Evaluate one channel component (number passthrough, MoLang string -> float)."""
    if isinstance(comp, (int, float)):
        return float(comp)
    ns = {"math": _Math, "q": _Time(t), "query": _Time(t)}
    val = eval(comp, {"__builtins__": {}}, ns)  # noqa: S307 - constrained namespace
    return float(val)


def _round(v):
    r = round(v, 5)
    return 0.0 if r == 0 else r  # kill -0.0


def _fmt_time(t):
    # Keep enough time precision for quarter samples of clips whose authored
    # length does not divide cleanly at five decimal places.
    t = round(t, 6)
    if t == int(t):
        return f"{int(t)}.0"
    return f"{t:.6f}".rstrip("0")


def _is_molang_vector(vec):
    return isinstance(vec, list) and any(isinstance(c, str) for c in vec)


def _is_molang_array(value):
    """Shape 1: bare array with >=1 MoLang string."""
    return _is_molang_vector(value)


def _is_molang_wrapper(value):
    """Shape 2: single-keyframe wrapper {"vector": [...], "easing": ...} with MoLang.

    Distinguished from a real keyframe map by the presence of a literal "vector" key
    (keyframe maps are keyed by numeric time strings like "0.0")."""
    return isinstance(value, dict) and "vector" in value and _is_molang_vector(value["vector"])


def _bake_channel(value, length, close_loop=False):
    """value is a MoLang vector (>=1 string component). Return a keyframe map dict.

    close_loop: for a loop:true clip, preserve the authored endpoint unless it already
    matches the authored start within the output tolerance. This keeps non-dividing
    authored periods faithful instead of silently replacing their final sample."""
    out = {}
    for i in range(STEPS + 1):
        t = length * i / STEPS
        out[_fmt_time(t)] = [_round(_eval_component(c, t)) for c in value]
    if close_loop and STEPS >= 1:
        first = out[_fmt_time(0.0)]
        endpoint = [_round(_eval_component(c, length)) for c in value]
        if max(abs(a - b) for a, b in zip(endpoint, first)) <= 0.0001:
            out[_fmt_time(length)] = list(first)
    return out


def _max_keyframe_time(bones):
    """Effective length GeckoLib infers when animation_length is omitted: the latest
    keyframe timestamp across all time-keyed channels. Single-keyframe (bare/wrapper)
    channels carry no timestamp and contribute nothing."""
    latest = 0.0
    for channels in bones.values():
        if not isinstance(channels, dict):
            continue
        for value in channels.values():
            if isinstance(value, dict) and "vector" not in value:
                for t in value:
                    try:
                        latest = max(latest, float(t))
                    except (TypeError, ValueError):
                        pass
    return latest


def bake_animation(anim, name="<anon>"):
    """Mutate one animation object in place. Returns count of channels baked."""
    length = anim.get("animation_length")
    bones = anim.get("bones")
    if not isinstance(bones, dict):
        return 0
    if length is None:
        length = _max_keyframe_time(bones)
    close_loop = anim.get("loop") is True
    n = 0
    for _bone, channels in bones.items():
        if not isinstance(channels, dict):
            continue
        for chan_name, value in list(channels.items()):
            vec = None
            if _is_molang_array(value):
                vec = value
            elif _is_molang_wrapper(value):
                vec = value["vector"]
            if vec is not None:
                if not length:
                    # No explicit length and no keyframed sibling to infer one from:
                    # GeckoLib treats this clip as a zero-length hold, so the channel
                    # would freeze whether baked or not. Leave it untouched and warn.
                    print(f"    ! skip {name}/{_bone}.{chan_name}: indeterminate length")
                    continue
                channels[chan_name] = _bake_channel(vec, length, close_loop)
                n += 1
    return n


def bake_file(path):
    with open(path, "r", encoding="utf-8") as f:
        data = json.load(f)
    animations = data.get("animations", {})
    total = 0
    per_anim = {}
    for name, anim in animations.items():
        if isinstance(anim, dict):
            c = bake_animation(anim, name)
            if c:
                per_anim[name] = c
                total += c
    if total:
        with open(path, "w", encoding="utf-8") as f:
            json.dump(data, f, indent="\t", ensure_ascii=False)
            f.write("\n")
    return total, per_anim


def main():
    anim_dir = sys.argv[1] if len(sys.argv) > 1 else (
        "common/src/main/resources/assets/bensfintasticsharks/animations/entity"
    )
    files = sorted(f for f in os.listdir(anim_dir) if f.endswith(".animation.json"))
    grand = 0
    for fn in files:
        path = os.path.join(anim_dir, fn)
        total, per_anim = bake_file(path)
        grand += total
        if total:
            detail = ", ".join(f"{k}={v}" for k, v in per_anim.items())
            print(f"  baked {total:3d} channels  {fn}  ({detail})")
        else:
            print(f"  --              {fn}  (no MoLang bare-array channels)")
    print(f"\nTotal channels baked: {grand}")


if __name__ == "__main__":
    main()
