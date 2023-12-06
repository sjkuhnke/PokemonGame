package Swing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Swing.Battle.JGradientButton;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import Overworld.Main;

public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int cost;
	private int healAmount;
	private String desc;
	
	public Item(int i) {
		id = i;
		cost = setCost();
		desc = setDesc();
		if (id >= 4 && id <= 8) {
			switch(id) {
				case 4:
					healAmount = 20;
					break;
				case 5:
					healAmount = 60;
					break;
				case 6:
					healAmount = 200;
					break;
				case 7:
					healAmount = -1;
					break;
				case 8:
					healAmount = -1;
					break;
				default:
					healAmount = 0;
			}
		}
	}
	
	private int setCost() {
		if (id == 0) return 25;
		else if (id == 1) return 10;
		else if (id == 2) return 25;
		else if (id == 3) return 50;
		else if (id == 4) return 50;
		else if (id == 5) return 125;
		else if (id == 6) return 275;
		else if (id == 7) return 400;
		else if (id == 8) return 500;
		else if (id == 9) return 25;
		else if (id == 10) return 25;
		else if (id == 11) return 25;
		else if (id == 12) return 25;
		else if (id == 13) return 25;
		else if (id == 14) return 150;
		else if (id == 15) return 50;
		else if (id == 16) return 500;
		else if (id == 17) return 1500;
		else if (id == 18) return 0;
		else if (id == 19) return 500;
		else if (id == 20) return 1000;
		else if (id == 21) return 1000;
		else if (id == 22) return 1000;
		else if (id == 23) return 1000;
		else if (id == 24) return 2000;
		else if (id == 25) return 2000;
		else if (id == 27) return 1000;
		else if (id == 29) return 2500;
		else if (id == 30) return 1750;
		else if (id == 31) return 1000;
		else if (id == 32) return 1500;
		else if (id == 33) return 1250;
		else if (id == 34) return 1750;
		else if (id == 35) return 2500;
		else if (id == 36) return 2250;
		else if (id == 37) return 1000;
		else if (id == 38) return 1250;
		else if (id == 39) return 2500;
		else if (id == 40) return 300;
		else if (id == 41) return 250;
		else if (id == 42) return 2500;
		else if (id == 112) return 750;
		else if (id == 113) return 5000;
		else if (id == 115) return 1000;
		else if (id == 116) return 1000;
		else if (id == 123) return 800;
		else if (id == 124) return 900;
		else if (id == 138) return 1500;
		else if (id == 141) return 1000;
		else if (id == 142) return 1000;
		else if (id == 143) return 1000;
		else if (id == 144) return 1000;
		else if (id == 145) return 1000;
		else if (id == 146) return 1500;
		else if (id == 149) return 1500;
		else if (id == 150) return 1250;
		else if (id == 151) return 400;
		else if (id == 154) return 750;
		else if (id == 163) return 1500;
		else if (id == 164) return 1500;
		else if (id == 166) return 2000;
		else if (id == 167) return 2000;
		else if (id == 170) return 1500;
		else if (id == 174) return 900;
		else if (id == 175) return 900;
		else if (id == 176) return 700;
		else if (id == 177) return 700;
		else if (id == 178) return 800;
		else if (id == 179) return 300;
		else if (id == 180) return 300;
		else if (id == 181) return 300;
		else if (id == 182) return 300;
		else if (id == 191) return 1000;
		else if (id == 195) return 500;
		else return 0;
	}
	
	public int getCost() { return cost; }

	public int getID() { return id; }
	
	@Override
	public String toString() {
		String result = "";
		switch (id) {
		case 0:
			result = "Repel";
			break;
		case 1:
			result = "Pokeball";
			break;
		case 2:
			result = "Great Ball";
			break;
		case 3:
			result = "Ultra Ball";
			break;
		case 4:
			result = "Potion";
			break;
		case 5:
			result = "Super Potion";
			break;
		case 6:
			result = "Hyper Potion";
			break;
		case 7:
			result = "Max Potion";
			break;
		case 8:
			result = "Full Restore";
			break;
		case 9:
			result = "Antidote";
			break;
		case 10:
			result = "Awakening";
			break;
		case 11:
			result = "Burn Heal";
			break;
		case 12:
			result = "Paralyze Heal";
			break;
		case 13:
			result = "Freeze Heal";
			break;
		case 14:
			result = "Full Heal";
			break;
		case 15:
			result = "Rage Candy Bar";
			break;
		case 16:
			result = "Revive";
			break;
		case 17:
			result = "Max Revive";
			break;
		case 18:
			result = "Rare Candy";
			break;
		case 19:
			result = "Euphorian Gem";
			break;
		case 20:
			result = "Leaf Stone";
			break;
		case 21:
			result = "Dusk Stone";
			break;
		case 22:
			result = "Dawn Stone";
			break;
		case 23:
			result = "Ice Stone";
			break;
		case 24:
			result = "Valiant Gem";
			break;
		case 25:
			result = "Petticoat Gem";
			break;
		case 26:
			result = "Ability Capsule";
			break;
		case 27:
			result = "Bottle Cap";
			break;
		case 28:
			result = "Gold Battle Cap";
			break;
		case 29:
			result = "Adamant Mint";
			break;
		case 30:
			result = "Bold Mint";
			break;
		case 31:
			result = "Brave Mint";
			break;
		case 32:
			result = "Calm Mint";
			break;
		case 33:
			result = "Careful Mint";
			break;
		case 34:
			result = "Impish Mint";
			break;
		case 35:
			result = "Jolly Mint";
			break;
		case 36:
			result = "Modest Mint";
			break;
		case 37:
			result = "Quiet Mint";
			break;
		case 38:
			result = "Serious Mint";
			break;
		case 39:
			result = "Timid Mint";
			break;
		case 40:
			result = "Elixir";
			break;
		case 41:
			result = "Max Elixir";
			break;
		case 42:
			result = "PP Up";
			break;
		case 43:
			result = "PP Max";
			break;
		case 44:
			result = "Flame Orb";
			break;
		case 93:
			result = "HM01";
			break;
		case 94:
			result = "HM02";
			break;
		case 95:
			result = "HM03";
			break;
		case 96:
			result = "HM04";
			break;
		case 97:
			result = "HM05";
			break;
		case 98:
			result = "HM06";
			break;
		case 99:
			result = "HM07";
			break;
		case 100:
			result = "HM08";
			break;
		case 101:
			result = "TM01";
			break;
		case 102:
			result = "TM02";
			break;
		case 103:
			result = "TM03";
			break;
		case 104:
			result = "TM04";
			break;
		case 105:
			result = "TM05";
			break;
		case 106:
			result = "TM06";
			break;
		case 107:
			result = "TM07";
			break;
		case 108:
			result = "TM08";
			break;
		case 109:
			result = "TM09";
			break;
		case 110:
			result = "TM10";
			break;
		case 111:
			result = "TM11";
			break;
		case 112:
			result = "TM12";
			break;
		case 113:
			result = "TM13";
			break;
		case 114:
			result = "TM14";
			break;
		case 115:
			result = "TM15";
			break;
		case 116:
			result = "TM16";
			break;
		case 117:
			result = "TM17";
			break;
		case 118:
			result = "TM18";
			break;
		case 119:
			result = "TM19";
			break;
		case 120:
			result = "TM20";
			break;
		case 121:
			result = "TM21";
			break;
		case 122:
			result = "TM22";
			break;
		case 123:
			result = "TM23";
			break;
		case 124:
			result = "TM24";
			break;
		case 125:
			result = "TM25";
			break;
		case 126:
			result = "TM26";
			break;
		case 127:
			result = "TM27";
			break;
		case 128:
			result = "TM28";
			break;
		case 129:
			result = "TM29";
			break;
		case 130:
			result = "TM30";
			break;
		case 131:
			result = "TM31";
			break;
		case 132:
			result = "TM32";
			break;
		case 133:
			result = "TM33";
			break;
		case 134:
			result = "TM34";
			break;
		case 135:
			result = "TM35";
			break;
		case 136:
			result = "TM36";
			break;
		case 137:
			result = "TM37";
			break;
		case 138:
			result = "TM38";
			break;
		case 139:
			result = "TM39";
			break;
		case 140:
			result = "TM40";
			break;
		case 141:
			result = "TM41";
			break;
		case 142:
			result = "TM42";
			break;
		case 143:
			result = "TM43";
			break;
		case 144:
			result = "TM44";
			break;
		case 145:
			result = "TM45";
			break;
		case 146:
			result = "TM46";
			break;
		case 147:
			result = "TM47";
			break;
		case 148:
			result = "TM48";
			break;
		case 149:
			result = "TM49";
			break;
		case 150:
			result = "TM50";
			break;
		case 151:
			result = "TM51";
			break;
		case 152:
			result = "TM52";
			break;
		case 153:
			result = "TM53";
			break;
		case 154:
			result = "TM54";
			break;
		case 155:
			result = "TM55";
			break;
		case 156:
			result = "TM56";
			break;
		case 157:
			result = "TM57";
			break;
		case 158:
			result = "TM58";
			break;
		case 159:
			result = "TM59";
			break;
		case 160:
			result = "TM60";
			break;
		case 161:
			result = "TM61";
			break;
		case 162:
			result = "TM62";
			break;
		case 163:
			result = "TM63";
			break;
		case 164:
			result = "TM64";
			break;
		case 165:
			result = "TM65";
			break;
		case 166:
			result = "TM66";
			break;
		case 167:
			result = "TM67";
			break;
		case 168:
			result = "TM68";
			break;
		case 169:
			result = "TM69";
			break;
		case 170:
			result = "TM70";
			break;
		case 171:
			result = "TM71";
			break;
		case 172:
			result = "TM72";
			break;
		case 173:
			result = "TM73";
			break;
		case 174:
			result = "TM74";
			break;
		case 175:
			result = "TM75";
			break;
		case 176:
			result = "TM76";
			break;
		case 177:
			result = "TM77";
			break;
		case 178:
			result = "TM78";
			break;
		case 179:
			result = "TM79";
			break;
		case 180:
			result = "TM80";
			break;
		case 181:
			result = "TM81";
			break;
		case 182:
			result = "TM82";
			break;
		case 183:
			result = "TM83";
			break;
		case 184:
			result = "TM84";
			break;
		case 185:
			result = "TM85";
			break;
		case 186:
			result = "TM86";
			break;
		case 187:
			result = "TM87";
			break;
		case 188:
			result = "TM88";
			break;
		case 189:
			result = "TM89";
			break;
		case 190:
			result = "TM90";
			break;
		case 191:
			result = "TM91";
			break;
		case 192:
			result = "TM92";
			break;
		case 193:
			result = "TM93";
			break;
		case 194:
			result = "TM94";
			break;
		case 195:
			result = "TM95";
			break;
		case 196:
			result = "TM96";
			break;
		case 197:
			result = "TM97";
			break;
		case 198:
			result = "TM98";
			break;
		case 199:
			result = "TM99";
			break;
		case 200:
			result = "Calculator";
			break;
		}
		if (id >= 93 && id < 200) result += " " + getMove().toString();
		return result;
	}

	public int getHealAmount() {
		return healAmount;
	}
	
	public boolean getLearned(Pokemon p) {
		int index1 = p.id - 1;
		int index2 = this.id - 93;
		boolean[][] tm = new boolean[][] {
			
			
															//     1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true }, // 001
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true }, // 003
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true },
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true }, // 005
				{false,false,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true }, 
				{true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true ,true }, // 007
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true }, // 008
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true },
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true }, // 010
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true }, // 012
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,true }, // 014
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true },
				{true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 016
				{true ,true ,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true }, // 018
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true }, // 020
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true },
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true }, // 022
				{true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,true }, // 024
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true }, // 026
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,true }, // 028
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,true }, // 030
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true }, // 032
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,true }, // 034
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
															//+1   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true }, // 036
				{true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true },
				{false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true }, // 038
				{false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true },
				{false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true }, // 040
				{true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true }, // 042
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 044
				{false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true },
				{false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 046
				{false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 048
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true }, // 050
				{true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,false,true },
				{true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true }, // 052
				{true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true },
				{true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true }, // 054
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true }, // 056
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true }, // 058
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,true },
				{false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true }, // 060
				{false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 062
				{false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,true },
				{true ,true ,false,false,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true }, // 064
				{true ,true ,false,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,true }, // 066
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,true },
				{true ,false,false,true ,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 068
				{true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true },
				{true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 070
				{false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true },
				{false,true ,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,true ,true }, // 072
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,false,true },
															//+2   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true }, // 074
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true }, // 076
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true }, // 078
				{true ,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true },
				{true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true }, // 080
				{true ,false,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true },
				{false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true }, // 082
				{false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true },
				{false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true }, // 084
				{false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true }, // 086
				{false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true ,false,true }, // 088
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true },
				{true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true }, // 090
				{true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true },
				{true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true }, // 092
				{true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true }, // 094
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true }, // 096
				{false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true }, // 098
				{true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true }, // 100
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true }, // 102
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true },
				{false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true }, // 104
				{false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true },
				{false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,true }, // 106
				{false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true }, // 108
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true }, // 110
				{false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,false,true }, // 112
															//+3   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 114
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true }, // 116
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true }, // 118
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true }, // 120
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,false,true }, // 122
				{true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,true }, // 124
				{true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 126
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true }, // 128
				{true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 130
				{true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 132
				{true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true }, // 134
				{false,false,false,true ,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true }, // 136
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
				{false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true }, // 138
				{false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true },
				{false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true }, // 140
				{true ,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true },
				{true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,false,false,true ,true ,true }, // 142
				{false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true },
				{true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,false,true ,false,true ,true }, // 144
				{true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true },
				{true ,true ,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true }, // 146
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true }, // 148
				{true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,true ,false,true ,true ,true },
				{false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,true }, // 150
				{false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true },
				{false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,true }, // 152
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true },
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true }, // 154
															//+4   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,true },
				{true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true }, // 156
				{true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,true ,false,true },
				{true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true }, // 158
				{true ,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true }, // 159
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true }, // 161
				{true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 163
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,true ,false,true }, // 165
				{true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true }, // 167
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,true ,false,true }, // 169
				{true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true }, // 171
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 173
				{true ,true ,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,true },
				{true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true }, // 175
				{true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true },
				{true ,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true }, // 177
				{true ,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true },
				{true ,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true }, // 179
				{true ,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true }, // 181
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,true }, // 183
				{true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true },
				{true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true }, // 185
				{true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,false,false,true },
				{true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true }, // 187
				{true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true },
				{true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true }, // 189
				{false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,true },
				{true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,true }, // 191
				{true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,true },
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true }, // 193
				{true ,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,true ,false,true }, // 195
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,true ,false,true },
															//+5   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true }, // 197
				{false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true }, // 199
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true }, // 201
				{true ,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 203
				{true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,true }, // 205
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true }, // 207
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false}, // 209
				{true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true },
				{true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,true }, // 211
				{true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true }, // 213
				{true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,true },
				{false,true ,true ,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,true }, // 215
				{true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true },
				{false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true }, // 217
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,true }, // 219
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,true }, // 221
				{true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true }, // 223
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true }, // 225
				{false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 227
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 229
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 231
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 233
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 235
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 237
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 239
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },

		};
		
		return tm[index1][index2];
		
	}

	public Move getMove() {
		if (this.id < 93) return null;
		Move result;
		
		switch (this.id) {
		case 93:
			result = Move.CUT;
			break;
		case 94:
			result = Move.ROCK_SMASH;
			break;
		case 95:
			result = Move.VINE_CROSS;
			break;
		case 96:
			result = Move.SURF;
			break;
		case 97:
			result = Move.SLOW_FALL;
			break;
		case 98:
			result = Move.WHIRLPOOL;
			break;
		case 99:
			result = Move.ROCK_CLIMB;
			break;
		case 100:
			result = Move.LAVA_SURF;
			break;
		case 101:
			result = Move.SUPER_FANG;
			break;
		case 102:
			result = Move.DRAGON_CLAW;
			break;
		case 103:
			result = Move.ELEMENTAL_SPARKLE;
			break;
		case 104:
			result = Move.CALM_MIND;
			break;
		case 105:
			result = Move.BODY_SLAM;
			break;
		case 106:
			result = Move.SHADOW_BALL;
			break;
		case 107:
			result = Move.FOCUS_BLAST;
			break;
		case 108:
			result = Move.BULK_UP;
			break;
		case 109:
			result = Move.LEAF_BLADE;
			break;
		case 110:
			result = Move.ICE_BEAM;
			break;
		case 111:
			result = Move.PSYSHOCK;
			break;
		case 112:
			result = Move.PROTECT;
			break;
		case 113:
			result = Move.BATON_PASS;
			break;
		case 114:
			result = Move.TAUNT;
			break;
		case 115:
			result = Move.GIGA_IMPACT;
			break;
		case 116:
			result = Move.HYPER_BEAM;
			break;
		case 117:
			result = Move.SOLAR_BEAM;
			break;
		case 118:
			result = Move.IRON_HEAD;
			break;
		case 119:
			result = Move.PHOTON_GEYSER;
			break;
		case 120:
			result = Move.EARTHQUAKE;
			break;
		case 121:
			result = Move.THROAT_CHOP;
			break;
		case 122:
			result = Move.FELL_STINGER;
			break;
		case 123:
			result = Move.WEATHER_BALL;
			break;
		case 124:
			result = Move.TERRAIN_PULSE;
			break;
		case 125:
			result = Move.THUNDERBOLT;
			break;
		case 126:
			result = Move.HIDDEN_POWER;
			break;
		case 127:
			result = Move.DRAIN_PUNCH;
			break;
		case 128:
			result = Move.FLAME_CHARGE;
			break;
		case 129:
			result = Move.LIQUIDATION;
			break;
		case 130:
			result = Move.U$TURN;
			break;
		case 131:
			result = Move.FALSE_SWIPE;
			break;
		case 132:
			result = Move.ZING_ZAP;
			break;
		case 133:
			result = Move.PSYCHIC_FANGS;
			break;
		case 134:
			result = Move.MAGIC_TOMB;
			break;
		case 135:
			result = Move.FLAMETHROWER;
			break;
		case 136:
			result = Move.SLUDGE_BOMB;
			break;
		case 137:
			result = Move.ROCK_TOMB;
			break;
		case 138:
			result = Move.BLIZZARD;
			break;
		case 139:
			result = Move.PSYCHIC;
			break;
		case 140:
			result = Move.FACADE;
			break;
		case 141:
			result = Move.REFLECT;
			break;
		case 142:
			result = Move.LIGHT_SCREEN;
			break;
		case 143:
			result = Move.DAZZLING_GLEAM;
			break;
		case 144:
			result = Move.PLAY_ROUGH;
			break;
		case 145:
			result = Move.WILL$O$WISP;
			break;
		case 146:
			result = Move.FIRE_BLAST;
			break;
		case 147:
			result = Move.STAR_STORM;
			break;
		case 148:
			result = Move.SCALD;
			break;
		case 149:
			result = Move.REST;
			break;
		case 150:
			result = Move.TOXIC;
			break;
		case 151:
			result = Move.SLEEP_TALK;
			break;
		case 152:
			result = Move.AERIAL_ACE;
			break;
		case 153:
			result = Move.VOLT_SWITCH;
			break;
		case 154:
			result = Move.THUNDER_WAVE;
			break;
		case 155:
			result = Move.MAGIC_BLAST;
			break;
		case 156:
			result = Move.SPARKLE_STRIKE;
			break;
		case 157:
			result = Move.CHARGE_BEAM;
			break;
		case 158:
			result = Move.DRAGON_PULSE;
			break;
		case 159:
			result = Move.BRICK_BREAK;
			break;
		case 160:
			result = Move.FREEZE$DRY;
			break;
		case 161:
			result = Move.SMACK_DOWN;
			break;
		case 162:
			result = Move.BUG_BUZZ;
			break;
		case 163:
			result = Move.THUNDER;
			break;
		case 164:
			result = Move.CLOSE_COMBAT;
			break;
		case 165:
			result = Move.SHADOW_CLAW;
			break;
		case 166:
			result = Move.DRACO_METEOR;
			break;
		case 167:
			result = Move.OUTRAGE;
			break;
		case 168:
			result = Move.FLASH;
			break;
		case 169:
			result = Move.ROCK_POLISH;
			break;
		case 170:
			result = Move.HYDRO_PUMP;
			break;
		case 171:
			result = Move.STONE_EDGE;
			break;
		case 172:
			result = Move.ICE_SPINNER;
			break;
		case 173:
			result = Move.GYRO_BALL;
			break;
		case 174:
			result = Move.SUNNY_DAY;
			break;
		case 175:
			result = Move.RAIN_DANCE;
			break;
		case 176:
			result = Move.SNOWSCAPE;
			break;
		case 177:
			result = Move.SANDSTORM;
			break;
		case 178:
			result = Move.SWORDS_DANCE;
			break;
		case 179:
			result = Move.GRASSY_TERRAIN;
			break;
		case 180:
			result = Move.ELECTRIC_TERRAIN;
			break;
		case 181:
			result = Move.PSYCHIC_TERRAIN;
			break;
		case 182:
			result = Move.SPARKLING_TERRAIN;
			break;
		case 183:
			result = Move.CAPTIVATE;
			break;
		case 184:
			result = Move.DARK_PULSE;
			break;
		case 185:
			result = Move.ROCK_SLIDE;
			break;
		case 186:
			result = Move.X$SCISSOR;
			break;
		case 187:
			result = Move.POISON_JAB;
			break;
		case 188:
			result = Move.GALAXY_BLAST;
			break;
		case 189:
			result = Move.ACROBATICS;
			break;
		case 190:
			result = Move.IRON_BLAST;
			break;
		case 191:
			result = Move.TRI$ATTACK;
			break;
		case 192:
			result = Move.COMET_CRASH;
			break;
		case 193:
			result = Move.EARTH_POWER;
			break;
		case 194:
			result = Move.HURRICANE;
			break;
		case 195:
			result = Move.TRICK_ROOM;
			break;
		case 196:
			result = Move.ENERGY_BALL;
			break;
		case 197:
			result = Move.SPIRIT_BREAK;
			break;
		case 198:
			result = Move.FLIP_TURN;
			break;
		case 199:
			result = Move.RETURN;
			break;
		default:
			result = null;
			break;
		}
		return result;
	}

	public Status getStatus() {
		if (id == 9) return Status.POISONED;
		else if (id == 10) return Status.ASLEEP;
		else if (id == 11) return Status.BURNED;
		else if (id == 12) return Status.PARALYZED;
		else if (id == 13) return Status.FROSTBITE;
		else if (id == 14) return null;
		else if (id == 15) return null;
		else return Status.CONFUSED; // lol
		
	}
	
	public Color getColor() {
		if (id == 0) return new Color(0, 92, 5);
		else if (id == 1) return new Color(176, 0, 12);
		else if (id == 2) return new Color(0, 0, 148);
		else if (id == 3) return new Color(148, 171, 0);
		else if (id == 4) return new Color(124, 0, 219);
		else if (id == 5) return new Color(140, 24, 8);
		else if (id == 6) return new Color(255, 0, 191);
		else if (id == 7) return new Color(0, 21, 255);
		else if (id == 8) return new Color(255, 196, 0);
		else if (id == 9) return new Color(157, 0, 255);
		else if (id == 10) return new Color(63, 83, 92);
		else if (id == 11) return new Color(133, 15, 19);
		else if (id == 12) return new Color(176, 158, 0);
		else if (id == 13) return new Color(0, 170, 189);
		else if (id == 14) return new Color(255, 247, 0);
		else if (id == 15) return new Color(0, 55, 255);
		else if (id == 16) return new Color(219, 194, 0);
		else if (id == 17) return new Color(219, 194, 0);
		else if (id == 18) return new Color(124, 54, 255);
		else if (id == 19) return new Color(138, 237, 255);
		else if (id == 20) return new Color(0, 120, 20);
		else if (id == 21) return new Color(64, 64, 64);
		else if (id == 22) return new Color(0, 176, 179);
		else if (id == 23) return new Color(176, 244, 245);
		else if (id == 24) return new Color(72, 75, 219);
		else if (id == 25) return new Color(204, 61, 140);
		else if (id == 26) return new Color(102, 7, 143);
		else if (id == 27) return new Color(192, 192, 192);
		else if (id == 28) return new Color(255, 215, 0);
		else if (id >= 29 && id <= 39) return new Color(113, 84, 255);
		else if (id == 40) return new Color(230, 146, 78);
		else if (id == 41) return new Color(246, 255, 120);
		else if (id == 42) return new Color(150, 51, 156);
		else if (id == 43) return new Color(142, 230, 21);
		else if (id == 44) return new Color(232, 52, 54);
		else if (id == 200) return Color.black;
		else return getMove().mtype.getColor();
	}

	public boolean getEligible(int pid) {
		int[] check;
		boolean result = false;
		if (id == 20) {
			check = new int[] {27, 45, 118};
		} else if (id == 21) {
			check = new int[] {141, 160, 215, 220};
		} else if (id == 22) {
			check = new int[] {30, 175, 177};
		} else if (id == 23) {
			check = new int[] {62, 64, 193};
		} else if (id == 24) {
			check = new int[] {38, 86, 108};
		} else if (id == 25) {
			check = new int[] {38, 108};
		} else {
			check = new int[] {};
		}
		
		for (int i = 0; i < check.length; i++) {
			if (pid == check[i]) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	public void useCalc(Player p) {
		JPanel calc = new JPanel();
	    calc.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5); // Add space between components
        
        JComboBox<Pokemon> userMons = new JComboBox<>();
        JLabel userLevel = new JLabel();
        JLabel[] userStatLabels = new JLabel[6];
        @SuppressWarnings("unchecked")
		JComboBox<Integer>[] userStages = new JComboBox[6];
        JButton userCurrentHP = new JButton();
        JLabel userHPP = new JLabel();
        JLabel userSpeed = new JLabel();
        JGradientButton[] userMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
        JLabel[] userDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
        for (int k = 0; k < p.team.length; k++) {
        	if (p.team[k] != null) userMons.addItem(p.team[k].clone());
        }
        AutoCompleteDecorator.decorate(userMons);
        
        JComboBox<Pokemon> foeMons = new JComboBox<>();
        JTextField foeLevel = new JTextField();
        JLabel[] foeStatLabels = new JLabel[6];
        @SuppressWarnings("unchecked")
		JComboBox<Integer>[] foeStages = new JComboBox[6];
        JLabel foeSpeed = new JLabel();
        JGradientButton[] foeMoves = new JGradientButton[] {new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), new JGradientButton(""), };
        JLabel[] foeDamage = new JLabel[] {new JLabel(""), new JLabel(""), new JLabel(""), new JLabel(""), };
        for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
        	foeMons.addItem(new Pokemon(k, 50, false, true));
        }
        for (Trainer tr : Main.modifiedTrainers) {
        	for (Pokemon po : tr.getTeam()) {
        		Pokemon add = po.clone();
        		add.nickname = tr.getName();
        		foeMons.addItem(add);
        	}
        }
        AutoCompleteDecorator.decorate(foeMons);
        
        userMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
            userLevel.setText(userCurrent.getLevel() + "");
            updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
            updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        });
        
        foeMons.addActionListener(l -> {
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	foeLevel.setText(foeCurrent.getLevel() + "");
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        
        foeLevel.addActionListener(l ->{
        	Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        	try {
        		int level = Integer.parseInt(foeLevel.getText());
    			if (level >= 1 && level <= 100) {
    				foeCurrent.level = level;
    			} else {
    				foeCurrent.level = 50;
    			}
    		} catch (NumberFormatException e1) {
    			foeCurrent.level = 50;
    		}
        	foeCurrent.stats = foeCurrent.getStats();
        	foeCurrent.setMoves();
        	
        	updateMoves(foeCurrent, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        	updateMoves(userCurrent, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        
		foeLevel.addFocusListener(new FocusAdapter() {
			@Override // implementation
	    	public void focusGained(FocusEvent e) {
	        	foeLevel.selectAll();
	    	}
		});
        
        Pokemon userC = ((Pokemon) userMons.getSelectedItem());
        Pokemon foeC = ((Pokemon) foeMons.getSelectedItem());
        
        calc.add(userMons, gbc);
        gbc.gridx++;
        calc.add(foeMons, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        calc.add(userLevel, gbc);
        gbc.gridx++;
        JPanel foeLevelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        foeLevelPanel.add(foeLevel);
        calc.add(foeLevelPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel statsPanel = new JPanel(new GridLayout(6, 3));
        for (int i = 0; i < 6; i++) {
        	userStatLabels[i] = new JLabel(userC.stats[i] + "");
        	Integer[] stages = new Integer[] {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6};
        	userStages[i] = new JComboBox<Integer>(stages);
        	if (i != 0) userStages[i].setSelectedIndex(userC.statStages[i - 1] + 6);
        	JLabel blank = new JLabel("");
        	statsPanel.add(userStatLabels[i]);
        	if (i == 0) {
        		userCurrentHP.setText(userC.currentHP + "");
        		statsPanel.add(userCurrentHP);
        	} else {
        		int index = i;
        		userStages[i].addActionListener(e -> {
        			Pokemon current = ((Pokemon) userMons.getSelectedItem());
        			Pokemon foeCurrent = ((Pokemon) foeMons.getSelectedItem());
        			int amt = (int) userStages[index].getSelectedItem();
        			current.statStages[index - 1] = amt;
        			updateMoves(current, userMoves, userDamage, foeCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        			updateMoves(foeCurrent, foeMoves, foeDamage, current, foeStatLabels, foeStages, foeSpeed, null, null);
        			if (index == 5) userSpeed.setText((current.getSpeed()) + "");
        		});
        		statsPanel.add(userStages[i]);
        	}
        	
        	if (i == 0) {
        		double percent = userC.currentHP * 100.0 / userC.getStat(0);
        		userHPP.setText(String.format("%.1f", percent) + "%");
        		statsPanel.add(userHPP);
        	} else if (i == 5) {
        		userSpeed.setText((userC.getSpeed()) + "");
        		statsPanel.add(userSpeed);
        	} else {
        		statsPanel.add(blank);
        	}
        	
        	
        }
        
        JPanel fStatsPanel = new JPanel(new GridLayout(6, 3));
        for (int i = 0; i < 6; i++) {
        	foeStatLabels[i] = new JLabel(foeC.stats[i] + "");
        	Integer[] stages = new Integer[] {-6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6};
        	foeStages[i] = new JComboBox<Integer>(stages);
        	if (i != 0) foeStages[i].setSelectedIndex(foeC.statStages[i - 1] + 6);
        	JLabel blank = new JLabel("");
        	fStatsPanel.add(foeStatLabels[i]);
        	if (i == 0) {
        		fStatsPanel.add(new JButton());
        	} else {
        		int index = i;
        		foeStages[i].addActionListener(e -> {
        			Pokemon current = ((Pokemon) foeMons.getSelectedItem());
        			Pokemon userCurrent = ((Pokemon) userMons.getSelectedItem());
        			int amt = (int) foeStages[index].getSelectedItem();
        			current.statStages[index - 1] = amt;
        			updateMoves(current, foeMoves, foeDamage, userCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        			updateMoves(userCurrent, userMoves, userDamage, current, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        			if (index == 5) foeSpeed.setText((current.getSpeed()) + "");
        		});
        		fStatsPanel.add(foeStages[i]);
        	}
        	
        	if (i == 5) {
        		foeSpeed.setText((foeC.getSpeed()) + "");
        		fStatsPanel.add(foeSpeed);
        	} else {
        		fStatsPanel.add(blank);
        	}
        	
        }
        
        userLevel.setText(userC.getLevel() + "");
        updateMoves(userC, userMoves, userDamage, foeC, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        
        foeLevel.setText(foeC.getLevel() + "");
        updateMoves(foeC, foeMoves, foeDamage, userC, foeStatLabels, foeStages, foeSpeed, null, null);
        
        calc.add(statsPanel, gbc);
        gbc.gridx++;
        calc.add(fStatsPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel userMovesPanel = new JPanel(new GridLayout(4,2));
        for (int k = 0; k < userMoves.length; k++) {
        	userMovesPanel.add(userMoves[k]);
        	userMovesPanel.add(userDamage[k]);
        }
        calc.add(userMovesPanel, gbc);
        gbc.gridx++;
        JPanel foeMovesPanel = new JPanel(new GridLayout(4,2));
        for (int k = 0; k < userMoves.length; k++) {
        	foeMovesPanel.add(foeMoves[k]);
        	foeMovesPanel.add(foeDamage[k]);
        }
        calc.add(foeMovesPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JPanel resetButtonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	for (int i = 0; i < 4; i++) {
        		uCurrent.moveset[i] = p.team[userMons.getSelectedIndex()].moveset[i];
        	}
        	updateMoves(uCurrent, userMoves, userDamage, fCurrent, userStatLabels, userStages, userSpeed, userCurrentHP, userHPP);
        });
        resetButtonPanel.add(resetButton);
        calc.add(resetButtonPanel, gbc);
        gbc.gridx++;
        
        JPanel fResetButtonPanel = new JPanel();
        JButton fResetButton = new JButton("Reset");
        fResetButton.addActionListener(e -> {
        	Pokemon uCurrent = ((Pokemon) userMons.getSelectedItem());
        	Pokemon fCurrent = ((Pokemon) foeMons.getSelectedItem());
        	fCurrent.setMoves();
        	updateMoves(fCurrent, foeMoves, foeDamage, uCurrent, foeStatLabels, foeStages, foeSpeed, null, null);
        });
        fResetButtonPanel.add(fResetButton);
        calc.add(fResetButtonPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        
        JOptionPane.showMessageDialog(null, calc, "Damage Calculator", JOptionPane.PLAIN_MESSAGE);
		
	}
	
	private void updateMoves(Pokemon current, JGradientButton[] moves, JLabel[] damages, Pokemon foe, JLabel[] statLabels, JComboBox<Integer>[] stages,
			JLabel speed, JButton currentHP, JLabel HPP) {
        for (int k = 0; k < moves.length; k++) {
        	if (current.moveset[k] != null) {
        		moves[k].setText(current.moveset[k].move.toString());
        		moves[k].setBackground(current.moveset[k].move.mtype.getColor());
        		if (current.moveset[k].move == Move.HIDDEN_POWER) moves[k].setBackground(current.determineHPType().getColor());
        		int minDamage = current.calcWithTypes(foe, current.moveset[k].move, false, -1);
        		int maxDamage = current.calcWithTypes(foe, current.moveset[k].move, false, 1);
        		double minDamageD = minDamage * 1.0 / foe.getStat(0);
        		minDamageD *= 100;
        		String formattedMinD = String.format("%.1f", minDamageD);
        		double maxDamageD = maxDamage * 1.0 / foe.getStat(0);
        		maxDamageD *= 100;
        		String formattedMaxD = String.format("%.1f", maxDamageD);
        		damages[k].setText(formattedMinD + "% - " + formattedMaxD + "%");
        	} else {
        		moves[k].setText("[NO MOVE]");
        		moves[k].setBackground(null);
        		damages[k].setText("0% - 0%");
        	}
    		MouseListener[] listeners = moves[k].getMouseListeners();
    		for (MouseListener listener : listeners) {
    			moves[k].removeMouseListener(listener);
    		}
    		final int kndex = k;
    		moves[k].addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			    	if (SwingUtilities.isRightMouseButton(e)) {
			    		if (current.moveset[kndex] != null) {
	    	                JOptionPane.showMessageDialog(null, current.moveset[kndex].move.getMoveSummary(), "Move Description", JOptionPane.INFORMATION_MESSAGE);
	        			}
			    	} else {
			    		Move[] allMoves = Move.values();
			    		JComboBox<Move> moveComboBox = new JComboBox<>(allMoves);
			    		
			    		AutoCompleteDecorator.decorate(moveComboBox);
			    		
			    		JPanel setMovePanel = new JPanel();
			    		setMovePanel.add(new JLabel("Select a move:"));
			            setMovePanel.add(moveComboBox);
			            
			    		int result = JOptionPane.showConfirmDialog(null, setMovePanel, "Set Move", JOptionPane.OK_OPTION);
			    		if (result == JOptionPane.OK_OPTION) {
			    			Move selectedMove = (Move) moveComboBox.getSelectedItem();
			    			current.moveset[kndex] = new Moveslot(selectedMove);
			    		}
			    		updateMoves(current, moves, damages, foe, statLabels, stages, speed, currentHP, HPP);
			    	}
			    }
            });

    		for (int i = 0; i < 6; i++) {
    			statLabels[i].setText(current.getStat(i) + "");
    			
    			if (i != 0) {
	    			ActionListener stagesListener = stages[i].getActionListeners()[0];
	    		    stages[i].removeActionListener(stagesListener);
	    		    
	    		    if (i != 0) stages[i].setSelectedIndex(current.statStages[i-1] + 6);
	    		    
	    		    stages[i].addActionListener(stagesListener);
    			}

    			if (i == 0 && currentHP != null) {
    				currentHP.setText(current.currentHP + "");
    				double percent = current.currentHP * 100.0 / current.getStat(0);
            		HPP.setText(String.format("%.1f", percent) + "%");
    			}
    			if (i == 5) speed.setText(current.getSpeed() + "");
    		}
    		
        }
	}
	
	private String setDesc() {
		switch (id) {
		case 0:
			return "Prevents wild Pokemon encounters\nfor 200 steps";
		case 1:
			return "A standard device for capturing\nwild Pokemon";
		case 2:
			return "An upgraded device for capturing\nwild Pokemon";
		case 3:
			return "A very efficient device for\ncapturing wild Pokemon";
		case 4:
			return "Restores 20 HP";
		case 5:
			return "Restores 60 HP";
		case 6:
			return "Restores 200 HP";
		case 7:
			return "Restores a Pokemon's HP to full";
		case 8:
			return "Restores a Pokemon's HP to full\nand cures any status conditions";
		case 9:
			return "Cures a Pokemon of Poison";
		case 10:
			return "Cures a Pokemon of Sleep";
		case 11:
			return "Cures a Pokemon of Burn";
		case 12:
			return "Cures a Pokemon of Paralysis";
		case 13:
			return "Cures a Pokemon of Frostbite";
		case 14:
			return "Cures a Pokemon of any status\ncondition";
		case 15:
			return "Cures a Pokemon of any status\ncondition";
		case 16:
			return "Recovers a Pokemon from fainting\nwith 50% HP";
		case 17:
			return "Recovers a Pokemon from fainting\nwith full HP";
		case 18:
			return "Elevates a Pokemon by 1 level";
		case 19:
			return "Grants a Pokemon 100 friendship\npoints";
		case 20:
			return "Evolves a certain species of\nPokemon";
		case 21:
			return "Evolves a certain species of\nPokemon";
		case 22:
			return "Evolves a certain species of\nPokemon";
		case 23:
			return "Evolves a certain species of\nPokemon";
		case 24:
			return "Grants Masculine energy to\na Pokemon, evolving them\ninto their male evolution";
		case 25:
			return "Grants Feminine energy to\na Pokemon, evolving them\ninto their female evolution";
		case 26:
			return "Swaps a Pokemon's ability\nwith its other possible\nability";
		case 27:
			return "Maxes out an IV of choosing";
		case 28:
			return "Maxes out all IVs of a\nPokemon";
		case 29:
			return "Changes a Pokemon's nature\n to +Atk, -SpA";
		case 30:
			return "Changes a Pokemon's nature\n to +Def, -Atk";
		case 31:
			return "Changes a Pokemon's nature\n to +Atk, -Spe";
		case 32:
			return "Changes a Pokemon's nature\n to +SpD, -Atk";
		case 33:
			return "Changes a Pokemon's nature\n to +SpD, -SpA";
		case 34:
			return "Changes a Pokemon's nature\n to +Def, -SpA";
		case 35:
			return "Changes a Pokemon's nature\n to +Spe, -SpA";
		case 36:
			return "Changes a Pokemon's nature\n to +SpA, -Atk";
		case 37:
			return "Changes a Pokemon's nature\n to +SpA, -Spe";
		case 38:
			return "Changes a Pokemon's nature\n to Neutral";
		case 39:
			return "Changes a Pokemon's nature\n to +Spe, -Atk";
		case 40:
			return "Restores PP of a selected\nmove";
		case 41:
			return "Restores PP of all moves\non a Pokemon";
		case 42:
			return "Increases max PP of a\nselected move by 20%";
		case 43:
			return "Increases max PP of a\nselected move by its\nmax PP, which is 160%";
		case 44:
			return "Burns a selected Pokemon";
		case 45:
			return "";
		case 46:
			return "";
		case 47:
			return "";
		case 48:
			return "";
		case 49:
			return "";
		case 200:
			return "Calculates damage simulating\na battle";
		default:
			return "Teaches a Pokemon this move.";
		}
	}
	
	public String getDesc() {
		return desc;
	}

	public static JPanel displayGenerator(Player p) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));
		
		JComboBox<Pokemon> nameInput = new JComboBox<Pokemon>();
		for (int k = 1; k <= Pokemon.MAX_POKEMON; k++) {
        	nameInput.addItem(new Pokemon(k, 50, false, true));
        }
		result.add(nameInput);
		
		Integer[] levels = new Integer[100];
		for (int i = 0; i < 100; i++) {
			levels[i] = i + 1;
		}
		JComboBox<Integer> levelInput = new JComboBox<>(levels);
		result.add(levelInput);
		
		Ability[] abilityOptions = new Ability[2];
		for (int i = 0; i < 2; i++) {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.setAbility(i);
			abilityOptions[i] = test.ability;
		}
		JComboBox<Ability> abilityInput = new JComboBox<>(abilityOptions);
		result.add(abilityInput);
		
		JComboBox<String> natures = new JComboBox<>();
		Pokemon temp = new Pokemon(1, 1, false, false);
		for (int i = 0; i < 25; i++) {
			temp.nature = temp.getNature(i);
			natures.addItem(temp.getNature());
		}
		result.add(natures);
		
		@SuppressWarnings("unchecked")
		JComboBox<Integer>[] ivInputs = new JComboBox[6];
		Integer[] options = new Integer[32];
		for (int i = 0; i < 32; i++) {
			options[i] = i;
		}
		for (int i = 0; i < 6; i++) {
			ivInputs[i] = new JComboBox<Integer>(options);
			ivInputs[i].setSelectedIndex(31);
			AutoCompleteDecorator.decorate(ivInputs[i]);
			result.add(ivInputs[i]);
		}
		
		Integer[] happinessOptions = new Integer[256];
		for (int i = 0; i < happinessOptions.length; i++) {
			happinessOptions[i] = i;
		}
		JComboBox<Integer> happiness = new JComboBox<>(happinessOptions);
		happiness.setSelectedIndex(70);
		result.add(happiness);
		
		AutoCompleteDecorator.decorate(nameInput);
		AutoCompleteDecorator.decorate(levelInput);
		AutoCompleteDecorator.decorate(abilityInput);
		AutoCompleteDecorator.decorate(natures);
		AutoCompleteDecorator.decorate(happiness);
		
		JPanel movesPanel = new JPanel(new GridLayout(2, 2));
		@SuppressWarnings("unchecked")
		JComboBox<Move>[] moveInputs = new JComboBox[4];
		Move[] movebank = Move.values();
		Pokemon testMoves = (Pokemon) nameInput.getSelectedItem();
		testMoves.level = 1;
		testMoves.setMoves();
		for (int i = 0; i < 4; i++) {
			moveInputs[i] = new JComboBox<Move>(movebank);
			moveInputs[i].setSelectedItem(Move.STRUGGLE);
			Moveslot m = testMoves.moveset[i];
			Move m1 = m == null ? null : m.move;
			moveInputs[i].setSelectedItem(m1);
			AutoCompleteDecorator.decorate(moveInputs[i]);
			movesPanel.add(moveInputs[i]);
		}
		
		result.add(movesPanel);
		
		nameInput.addActionListener(f -> {
			abilityInput.removeAllItems();
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.level = (Integer) levelInput.getSelectedItem();
			test.setMoves();
			for (int i = 0; i < 2; i++) {
				test.setAbility(i);
				abilityInput.addItem(test.ability);
			}
			for (int j = 0; j < 4; j++) {
				moveInputs[j].setSelectedItem(Move.STRUGGLE);
				Moveslot m = test.moveset[j];
				Move m1 = m == null ? null : m.move;
				moveInputs[j].setSelectedItem(m1);
			}
		});
		
		levelInput.addActionListener(f -> {
			Pokemon test = (Pokemon) nameInput.getSelectedItem();
			test.level = (Integer) levelInput.getSelectedItem();
			test.setMoves();
			for (int j = 0; j < 4; j++) {
				moveInputs[j].setSelectedItem(Move.STRUGGLE);
				Moveslot m = test.moveset[j];
				Move m1 = m == null ? null : m.move;
				moveInputs[j].setSelectedItem(m1);
			}
		});
		
		JButton randomize = new JButton("RANDOMIZE");
		randomize.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseClicked(MouseEvent e) {
				Random random = new Random();
		    	if (SwingUtilities.isRightMouseButton(e)) {
		            nameInput.setSelectedIndex(random.nextInt(Pokemon.MAX_POKEMON));
		            levelInput.setSelectedIndex(random.nextInt(100));
		            happiness.setSelectedIndex(random.nextInt(255));
		            for (int i = 0; i < 4; i++) {
		            	moveInputs[i].setSelectedIndex(random.nextInt(Move.values().length));
		            }
		        }
		    	abilityInput.setSelectedIndex(random.nextInt(2));
		    	natures.setSelectedIndex(random.nextInt(25));
		    	for (int i = 0; i < 6; i++) {
		    		ivInputs[i].setSelectedIndex(random.nextInt(32));
		    	}
		    	
		    }
			
		});
		result.add(randomize);
		
		JButton generate = new JButton("GENERATE");
		generate.addActionListener(e -> {
			int id = ((Pokemon) nameInput.getSelectedItem()).id;
			int level = (Integer) levelInput.getSelectedItem();
			int ability = abilityInput.getSelectedIndex();
			String selectedNature = (String) natures.getSelectedItem();
			for (int i = 0; i < 25; i++) {
				temp.nature = temp.getNature(i);
				if (temp.getNature().equals(selectedNature)) break;
			}
			double[] nature = temp.nature;
			int[] ivs = new int[6];
			for (int i = 0; i < 6; i++) {
				ivs[i] = (Integer) ivInputs[i].getSelectedItem();
			}
			Move[] moveset = new Move[4];
			for (int i = 0; i < 4; i++) {
				moveset[i] = (Move) moveInputs[i].getSelectedItem();
			}
			
			Pokemon resultPokemon = new Pokemon(id, level, true, false);
			resultPokemon.abilitySlot = ability;
			resultPokemon.setAbility(resultPokemon.abilitySlot);
			resultPokemon.nature = nature;
			resultPokemon.ivs = ivs;
			resultPokemon.getStats();
			
			for (int i = 0; i < 4; i++) {
				resultPokemon.moveset[i] = moveset[i] == null ? null : new Moveslot(moveset[i]);
			}
			
			resultPokemon.happiness = (Integer) happiness.getSelectedItem();
			
			resultPokemon.heal();
			
			p.catchPokemon(resultPokemon);
			
		});
		result.add(generate);
		
		return result;
	}
}
