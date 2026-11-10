package docs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import entity.Entity;
import entity.NPC_Market;
import entity.NPC_Star;
import entity.PlayerCharacter;
import object.ItemObj;
import object.TreasureChest;
import overworld.GamePanel;
import overworld.PMap;
import pokemon.Item;

/**
 * Items, TM Locations, Overworld Items and Shops sheets.
 * Items sheet rows are the ITEM link targets; item names on the other three
 * sheets (and in Pokemon/Trainer sections) link back to them.
 */
public class ItemsSection implements DocSection {

	// icon | name | pocket | buy | sell | description
	private static final int COL_ICON = 0, COL_NAME = 1, COL_POCKET = 2, COL_BUY = 3, COL_SELL = 4, COL_DESC = 5;
	private static final int ITEMS_LAST_COL = COL_DESC;
	private static final int DESC_COL_CHARS = 60;
	private static final int OW_LAST_COL = 8; // matches the 9-wide location banner

	private final GamePanel gp;

	public ItemsSection(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public String label() {
		return "Writing items...";
	}

	@Override
	public int weight() {
		return 6;
	}

	@Override
	public void write(DocContext ctx) {
		writeItemsSheet(ctx);
		writeTMLocationsSheet(ctx);
		writeOverworldAndShops(ctx);
	}

	// ----------------------------------------------------------------------
	// Styles used by several sheets
	// ----------------------------------------------------------------------
	private static StyleSpec plain(boolean bold, HorizontalAlignment align) {
		return DocStyles.text(bold, 10, align, VerticalAlignment.CENTER);
	}

	private void icon(DocContext ctx, Sheet sheet, Item item, int col, int row) {
		ctx.image(sheet, "item:" + item.name(), item::getImage, col, row, 1, 1, 0.7, 0.7);
	}

	// ----------------------------------------------------------------------
	// Sheet 1: Items
	// ----------------------------------------------------------------------
	private void writeItemsSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Items");
		sheet.setColumnWidth(COL_ICON, 1200);
		sheet.setColumnWidth(COL_NAME, 5800);
		sheet.setColumnWidth(COL_POCKET, 3000);
		sheet.setColumnWidth(COL_BUY, 1800);
		sheet.setColumnWidth(COL_SELL, 1800);
		sheet.setColumnWidth(COL_DESC, 16000);
		sheet.createFreezePane(0, 1);

		Row header = sheet.createRow(0);
		header.setHeightInPoints(20);
		String[] labels = new String[ITEMS_LAST_COL + 1];
		labels[COL_ICON] = "";
		labels[COL_NAME] = "Item";
		labels[COL_POCKET] = "Pocket";
		labels[COL_BUY] = "Buy";
		labels[COL_SELL] = "Sell";
		labels[COL_DESC] = "Description";
		StyleSpec headerStyle = DocStyles.header(11, HorizontalAlignment.CENTER);
		for (int c = 0; c <= ITEMS_LAST_COL; c++) {
			ctx.text(header, c, labels[c], headerStyle);
		}

		Item[] allItems = Item.values();
		int rowIndex = 1;
		for (Item i : allItems) {
			Row row = sheet.createRow(rowIndex);
			row.setHeightInPoints(20);

			icon(ctx, sheet, i, COL_ICON, rowIndex);

			ctx.text(row, COL_NAME, i.toString(), plain(true, HorizontalAlignment.LEFT));
			ctx.links.register(LinkRegistry.Kind.ITEM, i, sheet, rowIndex, COL_NAME);

			ctx.text(row, COL_POCKET, Item.getPocketName(i.getPocket()), plain(false, HorizontalAlignment.LEFT));

			int cost = i.getCost();
			ctx.text(row, COL_BUY, cost == 0 ? "--" : "$" + cost, plain(false, HorizontalAlignment.CENTER));
			int sell = i.getSell();
			ctx.text(row, COL_SELL, sell == 0 ? "--" : "$" + sell, plain(false, HorizontalAlignment.CENTER));

			String desc = i.getDesc();
			ctx.text(row, COL_DESC, desc, DocStyles.wrapped(10));
			DocUtils.setWrappedRowHeight(row, desc, DESC_COL_CHARS, 13f);

			rowIndex++;
			ctx.progress.update(rowIndex - 1, allItems.length, i.toString());
		}
	}

