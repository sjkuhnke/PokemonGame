package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Overworld.GamePanel;

public class TileManager {
	GamePanel gp;
	public Tile[] tile;
	public int mapTileNum[][][];
	
	public TileManager(GamePanel gp) {
		this.gp = gp;
		tile = new Tile[500];
		mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
		
		getTileImage();
		loadMap("/maps/map01.txt", 0);
		loadMap("/maps/pc.txt", 1);
		loadMap("/maps/mart.txt", 2);
		loadMap("/maps/gate01.txt", 3);
		loadMap("/maps/map02.txt", 4);
		loadMap("/maps/pc.txt", 5);
		loadMap("/maps/mart.txt", 6);
		loadMap("/maps/warehouse01.txt", 7);
		loadMap("/maps/warehouse01B.txt", 8);
		loadMap("/maps/gym01.txt", 9);
		loadMap("/maps/gate02.txt", 10);
		loadMap("/maps/map03.txt", 11);
		loadMap("/maps/gate01.txt", 12);
		loadMap("/maps/map04.txt", 13);
		loadMap("/maps/energy01.txt", 14);
		loadMap("/maps/energy01B.txt", 15);
		loadMap("/maps/energy02.txt", 16);
		loadMap("/maps/office01.txt", 17);
		loadMap("/maps/office02.txt", 18);
		loadMap("/maps/pc.txt", 19);
		loadMap("/maps/mart.txt", 20);
		loadMap("/maps/gym02.txt", 21);
		loadMap("/maps/map05.txt", 22);
		loadMap("/maps/gate01.txt", 23);
		loadMap("/maps/splinkty01A.txt", 24);
		loadMap("/maps/splinkty02B.txt", 25);
		loadMap("/maps/splinkty03B.txt", 26);
		loadMap("/maps/splinkty03A.txt", 27);
		loadMap("/maps/map06.txt", 28);
		loadMap("/maps/pc.txt", 29);
		loadMap("/maps/market01.txt", 30);
		loadMap("/maps/gym03.txt", 31);
		loadMap("/maps/house01.txt", 32);
		loadMap("/maps/map07.txt", 33);
		loadMap("/maps/gate02.txt", 34);
		loadMap("/maps/electric01.txt", 35);
		loadMap("/maps/map08.txt", 36);
		loadMap("/maps/gate02.txt", 37);
		loadMap("/maps/map09.txt", 38);
		loadMap("/maps/pc.txt", 39);
		loadMap("/maps/supermarket02.txt", 40);
		loadMap("/maps/school01.txt", 41);
		loadMap("/maps/radio_tower01.txt", 42);
		loadMap("/maps/radio_tower02.txt", 43);
		loadMap("/maps/gym04.txt", 44);
		loadMap("/maps/supermarket01.txt", 45);
		loadMap("/maps/house01.txt", 46);
		loadMap("/maps/house01.txt", 47);
		loadMap("/maps/house02.txt", 48);
	}
	
