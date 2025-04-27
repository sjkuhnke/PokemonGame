package overworld;

import pokemon.*;

public class EventHandler {

	
	GamePanel gp;
	Player p;
	EventRect eventRect[][][];
	
	int previousEventX, previousEventY;
	boolean canTouchEvent = true;
	
	int tempMap, tempCol, tempRow;
	boolean tempCooldown;
	
	public EventHandler(GamePanel gp) {
		this.gp = gp;
		
		eventRect = new EventRect[GamePanel.MAX_MAP][gp.maxWorldCol][gp.maxWorldRow];
		
		int map = 0;
		int col = 0;
		int row = 0;
		while(map < GamePanel.MAX_MAP && col < gp.maxWorldCol && row < gp.maxWorldRow) {
			
			eventRect[map][col][row] = new EventRect();
			eventRect[map][col][row].x = gp.tileSize / 6;
			eventRect[map][col][row].y = gp.tileSize / 6;
			eventRect[map][col][row].width = gp.tileSize * 2 / 3;
			eventRect[map][col][row].height = gp.tileSize * 2 / 3;
			eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
			eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
			
			col++;
			if (col == gp.maxWorldCol) {
				col = 0;
				row++;
				
				if (row == gp.maxWorldRow) {
					row = 0;
					map++;
				}
			}
		}
	}
	