	// ----------------------------------------------------------------------
	// Sheet 2: TM Locations (plain text, from the bundled resource)
	// ----------------------------------------------------------------------
	private void writeTMLocationsSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("TM Locations");
		sheet.setColumnWidth(0, 6500);
		sheet.setColumnWidth(1, 18000);
		sheet.createFreezePane(0, 1);

		Row header = sheet.createRow(0);
		header.setHeightInPoints(20);
		StyleSpec headerStyle = DocStyles.header(11, HorizontalAlignment.CENTER);
		ctx.text(header, 0, "TM / HM", headerStyle);
		ctx.text(header, 1, "Location", headerStyle);

		int rowIndex = 1;
		try (Scanner scanner = new Scanner(ItemsSection.class.getResourceAsStream("/info/tm_locations.txt"))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.isBlank()) continue;

				String tmName;
				String location;
				int colonIdx = line.indexOf(':');
				if (colonIdx >= 0) {
					tmName = line.substring(0, colonIdx).trim();
					location = line.substring(colonIdx + 1).trim();
				} else {
					tmName = line.trim();
					location = "";
				}

				Row row = sheet.createRow(rowIndex);
				ctx.text(row, 0, tmName, plain(true, HorizontalAlignment.LEFT));
				ctx.text(row, 1, location, DocStyles.wrapped(10));
				DocUtils.setWrappedRowHeight(row, location, 75, 13f);
				rowIndex++;
			}
		} catch (Exception ex) {
			Row row = sheet.createRow(rowIndex);
			row.createCell(0).setCellValue("Could not load TM Locations file.");
			ex.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------
	// Sheets 3 + 4: Overworld Items and Shops.
	// Both need the "fresh save" object layout and both call PMap.getLoc,
	// which mutates PlayerCharacter.currentMapName, so the shared state is
	// saved up front and restored in finally.
	// ----------------------------------------------------------------------
	private void writeOverworldAndShops(DocContext ctx) {
		boolean[][] savedCollected = gp.player.p.itemsCollected;
		String savedMapName = PlayerCharacter.currentMapName;
		try {
			gp.player.p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
			gp.aSetter.setObject();

			writeOverworldItemsSheet(ctx);
			writeShopsSheet(ctx);
		} finally {
			gp.player.p.itemsCollected = savedCollected;
			gp.aSetter.setObject();
			PlayerCharacter.currentMapName = savedMapName;
		}
	}

	private void writeOverworldItemsSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Overworld Items");
		sheet.setColumnWidth(0, 1200);
		for (int c = 1; c <= OW_LAST_COL; c++) {
			sheet.setColumnWidth(c, 2600);
		}

		ItemObj[][] items = gp.obj;
		Map<String, ArrayList<ItemObj>> itemsMap = new LinkedHashMap<>();
		for (int loc = 0; loc < items.length; loc++) {
			for (int col = 0; col < items[loc].length; col++) {
				ItemObj e = items[loc][col];
				if (e == null) continue;
				PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
				itemsMap.computeIfAbsent(PlayerCharacter.currentMapName, k -> new ArrayList<>()).add(e);
			}
		}

		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<ItemObj>> e : itemsMap.entrySet()) {
			rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex, e.getKey(), OW_LAST_COL);
			for (ItemObj obj : e.getValue()) {
				rowIndex = writeOverworldItemRow(ctx, sheet, obj, rowIndex);
			}
			rowIndex++; // blank row between locations
		}
	}

	private int writeOverworldItemRow(DocContext ctx, Sheet sheet, ItemObj obj, int rowIndex) {
		int x = obj.worldX / gp.tileSize;
		int y = obj.worldY / gp.tileSize;
		boolean chest = obj instanceof TreasureChest;

		Row row = sheet.createRow(rowIndex++);
		row.setHeightInPoints(20);

		Item item = chest ? null : obj.item;
		if (item != null) {
			icon(ctx, sheet, item, 0, row.getRowNum());
		}

		Cell nameCell = ctx.text(row, 1, chest ? "Treasure Chest" : item.toString(), plain(true, HorizontalAlignment.LEFT));
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 5));
		if (item != null) ctx.links.link(nameCell, LinkRegistry.Kind.ITEM, item);

		ctx.text(row, 6, String.format("(%d, %d)", x, y), plain(false, HorizontalAlignment.LEFT));
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, OW_LAST_COL));

		if (chest) {
			for (Item contained : ((TreasureChest) obj).inventory) {
				Row chestRow = sheet.createRow(rowIndex++);
				chestRow.setHeightInPoints(18);

				icon(ctx, sheet, contained, 1, chestRow.getRowNum());

				Cell c = ctx.text(chestRow, 2, contained.toString(), plain(false, HorizontalAlignment.LEFT));
				sheet.addMergedRegion(new CellRangeAddress(chestRow.getRowNum(), chestRow.getRowNum(), 2, OW_LAST_COL));
				ctx.links.link(c, LinkRegistry.Kind.ITEM, contained);
			}
		}
		return rowIndex;
	}

	private void writeShopsSheet(DocContext ctx) {
		Sheet sheet = ctx.newSheet("Shops");
		sheet.setColumnWidth(0, 5000);  // Shop
		sheet.setColumnWidth(1, 3500);  // Coordinates
		sheet.setColumnWidth(2, 1200);  // Icon
		sheet.setColumnWidth(3, 5000);  // Item
		sheet.setColumnWidth(4, 4500);  // Cost

		Map<String, ArrayList<Entity>> shopsMap = new LinkedHashMap<>();
		for (int map = 0; map < gp.npc.length; map++) {
			for (int index = 0; index < gp.npc[map].length; index++) {
				Entity npc = gp.npc[map][index];
				if (npc == null) continue;
				if (!(npc instanceof NPC_Market) && !(npc instanceof NPC_Star)) continue;

				PMap.getLoc(map, npc.worldX / gp.tileSize, npc.worldY / gp.tileSize);
				shopsMap.computeIfAbsent(PlayerCharacter.currentMapName, k -> new ArrayList<>()).add(npc);
			}
		}

		int rowIndex = 0;
		for (Map.Entry<String, ArrayList<Entity>> entry : shopsMap.entrySet()) {
			rowIndex = DocUtils.writeLocationHeader(ctx, sheet, rowIndex, entry.getKey(), 4);

			for (Entity shop : entry.getValue()) {
				int x = shop.worldX / gp.tileSize;
				int y = shop.worldY / gp.tileSize;
				boolean starShop = shop instanceof NPC_Star;
				String shopName = starShop ? "Star Shop" : "Market";

				for (Item item : shop.inventory) {
					Row row = sheet.createRow(rowIndex);
					row.setHeightInPoints(20);

					ctx.text(row, 0, shopName, plain(true, HorizontalAlignment.LEFT));
					ctx.text(row, 1, String.format("(%d, %d)", x, y), plain(false, HorizontalAlignment.LEFT));
					icon(ctx, sheet, item, 2, rowIndex);

					Cell itemCell = ctx.text(row, 3, item.toString(), plain(false, HorizontalAlignment.LEFT));
					ctx.links.link(itemCell, LinkRegistry.Kind.ITEM, item);

					int cost = item.getCost();
					String costText = cost == 0 ? "--" : starShop ? cost + " Star Piece(s)" : "$" + cost;
					ctx.text(row, 4, costText, plain(false, HorizontalAlignment.CENTER));

					rowIndex++;
				}
			}
			rowIndex++; // blank row between locations
		}
	}
}