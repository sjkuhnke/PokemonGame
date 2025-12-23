package overworld;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import entity.*;
import pokemon.*;
import puzzle.Puzzle;

public class Script {
	
	private GamePanel gp;
	private PlayerCharacter player;
	private Player p;
	private HashMap<Double, Consumer<Entity>> scriptMap;
	
	public Script(GamePanel gp) {
		this.gp = gp;
		this.player = gp.player;
		this.p = gp.player.p;
		scriptMap = new HashMap<>();
		initializeScripts();
	}
	
	private void initializeScripts() {
		scriptMap.put(52.0, (npc) -> { // professor dad
			if (!p.flag[0][0]) {
				p.flag[0][0] = true;
				Task.addTask(Task.DIALOGUE, npc, "Well hiya there son!");
				Task.addTask(Task.DIALOGUE, npc, "Welcome to the wonderful world of Pokemon! I'm sure you're familiar with them considering your old man is a top scientist here in Xhenos.");
				Task.addTask(Task.DIALOGUE, npc, "But, as a reminder, Pokemon are pocket monsters with unique typings and traits that coexist with us humans.");
				Task.addTask(Task.DIALOGUE, npc, "And I think it's high time you go on a Pokemon adventure of your own, get outta town, and see what this region has to offer.");
				Task.addTask(Task.DIALOGUE, npc, "You see that machine over there son? I've started a program to help new trainers, and you and two other bright young boys are to be the first participants.");
				Task.addTask(Task.DIALOGUE, npc, "Inside that machine holds three Pokemon I have chosen to be the starting options. So go pick your starter!");
			} else if (p.flag[0][0] && !p.flag[0][1]) {
				Task.addTask(Task.DIALOGUE, npc, "Go ahead, go pick your starter from my machine! The world of Xhenos awaits!");
			} else if (p.flag[0][1] && !p.flag[0][2]) {
				p.flag[0][2] = true;
				Task.addTask(Task.DIALOGUE, npc, "You picked " + p.team[0].getName() + "? Wonderful pick! It seems very eager to get out and explore, as I'm sure you are too.");
				Task.addTask(Task.DIALOGUE, npc, "But!");
				Task.addTask(Task.DIALOGUE, npc, "As your Dad, I must make sure you have adequate equipment to not get lost out there.");
				Task.addTask(Task.DIALOGUE, npc, "So first, I'm giving you this digital map, equipped with Cellular Data that will always update with where you are!");
				Task t = Task.addTask(Task.TEXT, "You got the Map!", 5);
				t.ui = Task.MENU_ICON;
				Task.addTask(Task.DIALOGUE, npc, "And as your Professor, I need your help for collecting as much data about the Pokemon inhabiting our world with us!");
				Task.addTask(Task.DIALOGUE, npc, "This little doohickey is the Neodex! In a region as unique as ours, I've had to make plenty of modifications to account for them.");
				Task.addTask(Task.DIALOGUE, npc, "It's one of my finest inventions yet, and I even got help from Professor Oak, the greatest professor of all time!");
				Task.addTask(Task.DIALOGUE, npc, "Instead of Rotom, it taps into a shared database that allows for identifying new forms for old Pokemon. Give it a whirl!");
				t = Task.addTask(Task.TEXT, "You got the Pokedex!", 0);
				t.ui = Task.MENU_ICON;
				Task.addTask(Task.DIALOGUE, npc, "Oh right! Speaking of collecting data, I made a little something to help you find as many different species as you can.");
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.DEX_NAV;
				Task.addTask(Task.DIALOGUE, npc, "Just open your bag and open the \"Key Items\" pocket, you should see it there. When you're near grass, it'll show you the Pokemon there!");
				Task.addTask(Task.DIALOGUE, npc, "I would've installed it in the Neodex, but I have all of the slots reserved for special database add-ons to keep track of all the unique forms here.");
				Task.addTask(Task.DIALOGUE, npc, "Speaking of which, as you know son, my specialty as a Professor is studying Shadow Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "These Pokemon had their DNA changed long ago by the meteor in Shadow Ravine, which happens to be just north of here.");
				Task.addTask(Task.DIALOGUE, npc, "They often have taken Dark and Ghost typings, and have significant changes in mentality, similar to Xhenovian Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "Here, can I see your Pokedex for a second?");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "There, I added a 'Shadow Pokedex' section to your Neodex. That way, if you run into any Shadow forms near Shadow Ravine, you can record them!");
				Task.addTask(Task.DIALOGUE, npc, "...What's that? Oh right, I should probably explain how to use the Neodex.");
				Task.addTask(Task.DIALOGUE, npc, "You can see a Pokemon you've registered in the Neodex and their information, like their moves, abilities, typings, and much more!");
				Task.addTask(Task.DIALOGUE, npc, "If you want to toggle which section you're in, just press the 'A' key and it will cycle through all of your sections you have installed!");
				Task.addTask(Task.DIALOGUE, npc, "Oh and one more thing! Oh my Arceus, I can't believe I almost forgot to tell you about my favorite creation yet!");
				Task.addTask(Task.DIALOGUE, npc, "You've always loved battling, so I made this just for you. A state of the line BATTLECALC 3000! Or you could just call it your calculator.");
				Task.addTask(Task.DIALOGUE, npc, "It provides accurate data during or outside of a battle, just press 'C', and you can check how much damage a move can do!");
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.CALCULATOR;
				Task.addTask(Task.DIALOGUE, npc, "Now go out there and make me proud - and most importantly...");
				Task.addTask(Task.DIALOGUE, npc, "COLLECT THAT DATA!");
				Task.addTask(Task.UPDATE, "");
			} else if (p.flag[8][1] && !p.flag[8][2]) { // cutscene for lava surf
				p.flag[8][2] = true;
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', gp.tileSize * 37, player, false, 4);
				Task.addTask(Task.TURN, gp.npc[52][2], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "<@>! You're back! You're in one piece!");
				Task.addTask(Task.DIALOGUE, npc, "I mean - I knew you would be, but also I didn't, and I wrote two eulogies just in case.");
				Task.addTask(Task.DIALOGUE, npc, "BUT! You're here!");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('x', gp.tileSize * 32, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, gp.npc[52][2], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "And you must be the girlfriend! Robin said you were -");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[52][2], "Nope! I'm into women. And taken, thank you very much.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, npc, "Ah - right, yes - modern youth -");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "- uh, wonderful! Good for you!");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('y', gp.tileSize * 34, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "Anyway! You saved the planet.");
				Task.addTask(Task.SLEEP, "", 70);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('x', gp.tileSize * 31, npc, false, 2);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "From a space god.");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 25);
				Task.addNPCMoveTask('y', gp.tileSize * 36, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 35);
				Task.addTask(Task.DIALOGUE, npc, "That deserves more than a fruit basket.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM08;
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('x', gp.tileSize * 32, npc, false, 2);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.DIALOGUE, npc, "HM08 Lava Surf. Let's you cross lava, anywhere.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addNPCMoveTask('x', gp.tileSize * 31, npc, false, 2);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.DIALOGUE, npc, "I gave it to Scott and Fred too. They've been training together. Bonding. Being not-angsty.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPOT, player, "");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, player, "Wait - Fred? You gave Fred Lava Surf?");
				Task.addTask(Task.SPEAK, player, "You mean the half-alien formerly-Eclipse Fred?!");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "The very one. Apparently all it took was a near-apocalypse to work out his trust issues.");
				Task.addTask(Task.DIALOGUE, npc, "He and Scott have been inseparable. Real 'odd couple goes lava-boarding' situation.");
				Task.addTask(Task.DIALOGUE, npc, "They're somewhere out on Route 43, trying to reach Checkpoint Charlie past the lava.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "You should head that way too - it's Elite Four territory.");
				Task.addTask(Task.DIALOGUE, npc, "If you're serious about being the best, that's your next stop.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[52][2], "I was just thinking that. And now we've got the tech to get across lava.");
				Task.addTask(Task.DIALOGUE, gp.npc[52][2], "Professor, it was great to meet you.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[52][2], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[52][2], "And <@> - you better not keep me waiting. I've got a score to settle.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "Shame she only likes girls. You two would've made a power couple.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "Anyway - rest up if you need to, then get out there.");
				Task.addTask(Task.DIALOGUE, npc, "Xhenos is waiting. And your friends are too.");
			} else if (p.flag[8][2] && !p.flag[8][3] && !p.flag[8][4]) { // after lava surf but before fred or scott
				Task.addTask(Task.DIALOGUE, npc, "Well hiya there son!");
				Task.addTask(Task.DIALOGUE, npc, "All rested up? Route 43 just opened up now that you've got Lava Surf.");
				Task.addTask(Task.DIALOGUE, npc, "Scott and Fred are out there, waiting...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Once you reach the end, you'll hit Checkpoint Charlie - the gate to the Leviathan League.");
				Task.addTask(Task.DIALOGUE, npc, "And don't be surprised if someone you know is waiting for you at the top.");
				Task.addTask(Task.DIALOGUE, npc, "The League has a funny way of recognizing greatness... before it's even official.");
				Task.addTask(Task.SLEEP, "", 20);
				if (!p.flag[0][21]) Task.addTask(Task.DIALOGUE, npc, "Also...");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Well hiya there son!");
				Task.addTask(Task.DIALOGUE, npc, "How's it going?");
			}
			if (p.flag[0][5] && !p.flag[0][21]) {
				Task.addTask(Task.DIALOGUE, npc, "Have you seen any new Shadow forms? Can I take a look?");
				Pokemon[] sDex = p.getDexType(1);
				int amt = 0;
				for (Pokemon po : sDex) {
					if (p.pokedex[po.id] == 2) amt++;
				}
				if (!p.flag[0][20] && amt >= 1) {
					String plural = amt > 1 ? "s!" : "!";
					Task.addTask(Task.DIALOGUE, npc, "Oh nice! You've caught " + amt + " form" + plural);
					Task.addTask(Task.DIALOGUE, npc, "Here son, take this for helping me out! You'd get better use out of it than me, anyways!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.AMULET_COIN;
					p.flag[0][20] = true;
				}
				if (amt >= Pokemon.POKEDEX_METEOR_SIZE) {
					Task.addTask(Task.DIALOGUE, npc, "<@>!! You did it! You finished the Shadow Pokedex I gave you!");
					Task.addTask(Task.DIALOGUE, npc, "This item is extremely rare, so please, use it wisely!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.MASTER_BALL;
					p.flag[0][21] = true;
				}
			}
		});
		
		scriptMap.put(51.0, (npc) -> {
			if (!p.flag[0][3]) { // dad's mom
				Task.addTask(Task.DIALOGUE, npc, "Ah, I remember my first time having a Pokemon adventure, before I had your father. I was around your age now actually. Brings back fond memories...");
				Task.addTask(Task.DIALOGUE, npc, "...Oh, you want advice? I believe being a Pokemon trainer isn't about being the strongest, or collecting them all.");
				Task.addTask(Task.DIALOGUE, npc, "It's about forming a life-long bond with your Pokemon, becoming partners. Friendship is key to becoming a better trainer, and a better person.");
				Task.addTask(Task.DIALOGUE, npc, "That's how it was with your grandfather. Back in my day, I wanted to be the best there ever was.");
				Task.addTask(Task.DIALOGUE, npc, "He helped me see the value in friendship, how Pokemon were great companions in life. He's the person who gave me Duchess' dinky old Soothe Bell.");
				Task.addTask(Task.DIALOGUE, npc, "I miss him every day, even with my Pokemon, you and your father by my side.");
				Task.addTask(Task.DIALOGUE, npc, "So, this is my parting gift to you, to show you the power of friendship. I'm so thankful to have been your grandmother.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.SOOTHE_BELL;
				Task.addTask(Task.DIALOGUE, npc, "Go out there and make the world a better place, dear. I know you will.");
				p.flag[0][3] = true;
			}
		});
		
		scriptMap.put(3.0, (npc) -> { // avery
			Task.addTask(Task.DIALOGUE, npc, "I believe he was looking to introduce himself to you, he mentioned he was heading towards New Minnow Town.");
			Task.addTask(Task.DIALOGUE, npc, "Please do make haste, I sure hope he's okay.");
			
			if (!p.flag[0][4] && p.getDexAmounts(p.getDexType(0))[1] <= 2) {
				Task.addTask(Task.DIALOGUE, npc, "Oh! And I reckon he's itching to have a Pokemon battle.");
				Task.addTask(Task.DIALOGUE, npc, "I trust you won't be underprepared, but since you haven't caught any wild Pokemon yet, here you are.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.POKEBALL;
				t.counter = 5;
			}
			p.flag[0][4] = true;
		});
		
		scriptMap.put(47.0, (npc) -> { // second starter
			Task.addTask(Task.DIALOGUE, npc, "Here, we breed and house rare Pokemon to fight against their extinction.");
			Task.addTask(Task.DIALOGUE, npc, "...What's that? You have a " + Pokemon.getName(((p.starter + 1) * 3) - 2) + "?? That's insanely rare. Did you get that from the professor?");
			Task.addTask(Task.DIALOGUE, npc, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task t = Task.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		});
		
		scriptMap.put(4.0, (npc) -> { // tn grunt warehouse
			if (p.flag[0][11] && !p.flag[0][12]) {
				p.flag[0][12] = true;
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "What, you've got a package, pipsqueak? Pfft! You can't be older than 10... Didn't know Robin stooped that low.");
				Task.addTask(Task.DIALOGUE, npc, "Last time I checked, the boss wasn't expecting any packages. You can't even get in here anyway, I JUST locked the door and I'm getting the hell outta here.");
				Task.addTask(Task.DIALOGUE, npc, "I'm running off to the woods and you'll NEVER find me in there, don't even TRY to follow me.");
				Task.addTask(Task.DIALOGUE, npc, "So yeah, big whoop. Tell that bundle of nerves - A.K.A. your boss - to deal with it. See ya idiot!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "BEAT IT!");
			}
		});
		
		scriptMap.put(161.0, (npc) -> { // robin
			if (!p.flag[0][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Sorry, I'm a little busy right now. As the only mailman in this entire region, I've been running ragged delivering letters across Xhenos.");
				Task.addTask(Task.DIALOGUE, npc, "I can't afford to waste any time, even for a gym battle.");
				Task.addTask(Task.DIALOGUE, npc, "Oh? You'll help me? Thank you, thank you, thank you! Maybe then I'll have enough time to open the gym... Here, take these!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_A;
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_B;
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.PACKAGE_C;
				p.flag[0][7] = true;
			} else if (p.flag[0][7] && !p.flag[0][11]) {
				if (p.flag[0][8] && p.flag[0][9] && p.flag[0][10]) {
					Task.addTask(Task.DIALOGUE, npc, "Oh it's you, thanks for delivering all those packages! You're a lifesaver, and I truly thank you for being selfless.");
					Task.addTask(Task.DIALOGUE, npc, "But... there's one last package to deliver.");
					Task.addTask(Task.DIALOGUE, npc, "It's been sitting here for weeks for the warehouse, but I can't get in to the building and the owner isn't answering any of my calls.");
					Task.addTask(Task.DIALOGUE, npc, "I've been far too busy to look into it though, could you look into it for me? I've seen some pretty suspicious activity going on there.");
					Task.addTask(Task.DIALOGUE, npc, "Oh, you will? Thank you so much! Don't worry, it's just a short walk away. Once that's done, I'll have a small window where I can open the gym.");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.PACKAGE_D;
					p.flag[0][11] = true;
					Task.addTask(Task.DIALOGUE, npc, "Welp, the clock is ticking, and I need more caffeine.");
				} else {
					Task.addTask(Task.DIALOGUE, npc, "The addresses are on the back of the boxes. You're doing me a massive favor, so once you're done I can give you that battle. So get to it!");
				}
			} else if (p.flag[0][11] && !p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello my helper! How's it going at the warehouse? Did you manage to get in okay?");
			} else if (p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you've delivered my last package? I can't thank you enough, I can finally have a break from all this mail. I'll open the gym up right away, with no delay!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				p.flag[0][16] = true;
			}
		});
		
		scriptMap.put(57.0, (npc) -> { // guy eddie
			if (p.flag[0][7] && !p.flag[0][9]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh! That's my new frying pan! Thanks squirt, I owe you something.");
				Task.addTask(Task.DIALOGUE, npc, "I got a little trinket I've been wanting to get rid of - I mean pass off - to a worthy kiddo like you. Consider this your appetizer!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.FLAME_ORB;
				p.bag.remove(Item.PACKAGE_B);
				p.flag[0][9] = true;
			} else if (p.flag[0][7]) {
				if (!p.flag[0][19]) {
					Task.addTask(Task.DIALOGUE, npc, "Hey, thanks for the help squirt! Listen, I have a 5-star restaraunt in Rawwar City, you should come check it out!");
				}
				Task.addTask(Task.CONFIRM, npc, "Interested in coming to Rawwar City with me?", 5);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "What...? You've never heard of me? I have the most famous restaurant in Xhenos!");
			}
		});
		
		scriptMap.put(58.0, (npc) -> { // ms plum
			if (p.flag[0][7] && !p.flag[0][17]) {
				if (!p.flag[0][8]) {
					Task.addTask(Task.DIALOGUE, npc, "...Oh, is that my package?");
					Task.addTask(Task.DIALOGUE, npc, "Thank you so much young man! Sorry for jumping at you, I'm just really unsettled right now.");
					p.bag.remove(Item.PACKAGE_A);
					p.flag[0][8] = true;
				} else {
					Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				}
				Task.addTask(Task.DIALOGUE, npc, "Some rampant Pokemon sniffing out berries all burst into my house, but I was just making juice.");
				Task.addTask(Task.DIALOGUE, npc, "In the midst of all the chaos, some crook snuck in and stole my precious item right off my poor Pokemon!");
				Task.addTask(Task.DIALOGUE, npc, "Whatever will I do to find it?");
			} else if (p.flag[0][7] && p.flag[0][17]) {
				p.flag[0][18] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Task.addTask(Task.DIALOGUE, npc, "Oh my god, my item! You found it! Wow, you really are impressive!");
				Task.addTask(Task.DIALOGUE, npc, "You know what, since you're so kind, why don't you just keep it? It'll be better off with a stronger trainer like you.");
				Task.addTask(Task.DIALOGUE, npc, "Here, take this for your troubles too. Really, it's the least I can do.");
				Task t = Task.addTask(Task.ITEM, "Obtained a Lucky Egg!");
				t.item = Item.LUCKY_EGG;
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Oh, hello there. Sorry for being alarmed, I'm just a little unsettled right now.");
				Task.addTask(Task.DIALOGUE, npc, "I just got robbed! And at the worst time too, I was expecting a new juicer I was so excited for...");
				Task.addTask(Task.DIALOGUE, npc, "And I heard a noise at my door thinking it was Robin with my package, but it was a criminal who burst right in!");
				Task.addTask(Task.DIALOGUE, npc, "It's times like this where I wish I was still married.");
			}
		});
		
		scriptMap.put(48.0, (npc) -> { // pound town
			if (!p.flag[0][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Feel free to look around!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you have a package for us? Thank you so much! I'm sure Robin greatly appreciates the help!");
				Task.addTask(Task.DIALOGUE, npc, "Here, have a complimentary gift dog! No really, we insist.");
				
				Random dog = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = dog.nextInt(3);
				id = 120 + (id * 3);
				Pokemon dogP = new Pokemon(id, 5, true, false);
				Task.addTask(Task.TEXT, "You adopted a gift dog!");
				Task t = Task.addTask(Task.GIFT, "", dogP);
				t.item = Item.SILK_SCARF;
				p.bag.remove(Item.PACKAGE_C);
				p.flag[0][10] = true;
			}
		});
		
		scriptMap.put(46.0, (npc) -> {
			Task.addTask(Task.HP, "Check your team's Hidden Power types here!");
		});
		
		scriptMap.put(8.0, (npc) -> { // warehouse owner
			Task.addTask(Task.DIALOGUE, npc, "And at the worst time too, I was expecting a package all the way from Galar.");
			if (p.flag[0][14] && !p.flag[0][15]) {
				Task.addTask(Task.DIALOGUE, npc, "What's that? You have the package for me? That must be my lucky stapler! Please hand it here, young one.");
				Task.addTask(Task.DIALOGUE, npc, "Robin must be swamped if he's making you do all this delivery work. Is this his version of a gym puzzle?");
				Task.addTask(Task.DIALOGUE, npc, "Heh! I'm just messing with you, you must have volunteered. Here, for your generosity and service.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM01;
				p.bag.remove(Item.PACKAGE_D);
				p.flag[0][15] = true;
			} else if (!p.flag[0][14]) {
				Task.addTask(Task.DIALOGUE, npc, "Can you please help me get rid of these criminals? I don't have any Pokemon of my own, I won't be of much help to you I'm afraid.");
			}
		});
		
		scriptMap.put(10.0, (npc) -> { // ryder 1
			Task.addTask(Task.DIALOGUE, npc, "That's hello where I come from. I'm Ryder, adventurer extraordinaire at the ripe old age of 16.");
			Task.addTask(Task.DIALOGUE, npc, "Say, you look like a competent Pokemon trainer. Mind taking care of something for me?");
			Pokemon abra = new Pokemon(243, 15, true, false);
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			if (gift.nextDouble() < 0.4 && abra.getAbility(2) != Ability.NULL) {
				abra.abilitySlot = 2;
				abra.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + abra.name() + "!");
			Task.addTask(Task.GIFT, "", abra);
			Task.addTask(Task.DIALOGUE, npc, "Yeah, I noticed Abra seems to gain a new ability here, probably because of that new magic type I've been hearing about.");
			Task.addTask(Task.DIALOGUE, npc, "Anyways, it was pleasure doing business with you mate, I'm sure I'll see you around. I gotta see what the rest of the region has in store for me!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			p.flag[1][3] = true;
		});
		
		scriptMap.put(22.0, (npc) -> { // shell bell
			Task.addTask(Task.DIALOGUE, npc, "Want to know a piece of history? This lake of lava here was formerly a beautiful blue body of water!");
			Task.addTask(Task.DIALOGUE, npc, "A long long time ago, before the volcano Mt. St. Joseph was active, this area flourished with life everywhere!");
			Task.addTask(Task.DIALOGUE, npc, "Right where I'm standing used to be a vibrant beach full of shells and coral!");
			Task.addTask(Task.DIALOGUE, npc, "In fact, if you look hard enough, you might be able to still see some fragrants!");
			Task.addTask(Task.DIALOGUE, npc, "I've spent a while collecting shell pieces to make a special item for a Pokemon to hold, called a Shell Bell!");
			Task.addTask(Task.DIALOGUE, npc, "You may have heard of the item before, but this one is special! It heals your Pokemon a whopping 25% of any damage dealt!");
			Task.addTask(Task.DIALOGUE, npc, "I'm willing to part with it, but for a price. You see, my Cleffa and Azurill will only evolve when they're happy enough...");
			Task.addTask(Task.DIALOGUE, npc, "But they're far too weak to train on their own, so I'd like to use the unique item to this region, being the Euphorian Gem!");
			Task t = Task.addTask(Task.CONFIRM, npc, "If you have 2 Euphorian Gems to give me, I'll give you this Shell Bell. Do we have a deal?", 6);
			t.ui = Task.ITEM;
			t.item = Item.EUPHORIAN_GEM;
		});
		
		scriptMap.put(11.0, (npc) -> { // fred 1
			if (!p.flag[1][0]) {
				if (npc.worldX < 39 * gp.tileSize || npc.worldY > gp.tileSize * 65) {
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					if (player.worldX < 39 * gp.tileSize) {
						Task.addTask(Task.TURN, player, "", Task.RIGHT);
					} else {
						Task.addTask(Task.TURN, player, "", Task.LEFT);
					}
					Task.addNPCMoveTask('x', 39 * gp.tileSize, player, false, 1);
					if (player.worldY < 64 * gp.tileSize) {
						Task.addTask(Task.TURN, player, "", Task.DOWN);
						Task.addNPCMoveTask('y', 64 * gp.tileSize, player, false, 1);
					}
					Task.addTask(Task.TURN, player, "", Task.LEFT);
					Task.addNPCMoveTask('x', 32 * gp.tileSize, npc, false, 6);
					Task.addTask(Task.TURN, npc, "", Task.UP);
					Task.addNPCMoveTask('y', 65 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addNPCMoveTask('x', 33 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, npc, "", Task.UP);
					Task.addNPCMoveTask('y', 64 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addNPCMoveTask('x', 34 * gp.tileSize, npc, false, 2);
					Task.addTask(Task.SLEEP, "", 60);
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addTask(Task.SLEEP, "", 30);
					Task.addTask(Task.TURN, gp.npc[11][9], "", Task.UP);
					Task.addTask(Task.SLEEP, "", 30);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addTask(Task.SLEEP, "", 60);
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.SPOT, npc, "");
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addNPCMoveTask('y', 66 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, player, "", Task.DOWN);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addNPCMoveTask('x', 39 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, npc, "", Task.UP);
					Task.addTask(Task.SLEEP, "", 30);
					Task.addTask(Task.INTERACTIVE, gp.iTile[11][0], "", 0);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addNPCMoveTask('y', 65 * gp.tileSize, npc, false, 2);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.DIALOGUE, npc, "Heh, you must be pretty tough to make it this far, but don't get too full of yourself.");
					Task.addTask(Task.DIALOGUE, npc, "I'll gladly put an end to your winning streak right here.");
				} else {
					Task.addTask(Task.DIALOGUE, npc, "Back for more, huh? Go back home, bud.");
				}
				Task.addTask(Task.DIALOGUE, npc, "You're just another weak trainer in my way.");
				Task.addTask(Task.BATTLE, "", 34);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Tch, I lost? How did that happen?");
				Task.addTask(Task.DIALOGUE, npc, "Wait... you know Scott? Ugh, should've figured. Maybe I should've taken you more seriously.");
				Task.addTask(Task.DIALOGUE, npc, "But I just got unlucky this time. Next time, you won't be so fortunate.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(13.0, (npc) -> { // photon
			Task.addTask(Task.DIALOGUE, npc, "Wait, you're his kid? Oh my goodness, your father has told me all about you. Did you come here trying to challenge the gym?");
			Task.addTask(Task.DIALOGUE, npc, "...");
			Task.addTask(Task.DIALOGUE, npc, "You did? Well, that's going to be a difficult task considering the city's gone into full black-out, no power or anything.");
			Task.addTask(Task.DIALOGUE, npc, "I'd try and fix it, but the Control Center door is jammed, and that's our best shot at figuring out what's wrong.");
			Task.addTask(Task.DIALOGUE, npc, "Most of the buildings in this city have electric doors, and they all seem to not be working.");
			Task.addTask(Task.DIALOGUE, npc, "Actually, you know what we could try?");
			Task.addTask(Task.DIALOGUE, npc, "We can grab enough auxillery energy to keep the Control Center door open, and then give it a go from there.");
			Task.addTask(Task.DIALOGUE, npc, "Could you meet me there? It's located at the end of Route 45.");
			Task.addTask(Task.DIALOGUE, npc, "It's up straight North of this city, and then just a bit East.");
			Task.addTask(Task.DIALOGUE, npc, "I'll go on up ahead to set everything up, please don't keep me waiting!");
			Task.addTask(Task.DIALOGUE, npc, "I'm sure your father would be proud of you for helping out.");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			p.flag[1][1] = true;
		});
		
		scriptMap.put(13.1, (npc) -> { // stanford outside gym
			Task.addTask(Task.DIALOGUE, npc, "As much as I appreciate the help, don't think I'll go easy on you. Normal types really shouldn't be underestimated.");
			Task.addTask(Task.DIALOGUE, npc, "Good luck to you both, see you guys inside.");
			p.flag[1][16] = true;
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(13.2, (npc) -> { // ryder 2
			Task.addTask(Task.DIALOGUE, npc, "Wow, you beat Stanford? And you restored power to the city? You're impressive!");
			Task.addTask(Task.DIALOGUE, npc, "Say, you're helping your dad do research, right? I'm sure you've come across a couple Xhenovian forms then.");
			Task.addTask(Task.DIALOGUE, npc, "You seem like you love Pokemon so this might be obvious to you, but those Pokemon are variants of Pokemon from other regions.");
			Task.addTask(Task.DIALOGUE, npc, "My mom's friend is actually a researcher studying the forms here, and I'm sure you guys could help each other out!");
			Task.addTask(Task.DIALOGUE, npc, "She's camped out right near here, come with me, let me introduce you to her!");
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = 13;
			t.start = 88;
			t.finish = 19;
			t.wipe = false;
			p.flag[2][1] = true;
			t = Task.addTask(Task.FLAG, "");
			t.start = 2;
			t.finish = 3;
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Hello there Ryder! Who's this that you brought with?");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "... Well, nice to meet you young man! What do I owe the pleasure of this visit to?");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "...");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Yes, I do research Xhenovian forms! If you can bring me a regional form, I can trade you for its counterpart!");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "First though, I'll have to upgrade your Pokedex to add a 'Variant' Pokedex for you to keep track of these forms.");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "... And there you go! All upgraded. Come talk to me when you have a Xhenovian Pokemon to trade!");
			Task.addTask(Task.DIALOGUE, gp.npc[13][3], "Ryder, it was great to see you as always. Take care boys!");
			t = Task.addTask(Task.TURN, player, "", Task.RIGHT);
			t = Task.addTask(Task.TURN, gp.npc[13][7], "", Task.LEFT);
			t.start = 13;
			t.finish = 7;
			Task.addTask(Task.DIALOGUE, npc, "Thanks for your patience, I just figured that this little connection would help out you both mutually.");
			Task.addTask(Task.DIALOGUE, npc, "I'm gonna head out, I'll see you soon!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(13.3, (npc) -> { // regional trade
			if (!p.flag[2][1]) {
				Task.addTask(Task.DIALOGUE, npc, "I think you might have went the wrong way...");
				Task.addTask(Task.DIALOGUE, npc, "Are you looking for Mt. Splinkty? You have to go back to Route 26 and head North!");
			} else {
				if (!p.flag[2][15]) {
					Pokemon[] vDex = p.getDexType(3);
					int amt = 0;
					for (Pokemon po : vDex) {
						if (p.pokedex[po.id] == 2) amt++;
					}
					if (amt >= Pokemon.POKEDEX_2_SIZE) {
						Task.addTask(Task.DIALOGUE, npc, "You did it! You got me all of the Xhenovian forms!");
						Task.addTask(Task.DIALOGUE, npc, "Thank you so much for helping my research!");
						Task.addTask(Task.DIALOGUE, npc, "Here, this is for you, use it wisely!");
						Task t = Task.addTask(Task.ITEM, "");
						t.item = Item.MASTER_BALL;
						p.flag[2][15] = true;
						return;
					}
				}
				Task.addTask(Task.DIALOGUE, npc, "Got any Xhenovian forms to trade me?");
				Task.addTask(Task.REGIONAL_TRADE, "");
			}
		});
		
		scriptMap.put(162.0, (npc) -> { // photon
			if (!p.flag[1][2]) {
				Task.addTask(Task.DIALOGUE, npc, "You're just in time, I almost have the energy prepared.");
				Task.addTask(Task.DIALOGUE, npc, "In the meantime, can we chat for a bit about what I do here?");
				Task.addTask(Task.DIALOGUE, npc, "I have this research post out here in the sticks because of the close proximity to Electric Tunnel, the birthplace of another type of Pokemon.");
				Task.addTask(Task.DIALOGUE, npc, "They're the Electric forms, and I've seen a lot of them migrating to Sicab City to feast on the power.");
				Task.addTask(Task.DIALOGUE, npc, "I suspect the power issues have been caused by them finishing off all of the energy there, causing the outage.");
				Task.addTask(Task.DIALOGUE, npc, "When we head back to try and fix it, I figure we'll probably run into some Electric forms there.");
				Task.addTask(Task.DIALOGUE, npc, "Thankfully, your dad told me to work on an extension to record all of the new forms, and I'd like to give it to you to help me document them.");
				Task.addTask(Task.DIALOGUE, npc, "So, without further ado, here's an update to your Neodex!");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "There, I added an 'Electric Pokedex' section. That way you can be prepared to take note of any new forms you see there!");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Okay! I believe I have enough auxillary power here to open the door of the Control Center for a bit.");
				Task.addTask(Task.DIALOGUE, npc, "However, I don't think you have enough time to get back there on foot. But, who says you need to walk?");
				Task.addTask(Task.DIALOGUE, npc, "Here's a high-tech invention I've made so you can warp back! Let me upgrade your map real quick to integerate the tech.");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Now you can teleport instantly to any town you've already been! Use it to get back to the city in time, and the doors to the center should be open.");
				Task.addTask(Task.DIALOGUE, npc, "At least if my calculations are correct.");
				if (p.nuzlocke) {
					Task.addTask(Task.DIALOGUE, npc, "You're NUZLOCKING?!?! You must be insane.");
					Task.addTask(Task.DIALOGUE, npc, "But cool! Your Pocket PC and Healing Pack should work starting now too!");
				}
				Task.addTask(Task.DIALOGUE, npc, "God speed kid, and tell your dad I said hi!");
				p.flag[1][2] = true;
			} else if (!p.flag[1][19]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh yes, one more thing! As thanks for helping me out here, I have a gift for you.");
				Task.addTask(Task.DIALOGUE, npc, "I have here a Pokemon that has a confirmed Electric form, but there appears to be no way to switch between forms.");
				Task.addTask(Task.DIALOGUE, npc, "It's not much use to me anymore, so I figured it should have the pleasure of going out and exploring with you!");
				p.flag[1][19] = true;
				Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = 0;
				int counter = 0;
				do {
					counter++;
					id = gift.nextInt(6); // Rocky, Magikarp, Droid, Poof, Elgyem, Flamehox
					switch (id) {
					case 0:
						id = 48;
						break;
					case 1:
						id = 137;
						break;
					case 2:
						id = 181;
						break;
					case 3:
						id = 156;
						break;
					case 4:
						id = 265;
						break;
					case 5:
						id = 98;
						break;
					}
				} while (p.isDupes(id) && counter < 100);
				Pokemon result = new Pokemon(id, 20, true, false);
				if (result.getAbility(2) != Ability.NULL) {
					result.abilitySlot = 2;
					result.setAbility();
				}
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			} else if (p.flag[1][2] && !p.flag[1][16]) {
				Task.addTask(Task.DIALOGUE, npc, "The energy levels are getting low at the Control Center?");
				Task.addTask(Task.DIALOGUE, npc, "Hold on, I can get them up for a little longer.");
				Task.addTask(Task.DIALOGUE, npc, "...");
				Task.addTask(Task.DIALOGUE, npc, "Alright, keep at it champ.");
			} else if (!p.flag[1][22]) {
				Task.addTask(Task.DIALOGUE, npc, "Have you seen any new Electric forms? Can I take a look?");
				Pokemon[] eDex = p.getDexType(2);
				int amt = 0;
				for (Pokemon po : eDex) {
					if (p.pokedex[po.id] == 2) amt++;
				}
				if (!p.flag[1][21] && amt >= 3) {
					String plural = amt > 1 ? "s!" : "!";
					Task.addTask(Task.DIALOGUE, npc, "Wow! You've already caught " + amt + " form" + plural);
					Task.addTask(Task.DIALOGUE, npc, "Here, I have a special Electric-type move as a gift for helping me out!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.TM32;
					p.flag[1][21] = true;
				}
				if (amt >= Pokemon.POKEDEX_METEOR_SIZE) {
					Task.addTask(Task.DIALOGUE, npc, "...Oh my god! You did it! You completed the Electric form Pokedex!");
					Task.addTask(Task.DIALOGUE, npc, "I have here an extremely rare item, use it wisely!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.MASTER_BALL;
					p.flag[1][22] = true;
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "How's life, <@>? You see your father recently?");
			}
		});
		
		scriptMap.put(32.0, (npc) -> { // fisherman
			p.flag[1][17] = true;
			Task.addTask(Task.DIALOGUE, npc, "Say, you look like you'd be great at fishing. Here, take this spare I got lying around. Maybe you'll fish up a Durfish!");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.FISHING_ROD;
			Task.addTask(Task.DIALOGUE, npc, "Look at water and use the Fishing Rod in your bag to fish!");
		});
		
		scriptMap.put(16.0, (npc) -> { // stanford in power plant
			if (!p.flag[1][7]) {
				Task.addTask(Task.DIALOGUE, npc, "Name's Stanford, the leader of this here town, though I haven't done much work here. At least not recently.");
				Task.addTask(Task.DIALOGUE, npc, "Honestly, this town is pretty neglected and has gone to shit recently. You're like the first person I've met that's given a fuck.");
				Task.addTask(Task.DIALOGUE, npc, "This stupid electric ghost is running through my team, and I'm running out of options.");
				Task.addTask(Task.DIALOGUE, npc, "I'm a Normal-type gym leader for fuck's sake! It doesn't get affected by most of my attacks!");
			} else if (p.flag[1][7] && !p.flag[1][8]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, the ghost is gone. Good job kiddo.");
				Task.addTask(Task.DIALOGUE, npc, "The fuse box might be usable now, could you try turning it on?");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Thanks for the hand squirt, though don't doubt Normal types because of this, they can still be very strong.");
				Task.addTask(Task.DIALOGUE, npc, "Just not against supernatural shit, fuck whatever that was. Those ones seemed extra powerful too.");
				Task.addTask(Task.DIALOGUE, npc, "Did you notice that they all had their hidden abilities? Those are special abilities only accessible through a special item.");
				Task.addTask(Task.DIALOGUE, npc, "Tell you what, as thanks for helping, I'd like to give you this. Try it out to get a special ability of your own!");
				p.flag[1][9] = true;
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.ABILITY_PATCH;
				Task.addTask(Task.DIALOGUE, npc, "That fuse box should've opened a gate in the Control Center, you should check out the situation there.");
				if (!p.flag[1][12]) {
					Task.addTask(Task.DIALOGUE, npc, "The energy field still seems to not be fully fixed yet though, and I noticed some commotion at the office.");
				}
				Task.addTask(Task.DIALOGUE, npc, "But, you know what, I'm heading to the bar or something. See ya kid.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(18.0, (npc) -> { // scott office
			if (!p.flag[1][11]) {
				Task.addTask(Task.DIALOGUE, npc, "Not... strong... enough...");
			} else if (p.flag[1][11] && !p.flag[1][12]) {
				Task.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
				Task.addTask(Task.DIALOGUE, npc, "Anyways, I think now that you deal with... whatever Electric Pokemon that was... that the fuse box is working. Try turning it on real quick, my head is buzzing...");
			} else if (p.flag[1][12] && !p.flag[1][13]) {
				p.flag[1][13] = true;
				Task.addTask(Task.DIALOGUE, npc, "None of my usual tricks were getting to it, and I was on the ropes... So thanks for the hand...");
				Task.addTask(Task.DIALOGUE, npc, "Oh, by the way, I found something here that might be useful. Consider it as a token of appreciation...");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM02;
				Task.addTask(Task.DIALOGUE, npc, "It's a magic trick that makes rocks disappear! Anyways, I should get back to the gym to see if the gym leader finally came back. Presto!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(18.1, (npc) -> { // gift magic
			p.flag[2][2] = true;
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int id = 0;
			int counter = 0;
			do {
				counter++;
				id = gift.nextInt(8); // Cervora, Sparkdust, Posho, Kissyfishy, Minishoo, Tinkie, Bronzor-X, Willolagos
				switch (id) {
				case 0:
					id = 61;
					break;
				case 1:
					id = 106;
					break;
				case 2:
					id = 143;
					break;
				case 3:
					id = 150;
					break;
				case 4:
					id = 177;
					break;
				case 5:
					id = 184;
					break;
				case 6:
					id = 276;
					break;
				case 7:
					id = 47;
					break;
				}
			} while (p.isDupes(id) && counter < 100);
			Pokemon result = new Pokemon(id, 15, true, false);
			if (result.getAbility(2) != Ability.NULL) {
				result.abilitySlot = 2;
				result.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});
		
		scriptMap.put(18.2, (npc) -> { // endure move tutor
			if (!p.flag[1][23]) {
				Task.addTask(Task.TEXT, "Phew... I thought I was fried circuits for sure.");
				Task.addTask(Task.TEXT, "Those Team Eclipse thugs and the wild Electric forms really pushed us to the brink.");
				Task.addTask(Task.TEXT, "Still, we held on, and here we are - standing. That's the secret, y'know?");
				Task.addTask(Task.TEXT, "Endure long enough, and you'll always find another turn.");
				Task.addTask(Task.TEXT, "I can teach your Pokemon that same survival instinct.");
				p.flag[1][23] = true;
			}
			Task.addTask(Task.TEXT, "Want me to show one of them how to Endure?");
			Task t = Task.addTask(Task.PARTY, "", Task.MOVE);
			t.setMove(Move.ENDURE);
		});
		
		scriptMap.put(18.3, (npc) -> {
			if (!p.flag[1][24]) {
				p.flag[1][24] = true;
				Task.addTask(Task.TEXT, "This office has seen its fair share of... messes. Power surges, Eclipse grunts, broken coffee machines.");
				Task.addTask(Task.TEXT, "But I specialize in cleaing up a different kind of mess - bad moves.");
				Task.addTask(Task.TEXT, "If your Pokemon is stuck with a move you don't want, I can help it forget. Clean slate, fresh start.");
			}
			Task.addTask(Task.TEXT, "So... want me to tidy up a moveset for you?");
			Task.addTask(Task.PARTY, "", Task.DELETE_MOVE);
		});
		
		scriptMap.put(28.0, (npc) -> { // millie 1 and 2
			Task.addTask(Task.DIALOGUE, npc, "I'm Millie, kind of a big deal here. I've starred in several movies and TV stuff!");
			Task.addTask(Task.DIALOGUE, npc, "You've probably heard of Magikarp Jump: The Motion Picture, and Mystery Doors of the Magical Land: The Animated Series.");
			Task.addTask(Task.DIALOGUE, npc, "Anyways, yeah this isn't a shoot or scene, the trainers here are possessed or something! They just attack anything that try to enter the town.");
			Task.addTask(Task.DIALOGUE, npc, "You're going to have to fend them off back to back, and from what I've seen most of the town is infected! I'm not one to be in horror movies...");
			Task.addTask(Task.DIALOGUE, npc, "There won't be any breaks in between once we go in, so you're gonna need some strong Pokemon.");
			Task.addTask(Task.DIALOGUE, npc, "I saw those evil guys by the cell tower. They messed with the PC's signal somehow, and now you can only use Pokemon in the Gauntlet Box!");
			Task.addTask(Task.DIALOGUE, npc, "You might wanna put some Pokemon in there, preferably 4. If you don't have a full Gauntlet Box as well as a full team, you'll have a huge disadvantage!");
			int selected = p.getAmountSelected();
			String message = "Are you ready to fight once you come with me? We need to defend our town and there's no going back!";
			if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
				message = "You don't have 4 Pokemon selected to bring in the Gauntlet Box! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
			}
			for (String s : message.split("\n")) {
				Task.addTask(Task.DIALOGUE, npc, s);
			}
			Task.addTask(Task.DIALOGUE, npc, "Trust me, I had to fight off some of them, they hit hard. Especially the stunt doubles...");
			gp.ui.commandNum = 1;
			Task.addTask(Task.CONFIRM, npc, "There won't be any leaving until it's clear! Are you SURE you're ready?", 2);
			// Millie 2
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.DIALOGUE, npc, "There's this weird kid that's been in a trance blocking the way to the tower. He hasn't moved an inch, almost like he's guarding the place.");
			Task.addTask(Task.DIALOGUE, npc, "I've heard him mutter a few things. \"Toxic\", \"Get decked\", it's off-putting.");
			Task t = Task.addTask(Task.TURN, gp.npc[28][1], "", Task.RIGHT);
			t.start = 28;
			t.finish = 1;
			Task.addTask(Task.DIALOGUE, npc, "Wait, that's your rival? Maybe he'll recognize you, try saying something that'll, I don't know, make him angry!");
			Task.addTask(Task.DIALOGUE, npc, "Y'know, like in method acting! Give it a shot.");
			Task.addTask(Task.TURN, player, "", Task.UP);
			t = Task.addTask(Task.TURN, gp.npc[28][1], "", Task.UP);
			t.start = 28;
			t.finish = 1;
			Task.addTask(Task.DIALOGUE, npc, "...");
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.DIALOGUE, npc, "AHHH! HE'S DEFINITELY AWAKE NOW! JEEZ, WHAT DID YOU EVEN SAY!?");
			Task.addTask(Task.TURN, player, "", Task.UP);
			t = Task.addTask(Task.FLAG, "");
			t.start = 2;
			t.finish = 5;
		});
		
		scriptMap.put(28.1, (npc) -> { // millie 3
			if (npc.direction.equals("down")) { // player wiped: talked to her from the bottom
				Task.addTask(Task.DIALOGUE, npc, "Oh, there you are. What happened?");
				Task.addTask(Task.DIALOGUE, npc, "They must have beaten you up... You're definitely a tough kid, but I need you back in there. Maybe be extra careful this time.");
				Task t = Task.addTask(Task.TELEPORT, "");
				t.counter = 28;
				t.start = 82;
				t.finish = 36;
				t.wipe = false;
			} else {
				if (!p.flag[2][7]) {
					Task.addTask(Task.DIALOGUE, npc, "It's definitely too dangerous to go back to the town, at least for now. You need to scout the area to find the source of these strange radio waves...");
					Task.addTask(Task.DIALOGUE, npc, "Let me know if you figure out what's going on! Please... I'm scared...");
				} else {
					Task.addTask(Task.DIALOGUE, npc, "Thank Arceus you're back... I was so petrified in fear.");
					Task.addTask(Task.DIALOGUE, npc, "What did you find out? ... Oh, ... Oh my. What?!?!?");
					Task.addTask(Task.DIALOGUE, npc, "There's a poor Pokemon chained up to the tower?? That is so cruel! Ack! That makes me so sick.");
					Task.addTask(Task.DIALOGUE, npc, "It has to be a pretty powerful Pokemon to be able to transmit such strong radio waves to possess all of these people.");
					Task.addTask(Task.DIALOGUE, npc, "Did you happen to notice what Pokemon it was?");
					Task.addTask(Task.DIALOGUE, npc, "A... what? An alien spider? What??");
					Task.addTask(Task.DIALOGUE, npc, "I have no idea what you're talking about, I've never heard of anything like that in my life.");
					Task.addTask(Task.DIALOGUE, npc, "If it's a spider though, I'm sure we can be great friends. I love bugs!");
					Task.addTask(Task.DIALOGUE, npc, "Alien bugs might take a bit more to get used to. I wonder where it came from?");
					Task.addTask(Task.DIALOGUE, npc, "And I can't believe it's chained up! We have to set it free! I bet it was those space goons.");
					Task.addTask(Task.DIALOGUE, npc, "Come to think of it, I did notice an evil grunt run off towards Mt. Splinkty. Maybe we can stop him?");
					Task.addTask(Task.DIALOGUE, npc, "Go check out the Mountain and see if you can find the grunt, I'll stand guard here!");
					p.flag[2][8] = true;
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
				}
			}
		});

		scriptMap.put(28.2, (npc) -> { // millie 4
			Task.addTask(Task.DIALOGUE, npc, "OHHH! YOU! I'm so sorry, that was a reflex, there's so many of them.");
			if (!p.flag[2][9]) {
				Task.addTask(Task.DIALOGUE, npc, "Any luck finding the grunt? ..I've been holding them off okay.");
				Task.addTask(Task.DIALOGUE, npc, "Like I said, before, I believe that there's a secret room that you can access from the 2nd floor of the mountain, check that out if you haven't yet.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "You found him? How'd it go? Are you and your Pokemon okay?");
				Task.addTask(Task.DIALOGUE, npc, "You got wire cutters? Quick, let's try and cut that creature free and stop this madness!");
				Task.addTask(Task.DIALOGUE, npc, "I'll try and make sure the possessed people are okay. Good luck!");
				p.flag[2][10] = true;
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(28.3, (npc) -> { // xurkitree
			if (!p.flag[2][11]) {
				Task.addTask(Task.TEXT, "(The mysterious Pokemon seems to be alive, though it's unable to move because it's chained up.)");
				if (!p.flag[2][9]) {
					p.flag[2][7] = true;
				} else {
					p.bag.remove(Item.WIRE_CUTTERS);
					Task.addTask(Task.TEXT, "You used the wire cutters to set it free!");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
					Task.addTask(Task.DIALOGUE, npc, "Bzzz....Zzzzttt..... ZUZUZUURRKIII!!!");
					Task.addTask(Task.TEXT, "(The mysterious creature seems shell-shocked, and is now lashing out at everything around it!)");
					Task t = Task.addTask(Task.FLAG, "");
					t.start = 2;
					t.finish = 11;
					t = Task.addTask(Task.BATTLE, "", 387);
					t.start = 284;
				}	
			}
		});
		
		scriptMap.put(28.4, (npc) -> { // millie 5
			p.flag[2][13] = true;
			Task.addTask(Task.DIALOGUE, npc, "Even though bug Pokemon aren't as flimsy and weak as they seem, I still have a feeling that you'll be able to defeat me.");
			Task.addTask(Task.DIALOGUE, npc, "I mean, you were literally able to fend off a huge alien bug and save me and my town!");
			Task.addTask(Task.DIALOGUE, npc, "Have you noticed the big openings in the Earth around this area? There's lots more around this region.");
			Task.addTask(Task.DIALOGUE, npc, "There's a pretty interesting myth that my mom would tell me stories of when I was younger.");
			Task.addTask(Task.DIALOGUE, npc, "She said that the terrain wasn't always so rough, that it's actually the result of an ancient war that happened.");
			Task.addTask(Task.DIALOGUE, npc, "According to the myth, there used to be a cult-like following of an all-powerful diety that apparently created the Pokemon and people living here!");
			Task.addTask(Task.DIALOGUE, npc, "But once people discovered the existance of alien Pokemon and the Galactic type, their religion was proven obsolete by the athiests.");
			Task.addTask(Task.DIALOGUE, npc, "Or at least the athiests tried to disprove it that way. As you can imagine, it didn't go over too well with the believers, erupting in the massive conflict.");
			Task.addTask(Task.DIALOGUE, npc, "Apparently the destruction of the terrain was actually caused by the leaders of each group fighting, who are rumored to both be powerful Pokemon.");
			Task.addTask(Task.DIALOGUE, npc, "But, as one of my favorite creators would say, that's \"just a theory\", after all.");
			Task.addTask(Task.DIALOGUE, npc, "I do like thinking about it though, it's pretty interesting, wouldn't you agree?");
			Task.addTask(Task.DIALOGUE, npc, "..What's that? Oh right! Silly me, I was going to give you a technique to help traverse the gaps!");
			Task.addTask(Task.DIALOGUE, npc, "I just got side-tracked thinking about all of the cool films that could be made about that myth. It's a pretty popular one around here!");
			Task.addTask(Task.DIALOGUE, npc, "Anyways, I'm only giving you this technique because even though you can't use it until you beat me, I know you have the skills to do it.");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.HM03;
			Task.addTask(Task.DIALOGUE, npc, "Come fight me inside when you're ready, I know it'll be an amazing battle. Maybe even one we'll turn into a film when you're a world-famous trainer someday!");
			Task.addTask(Task.DIALOGUE, npc, "Good luck, my friend. You're going to need it! EEK I'm so excited!!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(49.0, (npc) -> { // strong deep pokemon
			p.flag[2][14] = true;
			Task.addTask(Task.DIALOGUE, npc, "I encountered this very strong Pokemon, and I don't think I'm strong enough to train it. Here!");
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int id;
			int counter = 0;
			do {
				counter++;
				id = gift.nextInt(5); // Pebblepup, Fightorex, Tricerpup, Shockfang, Nightrex
				switch (id) {
				case 0:
					id = 55;
					break;
				case 1:
					id = 57;
					break;
				case 2:
					id = 66;
					break;
				case 3:
					id = 211;
					break;
				case 4:
					id = 213;
					break;
				}
			} while (p.isDupes(id) && counter < 50);
			Pokemon result = new Pokemon(id, 25, true, false);
			if (result.getAbility(2) != Ability.NULL) {
				result.abilitySlot = 2;
				result.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});
		
		scriptMap.put(50.0, (npc) -> { // gift "starter"
			p.flag[3][9] = true;
			Task.addTask(Task.DIALOGUE, npc, "Great choice young cracka!!!!");
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int id = gift.nextInt(3); // Otterpor, Florline, Flameruff
			switch (id) {
			case 0:
				id = 78;
				break;
			case 1:
				id = 80;
				break;
			case 2:
				id = 92;
				break;
			}
			if (p.isDupes(id)) {
				Task.addTask(Task.DIALOGUE, npc, "Wait..... you have that one?!?!? Shit. Well, take this one instead bozo.");
				boolean sparkitten = gift.nextBoolean();
				if (sparkitten) {
					id = 108;
				} else {
					id = 190;
				}
			}
			Pokemon result = new Pokemon(id, 30, true, false);
			if (gift.nextDouble() < 0.4 && result.getAbility(2) != Ability.NULL) {
				result.abilitySlot = 2;
				result.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});

		scriptMap.put(39.0, (npc) -> { // ryder 3
			p.flag[3][0] = true;
			Task.addTask(Task.DIALOGUE, npc, "Well, I say it's weather, more like the atmospheric nonsense going outside.");
			Task.addTask(Task.DIALOGUE, npc, "Jeez it's so bright, worse than the time I stared directly at the sun.");
			Task.addTask(Task.DIALOGUE, npc, "But hey Alakazam, you live and you learn. Thankfully I brought these awesome Pit Sevipers!");
			Task.addTask(Task.DIALOGUE, npc, "These suckers block out all kinds of rays, heat rays, UV rays, even Confuse Rays!");
			Task.addTask(Task.DIALOGUE, npc, "You look quite strained, I'll let you borrow these since I wasn't planning on sticking around long anyways.");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.VISOR;
			Task.addTask(Task.DIALOGUE, npc, "If you put those on you should be able to see a lot better outside, it's crazy out there.");
			Task.addTask(Task.DIALOGUE, npc, "To be honest with you dude, I'm not even sure why it's so darn bright... and the local school is in a full blown panic about it.");
			Task.addTask(Task.DIALOGUE, npc, "The classrooms are all locked and there's no sign of the teachers... come to think of it, that should probably be looked at.");
			Task.addTask(Task.DIALOGUE, npc, "Just don't think I'll be doing too much of the \"looking\", because you have my one and only pair of shades.");
			Task.addTask(Task.DIALOGUE, npc, "Oh, one more thing before I leave you to save the town alone! There's a lot of Ice-type Pokemon in this area...");
			Task.addTask(Task.DIALOGUE, npc, "So I'm entrusting you with one of my favorite Pokemon!");
			Task.addTask(Task.DIALOGUE, npc, "I brought a couple Flamigo as travel buddies from my home region, and I'm entrusting one to help you out with the Ice-types!");
			Task.addTask(Task.TEXT, "You received Flamigo!");
			Pokemon p = new Pokemon(246, 35, true, false);
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			if (gift.nextDouble() < 0.4) {
				p.abilitySlot = 2;
				p.setAbility();
			}
			Task.addTask(Task.GIFT, "", p);
			Task.addTask(Task.DIALOGUE, npc, "...Just probably don't let it get hit by an Ice move. Anyways, toodles, and good luck kid!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(38.0, (npc) -> { // ice master
			Task.addTask(Task.DIALOGUE, npc, "It may help you in your journey, maybe even in ways you don't expect. Consider it a token of my thanks for your help with the school.");
			p.flag[3][10] = true;
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.PETTICOAT_GEM;
		});
		
		scriptMap.put(38.1, (npc) -> { // robin cutscene
			Task.addTask(Task.DIALOGUE, npc, "*pant* I even had to jam your portal system just so I could track you down.");
			Task.addTask(Task.DIALOGUE, npc, "I've got an urgent letter from your mom. Don't know what's in it, but she said it was really important.");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.LETTER;
			Task.addTask(Task.DIALOGUE, npc, "She was pretty insistent I get this to you right away. You might want to open it as soon as you can.");
			Task.addTask(Task.DIALOGUE, npc, "Anyway, I'm just glad I finally found you. Take care of yourself, alright?");
			p.flag[4][0] = true;
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(43.0, (npc) -> {
			Task.addTask(Task.DIALOGUE, npc, "It's rare, even among explorers. Use it wisely - it could be the edge you need.");
			p.flag[3][11] = true;
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.VALIANT_GEM;
			Task.addTask(Task.DIALOGUE, npc, "That gem was unearthed during a deep excavation, and I was saving it for someone special. Looks like that's you.");
		});
		
		scriptMap.put(165.0, (npc) -> { // principal
			Task.addTask(Task.DIALOGUE, npc, "I don't have any Pokemon, so I wasn't sure what to do! But now that I see you're here to help... maybe there's hope.");
			if (!p.flag[3][1] && !p.flag[3][2]) { // haven't found either ground or ice master
				Task.addTask(Task.DIALOGUE, npc, "Listen, this whole situation is a mess! Those Grunts have taken over the classrooms, and I can't do anything to stop them.");
				Task.addTask(Task.DIALOGUE, npc, "The teachers - Ice Master and Ground Master - might be able to help, but I haven't seen them in a while.");
				Task.addTask(Task.DIALOGUE, npc, "I think Ice Master is somewhere in the fields north of town, and Ground Master... I believe he's hiding out in the city.");
				Task.addTask(Task.DIALOGUE, npc, "Find them, please!");
			} else if ((p.flag[3][1] || p.flag[3][2]) && !p.flag[3][3] && !p.flag[3][4]) { // got at least one of the keys but hasn't unlocked the classrooms yet
				String teacher = "";
				if (p.flag[3][1] && !p.flag[3][2]) teacher = "Ice Master's";
				if (p.flag[3][2] && !p.flag[3][1]) teacher = "Ground Master's";
				if (p.flag[3][1] && p.flag[3][2]) teacher = "both of their";
				Task.addTask(Task.DIALOGUE, npc, "You've got " + teacher + " keys? That's amazing!");
				Task.addTask(Task.DIALOGUE, npc, "Now you can get into the classrooms and see what those Team Eclipse Grunts are up to.");
				Task.addTask(Task.DIALOGUE, npc, "I wish I could do more, but... I don't have any Pokemon, and I - I just can't face them.");
				Task.addTask(Task.DIALOGUE, npc, "Please, go and clear them out! I'll do whatever I can to help once they're gone.");
			} else if ((p.flag[3][3] || p.flag[3][4]) && (!p.flag[3][5] || !p.flag[3][6])) {
				Task.addTask(Task.DIALOGUE, npc, "You've unlocked a classroom, right? Go check inside! There are Grunts taking over the school, and it's a disaster.");
				Task.addTask(Task.DIALOGUE, npc, "I know I should be the one handling this, but without Pokemon, I'm helpless! Please, clear out those rooms!");
			} else if (p.flag[3][5] && p.flag[3][6]) {
				Task.addTask(Task.DIALOGUE, npc, "You did it! You cleared both classrooms! I - I don't know how to thank you enough.");
				Task.addTask(Task.DIALOGUE, npc, "I may not be able to help in battle, but I can give you something that will aid you on your journey.");
				Task.addTask(Task.DIALOGUE, npc, "Here - take this, it's the HM for Surf. I know you'll make great use of it.");
				p.flag[3][7] = true;
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM04;
				if (!p.flag[3][8]) {
					Task.addTask(Task.DIALOGUE, npc, "Now, go find the cause of the extreme light outside with your new tools and put a stop to it!");
				}
			}
		});

		scriptMap.put(44.0, (npc) -> { // gym leader block
			Task.addTask(Task.DIALOGUE, npc, "Leader Glacius said no battles until we can get things under control in the city.");
			if (!p.flag[3][5] || !p.flag[3][6]) {
				Task.addTask(Task.DIALOGUE, npc, "You're wondering why the gym's closed, right? Well, it's not just the gym - there's trouble over at the school too.");
				Task.addTask(Task.DIALOGUE, npc, "The gym's been shut down ever since the school got overrun by those Team Eclipse guys.");
				Task.addTask(Task.DIALOGUE, npc, "If you're looking to challenge Glacius, you'll need to sort out what's happening at the school first.");
			} else if (p.flag[3][5] && p.flag[3][6] && !p.flag[3][7]) {
				Task.addTask(Task.DIALOGUE, npc, "I heard you cleared the whole school out of those goons! The principal mentioned he wants to thank you for everything you've done.");
				Task.addTask(Task.DIALOGUE, npc, "Might be a good idea to visit him before you come back here. He should be in his office in the school.");
			} else if (p.flag[3][7] && !p.flag[3][8]) {
				Task.addTask(Task.DIALOGUE, npc, "You did great clearing the school of those Grunts, but that bright light's still making things difficult.");
				Task.addTask(Task.DIALOGUE, npc, "Leader Glacius won't accept challengers until the light's sorted out. Maybe you can do something about it?");
			}
		});
		
		scriptMap.put(88.0, (npc) -> { // grandpa frenco
			if (!p.flag[4][2]) {
				p.flag[4][2] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh... thank Arceus you're here. I was afraid I wouldn't make it through this.");
				Task.addTask(Task.DIALOGUE, npc, "Those thugs - Team Eclipse - stormed in, demanding something that I don't fully understand.");
				Task.addTask(Task.DIALOGUE, npc, "You beat them, right? Why are they still here? Can you do anything?");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.SPEAK, player, "Get out, you creeps! Leave us alone. I already destroyed your flimsy Pokemon, you want some more?");
				Task.addTask(Task.SPEAK, player, "Didn't think so. Then, GET!");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Thank you <@>, I don't know what I would've done without you.");
				Task.addTask(Task.DIALOGUE, npc, "They said something about summoning an Ultra Paradox Pokemon... I think it's some dangerous creature they're after.");
				Task.addTask(Task.DIALOGUE, npc, "I heard they were headed for Peaceful Park next, something about unleashing that alien there, probably.");
				Task.addTask(Task.DIALOGUE, npc, "You might want to stop them before they cause more chaos.");
				Task.addTask(Task.DIALOGUE, npc, "Oh! But before you go, I have a gift for you.");
				Task.addTask(Task.DIALOGUE, npc, "Take this, it's the HM for Slow Fall. It should help you out exploring Xhenos once you take down Mindy.");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.HM05;
				Task.addTask(Task.DIALOGUE, npc, "Be careful out there. These aren't just ordinary trainers - they're after something far more dangerous than I've ever seen.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Still here, eh? You'd better hurry to Peaceful Park before those Team Eclipse thugs cause even more trouble.");
				Task.addTask(Task.DIALOGUE, npc, "They were talking about summoning an Ultra Paradox Pokemon, some terrible creature. The park's just south of Kleine Village - head there quickly!");
				Task.addTask(Task.DIALOGUE, npc, "Be careful, that thing's dangerous. I know you can handle it, but don't take it lightly.");
			}
		});
		
		scriptMap.put(93.0, (npc) -> { // move reminder
			if (p.nuzlocke && !p.flag[4][7]) {
				Task.addTask(Task.TEXT, "Hello there! I'm the move reminder, I can teach any Pokemon a move they've forgotten!");
				Task.addTask(Task.TEXT, "I know that having to come to me every time isn't always convenient, so here, I made this item for use on-the-go!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.MOVE_ENCYCLOPEDIA;
				p.flag[4][7] = true;
			} else {
				Task.addTask(Task.TEXT, "Would you like to remember a move? Which Pokemon should remember?");
				Task.addTask(Task.PARTY, "Teach # a move?", Task.REMIND);
			}
		});
		
		scriptMap.put(94.0, (npc) -> { // gift e/s
			p.flag[4][4] = true;
			Task.addTask(Task.TEXT, "They struck like lightning and silence - two meteorites, one from brilliance, one from void.");
			Task.addTask(Task.TEXT, "The Heart of the Electric Tunnel crackles with restless current. Pokemon near it began to change...");
			Task.addTask(Task.TEXT, "And deep within the Shadow Ravine, something darker took root. Pokemon warpedin ways no scientist can explain.");
			Task.addTask(Task.TEXT, "They say the energy isn't from this world. Or myabe it is... just a part we were never meant to touch.");
			Task.addTask(Task.TEXT, "Here. One of the altered Pokemon found its way to me. It feels wrong to keep it hidden.");
			
			int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226, 267, 281};
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.isDupes(ids[index]) && counter < 100);
			
			Pokemon result = new Pokemon(ids[index], 30, true, false);
			if (result.getAbility(2) != Ability.NULL) {
				result.abilitySlot = 2;
				result.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});
		
		scriptMap.put(107.0, (npc) -> { // arthra ghostly woods
			if (!p.flag[5][0]) {
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.DIALOGUE, npc, "Oh, didn't expect company out here. You must be here for the same reason, yeah? You've noticed these ghosts swarming the woods?");
				Task.addTask(Task.DIALOGUE, npc, "Name's Arthra, by the way. Merlin's my grandfather; you'll meet him someday if you get that far. Right now, I'm trying to figure out what's causing this mess.");
				Task.addTask(Task.DIALOGUE, npc, "These ghosts are sneaky, but I've got them down. They're phasing in and out like something's messing with their energy.");
				Task.addTask(Task.DIALOGUE, npc, "And I'm willing to bet it's no accident.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', 91 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "But if you're serious about this, let's see if you can keep up. I don't have time to drag around dead weight.");
				Task.addTask(Task.BATTLE, "", 392);
			} else {
				p.flag[5][1] = true;
				Task.addTask(Task.DIALOGUE, npc, "But don't get too comfortable with that win. I'll make sure you regret it next time we battle.");
				Task.addTask(Task.DIALOGUE, npc, "You want to play hero here? Be my guest! If you're so great, then take care of those ghosts yourself. I've got better things to do.");
				Task.addTask(Task.DIALOGUE, npc, "Oh, and here you go, hero! You can probably make far better use of this than I ever could!");
				Task t = Task.addTask(Task.ITEM, "");
				t.item = Item.ABILITY_PATCH;
				Task.addTask(Task.DIALOGUE, npc, "I really hate your hero attitude. You don't know shit. But like I said, have at it here.");
				Task.addTask(Task.DIALOGUE, npc, "Just don't mess it up. And keep an eye out - these ghosts aren't the only threat around here.");
				Task.addTask(Task.DIALOGUE, npc, "There's something bigger going on, and I'll figure it out - no thanks to you. See you around, hero.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});

		scriptMap.put(107.1, (npc) -> { // rick ghostly woods
			Task.addNPCMoveTask('y', 55 * gp.tileSize, npc, false, 96);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addNPCMoveTask('y', 56 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addNPCMoveTask('x', 46 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addNPCMoveTask('x', 46 * gp.tileSize, player, false, 1);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addNPCMoveTask('y', 58 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Well, well. Look who decided to show up - the meddling pest who can't keep their nose out of Eclipse's business.");
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addNPCMoveTask('y', 64 * gp.tileSize, player, false, 1);
			Task.addNPCMoveTask('y', 61 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Do you have any idea what you've done? Those ghosts were just the beginning!");
			Task.addTask(Task.DIALOGUE, npc, "You may have beaten a few, but I can summon hundreds more if I want. Once I'm done with you, Ghostly Woods will be overrun!");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addNPCMoveTask('y', 63 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Once we're done here, not even the bravest of trainers will stand in Team Eclipse's way.");
			Task.addTask(Task.BATTLE, "", 234);
		});
		
		scriptMap.put(103.0, (npc) -> { // maxwell 1
			if (!p.flag[5][4]) {
				Task.addTask(Task.DIALOGUE, npc, "Thra'knash koru'dan Gzhaz... Zhar'mir vakta da'tor!");
				Task.addTask(Task.DIALOGUE, npc, "Vahl'orim Dragowrath! Zhar'kor-Gzazha... vass'dar athra!");
				Task.addTask(Task.DIALOGUE, npc, "Zharkh'nir da'kash! Gzazha, ir'thar vak'tai khar... rise'thil an'dor!");
				Task.addTask(Task.DIALOGUE, npc, "Vahl'krim da'sharak... rise! KHAR DA'ZHAR GZAZHA!");
				Task.addTask(Task.SHAKE, "BOOOOOOOOOOOOOOOOOOOOOM!", 300);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.DIALOGUE, npc, "BWAHAHAHAH! The pest finally arrives! Did you really think you could follow us all the way down here and stop what's already in motion? How amusingly naive.");
				Task.addTask(Task.DIALOGUE, npc, "You see, child, I am far more than just a 'trainer'. I am the vanguard of an empire - we are here to prepare this world for something beyond your comprehension.");
				Task.addTask(Task.DIALOGUE, npc, "My master, Dragowrath, watches from the stars. Earth is merely the first of many worlds he'll claim, and when the Sorcerer rises, this land will be ours.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', gp.tileSize * 36, npc, false, 4);
				Task.addTask(Task.DIALOGUE, npc, "But enough talk. You've come this far, so let's end this with a little... demonstration of power. Prepare yourself - my Pokemon and I will not hold back.");
				Task.addTask(Task.BATTLE, "", 217);
			} else if (!p.flag[5][9]) {
				p.flag[5][9] = true;
				Task.addTask(Task.DIALOGUE, npc, "Impressive... but don't think this victory means anything. The master plan is already in place, and there's nothing you can do to stop us.");
				Task.addTask(Task.DIALOGUE, npc, "Enjoy your hollow victory while it lasts. Soon, this world will be torn apart, and you'll see just how powerless you truly are.");
				Task.addTask(Task.DIALOGUE, npc, "Farewell... and enjoy what little time your precious world has left.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SHAKE, "", 100);
				
				Task.addCameraMoveTask('y', -500, 2);
				
				Task.addTask(Task.SHAKE, "", 50);
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addNPCMoveTask('y', 45 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task.addTask(Task.TURN, gp.npc[103][1], "", Task.RIGHT);
				
				Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task.addTask(Task.TURN, gp.npc[103][1], "", Task.UP);
				
				Task.addNPCMoveTask('y', 41 * gp.tileSize, gp.npc[103][1], true, 2);
				
				Task t = Task.addTask(Task.TURN, gp.npc[103][1], "", Task.DOWN);
				t.wipe = true;
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addDiagCameraMoveTask(0, 0, 45);
				
				Task.addTask(Task.SLEEP, "", 30);
				
				Task.addTask(Task.TEXT, "A powerful presence awaits...");
			}
		});
		
		scriptMap.put(109.0, (npc) -> { // breeder
			p.flag[5][7] = true;
			Task.addTask(Task.DIALOGUE, npc, "It was just sitting there in the dirt... no nest, no parents, nothing.");
			Task.addTask(Task.DIALOGUE, npc, "It's warm though. Still alive. Still waiting.");
			Task.addTask(Task.DIALOGUE, npc, "I'd take it with me, but I don't have the supplies - and my own team's too shaken to travel back.");
			Task.addTask(Task.DIALOGUE, npc, "Could you... raise it instead? Give it a home?");
			
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int[] ids = new int[] {179, 98, 238, 254, 257, 261, 292}; // zorua, flamehox, scraggy, solosis-x, seviper, gulpin-x, azurill
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.isDupes(ids[index]) && counter < 100);
			
			Egg result = new Egg(ids[index]);
			if (result.getAbility(2) != Ability.NULL) {
				result.abilitySlot = 2;
				result.setAbility();
			}
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});
		
		scriptMap.put(109.1, (npc) -> { // scott cutscene
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addNPCMoveTask('y', 40 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addNPCMoveTask('y', 42 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addNPCMoveTask('x', 11 * gp.tileSize, npc, false, 3);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addNPCMoveTask('y', 2052, gp.npc[109][5], false, 3);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addNPCMoveTask('x', 14 * gp.tileSize, npc, false, 4);
			
			Task.addTask(Task.DIALOGUE, npc, "You beat Rayna?! Me too! But that's not important right now. You need to listen to me, something HUGE is going on!");
			Task.addTask(Task.DIALOGUE, npc, "It's... it's Mt. St. Joseph! Something's about to happen - an eruption, but not just any eruption!");
			Task.addTask(Task.DIALOGUE, npc, "There's some kind of supernatural force messing with the volcano. It's all connected to Team Eclipse.");
			Task.addTask(Task.DIALOGUE, npc, "I don't know the exact details, but...");
			Task.addTask(Task.DIALOGUE, npc, "Well, I don't know how to tell you this, because I know you're friends with him too, but...");
			Task.addTask(Task.SPEAK, player, "What? What happened? Who are you talking about?");
			Task.addTask(Task.DIALOGUE, npc, "I just ran into Fred, and he told me Eclipse is trying to cause a catastrophe there - and he's part of it!");
			Task.addTask(Task.DIALOGUE, npc, "Can you believe that Farfetch'd nonsense? Fred... joined them!");
			Task.addTask(Task.SPEAK, player, "...");
			Task.addTask(Task.SPEAK, player, "Yeah... Scott, I already knew. I fought him in Shadow Path and bested him there.");
			Task.addTask(Task.DIALOGUE, npc, "WHAT?! Ugh, of course you did... you probably know EVERYTHING by now.");
			Task.addTask(Task.DIALOGUE, npc, "Anyway, I guess that's not the biggest news. The real problem is Mt. St. Joseph - that's where they're going to make their move.");
			Task.addTask(Task.DIALOGUE, npc, "If we don't stop them, we could be looking at a real disaster.");
			Task.addTask(Task.SPEAK, player, "Fred told you their plans? Why?");
			Task.addTask(Task.DIALOGUE, npc, "Well, I don't think he's happy with what they're doing, but he's too far gone to stop them. He said they're planning something big at the volcano.");
			Task.addTask(Task.DIALOGUE, npc, "And I - I think he knew how they're going to do it. I think they're summoning an Ultra Paradox Pokemon to cause an eruption.");
			Task.addTask(Task.DIALOGUE, npc, "It's going to be a strong enough natural disaster to destroy everything in Xhenos. I... I'm not sure how, but I...");
			Task.addTask(Task.DIALOGUE, npc, "...");
			Task.addTask(Task.SPEAK, player, "What, Scott? Spit it out!");
			Task.addTask(Task.DIALOGUE, npc, "I think I have a feeling why they want to wipe out the whole region.");
			Task.addTask(Task.DIALOGUE, npc, "...");
			Task.addTask(Task.DIALOGUE, npc, "I think they have an alien leader... and I think it wants to colonize this planet.");
			Task.addTask(Task.SPEAK, player, "WHAT?! They want to... take over all of us??");
			Task.addTask(Task.SPEAK, player, "Oh God, Scott! How do we stop them?!");
			Task.addTask(Task.DIALOGUE, npc, "I don't know, <@>! It's going to be very dangerous. You'll need to go South through Gelb Forest to Rawwar City.");
			Task.addTask(Task.DIALOGUE, npc, "But there's a big problem, you will need to cross an intense vortex of water with Whirlpool.");
			if (p.bag.contains(Item.HM06)) {
				Task.addTask(Task.SPEAK, player, "I got the Whirlpool HM, I should be fine. Thanks Scott.");
			} else {
				Task.addTask(Task.SPEAK, player, "How can I cross it?");
				Task.addTask(Task.DIALOGUE, npc, "You can cross it with the Hidden Machine Whirlpool.");
				Task.addTask(Task.DIALOGUE, npc, "Fred... Fred actually told me where it is - right next to Team Eclipse's underground base...");
				Task.addTask(Task.DIALOGUE, npc, "At the bottom of Shadow Ravine, by where that Ultra Paradox Pokemon you fought was.");
				Task.addTask(Task.SPEAK, player, "I must've missed it. Thanks for letting me know.");
			}
			Task.addTask(Task.DIALOGUE, npc, "Just... just be careful, alright? I didn't get all the details from Fred, but by the way he was talking...");
			Task.addTask(Task.DIALOGUE, npc, "Things are much worse than we realize. Please, <@>... I can't lose another friend to Eclipse.");
			Task.addTask(Task.SPEAK, player, "Don't worry, Scott. I'll stop them. You just stay safe.");
			Task.addTask(Task.DIALOGUE, npc, "Thanks, <@>... I know you can do it. Good luck.");
			Task.addTask(Task.DIALOGUE, npc, "Now, go and stop them before it's too late.");
			p.flag[6][0] = true;
		});
		
		scriptMap.put(118.0, (npc) -> { // fossil
			if (p.nuzlocke) {
				Task.addTask(Task.DIALOGUE, npc, "Normally, we'd use advanced machinery to reconstitute ancient Pokemon from fossilized cells...");
				Task.addTask(Task.DIALOGUE, npc, "But those methods are too unstable right now.");
				Task.addTask(Task.DIALOGUE, npc, "Instead, we've developed an incubation technique inspired by real-world surrogacy models!");
				Task.addTask(Task.DIALOGUE, npc, "Give me your fossil, and I'll extract the embryo... the rest is up to you.");
				Task.addTask(Task.FOSSIL, npc, "Are you ready to hatch history in your own hands?");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "We extract genetic code from fossilized scales, then reconstruct the organism using modern cloning tech.");
				Task.addTask(Task.DIALOGUE, npc, "It's science, sure - but there's a bit of magic to seeing ancient life stir again.");
				Task.addTask(Task.FOSSIL, npc, "Got a fossil? Let's revive something long thought lost!");
			}
		});

		scriptMap.put(127.0, (npc) -> { // blackjack
			Task.addTask(Task.CONFIRM, npc, "Would you like to play Blackjack?\n(Warning: Will Auto-Save)", 3);
		});
		
		scriptMap.put(127.1, (npc) -> { // battle bet
			if (p.coins > 0) {
				Task.addTask(Task.CONFIRM, npc, "Would you like to Battle Bet?\n(Warning: Will Auto-Save)", 4);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "I'm sorry, you don't have any coins to bet with! Come back later!");
			}
		});
		
		scriptMap.put(129.0, (npc) -> { // coin guy prize shop
			if (!p.flag[6][1]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you haven't gotten any coins yet?");
				Task.addTask(Task.DIALOGUE, npc, "Here, it's on the house!");
				Task.addTask(Task.DIALOGUE, npc, "You received 100 Coins!");
				Task.addTask(Task.DIALOGUE, npc, "Yes, now don't go spend them all in one place. Or do! I don't care!");
				Task.addTask(Task.DIALOGUE, npc, "But come back when you get more badges. Perhaps I'll have a reward for you!");
				p.coins += 100;
				p.flag[6][1] = true;
			} else {
				if (p.bag.contains(Item.TEMPLE_ORB)) {
					Task t = Task.addTask(Task.DIALOGUE, npc, "Ooh, you have some shiny Temple Orbs for me? That's awesome!");
					t.ui = Task.ITEM;
					t.item = Item.TEMPLE_ORB;
					Task.addTask(Task.DIALOGUE, npc, "Those are really rare, and I'm a collector. I'll give you 100 coins for each!");
					int coins = p.bag.getCount(Item.TEMPLE_ORB) * 100;
					p.coins += coins;
					p.bag.removeAll(Item.TEMPLE_ORB);
					Task.addTask(Task.TEXT, "Traded in your Temple Orbs and got " + coins + " coins!");
				} else {
					boolean newBadges = false;
					for (int i = 0; i < p.badges; i++) {
						if (!p.coinBadges[i]) {
							newBadges = true;
							break;
						}
					}
					if (newBadges) {
						Task.addTask(Task.DIALOGUE, npc, "Hey hey hey, what do we have here?");
						Task.addTask(Task.DIALOGUE, npc, "Got some new badges, eh? Let me take a look!");
						String[] messages = new String[] {
							"Bested Robin in Poppy Grove! Lemme see your case real quick.",
							"Beat Stanford, huh? I got a shiny reward for you, kid!",
							"Ah, a shiny new badge! Made quick work of Millie, I see! Guess that calls for a bonus, bub.",
							"4 badges, huh?  You're halfway through the gyms, and that's no easy feat. Here's a little something for your troubles.",
							"Bested Millie's own mom too? You took down that family no trouble! Pass me your case!",
							"What's that? You beat both Maxwell and Rayna? Wow, I really underestimated you! Here!",
							"Defeated our resident gambler Merlin? Heck yeah, I never trusted him anyways, heard he always cheated at Blackjack.",
							"Wow, all 8 badges! Can't believe you struck down the gyms! I got just the reward for a champion in the making like youse!"
						};
						for (int i = 0; i < p.badges; i++) {
							if (!p.coinBadges[i]) {
								Task.addTask(Task.DIALOGUE, npc, messages[i]);
								int coins = i > 4 ? 250 : i > 2 ? 200 : i > 0 ? 100 : 50;
								Task.addTask(Task.TEXT, "You received " + coins + " coins!");
								p.coinBadges[i] = true;
								p.coins += coins;
							}
						}
					} else {
						if (p.coins > 0) {
							gp.ui.sellAmt = 0;
							Task t = Task.addTask(Task.GAME_STATE, "");
							t.counter = GamePanel.COIN_STATE;
						} else {
							Task.addTask(Task.DIALOGUE, npc, "Looks like your stash just ran dry, so I can't exchange air for anything. Come back when you're... a little richer.");
						}
						
					}
				}	
			}
		});
		
		scriptMap.put(130.0, (npc) -> { // guy eddie in restaurant
			if (p.flag[5][8] && !p.flag[6][3]) {
				p.flag[6][3] = true;
				Task.addTask(Task.DIALOGUE, npc, "Oh hey squirt! You finally got strong enough to make it here on your own!");
				Task.addTask(Task.DIALOGUE, npc, "Here, take this rare Pokemon!");
				Pokemon result = new Pokemon(97, 50, true, false);
				Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
				Task.addTask(Task.GIFT, "", result);
			}
			Task.addTask(Task.DIALOGUE, npc, "Here, grab a seat! You and your Pokemon look like you want some SPICE!");
			for (Pokemon p : p.team) {
				if (p != null && !p.isFainted() && p.type1 != PType.FIRE && p.type2 != PType.FIRE) {
					p.status = Status.BURNED;
				}
			}
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.DIALOGUE, npc, "Haha! You guys look like you really enjoyed that!");
			if (p.flag[5][8]) {
				Task.addTask(Task.DIALOGUE, npc, "Come back anytime, squirt!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Looks like that might've been too spicy for you. C'mon, let's get you back home.");
				Task t = Task.addTask(Task.TELEPORT, "");
				t.counter = 4;
				t.start = 68;
				t.finish = 63;
				
			}
		});
		
		scriptMap.put(168.0, (npc) -> { // shroom guy
			Task.addTask(Task.MUSHROOM, npc, "Gimmie, gimmie, GIMMIE!");
		});

		scriptMap.put(178.0, (npc) -> { // research a
			Task.addTask(Task.DIALOGUE, npc, "I specialize in checking those strange evolution methods for your Pokemon.");
			if (!p.flag[1][20]) {
				p.flag[1][20] = true;
				Task.addTask(Task.DIALOGUE, npc, "Wait a minute... we haven't met yet, have we?");
				Task.addTask(Task.DIALOGUE, npc, "You're the Professor's kid, right? Here, he asked me to give you this if you ever passed by.");
				Random random = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
				int id = getUnregisteredBasePokemon(random);
				Egg egg = new Egg(id);
				if (egg.getAbility(2) != Ability.NULL) {
					egg.abilitySlot = 2;
					egg.setAbility();
				}
				Task.addTask(Task.GIFT, "", egg);
				Task.addTask(Task.DIALOGUE, npc, "Let me know if you want to check any strange evolution progress!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Have any to show me?");
				Task.addTask(Task.EVO_INFO, npc, "");
			}
		});

		scriptMap.put(124.0, (npc) -> { // merlin outside gym
			p.flag[6][5] = true;
			Task.addTask(Task.DIALOGUE, npc, "The fever's fading, the mountain's quiet... Seems you handled things nicely. A true magician knows when to step in and when to let the show play out on its own.");
			Task.addTask(Task.DIALOGUE, npc, "Ah, where are my manners? The name's Merlin, Gym Leader of Rawwar City. If you've met my granddaughter, I imagine she had some... strong opinions about you.");
			Task.addTask(Task.DIALOGUE, npc, "She sees the world in black and white. Heroes and villains, tricks and truths. But magic, real magic, lies in the in-between.");
			Task.addTask(Task.DIALOGUE, npc, "You, though... You're full of surprises. And I do love a good surprise.");
			Task.addTask(Task.DIALOGUE, npc, "Here, consider this a reward for your performance.");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.HM07;
			Task.addTask(Task.DIALOGUE, npc, "You'll need this if you want to keep climbing higher once you defeat me. Just don't let the heights get to your head.");
			Task.addTask(Task.DIALOGUE, npc, "Now then, you've come all this way. Might as well see if your magic can match mine.");
			Task.addTask(Task.DIALOGUE, npc, "Step inside when you're ready. But be warned - my tricks aren't just for show.");
			
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(124.1, (npc) -> { // arthra cutscene
			p.flag[7][1] = true;
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addNPCMoveTask('y', 84 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addNPCMoveTask('x', 27 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addNPCMoveTask('x', 25 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			
			Task.addTask(Task.DIALOGUE, npc, "A massive beam of light just shot up from Mt. Splinkty! The whole sky lit up like a spotlight, and not the fun kind.");
			Task.addTask(Task.DIALOGUE, npc, "I don't know what Team Eclipse is doing up there, but it's big. Bigger than the mountain, even.");
			Task.addTask(Task.DIALOGUE, gp.npc[124][22], "..Arthra? Is that you?");
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addNPCMoveTask('x', 26 * gp.tileSize, player, false, 4);
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.DIALOGUE, npc, "Grandpa! It's so good to see you.");
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.DIALOGUE, gp.npc[124][22], "Arthra! I felt the shift all the way inside the Gym. It was like the air itself cracked open.");
			Task.addTask(Task.DIALOGUE, gp.npc[124][22], "If Team Eclipse is behind this, then we can't waste any more time. Mt. Splinkty won't wait for us.");
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.DIALOGUE, npc, "Glad you're here, Grandpa. I was just about to head out myself.");
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.DIALOGUE, gp.npc[124][22], "Then let's not waste another heartbeat. We'll meet you at the summit, <@>.");
			Task t = Task.addTask(Task.FLAG, "");
			t.start = 7;
			t.finish = 0;
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			
		});

		scriptMap.put(146.0, (npc) -> { // arthra top of splinkty gauntlet
			if (!p.flag[7][6]) {
				Task.addTask(Task.DIALOGUE, npc, "Took you long enough. What, did you have to hike back down for trail mix?");
				Task.addTask(Task.DIALOGUE, npc, "Anyway, this is it. Inside, they've corrupted the PC, only letting you access your Gauntlet Box.");
				Task.addTask(Task.DIALOGUE, npc, "You've used one before I'm sure, so I'll keep this short.");
				Task.addTask(Task.DIALOGUE, npc, "Once you're inside, you're locked in with your party and up to four extra Pokemon you place in the Gauntlet Box.");
				Task.addTask(Task.DIALOGUE, npc, "No PC access. No going back. Once you go in, that's all you've got.");
				Task.addTask(Task.DIALOGUE, npc, "So prep wisely. We'll handle anything and anyone that tries to come up after you.");
				int selected = p.getAmountSelected();
				if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
					String message = "Hey dumbass, you don't have 4 Pokemon selected to bring in the Gauntlet Box.\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).";
					for (String s : message.split("\n")) {
						Task.addTask(Task.DIALOGUE, npc, s);
					}
				}
				gp.ui.commandNum = 1;
				Task.addTask(Task.CONFIRM, npc, "Are you ready to battle as soon as you enter? There's no going back once I let you in.", 0);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Merlin's pushing himself too hard, as usual. I'm making sure he doesn't keel over before the real fight starts.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Don't worry about us. We'll catch up when the time's right.");
				Task.addTask(Task.DIALOGUE, npc, "But you - you've got to move. Head straight into Ghostly Woods.");
				Task.addTask(Task.DIALOGUE, npc, "The heart of the forest - where all the possessed Pokemon were acting weird - that's where that Eclipse machine is.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "I don't like it... I feel like something worse than Eclipse is guarding it. Stay sharp, okay?");
			}
		});
		
		scriptMap.put(146.1, (npc) -> { // merlin top of splinkty
			if (!p.flag[7][6]) {
				Task.addTask(Task.DIALOGUE, npc, "You've come a long way, <@>. Arthra may not say it, but she's impressed.");
				Task.addTask(Task.DIALOGUE, npc, "I'll be right here. Go on - show them why the mountain trembled.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Hff... This old vessel's not what it used to be. The protection spell took more out of me than I thought.");
				Task.addTask(Task.DIALOGUE, npc, "I'm afraid I'll be of little use in battle for now. Arthra's insisted I stay here until I recover a sliver of strength.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "But you - you're still strong. You have to head to the heart of Ghostly Woods.");
				Task.addTask(Task.DIALOGUE, npc, "That cursed machine Team Eclipse built - it's the nexus of all this madness.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "If you can destroy it, we may still have a chance to stop Dragowrath from gaining more power.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "And... be careful. Something alien stirs in that forest. Something just as bad as even Dragowrath's wrath.");
			}
		});
		
		scriptMap.put(149.0, (npc) -> { // rick 3 cutscene
			Task.addNPCMoveTask('y', 64 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Well, look who's back to play hero again.");
			Task.addNPCMoveTask('y', 62 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.DIALOGUE, npc, "You just keep crawling into places you don't belong. I told you before - we're summoning forces way beyond your comprehension.");
			Task.addTask(Task.DIALOGUE, npc, "The machine behind me? It's the real deal. You won't be walking out of here once it's finished.");
			Task.addNPCMoveTask('y', 63 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.DIALOGUE, npc, "But I'm not letting you even get that far. Let's see if your luck's finally run out.");
			Task.addTask(Task.BATTLE, "", 343);
		});
		
		scriptMap.put(149.1, (npc) -> { // fred 4 cutscene
			Task.addNPCMoveTask('y', 58 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "You again.");
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "You just couldn't leave it alone, huh? I gave you a chance back at Shadow Path, but now you're charging into the heart of everything?");
			Task.addNPCMoveTask('y', 57 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.DIALOGUE, npc, "This isn't just another mission, <@>. Maxwell's about to rewrite everything. And I'm not letting you stop that.");
			Task.addTask(Task.DIALOGUE, npc, "You'll have to go through me. And this time? I'm not holding back.");
			Task.addTask(Task.BATTLE, "", 344);
		});
		
		scriptMap.put(149.2, (npc) -> { // maxwell 2 cutscene
			if (!p.flag[7][4]) {
				Task.addNPCMoveTask('y', 51 * gp.tileSize, player, false, 4);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "So... you've arrived. The little pest that keeps gnawing at the edges of greatness.");
				Task.addTask(Task.DIALOGUE, npc, "Did you enjoy the show so far? The lights, the shadows, the chaos?");
				Task.addTask(Task.DIALOGUE, npc, "Come, <@>. Don't be afraid.");
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 50 * gp.tileSize, player, false, 4);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.DIALOGUE, npc, "Stand beside me. Just for a moment.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addNPCMoveTask('y', 49 * gp.tileSize, player, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addCameraMoveTask('y', 200, 4);
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.DIALOGUE, npc, "Look at what we've built. The culmination of years... decades of silence, research, and sacrifice.");
				Task.addTask(Task.SLEEP, "", 120);
				Task.addTask(Task.DIALOGUE, npc, "This machine - this Earth Core Gauntlet - will reshape everything. Not with noise or fire... but with memory.");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addCameraMoveTask('y', 0, 4);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, npc, "The Earth remembers, <@>. I've given it a voice. A will. A weapon.");
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "And you, the one who's clawed your way through shadows and rubble... you want to silence it?");
				Task.addTask(Task.DIALOGUE, npc, "Then go on. Let's see what you've learned after all this time.");
				Task.addTask(Task.BATTLE, "", 345);
			} else {
				Task.addTask(Task.DIALOGUE, npc, "No... no, this can't be. You were just... noise. An obstacle.");
				Task.addTask(Task.DIALOGUE, npc, "You've undone everything... again...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "*sigh* Very well...");
				Task.addTask(Task.DIALOGUE, npc, "It's over. Team Eclipse... is finished.");
				Task.addTask(Task.DIALOGUE, npc, "You've broken our machine. Or perhaps I should say... I have.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.SHAKE, "", 100);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, npc, "But even now... it's too late.");
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "You felt it, didn't you? That rumble beneath your feet. That shift in the air.");
				Task.addTask(Task.DIALOGUE, npc, "The Earth has already answered. The summoning is complete.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "Whatever happens next... is no longer in my hands. Or yours.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "So take your little victory, <@>. You earned it.");
				Task.addTask(Task.DIALOGUE, npc, "But remember this - the Earth doesn't forget. And neither do I...");
				Task t = Task.addTask(Task.FLAG, "");
				t.start = 7;
				t.finish = 5;
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(146.2, (npc) -> { // dragowrath cutscene
			p.flag[7][6] = true;
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "<@>! Finally! Are you okay -");
			Task.addTask(Task.SPOT, gp.npc[146][1], "");
			Task.addTask(Task.SHAKE, "", 200);
			Task.addTask(Task.DIALOGUE, npc, "...Pathetic. Every last one of them - pawns, weaklings, hollow shells.", 1);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addTask(Task.SPOT, player, "");
			Task.addNPCMoveTask('y', 24 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.SPEAK, npc, "Minions? Bah! They were never worthy to begin with.", 1);
			Task.addTask(Task.SPEAK, npc, "The universe... the stars... the endless reaches of power... and I'm expected to leave it in the hands of insects?", 1);
			Task.addTask(Task.SPEAK, npc, "No. No more games. No more middlemen. I will take this world myself. Tear it free from the crust. Mold it anew.", 1);
			Task.addTask(Task.SPEAK, npc, "And when the last mind bends to my will... when every heart forgets its former self... then, and only then, will this wretched sphere know its place.", 1);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.SPEAK, npc, "I am its beginning. I am its end.", 1);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.SPEAK, npc, "Hide if you wish. Struggle if you dare. But know this - your fates are already mine.", 1);
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Ancient flame, ancient mind - Form the circle, hold the line!");
			Task.addParticleTask(gp.npc[146][2], "smoke", new Color(237, 19, 223), 100);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Hff... That should hold for a little while. Mind protection spell - basic, but enough to stop him from sinking his teeth into your heads... for now.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "I'm... not what I used to be. Magic runs thinner in these old veins.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "But it will buy us a sliver of time to think. And we need it, desperately.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "That thing... It wasn't just flexing. It meant every word. It's already moving - trying to take over everything, everywhere.");
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "What the hell do we even do against that?");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "...There was a time... before all this, before the stars cracked open and spat out creatures like him, that we believed in something greater.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "A higher order. A power beyond understanding. We called it the 'First Light' - the source of all magic, all life.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "We built temples. Sang songs to the heavens. We believed the universe cared.");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "But when Dragowrath's kind arrived... when they descended from their broken worlds to colonize ours... we saw the truth.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "There was no First Light. No higher power waiting to save us. Just parasites with sharper claws.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "...That was the end of the old religion?");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Yes. It crumbled. Just stories and dust now... But...");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "...Not everything was a lie.");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "I heard a legend once. Grandfather used to tell it when I was little - like a scary bedtime story.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "About Dragowrath... and his offspring.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "Two of them. One born from Faith, the other from Logic.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "They were supposed to be even stronger than him, if they ever woke up.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Not just a legend.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Real.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "But hidden. Buried. Forgotten on purpose.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "The offspring - twin entities. One embodies pure faith, the blind leap into the unknown.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "The other, cold, relentless logic, the drive to understand and conquer.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Both... necessary... yet both far too dangerous for this world.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "If either were to awaken fully without balance, they would overwhelm everything - humanity, Pokemon, all life.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "...So where are they? What keeps them asleep?");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Meteorite energy.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Pure meteorite energy, unfiltered by human hands. It acts as both lock and key. Without it, their spirits remain dormant, sleeping beneath the earth.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "And lately... there's been no shortage of fallen stars crashing into this region.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "...Wait. The Electric and Shadow meteorites. The ones mutating Pokemon near their impact sites?");
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "You're saying those things - they carry enough energy to wake the offspring?!");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Not just wake them. Shape them. Twist them, if misused.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "I imagine Team Eclipse wasn't harvesting that meteorite energy just to fuel that machine of theirs in Ghostly Woods.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "They were trying to prime the land. Disturb the balance. Create footholds for possession.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "That's why the Pokemon in Ghostly Woods started going berserk. It wasn't random. They were experimenting.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "Testing if they could possess Pokemon - before moving onto... people.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "And now that Eclipse is gone, Dragowrath has no leash. No patience left.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "He'll go after the meteorite energy himself.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "And if he manages to fuse that power into himself - or worse, awaken his offspring - there will be no saving this region.");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "We are out of time.");
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "We must find the meteorite cores before he does.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "We must prevent the awakening.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[146][1], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[146][1], "Then what are we waiting for? Let's go dragon hunting.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "Easy to say. Not so easy to do.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "But... I think with your strength, <@>, and your stubbornness, Arthra...");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[146][2], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[146][2], "...we might just stand a chance yet.");
		});
		
		scriptMap.put(107.2, (npc) -> { // arthra in ghostly woods heart
			if (!p.flag[7][9] && !p.flag[7][10]) {
				Task.addNPCMoveTask('y', 59 * gp.tileSize, player, false, 2);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SPOT, player, "");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.DIALOGUE, npc, "<@>! There you are!");
				Task.addNPCMoveTask('y', 63 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.INTERACTIVE, gp.iTile[107][0], "", 0);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addNPCMoveTask('y', 61 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "C'mon Grandpa, hurry!");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Hff... Hff...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', 63 * gp.tileSize, gp.npc[107][14], false, 2);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.LEFT);
				Task.addNPCMoveTask('x', 50 * gp.tileSize, gp.npc[107][14], false, 2);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.UP);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addNPCMoveTask('y', 60 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 51 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addNPCMoveTask('y', 61 * gp.tileSize, gp.npc[107][14], false, 4);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addNPCMoveTask('y', 60 * gp.tileSize, gp.npc[107][14], false, 4);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Hff... You did it... the machine is down...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "But the damage... it's already soaked into the land. The corruption won't fade so easily.");
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				if (player.worldX > 50 * gp.tileSize) {
					Task.addTask(Task.TURN, player, "", Task.RIGHT);
					Task.addNPCMoveTask('x', 50 * gp.tileSize, player, false, 2);
				}
				Task.addNPCMoveTask('y', 59 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "Hold up... look!");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('x', 49 * gp.tileSize, player, false, 2);
				Task.addNPCMoveTask('x', 50 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "...Meteorite cores. Two of them.");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "One radiates Faith... unwavering belief, hope against the darkness.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "The other, pure Logic... cold calculation, the unshakable pursuit of truth.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addNPCMoveTask('x', 49 * gp.tileSize, gp.npc[107][14], false, 2);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Both are powerful. Both could be the key to stopping Dragowrath.");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "But only one can be awakened.");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Tell me, <@>. Which do you believe will triumph against the coming storm?");
				Task.addTask(Task.SLEEP, "", 10);
				gp.ui.commandNum = -1; // set to -1 so the player has to arrow key first
				Task.addTask(Task.CONFIRM, gp.npc[107][14], "Faith... or Logic?", 7);
			} else {
				boolean faith = p.flag[7][9];
				if (faith) {
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.FAITH_CORE;
				} else {
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.LOGIC_CORE;
				}
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "So... You've made your choice...");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "I had a feeling you would choose that path.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				String[] dialogues = faith
					? new String[] {
						"Seriously?! Faith?!",
						"Hope and prayers won't stop that monster. We need to think, plan, understand what we're up against!",
						"...Whatever. Guess we'll see who's right.",
						"Then we go to the Abandoned Tower.",
						"The last sanctuary of the old faith - where the Spirit of Faith slumbers still."
					}
					: new String[] {
						"Seriously?! Logic?!",
						"You think you can out-think a monster like that?! It's bigger than reason! We need faith to beat it!",
						"...Whatever. Guess we'll see who's right.",
						"Then we seek the Deep Chasm.",
						"Where the mind sharpens against the void - where the Spirit of Logic lies waiting."
					}
				;
				
				Task.addTask(Task.DIALOGUE, npc, dialogues[0]);
				Task.addTask(Task.DIALOGUE, npc, dialogues[1]);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, dialogues[2]);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", faith ? Task.RIGHT : Task.LEFT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], dialogues[3]);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], dialogues[4]);
				
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "So that's it, huh?");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addNPCMoveTask('y', 60 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, npc, "You go chasing your dreams... and I'm supposed to just stand here and let it happen?");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "No.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "You have your own path to walk, Arthra.");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "One not bound to <@>'s choice... but born from your own spirit.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "...You really think I can do this?");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "Alone?");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "You're stronger than you know.");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "And stubborn enough to change fate itself if you put your mind to it.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.DIALOGUE, npc, "Alright.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "You and <@> go your way.");
				Task.addTask(Task.DIALOGUE, npc, "I'll figure out mine.");
				if (faith) { // move towards abandoned tower
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addNPCMoveTask('y', 62 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.TURN, gp.npc[107][14], "", Task.DOWN);
					Task.addTask(Task.SLEEP, "", 60);
					Task.addTask(Task.TURN, npc, "", Task.UP);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.DIALOGUE, npc, "I'm not just going to sit around waiting to be saved. If I can make a difference... then I will.");
					Task.addTask(Task.SLEEP, "", 10);
					Task.addTask(Task.TURN, npc, "", Task.DOWN);
					Task.addTask(Task.SLEEP, "", 5);
					Task.addNPCMoveTask('y', 64 * gp.tileSize, npc, false, 4);
				} else { // go towards exit
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.TURN, npc, "", Task.RIGHT);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addNPCMoveTask('x', 52 * gp.tileSize, npc, false, 4);
					Task.addTask(Task.SLEEP, "", 60);
					Task.addTask(Task.TURN, npc, "", Task.LEFT);
					Task.addTask(Task.SLEEP, "", 15);
					Task.addTask(Task.DIALOGUE, npc, "I'm not just going to sit around waiting to be saved. If I can make a difference... then I will.");
					Task.addTask(Task.SLEEP, "", 10);
					Task.addTask(Task.TURN, npc, "", Task.UP);
					Task.addTask(Task.SLEEP, "", 5);
					Task.addNPCMoveTask('y', 59 * gp.tileSize, npc, false, 4);
				}
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Then it's decided.");
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "I'll walk with you, <@> - to the end of whatever road you have chosen.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[107][14], "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[107][14], "Whether it leads us to salvation... or ruin.");
			}
		});
		
		scriptMap.put(107.3, (npc) -> { // merlin in ghostly woods heart after logic/faith scene
			p.flag[7][11] = true;
			boolean faith = p.flag[7][9];
			if (faith) {
				Task.addTask(Task.DIALOGUE, npc, "The road of faith lies ahead, <@>.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Seed the Abandoned Tower - a relic of the old world, steeped in the hopes and prayers of those who came before us.");
				Task.addTask(Task.DIALOGUE, npc, "There, your chosen path will be tested.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "I'll await you near its gates.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "You have chosen the path of logic, <@>.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Head toward the Deep Chasm - a place where thought alone clings to the edges of existence.");
				Task.addTask(Task.DIALOGUE, npc, "There, reason must prove itself against the void.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "I'll meet you near the entrance.");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(190.0, (npc) -> { // merlin guide faith route
			Task.addTask(Task.DIALOGUE, npc, "This is the last shrine to the First Light.");
			Task.addTask(Task.DIALOGUE, npc, "Long ago, those who believed built this tower to reach toward the heavens... to hold faith even when the skies fell silent.");
			Task.addTask(Task.DIALOGUE, npc, "The guardians within will not yield easily.");
			Task.addTask(Task.DIALOGUE, npc, "They will test the strength of your heart - and the purity of your conviction.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Remember... true faith does not mean blind hope.");
			Task.addTask(Task.DIALOGUE, npc, "It means standing firm even when all else falls away.");
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.DIALOGUE, npc, "I cannot walk this trial for you, <@>.");
			Task.addTask(Task.DIALOGUE, npc, "But I will walk beside you... to the end.");
		});
		
		scriptMap.put(144.0, (npc) -> { // merlin guide logic route
			Task.addTask(Task.DIALOGUE, npc, "Beyond here lies the Deep Chasm.");
			Task.addTask(Task.DIALOGUE, npc, "When the First Light faltered, those who doubted carved this place - not with belief, but with understanding.");
			Task.addTask(Task.DIALOGUE, npc, "The guardians within were born from pure thought - shaped by knowledge... and fear.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Logic will guide you, but it will also challenge you.");
			Task.addTask(Task.DIALOGUE, npc, "Beware: reason without compassion can be as deadly as ignorance.");
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.DIALOGUE, npc, "This is your trial to bear, <@>.");
			Task.addTask(Task.DIALOGUE, npc, "I will follow where you lead... whatever end awaits.");
		});
		
		scriptMap.put(191.0, (npc) -> { // tower guard faith route
			if (p.flag[7][9] && !p.flag[7][15]) { // finn has faith and needs to attempt the gauntlet
				Task.addTask(Task.DIALOGUE, npc, "Traveler of faith...");
				Task.addTask(Task.DIALOGUE, npc, "Beyond these doors lies the trial of the soul.");
				Task.addTask(Task.DIALOGUE, npc, "This tower was raised by those who clung to hope even when the heavens fell silent.");
				Task.addTask(Task.DIALOGUE, npc, "Their prayers built these walls. Their dreams carved these stones.");
				Task.addTask(Task.DIALOGUE, npc, "Within, you will find no exit until your spirit proves worthy.");
				Task.addTask(Task.DIALOGUE, npc, "You must bring 10 Pokemon total, no more - and no less.");
				
				if (!p.hasFullPartyAndGauntletBox()) { // Not enough selected
					Task.addTask(Task.DIALOGUE, npc, "...");
					Task.addTask(Task.DIALOGUE, npc, "You do not have 10 Pokemon selected to bring.");
					Task.addTask(Task.DIALOGUE, npc, "You can choose what Pokemon to bring using the Gauntlet Box in the PC here (press [\u2191] when selecting a box at the top).");
					Task.addTask(Task.DIALOGUE, npc, "Please ensure that your party is full of 6 Pokemon and your Gauntlet Box is full of 4 extra Pokemon.");
				} else {
					gp.ui.commandNum = 1;
					Task.addTask(Task.CONFIRM, npc, "Are you prepared to stake your soul upon your faith?", 11);
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "You... don't have faith... be gone...");
			}
		});
		scriptMap.put(191.011, (npc) -> {
			if (!gp.player.p.flag[7][12]) { // player hasn't done the cutscene with Merlin yet
				gp.player.p.flag[7][12] = true;
				gp.npc[191][1].worldY = 83 * gp.tileSize;
				gp.npc[191][1].direction = "up";
				Task.addTask(Task.TURN, gp.player, "", Task.DOWN);
				Task.addNPCMoveTask('y', 77 * gp.tileSize, gp.npc[191][1], false, 4);
				Task.addTask(Task.DIALOGUE, gp.npc[191][1], "This is where your path truly begins, <@>.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, gp.player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.player, false, 2);
				Task.addTask(Task.TURN, gp.player, "", Task.DOWN);
				Task.addNPCMoveTask('y', (int) (73.75 * gp.tileSize), gp.npc[191][1], false, 2);
				Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, gp.npc[191][1], "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, gp.npc[191][1], "You and I, we're in this together.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[191][1], "But remember: Faith is not blind. It is a choice - made again and again, even in the darkness.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[191][1], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[191][1], "Let's do this.");
			}
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = 191;
			t.start = 49;
			t.finish = 47;
			gp.puzzleM.doReset(true);
		});
		
		scriptMap.put(191.1, (npc) -> { // merlin in AT 1B
			p.heal();
			Task.addTask(Task.DIALOGUE, npc, "I've got your team all healed up. You have to be prepared for these guards.");
			Task.addTask(Task.DIALOGUE, npc, "These spirits... the Disciples... they whisper things. Fragments. A letter here, a silence there.");
			Task.addTask(Task.DIALOGUE, npc, "Pieces of something... familiar. A name? A word? Hm...");
			Task.addTask(Task.DIALOGUE, npc, "And those paintings on the walls... one of them must be right. But which?");
			Task.addTask(Task.DIALOGUE, npc, "They're like reflections in a broken mirror - colors all blurred, meanings scattered.");
			Task.addTask(Task.DIALOGUE, npc, "If I were to guess - and guessing is half of wisdom - I'd say... each phantom offers you a part of the answer.");
			Task.addTask(Task.DIALOGUE, npc, "Speak to them. Gather their hints. Listen closely.");
			Task.addTask(Task.DIALOGUE, npc, "When you pick your painting, bring your answer to him - the one who waits in the grand painting. Only then will we see if you've stirred his memory...");
			Task.addTask(Task.DIALOGUE, npc, "...Oh, and if you lose your way... there's a blank canvas here.");
			Task.addTask(Task.DIALOGUE, npc, "It might help... or maybe it just reflects your own uncertainty. Hahh... hard to tell with these things.");
			Task.addTask(Task.DIALOGUE, npc, "I'll wait here, I'm too weak holding this mind-control spell from gnawing at our minds to battle anyone. We'll reconnect once you make it to the top.");
		});
		
		scriptMap.put(193.0, (npc) -> { // temple ball guy in AT 3A
			if (!p.bag.contains(Item.TEMPLE_BALL)) {
				Puzzle currentPuzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
				if (currentPuzzle.isLocked()) {
					Task.addTask(Task.DIALOGUE, npc, "You used my Temple Ball...?");
					Task.addTask(Task.DIALOGUE, npc, "You must pray that its the species he required.");
					Task.addTask(Task.DIALOGUE, npc, "I can't give you another until you cast the reset spell on this place.");
					Task.addTask(Task.DIALOGUE, npc, "Find the empty painting to try again.");
					return;
				} else {
					Task.addTask(Task.DIALOGUE, npc, "You'll need this.");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.TEMPLE_BALL;
					Task.addTask(Task.DIALOGUE, npc, "The Temple Ball will catch any Pokemon here without fail.");
				}
			}
			Task.addTask(Task.DIALOGUE, npc, "Catch the correct Pokemon in the Temple Ball and show him.");
			Task.addTask(Task.DIALOGUE, npc, "If you're unsure which... talk to the spirits. They will give you objective hints on the Pokemon's aspects...");
			Task.addTask(Task.DIALOGUE, npc, "There are 4 hints that apply to the Pokemon's typing in some way, 3 that apply to their moves, 2 for their stats, and 1 miscellaneous hint.");
			Task.addTask(Task.DIALOGUE, npc, "Also, critically, each individual hint applies to more than one Pokemon, at the least.");
		});
		
		scriptMap.put(195.0, (npc) -> { // temple orb guy in AT 5A
			Puzzle currentPuzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
			if (!currentPuzzle.isLocked()) {
				Task.addTask(Task.DIALOGUE, npc, "You'll need these orbs to reach master.");
				Task t = Task.addTask(Task.ITEM, "You got 250 Temple Orbs!");
				t.item = Item.TEMPLE_ORB;
				t.counter = 250;
				Task.addTask(Task.DIALOGUE, npc, "But that won't be enough. You need to prove you have faith - faith in the odds.");
				currentPuzzle.setLocked(true);
			} else {
				if (currentPuzzle.isLost()) {
					Task.addTask(Task.DIALOGUE, npc, "But you're out of orbs...?");
					Task.addTask(Task.DIALOGUE, npc, "I pray for you. You aren't strong enough to meet him.");
					Task.addTask(Task.DIALOGUE, npc, "I can't give you any more until you cast the reset spell on this place.");
					Task.addTask(Task.DIALOGUE, npc, "Find the empty painting to try again.");
					return;
				}
			}
			Task.addTask(Task.DIALOGUE, npc, "Gamble against master using his paintings until you have 1000 or more orbs. He's testing your will...");
		});
		
		scriptMap.put(196.0, (npc) -> { // faith dragon in 6A
			if (!p.flag[7][13]) {
				p.flag[7][13] = true;
				Task.addTask(Task.TEXT, "The ancient being is in a deep slumber... its body radiates a warm, sacred aura.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SPOT, player, "");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "So it wasn't just a legend... it truly is Relopamil. The dragon of faith.");
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "I've never seen it with my own eyes until now... but I can feel it. The divine presence.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', 49 * gp.tileSize, gp.npc[196][1], false, 2);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "You've brought the Faith Core, yes? Then we mustn't delay. Dragowrath's power grows by the minute...");
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Let's awaken Relopamil, before it's too late.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "By the sacred breath... awaken, Relopamil. The world needs you once more.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.AWAKE, npc, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPOT, gp.npc[196][1], "");
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "It's awake...!");
		        Task.addTask(Task.SLEEP, "", 10);
		        Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 5);
		        Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Here, quick! Take this - it's a Temple Ball. Forged long ago by the founders of the old faith.");
		        Task.addTask(Task.DIALOGUE, gp.npc[196][1], "It will ensure Relopamil joins you, without resistance!");
		        Task t = Task.addTask(Task.ITEM, "");
		        t.item = Item.TEMPLE_BALL;
		        Task.addTask(Task.SLEEP, "", 15);
		        Task.addTask(Task.TURN, player, "", Task.UP);
		        Task.addTask(Task.SLEEP, "", 15);
			}
			Task.addTask(Task.DIALOGUE, npc, "DRAAAGH!");
			Task t = Task.addTask(Task.BATTLE, "", 405);
			t.start = 234;
		});
		
		scriptMap.put(196.1, (npc) -> { // dragowrath AT
			if (!p.flag[7][16]) {
				p.flag[7][16] = true;
				Task.addTask(Task.SPOT, gp.npc[196][1], "");
				Task.addTask(Task.SPOT, player, "");
				Task.addNPCMoveTask('y', 46 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SPEAK, npc, "You dare...", 1);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Dragowrath! No - this place is sacred!");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, npc, "You awaken my child... as if it belongs to you. As if your brittle faith is worthy of such power.", 1);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, npc, "Relopamil was born of me. Molded from me. Do you think your trembling hope can sever my bloodline?", 1);
				Task.addTask(Task.SPEAK, npc, "Let me show you what true will looks like.", 1);
				Task t = Task.addTask(Task.BATTLE, "", 406);
				t.start = 235;
			} else {
				p.current.fainted = false;
				p.heal();
				Pokemon[] tempTeam = p.team;
				p.team = p.tempTeam;
				p.setCurrent();
				p.tempTeam = tempTeam; // preserve the legendary dragon here
				Task.addTask(Task.SPEAK, npc, "So... this is the fruit of your faith. Weak. Broken. Not enough.", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, npc, "You believed it would be enough. Just as she did.", 1);
				Task.addTask(Task.SPEAK, npc, "Arthra, too, raised her voice to the skies. She, too thought belief alone could change the world.", 1);
				Task.addTask(Task.SPEAK, npc, "She was wrong. And now, she suffers for it.", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, player, "(What...?)");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.SPEAK, npc, "You still don't understand, do you.", 1);
				Task.addTask(Task.SPEAK, npc, "You were born in a world that clings to stories. But stories don't win wars.", 1);
				Task.addTask(Task.SPEAK, npc, "I will show you a world that no longer needs you... or them.", 1);
				Task.addTask(Task.SLEEP, "", 15);
		        Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Ngh - no... not yet...!");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addParticleTask(gp.npc[196][1], "totem", new Color(66, 252, 231), 100);
				int diff = 52 * gp.tileSize - player.worldX;
				Task.addNPCMoveTask('x', 52 * gp.tileSize, player, false, diff / 2);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addNPCMoveTask('y', 48 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SPEAK, npc, "Your time is over, Merlin. You placed your faith in the wrong vessel.", 1);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Hrk - Nngh... G-Get... get them out of here!");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[196][1], "Sanctum Arum... Portalis Verum...!");
				Task t = Task.addTask(Task.TELEPORT, "");
				t.counter = 186;
				t.start = 33;
				t.finish = 42;
				t.color = Color.WHITE;
				Task.addTask(Task.TURN, player, "", Task.DOWN);
			}
		});
		
		scriptMap.put(196.2, (npc) -> { // merlin 6A AT
			if (p.bag.contains(Item.TEMPLE_BALL)) {
				Task.addTask(Task.DIALOGUE, npc, "Try and capture the ancient beast with the special ball I gave you!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Here, take this and capture the beast! Quick!");
				Task t = Task.addTask(Task.ITEM, "");
		        t.item = Item.TEMPLE_BALL;
			}
		});
		
		scriptMap.put(197.0, (npc) -> { // chasm guard logic route
			if (p.flag[7][10]) { // finn has logic and needs to attempt the gauntlet
				Task.addTask(Task.DIALOGUE, npc, "Seeker of logic...");
				Task.addTask(Task.DIALOGUE, npc, "Before you lies the Chasm - a wound carved by thought itself.");
				Task.addTask(Task.DIALOGUE, npc, "Here, those who abandoned faith sought to survive through reason alone.");
				Task.addTask(Task.DIALOGUE, npc, "They built nothing. They prayed to nothing. Only understanding held them back from the void.");
				Task.addTask(Task.DIALOGUE, npc, "Inside, you will find no mercy. No second chances.");
				Task.addTask(Task.DIALOGUE, npc, "You must bring 10 Pokemon total, no more - and no less.");
				
				if (!p.hasFullPartyAndGauntletBox()) { // Not enough selected
					Task.addTask(Task.DIALOGUE, npc, "...");
					Task.addTask(Task.DIALOGUE, npc, "You do not have 10 Pokemon selected to bring.");
					Task.addTask(Task.DIALOGUE, npc, "You can choose what Pokemon to bring using the Gauntlet Box in the PC here (press [\u2191] when selecting a box at the top).");
					Task.addTask(Task.DIALOGUE, npc, "Please ensure that your party is full of 6 Pokemon and your Gauntlet Box is full of 4 extra Pokemon.");
				} else {
					gp.ui.commandNum = 1;
					Task.addTask(Task.CONFIRM, npc, "Are you prepared to face the void armed only with logic?", 12);
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "You... aren't worthy... be gone...");
			}
		});
		scriptMap.put(197.012, (npc) -> {
			if (!p.flag[7][12]) { // player hasn't done the cutscene with Merlin yet
				p.flag[7][12] = true;
				gp.npc[197][1].worldY = 82 * gp.tileSize;
				gp.npc[197][1].direction = "up";
				Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
				Task.addNPCMoveTask('y', 77 * gp.tileSize, gp.npc[197][1], false, 4);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 47 * gp.tileSize, gp.npc[197][1], false, 4);
				Task.addTask(Task.DIALOGUE, gp.npc[197][1], "This is where your path truly begins, <@>.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, gp.player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addNPCMoveTask('x', 51 * gp.tileSize, gp.player, false, 2);
				Task.addTask(Task.TURN, gp.player, "", Task.LEFT);
				Task.addNPCMoveTask('x', 48 * gp.tileSize, gp.npc[197][1], false, 4);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
				Task.addNPCMoveTask('y', (int) (75.75 * gp.tileSize), gp.npc[197][1], false, 4);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 50 * gp.tileSize, gp.npc[197][1], false, 2);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, gp.npc[197][1], "You and I, we're in this together.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[197][1], "The mind can be a weapon... or a prison, <@>. Trust in your wit - but trust in yourself more.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[197][1], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[197][1], "Let's do this.");
			}
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = 197;
			t.start = 51;
			t.finish = 63;
			gp.puzzleM.doReset(false);
		});
		
		scriptMap.put(197.1, (npc) -> { // merlin in Deep Chasm 0B
			p.heal();
			Task.addTask(Task.DIALOGUE, npc, "Your team's healed up. I've done what I can to shield us from the noise - the static, the pressure... you feel it too, don't you?");
			Task.addTask(Task.DIALOGUE, npc, "This place... it's not like the tower where Arthra is. No whispers. Just cold, unrelenting silence. A silence that thinks.");
			Task.addTask(Task.DIALOGUE, npc, "The Wardens here don't test your spirit - they dissect your logic. Strip away impulse. Demand pure, mechanical thought.");
			Task.addTask(Task.DIALOGUE, npc, "First, here, a maze. Winding, shifting. Simple on the surface, but every wrong step adds up. Errors compound.");
			Task.addTask(Task.DIALOGUE, npc, "Then ice blocks... thin, cracking. The puzzle breaks under miscalculation. Every move a variable. Misplace one and-back you go.");
			Task.addTask(Task.DIALOGUE, npc, "After that, the trap tightens. Battles. Rapid. Relentless. One after another. No room for breath - just sequence.");
			Task.addTask(Task.DIALOGUE, npc, "Next, a grid of logic gates, hidden as ice and spikes. Press one switch, another reacts. Cause and effect. Plan or perish.");
			Task.addTask(Task.DIALOGUE, npc, "And finally... a puzzle of deduction. One correct form among many. Clues scattered like fragments of an algorithm.");
			Task.addTask(Task.DIALOGUE, npc, "The Wardens... they don't *say* much. But they *think*. And they want *you* to think, too. No faith. No feeling. Just precision.");
			Task.addTask(Task.DIALOGUE, npc, "I'll stay here, focus on holding the line against the mental pull. I can't fight - not while keeping the static out of our heads.");
			Task.addTask(Task.DIALOGUE, npc, "So go. Keep your thoughts clear. Analyze, adapt, and above all - don't act without reason.");
		});
		
		scriptMap.put(201.0, (npc) -> { // temple ball guy in Chasm -4A
			if (!p.bag.contains(Item.TEMPLE_BALL)) {
				Puzzle currentPuzzle = gp.puzzleM.getCurrentPuzzle(gp.currentMap);
				if (currentPuzzle.isLocked()) {
					Task.addTask(Task.DIALOGUE, npc, "You used my Temple Ball...?");
					Task.addTask(Task.DIALOGUE, npc, "You must pray that its the species he required.");
					Task.addTask(Task.DIALOGUE, npc, "I can't give you another until you cast the reset spell on this place.");
					Task.addTask(Task.DIALOGUE, npc, "Find the empty statue pedestal to try again.");
					return;
				} else {
					Task.addTask(Task.DIALOGUE, npc, "You'll need this.");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.TEMPLE_BALL;
					Task.addTask(Task.DIALOGUE, npc, "The Temple Ball will catch any Pokemon here without fail.");
				}
			}
			Task.addTask(Task.DIALOGUE, npc, "Catch the correct Pokemon in the Temple Ball and show him.");
			Task.addTask(Task.DIALOGUE, npc, "If you're unsure which... talk to the spirits. They will give you objective hints on the Pokemon's aspects...");
			Task.addTask(Task.DIALOGUE, npc, "There are 4 hints that apply to the Pokemon's typing in some way, 3 that apply to their moves, 2 for their stats, and 1 miscellaneous hint.");
			Task.addTask(Task.DIALOGUE, npc, "Also, critically, each individual hint applies to more than one Pokemon, at the least.");
		});
		
		scriptMap.put(202.0, (npc) -> { // logic dragon in -5A
			if (!p.flag[7][13]) {
				p.flag[7][13] = true;
				Task.addTask(Task.TEXT, "The ancient being lies dormant... its mind is unreachable, like a locked door behind glass.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SPOT, player, "");
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "So this is it. Relomidel, the Logic Dragon.");
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "All calculations led here. Yet now that I stand before it... even I feel... small.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addNPCMoveTask('y', 49 * gp.tileSize, gp.npc[202][1], false, 2);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "You've brought the Logic Core, didn't you? Then there's no time to hesitate.");
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Dragowrath's presence grows - the framework is unraveling. We need Relomidel.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Code of the First Mind... awaken now. Logic eternal, emerge.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.AWAKE, npc, "");
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPOT, gp.npc[202][1], "");
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "It's awake...!");
		        Task.addTask(Task.SLEEP, "", 10);
		        Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 5);
		        Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Here, quick! Take this - it's a Temple Ball. Forged long ago by the founders of the First Light.");
		        Task.addTask(Task.DIALOGUE, gp.npc[202][1], "It will ensure Relomidel joins you, without resistance!");
		        Task t = Task.addTask(Task.ITEM, "");
		        t.item = Item.TEMPLE_BALL;
		        Task.addTask(Task.SLEEP, "", 15);
		        Task.addTask(Task.TURN, player, "", Task.UP);
		        Task.addTask(Task.SLEEP, "", 15);
			}
			Task.addTask(Task.DIALOGUE, npc, "DRAAAGH!");
			Task t = Task.addTask(Task.BATTLE, "", 404);
			t.start = 233;
		});
		
		scriptMap.put(202.1, (npc) -> { // dragowrath Deep Chasm
			if (!p.flag[7][16]) {
				p.flag[7][16] = true;
				Task.addTask(Task.SPOT, gp.npc[202][1], "");
				Task.addTask(Task.SPOT, player, "");
				Task.addNPCMoveTask('y', 46 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SPEAK, npc, "So. You found it. My child.", 1);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Dragowrath?! Here?! No - this place is shielded - !");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, npc, "Shielded by what? Knowledge? You think wisdom can bar my path?", 1);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, npc, "You awaken Relomidel as if it is your servant. As if it has not tasted my breath before.", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, npc, "That Dragon is of me. Every thought, every pattern... spun from my design.", 1);
				Task.addTask(Task.SPEAK, npc, "And now you claim it - like a formula you've proven. Let me show you what you've miscalculated.", 1);
				Task t = Task.addTask(Task.BATTLE, "", 406);
				t.start = 235;
			} else {
				p.current.fainted = false;
				p.heal();
				Pokemon[] tempTeam = p.team;
				p.team = p.tempTeam;
				p.setCurrent();
				p.tempTeam = tempTeam; // preserve the legendary dragon here
				Task.addTask(Task.SPEAK, npc, "This is logic? This is your answer?", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, npc, "Like her - so sure the mind alone could save her people.", 1);
				Task.addTask(Task.SPEAK, npc, "Arthra. She followed the same flawed algorithm.", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, player, "(What...?)");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.SPEAK, npc, "You cling to systems. Patterns. Structures.", 1);
				Task.addTask(Task.SPEAK, npc, "But the universe is not a theorem. It is a will. And mine is supreme.", 1);
				Task.addTask(Task.SPEAK, npc, "I will erase your scaffolds. Tear down your frameworks. The age of thought is over.", 1);
				Task.addTask(Task.SLEEP, "", 15);
		        Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Nngh - we're not done yet...!");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addParticleTask(gp.npc[202][1], "totem", new Color(66, 252, 231), 100);
				int diff = 52 * gp.tileSize - player.worldX;
				Task.addNPCMoveTask('x', 52 * gp.tileSize, player, false, diff / 2);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addNPCMoveTask('y', 48 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.SPEAK, npc, "You spent your life chasing knowledge, old man. And still... you understand nothing.", 1);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Hrk - Nngh... G-Get... get them out of here!");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[202][1], "Veritas fractum... Apertus nexum...!");
				Task t = Task.addTask(Task.TELEPORT, "");
				t.counter = 186;
				t.start = 33;
				t.finish = 42;
				t.color = Color.WHITE;
				Task.addTask(Task.TURN, player, "", Task.DOWN);
			}
		});
		
		scriptMap.put(202.2, (npc) -> { // merlin 6A AT
			if (p.bag.contains(Item.TEMPLE_BALL)) {
				Task.addTask(Task.DIALOGUE, npc, "Try and capture the ancient beast with the special ball I gave you!");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Here, take this and capture the beast! Quick!");
				Task t = Task.addTask(Task.ITEM, "");
		        t.item = Item.TEMPLE_BALL;
			}
		});
		
		scriptMap.put(186.0, (npc) -> { // arthra after getting wiped by dragowrath
			if (!p.flag[7][17]) {
				p.flag[7][17] = true;
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "<@>! You're okay!");
				Task.addTask(Task.DIALOGUE, npc, "I - I wasn't sure if you'd made it...!");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "I heard Merlin's voice... in my head.");
				Task.addTask(Task.DIALOGUE, npc, "It was like a whisper carried on the wind. Casting a spell.");
				Task.addTask(Task.DIALOGUE, npc, "I captured the Legendary Dragon, but Dragowrath destroyed me and it both.");
				Task.addTask(Task.DIALOGUE, npc, "Then everything went white.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.DIALOGUE, npc, "You were with him. What happened?");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.SPEAK, player, "He... saved us. He used the last of his power to teleport us away.");
				Task.addTask(Task.SPEAK, player, "Dragowrath tried to take him. End him. I think it worked.");
				Task.addTask(Task.SPEAK, player, "But he fought it off long enough to protect us.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "No... Merlin...");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.SPEAK, player, "We need to do something. We need to avenge him - and save the world from Dragowrath's grasp.");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.SPEAK, player, "But I don't know what. Your belief wasn't right. My belief wasn't right. Everything is wrong, everything is hopeless.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "All this time, I was so sure I was right.");
				if (p.flag[7][10]) {
					Task.addTask(Task.DIALOGUE, npc, "That faith was the only path forward.");
					Task.addTask(Task.DIALOGUE, npc, "But you made it just as far believing in logic. In your own way. We both did.");
				} else if (p.flag[7][9]) {
					Task.addTask(Task.DIALOGUE, npc, "That logic was the only path forward.");
					Task.addTask(Task.DIALOGUE, npc, "But you made it just as far believing in faith. In your own way. We both did.");
				}
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, npc, "Maybe... maybe it's not about one or the other.");
				Task.addTask(Task.DIALOGUE, npc, "Maybe true strength is in knowing when to believe... and when to think.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "Balance. That's what we were missing.");
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.DIALOGUE, npc, "I thought I had to do this all on my own. That being worthy meant proving myself...");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "But the truth is... I need you. I can't do this without you.");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.SPEAK, player, "...");
				Task.addTask(Task.SPEAK, player, "You're right, Arthra. I need you too. We need each other.");
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "Dragowrath's still out there. And it's stronger than ever.");
				Task.addTask(Task.SLEEP, "", 5);
				Task.addTask(Task.TURN, npc, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "But remember what it said before leaving - \"This world is mine, and the stars will follow.\"");
				Task.addTask(Task.TURN, player, "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, npc, "It's not here anymore. It's in space.");
				Task.addTask(Task.DIALOGUE, npc, "And there's only one space station left with a working launch... Iron Town.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "We have to go. For Merlin. For the world.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Dragowrath's still out there. And it's stronger than ever.");
				Task.addTask(Task.DIALOGUE, npc, "But remember what it said before leaving - \"This world is mine, and the stars will follow.\"");
				Task.addTask(Task.DIALOGUE, npc, "It's not here anymore. It's in space.");
				Task.addTask(Task.DIALOGUE, npc, "And there's only one space station left with a working launch... Iron Town.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, npc, "We have to go. For Merlin. For the world.");
				Task.addTask(Task.DIALOGUE, npc, "Head south from this city. It's a treacherous route, so be prepared.");
				Task.addTask(Task.DIALOGUE, npc, "I'll follow suit soon - I just need a second to prepare myself.");
			}
		});
		
		scriptMap.put(152.0, (npc) -> { // nursery outside
			if (p.flag[7][22]) {
				if (p.nursery == null) {
					Task.addTask(Task.DIALOGUE, npc, "Hey there - I don't believe we've met! Welcome to Xhenos's quaint little Pokemon nursery!");
					Task.addTask(Task.DIALOGUE, npc, "Head inside and talk to my daughter to get started!");
				} else {
					p.nursery.interactOutside(npc);
				}
			} else {
				Entity question = new Entity(gp, "???");
				Task.addTask(Task.DIALOGUE, question, "...");
			}
		});
		
		scriptMap.put(204.0, (npc) -> { // nursery inside
			if (p.flag[7][22]) {
				if (p.nursery == null) {
					Task.addTask(Task.DIALOGUE, npc, "Hi, nice to meet you. Welcome to our Pokemon nursery.");
					p.nursery = new Nursery();
					Task.addTask(Task.DIALOGUE, npc, "We can hold up to 2 Pokemon to keep here and play.");
					Task.addTask(Task.DIALOGUE, npc, "Would you like to deposit any Pokemon?");
					Task.addTask(Task.NURSERY_DEPOSIT, "");
				} else {
					p.nursery.interactInside(npc);
				}
			} else {
				Entity question = new Entity(gp, "???");
				Task.addTask(Task.DIALOGUE, question, "...");
			}
		});
		
		scriptMap.put(152.1, (npc) -> { // alakazam outside shack
			Task.addTask(Task.TEXT, "Alakazam is standing firm. It won't let you pass.");
			Task.addTask(Task.TEXT, "Maybe someone important is inside the shack nearby...");
		});
		
		scriptMap.put(205.0, (npc) -> { // ryder inside shack
			p.flag[7][18] = true;
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SPOT, npc, "");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addNPCMoveTask('x', 30 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.FLASH_IN, "");
			Task.addNPCMoveTask('y', 45 * gp.tileSize, gp.npc[205][1], false, 2592);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.TURN, gp.npc[205][1], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Oh! Uh... finally. Someone not wearing sunglasses and lookin' like a zombie...");
			Task.addTask(Task.SLEEP, "", 5);
			Task.addNPCMoveTask('x', 30 * gp.tileSize, npc, false, 4);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addTask(Task.TURN, gp.npc[205][1], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "I've been holed up here all night. I thought it was just the paparazzi chasin' me again.");
			Task.addTask(Task.DIALOGUE, npc, "My whole team and I had to fight 'em off - 'cept Alakazam. He's been standing guard.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', 43 * gp.tileSize, gp.npc[205][1], false, 4);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[205][1], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[205][1], "You really thought they were chasing you for autographs?!");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[205][1], "Ryder, all of Xhenos is under control. Dragowrath's doing. It's not fans dude, it's... possession.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Wait, what? You're saying the glowing-eyed mob and the sky laser are connected??");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[205][1], "Yes, Ryder. And look, we need you here. There's a space station in Iron Town. It might be the only way to reach Dragowrath.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "...We're going to space now?");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "...Guess it's not the weirdest thing that's happened this week.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.FLASH_IN, "");
			Task.addNPCMoveTask('y', 43 * gp.tileSize, gp.npc[205][2], false, 2688);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[205][2], "Vxxxvhh...");
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "...There he is. Finally. He's been weirdly quiet.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "Well, you guys ready? Let's do this thing. To Iron Town we go!");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(152.2, (npc) -> { // ryder/arthra at iron town entrance
			p.flag[7][19] = true;
			Task.addCameraMoveTask('y', -120, 2);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Ugh... My head... it's like static, but... inside.");
			Task.addTask(Task.DIALOGUE, npc, "I can hear voices that aren't mine. They're... loud. Angry. Calculating.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "Sus, dili ba sila makapakalma? Ang kalibutan na gani ang nakataya diri!");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Ugh, it's messing with my brain! I... I... Ni hindi ko na nga maintindihan ang sarili ko!");
			Task.addTask(Task.DIALOGUE, npc, "Parang pinipiga at dinudurog ang mga iniisip ko laban sa mga pader ng sarili kong bungo...");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addCameraMoveTask('y', 0, 4);
			Task.addNPCMoveTask('y', 57 * gp.tileSize, player, false, 4);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "He's feeling the pull. Dragowrath's possession. Same as the others.");
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "It's starting to dig into his thoughts... rewriting them.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', 59 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.DIALOGUE, npc, "I thought I could resist it. Thought I was strong. But this thing - it doesn't break you... it rewires you.");
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.DIALOGUE, npc, "I can feel it trying to overwrite who I am.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "We need to move - fast. Before it finishes its grip.");
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "Alakazam's the only thing keeping him grounded right now...");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.LEFT);
			Task.addCameraMoveTask('x', 60, 2);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][4], "Vxxxvhh...");
			Task.addTask(Task.SLEEP, "", 40);
			Task.addCameraMoveTask('x', 0, 3);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.UP);
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "...And I don't think that's going to last much longer.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "We need to get him to the space center.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][3], "Now.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.TURN, gp.npc[152][3], "", Task.LEFT);
			Task.addTask(Task.DIALOGUE, gp.npc[152][4], "Vxxxvhh...");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Alakazam... thank you.");
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(152.3, (npc) -> { // ryder/arthra before spaceship
			p.flag[7][20] = true;
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "Agh - it's happening-! I can't stop it... I can't-!");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][7], "Vxxxvhh!");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addParticleTask(gp.npc[152][7], "smoke", new Color(252, 142, 140), 100);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[152][7], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][6], "...Alakazam...?");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[152][6], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[152][6], "...No. Ryder's gone. Alakazam's taken control of his body. He's piloting it like a shell.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[152][6], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "...Systems stable. Manual override engaged. Emotional core suppressed.", 2);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, npc, "I will fly the vessel. It is... the only logical solution.", 2);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[152][6], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][6], "He's not himself - but this might be the only way to get off the planet.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[152][6], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addCameraMoveTask('y', 150, 2);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "Board now. The window for orbital alignment is short. We must depart.", 2);
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = gp.currentMap;
			t.start = player.worldX / gp.tileSize;
			t.finish = player.worldY / gp.tileSize;
			t.color = Color.BLACK;
			Task.addTask(Task.SPACE, "", 432);
			Task.addTask(Task.SPACE, gp.npc[152][6], "...This is really happening.", 200);
			Task.addTask(Task.SPACE, "", 50);
			Task.addTask(Task.SPACE, gp.npc[152][6], "We're in space.", 200);
			Task.addTask(Task.SPACE, "", 25);
			Task.addTask(Task.SPACE, gp.npc[152][6], "Fighting a space demon god thing.", 200);
			Task.addTask(Task.SPACE, "", 75);
			Task.addTask(Task.SPACE, gp.npc[152][6], "This is my destiny. This is me. This is us.", 200);
			Task.addTask(Task.SPACE, "", 100);
			t = Task.addTask(Task.SPACE, npc, "Trajectory stable. Combat probabilities... uncertain.", 200);
			t.finish = 2;
			Task.addTask(Task.SPACE, "", 50);
			Task.addTask(Task.SPACE, gp.npc[152][6], "...Thanks for the vote of confidence, Alakazam.", 150);
			Task.addTask(Task.SPACE, "", 300);
			t = Task.addTask(Task.SPACE, "", 50);
			t.start = 160;
			t.color = Color.BLACK;
			t.wipe = true;
			
			Task.addTask(Task.SLEEP, "", 90);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "We're really here... in orbit. Above everything we've ever known.");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Dragowrath's near. You feel that?");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "He's like a gravitational pull. Warping thoughts. Crushing willpower.");
			Task.addTask(Task.SLEEP, "", 40);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "You and I both tried. Alone, we failed. But maybe together...");
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "The dragons - Faith and Logic - they're not enemies. They're parts of a whole.");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Let them fight together. Use both in tandem. They were born for this.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 75);
			Task.addTask(Task.SPOT, gp.npc[160][0], "");
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Oh - one more thing. Don't bother giving them items. Something about this place... they just vanish.");
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "It's like space rejects them. You'll have to rely on their moves and your skill - as a trainer.");
			Task.addTask(Task.DIALOGUE, gp.npc[160][0], "You got this, <@>. And remember what Merlin taught us: belief only matters when it's paired with action.");
			Pokemon[] tempTeam = p.team;
			p.team = p.tempTeam;
			p.tempTeam = tempTeam;
			int trainerIndex = p.flag[7][14] ? 405 : 404;
			p.team[1] = Trainer.trainers[trainerIndex].getCurrent();
			p.team[0].fainted = false;
			p.team[0].script = true;
			p.team[1].fainted = false;
			p.team[1].script = true;
			p.team[1].setSprites();
			p.team[1].ball = Item.TEMPLE_BALL;
			p.team[1].trainer = p;
			p.heal();
			p.setCurrent();
		});
		
		scriptMap.put(160.0, npc -> { // alakazam move reminder
			Task.addTask(Task.TEXT, "Alakazam seems like it's trying to help your Pokemon remember ancient moves...");
			Task.addTask(Task.PARTY, "Teach # a move?", Task.REMIND);
		});
		
		scriptMap.put(160.1, npc -> { // dragowrath final battle
			if (!p.flag[7][21]) {
				Task.addTask(Task.DIALOGUE, npc, "You claw your way to the heavens... and still think you can defy me?", 1);
				Task.addTask(Task.DIALOGUE, npc, "Fools born of stardust. Worshipping balance. Bound by frail logic and faith.", 1);
				Task.addTask(Task.DIALOGUE, npc, "My children turned from me. And now you wield them as weapons?", 1);
				Task.addTask(Task.DIALOGUE, npc, "Let this cosmos be your grave. Perish along with the rest of your species and planet. Let silence claim your rebellion.", 1);
				Task t = Task.addTask(Task.BATTLE, "", 489);
				t.start = 235;
			} else {
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.DIALOGUE, npc, "So... this is what it means... to lose.", 1);
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.DIALOGUE, npc, "Faith... and Logic... bound together. Not dominance... but unity.", 1);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "...Xhenos resists.", 1);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "...You resist.", 1);
				Task.addTask(Task.SLEEP, "", 120);
				Task.addTask(Task.DIALOGUE, npc, "You have earned back your world.", 1);
				Task.addTask(Task.DIALOGUE, npc, "The broadcast will end.", 1);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addParticleTask(npc, "smoke", new Color(5, 5, 20), 50);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, npc, "My reach... severs.", 1);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "But I remain.", 1);
				Task.addTask(Task.SLEEP, "", 50);
				Task.addTask(Task.DIALOGUE, npc, "One day... you will seek me.", 1);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "A place where stars spiral backwards, where thought folds in on itself.", 1);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, npc, "There... I wait.", 1);
				Task.addTask(Task.SLEEP, "", 20);
				
				p.team[0].fainted = false;
				p.team[1].fainted = false;
				p.heal();
				int id = p.flag[7][14] ? 233 : 234;
				int removeIndex = p.team[0].id == id ? 0 : p.team[1].id == id ? 1 : -1;
				if (removeIndex >= 0) {
					p.team[removeIndex] = null;
					p.shiftTeamForward(removeIndex);
				}
				Pokemon[] tempTeam = p.team;
				p.team = p.tempTeam;
				p.tempTeam = tempTeam;
				p.setCurrent();
				
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
				
				gp.npc[160][0].worldX = 44 * gp.tileSize;
				gp.npc[160][0].worldY = 17 * gp.tileSize;
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addNPCMoveTask('y', 14 * gp.tileSize, player, false, 4);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 50 * gp.tileSize, gp.npc[160][0], false, 4);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
				Task.addNPCMoveTask('y', 16 * gp.tileSize, gp.npc[160][0], false, 4);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 54 * gp.tileSize, gp.npc[160][0], false, 4);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "...Did we really just do that?");
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "I can't believe it. We actually won.");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
				Task.addTask(Task.FLASH_IN, "");
				// ryder
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 52 * gp.tileSize, gp.npc[160][3], false, 720); // 720
				Task.addNPCMoveTask('y', 15 * gp.tileSize, gp.npc[160][3], false, 96); // 96
				// alakazam
				Task.addTask(Task.TURN, gp.npc[160][1], "", Task.RIGHT);
				Task.addNPCMoveTask('x', 52 * gp.tileSize, gp.npc[160][1], false, 672);
				Task.addNPCMoveTask('y', 14 * gp.tileSize, gp.npc[160][1], false, 144);
				Task.addTask(Task.FLASH_OUT, "");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "I feel like I was a USB stick and someone just pulled me out without ejecting...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "But... I'm me again. That voice inside my head - it's gone.");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[160][1], "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.UP);
				Task.addTask(Task.DIALOGUE, gp.npc[160][1], "Vxxxvhh...");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[160][1], "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "...He still talks weird though.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Dragowrath's gone. But... we're still in orbit.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "And last I checked, none of us are certified to pilot a ship back to Earth.");
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "Wait - so we're stuck here? Like... space pirates now?!");
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 20);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "That's kinda based actually.");
				Task.addTask(Task.SLEEP, "", 40);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[160][1], "Vxxxvhh...");
				Task.addTask(Task.SLEEP, "", 35);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "He's right. There's a cave system just south of here. Artificial. Looks... inhabited.");
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.RIGHT);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Let's start there. Someone must know something.");
				Task.addTask(Task.SLEEP, "", 60);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
				Task.addTask(Task.TURN, player, "", Task.LEFT);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "I'll check out the ship. Maybe fix comms. Or at least figure out how to cook with zero gravity.");
				Task.addTask(Task.SLEEP, "", 40);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 25);
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.RIGHT);
				Task.addTask(Task.SLEEP, "", 10);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "...Please don't leave me alone with Alakazam too long.");
				Task.addTask(Task.TURN, gp.npc[160][3], "", Task.UP);
				Task.addTask(Task.TURN, gp.npc[160][1], "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 40);
				Task.addTask(Task.DIALOGUE, gp.npc[160][1], "Vxxxvhh...");
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.LEFT);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.DIALOGUE, gp.npc[160][3], "NOPE. I'm out.");
				Task.addTask(Task.SLEEP, "", 40);
				Task.addTask(Task.TURN, gp.npc[160][0], "", Task.UP);
				Task.addTask(Task.SLEEP, "", 15);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.SLEEP, "", 30);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "I'm going to search the land west of here. You try to head east.");
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "If we split up, we'll cover more ground.");
				Task.addTask(Task.SLEEP, "", 45);
				Task.addTask(Task.DIALOGUE, gp.npc[160][0], "Until we meet again, <@>.");
				Task.addTask(Task.SLEEP, "", 30);
				Task t = Task.addTask(Task.FLAG, "");
				t.start = 7;
				t.finish = 22;
				Task.addTask(Task.FLASH_IN, "");
				Task.addTask(Task.UPDATE, "");
				Task.addTask(Task.FLASH_OUT, "");
			}
		});
		
		scriptMap.put(160.2, (npc) -> { // nova outside gym
			p.flag[7][23] = true;
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addCameraMoveTask('y', -300, 2);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, npc, "...Huh. So you made it down here. Color me surprised.");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, npc, "Most folks wouldn't last two seconds face-to-face with Dragowrath.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "Let alone take him down.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "But here you are, all flesh-and-blood and attitude.");
			Task.addTask(Task.SLEEP, "", 90);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.DIALOGUE, npc, "I was his second-in-command, you know. The voice behind the eye. A loyal spark in the great celestial flame.");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "But now... well, seems the fire's out.");
			Task.addTask(Task.SLEEP, "", 120);
			Task.addTask(Task.DIALOGUE, npc, "And you're stuck. Earth out of reach, friends scattered, and no ride home.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, npc, "Convenient that you found me, huh?");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addNPCMoveTask('x', 67 * gp.tileSize, npc, true, 2);
			Task.addTask(Task.SLEEP, "", 90);
			Task.addTask(Task.DIALOGUE, npc, "I can get you back. But power like that doesn't come free. You want to go back to Earth?");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, npc, "Then prove you're not just some cosmic fluke with a lucky pair of dragons.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addNPCMoveTask('x', 66 * gp.tileSize, npc, true, 1);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.DIALOGUE, npc, "Come inside, <@>. Let's see if you've got more than just storybook luck.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addNPCMoveTask('y', (int) (59 * gp.tileSize + gp.tileSize * 0.75), npc, true, 4);
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addDiagCameraMoveTask(0, 0, 45);
		});
		
		scriptMap.put(160.3, (npc) -> { // everyone going back to earth
			p.flag[8][0] = true;
			Task.addTask(Task.SLEEP, "", 30);
			Task.addDiagCameraMoveTask(-200, 220, 120);
			Task.addTask(Task.SLEEP, "", 90);
			Task.addNPCMoveTask('y', npc.worldY + 4, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 120);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 75);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "Alright, looks like this hunk o' junk might actually fly.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "Nova, that cockpit better have lumbar support.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addNPCMoveTask('y', npc.worldY - 4, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, npc, "It's got thrusters, stabilizers, and a shot at getting us home.");
			Task.addTask(Task.DIALOGUE, npc, "I think we can survive the lack of a seat cushion.");
			Task.addTask(Task.SLEEP, "", 90);
			Task.addDiagCameraMoveTask(0, 0, 100);
			Task.addTask(Task.SLEEP, "", 40);
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.TURN, player, "", Task.DOWN);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addNPCMoveTask('y', 20 * gp.tileSize, gp.npc[160][8], false, 4);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, npc, "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.DOWN);
			Task.addTask(Task.TURN, gp.npc[160][7], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.DIALOGUE, gp.npc[160][8], "There you are!");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addNPCMoveTask('x', 37 * gp.tileSize, player, false, 2);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.RIGHT);
			Task.addNPCMoveTask('x', 38 * gp.tileSize, gp.npc[160][8], false, 4);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.UP);
			Task.addNPCMoveTask('y', 16 * gp.tileSize, gp.npc[160][8], false, 4);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.RIGHT);
			Task.addNPCMoveTask('x', 41 * gp.tileSize, gp.npc[160][8], false, 4);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[160][8], "I've been dodging meteor junk and ghost rocks for ten minutes...");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addNPCMoveTask('y', 16 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addNPCMoveTask('x', 39 * gp.tileSize, player, false, 4);
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.DIALOGUE, gp.npc[160][8], "...What's the status?");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.DIALOGUE, npc, "Ship's boosted. Flight path locked. Was just waiting on you and <@> -");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, npc, "...Hold on.");
			Task.addTask(Task.SLEEP, "", 45);
			Task t = Task.addTask(Task.FLASH_IN, "");
			t.color = new Color(0,0,0,100);
			Task.addTask(Task.SLEEP, "", 80);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.LEFT);
			Task.addTask(Task.TURN, npc, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[160][7], "Vxxxvhh...");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.TURN, gp.npc[160][7], "", Task.UP);
			Task.addTask(Task.SPOT, gp.npc[160][7], "");
			Task.addTask(Task.SHAKE, "", 60);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.UP);
			Task.addTask(Task.SPOT, gp.npc[160][6], "");
			Task.addTask(Task.SPOT, player, "");
			Task.addNPCMoveTask('y', 9 * gp.tileSize, gp.npc[160][9], false, 96);
			Task.addTask(Task.SPOT, gp.npc[160][8], "");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addDiagCameraMoveTask(48, 336, 120);
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.SHAKE, "", 80);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addNPCMoveTask('y', 11 * gp.tileSize, gp.npc[160][9], true, 2);
			Task.addTask(Task.SLEEP, "", 75);
			Task.addNPCMoveTask('y', npc.worldY + 8, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, npc, "...That's not Dragowrath. That's something worse.");
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][8], "What is that then-?! Another of Dragowrath's kind...?");
			Task.addTask(Task.SLEEP, "", 75);
			Task.addTask(Task.DIALOGUE, gp.npc[160][7], "No. Older. Hungrier.", 2);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.SHAKE, "", 90);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addNPCMoveTask('y', npc.worldY + 4, npc, false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, npc, "We're not gonna make it off the ground like this!");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addNPCMoveTask('y', gp.tileSize * 14, gp.npc[160][6], false, 4);
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 75);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addNPCMoveTask('x', gp.tileSize * 39, gp.npc[160][6], false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "...Well. Guess it's time I finally do something smart.");
			Task.addTask(Task.SLEEP, "", 50);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.TURN, gp.npc[160][7], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "You always kept me safe, buddy. This time - I'm keeping you safe.");
			Task.addTask(Task.SLEEP, "", 40);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 25);
			Task.addNPCMoveTask('x', gp.tileSize * 38, gp.npc[160][6], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', gp.tileSize * 14, gp.npc[160][7], false, 4);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][7], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "Hey! Living lightbulb! Over here!");
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][7], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', gp.tileSize * 12, gp.npc[160][9], false, 8);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', gp.npc[160][6].worldY + 4, gp.npc[160][6], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "You want some light to eat? Take mine.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.SHAKE, "", 120);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('x', gp.tileSize * 42, gp.npc[160][8], false, 4);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addNPCMoveTask('y', gp.tileSize * 14, gp.npc[160][8], false, 4);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('x', gp.tileSize * 42, player, false, 4);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addNPCMoveTask('y', gp.tileSize * 15, player, false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 45);
			Task.addTask(Task.DIALOGUE, gp.npc[160][6], "Tell Earth I said hi.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[160][6], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.SHAKE, gp.npc[160][9], "Grrorghuuuagh!", 150);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, npc, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, gp.npc[160][8], "", Task.UP);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', gp.tileSize * 14 - 8, gp.npc[160][8], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addNPCMoveTask('y', gp.tileSize * 1, gp.npc[160][8], false, 616);
			Task.addTask(Task.TURN, player, "", Task.UP);
			Task.addNPCMoveTask('y', gp.tileSize * 14 - 8, player, false, 4);
			
			t = Task.addTask(Task.TELEPORT, "");
			t.counter = gp.currentMap;
			t.start = player.worldX / gp.tileSize;
			t.finish = player.worldY / gp.tileSize;
			t.color = Color.BLACK;
			Task.addTask(Task.SPACE, "", 60);
			t = Task.addTask(Task.SPACE, "", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, "", 120);
			t = Task.addTask(Task.SPACE, "", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, npc, "Brace yourselves! Artificial gravity's fried, and that black hole of a freakshow is NOT giving up!", 120);
			t = Task.addTask(Task.SPACE, "", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, gp.npc[160][8], "Is Ryder-?", 120);
			t = Task.addTask(Task.SPACE, "", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, "", 120);
			t = Task.addTask(Task.SPACE, "", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, npc, "He gave us a shot. I'm not wasting it.", 120);
			t = Task.addTask(Task.SPACE, npc, "Strap in, shut up, and don't puke on my dashboard!", 60);
			t.start = 1;
			t.color = new Color(255, 50, 35, 200);
			Task.addTask(Task.SPACE, "", 600);
			Task.addTask(Task.SPACE, npc, "...Trajectory stable. Earthfall in twenty-seven minutes.", 200);
			Task.addTask(Task.SPACE, "", 75);
			Task.addTask(Task.SPACE, npc, "Assuming no more cosmic nightmares show up.", 150);
			Task.addTask(Task.SPACE, "", 300);
			Task.addTask(Task.SPACE, gp.npc[160][8], "...He saved us.", 180);
			Task.addTask(Task.SPACE, "", 250);
			t = Task.addTask(Task.SPACE, gp.npc[160][7], "He is not gone. Not yet.", 150);
			t.finish = 2;
			Task.addTask(Task.SPACE, "", 300);
			Task.addTask(Task.SPACE, npc, "...One in a billion, that guy.", 240);
			Task.addTask(Task.SPACE, "", 600);
			t = Task.addTask(Task.SPACE, "", 50);
			t.start = 152;
			t.color = Color.BLACK;
			t.wipe = true;
			
			Task.addTask(Task.SLEEP, "", 90);
			Task.addTask(Task.TURN, gp.npc[152][8], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "Alright. Still breathing. No cosmic curses.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "And I didn't even crash.");
			Task.addTask(Task.SLEEP, "", 40);
			Task.addTask(Task.TURN, gp.npc[152][8], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "You're welcome.");
			Task.addTask(Task.SLEEP, "", 120);
			Task.addNPCMoveTask('y', gp.tileSize * 75 + 8, gp.npc[152][9], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "...It's quiet. Like... really quiet. Guess the mind-control fog really is gone.");
			Task.addTask(Task.SLEEP, "", 90);
			Task.addTask(Task.FLASH_IN, "");
			Task.addNPCMoveTask('y', 78 * gp.tileSize, gp.npc[152][11], false, 1008);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "FINN! FINN!! You made it! You really made it back alive!");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "I mean - I knew you were alive, but also? You DEFINITELY exploded.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addNPCMoveTask('y', 75 * gp.tileSize + 8, gp.npc[152][8], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "Robin, we did not explode.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addNPCMoveTask('y', 77 * gp.tileSize, gp.npc[152][11], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "You emotionally exploded! That counts.");
			Task.addTask(Task.SLEEP, "", 20);
			Task.addNPCMoveTask('y', 76 * gp.tileSize, gp.npc[152][11], false, 4);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "Oh, by the way! Your dad gave me this like... two hours after you left for orbit.");
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "Talk about timing, huh?");
			t = Task.addTask(Task.ITEM, "");
			t.item = Item.LETTER_2;
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "He told me to give it to you the second you touched dirt again.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "Also... he said something about a gift. And to bring your cute mint-haired friend along.");
			Task.addTask(Task.SLEEP, "", 10);
			Task.addNPCMoveTask('y', 75 * gp.tileSize + 8, gp.npc[152][9], false, 4);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "Wait. What.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][11], "His words, not mine. Anyway - go see him. He's at the lab, freaking out in a very dadly way.");
			Task.addTask(Task.SLEEP, "", 30);
			t = Task.addTask(Task.FLAG, "");
			t.start = 8;
			t.finish = 1;
			Task.addTask(Task.FLASH_IN, "");
			Task.addNPCMoveTask('y', 99 * gp.tileSize, gp.npc[152][11], false, 1104);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.TURN, gp.npc[152][8], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 5);
			Task.addTask(Task.TURN, gp.npc[152][9], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "...'cute mint-haired friend'? Really? Your dad thinks we're dating, doesn't he.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "You know what? Fine. Let's go prove him wrong. Loudly. With receipts.");
			Task.addTask(Task.SLEEP, "", 25);
			Task.addTask(Task.TURN, player, "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "Yeah, I'm gonna opt out of that awkward family drama. I've got my own chaos to chase.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[152][8], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[152][10], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "Come on, spacetime whisperer. Let's go find out if the atmosphere still likes psychic types.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.DIALOGUE, gp.npc[152][10], "Vxxxvhh...");
			Task.addTask(Task.SLEEP, "", 40);
			Task.addTask(Task.TURN, gp.npc[152][8], "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 20);
			Task.addTask(Task.DIALOGUE, gp.npc[152][8], "Stay weird, <@>.");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.FLASH_IN, "");
			Task.addNPCMoveTask('y', 99 * gp.tileSize, gp.npc[152][8], false, 1144);
			Task.addNPCMoveTask('y', 99 * gp.tileSize, gp.npc[152][10], false, 1152);
			Task.addTask(Task.FLASH_OUT, "");
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.TURN, gp.npc[152][9], "", Task.DOWN);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.TURN, player, "", Task.RIGHT);
			Task.addTask(Task.SLEEP, "", 10);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "Mint-haired. Ugh.");
			Task.addTask(Task.SLEEP, "", 60);
			Task.addTask(Task.TURN, gp.npc[152][9], "", Task.LEFT);
			Task.addTask(Task.SLEEP, "", 15);
			Task.addTask(Task.DIALOGUE, gp.npc[152][9], "Let's go shut your dad down before he starts planning a wedding.");
			Task.addTask(Task.SLEEP, "", 30);
			Task.addTask(Task.FLASH_IN, "");
			Task.addTask(Task.UPDATE, "");
			Task.addTask(Task.FLASH_OUT, "");
		});
		
		scriptMap.put(152.4, (npc) -> { // astronaut going to space
			if (p.getMoney() >= 500) {
				Task t = Task.addTask(Task.CONFIRM, npc, "Pay $500 and go to space?", 13);
				t.ui = Task.MONEY;
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Oh, I'm sorry, you don't have enough money for me to take you!");
			}
		});
		scriptMap.put(152.413, (npc) -> {
			p.setMoney(p.getMoney() - 500);
			Task.addTask(Task.DIALOGUE, npc, "Great! Let's go!");
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = gp.currentMap;
			t.start = player.worldX / gp.tileSize;
			t.finish = player.worldY / gp.tileSize;
			t.color = Color.BLACK;
			Task.addTask(Task.SPACE, npc, "Strap in! We'll be there in no time!", 150);
			Task.addTask(Task.SPACE, "", 120);
			t = Task.addTask(Task.SPACE, "", 50);
			t.start = 160;
			t.color = Color.BLACK;
			t.wipe = true;
		});
		
		scriptMap.put(160.4, (npc) -> { // astronaut going back to earth
			Task.addTask(Task.CONFIRM, npc, "Are you ready to head back to Earth?\n(Warning: Will save the game!)", 14);
		});
		scriptMap.put(160.414, (npc) -> { // confirm
			p.setPosX(67 * gp.tileSize);
        	p.setPosY(75 * gp.tileSize);
        	p.currentMap = 152;
			gp.saveGame(p, false);
			Random random = new Random();
			boolean mystery = random.nextDouble() < 0.05;
			if (!p.flag[8][12] && random.nextDouble() < 0.33) { // 1/3 chance to replay the cutscene
				p.flag[8][11] = false;
			}
			Task.addTask(Task.DIALOGUE, npc, "Okay! I'll start up the ship, let's go!");
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = gp.currentMap;
			t.start = player.worldX / gp.tileSize;
			t.finish = player.worldY / gp.tileSize;
			t.color = Color.BLACK;
			Task.addTask(Task.SPACE, npc, "Strap in tight - we'll be touching down soon!", 150);
			Task.addTask(Task.SPACE, "", 100);
			if (!mystery) {
				if (!p.flag[8][11]) {
					Task.addTask(Task.SPACE, npc, "You know... there's this old rumor among pilots.", 150);
					Task.addTask(Task.SPACE, "", 60);
					Task.addTask(Task.SPACE, npc, "They say sometimes ships don't land at the station at all...", 150);
					Task.addTask(Task.SPACE, "", 60);
					Task.addTask(Task.SPACE, npc, "Instead, they end up at some weird mountain that doesn't exist on any maps. Mystery Peak, they call it.", 150);
					Task.addTask(Task.SPACE, "", 60);
					Task.addTask(Task.SPACE, npc, "Never happened to me, though. Just a spooky story we tell to rookies.", 150);
					Task.addTask(Task.SPACE, "", 120);
					Task.addTask(Task.SPACE, npc, "Anyway - we're back safe on Earth. Enjoy the gravity!", 150);
					Task.addTask(Task.SPACE, "", 60);
				} else {
					Task.addTask(Task.SPACE, "", 100);
					Task.addTask(Task.SPACE, npc, "Alright, we're back on Earth. Enjoy the gravity!", 150);
					Task.addTask(Task.SPACE, "", 60);
				}
				t = Task.addTask(Task.SPACE, "", 50);
				t.start = 152;
				t.color = Color.BLACK;
				t.wipe = true;
			} else {
				t = Task.addTask(Task.SPACE, "", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "...Wait. Instruments are going haywire!", 60);
				t = Task.addTask(Task.SPACE, npc, "...Wait. Instruments are going haywire!", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "", 60);
				t = Task.addTask(Task.SPACE, "", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "This... this isn't right. The navigation system's locked onto something!", 60);
				t = Task.addTask(Task.SPACE, npc, "This... this isn't right. The navigation system's locked onto something!", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "", 60);
				t = Task.addTask(Task.SPACE, "", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				if (!p.flag[8][12]) {
					Task.addTask(Task.SPACE, npc, "No way... Mystery Peak?! That was supposed to just be a legend!", 60);
					t = Task.addTask(Task.SPACE, npc, "No way... Mystery Peak?! That was supposed to just be a legend!", 60);
					t.start = 1;
					t.color = new Color(255, 50, 35, 200);
					
					Task.addTask(Task.SPACE, npc, "", 60);
					t = Task.addTask(Task.SPACE, "", 60);
					t.start = 1;
					t.color = new Color(255, 50, 35, 200);
				}
				
				Task.addTask(Task.SPACE, npc, "Hold on - we're not heading back to the station. Something's dragging us in!!", 60);
				t = Task.addTask(Task.SPACE, npc, "Hold on - we're not heading back to the station. Something's dragging us in!!", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "", 60);
				t = Task.addTask(Task.SPACE, "", 60);
				t.start = 1;
				t.color = new Color(255, 50, 35, 200);
				
				Task.addTask(Task.SPACE, npc, "", 60);
				t = Task.addTask(Task.SPACE, "", 50);
				t.start = 208;
				t.color = Color.BLACK;
				t.wipe = true;
				p.flag[8][12] = true;
			}
			p.flag[8][11] = true;
		});
		
		scriptMap.put(208.0, (npc) -> { // astronaut on mystery peak
			Task.addTask(Task.CONFIRM, npc, "It's so strange here... ready to head home?", 15);
		});
		scriptMap.put(208.015, (npc) -> {
			Task t = Task.addTask(Task.TELEPORT, "");
			t.counter = gp.currentMap;
			t.start = player.worldX / gp.tileSize;
			t.finish = player.worldY / gp.tileSize;
			t.color = Color.BLACK;
			Task.addTask(Task.SPACE, npc, "Alright then, buckle in. The stars may have shown you what's possible, but the ground is where you'll stand tall.", 150);
			Task.addTask(Task.SPACE, "", 60);
			Task.addTask(Task.SPACE, npc, "Trajectory locked, next stop: home finally!", 150);
			Task.addTask(Task.SPACE, "", 60);
			
			t = Task.addTask(Task.SPACE, "", 50);
			t.start = 152;
			t.color = Color.BLACK;
			t.wipe = true;
		});
		
		scriptMap.put(215.1, (npc) -> { // scott 5
			Task.addTask(Task.DIALOGUE, npc, "Here, we breed and house rare Pokemon to fight against their extinction.");
			Task.addTask(Task.DIALOGUE, npc, "...What's that? You have a " + Pokemon.getName(((p.starter + 1) * 3) - 2) + "?? That's insanely rare. Did you get that from the professor?");
			Task.addTask(Task.DIALOGUE, npc, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			
			Random rand = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int secondStarter = -1;
			do {
				secondStarter = rand.nextInt(3);
			} while (secondStarter == p.starter);
			p.secondStarter = secondStarter;
			
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task t = Task.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		});
		
		scriptMap.put(215.2, (npc) -> { // fred 5
			Task.addTask(Task.DIALOGUE, npc, "...You win. Again.");
			Task.addTask(Task.DIALOGUE, npc, "Funny thing is... this loss doesn't bother me. Not even a little.");
			Task.addTask(Task.DIALOGUE, npc, "Oh, you're his son, and you're helping him research? Well, in that case, take this one as well. This should help your guys' study!");
			p.flag[0][6] = true;
			Item[] items = new Item[] {Item.MIRACLE_SEED, Item.CHARCOAL, Item.MYSTIC_WATER};
			
			Random rand = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int secondStarter = -1;
			do {
				secondStarter = rand.nextInt(3);
			} while (secondStarter == p.starter);
			p.secondStarter = secondStarter;
			
			Pokemon result = new Pokemon(((p.secondStarter + 1) * 3) - 2, 5, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task t = Task.addTask(Task.GIFT, "", result);
			t.item = result.item = items[p.secondStarter];
		});
	}
	
	public int getUnregisteredBasePokemon(Random random) {
		int id = 0;
		int counter = 0;
		do {
			id = Pokemon.getRandomBasePokemon(random);
			counter++;
		} while (p.isDupes(id) && counter < 100);
		
		return id;
	}
	
	public void runScript(Entity npc) {
		runScript(npc, npc.scriptIndex);
	}

	public void runScript(Entity npc, double key) {
		boolean error = false;
		if (key < 0) return;
		if (!error && scriptMap.containsKey(key)) {
			scriptMap.get(key).accept(npc);
		} else {
			error = true;
		}
		if (error) {
			System.err.println("An error occured: NPC's scriptKey was " + key + ", and scriptMap doesn't contain that key.");
		}
	}
}