	public void checkEvent() {
		if (!canTouchEvent) {
			int xDistance = Math.abs(gp.player.worldX - previousEventX);
			int yDistance = Math.abs(gp.player.worldY - previousEventY);
			int distance = Math.max(xDistance, yDistance);
			if (distance >= gp.tileSize) {
				canTouchEvent = true;
			}
		}
		
		if (canTouchEvent) {
			// Bananaville Town PC
			if (hit(0,35,44)) teleport(1, 31, 45,false);
			if (hit(1,31,46)) teleport(0, 35, 45,false);
			
			// Bananaville Town Shop
			if (hit(0,27,53)) teleport(2, 31, 45,false);
			if (hit(2,31,46)) teleport(0, 27, 54,false);
			
			// Route 23 <-> Poppy Grove gate
			if (hit(0,20,10,false)) teleport(3, 31, 45,false);
			if (hit(3,31,46)) teleport(0, 20, 11,false);
			if (hit(3,31,33)) teleport(4, 74, 84,false);
			if (hit(4,74,85,false)) teleport(3, 31, 34,false);
			
			// Poppy Grove PC
			if (hit(4,69,71)) teleport(5, 31, 45,false);
			if (hit(5,31,46)) teleport(4, 69, 72,false);
			
			// Poppy Grove Shop
			if (hit(4,64,71)) teleport(6, 31, 45,false);
			if (hit(6,31,46)) teleport(4, 64, 72,false);
			
			// Poppy Grove Warehouse
			if (hit(4,66,79)) teleport(7, 31, 45,false);
			if (hit(7,31,46)) teleport(4, 66, 80,false);
			if (hit(7,40,33)) teleport(8, 28, 39,false);
			if (hit(8,27,39)) teleport(7, 39, 33,false);
			
			// Poppy Grove Post Office
			if (hit(4,81,79)) teleport(161, 50, 50,false);
			if (hit(161,50,51)) teleport(4, 81, 80,false);
			
			// Poppy Grove Gym
			if (hit(4,81,61)) teleport(9, 31, 45,false);
			if (hit(9,31,46)) teleport(4, 81, 62,false);
			
			// Route 24 pt. 1 gate
			if (hit(4,7,68,false)) teleport(10, 36, 40,false);
			if (hit(10,37,40)) teleport(4, 8, 68,false);
			if (hit(10,22,40)) teleport(11, 91, 49,false);
			if (hit(11,92,49,false)) teleport(10, 23, 40,false);
			
			// Cutscene with Fred 1
			if (!gp.player.p.flag[1][0] && hit(11,39,64)) gp.player.interactNPC(gp.npc[11][7], false);
			
			// Route 25 gate
			if (hit(11,24,17,false)) teleport(12, 31, 45,false);
			if (hit(12,31,46)) teleport(11, 24, 18,false);
			if (hit(12,31,33)) teleport(13, 28, 89,false);
			if (hit(13,28,90,false)) teleport(12, 31, 34,false);
			
			// Energy Plant A
			if (hit(13,19,85)) teleport(14, 34, 49,false);
			if (hit(14,34,50)) teleport(13, 19, 86,false);
			if (hit(14,21,37)) teleport(15, 50, 40,false);
			if (hit(15,51,40)) teleport(14, 22, 37,false);
			
			// Energy Plant B
			if (hit(15,29,9)) teleport(16, 32, 46,false);
			if (hit(16,31,46)) teleport(15, 28, 9,false);
			
			// Office
			if (hit(13,48,57)) teleport(17, 53, 56,false);
			if (hit(17,53,57)) teleport(13, 48, 58,false);
			
			if (hit(17,48,53)) teleport(18, 54, 51,false);
			if (hit(18,55,51)) teleport(17, 49, 53,false);
			if (hit(17,58,53)) teleport(18, 42, 51,false);
			if (hit(18,41,51)) teleport(17, 57, 53,false);
			
			// Sicab PC
			if (hit(13,38,85)) teleport(19, 31, 45,false);
			if (hit(19,31,46)) teleport(13, 38, 86,false);
			
			// Sicab Shop
			if (hit(13,12,85)) teleport(20, 31, 45,false);
			if (hit(20,31,46)) teleport(13, 12, 86,false);
			
			// Sicab Fishing House
			if (hit(13,44,75)) teleport(32, 31, 45,false);
			if (hit(32,31,46)) teleport(13, 44, 76,false);
			
			// Gym 2
			if (hit(13,15,56)) teleport(21, 63, 62,false);
			if (hit(21,63,63)) teleport(13, 15, 57,false);
			
			if (hit(21,54,61)) gp.iTile[21][0] = null;
			if (hit(21,74,58)) gp.iTile[21][1] = null;
			if (hit(21,75,53)) gp.iTile[21][2] = null;
			if (hit(21,73,47)) gp.iTile[21][3] = null;
			if (hit(21,53,47)) gp.iTile[21][4] = null;
			
			// Bannana Grove to Route 40 gate
			if (hit(0,17,78,false)) teleport(23, 31, 34,false);
			if (hit(23,31,33)) teleport(0, 17, 77,false);
			if (hit(23,31,46)) teleport(22, 71, 8,false);
			if (hit(22,71,7,false)) teleport(23, 31, 45,false);
			
			// Route 26 to Mt. Splinkty
			if (hit(13,27,5)) teleport(24, 56, 84,false);
			if (hit(24,56,85)) teleport(13, 27, 6,false);
			
			// Route 45 research facility
			if (hit(13,77,9)) teleport(162, 31, 45, false);
			if (hit(162,31,46)) teleport(13, 77, 10, false);
			
			// Route 24 pt 1 research facility
			if (hit(4,23,53)) teleport(178, 31, 45, false);
			if (hit(178,31,46)) teleport(4, 23, 54, false);
			
			// Sicab River to gate to Peaceful Park
			if (hit(13,86,74,false)) teleport(167, 23, 40,false);
			if (hit(167,22,40)) teleport(13, 85, 74,false);
			if (hit(167,37,40)) teleport(33, 10, 68,false);
			if (hit(33,9,68,false)) teleport(167, 36, 40,false);
			
			// Mt. Splinkty
			if (hit(24,73,63)) teleport(25, 73, 69,true);
			if (hit(25,73,69)) teleport(24, 73, 63,true);
			if (hit(24,46,80)) teleport(145, 44, 66,true);
			if (hit(145,44,66)) teleport(24, 46, 80,true);
			if (hit(24,77,77)) teleport(145, 66, 72,true);
			if (hit(145,66,72)) teleport(24, 77, 77,true);
			
			if (hit(25,74,62)) teleport(26, 58, 73,true);
			if (hit(26,58,73)) teleport(25, 74, 62,true);
			
			if (hit(25,76,81)) teleport(27, 74, 68,true);
			if (hit(27,74,68)) teleport(25, 76, 81,true);
			if (hit(27,76,70)) teleport(28, 8, 46,false);
			if (hit(28,8,45)) teleport(27, 76, 69,false);
			
			if (hit(145,39,73)) teleport(27, 39, 70,true);
			if (hit(27,39,70)) teleport(145, 39, 73,true);
			
			if (hit(25,52,88)) teleport(145, 43, 59,false);
			if (hit(145,43,58)) teleport(25, 52, 87,false);
			if (hit(25,68,86)) teleport(145, 62, 64,false);
			if (hit(145,62,63)) teleport(25, 68, 85,false);
			
			if (hit(24,39,43)) teleport(146, 45, 53,false);
			if (hit(146,45,52)) teleport(24, 39, 42,false);
			
			if (hit(25,45,63)) teleport(146, 65, 42,false);
			if (hit(146,65,41)) teleport(25, 45, 62,false);
			
			if (hit(27,39,74)) teleport(146, 70, 33,false);
			if (hit(146,70,32)) teleport(27, 39, 73,false);
			
			if (hit(24,52,74)) teleport(145, 59, 70,true);
			if (hit(145,59,70)) teleport(24, 52, 74,true);
			if (hit(24,51,75)) teleport(147, 60, 58,true);
			if (hit(147,60,58)) teleport(24, 51, 75,true);			
			
			// Mt. Splinkty Outside
			if (hit(146,77,38)) teleport(148, 50, 75,false);
			if (hit(148,50,76)) teleport(146, 77, 39,false);
			if (hit(146,82,26)) teleport(149, 49, 76,false);
			if (hit(149,49,77)) teleport(146, 82, 27,false);
			
			// Kleine Village PC
			if (hit(28,76,51)) teleport(29, 31, 45,false);
			if (hit(29,31,46)) teleport(28, 76, 52,false);
			
			// Kleine Market
			if (hit(28,75,44)) teleport(30, 31, 45,false);
			if (hit(30,31,46)) teleport(28, 75, 45,false);
			
			// Kleine Gym
			if (hit(28,87,44)) teleport(31, 51, 81,false);
			if (hit(31,51,82)) teleport(28, 87, 45,false);
			
			// Kleine Village Gate
			if (hit(28,93,46,false)) teleport(34, 23, 40,false);
			if (hit(34,22,40)) teleport(28, 92, 46,false);
			if (hit(34,37,40)) teleport(33, 8, 13,false);
			if (hit(33,7,13,false)) teleport(34, 36, 40,false);
			
			// Electric Tunnel
			if (hit(33,62,16)) teleport(35, 11, 72,false);
			if (hit(35,11,73)) teleport(33, 62, 17,false);
			
			if (hit(35,90,69)) teleport(36, 15, 46,false);
			if (hit(36,15,45)) teleport(35, 90, 68,false);
			
			// Schrice City Gate
			if (hit(36,93,41,false)) teleport(37, 23, 40,false);
			if (hit(37,22,40)) teleport(36, 92, 41,false);
			if (hit(37,37,40)) teleport(38, 7, 47,false);
			if (hit(38,6,47,false)) teleport(37, 36, 40,false);
			
			// Schrice PC
			if (hit(38,49,54)) teleport(39, 31, 45,false);
			if (hit(39,31,46)) teleport(38, 49, 55,false);
						
			// Schrice Supermarket
			if (hit(38,18,58)) teleport(45, 31, 45,false);
			if (hit(45,31,46)) teleport(38, 18, 59,false);
			if (hit(45,36,38)) teleport(40, 27, 38,false);
			if (hit(40,26,38)) teleport(45, 35, 38,false);
			
			// Schrice School
			if (hit(38,28,55)) teleport(41, 31, 46,false);
			if (hit(41,31,47)) teleport(38, 28, 56,false);
			
			if (hit(41,18,30)) teleport(163, 32, 46,false);
			if (hit(163,32,47)) teleport(41, 18, 31,false);
			if (hit(41,44,30)) teleport(164, 32, 46,false);
			if (hit(164,32,47)) teleport(41, 44, 31,false);
			if (hit(41,31,30)) teleport(165, 32, 46,false);
			if (hit(165,32,47)) teleport(41, 31, 31,false);
			
			// Radio Tower
			if (hit(38,62,57)) teleport(42, 31, 45,false);
			if (hit(42,31,46)) teleport(38, 62, 58,false);
			
			if (hit(42,31,33)) teleport(43, 31, 45,false);
			if (hit(43,31,46)) teleport(42, 31, 34,false);
			
			// Schrice Gym
			if (hit(38,62,41)) teleport(44, 63, 67,false);
			if (hit(44,63,68)) teleport(38, 62, 42,false);
			
			// Cutscene with Robin
			if (gp.player.p.flag[3][12] && !gp.player.p.flag[4][0] && hit(38,62,42)) gp.player.interactNPC(gp.npc[38][1], true);
			
			// Icy Path
			if (hit(38,42,7)) teleport(166, 51, 53,true);
			if (hit(166,51,53)) teleport(38, 42, 7,true);
			
			// Poppy Grove HP House
			if (hit(4,80,71)) teleport(46, 31, 45,false);
			if (hit(46,31,46)) teleport(4, 80, 72,false);
			
			// Poppy Grove Hotel
			if (hit(4,64,55)) teleport(48, 31, 45,false);
			if (hit(48,31,46)) teleport(4, 64, 56,false);
			
			// Bananaville Town Barn
			if (hit(0,12,55)) teleport(47, 31, 45,false);
			if (hit(47,31,46)) teleport(0, 12, 56,false);
			
			// Kleine Village House
			if (hit(28,68,50)) teleport(49, 31, 45,false);
			if (hit(49,31,46)) teleport(28, 68, 51,false);
			
			// Schrice City House
			if (hit(38,23,36)) teleport(50, 31, 45,false);
			if (hit(50,31,46)) teleport(38, 23, 37,false);
			
			// New Minnow Town Houses
			if (hit(0,89,45)) teleport(51, 31, 45,false);
			if (hit(51,31,46)) teleport(0, 89, 46,false);
			
			if (hit(0,79,45)) teleport(52, 31, 45,false);
			if (hit(52,31,46)) teleport(0, 79, 46,false);
			
			if (hit(0,84,54)) teleport(54, 31, 45,false);
			if (hit(54,31,46)) teleport(0, 84, 55,false);
			
			// Bananaville Town Houses
			if (hit(0,27,44)) teleport(55, 31, 45,false);
			if (hit(55,31,46)) teleport(0, 27, 45,false);
			
			if (hit(0,33,52)) teleport(56, 31, 45,false);
			if (hit(56,31,46)) teleport(0, 33, 53,false);
			
			// Poppy Grove Houses
			if (hit(4,68,62)) teleport(57, 31, 45,false);
			if (hit(57,31,46)) teleport(4, 68, 63,false);
			
			if (hit(4,63,62)) teleport(58, 31, 45,false);
			if (hit(58,31,46)) teleport(4, 63, 63,false);
			
			// Sicab City Houses
			if (hit(13,49,85)) teleport(59, 31, 45,false);
			if (hit(59,31,46)) teleport(13, 49, 86,false);
			
			if (hit(13,26,64)) teleport(60, 31, 45,false);
			if (hit(60,31,46)) teleport(13, 26, 65,false);
			
			// Kleine Village House
			if (hit(28,68,44)) teleport(61, 31, 45,false);
			if (hit(61,31,46)) teleport(28, 68, 45,false);
			
			// Schrice City Houses
			if (hit(38,48,44)) teleport(62, 31, 45,false);
			if (hit(62,31,46)) teleport(38, 48, 45,false);
			
			if (hit(38,43,44)) teleport(63, 31, 45,false);
			if (hit(63,31,46)) teleport(38, 43, 45,false);
			
			if (hit(38,38,44)) teleport(64, 31, 45,false);
			if (hit(64,31,46)) teleport(38, 38, 45,false);
			
			if (hit(38,28,44)) teleport(65, 31, 45,false);
			if (hit(65,31,46)) teleport(38, 28, 45,false);
			
			if (hit(38,23,44)) teleport(66, 31, 45,false);
			if (hit(66,31,46)) teleport(38, 23, 45,false);
			
			if (hit(38,18,44)) teleport(67, 31, 45,false);
			if (hit(67,31,46)) teleport(38, 18, 45,false);
			
			if (hit(38,48,36)) teleport(68, 31, 45,false);
			if (hit(68,31,46)) teleport(38, 48, 37,false);
			
			if (hit(38,43,36)) teleport(69, 31, 45,false);
			if (hit(69,31,46)) teleport(38, 43, 37,false);
			
			if (hit(38,38,36)) teleport(70, 31, 45,false);
			if (hit(70,31,46)) teleport(38, 38, 37,false);
			
			if (hit(38,33,36)) teleport(71, 31, 45,false);
			if (hit(71,31,46)) teleport(38, 33, 37,false);
			
			if (hit(38,28,36)) teleport(72, 31, 45,false);
			if (hit(72,31,46)) teleport(38, 28, 37,false);
			
			if (hit(38,18,36)) teleport(73, 31, 45,false);
			if (hit(73,31,46)) teleport(38, 18, 37,false);
			
			// Route 41/Peaceful park gate
			if (hit(28,81,89,false)) teleport(74, 31, 34,false);
			if (hit(74,31,33)) teleport(28, 81, 88,false);
			if (hit(74,31,46)) teleport(33, 33, 34,false);
			if (hit(33,33,33,false)) teleport(74, 31, 45,false);
			
			// Route 36/Peaceful park gate
			if (hit(4,17,11,false)) teleport(75, 36, 40,false);
			if (hit(75,37,40)) teleport(4, 18, 11,false);
			if (hit(75,22,40)) teleport(33, 55, 84,false);
			if (hit(33,56,84,false)) teleport(75, 23, 40,false);
			
			// Route 30/Mindagan Lake gate
			if (hit(38,42,90,false)) teleport(76, 31, 34,false);
			if (hit(76,31,33)) teleport(38, 42, 89,false);
			if (hit(76,31,46)) teleport(77, 48, 8,false);
			if (hit(77,48,7,false)) teleport(76, 31, 45,false);
			
			// Mindagan Cavern
			if (hit(77,83,32)) teleport(78, 58, 74,false);
			if (hit(78,58,75)) teleport(77, 83, 33,false);
			
			// Route 31/Mindagan Lake gate
			if (hit(77,56,74,false)) teleport(79, 31, 34,false);
			if (hit(79,31,33)) teleport(77, 56, 73,false);
			if (hit(79,31,46)) teleport(80, 32, 8,false);
			if (hit(80,32,7,false)) teleport(79, 31, 45,false);
			
			// Route 42/Shadow Ravine gate
			if (hit(80,47,85,false)) teleport(81, 31, 34,false);
			if (hit(81,31,33)) teleport(80, 47, 84,false);
			if (hit(81,31,46)) teleport(0, 85, 6,false);
			if (hit(0,85,5,false)) teleport(81, 31, 45,false);
			
			// Shadow Ravine 0
			if (hit(80,41,43)) teleport(90, 51, 9,true);
			if (hit(90,51,9)) teleport(80, 41, 43,true);
			
			// Shadow Ravine/Shadow Ravine 1A gate
			if (hit(83,6,70,false)) teleport(82, 36, 40,false);
			if (hit(82,37,40)) teleport(83, 7, 70,false);
			if (hit(82,22,40)) teleport(80, 92, 51,false);
			if (hit(80,93,51,false)) teleport(82, 23, 40,false);
			
			// Route 32/Frenco City gate
			if (hit(85,6,67,false)) teleport(84, 36, 40,false);
			if (hit(84,37,40)) teleport(85, 7, 67,false);
			if (hit(84,22,40)) teleport(83, 92, 51,false);
			if (hit(83,93,51,false)) teleport(84, 23, 40,false);
			
			// Frenco City PC
			if (hit(85,43,75)) teleport(86, 31, 45,false);
			if (hit(86,31,46)) teleport(85, 43, 76,false);
			
			// Frenco City Shop
			if (hit(85,53,75)) teleport(87, 31, 45,false);
			if (hit(87,31,46)) teleport(85, 53, 76,false);
			
			// Frenco City Gym
			if (hit(85,58,64)) teleport(88, 63, 67,false);
			if (hit(88,63,68)) teleport(85, 58, 65,false);
			
			if (hit(88,63,64)) teleport(88, 75, 63,true); // A
			if (hit(88,75,63)) teleport(88, 63, 64,true); // A
			if (hit(88,71,67)) teleport(88, 81, 53,true); // B
			if (hit(88,81,53)) teleport(88, 71, 67,true); // B
			if (hit(88,75,67)) teleport(88, 51, 63,true); // C
			if (hit(88,51,63)) teleport(88, 75, 67,true); // C
			if (hit(88,75,57)) teleport(88, 51, 47,true); // D
			if (hit(88,51,47)) teleport(88, 75, 57,true); // D
			if (hit(88,71,57)) teleport(88, 81, 63,true); // E
			if (hit(88,81,63)) teleport(88, 71, 57,true); // E
			if (hit(88,81,67)) teleport(88, 55, 43,true); // F
			if (hit(88,55,43)) teleport(88, 81, 67,true); // F
			if (hit(88,85,67)) teleport(88, 41, 67,true); // G
			if (hit(88,41,67)) teleport(88, 85, 67,true); // G
			if (hit(88,71,53)) teleport(88, 55, 53,true); // H
			if (hit(88,55,53)) teleport(88, 71, 53,true); // H
			if (hit(88,75,53)) teleport(88, 85, 53,true); // I
			if (hit(88,85,53)) teleport(88, 75, 53,true); // I
			if (hit(88,85,57)) teleport(88, 51, 43,true); // J
			if (hit(88,51,43)) teleport(88, 85, 57,true); // J
			if (hit(88,41,53)) teleport(88, 55, 57,true); // K
			if (hit(88,55,57)) teleport(88, 41, 53,true); // K
			if (hit(88,75,43)) teleport(88, 41, 63,true); // L
			if (hit(88,41,63)) teleport(88, 75, 43,true); // L
			if (hit(88,71,47)) teleport(88, 81, 43,true); // M
			if (hit(88,81,43)) teleport(88, 71, 47,true); // M
			if (hit(88,75,47)) teleport(88, 45, 53,true); // N
			if (hit(88,45,53)) teleport(88, 75, 47,true); // N
			if (hit(88,45,57)) teleport(88, 81, 57,true); // O
			if (hit(88,81,57)) teleport(88, 45, 57,true); // O
			if (hit(88,81,47)) teleport(88, 41, 57,true); // P
			if (hit(88,41,57)) teleport(88, 81, 47,true); // P
			if (hit(88,41,47)) teleport(88, 85, 47,true); // Q
			if (hit(88,85,47)) teleport(88, 41, 47,true); // Q
			if (hit(88,41,43)) teleport(88, 63, 39,true); // R
			if (hit(88,63,39)) teleport(88, 41, 43,true); // R
			if (hit(88,45,43)) teleport(88, 55, 67,true); // S
			if (hit(88,55,67)) teleport(88, 45, 43,true); // S
			if (hit(88,45,67)) teleport(88, 51, 57,true); // T
			if (hit(88,51,57)) teleport(88, 45, 67,true); // T
			if (hit(88,51,67)) teleport(88, 45, 63,true); // U
			if (hit(88,45,63)) teleport(88, 51, 67,true); // U
			if (hit(88,67,37)) teleport(88, 61, 65,true); // V
			
			// Frenco City Pawn Shop
			if (hit(85,33,75)) teleport(89, 31, 45,false);
			if (hit(89,31,46)) teleport(85, 33, 76,false);
			
			// Grandpa House
			if (hit(85,24,80)) teleport(91, 31, 45,false);
			if (hit(91,31,46)) teleport(85, 24, 81,false);
			
			// Peaceful park house
			if (hit(33,32,89)) teleport(92, 31, 45,false);
			if (hit(92,31,46)) teleport(33, 32, 90,false);
			
			// Move Reminder House
			if (hit(85,23,72)) teleport(93, 31, 45,false);
			if (hit(93,31,46)) teleport(85, 23, 73,false);
			
			// Gift E/S House
			if (hit(85,23,64)) teleport(94, 31, 45,false);
			if (hit(94,31,46)) teleport(85, 23, 65,false);
			
			// Frenco City Houses
			if (hit(85,23,76)) teleport(169, 31, 45,false);
			if (hit(169,31,46)) teleport(85, 23, 77,false);
			
			if (hit(85,18,64)) teleport(170, 31, 45,false);
			if (hit(170,31,46)) teleport(85, 18, 65,false);
			
			if (hit(85,13,64)) teleport(171, 31, 45,false);
			if (hit(171,31,46)) teleport(85, 13, 65,false);
			
			if (hit(85,23,60)) teleport(172, 31, 45,false);
			if (hit(172,31,46)) teleport(85, 23, 61,false);
			
			if (hit(85,18,60)) teleport(173, 31, 45,false);
			if (hit(173,31,46)) teleport(85, 18, 61,false);
			
			if (hit(85,13,60)) teleport(174, 31, 45,false);
			if (hit(174,31,46)) teleport(85, 13, 61,false);
			
			if (hit(85,23,56)) teleport(175, 31, 45,false);
			if (hit(175,31,46)) teleport(85, 23, 57,false);
			
			if (hit(85,18,56)) teleport(176, 31, 45,false);
			if (hit(176,31,46)) teleport(85, 18, 57,false);
			
			if (hit(85,13,56)) teleport(177, 31, 45,false);
			if (hit(177,31,46)) teleport(85, 13, 57,false);
			
			// Electric Tunnel 0 -> 1
			if (hit(95,52,46)) teleport(35, 65, 42,true);
			if (hit(35,65,42)) teleport(95, 52, 46,true);
			
			// Electric Tunnel -1 -> 0
			if (hit(96,40,56)) teleport(95, 41, 62,true);
			if (hit(95,41,62)) teleport(96, 40, 56,true);
			
			// Electric Tunnel -2 -> -1
			if (hit(97,38,58)) teleport(96, 38, 62,true);
			if (hit(96,38,62)) teleport(97, 38, 58,true);
			if (hit(97,56,57)) teleport(96, 58, 60,true);
			if (hit(96,58,60)) teleport(97, 56, 57,true);
			
			// Electric Tunnel -3 -> -2
			if (hit(98,47,45)) teleport(97, 46, 32,true);
			if (hit(97,46,32)) teleport(98, 47, 45,true);
			
			// Electric Tunnel -1 -> H
			if (hit(96,47,42)) teleport(99, 50, 63,false);
			if (hit(99,50,64)) teleport(96, 47, 43,false);
			
			// Shadow Ravine 0 -> H
			if (hit(90,52,18)) teleport(100, 50, 62,true);
			if (hit(100,50,62)) teleport(90, 52, 18,true);
			
			// Electric Tunnel -3 -> Shadow Path
			if (hit(98,53,63)) teleport(105, 29, 8,true);
			if (hit(105,29,7)) teleport(98, 53, 62,true);
			
			// Shadow Path -> TN Base
			if (hit(105,45,91)) teleport(104, 47, 43,false);
			if (hit(104,47,42)) teleport(105, 45, 90,false);
			
			// TN Base -> Shadow Ravine -3
			if (hit(104,47,63)) teleport(103, 47, 35,false);
			if (hit(103,47,34)) teleport(104, 47, 62,true); // was intentionally 1 below
			
			// Shadow Ravine 0 -> -1
			if (hit(90,25,10)) teleport(101, 24, 45,true);
			if (hit(101,24,45)) teleport(90, 25, 10,true);
			if (hit(90,55,37)) teleport(101, 57, 70,true);
			if (hit(101,57,70)) teleport(90, 55, 37,true);
			
			// Shadow Ravine -1 -> -2
			if (hit(101,48,30)) teleport(102, 53, 39,true);
			if (hit(102,53,39)) teleport(101, 48, 30,true);
			
			// Shadow Ravine -2 -> -3
			if (hit(102,59,53)) teleport(103, 57, 60,true);
			if (hit(103,57,60)) teleport(102, 59, 53,true);
			
			// Route 33 -> Ghostly Woods Gate -> Ghostly Woods
			if (hit(85,86,22,false)) teleport(106, 31, 45,false);
			if (hit(106,31,46)) teleport(85, 86, 23,false);
			if (hit(106,31,33)) teleport(107, 49, 92,false);
			if (hit(107,49,93,false)) teleport(106, 31, 34,false);
			
			// Arthra Cutscene
			if (gp.player.p.flag[4][5] && !gp.player.p.flag[5][0] && hit(107,49,92)) gp.player.interactNPC(gp.npc[107][12], false);
			
			// Ghostly Woods
			if (hit(107,57,91)) teleport(107, 26, 81,true); // A
			if (hit(107,26,81)) teleport(107, 57, 91,true); // A
			if (hit(107,39,86)) teleport(107, 84, 59,true); // B
			if (hit(107,84,59)) teleport(107, 39, 86,true); // B
			if (hit(107,81,55)) teleport(107, 36, 90,true); // C
			if (hit(107,36,90)) teleport(107, 81, 55,true); // C
			if (hit(107,17,75)) teleport(107, 57, 78,true); // D
			if (hit(107,57,78)) teleport(107, 17, 75,true); // D
			if (hit(107,55,78)) teleport(107, 72, 50,true); // E
			if (hit(107,72,50)) teleport(107, 55, 78,true); // E
			if (hit(107,66,50)) teleport(107, 53, 63,true); // F
			if (hit(107,53,63)) teleport(107, 66, 50,true); // F
			if (hit(107,47,63)) {
				if (!gp.player.p.flag[5][2] || !gp.player.p.flag[5][5]) {
					if (gp.player.p.grustCount < 10) {
						gp.setTaskState();
						Task t = Task.addTask(Task.TELEPORT, "");
						t.counter = 107;
						t.start = 42;
						t.finish = 57;
						t.wipe = false;
						Task.addTask(Task.TEXT, "This portal seems to be jammed by the Ghosts everywhere...");
						Task.addTask(Task.TEXT, "There are " + (10 - gp.player.p.grustCount) + " Ghosts remaining!");
					} else if (!gp.player.p.flag[5][2]) {
						gp.player.interactNPC(gp.npc[107][0], true);
					} else {
						gp.setTaskState();
						Task t = Task.addTask(Task.TELEPORT, "");
						t.counter = 107;
						t.start = 42;
						t.finish = 57;
						t.wipe = false;
						Task.addTask(Task.TEXT, "This portal seems to be jammed by Team Eclipse...");
						Task.addTask(Task.TEXT, "Rick said that they're at the bottom of Electric Tunnel!");
					}
				} else {
					teleport(107, 24, 48,true); // G
				}
			}
			if (hit(107,24,48)) teleport(107, 47, 63,true); // G
			if (hit(107,43,73)) teleport(107, 66, 54,true); // H
			if (hit(107,66,54)) teleport(107, 43, 73,true); // H
			if (hit(107,36,48)) teleport(107, 64, 19,true); // I
			if (hit(107,64,19)) teleport(107, 36, 48,true); // I
			if (hit(107,60,17)) teleport(107, 38, 90,true); // J
			if (hit(107,38,90)) teleport(107, 60, 17,true); // J
			if (hit(107,54,20)) teleport(107, 41, 48,true); // K
			if (hit(107,41,48)) teleport(107, 54, 20,true); // K
			if (hit(107,47,44)) teleport(107, 20, 34,true); // L
			if (hit(107,20,34)) teleport(107, 47, 44,true); // L
			if (hit(107,47,47)) teleport(107, 79, 63,true); // M
			if (hit(107,79,63)) teleport(107, 47, 47,true); // M
			if (hit(107,47,50)) teleport(107, 45, 41,true); // N
			if (hit(107,45,41)) teleport(107, 47, 50,true); // N
			if (hit(107,46,53)) teleport(107, 32, 28,true); // O
			if (hit(107,32,28)) teleport(107, 46, 53,true); // O
			if (hit(107,45,30)) teleport(107, 18, 39,true); // P
			if (hit(107,18,39)) teleport(107, 45, 30,true); // P
			if (hit(107,45,37)) teleport(107, 69, 31,true); // Q
			if (hit(107,69,31)) teleport(107, 45, 37,true); // Q
			if (hit(107,54,48)) teleport(107, 50, 20,true); // R
			if (hit(107,50,20)) teleport(107, 54, 48,true); // R
			if (hit(107,49,30)) teleport(107, 50, 64,true); // S
			if (hit(107,50,64)) teleport(107, 49, 30,true); // S
			if (hit(107,49,37)) teleport(107, 69, 83,true); // T
			if (hit(107,69,83)) teleport(107, 49, 37,true); // T
			if (hit(107,56,81)) teleport(107, 55, 40,true); // U
			if (hit(107,55,40)) teleport(107, 56, 81,true); // U
			if (hit(107,56,83)) teleport(107, 50, 51,true); // V
			if (hit(107,50,51)) teleport(107, 56, 83,true); // V
			if (hit(107,52,59)) teleport(107, 76, 26,true); // W
			if (hit(107,76,26)) teleport(107, 52, 59,true); // W
			if (hit(107,74,22)) teleport(107, 49, 83,true); // X'
			
			// Maxwell 1 Cutscene
			if (gp.player.p.flag[5][3] && !gp.player.p.flag[5][4] && hit(103,47,35)) gp.player.interactNPC(gp.npc[103][0], false);
			
			// Ghostly Woods -> Route 34 Gate -> Route 34
			if (hit(107,45,18,false)) teleport(108, 31, 45,false);
			if (hit(108,31,46)) teleport(107, 45, 19,false);
			if (hit(108,31,33)) teleport(109, 73, 92,false);
			if (hit(109,73,93,false)) teleport(108, 31, 34,false);
			
			// Route 34
			if (hit(109,87,84)) teleport(110, 42, 87,false); // bypass
			if (hit(110,42,88)) teleport(109, 87, 85,false); // bypass
			if (hit(109,71,72)) teleport(110, 34, 78,true);
			if (hit(110,34,78)) teleport(109, 71, 72,true);
			if (hit(109,55,44)) teleport(110, 26, 52,true); // upper exit
			if (hit(110,26,52)) teleport(109, 55, 44,true); // upper exit
			if (hit(109,55,36)) teleport(110, 28, 46,true); // bypass
			if (hit(110,28,46)) teleport(109, 55, 36,true); // bypass
			if (hit(109,44,47)) teleport(110, 16, 50,false); // lower exit
			if (hit(110,16,51)) teleport(109, 44, 48,false); // lower exit
			if (hit(109,60,48)) teleport(110, 34, 58,true);
			if (hit(110,34,58)) teleport(109, 60, 48,true);
			if (hit(109,70,84)) teleport(110, 17, 85,true);
			if (hit(110,17,85)) teleport(109, 70, 84,true);
			
			// Glurg Town PC
			if (hit(109,34,44)) teleport(111, 31, 45,false);
			if (hit(111,31,46)) teleport(109, 34, 45,false);
			
			// Glurg Town Shop
			if (hit(109,32,34)) teleport(112, 31, 45,false);
			if (hit(112,31,46)) teleport(109, 32, 35,false);
			
			// Glurg Town Houses
			if (hit(109,38,35)) teleport(187, 31, 45,false);
			if (hit(187,31,46)) teleport(109, 38, 36,false);
			if (hit(109,18,34)) teleport(188, 31, 45,false);
			if (hit(188,31,46)) teleport(109, 18, 35,false);
			
			// Route 35
			if (hit(109,6,36,false)) teleport(114, 36, 40,false);
			if (hit(114,37,40)) teleport(109, 7, 36,false);
			if (hit(114,22,40)) teleport(115, 91, 77,false);
			if (hit(115,92,77,false)) teleport(114, 23, 40,false);
			
			// Schrice City/Route 35 Gate
			if (hit(115,19,78,false)) teleport(116, 36, 40,false);
			if (hit(116,37,40)) teleport(115, 20, 78,false);
			if (hit(116,22,40)) teleport(38, 78, 47,false);
			if (hit(38,79,47,false)) teleport(116, 23, 40,false);
			
			// Gym 6
			if (hit(109,15,42)) teleport(113, 53, 68,false);
			if (hit(113,53,69)) teleport(109, 15, 43,false);
			
			// Cutscene with Scott
			if (gp.player.p.flag[5][8] && !gp.player.p.flag[6][0] && hit(109,15,43)) gp.player.interactNPC(gp.npc[109][5], false);
			
			// Mindagan Cavern
			if (hit(78,62,69)) teleport(117, 61, 72,true);
			if (hit(117,61,72)) teleport(78, 62, 69,true);
			
			// Glurg Town Fossil Center
			if (hit(109,12,34)) teleport(118, 31, 45,false);
			if (hit(118,31,46)) teleport(109, 12, 35,false);
			
			// New Minnow Town -> Route 43 Gate
			if (hit(0,92,49,false)) teleport(120, 23, 40,false);
			if (hit(120,22,40)) teleport(0, 91, 49,false);
			if (hit(120,37,40)) teleport(119, 8, 49,false);
			if (hit(119,7,49,false)) teleport(120, 36, 40,false);
			
			// Berry Shoppe House
			if (hit(85,32,63)) teleport(53, 31, 45,false);
			if (hit(53,31,46)) teleport(85, 32, 64,false);
			
			// Sicab City Tower
			if (hit(13,53,75)) teleport(121, 31, 45,false);
			if (hit(121,31,46)) teleport(13, 53, 76,false);
			
			if (hit(121,31,33)) teleport(122, 31, 45,false);
			if (hit(122,31,46)) teleport(121, 31, 34,false);
			
			// Route 37 Gate
			if (hit(11,20,93,false)) teleport(123, 31, 34,false);
			if (hit(123,31,33)) teleport(11, 20, 92,false);
			if (hit(123,31,46)) teleport(124, 22, 9,false);
			if (hit(124,22,8,false)) teleport(123, 31, 45,false);
			
			// Rawwar City PC
			if (hit(124,21,56)) teleport(125, 31, 45,false);
			if (hit(125,31,46)) teleport(124, 21, 57,false);
			
			// Rawwar City Shop
			if (hit(124,29,56)) teleport(126, 31, 45,false);
			if (hit(126,31,46)) teleport(124, 29, 57,false);
			
			// Rawwar City Casino
			if (hit(124,27,69)) teleport(127, 31, 45,false);
			if (hit(127,31,46)) teleport(124, 27, 70,false);
			
			// Rawwar City Prize Shop
			if (hit(124,37,62)) teleport(129, 31, 45,false);
			if (hit(129,31,46)) teleport(124, 37, 63,false);
			
			// Rawwar City Gym
			if (hit(124,25,82)) teleport(128, 53, 68,false);
			if (hit(128,53,69)) teleport(124, 25, 83,false);
			
			// Magmaclang House
			if (hit(124,36,78)) teleport(130, 31, 45,false);
			if (hit(130,31,46)) teleport(124, 36, 79,false);
			
			// Geology Shop
			if (hit(124,10,59)) teleport(131, 31, 45,false);
			if (hit(131,31,46)) teleport(124, 10, 60,false);
			
			// Supermarket
			if (hit(124,16,73)) teleport(132, 31, 45,false);
			if (hit(132,31,46)) teleport(124, 16, 74,false);
			if (hit(132,36,38)) teleport(133, 27, 38,false);
			if (hit(133,26,38)) teleport(132, 35, 38,false);
			
			// Geology Shop
			if (hit(131,37,38)) teleport(134, 12, 30,false);
			if (hit(134,11,30)) teleport(131, 36, 38,false);
			if (hit(134,85,40)) teleport(135, 26, 44,false);
			if (hit(135,25,44)) teleport(134, 84, 40,false);
			
			if (hit(135,31,33)) teleport(136, 31, 45,false);
			if (hit(136,31,46)) teleport(135, 31, 34,false);
			
			// Rawwar Houses
			if (hit(124,11,51)) teleport(181, 31, 45,false);
			if (hit(181,31,46)) teleport(124, 11, 52,false);
			if (hit(124,13,63)) teleport(182, 31, 45,false);
			if (hit(182,31,46)) teleport(124, 13, 64,false);
			if (hit(124,18,63)) teleport(183, 31, 45,false);
			if (hit(183,31,46)) teleport(124, 18, 64,false);
			if (hit(124,23,63)) teleport(184, 31, 45,false);
			if (hit(184,31,46)) teleport(124, 23, 64,false);
			if (hit(124,28,63)) teleport(185, 31, 45,false);
			if (hit(185,31,46)) teleport(124, 28, 64,false);
			if (hit(124,36,69)) teleport(186, 31, 45,false);
			if (hit(186,31,46)) teleport(124, 36, 70,false);
			
			// Cutscene with Arthra
			if (gp.player.p.flag[6][6] && !gp.player.p.flag[7][0] && hit(124,25,83)) gp.player.interactNPC(gp.npc[124][21], false);
			
			// Joseph 1A
			if (hit(124,91,76)) teleport(137, 60, 76,false);
			if (hit(137,60,77)) teleport(124, 91, 77,false);
			
			if (hit(137,75,70)) teleport(139, 46, 60,true);
			if (hit(139,46,60)) teleport(137, 75, 70,true);
			if (hit(137,80,80)) teleport(139, 57, 70,true);
			if (hit(139,57,70)) teleport(137, 80, 80,true);
			if (hit(137,90,64)) teleport(139, 60, 52,true);
			if (hit(139,60,52)) teleport(137, 90, 64,true);
			
			if (hit(137,46,62)) teleport(138, 51, 65,true);
			if (hit(138,51,65)) teleport(137, 46, 62,true);
			if (hit(137,69,49)) teleport(138, 59, 52,true);
			if (hit(138,59,52)) teleport(137, 69, 49,true);
			if (hit(137,41,48)) teleport(138, 40, 56,true);
			if (hit(138,40,56)) teleport(137, 41, 48,true);
			
			// Joseph 2A
			if (hit(124,81,57)) teleport(138, 31, 77,false);
			if (hit(138,31,78)) teleport(124, 81, 58,false);
			if (hit(138,53,77)) teleport(139, 42, 50,false);
			if (hit(139,42,49)) teleport(138, 53, 76,false);
			
			if (hit(138,61,60)) teleport(140, 49, 43,true);
			if (hit(140,49,43)) teleport(138, 61, 60,true);
			
			// Joseph 2B
			if (hit(124,87,71)) teleport(139, 38, 74,false);
			if (hit(139,38,75)) teleport(124, 87, 72,false);
			
			if (hit(139,54,56)) teleport(141, 57, 72,true);
			if (hit(141,57,72)) teleport(139, 54, 56,true);
			if (hit(139,31,51)) teleport(140, 38, 58,true);
			if (hit(140,38,58)) teleport(139, 31, 51,true);
			
			// Joseph 3A
			if (hit(140,71,64)) teleport(141, 33, 58,false);
			if (hit(141,33,59)) teleport(140, 71, 65,false);
			
			if (hit(140,26,69)) teleport(142, 42, 72,true);
			if (hit(142,42,72)) teleport(140, 26, 69,true);
			
			// Joseph 4A
			if (hit(142,73,40)) teleport(143, 53, 49,false);
			if (hit(143,53,48)) teleport(142, 73, 39,false);
			
			// Gym 7 split Shakes
			if (p.flag[5][8]) {
				if (!p.flag[6][7] && (hit(11,18,81) || hit(11,19,81) || hit(11,20,81))) shake(6, 7, 25);
				if (!p.flag[6][8] && hit(124,22,9)) shake(6, 8, 35);
				if (!p.flag[6][9] && hit(124,16,52)) shake(6, 9, 50);
				if (!p.flag[6][10] && hit(124,54,74)) shake(6, 10, 100);
				if (!p.flag[6][11] && hit(124,69,92)) shake(6, 11, 110);
				if (!p.flag[6][12] && hit(124,79,96)) shake(6, 12, 125);
				if (!p.flag[6][13] && hit(124,81,84)) shake(6, 13, 135);
				if (!p.flag[6][14] && hit(137,60,76)) shake(6, 14, 150);
				if (!p.flag[6][15] && hit(139,49,60)) shake(6, 15, 175);
				if (!p.flag[6][16] && hit(139,52,52)) shake(6, 16, 200);
				if (!p.flag[6][17] && hit(139,43,50)) shake(6, 17, 225);
				if (!p.flag[6][18] && hit(138,53,76)) shake(6, 18, 250);
			}
			
			// Gelb Forest 1 -> 2
			if (hit(11,42,77)) teleport(144, 11, 28,false);
			if (hit(144,10,28)) teleport(11, 41, 77,false);
			
			// Gelb Cavern Exit
			if (hit(11,75,26)) teleport(168, 59, 74,true);
			if (hit(168,59,74)) teleport(11, 75, 26,true);
			
			// Shadow Path -> Shadow Cavern
			if (hit(105,12,46)) teleport(150, 54, 77,false);
			if (hit(150,54,78)) teleport(105, 12, 47,false);
			
			// Splinkty 5A
			if (hit(149,56,71)) gp.iTile[149][0] = null;
			if (hit(149,42,71)) gp.iTile[149][1] = null;
			if (hit(149,39,65)) gp.iTile[149][2] = null;
			
			// Cutscene with Rick 3
			if (!gp.player.p.flag[7][2] && hit(149,49,65)) gp.player.interactNPC(gp.npc[149][14], false);
			// Cutscene with Fred 4
			if (!gp.player.p.flag[7][3] && hit(149,49,58)) gp.player.interactNPC(gp.npc[149][15], false);
			// Cutscene with Maxwell 2
			if (!gp.player.p.flag[7][4] && hit(149,49,51)) gp.player.interactNPC(gp.npc[149][16], false);
			if (gp.player.p.flag[7][4] && !gp.player.p.flag[7][5] && hit(149,50,49)) gp.player.interactNPC(gp.npc[149][16], false);
			if (hit(149,50,63)) teleport(149, 49, 69,false);
			
			// Cutscene with Dragowrath
			if (!gp.player.p.flag[7][6] && hit(146,82,27)) gp.player.interactNPC(gp.npc[146][3], false);
			
			// Rawwar City -> Route 37 gate
			if (hit(124,16,47,false)) teleport(179, 31, 34,false);
			if (hit(179,31,33)) teleport(124, 16, 46,false);
			if (hit(179,31,46)) teleport(124, 16, 52,false);
			if (hit(124,16,51,false)) teleport(179, 31, 45,false);
			
			// Rawwar City -> Route 38 gate
			if (hit(124,42,82,false)) teleport(180, 23, 40,false);
			if (hit(180,22,40)) teleport(124, 41, 82,false);
			if (hit(180,37,40)) teleport(124, 47, 82,false);
			if (hit(124,46,82,false)) teleport(180, 36, 40,false);
			
			// Rawwar City -> Route 39 gate
			if (hit(124,32,92,false)) teleport(151, 31, 34,false);
			if (hit(151,31,33)) teleport(124, 32, 91,false);
			if (hit(151,31,46)) teleport(152, 30, 8,false);
			if (hit(152,30,7,false)) teleport(151, 31, 45,false);
			
			// Iron Town PC
			if (hit(152,37,72)) teleport(153, 31, 45,false);
			if (hit(153,31,46)) teleport(152, 37, 73,false);
			
			// Iron Town Shop
			if (hit(152,44,81)) teleport(154, 31, 45,false);
			if (hit(154,31,46)) teleport(152, 44, 82,false);
		}
	}
	
