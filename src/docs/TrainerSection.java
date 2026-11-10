package docs;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import entity.Entity;
import entity.PlayerCharacter;
import pokemon.Field;
import overworld.GamePanel;
import pokemon.Ability;
import pokemon.Item;
import pokemon.Move;
import pokemon.PType;
import pokemon.Pokemon;
import pokemon.Trainer;

/**
 * "Trainer Teams" sheet. Links: team member name -> POKEMON, held item and the
 * trainer's reward item -> ITEM, ability -> ABILITY, each move -> MOVE.
 * Uses TrainerDoc.getTrainerLocationMap (shared with the TXT writer).
 */
public class TrainerSection implements DocSection {

	private static final int LAST_COL = 8;
	private static final Field field = new Field();

	private final GamePanel gp;

	public TrainerSection(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public String label() {
		return "Writing trainers...";
	}

	@Override
	public int weight() {
		return 30;
	}

	@Override
	public void write(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Trainer Teams");

		String savedMapName = PlayerCharacter.currentMapName; // getTrainerLocationMap -> PMap.getLoc mutates it
		Map<?, ?>[] maps;
		try {
			maps = TrainerDoc.getTrainerLocationMap(gp);
		} finally {
			PlayerCharacter.currentMapName = savedMapName;
		}

		@SuppressWarnings("unchecked")
		Map<String, ArrayList<Trainer>> trainerMap = (Map<String, ArrayList<Trainer>>) maps[0];
		@SuppressWarnings("unchecked")
		Map<Trainer, Entity> trainerNPCMap = (Map<Trainer, Entity>) maps[1];

		int totalTrainers = 0;
		for (ArrayList<Trainer> l : trainerMap.values()) totalTrainers += l.size();

		int done = 0;
		int rowIndex = 1; // original layout leaves row 0 for the first banner's top edge
		for (Map.Entry<String, ArrayList<Trainer>> e : trainerMap.entrySet()) {
			// banner occupies rowIndex-1 .. rowIndex+1, like the original
			rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex - 1, e.getKey(), LAST_COL);
			for (Trainer tr : e.getValue()) {
				rowIndex = writeTeam(ctx, sheet, tr, trainerNPCMap.get(tr), rowIndex);
				ctx.progress.update(++done, totalTrainers, tr.getName());
			}
			rowIndex += 2; // blank row between locations (+1 because the next banner starts a row early)
		}
	}

	private static StyleSpec s(boolean bold, boolean italic, int size) {
		return StyleSpec.plain().bold(bold).italic(italic).size(size).textColor(Color.BLACK);
	}

