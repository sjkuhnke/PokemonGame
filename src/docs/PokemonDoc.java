package docs;

import pokemon.*;
import pokemon.Nursery.EggGroup;
import util.Pair;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.nio.file.Path;

public class PokemonDoc {
	private static final int CARD_COLS = 8; // columns 0-7 used per Pokemon card

	// Column reserved for the sprite thumbnail in the header/type rows.
	// Text content in those two rows starts at SPRITE_COLS instead of 0.
	private static final int SPRITE_COLS = 2;

	public static void writePokemonToExcel(Path dir) {
	    Workbook wb = new XSSFWorkbook();
	    Sheet sheet = wb.createSheet("Pokemon Info");

	    // Consistent column widths across the whole sheet so every card lines up
	    for (int c = 0; c < CARD_COLS; c++) {
	        sheet.setColumnWidth(c, 3400); // ~13 chars wide
	    }

	    List<Pokemon> allPokemon = new ArrayList<>();
	    List<Integer> allIds = new ArrayList<>();
	    Map<Integer, Integer> idToHeaderRow = new HashMap<>(); // originalId -> header row num on this sheet, for linking

	    int rowIndex = 0;

	    int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE + 2];
	    int counter = 0;
	    for (Pokemon p : Player.pokedex1) {
	        ids[counter] = p.getID();
	        counter++;
	        if (p.getID() == 150) {
	            ids[counter++] = 237;
	        }
	        if (p.getID() == 290) {
	            ids[counter++] = 291;
	        }
	    }
	    for (Pokemon p : Player.pokedex2) {
	        ids[counter] = p.getID();
	        counter++;
	    }
	    for (Pokemon p : Player.pokedex3) {
	        ids[counter] = p.getID();
	        counter++;
	    }
	    for (Pokemon p : Player.pokedex4) {
	        ids[counter] = p.getID();
	        counter++;
	    }

	    for (int i : ids) {
	        Pokemon p = new Pokemon(i, 5, false, false);
	        rowIndex = writeCard(sheet, p, i, rowIndex, idToHeaderRow);
	        allPokemon.add(p);
	        allIds.add(i);
	    }

	    writeTMCompatibilitySheet(wb, sheet, allPokemon, allIds, idToHeaderRow);

	    Path outPath = dir.resolve("PokemonInfo.xlsx");
	    try (FileOutputStream fileOut = new FileOutputStream(outPath.toFile())) {
	        wb.write(fileOut);
	        wb.close();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}

	// ----------------------------------------------------------------------
	// Builds one Pokemon "card" starting at startRow, returns the next free row
	// ----------------------------------------------------------------------
	private static int writeCard(Sheet sheet, Pokemon p, int originalId, int startRow, Map<Integer, Integer> idToHeaderRow) {
	    Workbook wb = sheet.getWorkbook();
	    Color type1Color = p.type1.getColor();
	    Color type2Color = p.type2 != null ? p.type2.getColor() : new Color(120, 120, 120);

	    // ---------------- Header row: "#001 - Name" + [ID] chip ----------------
	    Row headerRow = sheet.createRow(startRow++);
	    headerRow.setHeight((short) 758);
	    idToHeaderRow.put(originalId, headerRow.getRowNum());

	    Cell nameCell = headerRow.createCell(SPRITE_COLS);
	    String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
	    nameCell.setCellValue(dexNo + "  -  " + p.name());
	    nameCell.setCellStyle(fillStyle(wb, type1Color, true, false, 14, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), SPRITE_COLS, 6));

	    // ID chip - dev/testing info only, so it just gets a single narrow cell
	    Cell idCell = headerRow.createCell(7);
	    idCell.setCellValue(Pokemon.getFormattedDexNo(originalId).replace('#', '[') + "]");
	    idCell.setCellStyle(fillStyle(wb, type1Color.darker(), true, false, 10, HorizontalAlignment.CENTER));

	    // ---------------- Type badges (name only - icons live in the combined-icon row below the sprite) ----------------
	    Row typeRow = sheet.createRow(startRow++);
	    typeRow.setHeight((short) 758);

