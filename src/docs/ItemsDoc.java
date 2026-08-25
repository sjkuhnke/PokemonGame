package docs;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import entity.PlayerCharacter;
import object.ItemObj;
import object.TreasureChest;
import overworld.GamePanel;
import overworld.PMap;
import pokemon.Item;

public class ItemsDoc {

	// icon | name | pocket | buy | sell | description
	private static final int COL_ICON = 0, COL_NAME = 1, COL_POCKET = 2, COL_BUY = 3, COL_SELL = 4, COL_DESC = 5;
	private static final int ITEMS_LAST_COL = COL_DESC;
	private static final int DESC_COL_CHARS = 60;

	// Overworld sheet layout: icon | item name (merged) | coordinates
	private static final int OW_LAST_COL = 8; // matches TrainerDoc's 9-wide location banner

	public static void writeItemsToExcel(GamePanel gp, Path dir) {
	    Workbook wb = new XSSFWorkbook();

	    writeItemsSheet(wb);
	    writeTMLocationsSheet(wb);
	    writeOverworldItemsSheet(wb, gp);

	    Path outPath = dir.resolve("ItemsInfo.xlsx");
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Sheet 1: Items - icon + full description table
	// ----------------------------------------------------------------------
	private static void writeItemsSheet(Workbook wb) {
	    Sheet sheet = wb.createSheet("Items");
	    sheet.setColumnWidth(COL_ICON, 1200);
	    sheet.setColumnWidth(COL_NAME, 5800);
	    sheet.setColumnWidth(COL_POCKET, 3000);
	    sheet.setColumnWidth(COL_BUY, 1800);
	    sheet.setColumnWidth(COL_SELL, 1800);
	    sheet.setColumnWidth(COL_DESC, 16000);
	    sheet.createFreezePane(0, 1);

	    Row header = sheet.createRow(0);
	    header.setHeightInPoints(20);
	    String[] labels = new String[6];
	    labels[COL_ICON] = "";
	    labels[COL_NAME] = "Item";
	    labels[COL_POCKET] = "Pocket";
	    labels[COL_BUY] = "Buy";
	    labels[COL_SELL] = "Sell";
	    labels[COL_DESC] = "Description";
	    for (int c = 0; c <= ITEMS_LAST_COL; c++) {
	        Cell cell = header.createCell(c);
	        cell.setCellValue(labels[c]);
	        cell.setCellStyle(headerStyle(wb));
	    }

	    Item[] allItems = Item.values();
	    int rowIndex = 1;
	    for (Item i : allItems) {
	        Row row = sheet.createRow(rowIndex);
	        row.setHeightInPoints(20);

	        insertItemIcon(sheet, i, COL_ICON, row.getRowNum());

	        Cell nameCell = row.createCell(COL_NAME);
	        nameCell.setCellValue(i.toString());
	        nameCell.setCellStyle(plainStyle(wb, true, HorizontalAlignment.LEFT));

	        Cell pocketCell = row.createCell(COL_POCKET);
	        pocketCell.setCellValue(Item.getPocketName(i.getPocket()));
	        pocketCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.LEFT));

	        int cost = i.getCost();
	        Cell buyCell = row.createCell(COL_BUY);
	        buyCell.setCellValue(cost == 0 ? "--" : "$" + cost);
	        buyCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	        int sell = i.getSell();
	        Cell sellCell = row.createCell(COL_SELL);
	        sellCell.setCellValue(sell == 0 ? "--" : "$" + sell);
	        sellCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.CENTER));

	        String desc = i.getDesc();
	        Cell descCell = row.createCell(COL_DESC);
	        descCell.setCellValue(desc);
	        descCell.setCellStyle(descStyle(wb));

	        DocUtils.setWrappedRowHeight(row, desc, DESC_COL_CHARS, 13f);

	        rowIndex++;
	    }
	}

	// ----------------------------------------------------------------------
	// Sheet 2: TM Locations - parsed from the same bundled resource the txt
	// doc reads from.
	// ----------------------------------------------------------------------
	private static void writeTMLocationsSheet(Workbook wb) {
	    Sheet sheet = wb.createSheet("TM Locations");
	    sheet.setColumnWidth(0, 6500);
	    sheet.setColumnWidth(1, 18000);
	    sheet.createFreezePane(0, 1);

	    Row header = sheet.createRow(0);
	    header.setHeightInPoints(20);
	    Cell tmHeader = header.createCell(0);
	    tmHeader.setCellValue("TM / HM");
	    tmHeader.setCellStyle(headerStyle(wb));
	    Cell locHeader = header.createCell(1);
	    locHeader.setCellValue("Location");
	    locHeader.setCellStyle(headerStyle(wb));

	    int rowIndex = 1;
	    try (Scanner scanner = new Scanner(ItemsDoc.class.getResourceAsStream("/info/tm_locations.txt"))) {
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
	            Cell nameCell = row.createCell(0);
	            nameCell.setCellValue(tmName);
	            nameCell.setCellStyle(plainStyle(wb, true, HorizontalAlignment.LEFT));

	            Cell locCell = row.createCell(1);
	            locCell.setCellValue(location);
	            locCell.setCellStyle(descStyle(wb));
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
	// Sheet 3: Overworld Items - grouped by location, TrainerDoc-style banners
	// ----------------------------------------------------------------------
	private static void writeOverworldItemsSheet(Workbook wb, GamePanel gp) {
	    Sheet sheet = wb.createSheet("Overworld Items");
	    sheet.setColumnWidth(0, 1200);
	    for (int c = 1; c <= OW_LAST_COL; c++) {
	        sheet.setColumnWidth(c, 2600);
	    }

	    boolean[][] tempItemsCollected = gp.player.p.itemsCollected.clone();
	    gp.player.p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
	    gp.aSetter.setObject();
	    ItemObj[][] items = gp.obj;

	    Map<String, ArrayList<ItemObj>> itemsMap = new LinkedHashMap<>();
	    for (int loc = 0; loc < items.length; loc++) {
	        for (int col = 0; col < items[loc].length; col++) {
	            ItemObj e = items[loc][col];
	            if (e == null) continue;
	            PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
	            String location = PlayerCharacter.currentMapName;
	            itemsMap.computeIfAbsent(location, k -> new ArrayList<>()).add(e);
	        }
	    }

	    int rowIndex = 0;
	    for (Map.Entry<String, ArrayList<ItemObj>> e : itemsMap.entrySet()) {
	        rowIndex = DocUtils.writeLocationHeader(sheet, rowIndex, e.getKey(), OW_LAST_COL);

	        for (ItemObj obj : e.getValue()) {
	            rowIndex = writeOverworldItemRow(wb, sheet, gp, obj, rowIndex);
	        }
	        rowIndex++; // blank row between locations
	    }

	    // cleanup - restore the player's real collected-items state
	    gp.player.p.itemsCollected = tempItemsCollected;
	    gp.aSetter.setObject();
	}

	private static int writeOverworldItemRow(Workbook wb, Sheet sheet, GamePanel gp, ItemObj obj, int rowIndex) {
	    int x = obj.worldX / gp.tileSize;
	    int y = obj.worldY / gp.tileSize;
	    boolean chest = obj instanceof TreasureChest;

	    Row row = sheet.createRow(rowIndex++);
	    row.setHeightInPoints(20);

	    Item item = chest ? null : obj.item;
	    if (item != null) {
	        insertItemIcon(sheet, item, 0, row.getRowNum());
	    }

	    Cell nameCell = row.createCell(1);
	    nameCell.setCellValue(chest ? "Treasure Chest" : item.toString());
	    nameCell.setCellStyle(plainStyle(wb, true, HorizontalAlignment.LEFT));
	    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 5));

	    Cell coordCell = row.createCell(6);
	    coordCell.setCellValue(String.format("(%d, %d)", x, y));
	    coordCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.LEFT));
	    sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 6, OW_LAST_COL));

	    if (chest) {
	        for (Item contained : ((TreasureChest) obj).inventory) {
	            Row chestRow = sheet.createRow(rowIndex++);
	            chestRow.setHeightInPoints(18);

	            insertItemIcon(sheet, contained, 1, chestRow.getRowNum());

	            Cell chestItemCell = chestRow.createCell(2);
	            chestItemCell.setCellValue(contained.toString());
	            chestItemCell.setCellStyle(plainStyle(wb, false, HorizontalAlignment.LEFT));
	            sheet.addMergedRegion(new CellRangeAddress(chestRow.getRowNum(), chestRow.getRowNum(), 2, OW_LAST_COL));
	        }
	    }

	    return rowIndex;
	}

	private static void insertItemIcon(Sheet sheet, Item item, int col, int rowNum) {
	    try {
	        java.awt.image.BufferedImage icon = item.getImage();
	        if (icon != null) {
	            byte[] bytes = DocUtils.imageToBytes(icon, "png");
	            if (bytes != null) {
	                DocUtils.insertImage(sheet, bytes, col, rowNum, 1, 1, 0.7, 0.7);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Moved from Main.java (formerly writeItems); output unchanged.
	// ----------------------------------------------------------------------
	public static void writeItemsToTxt(GamePanel gp, Path dir) {
	    try {
	        Path outPath = dir.resolve("ItemsInfo.txt");
	        FileWriter writer = new FileWriter(outPath.toFile());
	        writer.write("--------------------------------------\n");
	        writer.write("Items Info:\n");
	        writer.write("Item | Pocket | Buy | Sell | Description");
	        writer.write("\n--------------------------------------\n");
	        Item[] allItems = Item.values();
	        for (Item i : allItems) {
	            String item = i.toString();
	            while (item.length() < 22) {
	                item += " ";
	            }
	            String pocket = Item.getPocketName(i.getPocket());
	            while (pocket.length() < 10) {
	                pocket += " ";
	            }
	            int cost = i.getCost();
	            int s = i.getSell();
	            String buy = cost == 0 ? "--" : "$" + cost;
	            while (buy.length() < 5) {
	                buy += " ";
	            }
	            String sell = s == 0 ? "--" : "$" + s;
	            while (sell.length() < 5) {
	                sell += " ";
	            }
	            String desc = i.getDesc();
	            writer.write(String.format("%s | %s | %s | %s | %s\n", item, pocket, buy, sell, desc));
	        }

	        // TM Locations section
	        writer.write("\n--------------------------------------\n");
	        writer.write("TM Locations:\n");
	        writer.write("--------------------------------------\n");
	        try (Scanner scanner = new Scanner(ItemsDoc.class.getResourceAsStream("/info/tm_locations.txt"))) {
	            while (scanner.hasNextLine()) {
	                writer.write(scanner.nextLine() + "\n");
	            }
	        } catch (Exception ex) {
	            writer.write("Could not load TM Locations file.\n");
	            ex.printStackTrace();
	        }

	        boolean[][] tempItemsCollected = gp.player.p.itemsCollected.clone();
	        gp.player.p.itemsCollected = new boolean[gp.obj.length][gp.obj[1].length];
	        gp.aSetter.setObject();
	        ItemObj[][] items = gp.obj;
	        writer.write("\n--------------------------------------\n");
	        writer.write("Overworld Items:\n");
	        writer.write("(Note: the Variable Items:\n");
	        writer.write("- Nature Mints\n");
	        writer.write("- Type Resist Berries\n");
	        writer.write("- Stat Restoring Berries\n");
	        writer.write("- Treasure Chest Items\n");
	        writer.write("will show for only the save file you generated these docs for)");
	        writer.write("\n--------------------------------------\n");
	        Map<String, ArrayList<ItemObj>> itemsMap = new LinkedHashMap<>();
	        for (int loc = 0; loc < items.length; loc++) {
	            for (int col = 0; col < items[loc].length; col++) {
	                ItemObj e = items[loc][col];
	                if (e == null) continue;
	                PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
	                String location = PlayerCharacter.currentMapName;
	                if (itemsMap.containsKey(location)) {
	                    ArrayList<ItemObj> list = itemsMap.get(location);
	                    list.add(e);
	                } else {
	                    ArrayList<ItemObj> list = new ArrayList<>();
	                    list.add(e);
	                    itemsMap.put(location, list);
	                }
	            }
	        }

	        for (Map.Entry<String, ArrayList<ItemObj>> e : itemsMap.entrySet()) {
	            ArrayList<ItemObj> list = e.getValue();
	            String loc = e.getKey();
	            while (loc.length() < 50) {
	                loc += "-";
	            }
	            writer.write("\n\n" + loc + "\n");

	            for (ItemObj i : list) {
	                writer.write("\n");

	                int x = i.worldX / gp.tileSize;
	                int y = i.worldY / gp.tileSize;

	                boolean chest = i instanceof TreasureChest;
	                String itemString = chest ? "Treasure Chest" : i.item.toString();

	                writer.write(String.format("%s (%d, %d)", itemString, x, y));

	                if (chest) {
	                    for (Item it : i.inventory) {
	                        String label = "\n  [";
	                        label += it + "]";
	                        while (label.length() < 26) label += " ";
	                        label += "|";

	                        writer.write(label);
	                    }
	                }

	            }
	        }
	        writer.write("\n");

	        writer.close();

	        // cleanup
	        gp.player.p.itemsCollected = tempItemsCollected;
	        gp.aSetter.setObject();

	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Styles
	// ----------------------------------------------------------------------
	private static CellStyle headerStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 11);
	    font.setColor(new XSSFColor(Color.WHITE, null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle plainStyle(Workbook wb, boolean bold, HorizontalAlignment align) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(align);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(bold);
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle descStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.TOP);
	    style.setWrapText(true);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}
}