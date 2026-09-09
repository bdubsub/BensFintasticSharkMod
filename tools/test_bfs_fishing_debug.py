"""Fishing capture completeness and negative controls."""

import copy
import unittest

from test_bfs_debug_analyze import record
from bfs_debug_analyze import validate


def capture():
    return [
        record("header", 1),
        record("fishing", 2, attemptId="hook", stage="delivery", angler="player_test",
               outcome="committed", dropListTruncated=False, rodEnchantmentsTruncated=False,
               replaceVanillaMobs=True, fishEntities=True, cancelledBefore=False, cancelledAfter=True,
               deliveryCount=1, selectedCount=1, insertionAccepted=True, fishStatisticDelta=1,
               xpAccepted=3, xpRequested=3, deliveryKind="live",
               selectedSpecies="bensfintasticsharks:atlantic_cod",
               deliveryType="bensfintasticsharks:atlantic_cod",
               selectedItem="bensfintasticsharks:raw_atlantic_cod",
               advancementsBefore={"oh_my_cod": False, "why_arent_you_red": False},
               advancementsAfter={"oh_my_cod": True, "why_arent_you_red": False},
               reelImpulse={"x": 0.1, "y": 0.03, "z": 0.2}),
        record("fishing", 3, attemptId="hook", stage="settled", ambiguousSettlement=False,
               hookRemoved=True, hookStillOwned=False, rodDamageBefore=0, rodDamageAfter=1, rodCountAfter=1),
        record("end", 4, incomplete=False, recordsDropped=0),
    ]


class FishingDebugTest(unittest.TestCase):
    def analyze(self, rows):
        return validate(rows, [], {"fishing": {"minimumAttempts": 1, "expectedRodDamageDelta": 1}})

    def test_complete_live_and_item_observations(self):
        rows = capture()
        self.assertEqual("complete", self.analyze(rows)["verdict"])
        rows[1].update(fishEntities=False, deliveryKind="item", deliveryType="minecraft:item")
        self.assertEqual("complete", self.analyze(rows)["verdict"])

    def test_corrupt_delivery_is_rejected(self):
        changes = [
            {"deliveryCount": 2}, {"selectedCount": 2}, {"selectedCount": True}, {"fishStatisticDelta": 0},
            {"insertionAccepted": False}, {"cancelledBefore": True}, {"xpAccepted": 0},
            {"deliveryKind": "item"}, {"deliveryType": "minecraft:salmon"},
            {"deliveryType": "minecraft:pig", "selectedSpecies": "minecraft:pig"},
            {"selectedItem": "minecraft:cod"}, {"angler": "raw_player_uuid"},
            {"reelImpulse": {"x": float("nan"), "y": 0.0, "z": 0.0}},
            {"advancementsAfter": {"oh_my_cod": True, "why_arent_you_red": True}},
            {"advancementsAfter": {"oh_my_cod": "unavailable", "why_arent_you_red": False}},
        ]
        for change in changes:
            with self.subTest(change=change):
                rows = capture()
                rows[1].update(change)
                self.assertEqual("invalid", self.analyze(rows)["verdict"])

    def test_failure_must_not_award_success(self):
        rows = capture()
        rows[1].update(outcome="insertion_rejected", deliveryCount=0, fishStatisticDelta=0,
                       xpAccepted=0, xpRequested=0, insertionAccepted=False,
                       advancementsAfter={"oh_my_cod": False, "why_arent_you_red": False})
        self.assertEqual("complete", self.analyze(rows)["verdict"])
        rows[1]["xpAccepted"] = 1
        self.assertEqual("invalid", self.analyze(rows)["verdict"])

    def test_duplicate_commits_and_missing_settlement_fail(self):
        rows = capture()
        rows.insert(2, copy.deepcopy(rows[1]))
        for sequence, row in enumerate(rows, 1):
            row["sequence"] = sequence
        self.assertEqual("invalid", self.analyze(rows)["verdict"])
        rows = capture()
        del rows[2]
        rows[-1]["sequence"] = 3
        self.assertEqual("invalid", self.analyze(rows)["verdict"])

    def test_ambiguous_or_incomplete_rod_cleanup_fails(self):
        for changes in ({"ambiguousSettlement": True}, {"hookRemoved": False},
                        {"hookStillOwned": True}, {"rodDamageAfter": 0}, {"rodDamageAfter": "unavailable"}):
            rows = capture()
            rows[2].update(changes)
            self.assertEqual("invalid", self.analyze(rows)["verdict"])

    def test_empty_capture_cannot_pass_fishing(self):
        self.assertEqual("invalid", self.analyze([record("header", 1),
                         record("end", 2, incomplete=False, recordsDropped=0)])["verdict"])


if __name__ == "__main__":
    unittest.main()