	public void getTileImage() {
		setup(0, true);
		setup(1, false);
		setup(2, true);
		setup(3, true);
		setup(4, false);
		setup(5, true);
		setup(6, false);
		setup(8, false);
		setup(9, false);
		setup(10, false);
		setup(11, false);
		setup(12, false);
		setup(13, false);
		setup(14, false);
		setup(15, false);
		setup(16, false);
		setup(17, false);
		setup(18, false);
		setup(19, false);
		setup(20, false);
		setup(21, false);
		setup(22, false);
		setup(23, true);
		setup(24, true);
		setup(25, true);
		setup(26, true);
		setup(27, true);
		setup(28, true);
		setup(29, true);
		setup(30, true);
		setup(31, true);
		setup(32, true);
		setup(33, true);
		setup(34, true);
		setup(35, true);
		setup(36, true);
		
		setup(49, true);
		setup(50, true);
		setup(51, true);
		setup(52, true);
		setup(53, true);
		setup(54, true);
		setup(55, true);
		setup(56, true);
		setup(57, false); // door
		setup(58, true);
		setup(59, true);
		setup(60, false);
		setup(61, false);
		setup(62, false);
		setup(63, false);
		setup(64, false);
		setup(65, false);
		setup(66, false);
		setup(67, true);
		setup(68, true);
		setup(69, true);
		setup(70, true);
		setup(71, true);
		setup(72, true);
		setup(73, true);
		setup(74, true);
		setup(75, true);
		setup(76, true);
		setup(77, true);
		setup(78, true);
		setup(79, false); // door
		setup(80, true);
		setup(81, true);
		setup(82, true);
		setup(83, true);
		setup(84, true);
		setup(85, true);
		setup(86, true);
		setup(87, true);
		setup(88, true);
		setup(89, true);
		setup(90, true);
		setup(91, true);
		setup(92, false); // door
		setup(93, true);
		setup(94, false);
		setup(95, true);
		setup(96, true);
		setup(97, true);
		setup(98, true);
		setup(99, true);
		setup(100, true);
		setup(101, true);
		setup(102, true);
		setup(103, true);
		setup(104, true);
		setup(105, false);
		setup(106, true);
		setup(107, true);
		setup(108, true);
		setup(109, true);
		setup(110, true);
		setup(111, true);
		setup(112, true);
		setup(113, true);
		setup(114, false);
		setup(115, false);
		setup(116, false);
		setup(117, true);
		setup(118, false);
		setup(119, true);
		setup(120, true);
		setup(121, true);
		setup(122, true);
		setup(123, true);
		setup(124, true);
		setup(125, false);
		setup(126, false);
		setup(127, true);
		setup(128, true);
		setup(129, true);
		setup(130, true);
		setup(131, true);
		setup(132, true);
		setup(133, false);
		setup(134, true);
		setup(135, true);
		setup(136, false);
		setup(137, false);
		setup(138, false);
		setup(139, true);
		setup(140, true);
		setup(141, true);
		setup(142, true);
		setup(143, true);
		setup(144, true);
		setup(145, true);
		setup(146, true);
		setup(147, true);
		setup(148, true);
		setup(149, true);
		setup(150, true);
		setup(151, true);
		setup(152, true);
		setup(153, true);
		setup(154, true);
		setup(155, true);
		setup(156, true);
		setup(157, true);
		setup(158, true);
		setup(159, true);
		setup(160, false);
		setup(161, true);
		setup(162, true);
		setup(163, true);
		setup(164, true);
		setup(165, true);
		setup(166, true);
		setup(167, true);
		setup(168, true);
		setup(169, true);
		setup(170, true);
		setup(171, true);
		setup(172, true);
		setup(173, true);
		setup(174, true);
		setup(175, true);
		setup(176, true);
		setup(177, true);
		setup(178, true);
		setup(179, true);
		setup(180, false);
		setup(181, false);
		setup(182, true);
		setup(183, false);
		setup(184, true);
		setup(185, false);
		setup(186, true);
		setup(187, true);
		setup(188, true);
		setup(189, false);
		setup(190, true);
		setup(191, true);
		setup(192, true);
		setup(193, false);
		setup(194, false);
		setup(195, false);
		setup(196, true);
		setup(197, false);
		setup(198, false);
		setup(199, false);
		setup(200, false);
		setup(201, true);
		setup(202, false);
		setup(203, false);
		setup(204, false);
		setup(205, true);
		setup(206, true);
		setup(207, true);
		setup(208, false);
		setup(209, false);
		
		setup(210, false);
		setup(211, false);
		setup(212, false);
		setup(213, true);
		setup(214, true);
		setup(215, true);
		setup(216, true);
		setup(217, true);
		setup(218, true);
		setup(219, true);
		setup(220, true);
		setup(221, true);
		setup(222, true);
		setup(223, true);
		setup(224, true);
		setup(225, true);
		setup(226, false); // door
		setup(227, true);
		setup(228, true);
		setup(229, true);
		setup(230, true);
		setup(231, true);
		setup(232, true);
		setup(233, true);
		setup(234, true);
		
		setup(236, true);
		setup(237, true);
		setup(238, true);
		setup(239, true);
		setup(240, true);
		setup(241, true);
		setup(242, true);
		setup(243, true);
		setup(244, true);
		setup(245, true);
		setup(246, true);
		setup(247, true);
		setup(248, true);
		setup(249, true);
		setup(250, true);
		setup(251, true);
		setup(252, true);
		setup(253, true);
		setup(254, false);
		setup(255, false);
		setup(256, true);
		setup(257, true);
		setup(258, true);
		setup(259, false);
		setup(260, true);
		setup(261, true);
		setup(262, true);
		setup(263, true);
		setup(264, true);
		setup(265, true);

		setup(267, true);
		setup(268, true);
		setup(269, true);
		setup(270, true);
		setup(271, true);
		setup(272, true);
		setup(273, true);
		setup(274, true);
		setup(275, true);
		setup(276, false);
		setup(277, false);
		setup(278, true);
		setup(279, true);
		setup(280, true);
		setup(281, false);
		setup(282, true);
		setup(283, false);
		setup(284, true);
		setup(285, true);
		setup(286, true);
		setup(287, true);
		setup(288, true);
		setup(289, true);
		setup(290, false);
		setup(291, false);
		setup(292, true);
		setup(293, false);
		setup(294, false);
		setup(295, false);
		setup(296, true);
		setup(297, true);
		setup(298, false);
		setup(299, true);
		setup(300, true);
		setup(301, true);
		setup(302, true);
		setup(303, true);
		setup(304, true);
		setup(305, true);
		setup(306, true);
		setup(307, true);
		setup(308, true);
		setup(309, true);
		setup(310, true);
		setup(311, true);
		setup(312, true);
		
		setup(313, true);
		setup(314, true);
		setup(315, true);
		setup(316, true);
		setup(317, true);
		setup(318, true);
		setup(319, true);
		setup(320, true);
		setup(321, true);
		setup(322, true);
		setup(323, true);
		setup(324, true);
		
		setup(337, false);
		setup(338, true);
		setup(339, true);
		setup(340, true);
		setup(341, true);
		setup(342, true);
		setup(343, true);
		setup(344, true);
		setup(345, true);
		setup(346, true);
		setup(347, true);
		setup(348, true);
		setup(349, true);
		setup(350, false);
		setup(351, false);
		
		setup(352, false);
		setup(353, false);
		setup(354, false);
		setup(355, false);
		setup(356, false);
		setup(357, false);
		setup(358, false);
		setup(359, false);
		setup(360, false);
		setup(361, false);
		setup(362, false);

		setup(364, false);
		setup(365, false);
		setup(366, false);
		setup(367, true);
		setup(368, true);
		setup(369, true);
		setup(370, true);
		setup(371, true);
//		setup(372, true);
//		setup(373, true);
//		setup(374, true);
//		setup(375, true);
//		setup(376, true);
//		setup(377, true);
//		setup(378, true);
//		setup(379, true);
//		setup(380, true);
//		setup(381, true);
//		setup(382, true);
//		setup(383, true);
//		setup(384, true);
//		setup(385, true);
//		setup(386, true);
//		setup(387, true);
//		setup(388, true);
//		setup(389, true);
//		setup(390, true);
//		setup(391, true);
//		setup(392, true);
//		setup(393, true);
//		setup(394, true);
//		setup(395, true);
//		setup(396, true);
//		setup(397, true);
//		setup(398, true);
//		setup(399, true);

		try {
			tile[7] = new GrassTile();
			tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/007.png"));
			
			tile[235] = new BuildingTile();
			tile[235].image = ImageIO.read(getClass().getResourceAsStream("/tiles/235.png"));
			
			tile[266] = new CaveTile();
			tile[266].image = ImageIO.read(getClass().getResourceAsStream("/tiles/266.png"));
			
			tile[363] = new GrassTile();
			tile[363].image = ImageIO.read(getClass().getResourceAsStream("/tiles/363.png"));
			
			tile[37] = new Tile();
			tile[37].image = ImageIO.read(getClass().getResourceAsStream("/tiles/037.png"));
			tile[37].collisionDirection = "down";
			tile[37].collision = true;
			
			tile[38] = new Tile();
			tile[38].image = ImageIO.read(getClass().getResourceAsStream("/tiles/038.png"));
			tile[38].collisionDirection = "down";
			tile[38].collision = true;
			
			tile[39] = new Tile();
			tile[39].image = ImageIO.read(getClass().getResourceAsStream("/tiles/039.png"));
			tile[39].collisionDirection = "down";
			tile[39].collision = true;
			
			tile[40] = new Tile();
			tile[40].image = ImageIO.read(getClass().getResourceAsStream("/tiles/040.png"));
			tile[40].collisionDirection = "up";
			tile[40].collision = true;
			
			tile[41] = new Tile();
			tile[41].image = ImageIO.read(getClass().getResourceAsStream("/tiles/041.png"));
			tile[41].collisionDirection = "up";
			tile[41].collision = true;
			
			tile[42] = new Tile();
			tile[42].image = ImageIO.read(getClass().getResourceAsStream("/tiles/042.png"));
			tile[42].collisionDirection = "up";
			tile[42].collision = true;
			
			tile[43] = new Tile();
			tile[43].image = ImageIO.read(getClass().getResourceAsStream("/tiles/043.png"));
			tile[43].collisionDirection = "right";
			tile[43].collision = true;
			
			tile[44] = new Tile();
			tile[44].image = ImageIO.read(getClass().getResourceAsStream("/tiles/044.png"));
			tile[44].collisionDirection = "right";
			tile[44].collision = true;
			
			tile[45] = new Tile();
			tile[45].image = ImageIO.read(getClass().getResourceAsStream("/tiles/045.png"));
			tile[45].collisionDirection = "right";
			tile[45].collision = true;
			
			tile[46] = new Tile();
			tile[46].image = ImageIO.read(getClass().getResourceAsStream("/tiles/046.png"));
			tile[46].collisionDirection = "left";
			tile[46].collision = true;
			
			tile[47] = new Tile();
			tile[47].image = ImageIO.read(getClass().getResourceAsStream("/tiles/047.png"));
			tile[47].collisionDirection = "left";
			tile[47].collision = true;
			
			tile[48] = new Tile();
			tile[48].image = ImageIO.read(getClass().getResourceAsStream("/tiles/048.png"));
			tile[48].collisionDirection = "left";
			tile[48].collision = true;
			
			
			tile[325] = new Tile();
			tile[325].image = ImageIO.read(getClass().getResourceAsStream("/tiles/325.png"));
			tile[325].collisionDirection = "down";
			tile[325].collision = true;
			
			tile[326] = new Tile();
			tile[326].image = ImageIO.read(getClass().getResourceAsStream("/tiles/326.png"));
			tile[326].collisionDirection = "down";
			tile[326].collision = true;
			
			tile[327] = new Tile();
			tile[327].image = ImageIO.read(getClass().getResourceAsStream("/tiles/327.png"));
			tile[327].collisionDirection = "down";
			tile[327].collision = true;
			
			tile[328] = new Tile();
			tile[328].image = ImageIO.read(getClass().getResourceAsStream("/tiles/328.png"));
			tile[328].collisionDirection = "up";
			tile[328].collision = true;
			
			tile[329] = new Tile();
			tile[329].image = ImageIO.read(getClass().getResourceAsStream("/tiles/329.png"));
			tile[329].collisionDirection = "up";
			tile[329].collision = true;
			
			tile[330] = new Tile();
			tile[330].image = ImageIO.read(getClass().getResourceAsStream("/tiles/330.png"));
			tile[330].collisionDirection = "up";
			tile[330].collision = true;
			
			tile[331] = new Tile();
			tile[331].image = ImageIO.read(getClass().getResourceAsStream("/tiles/331.png"));
			tile[331].collisionDirection = "right";
			tile[331].collision = true;
			
			tile[332] = new Tile();
			tile[332].image = ImageIO.read(getClass().getResourceAsStream("/tiles/332.png"));
			tile[332].collisionDirection = "right";
			tile[332].collision = true;
			
			tile[333] = new Tile();
			tile[333].image = ImageIO.read(getClass().getResourceAsStream("/tiles/333.png"));
			tile[333].collisionDirection = "right";
			tile[333].collision = true;
			
			tile[334] = new Tile();
			tile[334].image = ImageIO.read(getClass().getResourceAsStream("/tiles/334.png"));
			tile[334].collisionDirection = "left";
			tile[334].collision = true;
			
			tile[335] = new Tile();
			tile[335].image = ImageIO.read(getClass().getResourceAsStream("/tiles/335.png"));
			tile[335].collisionDirection = "left";
			tile[335].collision = true;
			
			tile[336] = new Tile();
			tile[336].image = ImageIO.read(getClass().getResourceAsStream("/tiles/336.png"));
			tile[336].collisionDirection = "left";
			tile[336].collision = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setup(int index, boolean collision) {
		tile[index] = new Tile();
		String imageName = index + "";
		while (imageName.length() < 3) imageName = "0" + imageName;
		try {
			tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
			tile[index].collision = collision;
			if (collision) {
				tile[index].collisionDirection = "all";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadMap(String filePath, int map) {
		try {
			InputStream is = getClass().getResourceAsStream(filePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				
				String line = br.readLine();
				
				while(col < gp.maxWorldCol) {
					String numbers[] = line.split(" ");
					
					int num = Integer.parseInt(numbers[col]);
					
					mapTileNum[map][col][row] = num;
					col++;
				}
				if (col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g2) {
		int worldCol = 0;
		int worldRow = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
			
			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
					worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
					worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
					worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
				g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			}
			
			worldCol++;
			
			if (worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
	
	public ArrayList<Integer> getWaterTiles() {
		ArrayList<Integer> result = new ArrayList<>();
		result.add(3);
		for (int i = 24; i < 37; i++) {
			result.add(i);
		}
		for (int i = 313; i < 325; i++) {
			result.add(i);
		}
		
		return result;
	}
}
