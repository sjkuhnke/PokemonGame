package Overworld;

import Entity.Entity;
import Entity.NPC_Block;
import Entity.NPC_Clerk;
import Entity.NPC_GymLeader;
import Entity.NPC_Nurse;
import Entity.NPC_PC;
import Entity.NPC_Trainer;
import Obj.Cut_Tree;
import Obj.InteractiveTile;
import Obj.ItemObj;
import Obj.Rock_Smash;
import Obj.Tree_Stump;
import Swing.Item;

public class AssetSetter {

	GamePanel gp;
	int index;
	int objIndex;
	private int iIndex;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
		index = 0;
		objIndex = 0;
		iIndex = 0;
	}
	
	public void setObject() {
		int mapNum = 0;
		
		gp.obj[mapNum][objIndex] = ObjSetup(60, 45, 4, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(2, 54, 9, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 31, 1, mapNum);
		
		mapNum = 4;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(86, 56, 131, mapNum); // false swipe
		gp.obj[mapNum][objIndex] = ObjSetup(39, 59, 5, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(42, 74, 168, mapNum); // flash
		gp.obj[mapNum][objIndex] = ObjSetup(15, 57, 109, mapNum); // leaf blade
		gp.obj[mapNum][objIndex] = ObjSetup(9, 72, 0, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(9, 75, 2, mapNum);
		
		mapNum = 11;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(91, 55, 26, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(60, 66, 0, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(61, 62, 16, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 76, 2, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(57, 75, 5, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(53, 67, 12, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(55, 57, 1, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 68, 19, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(50, 78, 4, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(40, 82, 5, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(37, 81, 9, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(30, 87, 4, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(25, 90, 20, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(16, 81, 14, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(26, 69, 0, mapNum); // drain punch, taunt, 
		
		gp.obj[mapNum][objIndex] = ObjSetup(4, 43, 117, mapNum); // solar beam
		gp.obj[mapNum][objIndex] = ObjSetup(32, 60, 1, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 52, 10, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(34, 50, 5, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(33, 47, 11, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(15, 46, 0, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(32, 34, 16, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 38, 19, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(35, 44, 14, mapNum);
		
		mapNum = 22;
		objIndex = 0;
		gp.obj[mapNum][objIndex] = ObjSetup(60, 15, 128, mapNum); // flame charge
		gp.obj[mapNum][objIndex] = ObjSetup(94, 15, 19, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(72, 18, 11, mapNum);
		gp.obj[mapNum][objIndex] = ObjSetup(82, 14, 4, mapNum);
	}
	
	public void setNPC() {
		boolean[] flags = gp.player.p.flags;
		int mapNum = 0;
		
		if (flags[0]) {
			gp.npc[mapNum][index] = NPCSetup(4, 72, 48, 0);
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		gp.npc[mapNum][index] = NPCSetup(5, 18, 18, 1);
		
		gp.npc[mapNum][index] = NPCSetup(6, 23, 19, 2);
		
		gp.npc[mapNum][index] = NPCSetup(6, 23, 27, 3);
		
		gp.npc[mapNum][index] = NPCSetup(5, 21, 31, 4);
		
		
		// Nurses/PCs
		gp.npc[1][index] = NPCSetup(1, 31, 37, -1);
		gp.npc[1][index] = NPCSetup(0, 35, 36, -1);
		gp.npc[5][index] = NPCSetup(1, 31, 37, -1);
		gp.npc[5][index] = NPCSetup(0, 35, 36, -1);
		gp.npc[19][index] = NPCSetup(1, 31, 37, -1);
		gp.npc[19][index] = NPCSetup(0, 35, 36, -1);
		
		// Clerks
		gp.npc[2][index] = NPCSetup(2, 27, 39, -1);
		gp.npc[6][index] = NPCSetup(2, 27, 39, -1);
		gp.npc[20][index] = NPCSetup(2, 27, 39, -1);
		
		if (!flags[1]) {
			gp.npc[3][index] = NPCSetup(31, 40, "I saw a boy named Scott near New\nMinnow town saying he was looking\nfor a young man that looked like you.\nMaybe you should check it out?");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 4;
		
		if (!flags[2]) {
			gp.npc[mapNum][index] = NPCSetup(81, 61, "The gym is currently closed because the\nLeader is trying to help the Warehouse\nowner get rid of Team Nuke.\nCome back later.");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(4, 32, 62, 18);
		gp.npc[mapNum][index] = NPCSetup(4, 23, 65, 19); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(4, 32, 68, 20); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(4, 34, 76, 21); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(4, 45, 75, 22); // make way lower levels
		gp.npc[mapNum][index] = NPCSetup(4, 15, 70, 23);
		
		mapNum = 7;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(6, 30, 42, 5);
		gp.npc[mapNum][index] = NPCSetup(5, 33, 42, 6);
		gp.npc[mapNum][index] = NPCSetup(3, 36, 39, 7);
		gp.npc[mapNum][index] = NPCSetup(4, 36, 42, 8);
		
		mapNum = 8;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(5, 30, 39, 9);
		gp.npc[mapNum][index] = NPCSetup(4, 28, 41, 10);
		gp.npc[mapNum][index] = NPCSetup(3, 32, 39, 11);
		gp.npc[mapNum][index] = NPCSetup(4, 32, 45, 12);
		gp.npc[mapNum][index] = NPCSetup(8, 35, 41, 13);
		
		mapNum = 9;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(5, 34, 42, 14);
		gp.npc[mapNum][index] = NPCSetup(6, 27, 39, 15);
		gp.npc[mapNum][index] = NPCSetup(5, 42, 34, 16);
		gp.npc[mapNum][index] = NPCSetup(8, 38, 28, 17);
		
		mapNum = 11;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(4, 69, 65, 24);
		gp.npc[mapNum][index] = NPCSetup(4, 50, 72, 25);
		gp.npc[mapNum][index] = NPCSetup(4, 59, 74, 26);
		gp.npc[mapNum][index] = NPCSetup(4, 76, 68, 27);
		gp.npc[mapNum][index] = NPCSetup(3, 53, 59, 28);
		gp.npc[mapNum][index] = NPCSetup(4, 53, 65, 29);
		gp.npc[mapNum][index] = NPCSetup(4, 76, 59, 30);
		
		gp.npc[mapNum][index] = NPCSetup(4, 43, 73, 34);
		
		gp.npc[mapNum][index] = NPCSetup(5, 40, 78, 31);
		gp.npc[mapNum][index] = NPCSetup(5, 36, 79, 32);
		gp.npc[mapNum][index] = NPCSetup(4, 34, 89, 33);
		
		gp.npc[mapNum][index] = NPCSetup(7, 19, 61, -1);
		
		gp.npc[mapNum][index] = NPCSetup(6, 28, 55, 35);
		gp.npc[mapNum][index] = NPCSetup(6, 15, 49, 36);
		gp.npc[mapNum][index] = NPCSetup(5, 19, 49, 37);
		gp.npc[mapNum][index] = NPCSetup(6, 21, 45, 38);
		gp.npc[mapNum][index] = NPCSetup(6, 22, 33, 39);
		
		mapNum = 13;
		index = 0;
		if (!flags[4]) {
			gp.npc[mapNum][index] = NPCSetup(12, 56, "The gym leader is stuck in the\noffice upstairs trying to help\nScott with Team Nuke.\nGo find Scott and help!");
		} else {
			gp.npc[mapNum][index++] = null;
		}
		gp.npc[mapNum][index] = NPCSetup(5, 28, 34, 71);
		gp.npc[mapNum][index] = NPCSetup(5, 16, 37, 72);
		gp.npc[mapNum][index] = NPCSetup(6, 32, 24, 73);
		gp.npc[mapNum][index] = NPCSetup(6, 25, 18, 74);
		
		mapNum = 14;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(5, 33, 41, 44);
		gp.npc[mapNum][index] = NPCSetup(3, 26, 29, 40);
		gp.npc[mapNum][index] = NPCSetup(5, 39, 39, 41);
		gp.npc[mapNum][index] = NPCSetup(6, 22, 43, 42);
		gp.npc[mapNum][index] = NPCSetup(4, 26, 48, 43);
		
		mapNum = 16;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(6, 22, 29, 45);
		gp.npc[mapNum][index] = NPCSetup(6, 26, 29, 46);
		gp.npc[mapNum][index] = NPCSetup(5, 46, 29, 47);
		gp.npc[mapNum][index] = NPCSetup(6, 20, 39, 48);
		
		mapNum = 17;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(6, 50, 45, 49);
		gp.npc[mapNum][index] = NPCSetup(5, 56, 45, 50);
		gp.npc[mapNum][index] = NPCSetup(6, 50, 44, 51);
		gp.npc[mapNum][index] = NPCSetup(5, 56, 44, 52);
		gp.npc[mapNum][index] = NPCSetup(6, 50, 43, 53);
		gp.npc[mapNum][index] = NPCSetup(5, 56, 43, 54);
		
		if (!flags[3]) {
			gp.npc[mapNum][index] = NPCSetup(49, 53, "Quick! Team Nuke is taking over\nour office! Please help!");
			gp.npc[mapNum][index] = NPCSetup(57, 53, "Quick! Team Nuke is taking over\nour office! Please help!");
		} else {
			gp.npc[mapNum][index++] = null;
			gp.npc[mapNum][index++] = null;
		}
		
		mapNum = 18;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(4, 48, 45, 55);
		
		mapNum = 21;
		gp.npc[mapNum][index] = NPCSetup(3, 55, 60, 56);
		gp.npc[mapNum][index] = NPCSetup(4, 55, 62, 57);
		gp.npc[mapNum][index] = NPCSetup(3, 72, 52, 58);
		gp.npc[mapNum][index] = NPCSetup(4, 72, 54, 59);
		gp.npc[mapNum][index] = NPCSetup(6, 73, 57, 60);
		gp.npc[mapNum][index] = NPCSetup(5, 75, 57, 61);
		gp.npc[mapNum][index] = NPCSetup(3, 72, 46, 62);
		gp.npc[mapNum][index] = NPCSetup(4, 72, 48, 63);
		gp.npc[mapNum][index] = NPCSetup(3, 54, 46, 64);
		gp.npc[mapNum][index] = NPCSetup(4, 54, 48, 65);
		
		gp.npc[mapNum][index] = NPCSetup(8, 63, 39, 66);
		
		mapNum = 22;
		index = 0;
		gp.npc[mapNum][index] = NPCSetup(3, 80, 8, 67);
		gp.npc[mapNum][index] = NPCSetup(4, 78, 12, 68);
		gp.npc[mapNum][index] = NPCSetup(3, 72, 10, 69);
		gp.npc[mapNum][index] = NPCSetup(4, 69, 14, 70);
		
	}
	
	public void setInteractiveTile() {
		iIndex = 0;
		int mapNum = 4;
		gp.iTile[mapNum][iIndex] = ITileSetup(17, 63, 0);
		gp.iTile[mapNum][iIndex] = ITileSetup(16, 68, 0);
		gp.iTile[mapNum][iIndex] = ITileSetup(10, 68, 0);
		
		mapNum = 11;
		gp.iTile[mapNum][iIndex] = ITileSetup(39, 77, 0);
		gp.iTile[mapNum][iIndex] = ITileSetup(13, 52, 1);
		
		mapNum = 13;
		iIndex = 0;
		gp.iTile[mapNum][iIndex] = ITileSetup(23, 7, 1);
	}

	public void updateNPC() {
		boolean[] flags = gp.player.p.flags;
		// flags[0] is true after walking into first gate
		// flags[1] is true after beating Scott 1
		if (!flags[0] || flags[1]) gp.npc[0][0] = null;
		if (flags[0] && !flags[1]) gp.npc[0][0] = NPCSetup(4, 72, 48, 0);
		if (flags[1]) gp.npc[3][14] = null;
		if (flags[2]) gp.npc[4][15] = null;
		if (flags[3]) {
			gp.npc[17][6] = null;
			gp.npc[17][7] = null;
		}
		if (flags[4]) gp.npc[13][0] = null;
	}
	
	

	private Entity NPCSetup(int type, int x, int y, int team) {
		Entity result = null;
		switch (type) {
		case 0:
			result = new NPC_PC(gp);
			break;
		case 1:
			result = new NPC_Nurse(gp, "down");
			break;
		case 2:
			result = new NPC_Clerk(gp);
			break;
		case 3:
			result = new NPC_Trainer(gp, "down", team);
			break;
		case 4:
			result = new NPC_Trainer(gp, "up", team);
			break;
		case 5:
			result = new NPC_Trainer(gp, "left", team);
			break;
		case 6:
			result = new NPC_Trainer(gp, "right", team);
			break;
		case 7:
			result = new NPC_Nurse(gp, "up");
			break;
		case 8:
			result = new NPC_GymLeader(gp, "down", team);
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		index++;
		
		return result;
	}
	
	private Entity NPCSetup(int x, int y, String message) {
		Entity result = new NPC_Block(gp, message);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		index++;
		
		return result;
	}
	
	private ItemObj ObjSetup(int x, int y, int id, int mapNum) {
		if (gp.player.p.itemsCollected[mapNum][objIndex] == true) {
			objIndex++;
			return null;
		}
		
		ItemObj result = new ItemObj(gp);
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		result.item = new Item(id);
		
		objIndex++;
		
		return result;
		
		
	}
	
	private InteractiveTile ITileSetup(int x, int y, int type) {
		InteractiveTile result = null;
		switch (type) {
		case 0:
			result = new Cut_Tree(gp);
			break;
		case 1:
			result = new Rock_Smash(gp);
			break;
		case 2:
			result = new Tree_Stump(gp);
			break;
		case 3:
			break;
		}
		
		result.worldX = gp.tileSize*x;
		result.worldY = gp.tileSize*y;
		
		iIndex++;
		
		return result;
	}
}