	private int writeTeam(DocContext ctx, Sheet sheet, Trainer tr, Entity npc, int startRow) {
		// ---- Trainer name row ----
		Row nameRow = sheet.createRow(startRow++);
		String direction = "Facing: " + npc.direction + (npc.isSpin() ? "*" : "");
		ctx.text(nameRow, 0, tr.getName(), s(true, false, 14));
		ctx.text(nameRow, 3, String.format("X: %d, Y: %d", npc.worldX / gp.tileSize, npc.worldY / gp.tileSize),
				s(false, false, 12));
		ctx.text(nameRow, 5, direction, s(false, false, 12));

		final BufferedImage down1 = npc.down1;
		if (down1 != null) {
			ctx.image(sheet, "npc:" + System.identityHashCode(down1), () -> down1,
					7, nameRow.getRowNum(), 1, 1, 1, 1);
		} else {
			System.out.println(npc + "'s down1 is null");
		}
		sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 0, 2));
		sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 3, 4));
		sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 5, 6));

		// ---- IV / boost row ----
		if ((tr.staticEnc && !tr.catchable) || tr.boosts[0] > 0) {
			Row ivRow = sheet.createRow(startRow++);
			if (tr.staticEnc && !tr.catchable) {
				StyleSpec ivStyle = StyleSpec.plain().size(12).textColor(new Color(51, 51, 51));
				ctx.text(ivRow, 0, "IVs:", ivStyle);
				ctx.text(ivRow, 1, Arrays.toString(tr.getCurrent().ivs), ivStyle);
				sheet.addMergedRegion(new CellRangeAddress(ivRow.getRowNum(), ivRow.getRowNum(), 1, 4));
			}
			if (tr.boosts[0] > 0) {
				ctx.text(ivRow, 5, tr.getBoostString(), s(true, false, 12));
				sheet.addMergedRegion(new CellRangeAddress(ivRow.getRowNum(), ivRow.getRowNum(), 5, 13));
			}
		}

		// ---- Reward row ----
		Row rewardRow = sheet.createRow(startRow++);
		ctx.number(rewardRow, 0, tr.getMoney(), s(true, false, 12).format("$#,##0"));

		Item reward = tr.getItem();
		Cell rewardCell = rewardRow.createCell(1);
		rewardCell.setCellStyle(ctx.style(s(false, true, 12)));
		if (reward != null) {
			rewardCell.setCellValue("Gives: " + reward
					+ (reward == Item.TEMPLE_ORB ? " (x100)" : reward == Item.FABLE_CHARGE ? " (x3)" : ""));
			ctx.links.link(rewardCell, LinkRegistry.Kind.ITEM, reward);
			rewardRow.setHeight((short) 372);
			ctx.image(sheet, "item:" + reward.name(), reward::getImage, 5, rewardRow.getRowNum(), 1, 1, 1, 1);
		}
		sheet.addMergedRegion(new CellRangeAddress(rewardRow.getRowNum(), rewardRow.getRowNum(), 1, 4));

		// ---- Team block ----
		Row nameRow2 = sheet.createRow(startRow++);
		Row typeRow = sheet.createRow(startRow++);
		Row spriteRow = sheet.createRow(startRow++);
		Row spriteRow2 = sheet.createRow(startRow++); // needed for image height
		Row itemRow = sheet.createRow(startRow++);
		Row abilityRow = sheet.createRow(startRow++);
		Row natureRow = sheet.createRow(startRow++);
		Row[] moveRows = new Row[4];
		for (int i = 0; i < 4; i++) moveRows[i] = sheet.createRow(startRow++);

		int spriteColWidth = (int) (80 * 256 / 7.001);
		int itemColWidth = (int) (24 * 256 / 7.001);

		int col = 0;
		for (Pokemon p : tr.getTeam()) {
			Cell pName = ctx.text(nameRow2, col, p.name(), s(true, false, 11));
			ctx.links.link(pName, LinkRegistry.Kind.POKEMON, p.getID());

			ctx.number(nameRow2, col + 1, p.getLevel(), s(true, false, p.level >= 100 ? 9 : 11));

			// type icons
			final PType t1 = p.type1, t2 = p.type2;
			sheet.addMergedRegion(new CellRangeAddress(typeRow.getRowNum(), typeRow.getRowNum(), col, col + 1));
			typeRow.setHeight((short) 384);
			ctx.image(sheet, "typepair:" + t1.name() + "/" + (t2 == null ? "-" : t2.name()),
					() -> DocUtils.combineIcons(t1.getImage2(), t2 != null ? t2.getImage2() : null),
					col, typeRow.getRowNum(), 2, 1, 0.5, 1);

			// sprite (2x2 merged block)
			spriteRow.setHeight((short) 804);
			spriteRow2.setHeight((short) 804);
			sheet.setColumnWidth(col, spriteColWidth);
			sheet.addMergedRegion(new CellRangeAddress(spriteRow.getRowNum(), spriteRow2.getRowNum(), col, col + 1));
			final int spriteId = p.id;
			ctx.image(sheet, ImageCache.spriteKey(spriteId, false), () -> DocUtils.getCachedSprite(spriteId, false),
					col, spriteRow.getRowNum(), 2, 2, 1, 1);

			// held item
			Cell itemCell = ctx.text(itemRow, col, p.item == null ? "None" : p.item.toString(), s(true, true, 11));
			if (p.item != null) {
				ctx.links.link(itemCell, LinkRegistry.Kind.ITEM, p.item);
				final Item held = p.item;
				sheet.setColumnWidth(col + 1, itemColWidth);
				itemRow.setHeight((short) 372);
				ctx.image(sheet, "item:" + held.name(), held::getImage, col + 1, itemRow.getRowNum(), 1, 1, 1, 1);
			}

			// ability
			Cell abilityCell = ctx.text(abilityRow, col, p.ability.toString(), s(true, false, 11));
			sheet.addMergedRegion(new CellRangeAddress(abilityRow.getRowNum(), abilityRow.getRowNum(), col, col + 1));
			if (p.ability != Ability.NULL) ctx.links.link(abilityCell, LinkRegistry.Kind.ABILITY, p.ability);

			// nature
			ctx.text(natureRow, col, p.nat.toString() + " Nature", StyleSpec.plain());
			sheet.addMergedRegion(new CellRangeAddress(natureRow.getRowNum(), natureRow.getRowNum(), col, col + 1));

			// moves
			for (int i = 0; i < 4; i++) {
				writeMove(ctx, sheet, p, i, moveRows[i], col);
			}

			col += 2;
		}

		return startRow;
	}

	private void writeMove(DocContext ctx, Sheet sheet, Pokemon p, int i, Row moveRow, int col) {
		Move m = p.moveset[i] != null ? p.moveset[i].move : null;

		String moveName = (m == null) ? ""
				: (m == Move.HIDDEN_POWER || m == Move.RETURN)
						? (m == Move.HIDDEN_POWER ? "HP " + p.determineHPType() : m + " " + p.determineHPType())
						: m.toString();
		PType mtype = m != null ? m.getType(p, field) : null;
		if (m == Move.HIDDEN_POWER || m == Move.RETURN) mtype = p.determineHPType();
		if (m != null) {
			if (mtype == PType.NORMAL && m.isAttack()) {
				if (p.ability == Ability.GALVANIZE) mtype = PType.ELECTRIC;
				if (p.ability == Ability.REFRIGERATE) mtype = PType.ICE;
				if (p.ability == Ability.PIXILATE) mtype = PType.LIGHT;
			}
			if (p.ability == Ability.NORMALIZE) mtype = PType.NORMAL;
		}
		if (p.moveset[i] != null) {
			for (int pp = 0; pp < p.moveset[i].getPPUps(); pp++) moveName += "+";
		}

		Cell moveCell = moveRow.createCell(col);
		moveCell.setCellValue(moveName);
		sheet.addMergedRegion(new CellRangeAddress(moveRow.getRowNum(), moveRow.getRowNum(), col, col + 1));
		if (m != null) {
			// bold on the type color, default (black) text, as in the old doc
			moveCell.setCellStyle(ctx.style(StyleSpec.plain().bold().size(11).fill(mtype.getColor())));
			ctx.links.link(moveCell, LinkRegistry.Kind.MOVE, m);
		}
	}
}