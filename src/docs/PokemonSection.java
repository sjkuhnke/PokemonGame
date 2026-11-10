package docs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import pokemon.Ability;
import pokemon.Egg;
import pokemon.Item;
import pokemon.Move;
import pokemon.Node;
import pokemon.Nursery.EggGroup;
import pokemon.PType;
import pokemon.Pokemon;
import util.Pair;

/**
 * "Pokemon Info" (one card per species) and "TM Compatibility" sheets.
 *
 * Link targets: every card's name cell is registered as POKEMON (key = the
 * Integer original id, same id the old doc used for its ID chip), so
 * Encounters/Trainers/etc. can link to it.
 * Outgoing links: type badges -> TYPE, abilities -> ABILITY, level-up moves
 * -> MOVE, TM column headers -> ITEM. The card name cell links to the
 * species' row on the TM sheet (and that row links back), as before.
 * Evolution lines are still plain text (step 5).
 */
public class PokemonSection implements DocSection {

	private static final int CARD_COLS = 8;
	private static final int SPRITE_COLS = 2;
	private static final String CARD_SHEET = "Pokemon Info";
	private static final String TM_SHEET = "TM Compatibility";

	@Override
	public String label() {
		return "Writing Pokemon...";
	}

	@Override
	public int weight() {
		return 40;
	}

	@Override
	public void write(DocContext ctx) {
		Sheet sheet = ctx.newSheet(CARD_SHEET);
		for (int c = 0; c < CARD_COLS; c++) {
			sheet.setColumnWidth(c, 3400);
		}

		int[] ids = PokemonDoc.collectIds();
		List<Pokemon> allPokemon = new ArrayList<>();
		List<Integer> allIds = new ArrayList<>();
		List<Integer> headerRows = new ArrayList<>();

		int rowIndex = 0;
		for (int n = 0; n < ids.length; n++) {
			int id = ids[n];
			Pokemon p = new Pokemon(id, 5, false, false);
			headerRows.add(rowIndex);
			// TM sheet row for this species is n + 1 (row 0 is the header)
			rowIndex = writeCard(ctx, sheet, p, id, rowIndex, n + 1);
			allPokemon.add(p);
			allIds.add(id);
			// cards are ~70% of this section's weight
			ctx.progress.update(n + 1, ids.length * 2, p.name());
		}

		writeTMCompatibilitySheet(ctx, sheet, allPokemon, allIds, headerRows);
	}

	// ----------------------------------------------------------------------
	// Style recipes (cached by ctx, so reuse is free)
	// ----------------------------------------------------------------------
	private static StyleSpec fillStyle(Color bg, boolean bold, boolean italic, int size, HorizontalAlignment align) {
		return StyleSpec.plain().bold(bold).italic(italic).size(size).fillWithContrastText(bg)
				.align(align).valign(VerticalAlignment.CENTER);
	}

