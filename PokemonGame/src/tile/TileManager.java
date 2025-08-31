package tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import overworld.GamePanel;

public class TileManager {
	GamePanel gp;
	public Tile[] tile;
	public int[][][] mapTileNum;
	public boolean[] canFly;
	public boolean[] isCave;
	
	public static Rectangle[] collisionRectangles;
	public static String[] mapNames = new String[GamePanel.MAX_MAP];
	public List<Integer> lavaMaps = new ArrayList<>();
	
	private static final int OVER = -1;
	
	public static final int FULL = 0; // default, shouldn't ever be explicitly used
	public static final int TOP_HALF = 1;
	public static final int BOTTOM_HALF = 2;
	public static final int LEFT_HALF = 3;
	public static final int RIGHT_HALF = 4;
	public static final int TOP_TWO_THIRDS = 5;
	public static final int BOTTOM_TWO_THIRDS = 6;
	public static final int LEFT_TWO_THIRDS = 7;
	public static final int RIGHT_TWO_THIRDS = 8;
	public static final int BOTTOM_LEFT_CORNER = 9;
	public static final int BOTTOM_RIGHT_CORNER = 10;
	public static final int TOP_THREE_FOURTHS = 11;
	public static final int BOTTOM_THREE_FOURTHS = 12;
	public static final int TOP_FOURTH = 13;
	public static final int BOTTOM_FOURTH = 14;
	public static final int LEFT_FOURTH = 15;
	public static final int RIGHT_FOURTH = 16;
	public static final int TOP_RIGHT_SKINNY_CORNER = 17;
	public static final int TOP_LEFT_SKINNY_CORNER = 18;
	
