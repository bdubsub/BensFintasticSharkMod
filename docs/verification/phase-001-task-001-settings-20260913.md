# Phase 001 task 001 settings evidence

Date: 2026-09-13

Status: in progress. The session settings service and command surface are implemented on the
phase 001 branch. Movement, spawn, scale, attribute, recovery and client consumers remain later
phase tasks and are not claimed by this record.

## Implemented contract

`SpeciesSettingsService` owns one immutable revisioned snapshot for all 22 current BFS species.
It exposes typed fields for independent horizontal and vertical speed, independent sprint
multipliers, natural group bounds, scale bounds, health, damage, knockback, sensing, disengage,
action timeout, memory and scan radius. Every field reports a unit, inclusive bounds and a
capability result. Session overrides are separate from the validated baseline and clear when the
server stops.

Wildcard expansion is sorted and validated for every target before publication. Accepted changes
publish one new revision. Invalid values, invalid minimum and maximum pairs, unknown targets and
stale revisions return a rejection without changing the previous snapshot. Reset removes only
the requested session fields. Baseline reload validates the complete candidate and retains the
previous snapshot when the candidate or an existing override would be invalid.

The Forge command aliases delegate to this same service:

```text
/bfs debug settings list
/bfs debug settings get <entity|*>
/bfs debug settings set <entity|*> <field> <value> [revision]
/bfs debug settings reset [entity|*] [field] [revision]
/bfs debug settings reload
/bfs debug setspeed <entity|*> <horizontal> <vertical> [revision]
/bfs debug setsprint <entity|*> <horizontal> <vertical> [revision]
/bfs debug setspawnsize <entity|*> <minimum> <maximum> [revision]
/bfs debug setscale <entity|*> <minimum> <maximum> [revision]
/bfs debug sethealth <entity|*> <multiplier> [revision]
/bfs debug setdamage <entity|*> <multiplier> [revision]
/bfs debug setknockback <entity|*> <value> [revision]
/bfs debug setbehavior <entity|*> <detection> <disengage> <action_timeout> <memory_ticks> [revision]
```

The Forge baseline bridge imports existing server configuration values for group, attribute and
shark sensing fields. It does not write configuration or persist session overrides.

## Verification

`./gradlew :forge:compileJava :forge:compileTestJava --no-daemon --console=plain` passed.

`./gradlew :forge:test --tests tfar.bensfintasticsharks.entity.SpeciesSettingsServiceTest
--no-daemon --console=plain` passed. The test covers the 22 entry inventory, typed field
coverage, wildcard publication, atomic invalid and stale rejection, pair bounds, selective reset
and invalid baseline reload retention.

The complete phase evidence ladder is still open. No movement consumer, GameTest dispatcher
fixture, standalone server capture, client gate, phase pull request, merge, artifact checksum or
phase tag is claimed here.
