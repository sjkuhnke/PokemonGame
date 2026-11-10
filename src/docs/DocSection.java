package docs;

/**
 * One chunk of the master workbook (Abilities, Moves, Pokemon, ...).
 *
 * A section creates its own sheet(s) on ctx.wb, registers link targets for
 * the entities it owns, adds ctx.links.link(...) wherever it mentions
 * another entity, and reports progress through ctx.progress. It must NOT
 * save the workbook; MasterDoc does that after all links are resolved.
 */
public interface DocSection {
	/** Shown on the loading screen, e.g. "Writing Pokemon sheet...". */
	String label();

	/** Relative cost, used to size this section's share of the progress bar. */
	int weight();

	void write(DocContext ctx);
}