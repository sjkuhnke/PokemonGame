package docs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import entity.PlayerCharacter;
import overworld.GamePanel;

/**
 * Builds the single master workbook (XhenosDocs.xlsx) containing every
 * document as its own tab(s), with hyperlinks between them.
 *
 * Sheet order follows the order sections are added below. Links are resolved
 * after every section has written, so section order never affects them.
 *
 * Call from a background thread (it is slow and reports progress through the
 * listener, which is meant to be LoadingScreen::setProgress).
 */
public class MasterDoc {

	public static final String FILE_NAME = "XhenosDocs.xlsx";
	public static final String LINK_REPORT_FILE = "PokemonXhenosDocs_missing_links.txt";

	// the finalize steps get a little weight so the bar doesn't sit at the end
	private static final int LINK_WEIGHT = 2;
	private static final int SAVE_WEIGHT = 4;

	private MasterDoc() {}

	/**
	 * @param startPercent / endPercent  slice of the loading bar this run owns
	 */
	public static void generate(GamePanel gp, Path dir, ProgressListener listener, int startPercent, int endPercent) {
		List<DocSection> sections = new ArrayList<>();
		sections.add(new TrainerSection(gp));
		sections.add(new PokemonSection());
		sections.add(new EncounterSection(gp));
		sections.add(new MovesSection());
		sections.add(new AbilitiesSection());
		sections.add(new ItemsSection(gp));
		sections.add(new TypingsSection());
		sections.add(new TypeChartSection());

		int totalWeight = LINK_WEIGHT + SAVE_WEIGHT;
		for (DocSection s : sections) totalWeight += s.weight();

		DocContext ctx = new DocContext(listener, startPercent, endPercent, totalWeight);

		// sections call PMap.getLoc, which writes this static; put it back afterwards
		String savedMapName = PlayerCharacter.currentMapName;
		List<String> failedSections = new ArrayList<>();
		try {
			for (DocSection s : sections) {
				ctx.progress.begin(s.label(), s.weight());
				try {
					s.write(ctx);
				} catch (Exception e) {
					// keep going: a partial workbook is more useful than none
					System.err.println("Master doc: " + s.label() + " failed");
					e.printStackTrace();
					failedSections.add(s.label());
				}
			}
		} finally {
			PlayerCharacter.currentMapName = savedMapName;
		}

		ctx.progress.begin("Linking sheets together...", LINK_WEIGHT);
		List<String> unresolved = ctx.links.resolveAll(ctx.wb);
		writeMissingLinkReport(dir, unresolved);

		// Index tab (moved to the front) + "<- Index" link on every other tab
		try {
			IndexSheet.build(ctx, failedSections, unresolved.size(), LINK_REPORT_FILE);
		} catch (Exception e) {
			System.err.println("Master doc: could not build the Index tab");
			e.printStackTrace();
		}

		ctx.progress.begin("Saving workbook...\n(This can take a little while)", SAVE_WEIGHT);
		Path outPath = dir.resolve(FILE_NAME);
		try (FileOutputStream out = new FileOutputStream(outPath.toFile())) {
			ctx.wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ctx.wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Master doc: " + ctx.styles.stats());

		String done = failedSections.isEmpty() ? "Documentation complete!"
				: "Documentation saved (" + failedSections.size() + " section(s) failed; see console)";
		ctx.progress.finish(done);
	}

	/**
	 * Links whose target was never written (e.g. a move used by a trainer but
	 * missing from the Moves sheet). Grouped by entity so it's readable; the
	 * file is only present when there is something to report.
	 */
	private static void writeMissingLinkReport(Path dir, List<String> unresolved) {
		Path report = dir.resolve(LINK_REPORT_FILE);
		try {
			if (unresolved.isEmpty()) {
				Files.deleteIfExists(report); // don't leave a stale report from an earlier run
				return;
			}
			Map<String, List<String>> byEntity = new TreeMap<>();
			for (String line : unresolved) {
				int cut = line.indexOf(" from ");
				String entity = cut < 0 ? line : line.substring(0, cut);
				String where = cut < 0 ? "" : line.substring(cut + 6);
				byEntity.computeIfAbsent(entity, k -> new ArrayList<>()).add(where);
			}
			List<String> out = new ArrayList<>();
			out.add(unresolved.size() + " link(s) had no target (" + byEntity.size() + " distinct entries):");
			out.add("");
			for (Map.Entry<String, List<String>> e : byEntity.entrySet()) {
				List<String> places = e.getValue();
				out.add(e.getKey() + "  x" + places.size() + "   first seen at " + places.get(0));
			}
			Files.write(report, out);
			System.out.println("Master doc: " + unresolved.size() + " unresolved link(s), see " + report);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}