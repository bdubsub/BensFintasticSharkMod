import hashlib
import importlib.util
import json
import pathlib
import tempfile
import unittest


SCRIPT = pathlib.Path(__file__).with_name("bake_molang_animations.py")
SPEC = importlib.util.spec_from_file_location("bake_molang_animations", SCRIPT)
BAKER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(BAKER)


class BakeMolangAnimationsTest(unittest.TestCase):
    def test_allowed_vocabulary_matches_degrees_based_molang(self):
        self.assertAlmostEqual(BAKER._eval_component("math.sin(q.anim_time * 90) * 3", 1), 3.0)
        self.assertAlmostEqual(BAKER._eval_component("math.cos(query.anim_time * 180) + 2", 0.5), 2.0)
        self.assertAlmostEqual(BAKER._eval_component("-1 + 2 * 3", 0), 5.0)

    def test_unsupported_expression_fails_closed(self):
        for expression in (
            "__import__('os').system('touch /tmp/bad')",
            "q.__class__",
            "math.tan(q.anim_time)",
            "float('nan')",
        ):
            with self.assertRaises(ValueError):
                BAKER._eval_component(expression, 0.5)

    def test_bare_and_wrapper_channels_have_stable_five_point_values(self):
        animation = {
            "animation_length": 2.0,
            "loop": True,
            "bones": {
                "body": {
                    "rotation": [0, "math.sin(q.anim_time * 90) * 3", 0],
                    "position": {"vector": [0, "math.cos(q.anim_time * 90)", 0], "easing": "linear"},
                    "scale": [1, 1, 1],
                    "already": {"0.0": [0, 0, 0], "2.0": [1, 1, 1]},
                }
            },
        }
        original_static = list(animation["bones"]["body"]["scale"])
        self.assertEqual(BAKER.bake_animation(animation, "fixture"), 2)
        for channel in ("rotation", "position"):
            self.assertEqual(list(animation["bones"]["body"][channel]), [
                "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9",
                "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0",
            ])
        self.assertEqual(animation["bones"]["body"]["scale"], original_static)
        for time in (0.0, 0.5, 1.0, 1.5, 2.0):
            key = BAKER._fmt_time(time)
            expected = BAKER._eval_component("math.sin(q.anim_time * 90) * 3", time)
            actual = animation["bones"]["body"]["rotation"][key][1]
            self.assertLessEqual(abs(actual - expected), 0.0001)

    def test_missing_length_is_rejected_without_partial_mutation(self):
        animation = {"bones": {"body": {"rotation": [0, "q.anim_time", 0]}}}
        before = json.dumps(animation, sort_keys=True)
        with self.assertRaises(ValueError):
            BAKER.bake_animation(animation, "missing_length")
        self.assertEqual(json.dumps(animation, sort_keys=True), before)

    def test_file_bake_is_deterministic_and_second_run_is_a_noop(self):
        source = {
            "animations": {
                "fixture": {
                    "animation_length": 1.0,
                    "bones": {"body": {"rotation": [0, "q.anim_time * 2", 0]}},
                }
            }
        }
        with tempfile.TemporaryDirectory() as directory:
            path = pathlib.Path(directory) / "fixture.animation.json"
            path.write_text(json.dumps(source, indent=2) + "\n", encoding="utf-8")
            self.assertEqual(BAKER.bake_file(path), (1, {"fixture": 1}))
            first = path.read_bytes()
            first_hash = hashlib.sha256(first).hexdigest()
            self.assertEqual(BAKER.bake_file(path), (0, {}))
            self.assertEqual(path.read_bytes(), first)
            self.assertEqual(hashlib.sha256(path.read_bytes()).hexdigest(), first_hash)


if __name__ == "__main__":
    unittest.main()