	public TileManager(GamePanel gp) {
		this.gp = gp;
		tile = new Tile[1200];
		mapTileNum = new int[GamePanel.MAX_MAP][gp.maxWorldCol][gp.maxWorldRow];
		canFly = new boolean[GamePanel.MAX_MAP];
		isCave = new boolean[GamePanel.MAX_MAP];
		
		setupCollisionRectangles();
		
		getTileImage();
		loadMap("/maps/map01.txt", 0, true);
		loadMap("/maps/pc.txt", 1, true);
		loadMap("/maps/mart.txt", 2, true);
		loadMap("/maps/gate01.txt", 3, true);
		loadMap("/maps/map02.txt", 4, true);
		loadMap("/maps/pc.txt", 5, true);
		loadMap("/maps/mart.txt", 6, true);
		loadMap("/maps/warehouse01.txt", 7, true);
		loadMap("/maps/warehouse01B.txt", 8, true);
		loadMap("/maps/gym01.txt", 9, true);
		loadMap("/maps/gate02.txt", 10, true);
		loadMap("/maps/map03.txt", 11, true);
		loadMap("/maps/gate01.txt", 12, true);
		loadMap("/maps/map04.txt", 13, true);
		loadMap("/maps/energy01.txt", 14, true);
		loadMap("/maps/energy01B.txt", 15, true);
		loadMap("/maps/energy02.txt", 16, true);
		loadMap("/maps/office01.txt", 17, true);
		loadMap("/maps/office02.txt", 18, true);
		loadMap("/maps/pc.txt", 19, true);
		loadMap("/maps/mart.txt", 20, true);
		loadMap("/maps/gym02.txt", 21, true);
		loadMap("/maps/map05.txt", 22, true);
		loadMap("/maps/gate01.txt", 23, true);
		loadMap("/maps/splinkty01A.txt", 24, true);
		loadMap("/maps/splinkty02B.txt", 25, true);
		loadMap("/maps/splinkty03B.txt", 26, true);
		loadMap("/maps/splinkty03A.txt", 27, true);
		loadMap("/maps/map06.txt", 28, true);
		loadMap("/maps/pc.txt", 29, true);
		loadMap("/maps/market01.txt", 30, true);
		loadMap("/maps/gym03.txt", 31, true);
		loadMap("/maps/house01.txt", 32, true);
		loadMap("/maps/map07.txt", 33, true);
		loadMap("/maps/gate02.txt", 34, true);
		loadMap("/maps/electric01.txt", 35, true);
		loadMap("/maps/map08.txt", 36, true);
		loadMap("/maps/gate02.txt", 37, true);
		loadMap("/maps/map09.txt", 38, true);
		loadMap("/maps/pc.txt", 39, true);
		loadMap("/maps/supermarket02.txt", 40, true);
		loadMap("/maps/school01.txt", 41, true);
		loadMap("/maps/radio_tower01.txt", 42, true);
		loadMap("/maps/radio_tower02.txt", 43, true);
		loadMap("/maps/gym04.txt", 44, true);
		loadMap("/maps/supermarket01.txt", 45, true);
		loadMap("/maps/house01.txt", 46, true);
		loadMap("/maps/house01.txt", 47, true);
		loadMap("/maps/house02.txt", 48, true);
		loadMap("/maps/house01.txt", 49, true);
		loadMap("/maps/house01.txt", 50, true);
		loadMap("/maps/house01.txt", 51, true); // new minnow town
		loadMap("/maps/lab01.txt", 52, true);
		loadMap("/maps/house01.txt", 53, true); // frenco berry shoppe
		loadMap("/maps/house01.txt", 54, true);
		loadMap("/maps/house01.txt", 55, true); // bananaville town
		loadMap("/maps/house01.txt", 56, true);
		loadMap("/maps/house01.txt", 57, true); // poppy grove
		loadMap("/maps/house01.txt", 58, true);
		loadMap("/maps/house01.txt", 59, true); // sicab city
		loadMap("/maps/control.txt", 60, true);
		loadMap("/maps/house01.txt", 61, true); // kleine village
		loadMap("/maps/house01.txt", 62, true); // schrice city
		loadMap("/maps/house01.txt", 63, true);
		loadMap("/maps/house01.txt", 64, true);
		loadMap("/maps/house01.txt", 65, true);
		loadMap("/maps/house01.txt", 66, true);
		loadMap("/maps/house01.txt", 67, true);
		loadMap("/maps/house01.txt", 68, true);
		loadMap("/maps/house01.txt", 69, true);
		loadMap("/maps/house01.txt", 70, true);
		loadMap("/maps/house01.txt", 71, true);
		loadMap("/maps/house01.txt", 72, true);
		loadMap("/maps/house01.txt", 73, true);
		loadMap("/maps/gate01.txt", 74, true);
		loadMap("/maps/gate02.txt", 75, true);
		loadMap("/maps/gate01.txt", 76, true);
		loadMap("/maps/map10.txt", 77, true);
		loadMap("/maps/mindagan01.txt", 78, true);
		loadMap("/maps/gate01.txt", 79, true);
		loadMap("/maps/map11.txt", 80, true); // shadow ravine 1
		loadMap("/maps/gate01.txt", 81, true);
		loadMap("/maps/gate02.txt", 82, true);
		loadMap("/maps/map12.txt", 83, true); // shadow ravine 1A
		loadMap("/maps/gate02.txt", 84, true);
		loadMap("/maps/map13.txt", 85, true);
		loadMap("/maps/pc.txt", 86, true);
		loadMap("/maps/mart.txt", 87, true);
		loadMap("/maps/house01.txt", 88, true);
		loadMap("/maps/house02.txt", 89, true); // pawn shop
		loadMap("/maps/shadow0.txt", 90, true);
		loadMap("/maps/gym05.txt", 91, true);
		loadMap("/maps/pphouse.txt", 92, true);
		loadMap("/maps/house01.txt", 93, true); // move deletor
		loadMap("/maps/house01.txt", 94, true); // gift e/s pokemon
		loadMap("/maps/electric0.txt", 95, true);
		loadMap("/maps/electric-1.txt", 96, true);
		loadMap("/maps/electric-2.txt", 97, true);
		loadMap("/maps/electric-3.txt", 98, true);
		loadMap("/maps/electricH.txt", 99, true);
		loadMap("/maps/shadowH.txt", 100, true);
		loadMap("/maps/shadow-1.txt", 101, true);
		loadMap("/maps/shadow-2.txt", 102, true);
		loadMap("/maps/shadow-3.txt", 103, true);
		loadMap("/maps/tn_base.txt", 104, true);
		loadMap("/maps/shadow_path.txt", 105, true);
		loadMap("/maps/gate01.txt", 106, true);
		loadMap("/maps/map14.txt", 107, true);
		loadMap("/maps/gate01.txt", 108, true);
		loadMap("/maps/map15.txt", 109, true);
		loadMap("/maps/map16.txt", 110, true);
		loadMap("/maps/pc.txt", 111, true);
		loadMap("/maps/market01.txt", 112, true);
		loadMap("/maps/gym06.txt", 113, true);
		loadMap("/maps/gate02.txt", 114, true);
		loadMap("/maps/map17.txt", 115, true);
		loadMap("/maps/gate02.txt", 116, true);
		loadMap("/maps/mindagan0.txt", 117, true);
		loadMap("/maps/house01.txt", 118, true);
		loadMap("/maps/map18.txt", 119, true);
		loadMap("/maps/gate02.txt", 120, true);
		loadMap("/maps/radio_tower03.txt", 121, true); // sicab city tower thing
		loadMap("/maps/radio_tower02.txt", 122, true);
		loadMap("/maps/gate01.txt", 123, true);
		loadMap("/maps/map19.txt", 124, true);
		loadMap("/maps/pc.txt", 125, true);
		loadMap("/maps/mart.txt", 126, true);
		loadMap("/maps/casino.txt", 127, true);
		loadMap("/maps/gym07.txt", 128, true);
		loadMap("/maps/prize.txt", 129, true);
		loadMap("/maps/house02.txt", 130, true);
		loadMap("/maps/house03.txt", 131, true);
		loadMap("/maps/supermarket01.txt", 132, true);
		loadMap("/maps/supermarket02.txt", 133, true);
		loadMap("/maps/geologyB.txt", 134, true);
		loadMap("/maps/radio_tower04.txt", 135, true);
		loadMap("/maps/radio_tower02.txt", 136, true);
		loadMap("/maps/st_joseph01A.txt", 137, true);
		loadMap("/maps/st_joseph02A.txt", 138, true);
		loadMap("/maps/st_joseph02B.txt", 139, true);
		loadMap("/maps/st_joseph03A.txt", 140, true);
		loadMap("/maps/st_joseph03B.txt", 141, true);
		loadMap("/maps/st_joseph04A.txt", 142, true);
		loadMap("/maps/st_joseph04B.txt", 143, true);
		loadMap("/maps/map20.txt", 144, true);
		loadMap("/maps/splinkty02A.txt", 145, true);
		loadMap("/maps/splinkty01B.txt", 146, true);
		loadMap("/maps/splinktyH.txt", 147, true);
		loadMap("/maps/splinkty04A.txt", 148, true);
		loadMap("/maps/splinkty05A.txt", 149, false);
		loadMap("/maps/shadow_cavern.txt", 150, true);
		loadMap("/maps/gate01.txt", 151, true);
		loadMap("/maps/map21.txt", 152, true);
		loadMap("/maps/pc.txt", 153, true);
		loadMap("/maps/mart.txt", 154, true);
		loadMap("/maps/house01.txt", 155, true);
		loadMap("/maps/house01.txt", 156, true);
		loadMap("/maps/house01.txt", 157, true);
		loadMap("/maps/house04.txt", 158, true);
		loadMap("/maps/gym08.txt", 159, false);
		loadMap("/maps/space01.txt", 160, false);
		loadMap("/maps/post.txt", 161, true);
		loadMap("/maps/lab03.txt", 162, true);
		loadMap("/maps/school02.txt", 163, true);
		loadMap("/maps/school03.txt", 164, true);
		loadMap("/maps/school04.txt", 165, true);
		loadMap("/maps/icy_path.txt", 166, true);
		loadMap("/maps/gate02.txt", 167, true);
		loadMap("/maps/gelb_cavern.txt", 168, true);
		loadMap("/maps/house01.txt", 169, true);
		loadMap("/maps/house01.txt", 170, true);
		loadMap("/maps/house01.txt", 171, true);
		loadMap("/maps/house01.txt", 172, true);
		loadMap("/maps/house01.txt", 173, true);
		loadMap("/maps/house01.txt", 174, true);
		loadMap("/maps/house01.txt", 175, true);
		loadMap("/maps/house01.txt", 176, true);
		loadMap("/maps/house01.txt", 177, true);
		loadMap("/maps/lab03.txt", 178, true);
		loadMap("/maps/gate01.txt", 179, true);
		loadMap("/maps/gate02.txt", 180, true);
		loadMap("/maps/house01.txt", 181, true);
		loadMap("/maps/house01.txt", 182, true);
		loadMap("/maps/house01.txt", 183, true);
		loadMap("/maps/house01.txt", 184, true);
		loadMap("/maps/house01.txt", 185, true);
		loadMap("/maps/house01.txt", 186, true);
		loadMap("/maps/house01.txt", 187, true);
		loadMap("/maps/house01.txt", 188, true);
		loadMap("/maps/gate01.txt", 189, true);
		loadMap("/maps/at_path.txt", 190, true);
		loadMap("/maps/at01.txt", 191, false);
		loadMap("/maps/at02.txt", 192, false);
		loadMap("/maps/at03.txt", 193, false);
		loadMap("/maps/at04.txt", 194, false);
		loadMap("/maps/at05.txt", 195, false);
		loadMap("/maps/at06.txt", 196, false);
		loadMap("/maps/chasm01.txt", 197, false);
		loadMap("/maps/chasm02.txt", 198, false);
		loadMap("/maps/chasm03.txt", 199, false);
		loadMap("/maps/chasm04.txt", 200, false);
		loadMap("/maps/chasm05.txt", 201, false);
		loadMap("/maps/chasm06.txt", 202, false);
		loadMap("/maps/chasm02_iceblocks.txt", 203, false);
		loadMap("/maps/house01.txt", 204, true);
		loadMap("/maps/house05.txt", 205, true);
		loadMap("/maps/space02.txt", 206, false);
		loadMap("/maps/space03.txt", 207, false);
		loadMap("/maps/mystery_peak.txt", 208, false);
		loadMap("/maps/mystery_peakB.txt", 209, false);
	}
	
