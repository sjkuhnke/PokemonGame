# Phase 7 change list (EXTREME lead selection, spec §7.15)

Status: implemented, **not compiled against your project**. Scope kept to Phase 7: no tuning, caching or Tier 0/1 paths (Phase 8).

## What this phase does

At battle start, on EXTREME only, the AI picks its lead by playing a one-shot simultaneous game against the player's
**team** (never the player's lead choice). Rows are the AI's selectable mons, columns the player's. Each cell is the value
of the two leads after they enter on a clean field with the **real** HP/status of both teams. The top 3x3 of the plausible
region is replaced by the game value of a lite one-turn matrix. The matrix is solved, shaped and sampled, so the lead is
a mixed strategy the player cannot reliably counter-pick, and a strictly dominant lead is picked almost always.

## Decisions taken (yours, revisit in Phase 8)

| # | Decision | Why / what to revisit |
|---|---|---|
| D1 | **Baseline term dropped from the matrix.** `L[i][j] = eval(entry)`, not `eval(entry) - eval(neutralBaseline)`. The baseline survives only as `LeadSelection.entryDelta` (T25 and debugging). | A per-cell baseline would cancel the raw matchup the matrix exists to measure. |
| D2 | **Scale shift.** Refined cells are one-turn game values, static cells are entry evals. Unrefined cells get `+ mean(refined - static)` over the refined block. | Cheap heuristic. Phase 8: check it does not distort the equilibrium at the block edge (compare against refining everything on small teams). |
| D3 | **`evaluateSwitchInScore` left in `Pokemon`.** It now has no production caller; Phase 0-6 tests still use it. | Delete in Phase 8 together with those tests. |
| D4 | **Nuzlocke / level cap ignored.** `isSelectableLead` = non-null, not an Egg, not fainted. The old UI handler ignored the cap too. | Revisit if over-cap mons should not be columns. |
| D5 | **Eject Pack on entry:** if an entry effect leaves `Status.SWITCHING` on a lead, `simulateEntry` clears it and does not simulate the swap. | Rare; flagged in code. |
| D6 | **Cost.** About 36 entry sims plus up to 9 lite matrices. `cfg.leadRefineKAi / leadRefineKPlayer` (3 / 3) are the knobs; the plan logs its time. Not optimized. | Phase 8: profile against the 500 ms budget (§7.15). |
| D7 | **No per-trainer `fixedLead` data.** The field exists and is honored, but nothing sets it. | Slot + 1, 0 = none, so an old save that deserializes the field as 0 means "no fixed lead". |

## Revision 1 (after the first live run on the Elite Four / Champion)

Live output showed the Champion's static matrix was flat per row (Alakazam -37.8 in five of six columns, Superchargo -42.7) and
the equilibrium was a pure 100% Icy Serpent. A per-cell printout showed the cause: **live** matchup varied widely (Alakazam
-25.4 .. 90.0, with its omniboost applied) while the **capped** matchup was one number (-33.0). Four changes:

1. **Static pass uses the uncapped eval** (`Evaluator.eval(s, style, w.ai, w.player)`, no `MonWeights` context). The escape cap in
   `matchupTerm` bounds a winning duel by the foe's worst full-HP / zero-stage bench answer, which at lead time erased entry
   effects and made each row independent of the player's lead. The one-turn refinement already models the foe's escape.
2. **Logit softening of the lead distribution** (`LeadSelection.softLeads`, `AIConfig.leadTemperature = 10`): `x_i ~ exp(T * (u_i - max u) / range)`
   where `u = L * y_eq`. A strictly dominant lead still gets ~99%; a thin saddle (6 points over the runner-up) no longer gets 100%.
3. **Timing split and a cheaper default**: `Plan.staticMs` / `refineMs` in the debug header; default refinement 2x2 (was 3x3); the lite
   config keeps one switch answer and one player switch column. The 6 s was ~0.5 s static + ~5.4 s refinement (9 matrices).
   Still above the 500 ms budget: that is Phase 8 caching, not tuning. `leadRefineKAi = 0` gives the static-only plan (~0.5 s).
4. **T25 fixture** made non-saturating (weak Tackle-only mons; asserts on `Evaluator.activeMatchup` as well as the static value). The first
   failure was the fixture: Earthquake-vs-AI duels were already decided, so `matchupScore` sat at +-90 and no entry effect could move it.
   Added a `softening` test.

New decisions (revisit in Phase 8):

| # | Decision | Why / what to revisit |
|---|---|---|
| D8 | **Logit on `L * y` replaces sampling the raw equilibrium row.** For a *mixed* equilibrium every support row has the same payoff against `y`, so they get equal weight (the equilibrium proportions are not preserved). | Each is equally good against `y`; only an off-equilibrium player could tell. If you want equilibrium proportions kept, blend `x = a*x_eq + (1-a)*softmax`. |
| D9 | **Static cells are now uncapped, refined cells are still capped** (they come from `MatrixBuilder`, which evals with `MonWeights`). D2's shift corrects the mean gap, not a per-cell difference, so winning-duel cells outside the refined block can look better than refined ones. | The refined block is chosen from the static matrix, so an over-credited row gets refined and corrected. Check with a 6v6 dump; if it still bites, evaluate the refinement uncapped too. |
| D10 | **Saturation.** `matchupScore` clamps at +-90, so several cells tie there (Alakazam vs Hast and Swalot-X). | Eval-term tuning, Phase 8. |
| D11 | **Multi-turn value is not seen** (Necrozma's Stealth Rock is only worth `hazardPain` x placeholder weight after one turn). | Eval weights, Phase 8; not a lead-logic bug. |

## Method-level change list

### New: `LeadSelection.java` (package `pokemon`)
`plan` (pure, Rng-free, one SimContext scope), `choose` (plan + the single `Shaper.sample`), `startState`, `selectable`,
`solveLeads` / `softLeads` (solve, logit-soften, shape on a bare matrix, used by T24/T33/softening), `gameValue` (midpoint of the two certified bounds, since
`Solver.Result` carries no value), `entryDelta`, plus private `refinedValue`, `evalPinned`, `topRows`, `topCols`, `log`.

### New: `Phase7Tests.java` (package `test`): T23, T24, T25, T32, T33, EXTREME half of T27, wrapper and purity.

### `AIConfig.java`
Add fields (after `useFullSim`):
```java
	/** Phase 7 (§7.15, §8.1): EXTREME only. The AI picks its own lead from the player's team before turn 1. */
	public boolean selectLead = false;
	/** Phase 7: how many top rows / columns of the static lead matrix get a one-turn game value. 2x2 after the first live run (3x3 took ~6 s at 6v6). Placeholder; Phase 8. */
	public int leadRefineKAi = 2;
	public int leadRefineKPlayer = 2;
	/** Phase 7 (rev 1): sharpness of the logit that turns the lead equilibrium into a sampling distribution (see LeadSelection.softLeads). 10 gives a strictly dominant lead ~99%. Placeholder; Phase 8. */
	public double leadTemperature = 10.0;

	/** Phase 7: the cheaper config the lead refinement builds each one-turn matrix with (no sacking, no history, small caps). */
	public AIConfig leadLite() {
		AIConfig c = new AIConfig();
		c.branchBudget = 2;
		c.useHistoryModel = false;
		c.enableSacking = false;
		c.allowVoluntarySwitch = allowVoluntarySwitch;
		c.deadTurnForcesSwitch = deadTurnForcesSwitch;
		c.maxAISwitchRows = Math.min(maxAISwitchRows, 3); // rev 1: one switch-in answer (was 2) ...
		c.maxPlayerSwitchCols = 1; // ... and one player switch column (was 2): fewer cells per refined matrix
		c.sackRatio = sackRatio;
		c.sackMinGain = sackMinGain;
		c.alpha = alpha;
		c.temperatureExploit = temperatureExploit;
		c.minProb = minProb;
		c.epsilon = 0;
		c.style = style;
		c.useFullSim = useFullSim;
		return c;
	}
```
Replace `forDifficulty`:
```java
	public static AIConfig forDifficulty(int difficulty) {
		AIConfig c = difficulty == Player.NORMAL ? normal() : hard();
		c.selectLead = difficulty == Player.EXTREME; // the second and last difficulty gate (§1.1, §8.1)
		return c;
	}
```
Update its javadoc: EXTREME now differs from HARD only in `selectLead`.

### `BattleSimulator.java`: add after `simulateTurn`
```java
	/**
	 * Phase 7 (§7.15): the start-of-turn-1 state for one lead pair. Forks {@code start}, makes the two slots current and
	 * mirrors BattleUI.setStartingTasks: Neutralizing Gas first, then swapIn faster-first with hazards=true on a field with
	 * none. NO end of turn: the real battle runs none before turn 1 either, so the first endOfTurn happens inside the
	 * lead refinement's own simulateTurn. {@code applyEntryEffects=false} returns the same pair with nothing applied
	 * (the baseline T25 compares against). Does not touch {@code start}.
	 */
	public static SimState simulateEntry(SimState start, int aiSlot, int playerSlot, boolean applyEntryEffects) {
		Field prevField = Pokemon.field;
		try (SimContext.Scope sc = SimContext.enter(SimPolicy.DEFAULT)) {
			SimState s = start.fork(java.util.Collections.singleton(aiSlot), java.util.Collections.singleton(playerSlot));
			s.ai.shell.setCurrent(s.ai.shell.team[aiSlot]);
			s.player.shell.setCurrent(s.player.shell.team[playerSlot]);
			s.turn = 0;
			if (!applyEntryEffects) return s;

			Pokemon.field = s.field;
			Pokemon ai = s.ai.active(), pl = s.player.active();
			if (pl.getAbility(s.field) == Ability.NEUTRALIZING_GAS || ai.getAbility(s.field) == Ability.NEUTRALIZING_GAS) {
				s.field.setEffect(s.field.new FieldEffect(Field.Effect.NEUTRALIZING_GAS), false);
			}
			Pokemon faster = pl.getFaster(ai, 0, 0, s.field); // the player is "user" in the real order
			Pokemon slower = faster == pl ? ai : pl;
			faster.swapIn(slower, true, s.field);
			slower.swapIn(faster, true, s.field);
			// Eject Pack-style entry switches are not simulated (flagged, PHASE7_CHANGES.md D5).
			if (ai.hasStatus(Status.SWITCHING)) ai.removeStatus(Status.SWITCHING);
			if (pl.hasStatus(Status.SWITCHING)) pl.removeStatus(Status.SWITCHING);
			return s;
		} finally {
			Pokemon.field = prevField;
		}
	}
```

### `Trainer.java`
Delete `pickLead`'s old body, `predictPlayerLeads`, `weightedRandomSelection` (every `System.out` goes with them; `HashMap` / `Map` imports may become unused, harmless). Add:
```java
	/** Phase 7 (§7.15): 1-based team slot this trainer must lead with; 0 = none. Default 0 so an old save deserializes to "none". Nothing sets it yet. */
	public int fixedLead;

	/** Phase 7: THE predicate for "may lead". Used by the preview UI's select handler and by chooseLead's candidate sets (T32). */
	public static boolean isSelectableLead(Pokemon p) {
		return p != null && !(p instanceof Egg) && !p.isFainted();
	}

	/** Existing call site stays unchanged. foe is used ONLY to reach foe.trainer and its difficulty, never as "the player's lead". */
	public Pokemon pickLead(Pokemon foe) {
		if (!foe.playerOwned()) return current; // AI-vs-AI sim battles keep their fixed leads
		AIConfig cfg = AIConfig.forDifficulty(((Player) foe.trainer).difficulty);
		if (!cfg.selectLead) return current; // non-EXTREME: team order, unchanged
		if (fixedLead > 0 && fixedLead <= team.length && isSelectableLead(team[fixedLead - 1])) {
			current = team[fixedLead - 1];
			return current;
		}
		return chooseLead(foe.trainer, cfg);
	}

	/** §7.15: takes the player's TEAM only. Sets {@code current} to the chosen mon (the existing behavior) and returns it. */
	public Pokemon chooseLead(Trainer playerTrainer, AIConfig cfg) {
		Pokemon chosen = LeadSelection.choose(this, playerTrainer, cfg);
		if (chosen != null) current = chosen;
		if (AIV2.AI_DEBUG) Print.debug("[Lead] " + current + " chosen as lead\n");
		return current;
	}
```

### `BattleUI.java`: select handler (inside `drawUserTeamPreview`, the `wPressed` block)
```java
-				if (!pokemon.isFainted()) {
+				if (Trainer.isSelectableLead(pokemon)) {
```
Behavior is identical today: `Egg.isFainted()` returns true, so the old check already blocked Eggs. The point is one shared predicate.

### `UI.java`: no change. `startBattle()` already calls `pickLead` before `CHOOSE_LEAD_STATE`.

### `Phase0-6Tests.java` (Phase6Tests.t27): cosmetic. The message `"forDifficulty(EXTREME) is hard() until Phase 7"` still passes (EXTREME keeps `allowVoluntarySwitch`); reword it to "forDifficulty(EXTREME) keeps HARD's voluntary-switch gate".

## Findings from reading the code
- **Timing is already right.** `UI.startBattle` runs `pickLead` before `BattleUI` enters `CHOOSE_LEAD_STATE`, so the AI's lead is fixed before the player's selection is committed.
- **Nothing else assumes `team[0]` is the AI's lead.** The lead is `foe`, i.e. `current`. The one `team[0]` use is `Item.useCalc(user, null, foe.trainer.team[0], true)` in the preview; it does not reveal the lead, so it stays.
- **Turn-0 end of turn (your reminder).** The real start does `new Field()`, the Neutralizing Gas pre-step, then `swapIn` faster-first, with **no** `endOfTurn`. `simulateEntry` does exactly that. The first `endOfTurn` happens at the end of turn 1 inside the refinement's `simulateTurn`, as in the real game. Known limit: the **static** pass cannot see turn-1 residuals (weather chip, poison); only refined cells do.
- `swapIn` already calls `checkOmniBoost`, and `simulateEntry` runs on shells (`cloned`), so omniboost applies per pair and `boosts[2]` is never set for real.
- `Egg.isFainted()` always returns true, so `!isFainted()` alone already excluded Eggs; the explicit `instanceof Egg` is for clarity.
- `Solver.Result` has no value field; `gameValue` derives one (see above).

## Tests (`Phase7Tests.runAll()`)
T32 candidate sets (incl. null, fainted, Egg) / T23 no-peek (reflection on the signature, 25 seeds, different player `current`, rerun determinism) / T24 mixed vs dominant vs rock-paper-scissors on the production solve+shape / T25 Intimidate and Drizzle entry effects vs the no-entry baseline (live matchup and static value, control lead exactly 0) / softening test / T33 damaged + paralyzed player team changes the matrix, all-negative matrix not uniform / EXTREME half of T27 / non-player foe keeps `current`, `plan()` leaves real state untouched (fingerprint). `Phase7Tests.timing(n)` prints average plan time.
Re-run unchanged and expected green: Phase3 to Phase6 suites.

## Manual verification (live game, game thread)
1. `Phase7Tests.runAll();` then the earlier suites.
2. Set difficulty EXTREME, start any trainer battle with `AIV2.AI_DEBUG` on. The console prints the lead matrix (`*` = refined cell) and `[Lead] X chosen as lead`. The preview then shows the AI's team; the lead is already fixed.
3. Fight the same trainer 10 times with the same team: the lead should vary when the matrix is mixed and stay put when one lead dominates.
4. Arrive with a heavily damaged or statused team and compare matrices (T33 live).
5. Engine-level T24: build an AI team where one lead (say an Intimidate or Fake Out user) beats five of your six mons and one of yours counters it; the dump should show it well under 80%.
6. HARD and NORMAL: lead is team order, no matrix printed (non-EXTREME unchanged).
7. `Phase7Tests.timing(20)` and read the `time=` field of the matrix dump on a 6v6; compare with the 500 ms budget.

## Assumptions to confirm
1. `Player.difficulty` is the int `forDifficulty` takes (confirmed by you).
2. `Move.TACKLE`, `Move.EARTHQUAKE`, `Ability.INTIMIDATE`, `Ability.DRIZZLE`, `Status.PARALYZED`, `Pokemon.setTrainer(Trainer)` exist as named; `Egg(int)` works in the test context.
3. `Field.setEffect(FieldEffect, boolean)` and `new Field()` give a clean field with turns 0.
4. `new Trainer(true, name)` plus `initFieldEffectList()` is a valid fixture for a team with null slots (it is how the player's team is shaped).
5. In a real battle the AI trainer's team is already at its battle-start HP/status when `pickLead` runs.
6. `Pokemon.clone()` on an `Egg` returns something `simShell` can carry (Eggs are only columns when not selectable, so they are cloned but never entered).

## Not done / for Phase 8
Tune `leadRefineK*`, the shift heuristic (D2), `minProb` for leads; profile and cache the 36 entry sims (they depend only on the pair, not on any decision); optionally use a per-player lead history as a prior (§7.15 design notes, same shrinkage as §7.11); delete `evaluateSwitchInScore` (D3); Eject Pack on entry (D5).
