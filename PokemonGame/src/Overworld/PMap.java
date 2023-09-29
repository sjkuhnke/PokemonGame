package Overworld;


import java.awt.Color;

import javax.swing.*;

import Swing.Move;
import Swing.Battle.JGradientButton;
import java.awt.Font;

public class PMap extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	GamePanel gp;
	
	public PMap(GamePanel gp) {
		this.gp = gp;
		
		boolean[] locations = gp.player.p.locations; // NMT, BVT, PG, SC, KV, PP, SRC, GT, FC, RC, IT, CC
		
		double xD = gp.player.worldX;
		xD /= gp.tileSize;
		int x = (int) Math.round(xD);
		
		double yD = gp.player.worldY;
		yD /= gp.tileSize;
		int y = (int) Math.round(yD);
		
		int loc = getLoc(gp.currentMap, x, y);
		
		setTitle("Map");
		    
	    // Initializing panel
	    setResizable(false);
	    setBounds(100, 100, 648, 494);

	    // Set the location of the Box in the center of the screen
	    setLocationRelativeTo(null);
	    getContentPane().setLayout(null);
	    
	    JButton NMT = new JButton("");
	    NMT.setToolTipText("New Minnow Town");
	    NMT.setBounds(336, 312, 26, 23);
	    if (locations[0]) {
	    	NMT.setBackground(Color.red);
	    	NMT.addActionListener(e -> {
	    		fly("New Minnow Town", 0, 90, 46);
	    	});
	    }
	    if (loc == 0) NMT.setBackground(Color.yellow);
	    getContentPane().add(NMT);
	    
	    JButton R22 = new JButton("");
	    R22.setBackground(new Color(153, 255, 102));
	    R22.setToolTipText("Route 22");
	    R22.setBounds(247, 319, 89, 10);
	    if (loc == 1) R22.setBackground(Color.yellow);
	    getContentPane().add(R22);
	    
	    JButton BVT = new JButton("");
	    BVT.setToolTipText("Bananaville Town");
	    BVT.setBounds(221, 312, 26, 23);
	    if (locations[1]) {
	    	BVT.setBackground(Color.red);
	    	BVT.addActionListener(e -> {
	    		fly(BVT.getToolTipText(), 0, 35, 45);
	    	});
	    }
	    if (loc == 2) BVT.setBackground(Color.yellow);
	    getContentPane().add(BVT);
	    
	    JButton R23 = new JButton("");
	    R23.setBackground(new Color(153, 255, 102));
	    R23.setToolTipText("Route 23");
	    R23.setBounds(229, 264, 10, 49);
	    if (loc == 3) R23.setBackground(Color.yellow);
	    getContentPane().add(R23);
	    
	    JButton PG = new JButton("");
	    PG.setToolTipText("Poppy Grove");
	    PG.setBounds(215, 234, 38, 31);
	    if (locations[2]) {
	    	PG.setBackground(Color.red);
	    	PG.addActionListener(e -> {
	    		fly(PG.getToolTipText(), 4, 69, 72);
	    	});
	    }
	    if (loc == 4) PG.setBackground(Color.yellow);
	    getContentPane().add(PG);
	    
	    JButton R24_1 = new JButton("");
	    R24_1.setToolTipText("Route 24 pt. 1");
	    R24_1.setBackground(new Color(153, 255, 102));
	    if (loc == 5) R24_1.setBackground(Color.yellow);
	    R24_1.setBounds(148, 242, 68, 13);
	    getContentPane().add(R24_1);
	    
	    JButton R24_2 = new JButton("");
	    R24_2.setToolTipText("Route 24 pt. 2");
	    R24_2.setBackground(new Color(153, 255, 102));
	    if (loc == 6) R24_2.setBackground(Color.yellow);
	    R24_2.setBounds(96, 254, 59, 12);
	    getContentPane().add(R24_2);
	    
	    JGradientButton GF = new JGradientButton("");
	    GF.setBackground(new Color(0, 128, 0));
	    GF.setToolTipText("Gelb Forest");
	    GF.setBounds(44, 242, 52, 44);
	    if (loc == 7) GF.setBackground(Color.yellow);
	    getContentPane().add(GF);
	    
	    JButton R44 = new JButton("");
	    R44.setBackground(new Color(153, 255, 102));
	    R44.setToolTipText("Route 44");
	    R44.setBounds(82, 285, 33, 13);
	    if (loc == 41) R44.setBackground(Color.yellow);
	    getContentPane().add(R44);
	    
	    JGradientButton DeepChasm = new JGradientButton("");
	    DeepChasm.setBackground(Color.DARK_GRAY);
	    DeepChasm.setToolTipText("Deep Chasm");
	    DeepChasm.setBounds(115, 285, 26, 23);
	    if (loc == 42) DeepChasm.setBackground(Color.yellow);
	    getContentPane().add(DeepChasm);
	    
	    JButton R37 = new JButton("");
	    R37.setToolTipText("Route 37");
	    R37.setBackground(new Color(153, 255, 102));
	    R37.setBounds(63, 286, 12, 49);
	    if (loc == 34) R37.setBackground(Color.yellow);
	    getContentPane().add(R37);
	    
	    JButton RC = new JButton("");
	    RC.setToolTipText("Rawwar City");
	    RC.setBounds(44, 334, 50, 38);
	    if (locations[9]) {
	    	RC.setBackground(Color.red);
	    	RC.addActionListener(e -> {
	    		fly(RC.getToolTipText(), 0, 35, 45); // TODO
	    	});
	    }
	    if (loc == 35) RC.setBackground(Color.yellow);
	    getContentPane().add(RC);
	    
	    JButton IT = new JButton("");
	    IT.setToolTipText("Iron Town");
	    IT.setBounds(51, 402, 33, 23);
	    if (locations[10]) {
	    	IT.setBackground(Color.red);
	    	IT.addActionListener(e -> {
	    		fly(IT.getToolTipText(), 0, 35, 45); // TODO
	    	});
	    }
	    if (loc == 39) IT.setBackground(Color.yellow);
	    getContentPane().add(IT);
	    
	    JButton R39 = new JButton("");
	    R39.setToolTipText("Route 39");
	    R39.setBackground(new Color(153, 255, 102));
	    R39.setBounds(63, 371, 10, 32);
	    if (loc == 38) R39.setBackground(Color.yellow);
	    getContentPane().add(R39);
	    
	    JButton R25 = new JButton("");
	    R25.setBackground(new Color(153, 255, 102));
	    R25.setToolTipText("Route 25");
	    R25.setBounds(63, 166, 12, 76);
	    if (loc == 8) R25.setBackground(Color.yellow);
	    getContentPane().add(R25);
	    
	    JButton SC = new JButton("");
	    SC.setToolTipText("Sicab City");
	    SC.setBounds(44, 129, 50, 38);
	    if (locations[3]) {
	    	SC.setBackground(Color.red);
	    	SC.addActionListener(e -> {
	    		fly(SC.getToolTipText(), 13, 35, 86);
	    	});
	    }
	    if (loc == 9) SC.setBackground(Color.yellow);
	    getContentPane().add(SC);
	    
	    JButton R26 = new JButton("");
	    R26.setBackground(new Color(153, 255, 102));
	    R26.setToolTipText("Route 26");
	    R26.setBounds(61, 64, 16, 66);
	    if (loc == 10) R26.setBackground(Color.yellow);
	    getContentPane().add(R26);
	    
	    JGradientButton MtSpl = new JGradientButton("");
	    MtSpl.setToolTipText("Mt. Splinkty");
	    MtSpl.setBackground(new Color(153, 102, 0));
	    MtSpl.setBounds(50, 41, 38, 23);
	    if (loc == 11) MtSpl.setBackground(Color.yellow);
	    getContentPane().add(MtSpl);
	    
	    JButton R27 = new JButton("");
	    R27.setToolTipText("Route 27");
	    R27.setBackground(new Color(153, 255, 102));
	    R27.setBounds(88, 47, 56, 10);
	    if (loc == 12) R27.setBackground(Color.yellow);
	    getContentPane().add(R27);
	    
	    JGradientButton PP = new JGradientButton("");
	    PP.setBackground(new Color(0, 128, 0));
	    PP.setToolTipText("Peaceful Park");
	    PP.setBounds(112, 102, 89, 105);
	    if (loc == 15) PP.setBackground(Color.yellow);
	    getContentPane().add(PP);
	    
	    JButton KV = new JButton("");
	    KV.setToolTipText("Kleine Village");
	    KV.setBounds(143, 41, 26, 23);
	    if (locations[4]) {
	    	KV.setBackground(Color.red);
	    	KV.addActionListener(e -> {
	    		fly(KV.getToolTipText(), 28, 76, 52);
	    	});
	    }
	    if (loc == 13) KV.setBackground(Color.yellow);
	    getContentPane().add(KV);
	    
	    JButton R41 = new JButton("");
	    R41.setToolTipText("Route 41");
	    R41.setBackground(new Color(153, 255, 102));
	    R41.setBounds(150, 63, 12, 39);
	    if (loc == 14) R41.setBackground(Color.yellow);
	    getContentPane().add(R41);
	    
	    JGradientButton ET = new JGradientButton("");
	    ET.setToolTipText("Electric Tunnel");
	    ET.setBackground(new Color(238, 232, 170));
	    ET.setBounds(215, 41, 68, 23);
	    if (loc == 18) ET.setBackground(Color.yellow);
	    getContentPane().add(ET);
	    
	    JButton R36_1 = new JButton("");
	    R36_1.setToolTipText("Route 36");
	    R36_1.setBackground(new Color(153, 255, 102));
	    R36_1.setBounds(229, 201, 10, 34);
	    if (loc == 16) R36_1.setBackground(Color.yellow);
	    getContentPane().add(R36_1);
	    
	    JButton R36_2 = new JButton("");
	    R36_2.setToolTipText("Route 36");
	    R36_2.setBackground(new Color(153, 255, 102));
	    R36_2.setBounds(201, 192, 38, 10);
	    if (loc == 16) R36_2.setBackground(Color.yellow);
	    getContentPane().add(R36_2);
	    
	    JButton R28 = new JButton("");
	    R28.setToolTipText("Route 28");
	    R28.setBackground(new Color(153, 255, 102));
	    R28.setBounds(168, 47, 48, 10);
	    if (loc == 17) R28.setBackground(Color.yellow);
	    getContentPane().add(R28);
	    
	    JButton R29 = new JButton("");
	    R29.setToolTipText("Route 29");
	    R29.setBackground(new Color(153, 255, 102));
	    R29.setBounds(282, 47, 48, 10);
	    if (loc == 19) R29.setBackground(Color.yellow);
	    getContentPane().add(R29);
	    
	    JGradientButton ML = new JGradientButton("");
	    ML.setBackground(new Color(0, 191, 255));
	    ML.setToolTipText("Mindagan Lake");
	    ML.setBounds(271, 102, 196, 105);
	    if (loc == 23) ML.setBackground(Color.yellow);
	    getContentPane().add(ML);
	    
	    JGradientButton SR = new JGradientButton("");
	    SR.setToolTipText("Shadow Ravine");
	    SR.setBackground(Color.DARK_GRAY);
	    SR.setBounds(282, 242, 170, 44);
	    if (loc == 25) SR.setBackground(Color.yellow);
	    getContentPane().add(SR);
	    
	    JButton SRC = new JButton("");
	    SRC.setToolTipText("Schrice City");
	    SRC.setBounds(329, 34, 50, 38);
	    if (locations[6]) {
	    	SRC.setBackground(Color.red);
	    	SRC.addActionListener(e -> {
	    		fly(SRC.getToolTipText(), 38, 49, 55);
	    	});
	    }
	    if (loc == 20) SRC.setBackground(Color.yellow);
	    getContentPane().add(SRC);
	    
	    JButton R30 = new JButton("");
	    R30.setToolTipText("Route 30");
	    R30.setBackground(new Color(153, 255, 102));
	    R30.setBounds(349, 71, 12, 31);
	    if (loc == 22) R30.setBackground(Color.yellow);
	    getContentPane().add(R30);
	    
	    JButton R31 = new JButton("");
	    R31.setToolTipText("Route 31");
	    R31.setBackground(new Color(153, 255, 102));
	    R31.setBounds(347, 207, 12, 35);
	    if (loc == 24) R31.setBackground(Color.yellow);
	    getContentPane().add(R31);
	    
	    JButton R42 = new JButton("");
	    R42.setToolTipText("Route 42");
	    R42.setBackground(new Color(153, 255, 102));
	    R42.setBounds(344, 286, 10, 27);
	    if (loc == 26) R42.setBackground(Color.yellow);
	    getContentPane().add(R42);
	    
	    JButton R35 = new JButton("");
	    R35.setToolTipText("Route 35");
	    R35.setBackground(new Color(153, 255, 102));
	    R35.setBounds(378, 47, 75, 10);
	    if (loc == 33) R35.setBackground(Color.yellow);
	    getContentPane().add(R35);
	    
	    JButton GT = new JButton("");
	    GT.setToolTipText("Glurg Town");
	    GT.setBounds(452, 41, 26, 23);
	    if (locations[7]) GT.setBackground(Color.red);
	    if (loc == 32) GT.setBackground(Color.yellow);
	    getContentPane().add(GT);
	    
	    JButton R34_1 = new JButton("");
	    R34_1.setToolTipText("Route 34");
	    R34_1.setBackground(new Color(153, 255, 102));
	    R34_1.setBounds(477, 47, 59, 10);
	    if (loc == 31) R34_1.setBackground(Color.yellow);
	    getContentPane().add(R34_1);
	    
	    JGradientButton GW = new JGradientButton("");
	    GW.setToolTipText("Ghostly Woods");
	    GW.setBackground(new Color(0, 128, 0));
	    GW.setBounds(514, 122, 68, 58);
	    if (loc == 30) GW.setBackground(Color.yellow);
	    getContentPane().add(GW);
	    
	    JButton R34_2 = new JButton("");
	    R34_2.setToolTipText("Route 34");
	    R34_2.setBackground(new Color(153, 255, 102));
	    R34_2.setBounds(535, 47, 10, 76);
	    if (loc == 31) R34_2.setBackground(Color.yellow);
	    getContentPane().add(R34_2);
	    
	    JButton R32 = new JButton("");
	    R32.setToolTipText("Route 32");
	    R32.setBackground(new Color(153, 255, 102));
	    R32.setBounds(452, 257, 44, 13);
	    if (loc == 27) R32.setBackground(Color.yellow);
	    getContentPane().add(R32);
	    
	    JButton FC = new JButton("");
	    FC.setToolTipText("Frenco City");
	    FC.setBounds(495, 244, 50, 38);
	    if (locations[8]) FC.setBackground(Color.red);
	    if (loc == 28) FC.setBackground(Color.yellow);
	    getContentPane().add(FC);
	    
	    JButton R33 = new JButton("");
	    R33.setToolTipText("Route 33");
	    R33.setBackground(new Color(153, 255, 102));
	    R33.setBounds(526, 180, 10, 65);
	    if (loc == 29) R33.setBackground(Color.yellow);
	    getContentPane().add(R33);
	    
	    JGradientButton AT = new JGradientButton("");
	    AT.setToolTipText("Abandoned Tower");
	    AT.setBackground(new Color(153, 102, 0));
	    AT.setBounds(584, 64, 26, 23);
	    if (loc == 44) AT.setBackground(Color.yellow);
	    getContentPane().add(AT);
	    
	    JButton ATP_1 = new JButton("");
	    ATP_1.setBackground(new Color(153, 255, 102));
	    ATP_1.setToolTipText("A.T. Path");
	    ATP_1.setBounds(582, 129, 20, 10);
	    if (loc == 43) ATP_1.setBackground(Color.yellow);
	    getContentPane().add(ATP_1);
	    
	    JButton ATP_2 = new JButton("");
	    ATP_2.setToolTipText("A.T. Path");
	    ATP_2.setBackground(new Color(153, 255, 102));
	    ATP_2.setBounds(592, 87, 10, 43);
	    if (loc == 43) ATP_2.setBackground(Color.yellow);
	    getContentPane().add(ATP_2);
	    
	    JButton CC = new JButton("");
	    CC.setToolTipText("Checkpoint Charlie");
	    CC.setBounds(535, 306, 67, 38);
	    if (locations[11]) CC.setBackground(Color.red);
	    if (loc == 46) CC.setBackground(Color.yellow);
	    getContentPane().add(CC);
	    
	    JButton R43 = new JButton("");
	    R43.setToolTipText("Route 43");
	    R43.setBackground(new Color(153, 255, 102));
	    R43.setBounds(362, 319, 174, 10);
	    if (loc == 45) R43.setBackground(Color.yellow);
	    getContentPane().add(R43);
	    
	    JGradientButton MtJ = new JGradientButton("");
	    MtJ.setToolTipText("Mt. St. Joseph");
	    MtJ.setBackground(new Color(153, 102, 0));
	    MtJ.setBounds(125, 319, 68, 73);
	    if (loc == 37) MtJ.setBackground(Color.yellow);
	    getContentPane().add(MtJ);
	    
	    JButton R38 = new JButton("");
	    R38.setToolTipText("Route 38");
	    R38.setBackground(new Color(153, 255, 102));
	    R38.setBounds(93, 345, 32, 13);
	    if (loc == 36) R38.setBackground(Color.yellow);
	    getContentPane().add(R38);
	    
	    JGradientButton LL = new JGradientButton("");
	    LL.setToolTipText("Lava Lake");
	    LL.setBackground(new Color(196, 39, 0));
	    LL.setBounds(201, 360, 68, 65);
	    if (loc == 40) LL.setBackground(Color.yellow);
	    getContentPane().add(LL);
	    
	    JButton R40 = new JButton("");
	    R40.setToolTipText("Route 40");
	    R40.setBackground(new Color(153, 255, 102));
	    R40.setBounds(229, 334, 10, 26);
	    if (loc == 21) R40.setBackground(Color.yellow);
	    getContentPane().add(R40);
	    
	    JButton btnNewButton = new JButton("");
	    btnNewButton.setBackground(Color.YELLOW);
	    btnNewButton.setBounds(363, 391, 26, 23);
	    getContentPane().add(btnNewButton);
	    
	    JLabel lblNewLabel = new JLabel("= YOU ARE HERE");
	    lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblNewLabel.setBounds(390, 395, 137, 14);
	    getContentPane().add(lblNewLabel);
	}

	private void fly(String loc, int map, int x, int y) {
		if (gp.player.p.hasMove(Move.CUT)) {
			int answer = JOptionPane.showConfirmDialog(null, "Would you like to fly to " + loc + "?");
    		if (answer == JOptionPane.YES_OPTION) {
    			gp.eHandler.teleport(map, x, y);
    			gp.keyH.resume();
    			this.dispose();
    			gp.mapOpen = false;
    		}
		}
		
	}

	private int getLoc(int map, int x, int y) {
		switch (map) {
		case 0:
			if (x > 73 && y > 37) return 0;
			if (x <= 73 && x > 41 && y > 37) return 1;
			if (x <= 41 && y > 41) return 2;
			if (x <= 40 && y <= 41) return 3;
			if (x > 40 && y <= 37) return 26;
			break;
		case 1:
			return 2;
		case 2:
			return 2;
		case 3:
			return 3;
		case 4:
			if (y <= 43) return 16;
			if (x > 57) return 4;
			if (x <= 57) return 5;
			break;
		case 5:
			return 4;
		case 6:
			return 4;
		case 7:
			return 4;
		case 8:
			return 4;
		case 9:
			return 4;
		case 10:
			return 6;
		case 11:
			if (x > 43) return 6;
			if (x <= 43) {
				if (y <= 64) return 8;
				if (y > 64) return 7;
			}
		case 12:
			return 8;
		case 13:
			if (y <= 52) return 10;
			if (y > 52) return 9;
		case 14:
			return 9;
		case 15:
			return 9;
		case 16:
			return 9;
		case 17:
			return 9;
		case 18:
			return 9;
		case 19:
			return 9;
		case 20:
			return 9;
		case 21:
			return 9;
		case 22:
			if (y <= 18) return 21;
			if (y > 18) return 40;
		case 23:
			return 21;
		case 24:
			return 11;
		case 25:
			return 11;
		case 26:
			return 11;
		case 27:
			return 11;
		case 28:
			if (y > 57) return 14;
			if (x <= 61) return 12;
			if (x > 61) return 13;
		case 29:
			return 13;
		case 30:
			return 13;
		case 31:
			return 13;
		case 32:
			return 9;
		case 33:
			if (y <= 22) return 17;
			if (y > 22) return 15;
		case 34:
			return 17;
		case 35:
			return 18;
		case 36:
			return 19;
		case 37:
			return 19;
		case 38:
			if (y <= 63) return 20;
			if (y > 63) return 22;
		case 39:
			return 20;
		case 40:
			return 20;
		case 41:
			return 20;
		case 42:
			return 20;
		case 43:
			return 20;
		case 44:
			return 20;
		case 45:
			return 20;
		case 46:
			return 4;
		case 47:
			return 2;
		case 48:
			return 4;
		case 49:
			return 13;
		case 50:
			return 20;
		case 51:
			return 0;
		case 52:
			return 0;
		case 53:
			return 0;
		case 54:
			return 0;
		case 55:
			return 2;
		case 56:
			return 2;
		case 57:
			return 4;
		case 58:
			return 4;
		case 59:
			return 9;
		case 60:
			return 9;
		case 61:
			return 13;
		case 62:
			return 20;
		case 63:
			return 20;
		case 64:
			return 20;
		case 65:
			return 20;
		case 66:
			return 20;
		case 67:
			return 20;
		case 68:
			return 20;
		case 69:
			return 20;
		case 70:
			return 20;
		case 71:
			return 20;
		case 72:
			return 20;
		case 73:
			return 20;
		case 74:
			return 15;
		case 75:
			return 15;
		case 76:
			return 22;
		case 77:
			return 23;
		case 78:
			return 23;
		case 79:
			return 24;
		case 80:
			if (y <= 41) return 24;
			if (y > 41) return 25;
		case 81:
			return 26;
		case 82: // TODO from here down
			return 20;
		case 83:
			return 20;
		case 84:
			return 20;
		case 85:
			return 20;
		case 86:
			return 4;
		case 87:
			return 20;
		case 88:
			return 20;
		case 89:
			return 20;
		case 90:
			return 20;
		case 91:
			return 20;
		case 92:
			return 20;
		case 93:
			return 20;
		case 94:
			return 20;
		case 95:
			return 20;
		case 96:
			return 4;
		case 97:
			return 20;
		case 98:
			return 20;
		case 99:
			return 20;
		}
		
		return -1;
	}
}
