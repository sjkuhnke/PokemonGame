package Swing;

import java.io.Serializable;

public class Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int cost;
	private int healAmount;
	
	public Item(int i) {
		id = i;
		cost = setCost();
		if (id >= 4 && id <= 8) {
			switch(id) {
				case 4:
					healAmount = 20;
					break;
				case 5:
					healAmount = 60;
					break;
				case 6:
					healAmount = 120;
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
		else if (id == 5) return 150;
		else if (id == 6) return 300;
		else if (id == 7) return 500;
		else if (id == 8) return 750;
		else if (id == 9) return 25;
		else if (id == 10) return 25;
		else if (id == 11) return 25;
		else if (id == 12) return 25;
		else if (id == 13) return 25;
		else if (id == 14) return 150;
		else if (id == 15) return 100;
		else if (id == 16) return 500;
		else if (id == 17) return 1500;
		else if (id == 18) return 500;
		else if (id == 19) return 500;
		else if (id == 20) return 500;
		else if (id == 21) return 500;
		else if (id == 22) return 0;
		else if (id == 112) return 600;
		else if (id == 113) return 500;
		else if (id == 115) return 900;
		else if (id == 116) return 1100;
		else if (id == 123) return 800;
		else if (id == 124) return 900;
		else if (id == 126) return 250;
		else if (id == 138) return 1500;
		else if (id == 141) return 1000;
		else if (id == 142) return 1000;
		else if (id == 143) return 1000;
		else if (id == 144) return 1000;
		else if (id == 145) return 750;
		else if (id == 146) return 1500;
		else if (id == 149) return 500;
		else if (id == 150) return 750;
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
		if (id == 0) return "Repel";
		else if (id == 1) return "Pokeball";
		else if (id == 2) return "Great Ball";
		else if (id == 3) return "Ultra Ball";
		else if (id == 4) return "Potion";
		else if (id == 5) return "Super Potion";
		else if (id == 6) return "Hyper Potion";
		else if (id == 7) return "Max Potion";
		else if (id == 8) return "Full Restore";
		else if (id == 9) return "Antidote";
		else if (id == 10) return "Awakening";
		else if (id == 11) return "Burn Heal";
		else if (id == 12) return "Paralyze Heal";
		else if (id == 13) return "Freeze Heal";
		else if (id == 14) return "Full Heal";
		else if (id == 15) return "Rage Candy Bar";
		else if (id == 16) return "Revive";
		else if (id == 17) return "Max Revive";
		else if (id == 18) return "Rare Candy";
		else if (id == 19) return "Euphorian Gem";
		else if (id == 20) return "Leaf Stone";
		else if (id == 21) return "Dusk Stone";
		else if (id == 22) return "Dawn Stone";
		else if (id == 23) return "Ice Stone";
		else if (id == 24) return "Valiant Gem";
		else if (id == 25) return "Petticoat Gem";
		else if (id == 26) return "Ability Capsule";
		else if (id == 93) return "HM01";
		else if (id == 94) return "HM02";
		else if (id == 95) return "HM03";
		else if (id == 96) return "HM04";
		else if (id == 97) return "HM05";
		else if (id == 98) return "HM06";
		else if (id == 99) return "HM07";
		else if (id == 100) return "HM08";
		else if (id == 101) return "TM01";
		else if (id == 102) return "TM02";
		else if (id == 103) return "TM03";
		else if (id == 104) return "TM04";
		else if (id == 105) return "TM05";
		else if (id == 106) return "TM06";
		else if (id == 107) return "TM07";
		else if (id == 108) return "TM08";
		else if (id == 109) return "TM09";
		else if (id == 110) return "TM10";
		else if (id == 111) return "TM11";
		else if (id == 112) return "TM12";
		else if (id == 113) return "TM13";
		else if (id == 114) return "TM14";
		else if (id == 115) return "TM15";
		else if (id == 116) return "TM16";
		else if (id == 117) return "TM17";
		else if (id == 118) return "TM18";
		else if (id == 119) return "TM19";
		else if (id == 120) return "TM20";
		else if (id == 121) return "TM21";
		else if (id == 122) return "TM22";
		else if (id == 123) return "TM23";
		else if (id == 124) return "TM24";
		else if (id == 125) return "TM25";
		else if (id == 126) return "TM26";
		else if (id == 127) return "TM27";
		else if (id == 128) return "TM28";
		else if (id == 129) return "TM29";
		else if (id == 130) return "TM30";
		else if (id == 131) return "TM31";
		else if (id == 132) return "TM32";
		else if (id == 133) return "TM33";
		else if (id == 134) return "TM34";
		else if (id == 135) return "TM35";
		else if (id == 136) return "TM36";
		else if (id == 137) return "TM37";
		else if (id == 138) return "TM38";
		else if (id == 139) return "TM39";
		else if (id == 140) return "TM40";
		else if (id == 141) return "TM41";
		else if (id == 142) return "TM42";
		else if (id == 143) return "TM43";
		else if (id == 144) return "TM44";
		else if (id == 145) return "TM45";
		else if (id == 146) return "TM46";
		else if (id == 147) return "TM47";
		else if (id == 148) return "TM48";
		else if (id == 149) return "TM49";
		else if (id == 150) return "TM50";
		else if (id == 151) return "TM51";
		else if (id == 152) return "TM52";
		else if (id == 153) return "TM53";
		else if (id == 154) return "TM54";
		else if (id == 155) return "TM55";
		else if (id == 156) return "TM56";
		else if (id == 157) return "TM57";
		else if (id == 158) return "TM58";
		else if (id == 159) return "TM59";
		else if (id == 160) return "TM60";
		else if (id == 161) return "TM61";
		else if (id == 162) return "TM62";
		else if (id == 163) return "TM63";
		else if (id == 164) return "TM64";
		else if (id == 165) return "TM65";
		else if (id == 166) return "TM66";
		else if (id == 167) return "TM67";
		else if (id == 168) return "TM68";
		else if (id == 169) return "TM69";
		else if (id == 170) return "TM70";
		else if (id == 171) return "TM71";
		else if (id == 172) return "TM72";
		else if (id == 173) return "TM73";
		else if (id == 174) return "TM74";
		else if (id == 175) return "TM75";
		else if (id == 176) return "TM76";
		else if (id == 177) return "TM77";
		else if (id == 178) return "TM78";
		else if (id == 179) return "TM79";
		else if (id == 180) return "TM80";
		else if (id == 181) return "TM81";
		else if (id == 182) return "TM82";
		else if (id == 183) return "TM83";
		else if (id == 184) return "TM84";
		else if (id == 185) return "TM85";
		else if (id == 186) return "TM86";
		else if (id == 187) return "TM87";
		else if (id == 188) return "TM88";
		else if (id == 189) return "TM89";
		else if (id == 190) return "TM90";
		else if (id == 191) return "TM91";
		else if (id == 192) return "TM92";
		else if (id == 193) return "TM93";
		else if (id == 194) return "TM94";
		else if (id == 195) return "TM95";
		else if (id == 196) return "TM96";
		else if (id == 197) return "TM97";
		else if (id == 198) return "TM98";
		else if (id == 199) return "TM99";
		return "";
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
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true }, // 003
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true },
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,true ,false,false,false,true }, // 005
				{false,false,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true }, 
				{true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 007
				{true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 008
				{true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 010
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 012
				{false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 014
				{true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 016
				{true ,true ,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 018
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 020
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 022
				{true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 024
				{true ,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 026
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,true ,false,false,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 028
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,false,true }, // 030
				{false,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true }, // 032
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,true },
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,false,true }, // 034
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true },
															//+1   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true }, // 036
				{true ,false,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,false,true ,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,false,false,false,true ,false,false,false,false,true },
				{false,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 038
				{false,true ,true ,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,true ,false,false,true ,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 040
				{true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 042
				{true ,true ,false,false,false,true ,true ,false,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 044
				{false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true },
				{false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,true }, // 046
				{false,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,true ,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 048
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 050
				{true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 052
				{true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 054
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 056
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 058
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,true ,false,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 060
				{false,true ,true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 062
				{false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 064
				{true ,true ,false,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 066
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 068
				{true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 070
				{false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,true ,true ,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,false,true ,true ,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 072
				{true ,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
															//+2   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,true ,false,false,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 074
				{false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 076
				{false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,true ,true ,true ,false,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 078
				{true ,false,true ,true ,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 080
				{true ,false,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,false,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,true ,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 082
				{false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 084
				{false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 086
				{false,false,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 088
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,false,false,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 090
				{true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 092
				{true ,true ,false,false,false,false,false,true ,true ,false,false,false,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,true ,false,false,false,true ,true ,true ,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,true ,false,false,true ,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 094
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,true ,true ,false,false,false,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 096
				{false,false,false,false,true ,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,true ,true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 098
				{true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,true ,false,false,true ,false,false,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,true ,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,true ,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 100
				{false,false,false,false,false,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 102
				{false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 104
				{false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 106
				{false,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,true ,true ,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 108
				{true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 110
				{false,false,false,false,false,false,false,false,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 112
															//+3   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,true ,true ,false,true ,true ,true ,false,true ,false,true ,true ,false,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,true ,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 114
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,false,false,false,true ,true ,true ,true ,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,true ,true ,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,false,true ,false,false,true ,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 116
				{false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,true ,false,false,true ,false,false,false,false,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,false,false,true ,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 118
				{true ,true ,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,true ,false,false,true ,false,true ,false,false,false,true ,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,true ,false,false,false,true ,false,false,false,true ,false,true ,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 120
				{true ,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,true ,false,false,false,false,true ,false,true ,false,true ,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,false,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,true ,false,true ,true ,true ,true ,true ,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 122
				{true ,false,false,false,false,false,false,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,true ,false,false,false,true ,true ,false,false,false,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 124
				{true ,true ,true ,false,false,true ,true ,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,true ,true ,false,false,true ,false,true ,true ,false,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,false,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,true ,false,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 126
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,true ,false,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,true ,true ,false,true ,true ,false,true ,false,false,false,true ,false,false,true ,true ,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,true ,false,false,false,true ,true ,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,false,false,false,true ,false,false,true ,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 128
				{true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 130
				{true ,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,true ,true ,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,false,false,false,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 132
				{true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,true ,true ,true ,false,false,false,false,false,true ,true ,true ,false,true ,false,true ,false,false,true ,false,false,true ,true ,false,true ,false,true ,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 134
				{false,false,false,true ,false,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,true ,false,true ,false,false,false,false,false,true ,true ,false,true ,false,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 136
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 138
				{false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 140
				{true ,true ,false,true ,false,false,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,false,false,true ,false,false,false,true ,false,false,false,true ,true ,false,true ,true ,false,false,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,false,false,false,true ,false,true ,true ,false,false,true ,false,false,true ,false,false,false,false,true ,false,false,true ,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,true ,true ,false,false,false,true ,true ,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 142
				{false,false,true ,true ,true ,false,false,false,false,false,true ,true ,false,true ,true ,false,true ,true ,true ,false,false,false,false,false,true ,false,true ,false,false,false,true ,true ,true ,true ,false,false,false,false,false,false,false,true ,false,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,false,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,true ,true ,false,false,false,true ,true ,true ,false,true ,false,false,false,false,true ,false,true ,false,true ,true ,true ,true ,true ,false,false,true ,false,true ,false,false,true ,false,false,true ,false,false,false,true ,false,true ,true ,false,true ,false,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 144
				{true ,true ,true ,true ,true ,true ,false,false,true ,true ,false,false,true ,true ,false,false,false,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,true ,false,false,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,true ,false,true ,true ,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 146
				{true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,true ,false,true ,false,false,true ,false,true ,true ,true ,true ,false,true ,true ,true ,false,false,true ,true ,false,true ,false,false,false,true ,false,false,false,false,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 148
				{true ,true ,false,true ,false,false,true ,false,true ,false,true ,false,true ,true ,false,true ,false,false,true ,true ,false,true ,false,false,true ,true ,true ,true ,true ,true ,false,true ,true ,true ,true ,true ,true ,true ,false,true ,true ,false,false,true ,true ,false,false,true ,true ,false,true ,true ,false,false,false,true ,true ,true ,true ,false,true ,true ,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,true ,false,false,false,false,false,false,true ,false,false,false,false,false,false,true ,false,true ,false,false,false,true ,false,false,false,false,false,false,true ,false,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,true ,false,true ,false,false,false,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 150
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 152
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 154
															//+4   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{true ,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 156
				{true ,true ,false,false,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 158
				{true ,true ,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 159
				{false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 161
				{true ,true ,false,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 163
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 165
				{true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 167
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 169
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 171
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 173
				{true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 175
				{true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 177
				{true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 179
				{true ,false,true ,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 181
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 183
				{true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 185
				{true ,true ,true ,true ,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 187
				{true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,true ,true ,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 189
				{false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 191
				{true ,true ,false,true ,true ,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,true ,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 193
				{true ,true ,false,true ,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 195
				{true ,true ,false,false,false,false,true ,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true ,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
															//+5   1     2     3     4	   5	 6     7     8     9    10    11    12    13    14    15    16    17    18    19    20    21    22    23    24    25    26    27    28    29    30    31    32    33    34    35    36    37    38    39    40    41    42    43    44    45    46    47    48    49    50    51    52    53    54    55    56    57    58    59    60    61    62    63    64    65    66    67    68    69    70    71    72    73    74    75    76    77    78    79    80    81    82    83    84    85    86    87    88    89    90    91    92    93    94    95    96    97    98    99
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 197
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 199
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 201
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 203
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 205
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 207
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 209
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 211
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 213
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 215
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 217
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 219
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 221
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 223
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 225
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 227
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 229
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 231
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 233
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true },
				{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,true }, // 235
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
			result = Move.FLY;
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
			result = Move.U_TURN;
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
			result = Move.WILL_O_WISP;
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
			result = Move.FREEZE_DRY;
			break;
		case 161:
			result = Move.SMACK_DOWN;
			break;
		case 162:
			result = Move.SILVER_WIND;
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
			result = Move.X_SCISSOR;
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
			result = Move.TRI_ATTACK;
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
		else return Status.AUTO;
		
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
}
