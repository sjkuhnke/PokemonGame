# Phase 5 change list (sacking logic, shared forced-replacement chooser)

Every change is tied to spec §7.14 / §12-Phase-5 / §13 item 15, a Phase 3/4 deferral, or a decision you made this phase.
Files are in `phase5/`, CRLF like your sources. Copy them over the same-named files in `package pokemon`.

**Nothing in this phase has been compiled or run against your engine**: the authoring environment has a JDK but no game
classpath. Every file parses with `javac`, and every unresolved symbol `javac` reported is an engine class this environment
does not have (`Pokemon`, `Move`, `Field`, `Status`, ...). Members of those classes used by the new code were checked against
the uploaded sources; the enum constants I could not see (see "Guessed names") were guessed as you allowed. Expect a compile
fix or two.

## Decisions taken (yours)
1. **No `aceMultiplier`, no "ace" concept.** Every mon's value comes from evaluation (`contribution + unique + utility`);
   "ace" would only be flavor. Deleted from Phase 5's scope.
2. **Future value is a real input, not a tiebreak.** `MonWeights` (weight * hpFrac) already decides who is sackable and prices
   material; Phase 5 also feeds it into the replacement score (`ReplacementChooser`, `FUTURE_W`).
3. **T21 is written in matrix form only** until the Phase 6 player model exists (see "Known limits").
4. The min-gain guard compares a sack row with the best plain move row in each threat column (threat column = player move
   with accuracy x P(KO) >= 0.3 against the AI's active).

## New files
| File | Purpose |
|---|---|
| `ReplacementChooser` | The ONE forced-replacement function (`pickSlot(side, foe, field)`), used by `Trainer.getNext2` (real battle, every difficulty) and by the simulator. score = post-entry `matchupScore` - `FUTURE_W` * weight * hpFrac. Weights are cached by team composition |
| `SackAnalysis` | Threat columns, the pure min-gain guard (`applyMinGain`), and post-solve classification (`classify`: P(target faints), replacement plan) |
| `Phase5Tests` | 12 tests, see "Tests"; `selfPlayComparison(n)` for the manual acceptance run |

## Changed files
| File | Change | Ties to |
|---|---|---|
| `MonWeights.java` | Adds `unique` (`uniqueBonuses`: best edge, > 0, more than `GAP`=0.3 ahead of the runner-up; `UNIQUE_W`=0.75) and capped `utility` (hazard setter while `isHazardUseful`, Rapid Spin/Defog while hazards are on our side, screens, Trick Room/Tailwind, Heal Bell/Aromatherapy while a teammate is statused, Healing Wish/Lunar Dance while a teammate is hurt/statused; cap 0.6). `forSide(mine, theirs, field)` exposed for the chooser. No ace multiplier | §7.14.1 |
| `ActionGen.java` | `BenchPlan` (`answers`, `sacks`, `all()`, `sackOnly()`) via `benchPlan`; `candidateBench` now delegates. `sackCandidates` honors `cfg.enableSacking`. `genPlayerActions(root, cfg, weights)` overload (old signature delegates). The player's sack column is kept only when the AI threatens the player's active AND that mon is worth < `sackRatio` x the active. `valueOf` package-private | §7.14.2 |
| `SwitchInScorer.java` | Faint-on-entry returns finite `ENTRY_FAINT_SCORE` (-200) instead of `Integer.MIN_VALUE + 1`. New `scoreOn(side, idx, foe, field)` enters the candidate on ITS SIDE'S shell (`candidate.trainer` is not trusted in the sim: a shared bench mon still points at the shell it was forked from, i.e. older hazards) using a touched-slot-only `simShell(singleton(idx))` instead of a full clone | §7.14.3 |
| `Trainer.java` | `getNext2` is now `ReplacementChooser.pickSlot(this, other, Pokemon.field)`. Nothing else | §7.14.3 |
| `BattleSimulator.java` | `resolveReplacementsForSide` (takes the state's `Field`) and the Eject-style forced switch call `ReplacementChooser.pickSlot`. **`cheapReplacementSlot` deleted** | §7.14.3, T22 |
| `AIConfig.java` | `sackMinGain` (10, placeholder) and `enableSacking` (true) | §7.14.2, self-play A/B |
| `AIV2.java` | New `Plan`/`plan(root, cfg)` = the whole matrix pipeline incl. the guard (tests call the production code, no copy); `decide` uses it, classifies a chosen SWITCH/MOVE_THEN_SWITCH row and writes `[Sack: X for Y \| replacement plan: Z \| P(target faints) = N%]` into `p1Switch/p2Switch` (else the old `[Matrix: switch to ...]` line); `logSack` (candidates, weighted values, dropped rows, gain vs Stay; debug only). `AIV2.NO_SACK` instance for self-play | §7.14.4 |

### Deleted
`BattleSimulator.cheapReplacementSlot`; the `Integer.MIN_VALUE + 1` return; the score loop in `Trainer.getNext2`.
`Pokemon.evaluateSwitchInScore` and `pickLead`/`predictPlayerLeads` are kept (Phase 7 retires the lead uses).

## Two edits I did not ship as files (both are one-liners)
1. `Pokemon.java` ~line 592, javadoc of `evaluateSwitchInScore`: change "`Integer.MIN_VALUE + 1` means it faints on entry" to "`SwitchInScorer.ENTRY_FAINT_SCORE` means it faints on entry".
2. `Phase4Tests.switchInScorer` (two lines, they assert the old sentinel and will now FAIL):
   - `s1 != Integer.MIN_VALUE + 1`  ->  `s1 != SwitchInScorer.ENTRY_FAINT_SCORE`
   - `... == Integer.MIN_VALUE + 1` (the "1 HP into Stealth Rock" check)  ->  `... == SwitchInScorer.ENTRY_FAINT_SCORE`

## Behaviour changes worth knowing about
- **`pickLead` / `predictPlayerLeads`** average `evaluateSwitchInScore`; a fainting candidate is now -200 instead of about -2.1e9, so its average is finite. Lead choice can shift slightly. Phase 7 rewrites this.
- **Real forced replacements** now subtract a future-value term (`FUTURE_W` = 8 points per unit of weight x hpFrac). With near-equal answers the less valuable mon comes in first. Weights are computed once per faint on the real teams (fainted mon excluded).
- **Player sack column** used to be unconditional (Phase 3); it now needs an AI threat and a value gap. A Phase 3 test that expects it unconditionally would need adjusting.
- **Simulator cost per faint** rises: each replacement now scores every alive candidate with a real switch-in (2 mon clones each) instead of a type-chart lookup, and may compute `MonWeights` (cached by composition, 128 entries). Not optimized here (Phase 8).

## Tests (`Phase5Tests.runAll()`, in-game)
SKIPPED = the scenario could not be constructed from the species/moves scanned; it is not a pass.
- Pure: `uniqueBonuses`, `minGainGuard` (synthetic matrices: kept, dropped, aligned rows, no-threat, no-Stay, nothing sack-only), `replacementScorePure`.
- Engine: **T26** (mean 1.0, HP-independent, fainted = 0, answer > no-moves twin), `utilityRemover`, `scorerFinite`, **T22** (sim replacement == `Trainer.next` == chooser; plain, Stealth Rock on our side, and hurt+burned foe; 12 foes each), **T18**, **T19**, **T20**, **T21**, `sackingOff`.
- T18 also checks the label (`P(target faints) >= 0.5`, replacement plan is a different alive mon) and that NORMAL never plain-switches.

## Known limits, flagged rather than hidden
| Item | Note |
|---|---|
| **T21 is matrix form** | It asserts the sack's edge over Stay is larger against the attack column than against a switch column. The spec's probability half ("with a model that expects a switch, sack probability falls sharply") needs `predict()`; add it in Phase 6 (a comment marks the spot). `y_hat` is still the raw equilibrium, so the sack label also reflects the equilibrium, not a prediction |
| **T19 is candidate-level** | It asserts the twin is not a sack candidate and nothing is sack-only. With a 2-mon bench the twin can still be an ordinary *answer* row (top-N covers the whole bench), and whether the AI stays in then depends on speed order, so "sack probability ~ 0" is not asserted on the mixed strategy |
| **Guard only judges sack-ONLY rows** | A low-value mon that is also a top-N answer is never dropped: it is a legitimate answer row. On small benches most sack candidates are also answers, so the guard often has nothing to judge; the matrix alone then decides |
| Player sack column | One column (the lowest value), not "mon(s)" |
| `enableSacking=false` | Removes only the AI's sack candidates (and so the guard/label). The player-side sack column stays: it models the opponent, not the AI's own choice. It is a developer A/B switch, not a difficulty gate, and is true at every difficulty |
| Placeholders | `sackMinGain` 10, `FUTURE_W` 8, `UNIQUE_W` 0.75, `GAP` 0.3, `UTILITY_MAX` 0.6 and the per-term utility values are untuned; Phase 8 |
| Utility terms | Only listed moves count; "status absorber" and Perish/utility users from the spec's list are not modeled |
| Chooser weights cache | Keyed by species, all six stats, types, item, ability, moves (+PP>0), fainted flags and weather; it does not include volatile state. It is intentionally HP/status/stage independent, like the weights |
| `SwitchInScorer` foe clone | The foe is `fullClone`d per candidate (unchanged from Phase 4) |

## Guessed names (fix if the compiler complains)
`Move.EARTHQUAKE, SURF, ICE_BEAM, THUNDERBOLT, PSYCHIC, SHADOW_BALL, SLUDGE_BOMB, STONE_EDGE, CLOSE_COMBAT, REFLECT, LIGHT_SCREEN, AURORA_VEIL, TRICK_ROOM, TAILWIND, HEALING_WISH, LUNAR_DANCE, DEFOG, RAPID_SPIN` (`HEAL_BELL`/`AROMATHERAPY`/`FLAMETHROWER`/`GROWL` already appear in your code).
Fields assumed on `Pokemon`: `id`, `fainted`, `type1/type2` (enum, `.ordinal()`), `item`, `ability` (enum), `moveset[].move/.currentPP`, `getStat(0..5)`.

## Manual verification
1. Copy `phase5/*.java` over your sources, apply the two one-line edits above, compile.
2. `Phase5Tests.runAll();` then `Phase4Tests.runAll();`, `Phase3Tests.runAll();`, `Phase0Tests.runAll();`. Investigate any newly failing Phase 3 test (player sack column change, `genPlayerActions` now takes weights).
3. `AIV2.AI_DEBUG = true`; set up a real battle where the AI's active is about to be KO'd and a low-HP teammate is on the bench. Confirm the `[AIV2] sack candidates` log (values, gains), the `[Sack: ... ]` reason in the sim UI, and that the replacement that actually comes out equals the "replacement plan" (T22 in the wild).
4. Faint an AI mon in a real battle with Stealth Rock up: the log line `REPLACEMENT [mon: score]` should show finite scores; a 1-HP bench mon that dies to rocks shows about -200 minus its small future-value term and is never chosen while another mon is alive.
5. **Acceptance:** `Phase5Tests.selfPlayComparison(100);` (HARD with sacking vs HARD with `AIV2.NO_SACK`, random 6v6 teams, seats swapped). Start at 50-100 battles, check no crashes and a clean determinism check, then read engine A's win rate. If it is not above 50% by a sensible margin, first look at `sackMinGain` (raise it if sacks fire for little), then `sackRatio` (0.8) and `UNIQUE_W`; those are Phase 8 tuning knobs, not logic bugs.
6. "No sack when the active isn't threatened": with `AI_DEBUG`, decide from a position where the foe cannot threaten the active; there must be no `[Sack: ...]` line and no sack-only row (T20 covers this in matrix form).

## Not in Phase 5 (deliberately)
Player model, `predict()`, `finalStrategy()`, the T21 probability half, difficulty-tuned mistakes (Phase 6); lead selection and retiring `pickLead`/`predictPlayerLeads` (Phase 7); Tier 0/1 pruning, chooser/weights caching beyond the simple cache, tuning of every placeholder (Phase 8).
