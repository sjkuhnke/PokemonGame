package docs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import entity.Entity;
import entity.NPC_Market;
import entity.NPC_Star;
import entity.PlayerCharacter;
import object.ItemObj;
import object.TreasureChest;
import overworld.GamePanel;
import overworld.PMap;
import pokemon.Item;

public class ItemsDoc {

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
			Map<String, ArrayList<Entity>> shopsMap = new LinkedHashMap<>();
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
			for (int map = 0; map < gp.npc.length; map++) {
				for (int index = 0; index < gp.npc[map].length; index++) {
					
					Entity npc = gp.npc[map][index];
					
					if (npc == null) continue;
					
					if (!(npc instanceof NPC_Market) && !(npc instanceof NPC_Star)) {
						continue;
					}
					
					int x = npc.worldX / gp.tileSize;
					int y = npc.worldY / gp.tileSize;
					
					PMap.getLoc(map, x, y);
					String location = PlayerCharacter.currentMapName;
					
					shopsMap.computeIfAbsent(location, k -> new ArrayList<>()).add(npc);
			    }
			}
			
			Map<String, Boolean> locations = new LinkedHashMap<>();
			for (String location : itemsMap.keySet()) {
				locations.put(location, true);
			}
			
			for (String location : shopsMap.keySet()) {
				locations.put(location, true);
			}
			
			for (String loc : locations.keySet()) {
				String locationName = loc;
				while (locationName.length() < 50) {
					locationName += "-";
				}
				writer.write("\n\n" + locationName + "\n");
				
				// Shops
				ArrayList<Entity> shops = shopsMap.get(loc);
				
				if (shops != null) {
					for (Entity shop : shops) {
						writer.write("\n");
						
						int x = shop.worldX / gp.tileSize;
						int y = shop.worldY / gp.tileSize;
						
						boolean starShop = shop instanceof NPC_Star;
						
						String shopName = starShop ? "Star Shop" : "Market";
						
						writer.write(String.format("%s (%d, %d)", shopName, x, y));
						
						for (Item item : shop.inventory) {
							String label = "\n [" + item + "]";
							while (label.length() < 30) {
								label += " ";
							}
							int cost = item.getCost();
							String price;
							if (cost == 0) {
								price = "--";
							} else if (starShop) {
								price = cost + " Star Piece(s)";
							} else {
								price = "$" + cost;
							}
							
							label += " | " + price;
							writer.write(label);
						}
						writer.write("\n");
					}
				}
				
				// Existing overworld items
				ArrayList<ItemObj> list = itemsMap.get(loc);
				
				if (list != null) {
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

}