package docs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import entity.PlayerCharacter;
import overworld.GamePanel;
import pokemon.Encounter;
import pokemon.Pokemon;

/**
 * "Wild Encounters" and "Gift Encounters" sheets.
 *
 * Wild rows are shaded by encounter type (standard / fishing / surfing / lava),
 * species names link to their Pokemon card, and each location banner links to
 * the same location on the Trainer Teams sheet (if it has trainers).
 * Each area is registered as an ENCOUNTER_AREA target (wild first, so a gift
 * block is only the target for areas with no wild encounters).
 */
public class EncounterSection implements DocSection {

	// Wild sheet: sprite | name (3 cols) | min lv | max lv | chance
	private static final int WILD_LAST_COL = 6;
	// Gift sheet: sprite | name (3 cols) | level | notes
	private static final int GIFT_LAST_COL = 5;

	private static final int SPRITE_COL_WIDTH = 2440; // ~67px, square with the row height below
	private static final float ROW_HEIGHT = 50f;       // points (~67px)

	private final GamePanel gp;

	public EncounterSection(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public String label() {
		return "Writing encounters...";
	}

	@Override
	public int weight() {
		return 10;
	}

	@Override
	public void write(DocContext ctx) {
		String savedMapName = PlayerCharacter.currentMapName; // PMap.getLoc mutates this
		try {
			writeWildSheet(ctx);
			writeGiftSheet(ctx);
		} finally {
			PlayerCharacter.currentMapName = savedMapName;
		}
	}

	// ----------------------------------------------------------------------
	// Styling
	// ----------------------------------------------------------------------
	/** Background for a wild encounter type: G standard, F fishing, S surf, L lava. */
	private static Color typeColor(String type) {
		switch (type) {
			case "G": return new Color(218, 238, 214); // light washed-out green
			case "F": return new Color(168, 198, 228); // deeper washed-out blue
			case "S": return new Color(212, 230, 246); // light washed-out blue
			case "L": return new Color(247, 214, 192); // light washed-out red/orange
			default:  return new Color(236, 236, 236);
		}
	}

	private static StyleSpec cell(Color bg, boolean bold, HorizontalAlignment align) {
		return StyleSpec.plain().bold(bold).size(11).fill(bg).align(align).valign(VerticalAlignment.CENTER);
	}

	private static StyleSpec labelStyle(Color bg) {
		return StyleSpec.plain().bold().size(10).fill(bg).textColor(new Color(70, 70, 70))
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER);
	}

	private void sprite(DocContext ctx, Sheet sheet, int id, int row) {
		ctx.imageFit(sheet, ImageCache.spriteKey(id, false), () -> DocUtils.getCachedSprite(id, false),
				0, row, 1, 1, 0.94, true);
	}

