# Phase 0 - edit list

Apply from your source root (the folder that contains `pokemon/` and `util/`):

    patch -p1 --binary < phase0.patch        # or: git apply --ignore-whitespace phase0.patch

The patch was generated against the exact `Pokemon.java`, `Trainer.java`, `Print.java` you uploaded and was
re-applied to fresh copies to confirm it reproduces the patched files byte-for-byte (CRLF preserved).
Then add the new files (paths below are relative to the same root).

## New files

| File | Purpose |
|---|---|
| `util/Rng.java` | Injectable random source (`setSeed`, `next`, `chance`, `nextInt`, `nextBoolean`, `asRandom`, `randomize`) |
| `pokemon/TrainerAI.java` | `decide(self, foe, first, difficulty)` interface, `forPokemon`, `Config.defaultAI` |
| `pokemon/LegacyAI.java` | Forwards to `Pokemon.legacyBestMove` |
| `pokemon/SelfPlay.java` | Runner: `SelfPlay.run(Config)`, `playBattle(...)`, team sources |
| `pokemon/SelfPlayReport.java` | Win rate, Wilson CI, turns, think time, crashes, determinism |
| `pokemon/Phase0Tests.java` | `runAll()` (T15 + B9/B11/B16) and `damageParity(...)` (T1 skeleton) |
| `util/RngTest.java`, `pokemon/SelfPlayReportTest.java` | Standalone, no game needed (optional) |

## Changes to existing methods

### Pokemon.java
| Where | Change | Ties to |
|---|---|---|
| imports | `import util.Rng;` | 7.1 |
| statics (after `createTask`) | add `SHED_SKIN_CHANCE = 0.5` | B3 |
| `bestMove2` | body replaced by `TrainerAI.forPokemon(this).decide(...)` | Phase 0 |
| new `legacyBestMove` | the old `bestMove2` body, untouched, package-private | Phase 0 |
| `simulateBattle` | unchanged (dispatches through `bestMove2`, default Legacy) | - |
| `endOfTurn` (Shed Skin) | `if (Rng.chance(SHED_SKIN_CHANCE))` | B3 |
| `move()` (Magic Reflect) | `&&` chain -> `\|\|` chain | B4 |
| `calcWithTypes` (Merciless) | add `PARALYZED` (matches `move()`) | B6 |
| `move()` (Light immunity) | add `NEUROFORCE` next to `EVENT_HORIZON` (calcWithTypes and Trainer already had it) | B7 (direction reversed per author) |
| `simulateSwitchIn`, `analyzeMoveEffect` (head and `finally`) | save `createTask`, restore it | B9 |
| `move()`, the PP-consumption method, `faint`, `damage`, the Future Sight resolver (11 sites) | `!this.cloned` (or `!foe.cloned`) on `recordTurn`, `recordDamageDealt` x2, `recordPPUse`, `recordKill`/`recordDeath`, `recordDamageTaken`, and `field.misses/crits/superEffective` | B11 |
| `endOfTurn` (Leech Seed) | Big Root scales the heal (`healAmt`), not `hp` after the fact | B12 |
| `secondaryEffect` x4 (Fire/Ice/Thunder Fang, Tri Attack) | `((int) Math.random() * 3)` -> `((int) (Rng.next() * 3))` | B16 (new) |
| whole file | `Math.random()` -> `Rng.next()` (24), `new Random()` -> `Rng.asRandom()` (29), `Collections.shuffle(x)` -> `(x, Rng.asRandom())`; seeded `new Random(seed)` in `setStaticIVs` kept | 7.1 |

### Trainer.java
| Where | Change |
|---|---|
| fields | `public transient TrainerAI ai;` |
| `swapRandom` | `Rng.asRandom()`; comment documenting the B10 shared-trainer risk (fixed in Phase 2) |
| `weightedRandomSelection` | `Rng.next()` |

### Print.java
`setDebugSuppressed(boolean)` / `isDebugSuppressed()`; `log` drops DEBUG lines when suppressed. Without this,
every AI trace line is timestamped and buffered forever during a 200-battle run.

## Deleted
Nothing.

## Behaviour changes you will notice in the real game
* Shed Skin now procs 1/2 (was always).
* Brick Break / Magic Fang / Psychic Fangs now break Magic Reflect (was: always reflected).
* Neuroforce now blocks Light moves in `move()` too (was: only in the calculator).
* Fire/Ice/Thunder Fang: 1/3 status only, 1/3 flinch only (if the user moved first), 1/3 both. Tri Attack: 1/3 each of burn, paralysis, freeze.
* Leech Seed with Big Root heals 1.3x the damage dealt.
* Everything else is identical: default engine is `LegacyAI`, unseeded `Rng` behaves like the old calls.

## Running the harness (game thread, no battle on screen)

    SelfPlay.Config c = new SelfPlay.Config();
    c.battles = 20;                          // legacy is ~1 s/decision; start small
    c.teams = SelfPlay.randomCompetitive(3); // same generator as the betting sim (default: story trainers)
    SelfPlay.run(c);                         // prints the report
    Phase0Tests.runAll();                    // T15 + B9/B11/B16
    Phase0Tests.damageParity(12, 300, 0.03); // T1 baseline: prints every mismatch

`SelfPlay.run` blocks the thread it runs on. `c.maxSeconds` stops early with a partial report.