	public boolean hit(int map, int col, int row) {
		if (map != gp.currentMap) return false;
		return hit(map, col, row, true);
	}
	
	public boolean hit(int map, int col, int row, boolean deflt) {
		if (map != gp.currentMap) return false;
		if (!canTouchEvent) return false;
		boolean hit = false;
		
		if (map == gp.currentMap) {
			gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
			gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
			if (!deflt && !eventRect[map][col][row].modified) {
				eventRect[map][col][row].x = gp.tileSize / 8;
				eventRect[map][col][row].y = gp.tileSize / 8;
	 			eventRect[map][col][row].width = gp.tileSize * 3 / 4;
	 			eventRect[map][col][row].height = gp.tileSize * 3 / 4;
	 			eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
	 			eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
	 			eventRect[map][col][row].modified = true;
	 			
			}
			eventRect[map][col][row].x = col*gp.tileSize + eventRect[map][col][row].x;
			eventRect[map][col][row].y = row*gp.tileSize + eventRect[map][col][row].y;
			
			if (gp.player.solidArea.intersects(eventRect[map][col][row])) {
				previousEventX = gp.player.worldX;
				previousEventY = gp.player.worldY;
				hit = true;
			}
			
			gp.player.solidArea.x = gp.player.solidAreaDefaultX;
			gp.player.solidArea.y = gp.player.solidAreaDefaultY;
			eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
			eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
		}
		
		
		return hit;
	}
	
	public void teleport(int map, int col, int row, boolean cooldown) {
		gp.gameState = GamePanel.TRANSITION_STATE;
		tempMap = map;
		tempCol = col;
		tempRow = row;
		tempCooldown = cooldown;
		
//		gp.currentMap = map;
//		gp.player.worldX = gp.tileSize * col;
//		gp.player.worldY = gp.tileSize * row;
//		gp.player.worldY -= gp.tileSize / 4;
//		p.currentMap = map;
//		
//		canTouchEvent = !cooldown;
//		
//		previousEventX = gp.player.worldX;
//		previousEventY = gp.player.worldY;
	}
	
	public void shake(int flagX, int flagY, int intensity) {
		gp.setTaskState();
		Task.addTask(Task.SHAKE, "", intensity);
		p.flag[flagX][flagY] = true;
	}
}
