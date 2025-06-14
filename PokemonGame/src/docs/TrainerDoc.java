package docs;

import overworld.*;
import pokemon.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import entity.Entity;
import entity.PlayerCharacter;

import org.apache.poi.ss.util.CellRangeAddress;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TrainerDoc {
	
	private static BufferedImage[] sprites = new BufferedImage[Pokemon.MAX_POKEMON];
	
	public static void writeTrainersToExcel(GamePanel gp) {
		Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Trainer Teams");

        int rowIndex = 1;

        // Define cell style
        XSSFCellStyle headerStyle = (XSSFCellStyle) makeStyle(wb, true, false, 16, IndexedColors.WHITE.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(80, 80, 80), null));
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        Map<?, ?>[] maps = getTrainerLocationMap(gp);
        
        @SuppressWarnings("unchecked")
		Map<String, ArrayList<Trainer>> trainerMap = (Map<String, ArrayList<Trainer>>) maps[0];
		@SuppressWarnings("unchecked")
		Map<Trainer, Entity> trainerNPCMap = (Map<Trainer, Entity>) maps[1];
		
		for (Map.Entry<String, ArrayList<Trainer>> e : trainerMap.entrySet()) {
			String loc = e.getKey();
		    ArrayList<Trainer> trainers = e.getValue();
		    
		    // Location outline
		    CellStyle colored = sheet.getWorkbook().createCellStyle();
            colored.setFillForegroundColor(new XSSFColor(new java.awt.Color(80, 80, 80), null));
            colored.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Location header row
		    Row headerRow = sheet.createRow(rowIndex++);
		    Cell headerCell = headerRow.createCell(1);
		    headerCell.setCellValue(loc);
		    headerCell.setCellStyle(headerStyle);
		    
		    Cell o1 = headerRow.createCell(0);
		    o1.setCellStyle(colored);
		    Cell o2 = headerRow.createCell(8);
		    o2.setCellStyle(colored);
		    
		    sheet.addMergedRegion(new CellRangeAddress(headerRow.getRowNum(), headerRow.getRowNum(), 1, 7));
            
            Row outlineRowTop = sheet.createRow(rowIndex - 2);
            Row outlineRowBottom = sheet.createRow(rowIndex++);
            for (int i = 0; i <= 8; i++) {
            	Cell c1 = outlineRowTop.createCell(i);
            	c1.setCellStyle(colored);
            	Cell c2 = outlineRowBottom.createCell(i);
            	c2.setCellStyle(colored);
            }
            sheet.addMergedRegion(new CellRangeAddress(outlineRowTop.getRowNum(), outlineRowTop.getRowNum(), 0, 8));
            sheet.addMergedRegion(new CellRangeAddress(outlineRowBottom.getRowNum(), outlineRowBottom.getRowNum(), 0, 8));

		    for (Trainer tr : trainers) {
		        Entity npc = trainerNPCMap.get(tr);
		        rowIndex = writeTeam(sheet, tr, npc, rowIndex, gp);
		    }
		    
		    rowIndex++; // Blank row between locations
		}
		
		try (FileOutputStream fileOut = new FileOutputStream("./docs/TrainerInfo.xlsx")) {
		    wb.write(fileOut);
		    wb.close();
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	public static Map<?, ?>[] getTrainerLocationMap(GamePanel gp) {
		Entity[][] npc = gp.npc;
		
		Map<String, ArrayList<Trainer>> trainerMap = new LinkedHashMap<>();
		Map<Trainer, Entity> trainerNPCMap = new HashMap<>();
		for (int loc = 0; loc < npc.length; loc++) {
			for (int col = 0; col < npc[loc].length; col++) {
				Entity e = npc[loc][col];
				if (e == null || e.trainer < 0) continue;
				if (loc != 0 && e.trainer == 0) continue;
				PMap.getLoc(loc, e.worldX / gp.tileSize, e.worldY / gp.tileSize);
				String location = PlayerCharacter.currentMapName;
				Trainer tr = Trainer.getTrainer(e.trainer);
				if (trainerMap.containsKey(location)) {
					ArrayList<Trainer> list = trainerMap.get(location);
					list.add(tr);
				} else {
					ArrayList<Trainer> list = new ArrayList<>();
					list.add(tr);
					trainerMap.put(location, list);
				}
				trainerNPCMap.put(tr, e);
			}
		}
		
		return new Map<?, ?>[] {trainerMap, trainerNPCMap};
	}
	
	private static CellStyle makeStyle(Workbook wb, boolean bold, boolean italic, int fontSize, short color) {
		XSSFFont font = (XSSFFont) wb.createFont();
        font.setFontHeightInPoints((short) fontSize);
	    font.setBold(bold);
	    font.setItalic(italic);
	    font.setColor(color);

	    CellStyle style = wb.createCellStyle();
	    style.setFont(font);
	    return style;
	}
	
	private static void insertImage(Sheet sheet, byte[] imageBytes, int col, int row, int colSpan, int rowSpan, double scaleX, double scaleY) {
		Workbook wb = sheet.getWorkbook();
	    int pictureIdx = wb.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
	    CreationHelper helper = wb.getCreationHelper();
	    Drawing<?> drawing = sheet.createDrawingPatriarch();
	    ClientAnchor anchor = helper.createClientAnchor();
	    anchor.setCol1(col);
	    anchor.setRow1(row);
	    anchor.setCol2(col + colSpan);
	    anchor.setRow2(row + rowSpan);
	    
	    Picture pict = drawing.createPicture(anchor, pictureIdx);
	    pict.resize(scaleX, scaleY); // Scales it relative to the anchor box size
	}
	
	private static int writeTeam(Sheet sheet, Trainer tr, Entity npc, int startRow, GamePanel gp) {
	    int colStart = 0;
	    
	    XSSFFont headerFont = (XSSFFont) sheet.getWorkbook().createFont();
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setBold(true);
        
        XSSFCellStyle headerStyle = (XSSFCellStyle) sheet.getWorkbook().createCellStyle();
        headerStyle.setFont(headerFont);

	    // Trainer name row
	    Row nameRow = sheet.createRow(startRow++);
	    String trainerName = tr.getName();
	    String trainerCoordinates = String.format("X: %d, Y: %d", npc.worldX / gp.tileSize, npc.worldY / gp.tileSize);
	    String trainerDirection = "Facing: " + npc.direction;
	    if (npc.isSpin()) trainerDirection += "*";
	    Cell nameCell = nameRow.createCell(colStart);
	    Cell coordinatesCell = nameRow.createCell(3);
	    Cell directionCell = nameRow.createCell(5);
	    nameCell.setCellValue(trainerName);
	    nameCell.setCellStyle(makeStyle(sheet.getWorkbook(), true, false, 14, IndexedColors.BLACK.getIndex()));
	    coordinatesCell.setCellValue(trainerCoordinates);
	    coordinatesCell.setCellStyle(makeStyle(sheet.getWorkbook(), false, false, 12, IndexedColors.BLACK.getIndex()));
	    directionCell.setCellValue(trainerDirection);
	    directionCell.setCellStyle(makeStyle(sheet.getWorkbook(), false, false, 12, IndexedColors.BLACK.getIndex()));
	    
	    byte[] trainerBytes;
		try {
			BufferedImage down1 = npc.down1;
			if (down1 != null) {
				trainerBytes = imageToBytes(npc.down1, "png");
				if (trainerBytes != null) {
		            insertImage(sheet, trainerBytes, 7, nameRow.getRowNum(), 1, 1, 1, 1);
		        }
			} else {
				System.out.println(npc + "'s down1 is null");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 0, 2));
	    sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 3, 4));
	    sheet.addMergedRegion(new CellRangeAddress(nameRow.getRowNum(), nameRow.getRowNum(), 5, 6));

	    // Pokémon info in a horizontal block per team member
	    Row nameRow2 = sheet.createRow(startRow++);
	    Row typeRow = sheet.createRow(startRow++);
	    Row spriteRow = sheet.createRow(startRow++);
	    Row spriteRow2 = sheet.createRow(startRow++); // needed for image height
	    Row itemRow = sheet.createRow(startRow++);
	    Row abilityRow = sheet.createRow(startRow++);
	    Row natureRow = sheet.createRow(startRow++);
	    Row moveRow1 = sheet.createRow(startRow++);
	    Row moveRow2 = sheet.createRow(startRow++);
	    Row moveRow3 = sheet.createRow(startRow++);
	    Row moveRow4 = sheet.createRow(startRow++);

	    int col = 0;

	    for (Pokemon p : tr.getTeam()) {
	        // Name
	        Cell pNameCell = nameRow2.createCell(col);
	        pNameCell.setCellValue(p.name());
	        pNameCell.setCellStyle(makeStyle(sheet.getWorkbook(), true, false, 11, IndexedColors.BLACK.getIndex()));
	        
	        // Level
	        Cell pLevelCell = nameRow2.createCell(col + 1);
	        pLevelCell.setCellValue(p.getLevel());
	        pLevelCell.setCellStyle(makeStyle(sheet.getWorkbook(), true, false, 11, IndexedColors.BLACK.getIndex()));

	        // Sprite image
	        try {
	        	BufferedImage type1 = p.type1.getImage2();
	        	BufferedImage type2 = p.type2 != null ? p.type2.getImage2() : null;
	        	
	        	BufferedImage typeImage = combineIcons(type1, type2);
	        	byte[] typeBytes = imageToBytes(typeImage, "png");
	        	
	        	sheet.addMergedRegion(new CellRangeAddress(
	        			typeRow.getRowNum(), typeRow.getRowNum(), col, col + 1
		        ));
	        	
	        	typeRow.setHeight((short) 384);
	        	
	        	insertImage(sheet, typeBytes, col, typeRow.getRowNum(), 2, 1, 0.5, 1);
	        	
	        	// Set square size for sprite cells (adjust as needed)
	        	int spritePixelSize = 80; // Desired image dimension in pixels
	        	int charWidth = (int)(spritePixelSize * 256 / 7.001); // Excel column width
	        	
	        	spriteRow.setHeight((short) 804);
	        	spriteRow2.setHeight((short) 804);

	        	sheet.setColumnWidth(col, charWidth);

	        	// Merge 2x2 block for sprite
	        	sheet.addMergedRegion(new CellRangeAddress(
	        	    spriteRow.getRowNum(), spriteRow2.getRowNum(), col, col + 1
	        	));
	        	
		        byte[] spriteBytes = imageToBytes(getCachedSprite(p), "png");
		        if (spriteBytes != null) {
		            // Insert sprite as 2x2 image
		            insertImage(sheet, spriteBytes, col, spriteRow.getRowNum(), 2, 2, 1, 1);
		        }
	
		        // Item
		        int itemPixelSize = 24; // Desired image dimension in pixels
	        	charWidth = (int)(itemPixelSize * 256 / 7.001); // Excel column width
		        
		        Cell itemCell = itemRow.createCell(col);
		        itemCell.setCellValue(p.item == null ? "None" : p.item.toString());
		        itemCell.setCellStyle(makeStyle(sheet.getWorkbook(), true, true, 11, IndexedColors.BLACK.getIndex()));
		        if (p.item != null) {
		            byte[] itemImg = imageToBytes(p.item.getImage(), "png");
		            if (itemImg != null) {
		                sheet.setColumnWidth(col + 1, charWidth); // match width for consistent spacing
		                itemRow.setHeight((short) 372);             // match row height

		                insertImage(sheet, itemImg, col + 1, itemRow.getRowNum(), 1, 1, 1, 1);
		            }
		        }
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }

	        // Ability
	        Cell abilityCell = abilityRow.createCell(col);
	        abilityCell.setCellValue(p.ability.toString());
	        abilityCell.setCellStyle(makeStyle(sheet.getWorkbook(), true, false, 11, IndexedColors.BLACK.getIndex()));
	        sheet.addMergedRegion(new CellRangeAddress(abilityRow.getRowNum(), abilityRow.getRowNum(), col, col + 1));
	        
	        // Nature
	        natureRow.createCell(col).setCellValue(p.nat.toString() + " Nature");
	        sheet.addMergedRegion(new CellRangeAddress(natureRow.getRowNum(), natureRow.getRowNum(), col, col + 1));

	        // Moves
	        for (int i = 0; i < 4; i++) {
	            Move m = p.moveset[i] != null ? p.moveset[i].move : null;
	            if (m != null) {
	            	
	            }
	            String moveName = (m == null) ? "" :
	                (m == Move.HIDDEN_POWER || m == Move.RETURN) ? m == Move.HIDDEN_POWER ? "HP " + p.determineHPType() : m + " " + p.determineHPType() : m.toString();
	            PType mtype = m != null ? m.mtype : null;
	            if (m == Move.HIDDEN_POWER || m == Move.RETURN) mtype = p.determineHPType();
	            if (m != null && m.isAttack()) {
					if (mtype == PType.NORMAL) {
						if (p.ability == Ability.GALVANIZE) mtype = PType.ELECTRIC;
						if (p.ability == Ability.REFRIGERATE) mtype = PType.ICE;
						if (p.ability == Ability.PIXILATE) mtype = PType.LIGHT;
					}
					if (p.ability == Ability.NORMALIZE) mtype = PType.NORMAL;
				}

	            Row moveRow;
	            switch (i) {
	                case 0:
	                	moveRow = moveRow1;
	                	break;
	                case 1:
	                	moveRow = moveRow2;
	                	break;
	                case 2:
	                	moveRow = moveRow3;
	                	break;
	                default:
	                	moveRow = moveRow4;
	                	break;
	            };

	            Cell moveCell = moveRow.createCell(col);
	            moveCell.setCellValue(moveName);
	            sheet.addMergedRegion(new CellRangeAddress(moveRow.getRowNum(), moveRow.getRowNum(), col, col + 1));
	            if (m != null) {
	                java.awt.Color c = mtype.getColor();
	                CellStyle colored = sheet.getWorkbook().createCellStyle();
	                colored.setFillForegroundColor(new XSSFColor(new java.awt.Color(c.getRGB()), null));
	                colored.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	                Font font = sheet.getWorkbook().createFont();
	                font.setBold(true);
	                colored.setFont(font);
	                moveCell.setCellStyle(colored);
	            }
	        }

	        col += 2; // Leave one col buffer between mons
	    }

	    return startRow;
	}
	
	private static BufferedImage getCachedSprite(Pokemon p) {
	    if (sprites[p.id - 1] == null) {
	        sprites[p.id - 1] = p.setSprite();
	    }
	    return sprites[p.id - 1];
	}
	
	private static byte[] imageToBytes(BufferedImage image, String formatName) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, formatName, baos); // formatName can be "png", "jpg", etc.
	    baos.flush();
	    byte[] imageBytes = baos.toByteArray();
	    baos.close();
	    return imageBytes;
	}
	
	private static BufferedImage combineIcons(BufferedImage type1, BufferedImage type2) {
	    int width = 96;  // 2 x 24
	    int height = 48;
	    BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = combined.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(type1, 0, 0, null);
	    if (type2 != null) {
	        g.drawImage(type2, 48, 0, null);
	    }
	    g.dispose();
	    return combined;
	}
}