	private static StyleSpec labelStyle(boolean bold, boolean italic) {
		return StyleSpec.plain().bold(bold).italic(italic).size(11).fill(new Color(240, 240, 240))
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER);
	}

	private static StyleSpec statLabelStyle() {
		return StyleSpec.plain().bold().size(9).fill(new Color(230, 230, 230)).align(HorizontalAlignment.CENTER);
	}

	private static StyleSpec statValueStyle(int v) {
		Color c = v >= 100 ? new Color(30, 120, 40) : v <= 40 ? new Color(170, 40, 40) : new Color(30, 30, 30);
		return StyleSpec.plain().bold().size(12).textColor(c).align(HorizontalAlignment.CENTER);
	}

	private static StyleSpec infoStyle() {
		return StyleSpec.plain().size(10).align(HorizontalAlignment.LEFT).valign(VerticalAlignment.CENTER);
	}

	private static StyleSpec plainCenter() {
		return StyleSpec.plain().align(HorizontalAlignment.CENTER).valign(VerticalAlignment.CENTER);
	}

	private static void merge(Sheet s, Row r, int c1, int c2) {
		s.addMergedRegion(new CellRangeAddress(r.getRowNum(), r.getRowNum(), c1, c2));
	}

	// ----------------------------------------------------------------------
	// One card; returns next free row
	// ----------------------------------------------------------------------
	private int writeCard(DocContext ctx, Sheet sheet, Pokemon p, int originalId, int startRow, int tmRow) {
		Color type1Color = p.type1.getColor();
		Color type2Color = p.type2 != null ? p.type2.getColor() : new Color(120, 120, 120);

		// ---- Header: "#001 - Name" + [ID] chip ----
		Row headerRow = sheet.createRow(startRow++);
		headerRow.setHeight((short) 758);

		String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
		Cell nameCell = ctx.text(headerRow, SPRITE_COLS, dexNo + "  -  " + p.name(),
				fillStyle(type1Color, true, false, 14, HorizontalAlignment.CENTER));
		merge(sheet, headerRow, SPRITE_COLS, 6);
		ctx.links.register(LinkRegistry.Kind.POKEMON, originalId, sheet, headerRow.getRowNum(), SPRITE_COLS);
		// card -> its TM Compatibility row
		ctx.links.linkToSheet(nameCell, TM_SHEET, tmRow, 0);

		ctx.text(headerRow, 7, Pokemon.getFormattedDexNo(originalId).replace('#', '[') + "]",
				fillStyle(type1Color.darker(), true, false, 10, HorizontalAlignment.CENTER));

		// ---- Type badges ----
		Row typeRow = sheet.createRow(startRow++);
		typeRow.setHeight((short) 758);

		Cell type1Cell = ctx.text(typeRow, SPRITE_COLS, p.type1.toString().toUpperCase(),
				fillStyle(type1Color, true, false, 11, HorizontalAlignment.CENTER));
		merge(sheet, typeRow, SPRITE_COLS, SPRITE_COLS + 2);
		ctx.links.link(type1Cell, LinkRegistry.Kind.TYPE, p.type1);

		Cell type2Cell = ctx.text(typeRow, SPRITE_COLS + 3, p.type2 != null ? p.type2.toString().toUpperCase() : "—",
				fillStyle(p.type2 != null ? type2Color : new Color(210, 210, 210), true, false, 11,
						HorizontalAlignment.CENTER));
		merge(sheet, typeRow, SPRITE_COLS + 3, 7);
		if (p.type2 != null) ctx.links.link(type2Cell, LinkRegistry.Kind.TYPE, p.type2);

		Row typeIconRow = sheet.createRow(startRow++);
		typeIconRow.setHeight((short) 384);

		final int spriteId = p.id;
		ctx.image(sheet, ImageCache.spriteKey(spriteId, false), () -> DocUtils.getCachedSprite(spriteId, false),
				0, headerRow.getRowNum(), 1, 2, 1, 1);

		final PType t1 = p.type1, t2 = p.type2;
		String typeKey = "typepair:" + t1.name() + "/" + (t2 == null ? "-" : t2.name());
		ctx.image(sheet, typeKey,
				() -> DocUtils.combineIcons(t1.getImage2(), t2 != null ? t2.getImage2() : null),
				0, typeIconRow.getRowNum(), 1, 1, 0.5, 1);

		// ---- Abilities ----
		p.setAbility(0);
		Ability ability1 = p.ability;
		p.setAbility(1);
		Ability ability2 = (p.ability == ability1) ? null : p.ability;
		p.setAbility(2);
		Ability hiddenAbility = (p.ability == Ability.NULL || p.ability == ability1) ? null : p.ability;

		Row abilityRow = sheet.createRow(startRow++);
		Cell a1 = ctx.text(abilityRow, 0, ability1.toString(), labelStyle(true, false));
		merge(sheet, abilityRow, 0, 2);
		if (ability1 != Ability.NULL) ctx.links.link(a1, LinkRegistry.Kind.ABILITY, ability1);

		Cell a2 = ctx.text(abilityRow, 3, ability2 != null ? ability2.toString() : "—", labelStyle(true, false));
		merge(sheet, abilityRow, 3, 5);
		if (ability2 != null) ctx.links.link(a2, LinkRegistry.Kind.ABILITY, ability2);

		Cell ha = ctx.text(abilityRow, 6, hiddenAbility != null ? "(" + hiddenAbility + ")" : "(—)",
				labelStyle(true, true));
		merge(sheet, abilityRow, 6, 7);
		if (hiddenAbility != null) ctx.links.link(ha, LinkRegistry.Kind.ABILITY, hiddenAbility);

		// ---- Base stats ----
		Row statLabelRow = sheet.createRow(startRow++);
		Row statValueRow = sheet.createRow(startRow++);
		for (int j = 0; j < p.baseStats.length; j++) {
			ctx.text(statLabelRow, j, Pokemon.getStatType(j, false).trim(), statLabelStyle());
			int v = p.getBaseStat(j);
			ctx.number(statValueRow, j, v, statValueStyle(v));
		}
		ctx.text(statLabelRow, p.baseStats.length, "BST", statLabelStyle());
		ctx.number(statValueRow, p.baseStats.length, p.getBST(),
				fillStyle(new Color(60, 60, 60), true, false, 11, HorizontalAlignment.CENTER));

		// ---- Weight / Catch / Egg ----
		Row infoRow = sheet.createRow(startRow++);
		infoRow.setHeightInPoints(16);
		ctx.text(infoRow, 0, "Weight: " + p.weight + " lbs", infoStyle());
		merge(sheet, infoRow, 0, 1);
		ctx.text(infoRow, 2, "Catch: " + p.catchRate, infoStyle());
		merge(sheet, infoRow, 2, 3);
		ctx.text(infoRow, 4, "Egg Cyc: " + Egg.computeEggCycles(p.getFinalEvolution()), infoStyle());
		merge(sheet, infoRow, 4, 5);

		ArrayList<EggGroup> eggGroups = Pokemon.getEggGroup(originalId);
		String eggGroupStr = eggGroups.get(0).equals(eggGroups.get(1))
				? eggGroups.get(0).toString()
				: eggGroups.get(0) + "/" + eggGroups.get(1);
		ctx.text(infoRow, 6, eggGroupStr, infoStyle());
		merge(sheet, infoRow, 6, 7);

		// ---- Evolutions (plain text for now; species links come in step 5) ----
		if (p.canEvolve()) {
			for (String evoLine : p.getEvolveString().split("\n")) {
				Row evoRow = sheet.createRow(startRow++);
				ctx.text(evoRow, 0, evoLine,
						fillStyle(new Color(225, 240, 225), false, true, 11, HorizontalAlignment.LEFT));
				merge(sheet, evoRow, 0, 7);
			}
		}

		// ---- Level-up moves grid (column-major, 4 columns of 2 cells) ----
		Row movesLabelRow = sheet.createRow(startRow++);
		ctx.text(movesLabelRow, 0, "Level-Up Moves", labelStyle(true, false));
		merge(sheet, movesLabelRow, 0, 7);

		ArrayList<Pair<String, Move>> moveEntries = new ArrayList<>();
		Node[] movebank = p.getMovebank();
		for (int j = 0; j < movebank.length; j++) {
			Node n = movebank[j];
			while (n != null) {
				moveEntries.add(new Pair<String, Move>(j == 0 ? "E" : j + "", n.data));
				n = n.next;
			}
		}

		int moveCols = 4;
		int moveRowCount = (int) Math.ceil(moveEntries.size() / (double) moveCols);
		Row[] moveRows = new Row[moveRowCount];
		for (int r = 0; r < moveRowCount; r++) {
			moveRows[r] = sheet.createRow(startRow++);
		}

		for (int idx = 0; idx < moveEntries.size(); idx++) {
			int colSlot = idx / moveRowCount;
			int rowSlot = idx % moveRowCount;
			Pair<String, Move> entry = moveEntries.get(idx);
			Move move = entry.getSecond();

			int col = colSlot * 2;
			Row moveRow = moveRows[rowSlot];
			Cell moveCell = ctx.text(moveRow, col, entry.getFirst() + " - " + move,
					fillStyle(move.mtype.getColor(), true, false, 10, HorizontalAlignment.LEFT));
			merge(sheet, moveRow, col, col + 1);
			ctx.links.link(moveCell, LinkRegistry.Kind.MOVE, move);
		}

		startRow++; // spacer
		return startRow;
	}

	// ----------------------------------------------------------------------
	// TM/HM compatibility
	// ----------------------------------------------------------------------
	private void writeTMCompatibilitySheet(DocContext ctx, Sheet cardSheet, List<Pokemon> allPokemon,
			List<Integer> allIds, List<Integer> headerRows) {
		Sheet sheet = ctx.newSheet(TM_SHEET);
		// The card name cells linked to "TM Compatibility"; make sure Excel's
		// (possibly sanitized) sheet name matches.
		if (!TM_SHEET.equals(sheet.getSheetName())) {
			System.out.println("Warning: TM sheet was renamed to " + sheet.getSheetName());
		}

		boolean[][] tms = Pokemon.getTMTable();
		List<Item> tmItems = new ArrayList<>();
		for (int tm = 93; tm <= 199; tm++) tmItems.add(Item.getItem(tm));

		sheet.setColumnWidth(0, 3400);
		sheet.setColumnWidth(1, 900);
		for (int c = 0; c < tmItems.size(); c++) {
			sheet.setColumnWidth(c + 2, 720);
		}

		Row headerRow = sheet.createRow(0);
		headerRow.setHeightInPoints(92);

		ctx.text(headerRow, 0, "Pokemon", fillStyle(new Color(60, 60, 60), true, false, 11, HorizontalAlignment.LEFT));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

		StyleSpec rotated = StyleSpec.plain().bold().size(9).textColor(Color.WHITE).fill(new Color(80, 80, 80))
				.align(HorizontalAlignment.CENTER).valign(VerticalAlignment.BOTTOM).rotate(90);
		for (int c = 0; c < tmItems.size(); c++) {
			Cell h = ctx.text(headerRow, c + 2, tmItems.get(c).toString(), rotated);
			ctx.links.link(h, LinkRegistry.Kind.ITEM, tmItems.get(c));
		}

		sheet.createFreezePane(2, 1);

		StyleSpec yes = fillStyle(new Color(190, 230, 190), true, false, 9, HorizontalAlignment.CENTER);
		for (int r = 0; r < allPokemon.size(); r++) {
			Pokemon p = allPokemon.get(r);
			int id = allIds.get(r);
			Row row = sheet.createRow(r + 1);

			Cell nameCell = ctx.text(row, 0, Pokemon.getFormattedDexNo(p.getDexNo()) + " " + p.name(), infoStyle());
			sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
			// TM row -> species card
			ctx.links.link(nameCell, LinkRegistry.Kind.POKEMON, id);

			boolean[] compat = (id - 1 >= 0 && id - 1 < tms.length) ? tms[id - 1] : null;
			for (int c = 0; c < tmItems.size(); c++) {
				boolean ok = compat != null && c < compat.length && compat[c];
				if (ok) {
					ctx.text(row, c + 2, "Y", yes);
				} else {
					row.createCell(c + 2).setCellStyle(ctx.style(plainCenter()));
				}
			}
			ctx.progress.update(allPokemon.size() + r + 1, allPokemon.size() * 2, "TM table: " + p.name());
		}
	}
}