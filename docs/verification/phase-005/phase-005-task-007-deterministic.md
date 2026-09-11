# Phase 005 task 007 deterministic and static verification

Date: September 11, 2026

P005-TASK-007 ran after the `1.0-rc.1` metadata commit `5282853` and the
completed provenance and defect audit intake. The phase worktree is
`envy/0.24-phase-005` and the protected `build.gradle` line-ending change was
not staged.

## Formatter and static analysis inventory

The Gradle task inventory was inspected before execution. The repository does
not define a formatter, Checkstyle, Spotless, PMD, Error Prone, lint, or other
static-analysis task. This is recorded as capability unavailable, not treated
as a passing formatter result. Java compilation, tests, resource processing,
archive checks, and the later package and runtime gates remain mandatory.

## Deterministic checks

The Forge unit suite passed with Java 17:

```text
./gradlew :forge:test --no-daemon --console=plain
BUILD SUCCESSFUL
45 test cases, 0 failures, 0 errors
```

The animation transformation suite also passed:

```text
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s tools -p 'test_bake_molang_animations.py' -v
Ran 5 tests in 0.002s
OK
```

The deterministic tests include fail-closed expression handling, missing
length rejection without partial mutation, stable five-point transform
samples, second-run no-op behavior, exact icon and dimension checks,
advancement graph mutations, copy and punctuation checks, species profile
constraints, movement route controls, and replacement policy controls.

## Disposition

No deterministic or static-analysis failure was found. No source repair was
required, and no formatter or lint waiver was invented. Forge data generation,
the complete GameTest harness, packaging, server, laptop, multiplayer, and
artifact gates remain ordered downstream work.

Test-created Forge logs and temporary command output were removed after their
final consumers. No test runtime, world, screenshot, or cache was left behind.

P005-TASK-007 is complete and authorizes P005-TASK-008 data generation,
idempotence, and full GameTest execution.

