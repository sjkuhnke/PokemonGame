package docs;

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
 * "Wild Encounters" and "Gift Encounters" sheets. Every species name links to
 * its card on the Pokemon sheet.
 */
public class EncounterSection implements DocSection {

	private static final int LAST_COL = 8; // matches the 9-wide location banner

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

	private static StyleSpec plain(boolean bold, HorizontalAlignment align) {
		return DocStyles.text(bold, 11, align, VerticalAlignment.CENTER);
	}

	private void sprite(DocContext ctx, Sheet sheet, int id, int row) {
		ctx.image(sheet, ImageCache.spriteKey(id, false), () -> DocUtils.getCachedSprite(id, false),
				0, row, 1, 1, 0.6, 0.6);
	}

	private void setupColumns(Sheet sheet) {
		sheet.setColumnWidth(0, 1400);
		for (int c = 1; c <= LAST_COL; c++) sheet.setColumnWidth(c, 2400);
	}

	// ----------------------------------------------------------------------
	private void writeWildSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Wild Encounters");
		setupColumns(sheet);

		int total = Encounter.encounters.size();
		int done = 0;
		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<Encounter>> e : Encounter.encounters.entrySet()) {
			String[] parts = e.getKey().split("\\|");
			rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex, parts[0] + " (" + EncounterDoc.encounterTypeName(parts[1]) + ")", LAST_COL);

			for (Encounter enc : e.getValue()) {
				Row row = sheet.createRow(rowIndex);
				row.setHeightInPoints(20);
				sprite(ctx, sheet, enc.getId(), rowIndex);

				Cell nameCell = ctx.text(row, 1, Pokemon.getName(enc.getId()), plain(true, HorizontalAlignment.LEFT));
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 3));
				ctx.links.link(nameCell, LinkRegistry.Kind.POKEMON, enc.getId());

				boolean sameLv = enc.getMinLevel() == enc.getMaxLevel();
				String levelStr = sameLv ? "Lv. " + enc.getMinLevel() : "Lv. " + enc.getMinLevel() + " - " + enc.getMaxLevel();
				ctx.text(row, 4, levelStr, plain(false, HorizontalAlignment.LEFT));
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 4, 5));

				ctx.number(row, 6, enc.getEncounterChance(),
						plain(false, HorizontalAlignment.LEFT).format("0%"));
				sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 6, LAST_COL));
				rowIndex++;
			}
			rowIndex++; // spacer
			ctx.progress.update(++done, total * 2, parts[0]);
		}
		// remaining half of the section's progress is the gift sheet
	}

	// ----------------------------------------------------------------------
	private void writeGiftSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Gift Encounters");
		setupColumns(sheet);

		Map<String, ArrayList<GiftEncounter>> giftMap = EncounterDoc.buildGiftLocationMap(gp);

		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<GiftEncounter>> e : giftMap.entrySet()) {
			rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex, e.getKey(), LAST_COL);
			for (GiftEncounter g : e.getValue()) {
				rowIndex = writeGiftRows(ctx, sheet, g, rowIndex);
			}
			rowIndex++;
		}
		ctx.progress.update(1, 1, "gift encounters");
	}

	private int writeGiftRows(DocContext ctx, Sheet sheet, GiftEncounter g, int rowIndex) {
		for (int id : g.possibleIds) {
			Row row = sheet.createRow(rowIndex);
			row.setHeightInPoints(20);
			sprite(ctx, sheet, id, rowIndex);

			Cell nameCell = ctx.text(row, 1, Pokemon.getName(id), plain(true, HorizontalAlignment.LEFT));
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 1, 3));
			ctx.links.link(nameCell, LinkRegistry.Kind.POKEMON, id);

			String detail = "";
			if (g.level > 0) detail += "Lv. " + g.level;
			if (g.notes != null && !g.notes.isEmpty()) detail += (detail.isEmpty() ? "" : "  |  ") + g.notes;
			ctx.text(row, 4, detail, plain(false, HorizontalAlignment.LEFT));
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 4, LAST_COL));
			rowIndex++;
		}
		return rowIndex;
	}
}