	private void setupCollisionRectangles() {
		collisionRectangles = new Rectangle[19];
		collisionRectangles[FULL] = new Rectangle(0, 0, gp.tileSize, gp.tileSize); // full tile
		collisionRectangles[TOP_HALF] = new Rectangle(0, 0, gp.tileSize, gp.tileSize / 2); // top half
		collisionRectangles[BOTTOM_HALF] = new Rectangle(0, gp.tileSize / 2, gp.tileSize, gp.tileSize / 2); // bottom half
		collisionRectangles[LEFT_HALF] = new Rectangle(0, 0, gp.tileSize / 2, gp.tileSize); // left half
		collisionRectangles[RIGHT_HALF] = new Rectangle(gp.tileSize / 2, 0, gp.tileSize / 2, gp.tileSize); // right half
		collisionRectangles[TOP_TWO_THIRDS] = new Rectangle(0, 0, gp.tileSize, gp.tileSize * 2 / 3); // top 2/3
		collisionRectangles[BOTTOM_TWO_THIRDS] = new Rectangle(0, gp.tileSize / 3, gp.tileSize, gp.tileSize * 2 / 3); // bottom 2/3
		collisionRectangles[LEFT_TWO_THIRDS] = new Rectangle(0, 0, gp.tileSize * 2 / 3, gp.tileSize); // left 2/3
		collisionRectangles[RIGHT_TWO_THIRDS] = new Rectangle(gp.tileSize / 3, 0, gp.tileSize * 2 / 3, gp.tileSize); // right 2/3
		collisionRectangles[BOTTOM_LEFT_CORNER] = new Rectangle(0, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize / 2); // bottom left corner
		collisionRectangles[BOTTOM_RIGHT_CORNER] = new Rectangle(gp.tileSize / 2, gp.tileSize / 2, gp.tileSize / 2, gp.tileSize / 2); // bottom right corner
		collisionRectangles[TOP_THREE_FOURTHS] = new Rectangle(0, 0, gp.tileSize, gp.tileSize * 3 / 4); // top 3/4
		collisionRectangles[BOTTOM_THREE_FOURTHS] = new Rectangle(0, gp.tileSize / 4, gp.tileSize, gp.tileSize * 3 / 4); // bottom 3/4
		collisionRectangles[TOP_FOURTH] = new Rectangle(0, 0, gp.tileSize, gp.tileSize / 4); // top fourth
		collisionRectangles[BOTTOM_FOURTH] = new Rectangle(0, gp.tileSize * 3 / 4, gp.tileSize, gp.tileSize / 4); // bottom fourth
		collisionRectangles[LEFT_FOURTH] = new Rectangle(0, 0, gp.tileSize / 4, gp.tileSize); // top fourth
		collisionRectangles[RIGHT_FOURTH] = new Rectangle(gp.tileSize * 3 / 4, 0, gp.tileSize / 4, gp.tileSize); // bottom fourth
		collisionRectangles[TOP_RIGHT_SKINNY_CORNER] = new Rectangle(gp.tileSize * 3 / 4, 0, gp.tileSize / 4, gp.tileSize / 2); // bottom left corner
		collisionRectangles[TOP_LEFT_SKINNY_CORNER] = new Rectangle(0, 0, gp.tileSize / 4, gp.tileSize / 2); // bottom right corner
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
		setup(62, false, OVER);
		setup(63, false, OVER);
		setup(64, false, OVER);
		setup(65, false, OVER);
		setup(66, false, OVER);
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
		setup(104, true, LEFT_TWO_THIRDS);
		setup(105, false);
		setup(106, true);
		setup(107, true);
		setup(108, true);
		setup(109, true);
		setup(110, true);
		setup(111, true);
		setup(112, true);
		setup(113, true);
		setup(114, false, OVER);
		setup(115, false, OVER);
		setup(116, false, OVER);
		setup(117, true, LEFT_HALF, OVER);
		setup(118, false, OVER);
		setup(119, true, RIGHT_HALF, OVER);
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
		setup(132, true, LEFT_HALF);
		setup(133, false);
		setup(134, true, RIGHT_HALF);
		setup(135, true);
		setup(136, false);
		setup(137, false);
		setup(138, false);
		setup(139, true);
		setup(140, true);
		setup(141, true);
		setup(142, true);
		setup(143, true, LEFT_HALF);
		setup(144, true, RIGHT_HALF);
		setup(145, true);
		setup(146, false, OVER);
		setup(147, false, OVER);
		setup(148, false, OVER);
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
		setup(180, true, TOP_HALF);
		setup(181, true, TOP_HALF);
		setup(182, true, RIGHT_HALF);
		setup(183, false);
		setup(184, true, LEFT_HALF);
		setup(185, true, TOP_HALF);
		setup(186, true, OVER);
		setup(187, true, OVER);
		setup(188, true, OVER);
		setup(189, false, OVER);
		setup(190, true, OVER);
		setup(191, true);
		setup(192, true, OVER);
		setup(193, false, OVER);
		setup(194, false, OVER);
		setup(195, true, TOP_HALF, OVER);
		setup(196, true);
		setup(197, true, TOP_HALF, OVER);
		setup(198, false, OVER);
		setup(199, false);
		setup(200, false, OVER);
		setup(201, true);
		setup(202, false, OVER);
		setup(203, false);
		setup(204, false);
		setup(205, false);
		setup(206, true);
		setup(207, false);
		setup(208, false);
		
		setup(209, false, OVER);
		setup(210, false, OVER);
		setup(211, false, OVER);
		setup(212, false, OVER);
		setup(213, false, OVER);
		setup(214, false, OVER);
		setup(215, false, OVER);
		setup(216, false, OVER);
		setup(217, false, OVER);
		setup(218, false, OVER);
		setup(219, false, OVER);
		setup(220, false, OVER);
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
		setup(281, false, OVER);
		setup(282, true);
		setup(283, false);
		setup(284, true);
		setup(285, true, TOP_FOURTH);
		setup(286, true, TOP_FOURTH);
		setup(287, true, TOP_FOURTH);
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

		setup(373, false);
		setup(374, false);
		setup(375, false);
		setup(376, false);
		setup(377, false);
		setup(378, false);
		
		setup(379, true);
		setup(380, true);
		setup(381, true);
		setup(382, true);
		setup(383, true);
		setup(384, true);
		setup(385, true);
		setup(386, true);
		setup(387, true);
		setup(388, true);
		setup(389, true);
		setup(390, true);
		
		setup(391, false);
		setup(392, false);
		setup(393, false);
		
		setup(394, false, OVER);
		setup(395, false, OVER);
		setup(396, false, OVER);
		setup(397, true);
		setup(398, true);
		setup(399, true);
		setup(400, true);
		setup(401, true);
		setup(402, true);
		setup(403, true);
		setup(404, true);
		setup(405, true);
		setup(406, false);
		setup(407, true);
		setup(408, true);
		setup(409, true);
		setup(410, true, TOP_HALF);
		setup(411, true, TOP_HALF);
		setup(412, true, TOP_HALF);
		
		setup(413, true);
		setup(414, false, OVER);
		
		setup(415, false, OVER);
		setup(416, false, OVER);
		setup(417, false, OVER);
		setup(418, true);
		setup(419, true);
		setup(420, true);
		setup(421, true);
		setup(422, true);
		setup(423, true);
		setup(424, true);
		setup(425, true);
		setup(426, false);
		setup(427, true);
		setup(428, true);
		setup(429, true);
		setup(430, false, OVER);
		setup(431, false, OVER);
		setup(432, false, OVER);
		setup(433, true, TOP_HALF);
		setup(434, false, OVER);
		setup(435, false, OVER);
		setup(436, false, OVER);
		setup(437, true);
		setup(438, true);
		setup(439, true);
		setup(440, true);
		setup(441, true);
		setup(442, true);
		setup(443, true);
		setup(444, false);
		setup(445, true);
		setup(446, true);
		setup(447, true);
		setup(448, true);
		setup(449, true);
		setup(450, true);
		setup(451, true);
		setup(452, true);
		setup(453, true);
		setup(454, true);
		setup(455, true);
		setup(456, true);
		setup(457, true);
		setup(458, true);
		setup(459, true);
		setup(460, true);
		setup(461, true, OVER);
		setup(462, true, OVER);
		setup(463, true, OVER);
		setup(464, false, OVER);
		setup(465, true);
		setup(466, false, OVER);
		setup(467, true, OVER);
		setup(468, true, OVER);
		setup(469, true, OVER);
		setup(470, true, OVER);
		setup(471, true, OVER);
		setup(472, true, OVER);
		setup(473, true, OVER);
		setup(474, true, OVER);
		setup(475, true, OVER);
		setup(476, true, OVER);
		setup(477, true, RIGHT_HALF, OVER);
		setup(478, true, LEFT_HALF, OVER);
		setup(479, true);
		setup(480, true);
		setup(481, true, TOP_FOURTH);
		setup(482, true, TOP_FOURTH);
		setup(483, false);
		setup(484, true);
		setup(485, false, OVER);
		setup(486, false, OVER);
		setup(487, false, OVER);
		setup(488, true);
		setup(489, true);
		setup(490, true);
		setup(491, true);
		setup(492, true);
		setup(493, true);
		setup(494, true);
		setup(495, true);
		setup(496, true);
		setup(497, true);
		setup(498, true);
		setup(499, true);
		setup(500, true);
		setup(501, true);
		setup(502, true);
		setup(503, true);
		setup(504, true);
		setup(505, false);
		setup(506, false);
		setup(507, false);
		setup(508, false);
		setup(509, true);
		setup(510, true);
		setup(512, true);
		setup(513, false);
		setup(514, true, TOP_FOURTH);
		setup(515, true);
		setup(516, false);
		setup(517, false);
		setup(518, true);
		setup(519, false);
		setup(520, true);
		setup(521, true);
		setup(522, true);
		setup(523, true);
		setup(524, true);
		setup(525, true);
		setup(526, true);
		setup(527, true);
		setup(528, true);
		setup(529, true);
		setup(530, true);
		setup(531, true);
		setup(532, true);
		setup(533, true);
		setup(534, true);
		setup(535, true);
		setup(536, true);
		setup(537, true);
		setup(538, true);
		setup(539, true);
		setup(540, true);
		setup(541, true);
		setup(542, true);
		setup(543, true);
		setup(544, true);
		setup(545, true);
		setup(546, true);
		setup(547, true);
		setup(548, true);
		setup(549, true);
		setup(550, true);
		setup(551, true);
		setup(552, true);
		setup(553, true);
		setup(554, true);
		setup(555, true);
		setup(556, true, TOP_FOURTH);
		setup(557, true, TOP_FOURTH);
		setup(558, true);
		setup(559, true);
		setup(560, true);
		setup(561, true);
		setup(562, true);
		setup(563, true);
		setup(564, true);
		setup(565, true, TOP_HALF);
		setup(566, true, TOP_HALF);
		setup(567, true, RIGHT_HALF);
		setup(568, false);
		setup(569, true, LEFT_HALF);
		setup(570, true, TOP_HALF);
		setup(571, true);
		setup(572, true);
		setup(573, true);
		setup(574, true);
		setup(575, true);
		
		setup(576, false, OVER);
		setup(577, false, OVER);
		setup(578, false, OVER);
		setup(579, false, OVER);
		setup(580, false, OVER);
		setup(581, true);
		setup(582, true);
		setup(583, true);
		setup(584, true);
		setup(585, true);
		setup(586, true);
		setup(587, true);
		setup(588, true);
		setup(589, true);
		setup(590, true);
		setup(591, true);
		setup(592, true);
		setup(593, false);
		setup(594, true);
		setup(595, true);
		
		setup(596, false);
		setup(597, true);
		setup(598, true);
		setup(599, true);
		setup(600, true);
		setup(601, true);
		setup(602, true, TOP_HALF);
		setup(603, true);
		setup(604, true, TOP_HALF);
		setup(605, true);
		setup(606, true);
		setup(607, true);
		setup(608, true);
		setup(609, true);
		setup(610, true);
		setup(611, false, OVER);
		setup(612, false, OVER);
		setup(613, true);
		setup(614, true);
		setup(615, true, TOP_HALF);
		setup(616, true, TOP_HALF);
		setup(617, false);
		setup(618, false);
		setup(619, true);
		setup(620, true);
		setup(621, false);
		setup(622, true);
		setup(623, true);
		setup(624, true);
		setup(625, false, OVER);
		setup(626, false, OVER);
		setup(627, true);
		setup(628, true);
		setup(629, false);
		setup(630, false);
		setup(631, false, OVER);
		setup(632, true);
		setup(633, false);
		setup(634, false);
		
		setup(635, true);
		setup(636, true);
		setup(637, true);
		setup(638, true);
		setup(639, true);
		setup(640, true);
		setup(641, true);
		setup(642, true);
		setup(643, false);
		setup(644, true, BOTTOM_FOURTH);
		setup(645, true, BOTTOM_FOURTH);
		setup(646, true, BOTTOM_FOURTH);
		setup(647, true);
		setup(648, true);
		setup(649, true);
		setup(650, false);
		setup(651, false);
		setup(652, false);
		setup(653, false);
		setup(654, false);
		setup(655, false);
		setup(656, false);
		setup(657, false);
		setup(658, false);
		setup(659, false);
		setup(660, false);
		setup(661, false);
		setup(662, false);
		setup(663, false, OVER);
		setup(664, true);
		setup(665, true);
		setup(666, true);
		setup(667, true);
		setup(668, true);
		setup(669, true);
		setup(670, true);
		setup(671, true);
		setup(672, true);
		setup(673, true);
		setup(674, false);
		setup(675, true);
		setup(676, false);
		setup(677, false);
		setup(678, false, OVER);
		setup(679, false, OVER);
		setup(680, false, OVER);
		setup(681, true);
		setup(682, true);
		setup(683, true);
		setup(684, true);
		setup(685, true);
		setup(686, true);
		setup(687, true);
		setup(688, false);
		setup(689, true);
		setup(690, true);
		setup(691, true);
		setup(692, false);
		setup(693, false);
		setup(694, false);
		setup(695, true, BOTTOM_RIGHT_CORNER);
		setup(696, true, BOTTOM_LEFT_CORNER);
		setup(697, true);
		setup(698, true);
		setup(699, false, OVER);
		setup(700, true);
		setup(701, true);
		setup(702, true);
		setup(703, true);
		setup(704, true);
		setup(705, true);
		setup(706, true);
		setup(707, true);
		setup(708, true);
		setup(709, true);
		setup(710, true);
		setup(711, true, TOP_HALF);
		setup(712, true, TOP_HALF);
		setup(713, true, TOP_HALF);
		setup(714, false);
		setup(715, false);
		setup(716, false);
		setup(717, false);
		setup(718, true);
		setup(719, true);
		setup(720, true);
		setup(721, true);
		setup(722, true);
		setup(723, true);
		setup(724, true, TOP_HALF);
		setup(725, true, TOP_HALF);
		setup(726, true);
		setup(727, true);
		setup(728, true);
		setup(729, true);
		setup(730, true);
		setup(731, true);
		setup(732, true);
		setup(733, true);
		setup(734, false, OVER);
		setup(735, true);
		setup(736, true);
		setup(737, false, OVER);
		setup(738, true);
		setup(739, true);
		setup(740, true);
		setup(741, true);
		setup(742, false, OVER);
		setup(743, false, OVER);
		setup(744, false, OVER);
		setup(745, false, OVER);
		setup(746, false);
		
		setup(747, true);
		setup(748, true);
		setup(749, true);
		setup(750, true);
		setup(751, true);
		setup(752, true);
		setup(753, true);
		setup(754, false);
		setup(755, true);
		setup(756, false, OVER);
		setup(757, true);
		setup(758, false);
		setup(759, false);
		setup(760, false);
		setup(761, false, OVER);
		setup(762, false, OVER);
		setup(763, false, OVER);
		setup(764, true);
		setup(765, false, OVER);
		setup(766, false);
		setup(767, false);
		setup(768, false);
		setup(769, false);
		
		setup(770, true);
		setup(771, true);
		setup(772, true);
		setup(773, true, FULL, OVER);
		setup(774, true, FULL, OVER);
		setup(775, false, OVER);
		setup(776, false, OVER);
		setup(777, false, OVER);
		setup(778, false, OVER);
		setup(779, false, OVER);
		setup(780, false, OVER);
		setup(781, true, LEFT_TWO_THIRDS);
		setup(782, true, TOP_TWO_THIRDS);
		setup(783, true, RIGHT_TWO_THIRDS);
		
		setup(784, false);
		setup(785, true);
		setup(786, false);
		setup(787, true, RIGHT_TWO_THIRDS);
		setup(788, true);
		setup(789, false);
		setup(790, true);
		setup(791, true);
		setup(792, true);
		setup(793, true);
		setup(794, true);
		setup(795, true);
		setup(796, true);
		setup(797, true);
		setup(798, true);
		setup(799, true);
		setup(800, true);
		setup(801, true);
		setup(802, false);
		setup(803, false);
		setup(804, false);
		setup(805, false);
		setup(806, false);
		setup(807, false);
		setup(808, false);
		setup(809, false);
		setup(810, false);
		setup(811, false);
		setup(812, false);
		setup(813, false);
		setup(814, false);
		setup(815, false);
		setup(816, false);
		setup(817, false);
		setup(818, false);
		setup(819, false);
		setup(820, false);
		setup(821, false);
		setup(822, false);
		setup(823, false);
		setup(824, false);
		setup(825, false);
		setup(826, false);
		setup(827, true);
		setup(828, false);
		setup(829, false);
		setup(830, false, OVER);
		setup(831, false, OVER);
		setup(832, false, OVER);
		setup(833, true);
		setup(834, true, OVER);
		setup(835, true, OVER);
		setup(836, true);
		setup(837, true);
		setup(838, true);
		setup(839, true);
		setup(840, true);
		setup(841, true);
		setup(842, true);
		setup(843, true);
		setup(844, true);
		setup(845, true);
		setup(846, true);
		setup(847, true);
		setup(848, true);
		setup(849, true);
		setup(850, true);
		setup(851, true);
		setup(852, true);
		setup(853, true);
		setup(854, false);
		setup(855, false);
		setup(856, false);
		setup(857, false);
		setup(858, false);
		setup(859, false);
		setup(860, false);
		setup(861, false);
		setup(862, false);
		setup(863, false);
		setup(864, false);
		setup(865, false);
		setup(866, false);
		setup(867, false);
		setup(868, false);
		setup(869, false);
		setup(870, true);
		setup(871, true);
		setup(872, true);
		setup(873, true);
		setup(874, true);
		setup(875, true);
		setup(876, true);
		setup(877, true, OVER);
		setup(878, true, OVER);
		setup(879, true, OVER);
		setup(880, true, OVER);
		setup(881, true, OVER);
		setup(882, false, OVER);
		setup(883, false, OVER);
		setup(884, false, OVER);
		setup(885, false, OVER);
		setup(886, false, OVER);
		setup(887, false, OVER);
		setup(888, false, OVER);
		setup(889, false, OVER);
		setup(890, false, OVER);
		setup(891, false, OVER);
		setup(892, false, OVER);
		setup(893, false, OVER);
		setup(894, false, OVER);
		setup(895, true);
		setup(896, true);
		setup(897, true);
		setup(898, true);
		setup(899, true);
		setup(900, false, OVER);
		setup(901, false, OVER);
		setup(902, true);
		setup(903, true);
		setup(904, true);
		setup(905, true);
		setup(906, true);
		setup(907, false, OVER);
		setup(908, false, OVER);
		setup(909, true);
		setup(910, true);
		setup(911, true);
		setup(912, true);
		setup(913, true);
		setup(914, false, OVER);
		setup(915, true, RIGHT_FOURTH, OVER);
		setup(916, true);
		setup(917, true);
		setup(918, false);
		setup(919, true);
		setup(920, true);
		setup(921, true, LEFT_FOURTH, OVER);
		setup(922, true, TOP_RIGHT_SKINNY_CORNER);
		setup(923, true, TOP_HALF);
		setup(924, true, TOP_HALF);
		setup(925, false);
		setup(926, true, TOP_HALF);
		setup(927, true, TOP_HALF);
		setup(928, true, TOP_LEFT_SKINNY_CORNER);
		setup(929, true);
		setup(930, true);
		setup(931, true);
		setup(932, true);
		setup(933, true);
		setup(934, true);
		setup(935, true, TOP_HALF);

		setup(937, true, TOP_HALF);
		setup(938, true);
		setup(939, true);
		setup(940, true);
		setup(941, true);
		setup(942, true);
		
		setup(946, false);
		setup(947, true);
		setup(948, true);
		setup(949, true);
		setup(950, true);
		setup(951, false);
		setup(952, true);
		setup(953, false);
		setup(954, false);
		setup(955, false);

		setup(957, true);

		setup(959, true);
		setup(960, true);
		setup(961, true, TOP_FOURTH);
		setup(962, true);
		setup(963, true);

		setup(965, true);
		setup(966, true);
		setup(967, true);
		setup(968, true);
		setup(969, false);
		setup(970, true);
		setup(971, true);
		setup(972, true);
		setup(973, false);

		setup(986, false);

		setup(989, true);
		setup(990, true);
		setup(991, true);
		setup(992, true);
		setup(993, true, TOP_FOURTH);
		setup(994, true);
		setup(995, true);
		setup(996, true);
		setup(997, true);
		setup(998, true);
		setup(999, true);
		setup(1000, false);
		setup(1001, true);
		setup(1002, true);
		setup(1003, false);
		setup(1004, true);
		setup(1005, true);
		setup(1006, true);
		setup(1007, true);
		setup(1008, true);
		setup(1009, true);
		setup(1010, true);
		setup(1011, true);
		setup(1012, true);
		setup(1013, true);
		setup(1014, true);
		setup(1015, true);
		setup(1016, true);

		setup(1029, false);
		setup(1030, false);
		setup(1031, false);
		setup(1032, false);
		setup(1033, false);
		setup(1034, false);
		
		setup(1035, true);
		setup(1036, true, TOP_FOURTH);
		setup(1037, true);
		setup(1038, true);
		setup(1039, false);
		setup(1040, true);
		setup(1041, true);
		setup(1042, true);
		setup(1043, true);
		setup(1044, true);
		setup(1045, true);
		setup(1046, true);
		setup(1047, true);
		setup(1048, true);
		setup(1049, true);
		setup(1050, true);
		setup(1051, true);
		setup(1052, true);
		setup(1053, true);
		setup(1054, true, RIGHT_TWO_THIRDS);
		setup(1055, true, LEFT_TWO_THIRDS);
		setup(1056, true, TOP_TWO_THIRDS);
		setup(1057, true, TOP_TWO_THIRDS);
		setup(1058, true);
		setup(1059, true);
		setup(1060, false);
		setup(1061, true);
		setup(1062, true);
		setup(1063, true);
		setup(1064, true);
		setup(1065, true, TOP_FOURTH);
		setup(1066, true);
		setup(1067, true);
		setup(1068, true);
		setup(1069, false);
		setup(1070, true);
		setup(1071, true);
		setup(1072, true);
		setup(1073, true);
		setup(1074, true);
		setup(1075, true);
		setup(1076, true);
		setup(1077, true);
		setup(1078, true);
		setup(1079, false);
		setup(1080, false);
		setup(1081, false);
		setup(1082, false);
		setup(1083, false);
		setup(1084, false);
		setup(1085, false);
		setup(1086, false);
		setup(1087, false);
		setup(1088, false);
		setup(1089, false);
		setup(1090, false);
		setup(1091, true);
		setup(1092, false);
		setup(1093, true);
		
		setup(1094, false);
		setup(1095, false);
		setup(1096, false, OVER);
		setup(1097, false, OVER);
		setup(1098, false, OVER);
		setup(1099, true);
		setup(1100, true);
		setup(1101, true);
		setup(1102, true, TOP_THREE_FOURTHS);
		setup(1103, true, TOP_THREE_FOURTHS);
		setup(1104, true, TOP_THREE_FOURTHS);
		setup(1105, false, OVER);
		setup(1106, false, OVER);
		setup(1107, false, OVER);
		setup(1108, false, OVER);
		setup(1109, false, OVER);
		setup(1110, false, OVER);
		setup(1111, true);
		setup(1112, true);
		setup(1113, true);
		setup(1114, true);
		setup(1115, true);
		setup(1116, true);
		setup(1117, true);
		setup(1118, true);
		setup(1119, true, TOP_THREE_FOURTHS);
		setup(1120, true, TOP_THREE_FOURTHS);
		setup(1121, true, TOP_THREE_FOURTHS);
		setup(1122, true, TOP_THREE_FOURTHS);
		setup(1123, true, BOTTOM_HALF, OVER);
		setup(1124, true, BOTTOM_HALF, OVER);
		setup(1125, true);
		setup(1126, true);
		setup(1127, true);
		setup(1128, true);
		setup(1129, true, TOP_THREE_FOURTHS);
		setup(1130, true, TOP_THREE_FOURTHS);
		setup(1131, true);
		setup(1132, true);
		setup(1133, true);
//		setup(1134, true);
//		setup(1135, true);
//		setup(1136, true);
//		setup(1137, true);
//		setup(1138, true);
//		setup(1139, true);
//		setup(1140, true);
//		setup(1141, true);
//		setup(1142, true);
//		setup(1143, true);
//		setup(1144, true);
//		setup(1145, true);
//		setup(1146, true);
//		setup(1147, true);
//		setup(1148, true);
//		setup(1149, true);
//		setup(1150, true);
//		setup(1151, true);
//		setup(1152, true);
//		setup(1153, true);
//		setup(1154, true);
//		setup(1155, true);
//		setup(1156, true);
//		setup(1157, true);
//		setup(1158, true);
//		setup(1159, true);
//		setup(1160, true);
//		setup(1161, true);
//		setup(1162, true);
//		setup(1163, true);
//		setup(1164, true);
//		setup(1165, true);
//		setup(1166, true);
//		setup(1167, true);
//		setup(1168, true);
//		setup(1169, true);
//		setup(1170, true);
//		setup(1171, true);
//		setup(1172, true);
//		setup(1173, true);
//		setup(1174, true);
//		setup(1175, true);
//		setup(1176, true);
//		setup(1177, true);
//		setup(1178, true);
//		setup(1179, true);
//		setup(1180, true);
//		setup(1181, true);
//		setup(1182, true);
//		setup(1183, true);
//		setup(1184, true);
//		setup(1185, true);
//		setup(1186, true);
//		setup(1187, true);
//		setup(1188, true);
//		setup(1189, true);
//		setup(1190, true);
//		setup(1191, true);
//		setup(1192, true);
//		setup(1193, true);
//		setup(1194, true);
//		setup(1195, true);
//		setup(1196, true);
//		setup(1197, true);
//		setup(1198, true);
//		setup(1199, true);
//		setup(1200, true);

		try {
			tile[7] = new GrassTile();
			tile[7].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0007.png"));
			
			tile[235] = new CaveTile();
			tile[235].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0235.png"));
			
			tile[266] = new CaveTile();
			tile[266].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0266.png"));
			
			tile[363] = new GrassTile();
			tile[363].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0363.png"));
			
			tile[511] = new GrassTile();
			tile[511].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0511.png"));
			
			tile[936] = new CaveTile();
			tile[936].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0936.png"));
			
			tile[956] = new IceTile();
			tile[956].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0956.png"));
			
			tile[958] = new CaveTile();
			tile[958].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0958.png"));
			
			tile[964] = new CaveTile();
			tile[964].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0964.png"));
			
			tile[987] = new CaveTile();
			tile[987].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0987.png"));
			
			tile[988] = new CaveTile();
			tile[988].image = ImageIO.read(getClass().getResourceAsStream("/tiles/0988.png"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setupCliff(37, 2, new Tile());
		setupCliff(38, 2, new Tile());
		setupCliff(39, 2, new Tile());
		setupCliff(40, 1, new Tile());
		setupCliff(41, 1, new Tile());
		setupCliff(42, 1, new Tile());
		setupCliff(43, 4, new Tile());
		setupCliff(44, 4, new Tile());
		setupCliff(45, 4, new Tile());
		setupCliff(46, 3, new Tile());
		setupCliff(47, 3, new Tile());
		setupCliff(48, 3, new Tile());
		
		setupCliff(325, 2, new Tile());
		setupCliff(326, 2, new Tile());
		setupCliff(327, 2, new Tile());
		setupCliff(328, 1, new Tile());
		setupCliff(329, 1, new Tile());
		setupCliff(330, 1, new Tile());
		setupCliff(331, 4, new Tile());
		setupCliff(332, 4, new Tile());
		setupCliff(333, 4, new Tile());
		setupCliff(334, 3, new Tile());
		setupCliff(335, 3, new Tile());
		setupCliff(336, 3, new Tile());
		
		setupCliff(372, 2, new Tile());
		
		setupCliff(943, 3, new Tile());
		setupCliff(944, 4, new Tile());
		setupCliff(945, 2, new Tile());
		
		setupCliff(974, 2, new IceTile());
		setupCliff(975, 2, new IceTile());
		setupCliff(976, 2, new IceTile());
		setupCliff(977, 1, new IceTile());
		setupCliff(978, 1, new IceTile());
		setupCliff(979, 1, new IceTile());
		setupCliff(980, 3, new IceTile());
		setupCliff(981, 3, new IceTile());
		setupCliff(982, 3, new IceTile());
		setupCliff(983, 4, new IceTile());
		setupCliff(984, 4, new IceTile());
		setupCliff(985, 4, new IceTile());
		
		setupCliff(1017, 2, new Tile());
		setupCliff(1018, 2, new Tile());
		setupCliff(1019, 2, new Tile());
		setupCliff(1020, 1, new Tile());
		setupCliff(1021, 1, new Tile());
		setupCliff(1022, 1, new Tile());
		setupCliff(1023, 3, new Tile());
		setupCliff(1024, 3, new Tile());
		setupCliff(1025, 3, new Tile());
		setupCliff(1026, 4, new Tile());
		setupCliff(1027, 4, new Tile());
		setupCliff(1028, 4, new Tile());
	}
	
	private void setupCliff(int index, int collisionType, Tile gen) {
		tile[index] = gen;
		String imageName = index + "";
		while (imageName.length() < 4) imageName = "0" + imageName;
		try {
			tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
			String direction = "";
			switch (collisionType) {
			case 0:
				direction = "all";
				break;
			case 1:
				direction = "up";
				break;
			case 2:
				direction = "down";
				break;
			case 3:
				direction = "left";
				break;
			case 4:
				direction = "right";
				break;
			}
			tile[index].collision = true;
			tile[index].collisionDirection = direction;
			tile[index].collisionArea = collisionRectangles[collisionType];
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setup(int index, boolean collision) {
		setup(index, collision, FULL);
	}
	
	public void setup(int index, boolean collision, int collisionType) {
		setup(index, collision, collisionType, FULL);
	}
	
	public void setup(int index, boolean collision, int collisionType, int over) {
		tile[index] = new Tile();
		String imageName = index + "";
		while (imageName.length() < 4) imageName = "0" + imageName;
		try {
			tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
			tile[index].collision = collision;
			if (collision) {
				tile[index].collisionDirection = "all";
				tile[index].collisionArea = collisionRectangles[collisionType == OVER ? FULL : collisionType];
			}
			if (collisionType == OVER || over == OVER) {
				tile[index].drawAbove = true;
				try {
					tile[index].mask = ImageIO.read(getClass().getResourceAsStream("/masks/" + imageName + ".png"));
				} catch (IllegalArgumentException e) {
					System.out.println(imageName + " is null!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadMap(String filePath, int map, boolean canFly) {
		addName(filePath, map);
		boolean cave = false;
		boolean lava = false;
		List<Integer> lavaTiles = this.getLavaTiles();
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
					if (!cave && (tile[num] instanceof CaveTile || gp.inSpace(num))) cave = true;
					if (!lava && lavaTiles.contains(num)) {
						lava = true;
						lavaMaps.add(map);
					}
					
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
		this.canFly[map] = canFly;
		this.isCave[map] = cave;
	}
	
	public void draw(Graphics2D g2, boolean drawAbove) {
		int worldCol = 0;
		int worldRow = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNum[gp.currentMap][worldCol][worldRow];
			
			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			int screenX = worldX - gp.player.worldX + gp.player.screenX + gp.offsetX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY + gp.offsetY;
			
			if (worldX + gp.tileSize + gp.offsetX > gp.player.worldX - gp.player.screenX &&
				worldX - gp.tileSize + gp.offsetX < gp.player.worldX + gp.player.screenX &&
				worldY + gp.tileSize + gp.offsetY > gp.player.worldY - gp.player.screenY &&
				worldY - gp.tileSize + gp.offsetY < gp.player.worldY + gp.player.screenY) {
				
				if (tile[tileNum].mask == null) {
					if (tile[tileNum].drawAbove == drawAbove) {
						g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
					}
				} else {
					for (int y = 0; y < tile[tileNum].image.getHeight(); y++) {
						for (int x = 0; x < tile[tileNum].image.getWidth(); x++) {
							int maskPixel = tile[tileNum].mask.getRGB(x, y);
							int imagePixel = tile[tileNum].image.getRGB(x, y);
							
							if (drawAbove && maskPixel == Color.white.getRGB()) {
								g2.setColor(new Color(imagePixel));
								g2.fillRect(screenX + (x * gp.scale), screenY + (y * gp.scale), gp.scale, gp.scale);
							} else if (!drawAbove && maskPixel == Color.black.getRGB()) {
								g2.setColor(new Color(imagePixel));
								g2.fillRect(screenX + (x * gp.scale), screenY + (y * gp.scale), gp.scale, gp.scale);
							}
						}
					}
				}
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
		// spaceship water tiles
		result.add(461);
		result.add(462);
		result.add(463);
		result.add(467);
		result.add(468);
		result.add(469);
		result.add(470);
		result.add(471);
		result.add(472);
		result.add(473);
		result.add(474);
		result.add(475);
		result.add(476);
		return result;
	}
	
	public ArrayList<Integer> getLavaTiles() {
		ArrayList<Integer> result = new ArrayList<>();
		for (int i = 236; i < 249; i++) {
			result.add(i);
		}
		for (int i = 379; i < 391; i++) {
			result.add(i);
		}
		
		return result;
	}
	
	public void openGhostlyBluePortals() {
		int[] xCoords = new int[] {56, 56, 38, 69, 79, 43, 18, 20, 36, 50, 66, 52, 69, 32, 50, 54, 60, 64, 74, 76, 46, 47, 47, 47, 45, 50, 54, 55, 49, 45, 45, 49, 41};
		int[] yCoords = new int[] {81, 83, 90, 83, 63, 73, 39, 34, 48, 64, 54, 59, 31, 28, 20, 20, 17, 19, 22, 26, 53, 50, 47, 44, 41, 51, 48, 40, 37, 37, 30, 30, 48};
		for (int i = 0; i < xCoords.length; i++) {
			mapTileNum[107][xCoords[i]][yCoords[i]] = 483;
		}
	}
	
	public void addName(String name, int map) {
		name = name.replace("/maps/", "");
		name = name.replace(".txt", "");
		mapNames[map] = name;
	}
}