	/** Banner for one location. Registers the target and returns the next free row. */
	private int banner(DocContext ctx, Sheet sheet, int rowIndex, String title, String area, int lastCol,
			LinkRegistry.Kind registerAs) {
		int start = rowIndex;
		rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex, title, lastCol);
		ctx.links.register(registerAs, area, sheet, start, 0);
		Cell header = sheet.getRow(start + 1).getCell(1);
		ctx.links.linkOptional(header, LinkRegistry.Kind.TRAINER_AREA, area);
		return rowIndex;
	}

	// ----------------------------------------------------------------------
	// Wild encounters
	// ----------------------------------------------------------------------
	private void writeWildSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Wild Encounters");
		sheet.setColumnWidth(0, SPRITE_COL_WIDTH);
		for (int c = 1; c <= WILD_LAST_COL; c++) sheet.setColumnWidth(c, 2400);

		int total = Encounter.encounters.size();
		int done = 0;
		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<Encounter>> e : Encounter.encounters.entrySet()) {
			String[] parts = e.getKey().split("\\|");
			String area = parts[0];
			Color bg = typeColor(parts[1]);

			rowIndex = banner(ctx, sheet, rowIndex, area + " (" + EncounterDoc.encounterTypeName(parts[1]) + ")",
					area, WILD_LAST_COL, LinkRegistry.Kind.ENCOUNTER_AREA);

			// column labels
			Row labels = sheet.createRow(rowIndex++);
			labels.setHeightInPoints(18);
			StyleSpec ls = labelStyle(bg);
			ctx.text(labels, 0, "", ls);
			ctx.text(labels, 1, "Pokemon", ls);
			ctx.text(labels, 2, "", ls);
			ctx.text(labels, 3, "", ls);
			sheet.addMergedRegion(new CellRangeAddress(labels.getRowNum(), labels.getRowNum(), 1, 3));
			ctx.text(labels, 4, "Min Lv", ls);
			ctx.text(labels, 5, "Max Lv", ls);
			ctx.text(labels, 6, "Chance", ls);

			for (Encounter enc : e.getValue()) {
				Row row = sheet.createRow(rowIndex);
				row.setHeightInPoints(ROW_HEIGHT);

				ctx.text(row, 0, "", cell(bg, false, HorizontalAlignment.CENTER));
				sprite(ctx, sheet, enc.getId(), rowIndex);

				StyleSpec nameStyle = cell(bg, true, HorizontalAlignment.LEFT);
				Cell nameCell = ctx.text(row, 1, Pokemon.getName(enc.getId()), nameStyle);
				ctx.text(row, 2, "", nameStyle);
				ctx.text(row, 3, "", nameStyle);
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 3));
				ctx.links.link(nameCell, LinkRegistry.Kind.POKEMON, enc.getId());

				StyleSpec center = cell(bg, false, HorizontalAlignment.CENTER);
				ctx.number(row, 4, enc.getMinLevel(), center);
				ctx.number(row, 5, enc.getMaxLevel(), center);
				ctx.number(row, 6, enc.getEncounterChance(), center.format("0%"));
				rowIndex++;
			}
			rowIndex++; // spacer
			ctx.progress.update(++done, total * 2, area);
		}
	}

	// ----------------------------------------------------------------------
	// Gift encounters
	// ----------------------------------------------------------------------
	private void writeGiftSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Gift Encounters");
		sheet.setColumnWidth(0, SPRITE_COL_WIDTH);
		for (int c = 1; c <= 4; c++) sheet.setColumnWidth(c, 2400);
		sheet.setColumnWidth(5, 17000);

		Map<String, ArrayList<GiftEncounter>> giftMap = EncounterDoc.buildGiftLocationMap(gp);

		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<GiftEncounter>> e : giftMap.entrySet()) {
			rowIndex = banner(ctx, sheet, rowIndex, e.getKey(), e.getKey(), GIFT_LAST_COL,
					LinkRegistry.Kind.ENCOUNTER_AREA);

			Row labels = sheet.createRow(rowIndex++);
			labels.setHeightInPoints(18);
			StyleSpec ls = labelStyle(new Color(230, 230, 230));
			ctx.text(labels, 0, "", ls);
			ctx.text(labels, 1, "Pokemon", ls);
			ctx.text(labels, 2, "", ls);
			ctx.text(labels, 3, "", ls);
			sheet.addMergedRegion(new CellRangeAddress(labels.getRowNum(), labels.getRowNum(), 1, 3));
			ctx.text(labels, 4, "Level", ls);
			ctx.text(labels, 5, "Notes", ls);

			for (GiftEncounter g : e.getValue()) {
				rowIndex = writeGiftRows(ctx, sheet, g, rowIndex);
			}
			rowIndex++;
		}
		ctx.progress.update(1, 1, "gift encounters");
	}

	private int writeGiftRows(DocContext ctx, Sheet sheet, GiftEncounter g, int rowIndex) {
		Color bg = new Color(250, 250, 250);

		if (g.type == GiftEncounter.GiftType.UNREGISTERED_BASE) {
			// No fixed species: a random unregistered non-legendary base Pokemon (given as an egg)
			String notes = "Pool: random unregistered non-legendary base Pokemon."
					+ (g.notes != null && !g.notes.isEmpty() ? " " + g.notes : "");
			writeGiftRow(ctx, sheet, rowIndex, 0, "Random base Pokemon", -1, g.level, notes, bg);
			return rowIndex + 1;
		}

		for (int id : g.possibleIds) {
			String notes = g.notes != null ? g.notes : "";
			writeGiftRow(ctx, sheet, rowIndex, id, Pokemon.getName(id), id, g.level, notes, bg);
			rowIndex++;
		}
		return rowIndex;
	}

	/**
	 * @param spriteId  sprite to draw (0 = egg)
	 * @param linkId    species to link the name to, or -1 for no link
	 */
	private void writeGiftRow(DocContext ctx, Sheet sheet, int rowIndex, int spriteId, String name, int linkId,
			int level, String notes, Color bg) {
		Row row = sheet.createRow(rowIndex);
		row.setHeightInPoints(ROW_HEIGHT);

		ctx.text(row, 0, "", cell(bg, false, HorizontalAlignment.CENTER));
		sprite(ctx, sheet, spriteId, rowIndex);

		StyleSpec nameStyle = cell(bg, true, HorizontalAlignment.LEFT).wrap();
		Cell nameCell = ctx.text(row, 1, name, nameStyle);
		ctx.text(row, 2, "", nameStyle);
		ctx.text(row, 3, "", nameStyle);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 3));
		if (linkId >= 0) ctx.links.link(nameCell, LinkRegistry.Kind.POKEMON, linkId);

		StyleSpec center = cell(bg, false, HorizontalAlignment.CENTER);
		if (level > 0) {
			ctx.number(row, 4, level, center);
		} else {
			ctx.text(row, 4, "—", center);
		}

		ctx.text(row, 5, notes, cell(bg, false, HorizontalAlignment.LEFT).wrap());
		DocUtils.setWrappedRowHeight(row, notes, 60, 15f);
	}
}