	    Cell type1Cell = typeRow.createCell(SPRITE_COLS);
	    type1Cell.setCellValue(p.type1.toString().toUpperCase());
	    type1Cell.setCellStyle(fillStyle(wb, type1Color, true, false, 11, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(typeRow.getRowNum(), typeRow.getRowNum(), SPRITE_COLS, SPRITE_COLS + 2));

	    Cell type2Cell = typeRow.createCell(SPRITE_COLS + 3);
	    type2Cell.setCellValue(p.type2 != null ? p.type2.toString().toUpperCase() : "—");
	    type2Cell.setCellStyle(fillStyle(wb, p.type2 != null ? type2Color : new Color(210, 210, 210),
	            true, false, 11, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(typeRow.getRowNum(), typeRow.getRowNum(), SPRITE_COLS + 3, 7));


	    Row typeIconRow = sheet.createRow(startRow++);
	    typeIconRow.setHeight((short) 384); // 18.75pt / ~25px

	    try {
	        byte[] spriteBytes = DocUtils.imageToBytes(DocUtils.getCachedSprite(p), "png");
	        if (spriteBytes != null) {
	            DocUtils.insertImage(sheet, spriteBytes, 0, headerRow.getRowNum(), 1, 2, 1, 1);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    try {
	        BufferedImage type1Icon = p.type1.getImage2();
	        BufferedImage type2Icon = p.type2 != null ? p.type2.getImage2() : null;
	        byte[] combinedTypeBytes = DocUtils.imageToBytes(DocUtils.combineIcons(type1Icon, type2Icon), "png");
	        if (combinedTypeBytes != null) {
	        	DocUtils.insertImage(sheet, combinedTypeBytes, 0, typeIconRow.getRowNum(), 1, 1, 0.5, 1);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // ---------------- Abilities ----------------
	    p.setAbility(0);
	    Ability ability1 = p.ability;
	    p.setAbility(1);
	    Ability ability2 = (p.ability == ability1) ? null : p.ability;
	    p.setAbility(2);
	    Ability hiddenAbility = (p.ability == Ability.NULL || p.ability == ability1) ? null : p.ability;

	    Row abilityRow = sheet.createRow(startRow++);
	    Cell a1Cell = abilityRow.createCell(0);
	    a1Cell.setCellValue(ability1.toString());
	    a1Cell.setCellStyle(labelStyle(wb, true, false, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(abilityRow.getRowNum(), abilityRow.getRowNum(), 0, 2));

	    Cell a2Cell = abilityRow.createCell(3);
	    a2Cell.setCellValue(ability2 != null ? ability2.toString() : "—");
	    a2Cell.setCellStyle(labelStyle(wb, true, false, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(abilityRow.getRowNum(), abilityRow.getRowNum(), 3, 5));

	    Cell hiddenCell = abilityRow.createCell(6);
	    hiddenCell.setCellValue(hiddenAbility != null ? "(" + hiddenAbility + ")" : "(—)");
	    hiddenCell.setCellStyle(labelStyle(wb, true, true, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(abilityRow.getRowNum(), abilityRow.getRowNum(), 6, 7));

	    // ---------------- Base stats ----------------
	    Row statLabelRow = sheet.createRow(startRow++);
	    Row statValueRow = sheet.createRow(startRow++);
	    for (int j = 0; j < p.baseStats.length; j++) {
	        Cell labelCell = statLabelRow.createCell(j);
	        labelCell.setCellValue(Pokemon.getStatType(j, false).trim());
	        labelCell.setCellStyle(statLabelStyle(wb));

	        int statVal = p.getBaseStat(j);
	        Cell valueCell = statValueRow.createCell(j);
	        valueCell.setCellValue(statVal);
	        valueCell.setCellStyle(statValueStyle(wb, statVal));
	    }
	    Cell bstLabel = statLabelRow.createCell(p.baseStats.length);
	    bstLabel.setCellValue("BST");
	    bstLabel.setCellStyle(statLabelStyle(wb));
	    Cell bstValue = statValueRow.createCell(p.baseStats.length);
	    bstValue.setCellValue(p.getBST());
	    bstValue.setCellStyle(fillStyle(wb, new Color(60, 60, 60), true, false, 11, HorizontalAlignment.CENTER));

	    // ---------------- Weight / Catch Rate / Egg Cycles / Egg Group ----------------
	    Row infoRow = sheet.createRow(startRow++);
	    infoRow.setHeightInPoints(16);

	    Cell weightCell = infoRow.createCell(0);
	    weightCell.setCellValue("Weight: " + p.weight + " lbs");
	    weightCell.setCellStyle(infoStyle(wb));
	    sheet.addMergedRegion(new CellRangeAddress(infoRow.getRowNum(), infoRow.getRowNum(), 0, 1));

	    Cell catchCell = infoRow.createCell(2);
	    catchCell.setCellValue("Catch: " + p.catchRate);
	    catchCell.setCellStyle(infoStyle(wb));
	    sheet.addMergedRegion(new CellRangeAddress(infoRow.getRowNum(), infoRow.getRowNum(), 2, 3));

	    Cell eggCyclesCell = infoRow.createCell(4);
	    eggCyclesCell.setCellValue("Egg Cyc: " + Egg.computeEggCycles(p.getFinalEvolution()));
	    eggCyclesCell.setCellStyle(infoStyle(wb));
	    sheet.addMergedRegion(new CellRangeAddress(infoRow.getRowNum(), infoRow.getRowNum(), 4, 5));

	    ArrayList<EggGroup> eggGroups = Pokemon.getEggGroup(originalId);
	    String eggGroupStr = eggGroups.get(0).equals(eggGroups.get(1)) ?
	            eggGroups.get(0).toString() :
	            eggGroups.get(0) + "/" + eggGroups.get(1);
	    Cell eggGroupCell = infoRow.createCell(6);
	    eggGroupCell.setCellValue(eggGroupStr);
	    eggGroupCell.setCellStyle(infoStyle(wb));
	    sheet.addMergedRegion(new CellRangeAddress(infoRow.getRowNum(), infoRow.getRowNum(), 6, 7));

	    // ---------------- Evolutions ----------------
	    // Split evolutions (e.g. Kirlia -> Gardevoir / Kirlia -> Gallade) come back
	    // as one \n-separated string - give each branch its own row.
	    if (p.canEvolve()) {
	        String[] evoLines = p.getEvolveString().split("\n");
	        for (String evoLine : evoLines) {
	            Row evoRow = sheet.createRow(startRow++);
	            Cell evoCell = evoRow.createCell(0);
	            evoCell.setCellValue(evoLine);
	            evoCell.setCellStyle(fillStyle(wb, new Color(225, 240, 225), false, true, 11, HorizontalAlignment.LEFT));
	            sheet.addMergedRegion(new CellRangeAddress(evoRow.getRowNum(), evoRow.getRowNum(), 0, 7));
	        }
	    }

	    // ---------------- Level-up moves grid (one move per cell, no wrapping) ----------------
	    Row movesLabelRow = sheet.createRow(startRow++);
	    Cell movesLabel = movesLabelRow.createCell(0);
	    movesLabel.setCellValue("Level-Up Moves");
	    movesLabel.setCellStyle(labelStyle(wb, true, false, HorizontalAlignment.CENTER));
	    sheet.addMergedRegion(new CellRangeAddress(movesLabelRow.getRowNum(), movesLabelRow.getRowNum(), 0, 7));

	    ArrayList<Pair<String, Move>> moveEntries = new ArrayList<>(); // {level, move}
	    Node[] movebank = p.getMovebank();
	    for (int j = 0; j < movebank.length; j++) {
	        Node n = movebank[j];
	        while (n != null) {
	            String level = j == 0 ? "E" : j + "";
	            moveEntries.add(new Pair<String, Move>(level, n.data));
	            n = n.next;
	        }
	    }

	    // Vertical (column-major) fill: moves go down each column before wrapping
	    // to the next one, e.g.
	    //   E-Move1   Move5
	    //   1-Move2   Move6
	    //   1-Move3   Move7
	    //   2-Move4   Move8
	    int moveCols = 4;
	    int moveRowCount = (int) Math.ceil(moveEntries.size() / (double) moveCols);
	    Row[] moveRows = new Row[moveRowCount];
	    for (int r = 0; r < moveRowCount; r++) {
	        moveRows[r] = sheet.createRow(startRow++);
	    }

	    for (int idx = 0; idx < moveEntries.size(); idx++) {
	        int colSlot = idx / moveRowCount; // which of the 4 move columns
	        int rowSlot = idx % moveRowCount;  // which row within the grid

	        Pair<String, Move> entry = moveEntries.get(idx);
	        Move move = entry.getSecond();
	        PType mtype = move.mtype;

	        int col = colSlot * 2;
	        Row moveRow = moveRows[rowSlot];
	        Cell moveCell = moveRow.createCell(col);
	        moveCell.setCellValue(entry.getFirst() + " - " + entry.getSecond());
	        moveCell.setCellStyle(fillStyle(wb, mtype.getColor(), true, false, 10, HorizontalAlignment.LEFT));
	        sheet.addMergedRegion(new CellRangeAddress(moveRow.getRowNum(), moveRow.getRowNum(), col, col + 1));
	    }

	    // ---------------- Spacer row ----------------
	    startRow++;

	    return startRow;
	}

	// ----------------------------------------------------------------------
	// TM/HM Compatibility sheet - one row per Pokemon, one column per TM/HM
	// ----------------------------------------------------------------------
	private static void writeTMCompatibilitySheet(Workbook wb, Sheet pokemonSheet, List<Pokemon> allPokemon, List<Integer> allIds, Map<Integer, Integer> idToHeaderRow) {
	    Sheet sheet = wb.createSheet("TM Compatibility");
	    CreationHelper helper = wb.getCreationHelper();
	    String pokemonSheetName = pokemonSheet.getSheetName();
	    String tmSheetName = sheet.getSheetName();

	    boolean[][] tms = Pokemon.getTMTable();
	    List<String> tmLabels = getTMHMLabelList();

	    sheet.setColumnWidth(0, 3400); // "#001 Name" column
	    sheet.setColumnWidth(1, 900);  // spacer col so header row can rotate cleanly
	    for (int c = 0; c < tmLabels.size(); c++) {
	        sheet.setColumnWidth(c + 2, 720); // narrow columns, just enough for a checkmark
	    }

	    // ---------------- Header row ----------------
	    Row headerRow = sheet.createRow(0);
	    headerRow.setHeightInPoints(92);

	    Cell cornerCell = headerRow.createCell(0);
	    cornerCell.setCellValue("Pokemon");
	    cornerCell.setCellStyle(fillStyle(wb, new Color(60, 60, 60), true, false, 11, HorizontalAlignment.LEFT));
	    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

	    for (int c = 0; c < tmLabels.size(); c++) {
	        Cell labelCell = headerRow.createCell(c + 2);
	        labelCell.setCellValue(tmLabels.get(c));
	        labelCell.setCellStyle(rotatedHeaderStyle(wb));
	    }

	    sheet.createFreezePane(2, 1); // keep name column + header row visible while scrolling

	    // ---------------- One row per Pokemon ----------------
	    for (int r = 0; r < allPokemon.size(); r++) {
	        Pokemon p = allPokemon.get(r);
	        int id = allIds.get(r);
	        Row row = sheet.createRow(r + 1);

	        Cell nameCell = row.createCell(0);
	        nameCell.setCellValue(Pokemon.getFormattedDexNo(p.getDexNo()) + " " + p.name());
	        nameCell.setCellStyle(infoStyle(wb));
	        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));

	        // Link this TM row back to the Pokemon's card, and the card's name
	        // cell forward to this row, so either sheet can jump to the other.
	        Integer headerRowNum = idToHeaderRow.get(id);
	        if (headerRowNum != null) {
	            Hyperlink toCard = helper.createHyperlink(HyperlinkType.DOCUMENT);
	            toCard.setAddress("'" + pokemonSheetName + "'!A" + (headerRowNum + 1));
	            nameCell.setHyperlink(toCard);

	            Row cardHeaderRow = pokemonSheet.getRow(headerRowNum);
	            Cell cardNameCell = cardHeaderRow != null ? cardHeaderRow.getCell(SPRITE_COLS) : null;
	            if (cardNameCell != null) {
	                Hyperlink toTMRow = helper.createHyperlink(HyperlinkType.DOCUMENT);
	                toTMRow.setAddress("'" + tmSheetName + "'!A" + (row.getRowNum() + 1));
	                cardNameCell.setHyperlink(toTMRow);
	            }
	        }

	        // id is 1-based (matches writeTMLearn's loop); guard in case an alt-form id
	        // (e.g. 237, 291) falls outside the table's bounds.
	        boolean[] compatRow = (id - 1 >= 0 && id - 1 < tms.length) ? tms[id - 1] : null;

	        for (int c = 0; c < tmLabels.size(); c++) {
	            boolean compatible = compatRow != null && c < compatRow.length && compatRow[c];
	            Cell cell = row.createCell(c + 2);
	            if (compatible) {
	                cell.setCellValue("Y");
	                cell.setCellStyle(fillStyle(wb, new Color(190, 230, 190), true, false, 9, HorizontalAlignment.CENTER));
	            } else {
	                cell.setCellStyle(plainStyle(wb));
	            }
	        }
	    }
	}

	private static List<String> getTMHMLabelList() {
	    List<String> labels = new ArrayList<>();
	    for (int tm = 93; tm <= 199; tm++) {
	        labels.add(Item.getItem(tm).toString());
	    }
	    return labels;
	}
	
	public static void writePokemonToTxt(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile());
			
			int[] ids = new int[Pokemon.POKEDEX_1_SIZE + Pokemon.POKEDEX_METEOR_SIZE * 2 + Pokemon.POKEDEX_2_SIZE + 2];
			int counter = 0;
			for (Pokemon p : Player.pokedex1) {
				ids[counter] = p.getID();
				counter++;
				if (p.getID() == 150) {
					ids[counter++] = 237;
				}
				if (p.getID() == 290) {
					ids[counter++] = 291;
				}
			}
			for (Pokemon p : Player.pokedex2) {
				ids[counter] = p.getID();
				counter++;
			}
			for (Pokemon p : Player.pokedex3) {
				ids[counter] = p.getID();
				counter++;
			}
			for (Pokemon p : Player.pokedex4) {
				ids[counter] = p.getID();
				counter++;
			}
			
			for (int i : ids) {
				Pokemon p = new Pokemon(i, 5, false, false);
				writer.write("===================\n");
				String dexNo = Pokemon.getFormattedDexNo(p.getDexNo());
				String name = dexNo + " - " + p.name();
				while (name.length() < 103) {
					name = name + " ";
				}
				name = name + Pokemon.getFormattedDexNo(p.getID()).replace('#', '[') + "]\n";
				writer.write(name);
				writer.write("===================\n");
				
				writer.write("Type:\n");
				String type = p.type1.toString() + " / ";
				type = p.type2 == null ? type + "None" : type + p.type2.toString();
				writer.write(type + "\n\n");
				
				writer.write("Ability:\n");
				StringBuilder abilityBuilder = new StringBuilder();
				p.setAbility(0);
				Ability ability1 = p.ability;
				abilityBuilder.append(ability1.toString()).append(" / ");
				p.setAbility(1);
				if (p.ability == ability1) {
				    abilityBuilder.append("None");
				} else {
				    abilityBuilder.append(p.ability.toString());
				}
				p.setAbility(2);
				abilityBuilder.append(" / (");
				if (p.ability == Ability.NULL || p.ability == ability1) {
				    abilityBuilder.append("None");
				} else {
				    abilityBuilder.append(p.ability.toString());
				}
				abilityBuilder.append(")\n\n");
				writer.write(abilityBuilder.toString());
				
				writer.write("Base Stats:\n");
				String stats = "";
				for (int j = 0; j < p.baseStats.length; j++) {
					stats += p.getBaseStat(j) + " " + Pokemon.getStatType(j, false) + "/ ";
				}
				stats += p.getBST() + " BST";
				writer.write(stats + "\n\n");
				
				writer.write("Level Up:\n");
				Node[] movebank = p.getMovebank();
				for (int j = 0; j < movebank.length; j++) {
					Node n = movebank[j];
					while (n != null) {
						String level = j == 0 ? "E" : j + "";
						String move = level + " - " + n.data.toString() + "\n";
						n = n.next;
						writer.write(move);
					}
				}
				writer.write("\n");
				if (p.canEvolve()) {
					writer.write("Evolutions:\n");
					writer.write(p.getEvolveString() + "\n\n");
				}
				
				writer.write(String.format("%-13s| %s lbs\n", "Weight", p.weight));
				writer.write(String.format("%-13s| %d\n", "Catch Rate", p.catchRate));
				writer.write(String.format("%-13s| %d\n", "Egg Cycles", Egg.computeEggCycles(p.getFinalEvolution())));

				ArrayList<EggGroup> eggGroups = Pokemon.getEggGroup(i);
				String eggGroupStr = eggGroups.get(0).equals(eggGroups.get(1)) ?
				    eggGroups.get(0).toString() :
				    eggGroups.get(0) + ", " + eggGroups.get(1);
				writer.write(String.format("%-13s| %s\n", "Egg Group(s)", eggGroupStr));
				
				writer.write("\n");
				
			}
			writer.close();
			
			writeTMLearn(dir);
			//writeUnusedMoves(dir);
			//writeTypeStats(dir);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private static void writeTMLearn(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("TM Learnsets:\n");
			
			boolean[][] tms = Pokemon.getTMTable();
			int id = 1;
			
			StringBuilder header = new StringBuilder("====================================================================================\n");
			header.append("ID   Name               ");
			for (int hm = 1; hm <= 8; hm++) {
				header.append(String.format("HM%02d  ", hm));
			}
			for (int tm = 1; tm <= 99; tm++) {
				header.append(String.format("TM%02d  ", tm));
			}
			header.append("\n");
			header.append("====================================================================================\n");
			writer.write(header.toString());
			
			for (boolean[] row : tms) {
				if (id % 25 == 0) {
					writer.write(header.toString());
				}
				StringBuilder rowBuilder = new StringBuilder();
				String pokemonName = Pokemon.getName(id);
				rowBuilder.append(String.format("#%03d %-20s", id, pokemonName));
				for (boolean canLearn : row) {
					rowBuilder.append(canLearn ? "Y     " : "N     ");
				}
				rowBuilder.append("\n");
				id++;
				writer.write(rowBuilder.toString());
			}
			writer.write("====================================================================================\n");
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void writeTypeStats(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("Stats:\n");
			ArrayList<PType> types = new ArrayList<>(Arrays.asList(PType.values()));
			types.remove(PType.UNKNOWN);
			int[][] sums = new int[types.size()][6];
			int[] amounts = new int[types.size()];
			
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				for (int j = 0; j < p.baseStats.length; j++) {
					sums[types.indexOf(p.type1)][j] += p.baseStats[j];
					if (p.type2 != null) sums[types.indexOf(p.type2)][j] += p.baseStats[j];
				}
				amounts[types.indexOf(p.type1)]++;
				if (p.type2 != null) amounts[types.indexOf(p.type2)]++;
			}
			StringBuilder header = new StringBuilder();
			header.append("Type");
			for (int i = 0; i < sums[1].length; i++) {
				header.append("," + Pokemon.getStatType(i, false).trim());
			}
			header.append(",Amt\n");
			writer.write(header.toString());
			for (int i = 0; i < types.size(); i++) {
				StringBuilder stats = new StringBuilder();
				stats.append(types.get(i).toString());
				for (int j = 0; j < sums[1].length; j++) {
					double average = sums[i][j] * 1.0;
					average /= amounts[i];
					stats.append("," + String.format("%.1f", average));
				}
				stats.append("," + amounts[i]);
				stats.append("\n");
				writer.write(stats.toString());
			}
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void printIntArray2D(int[][] array) {
		// Iterate over each row of the 2D array
		for (int i = 0; i < array.length; i++) {
			// Iterate over each element in the current row
			for (int j = 0; j < array[i].length; j++) {
				// Print the current element followed by a space
				System.out.print(array[i][j] + " ");
			}
			// Move to the next line after printing all elements in the current row
			System.out.println();
		}
	}

	@SuppressWarnings("unused")
	private static void writeUnusedMoves(Path dir) {
		try {
			Path outPath = dir.resolve("PokemonInfo.txt");
			FileWriter writer = new FileWriter(outPath.toFile(), true);
			writer.write("Unused moves:\n");
			ArrayList<Move> unused = new ArrayList<>();
			ArrayList<Move> unusedButTM = new ArrayList<>();
			Map<Pokemon, Move> sigOne = new HashMap<>();
			Map<Pokemon, Move> sigTwo = new HashMap<>();
			Map<Pokemon, Move> sigThree = new HashMap<>();
			Map<Move, Integer> moveCount = new HashMap<>();
			for (Move m : Move.getAllMoves()) {
				moveCount.put(m, 0);
			}
			for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
				Pokemon p = new Pokemon(i, 5, false, false);
				ArrayList<Move> movebank = new ArrayList<>();
				Node[] pokemonMovebank = p.getMovebank();
				for (int j = 0; j < pokemonMovebank.length; j++) {
					Node n = pokemonMovebank[j];
					while (n != null) {
						movebank.add(n.data);
						n = n.next;
					}
				}

				for (Move move : movebank) {
					moveCount.put(move, moveCount.getOrDefault(move, 0) + 1);
				}
			}
			for (Map.Entry<Move, Integer> entry : moveCount.entrySet()) {
				if (entry.getValue() == 0) {
					if (entry.getKey().isTM()) {
						unusedButTM.add(entry.getKey());
					} else {
						unused.add(entry.getKey());
					}
				} else if (entry.getValue() == 3) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 3; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigThree.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 2) {
					int count = 0;
					for (int i = 1; i <= Pokemon.MAX_POKEMON && count < 2; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigTwo.put(p, entry.getKey());
							count++;
						}
					}
				} else if (entry.getValue() == 1) {
					for (int i = 1; i <= Pokemon.MAX_POKEMON; i++) {
						Pokemon p = new Pokemon(i, 5, false, false);
						ArrayList<Move> movebank = new ArrayList<>();
						Node[] pokemonMovebank = p.getMovebank();
						for (int j = 0; j < pokemonMovebank.length; j++) {
							Node n = pokemonMovebank[j];
							while (n != null) {
								movebank.add(n.data);
								n = n.next;
							}
						}
						if (movebank.contains(entry.getKey())) {
							sigOne.put(p, entry.getKey());
							break;
						}
					}
				}
			}
			for (Move m : unused) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nTM Only:\n");
			for (Move m : unusedButTM) {
				writer.write(m.toString() + "\n");
			}
			writer.write("\nSignature Moves (3):\n");
			sigThree.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			writer.write("\nSignature Moves (2):\n");
			sigTwo.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		
			writer.write("\nSignature Moves (1):\n");
			sigOne.entrySet().stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Move::toString)))
			.forEach(entry -> {
				try {
					writer.write(entry.getValue().toString() + " : " + entry.getKey().name() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	// ----------------------------------------------------------------------
	// Style helpers
	// ----------------------------------------------------------------------
	private static CellStyle fillStyle(Workbook wb, Color bg, boolean bold, boolean italic, int fontSize, HorizontalAlignment align) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(bg, null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(align);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(bold);
	    font.setItalic(italic);
	    font.setFontHeightInPoints((short) fontSize);
	    font.setColor(new XSSFColor(textColorFor(bg), null));
	    style.setFont(font);

	    return style;
	}

	private static CellStyle labelStyle(Workbook wb, boolean bold, boolean italic, HorizontalAlignment align) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(240, 240, 240), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(align);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(bold);
	    font.setItalic(italic);
	    font.setFontHeightInPoints((short) 11);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle statLabelStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setFillForegroundColor(new XSSFColor(new Color(230, 230, 230), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 9);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle statValueStyle(Workbook wb, int statValue) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 12);
	    // quick visual cue: strong stats pop green, weak stats fade red
	    if (statValue >= 100) {
	        font.setColor(new XSSFColor(new Color(30, 120, 40), null));
	    } else if (statValue <= 40) {
	        font.setColor(new XSSFColor(new Color(170, 40, 40), null));
	    } else {
	        font.setColor(new XSSFColor(new Color(30, 30, 30), null));
	    }
	    style.setFont(font);

	    return style;
	}

	private static CellStyle infoStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.LEFT);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setFontHeightInPoints((short) 10);
	    style.setFont(font);

	    return style;
	}

	private static CellStyle plainStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.CENTER);
	    return style;
	}

	private static CellStyle rotatedHeaderStyle(Workbook wb) {
	    XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
	    style.setFillForegroundColor(new XSSFColor(new Color(80, 80, 80), null));
	    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    style.setAlignment(HorizontalAlignment.CENTER);
	    style.setVerticalAlignment(VerticalAlignment.BOTTOM);
	    style.setRotation((short) 90);

	    XSSFFont font = (XSSFFont) wb.createFont();
	    font.setBold(true);
	    font.setFontHeightInPoints((short) 9);
	    font.setColor(new XSSFColor(Color.WHITE, null));
	    style.setFont(font);

	    return style;
	}

	// Picks black or white text based on background luminance so labels stay readable
	private static Color textColorFor(Color bg) {
	    double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue());
	    return luminance > 150 ? new Color(20, 20, 20) : Color.WHITE;
	}
}