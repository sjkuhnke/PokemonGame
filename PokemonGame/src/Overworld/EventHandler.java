package Overworld;

import javax.swing.JOptionPane;

import Entity.PlayerCharacter;
import Swing.Player;

public class EventHandler {

	
	GamePanel gp;
	Player p;
	EventRect eventRect[][][];
	
	int previousEventX, previousEventY;
	boolean canTouchEvent = true;
	
	public EventHandler(GamePanel gp) {
		this.gp = gp;
		
		eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
		
		int map = 0;
		int col = 0;
		int row = 0;
		while(map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
			
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
			if (hit(0,20,10)) {
				teleport(3, 31, 45,false);
				gp.player.p.flags[0] = true;
			}
			if (hit(3,31,46)) teleport(0, 20, 11,false);
			if (hit(3,31,33)) teleport(4, 74, 84,false);
			if (hit(4,74,85)) teleport(3, 31, 34,false);
			
			// Poppy Grove PC
			if (hit(4,69,71)) teleport(5, 31, 45,false);
			if (hit(5,31,46)) teleport(4, 69, 72,false);
			
			// Poppy Grove Shop
			if (hit(4,64,71)) teleport(6, 31, 45,false);
			if (hit(6,31,46)) teleport(4, 64, 72,false);
			
			// Poppy Grove Warehouse
			if (hit(4,66,79)) teleport(7, 31, 45,false);
			if (hit(7,31,46)) teleport(4, 66, 80,false);
			if (hit(7,40,39)) teleport(8, 28, 39,false);
			if (hit(8,27,39)) teleport(7, 39, 39,false);
			
			// Poppy Grove Gym
			if (hit(4,81,61)) teleport(9, 31, 45,false);
			if (hit(9,31,46)) teleport(4, 81, 62,false);
			
			// Route 24 pt. 1 gate
			if (hit(4,7,68)) teleport(10, 36, 40,false);
			if (hit(10,37,40)) teleport(4, 8, 68,false);
			if (hit(10,22,40)) teleport(11, 91, 49,false);
			if (hit(11,92,49)) teleport(10, 23, 40,false);
			
			// Route 25 gate
			if (hit(11,24,17)) teleport(12, 31, 45,false);
			if (hit(12,31,46)) teleport(11, 24, 18,false);
			if (hit(12,31,33)) teleport(13, 28, 89,false);
			if (hit(13,28,90)) teleport(12, 31, 34,false);
			
			// Energy Plant A
			if (hit(13,19,85)) teleport(14, 31, 49,false);
			if (hit(14,31,50)) teleport(13, 19, 86,false);
			if (hit(14,21,37)) teleport(15, 50, 40,false);
			if (hit(15,51,40)) teleport(14, 22, 37,false);
			
			// Energy Plant B
			if (hit(15,32,9)) teleport(16, 32, 46,false);
			if (hit(16,31,46)) teleport(15, 31, 9,false);
			
			// Office
			if (hit(13,48,57)) teleport(17, 53, 56,false);
			if (hit(17,53,57)) teleport(13, 48, 58,false);
			
			if (hit(17,52,46)) gp.aSetter.updateNPC();
			if (hit(17,53,46)) gp.aSetter.updateNPC();
			if (hit(17,54,46)) gp.aSetter.updateNPC();
			
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
			if (hit(0,17,58)) teleport(23, 31, 34,false);
			if (hit(23,31,33)) teleport(0, 17, 57,false);
			if (hit(23,31,46)) teleport(22, 76, 8,false);
			if (hit(22,76,7)) teleport(23, 31, 45,false);
			
			// Route 26 to Mt. Splinkty
			if (hit(13,27,5)) teleport(24, 56, 84,false);
			if (hit(24,56,85)) teleport(13, 27, 6,false);
			
			// Mt. Splinkty
			if (hit(24,73,63)) teleport(25, 73, 69,true);
			if (hit(25,73,69)) teleport(24, 73, 63,true);
			
			if (hit(25,74,62)) teleport(26, 58, 73,true);
			if (hit(26,58,73)) teleport(25, 74, 62,true);
			
			if (hit(25,76,81)) teleport(27, 74, 68,true);
			if (hit(27,74,68)) teleport(25, 76, 81,true);
			if (hit(27,76,70)) teleport(28, 8, 46,false);
			if (hit(28,8,45)) teleport(27, 76, 69,false);
			
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
			if (hit(28,93,46)) teleport(34, 23, 40,false);
			if (hit(34,22,40)) teleport(28, 92, 46,false);
			if (hit(34,37,40)) teleport(33, 8, 13,false);
			if (hit(33,7,13)) teleport(34, 36, 40,false);
			
			// Electric Tunnel
			if (hit(33,62,16)) teleport(35, 11, 72,false);
			if (hit(35,11,73)) teleport(33, 62, 17,false);
			
			if (hit(35,90,69)) teleport(36, 15, 46,false);
			if (hit(36,15,45)) teleport(35, 90, 68,false);
			
			// Schrice City Gate
			if (hit(36,93,41)) teleport(37, 23, 40,false);
			if (hit(37,22,40)) teleport(36, 92, 41,false);
			if (hit(37,37,40)) teleport(38, 7, 47,false);
			if (hit(38,6,47)) teleport(37, 36, 40,false);
			
			// Schrice PC
			if (hit(38,49,54)) teleport(39, 31, 45,false);
			if (hit(39,31,46)) teleport(38, 49, 55,false);
						
			// Schrice Supermarket
			if (hit(38,18,58)) teleport(45, 31, 45,false);
			if (hit(45,31,46)) teleport(38, 18, 59,false);
			if (hit(45,36,38)) teleport(40, 27, 38,false);
			if (hit(40,26,38)) teleport(45, 35, 38,false);
			
			// Schrice School
			if (hit(38,28,55)) teleport(41, 31, 45,false);
			if (hit(41,31,46)) teleport(38, 28, 56,false);
			
			// Radio Tower
			if (hit(38,62,57)) teleport(42, 31, 45,false);
			if (hit(42,31,46)) teleport(38, 62, 58,false);
			
			if (hit(42,31,33)) teleport(43, 31, 45,false);
			if (hit(43,31,46)) teleport(42, 31, 34,false);
			
			// Schrice Gym
			if (hit(38,62,41)) teleport(44, 63, 67,false);
			if (hit(44,63,68)) teleport(38, 62, 42,false);
			
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
			if (hit(28,81,89)) teleport(74, 31, 34,false);
			if (hit(74,31,33)) teleport(28, 81, 88,false);
			if (hit(74,31,46)) teleport(33, 33, 34,false);
			if (hit(33,33,33)) teleport(74, 31, 45,false);
			
			// Route 36/Peaceful park gate
			if (hit(4,17,11)) teleport(75, 36, 40,false);
			if (hit(75,37,40)) teleport(4, 18, 11,false);
			if (hit(75,22,40)) teleport(33, 55, 84,false);
			if (hit(33,56,84)) teleport(75, 23, 40,false);
			
			// Route 30/Mindagan Lake gate
			if (hit(38,42,90)) teleport(76, 31, 34,false);
			if (hit(76,31,33)) teleport(38, 42, 89,false);
			if (hit(76,31,46)) teleport(77, 48, 8,false);
			if (hit(77,48,7)) teleport(76, 31, 45,false);
			
			// Mindagan Cavern
			if (hit(77,83,32)) teleport(78, 58, 74,false);
			if (hit(78,58,75)) teleport(77, 83, 33,false);
			
			// Route 31/Mindagan Lake gate
			if (hit(77,56,74)) teleport(79, 31, 34,false);
			if (hit(79,31,33)) teleport(77, 56, 73,false);
			if (hit(79,31,46)) teleport(80, 32, 8,false);
			if (hit(80,32,7)) teleport(79, 31, 45,false);
			
			// Route 42/Shadow Ravine gate
			if (hit(80,47,85)) teleport(81, 31, 34,false);
			if (hit(81,31,33)) teleport(80, 47, 84,false);
			if (hit(81,31,46)) teleport(0, 85, 6,false);
			if (hit(0,85,5)) teleport(81, 31, 45,false);
			
			// Shadow Ravine 0
			if (hit(80,41,43)) teleport(90, 51, 9,true);
			if (hit(90,51,9)) teleport(80, 41, 43,true);
			
			// Shadow Ravine/Shadow Ravine 1A gate
			if (hit(83,6,70)) teleport(82, 36, 40,false);
			if (hit(82,37,40)) teleport(83, 7, 70,false);
			if (hit(82,22,40)) teleport(80, 92, 51,false);
			if (hit(80,93,51)) teleport(82, 23, 40,false);
			
			// Route 32/Frenco City gate
			if (hit(85,6,67)) teleport(84, 36, 40,false);
			if (hit(84,37,40)) teleport(85, 7, 67,false);
			if (hit(84,22,40)) teleport(83, 92, 51,false);
			if (hit(83,93,51)) teleport(84, 23, 40,false);
			
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
			if (hit(88,71,63)) teleport(88, 51, 47,true); // D
			if (hit(88,51,47)) teleport(88, 71, 63,true); // D
			if (hit(88,71,57)) teleport(88, 81, 63,true); // E
			if (hit(88,81,63)) teleport(88, 71, 57,true); // E
			if (hit(88,81,67)) teleport(88, 55, 43,true); // F
			if (hit(88,55,43)) teleport(88, 81, 67,true); // F
			if (hit(88,85,67)) teleport(88, 41, 67,true); // G
			if (hit(88,41,67)) teleport(88, 85, 67,true); // G
			if (hit(88,71,53)) teleport(88, 55, 53,true); // H
			if (hit(88,55,53)) teleport(88, 71, 53,true); // H
			if (hit(88,75,53)) teleport(88, 51, 43,true); // I
			if (hit(88,51,43)) teleport(88, 75, 53,true); // I
			if (hit(88,85,53)) teleport(88, 85, 57,true); // J
			if (hit(88,85,57)) teleport(88, 85, 53,true); // J
			if (hit(88,71,43)) teleport(88, 55, 57,true); // K
			if (hit(88,55,57)) teleport(88, 71, 43,true); // K
			if (hit(88,75,43)) teleport(88, 41, 63,true); // L
			if (hit(88,41,63)) teleport(88, 75, 43,true); // L
			if (hit(88,71,47)) teleport(88, 81, 43,true); // M
			if (hit(88,81,43)) teleport(88, 71, 47,true); // M
			if (hit(88,75,47)) teleport(88, 45, 53,true); // N
			if (hit(88,45,53)) teleport(88, 75, 47,true); // N
			if (hit(88,45,57)) teleport(88, 85, 43,true); // O
			if (hit(88,85,43)) teleport(88, 45, 57,true); // O
			if (hit(88,81,47)) teleport(88, 41, 57,true); // P
			if (hit(88,41,57)) teleport(88, 81, 47,true); // P
			if (hit(88,45,47)) teleport(88, 85, 47,true); // Q
			if (hit(88,85,47)) teleport(88, 45, 47,true); // Q
			if (hit(88,41,43)) teleport(88, 63, 39,true); // R
			if (hit(88,63,39)) teleport(88, 41, 43,true); // R
			if (hit(88,45,43)) teleport(88, 55, 63,true); // S
			if (hit(88,55,63)) teleport(88, 45, 43,true); // S
			if (hit(88,51,67)) teleport(88, 51, 57,true); // T
			if (hit(88,51,57)) teleport(88, 51, 67,true); // T
			if (hit(88,55,67)) teleport(88, 45, 63,true); // U
			if (hit(88,45,63)) teleport(88, 55, 67,true); // U
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
			if (hit(105,45,91)) teleport(104, 47, 43,true);
			if (hit(104,47,42)) teleport(105, 45, 90,true);
			
			// TN Base -> Shadow Ravine -3
			if (hit(104,47,63)) teleport(103, 47, 35,true);
			if (hit(103,47,34)) teleport(104, 47, 62,true);
			
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
			if (hit(85,86,22)) teleport(106, 31, 45,false);
			if (hit(106,31,46)) teleport(85, 86, 23,false);
			if (hit(106,31,33)) teleport(107, 49, 92,false);
			if (hit(107,49,93)) teleport(106, 31, 34,false);
			
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
				if (!gp.player.p.flags[19] || !gp.player.p.flags[20]) {
					gp.keyH.pause();
					teleport(107, 42, 57,true);
					if (gp.player.p.grustCount < 10) {
						JOptionPane.showMessageDialog(null, "This portal seems to be jammed\nby the Ghosts everywhere...\nThere are " + (10 - gp.player.p.grustCount) + " remaining!");
					} else if (!gp.player.p.flags[19]) {
						JOptionPane.showMessageDialog(null, "This portal seems to be jammed\nby Rick...");
					} else {
						JOptionPane.showMessageDialog(null, "This portal seems to be jammed\nby Team Nuke... Rick said\nthat they're at the bottom\nof Electric Tunnel!");
					}
					gp.keyH.resume();
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
			
			// Ghostly Woods -> Route 34 Gate -> Route 34
			if (hit(107,45,18)) teleport(108, 31, 45,false);
			if (hit(108,31,46)) teleport(107, 45, 19,false);
			if (hit(108,31,33)) teleport(109, 73, 92,false);
			if (hit(109,73,93)) teleport(108, 31, 34,false);
			
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
			
			// Route 35
			if (hit(109,6,36)) teleport(114, 36, 40,false);
			if (hit(114,37,40)) teleport(109, 7, 36,false);
			if (hit(114,22,40)) teleport(115, 91, 77,false);
			if (hit(115,92,77)) teleport(114, 23, 40,false);
			
			// Schrice City/Route 35 Gate
			if (hit(115,19,78)) teleport(116, 36, 40,false);
			if (hit(116,37,40)) teleport(115, 20, 78,false);
			if (hit(116,22,40)) teleport(38, 78, 47,false);
			if (hit(38,79,47)) teleport(116, 23, 40,false);
			
			// Gym 6
			if (hit(109,15,42)) teleport(113, 53, 68,false);
			if (hit(113,53,69)) teleport(109, 15, 43,false);
			
			// Mindagan Cavern
			if (hit(78,62,69)) teleport(117, 61, 72,true);
			if (hit(117,61,72)) teleport(78, 62, 69,true);
			
			// Glurg Town Fossil Center
			if (hit(109,12,34)) teleport(118, 31, 45,false);
			if (hit(118,31,46)) teleport(109, 12, 35,false);
			
			// New Minnow Town -> Route 43 Gate
			if (hit(0,92,49)) teleport(120, 23, 40,false);
			if (hit(120,22,40)) teleport(0, 91, 49,false);
			
			if (hit(120,37,40)) teleport(119, 8, 49,false);
			if (hit(119,7,49)) teleport(120, 36, 40,false);
			
			// Berry Shoppe House
			if (hit(85,32,63)) teleport(53, 31, 45,false);
			if (hit(53,31,46)) teleport(85, 32, 64,false);
			
			// Sicab City Tower
			if (hit(13,53,75)) teleport(121, 31, 45,false);
			if (hit(121,31,46)) teleport(13, 53, 76,false);
			
			if (hit(121,31,33)) teleport(122, 31, 45,false);
			if (hit(122,31,46)) teleport(121, 31, 34,false);
		}
	}
	
	public boolean hit(int map, int col, int row) {
		if (map != gp.currentMap) return false;
		if (!canTouchEvent) return false;
		boolean hit = false;
		
		if (map == gp.currentMap) {
			gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
			gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
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
		gp.currentMap = map;
		gp.player.worldX = gp.tileSize * col;
		gp.player.worldY = gp.tileSize * row;
		gp.player.worldY -= gp.tileSize / 4;
		p.currentMap = map;
		
		canTouchEvent = !cooldown;
		
		previousEventX = gp.player.worldX;
		previousEventY = gp.player.worldY;
		
		gp.aSetter.updateNPC();
		gp.aSetter.setInteractiveTile(map);
		gp.player.cross = false;
		
		PMap.getLoc(gp.currentMap, (int) Math.round(gp.player.worldX * 1.0 / 48), (int) Math.round(gp.player.worldY * 1.0 / 48));
		Main.window.setTitle("Pokemon Game - " + PlayerCharacter.currentMapName);
	}
}
