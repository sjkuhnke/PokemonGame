package docs;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Builds the "Index" tab (first tab of the master workbook) and adds a
 * "<- Index" link to the top of every other tab.
 *
 * Runs after all sections have written, so it lists whatever tabs actually
 * exist (a section that failed simply won't appear).
 */
final class IndexSheet {

	static final String NAME = "Index";
	private static final String BACK_TEXT = "← Index";

	private IndexSheet() {}

	/** group title -> (tab name -> description) */
	private static Map<String, Map<String, String>> layout() {
		Map<String, Map<String, String>> g = new LinkedHashMap<>();

		Map<String, String> world = new LinkedHashMap<>();
		world.put("Trainer Teams", "Every trainer's team by location: moves, items, abilities, natures.");
		world.put("Wild Encounters", "Wild Pokemon by area and encounter type, with levels and chances.");
		world.put("Gift Encounters", "Gift Pokemon, their levels and notes.");
		world.put("Overworld Items", "Items lying around the world and treasure chest contents.");
		world.put("Shops", "Market and Star Shop inventories.");
		g.put("The World", world);

		Map<String, String> mons = new LinkedHashMap<>();
		mons.put("Pokemon Info", "One card per Pokemon: types, abilities, stats, evolutions, level-up moves.");
		mons.put("TM Compatibility", "Which Pokemon can learn which TMs/HMs.");
		g.put("Pokemon", mons);

		Map<String, String> moves = new LinkedHashMap<>();
		moves.put("Moves", "All moves grouped by type, with power, accuracy, PP and descriptions.");
		moves.put("Hidden Power", "IV combinations and the Hidden Power type each gives.");
		moves.put("Abilities", "Every ability and what it does.");
		g.put("Moves & Abilities", moves);

		Map<String, String> items = new LinkedHashMap<>();
		items.put("Items", "Every item with pocket, buy/sell prices and description.");
		items.put("TM Locations", "Where to find each TM/HM.");
		g.put("Items", items);

		Map<String, String> types = new LinkedHashMap<>();
		types.put("Defensive Typings", "What every type combination is weak to, resists and is immune to.");
		types.put("Offensive Typings", "What every type combination hits for 2x, 1/2x and 0x.");
		types.put("Type Chart", "Single-type attack vs. defense effectiveness grid.");
		g.put("Types", types);

		return g;
	}

	static void build(DocContext ctx, List<String> failedSections, int missingLinks, String reportFileName) {
		// Add the back-links first, so the Index itself doesn't get one
		addBackLinks(ctx);

		Sheet sheet = ctx.newSheet(NAME);
		sheet.setColumnWidth(0, 7500);
		sheet.setColumnWidth(1, 24000);

		int r = 0;

		Row title = sheet.createRow(r++);
		title.setHeightInPoints(40);
		StyleSpec titleStyle = StyleSpec.plain().bold().size(20).textColor(Color.WHITE).fill(DocStyles.HEADER_BG)
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER);
		ctx.text(title, 0, "Pokemon Xhenos Documentation", titleStyle);
		ctx.text(title, 1, "", titleStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

		Row stamp = sheet.createRow(r++);
		ctx.text(stamp, 0, "Generated " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
				StyleSpec.plain().italic().size(10).textColor(new Color(110, 110, 110)));

		r++;
		String[] help = {
			"Names that look like links (Pokemon, moves, abilities, items, types) jump to their entry.",
			"Every tab has a \"" + BACK_TEXT + "\" link in its top row that brings you back here."
		};
		for (String line : help) {
			Row row = sheet.createRow(r++);
			ctx.text(row, 0, line, StyleSpec.plain().size(11));
		}
		r++;

		List<String> listed = new ArrayList<>();
		for (Map.Entry<String, Map<String, String>> group : layout().entrySet()) {
			boolean any = false;
			for (String tab : group.getValue().keySet()) if (ctx.wb.getSheet(tab) != null) any = true;
			if (!any) continue;

			Row gr = sheet.createRow(r++);
			gr.setHeightInPoints(20);
			StyleSpec groupStyle = DocStyles.header(12, HorizontalAlignment.LEFT);
			ctx.text(gr, 0, group.getKey(), groupStyle);
			ctx.text(gr, 1, "", groupStyle);

			for (Map.Entry<String, String> tab : group.getValue().entrySet()) {
				if (ctx.wb.getSheet(tab.getKey()) == null) continue;
				r = writeTabRow(ctx, sheet, r, tab.getKey(), tab.getValue());
				listed.add(tab.getKey());
			}
			r++;
		}

		// anything a future section adds that the layout above doesn't know about
		boolean otherHeader = false;
		for (int i = 0; i < ctx.wb.getNumberOfSheets(); i++) {
			String name = ctx.wb.getSheetName(i);
			if (name.equals(NAME) || listed.contains(name)) continue;
			if (!otherHeader) {
				Row gr = sheet.createRow(r++);
				StyleSpec groupStyle = DocStyles.header(12, HorizontalAlignment.LEFT);
				ctx.text(gr, 0, "Other", groupStyle);
				ctx.text(gr, 1, "", groupStyle);
				otherHeader = true;
			}
			r = writeTabRow(ctx, sheet, r, name, "");
		}

		StyleSpec warn = StyleSpec.plain().bold().size(11).textColor(new Color(170, 40, 40));
		if (!failedSections.isEmpty()) {
			r++;
			Row row = sheet.createRow(r++);
			ctx.text(row, 0, "Some sections failed and are missing: " + String.join(", ", failedSections), warn);
		}
		if (missingLinks > 0) {
			Row row = sheet.createRow(r++);
			ctx.text(row, 0, missingLinks + " link(s) pointed at something not in the docs; see " + reportFileName, warn);
		}

		// Index goes first and is the tab Excel opens on
		ctx.wb.setSheetOrder(NAME, 0);
		ctx.wb.setActiveSheet(0);
		for (int i = 0; i < ctx.wb.getNumberOfSheets(); i++) {
			ctx.wb.getSheetAt(i).setSelected(i == 0); // avoid several tabs opening "grouped"
		}
	}

	private static int writeTabRow(DocContext ctx, Sheet sheet, int r, String tab, String description) {
		Row row = sheet.createRow(r);
		Cell link = ctx.text(row, 0, tab, DocStyles.text(true, 11, HorizontalAlignment.LEFT, VerticalAlignment.TOP));
		ctx.links.linkToSheet(link, tab, 0, 0);
		ctx.text(row, 1, description, DocStyles.wrapped(11));
		DocUtils.setWrappedRowHeight(row, description, 90, 15f);
		return r + 1;
	}

	/** Puts a "<- Index" link in the first free cell of each tab's top row. */
	private static void addBackLinks(DocContext ctx) {
		StyleSpec style = StyleSpec.plain().bold().size(10).valign(VerticalAlignment.CENTER);
		for (int i = 0; i < ctx.wb.getNumberOfSheets(); i++) {
			Sheet sheet = ctx.wb.getSheetAt(i);
			if (NAME.equals(sheet.getSheetName())) continue;

			Row row = sheet.getRow(0);
			if (row == null) row = sheet.createRow(0);

			int col = Math.max(row.getLastCellNum(), 0);
			// skip past any merged block that covers the candidate cell
			boolean moved = true;
			while (moved) {
				moved = false;
				for (CellRangeAddress m : sheet.getMergedRegions()) {
					if (m.isInRange(0, col)) {
						col = m.getLastColumn() + 1;
						moved = true;
					}
				}
			}

			Cell cell = ctx.text(row, col, BACK_TEXT, style);
			ctx.links.linkToSheet(cell, NAME, 0, 0);
		}
	}
}