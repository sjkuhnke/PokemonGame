package pokemon;

/**
 * §6/§8. Difficulty gates and tuning knobs. Phase 2 only needs branchBudget; every other
 * field from §6's struct (allowVoluntarySwitch, temperature, alpha, ...) is added by the
 * phase that first reads it (mostly Phase 3), per §0 rule 5 (no drive-by additions).
 */
public class AIConfig {
	public int branchBudget = 4;
}