package overworld;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;

import entity.*;
import pokemon.*;

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
				Task.addTask(Task.TEXT, "You got the Map!");
				Task.addTask(Task.DIALOGUE, npc, "And as your Professor, I need your help for collecting as much data about the Pokemon inhabiting our world with us!");
				Task.addTask(Task.DIALOGUE, npc, "This little doohickey is the Neodex! In a region as unique as ours, I've had to make plenty of modifications to account for them.");
				Task.addTask(Task.DIALOGUE, npc, "It's one of my finest inventions yet, and I even got help from Professor Oak, the greatest professor of all time!");
				Task.addTask(Task.DIALOGUE, npc, "Instead of Rotom, it taps into a shared database that allows for identifying new forms for old Pokemon. Give it a whirl!");
				Task.addTask(Task.TEXT, "You got the Pokedex!");
				Task.addTask(Task.DIALOGUE, npc, "Oh right! Speaking of collecting data, I made a little something to help you find as many different species as you can.");
				Task t = Task.addTask(Task.ITEM, "");
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
				Task.addTask(Task.DIALOGUE, npc, "It provides accurate data during a battle, just press 'A' in a battle, or 'Ctrl + A' outside, and you can check how much damage a move can do!");
				t = Task.addTask(Task.ITEM, "");
				t.item = Item.CALCULATOR;
				Task.addTask(Task.DIALOGUE, npc, "Now go out there and make me proud - and most importantly...");
				Task.addTask(Task.DIALOGUE, npc, "COLLECT THAT DATA!");
				Task.addTask(Task.UPDATE, "");
			} else if (p.flag[0][5] && !p.flag[0][21]) {
				Task.addTask(Task.DIALOGUE, npc, "Have you seen any new Shadow forms? Can I take a look?");
				Pokemon[] sDex = p.getDexType(1);
				int amt = 0;
				for (Pokemon po : sDex) {
					if (p.pokedex[po.id] == 2) amt++;
				}
				if (!p.flag[0][20] && amt >= 1) {
					String plural = amt > 1 ? "s!" : "!";
					Task.addTask(Task.DIALOGUE, npc, "Oh nice! You've seen " + amt + " form" + plural);
					Task.addTask(Task.DIALOGUE, npc, "Here son, take this for helping me out! You'd get better use out of it than me, anyways!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.AMULET_COIN;
					p.flag[0][20] = true;
				}
				if (amt >= Pokemon.POKEDEX_METEOR_SIZE) {
					Task.addTask(Task.DIALOGUE, npc, "Finn!! You did it! You finished the Shadow Pokedex I gave you!");
					Task.addTask(Task.DIALOGUE, npc, "This item is extremely rare, so please, use it wisely!");
					Task t = Task.addTask(Task.ITEM, "");
					t.item = Item.MASTER_BALL;
					p.flag[0][21] = true;
				}
			} else {
				Task.addTask(Task.DIALOGUE, npc, "How's it going?");
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
			Task.addTask(Task.DIALOGUE, npc, "Please do make haste, I do hope he's okay.");
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
				Task.addTask(Task.DIALOGUE, npc, "What..? You've never heard of me? I have the most famous restaurant in Xhenos!");
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
			Task.addTask(Task.CONFIRM, "If you have 2 Euphorian Gems to give me, I'll give you this Shell Bell. Do we have a deal?", 6);
		});
		
		scriptMap.put(11.0, (npc) -> { // fred 1
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
				Task.addTask(Task.SLEEP, npc, "", 60);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.TURN, gp.npc[11][9], "", Task.UP);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, npc, "", 60);
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.SPOT, npc, "");
				Task.addTask(Task.TURN, npc, "", Task.DOWN);
				Task.addNPCMoveTask('y', 66 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, player, "", Task.DOWN);
				Task.addTask(Task.TURN, npc, "", Task.RIGHT);
				Task.addNPCMoveTask('x', 39 * gp.tileSize, npc, false, 4);
				Task.addTask(Task.TURN, npc, "", Task.UP);
				Task.addTask(Task.SLEEP, npc, "", 30);
				Task.addTask(Task.INTERACTIVE, gp.iTile[11][0], "", 0);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addNPCMoveTask('y', 65 * gp.tileSize, npc, false, 2);
				Task.addTask(Task.SLEEP, npc, "", 15);
				Task.addTask(Task.DIALOGUE, npc, "Heh, you must be pretty tough to make it this far, but don't get too full of yourself.");
				Task.addTask(Task.DIALOGUE, npc, "I'll gladly put an end to your winning streak right here.");
			} else {
				Task.addTask(Task.DIALOGUE, npc, "Back for more, huh? Go back home, bud.");
			}
			Task.addTask(Task.DIALOGUE, npc, "You're just another weak trainer in my way.");
			Task.addTask(Task.BATTLE, "", 34);
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
				} while (p.pokedex[id] == 2 && counter < 100);
				Pokemon result = new Pokemon(id, 20, true, false);
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
					Task.addTask(Task.DIALOGUE, npc, "Wow! You've already seen " + amt + " form" + plural);
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
				Task.addTask(Task.DIALOGUE, npc, "How's life, Finn? You see your father recently?");
			}
		});
		
		scriptMap.put(32.0, (npc) -> { // fisherman
			p.flag[1][17] = true;
			p.fish = true;
			Task.addTask(Task.DIALOGUE, npc, "Say, you look like you'd be great at fishing. Here, take this spare I got lying around. Maybe you'll fish up a Durfish!");
			Task t = Task.addTask(Task.ITEM, "");
			t.item = Item.FISHING_ROD;
			Task.addTask(Task.DIALOGUE, npc, "Look at water and press 'A', or use the item in your bag to fish!");
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
				id = gift.nextInt(8); // Dualmoose, Sparkdust, Posho, Kissyfishy, Minishoo, Tinkie, Bronzor-X, Bluebunn
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
			} while (p.pokedex[id] == 2 && counter < 100);
			Pokemon result = new Pokemon(id, 15, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
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
			Task.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?", 2);
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
					p.flag[2][11] = true;
					Task.addTask(Task.TEXT, "You used the wire cutters to set it free!");
					Task.addTask(Task.FLASH_IN, "");
					Task.addTask(Task.UPDATE, "");
					Task.addTask(Task.FLASH_OUT, "");
					Task.addTask(Task.DIALOGUE, npc, "Bzzz....Zzzzttt..... ZUZUZUURRKIII!!!");
					Task.addTask(Task.DIALOGUE, npc, "(The mysterious creature seems shell-shocked, and is now lashing out at everything around it!)");
					Task t = Task.addTask(Task.BATTLE, "", 387);
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
			} while (p.pokedex[id] == 2 && counter < 50);
			Pokemon result = new Pokemon(id, 25, true, false);
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
			if (p.pokedex[id] == 2) {
				Task.addTask(Task.DIALOGUE, npc, "Wait..... you have that one?!?!? Shit. Well, take this one instead bozo.");
				boolean sparkitten = gift.nextBoolean();
				if (sparkitten) {
					id = 108;
				} else {
					id = 190;
				}
			}
			Pokemon result = new Pokemon(id, 30, true, false);
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
			t = Task.addTask(Task.GIFT, "", p);
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
		
		scriptMap.put(91.0, (npc) -> {
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
				Task.addTask(Task.TURN, player, "", Task.UP);
				Task.addTask(Task.DIALOGUE, npc, "Thank you Finn, I don't know what I would've done without you.");
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
			Task.addTask(Task.PARTY, "");
		});
		
		scriptMap.put(94.0, (npc) -> { // gift e/s
			p.flag[4][4] = true;
			Task.addTask(Task.TEXT, "One makes Pokemon surge with electricity, and another casts them in a strange shadow.");
			Task.addTask(Task.TEXT, "Here's a gift of one of the Pokemon affected!");
			int[] ids = new int[] {197, 199, 202, 205, 209, 215, 217, 220, 223, 226};
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int index = -1;
			int counter = 0;
			do {
				counter++;
				index = gift.nextInt(ids.length);
			} while (p.pokedex[ids[index]] == 2 && counter < 100);
			
			Pokemon result = new Pokemon(ids[index], 30, true, false);
			Task.addTask(Task.TEXT, "You recieved " + result.name() + "!");
			Task.addTask(Task.GIFT, "", result);
		});
		
		scriptMap.put(107.0, (npc) -> {
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

		scriptMap.put(107.1, (npc) -> {
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
				
				Task.addCameraMoveTask('y', -400, 2);
				
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
			Task.addTask(Task.TEXT, "Here, could you raise it for me?");
			int[] ids = new int[] {177, 179, 98};
			Random gift = new Random(gp.aSetter.generateSeed(p.getID(), npc.worldX / gp.tileSize, npc.worldY / gp.tileSize, gp.currentMap));
			int index = gift.nextInt(ids.length - 1);
			if (p.pokedex[ids[0]] == 2 || p.pokedex[ids[1]] == 2) {
				index = 2;
			}
			
			Egg result = new Egg(ids[index], 3);
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
			Task.addTask(Task.DIALOGUE, npc, "I don't know, Finn! It's going to be very dangerous. You'll need to go South through Gelb Forest to Rawwar City.");
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
			Task.addTask(Task.DIALOGUE, npc, "Things are much worse than we realize. Please, Finn... I can't lose another friend to Eclipse.");
			Task.addTask(Task.SPEAK, player, "Don't worry, Scott. I'll stop them. You just stay safe.");
			Task.addTask(Task.DIALOGUE, npc, "Thanks, Finn... I know you can do it. Good luck.");
			Task.addTask(Task.DIALOGUE, npc, "Now, go and stop them before it's too late.");
			p.flag[6][0] = true;
		});
		
		scriptMap.put(118.0, (npc) -> { // fossil
			Task.addTask(Task.DIALOGUE, npc, "Do you have any fossils for me to resurrect?");
			Task.addTask(Task.FOSSIL, "Do you have any fossils for me to resurrect?");
		});

		scriptMap.put(127.0, (npc) -> { // blackjack
			Task t = Task.addTask(Task.CONFIRM, "Would you like to play Blackjack?\n(Warning: Will Auto-Save)");
			t.counter = 3;
		});
		
		scriptMap.put(127.1, (npc) -> { // battle bet
			if (p.coins > 0) {
				Task t = Task.addTask(Task.CONFIRM, "Would you like to Battle Bet?\n(Warning: Will Auto-Save)");
				t.counter = 4;
			} else {
				Task.addTask(Task.TEXT, "I'm sorry, you don't have any coins to bet with! Come back later!");
			}
		});
		
		scriptMap.put(129.0, (npc) -> {
			if (!p.flag[6][1]) {
				Task.addTask(Task.DIALOGUE, npc, "Oh, you haven't gotten any coins yet?");
				Task.addTask(Task.DIALOGUE, npc, "Here, it's on the house!");
				Task.addTask(Task.DIALOGUE, npc, "You received 10 Coins!");
				Task.addTask(Task.DIALOGUE, npc, "Yes, now don't go spend them all in one place. Or do! I don't care!");
				Task.addTask(Task.DIALOGUE, npc, "But come back when you get more badges. Perhaps I'll have a reward for you!");
				p.coins += 10;
				p.flag[6][1] = true;
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
						"4 badges, huh?  You're halfway through the gyms, and that’s no easy feat. Here's a little something for your troubles.",
						"Bested Millie's own mom too? You took down that family no trouble! Pass me your case!",
						"What's that? You beat both Maxwell and Rayna? Wow, I really underestimated you! Here!",
						"Defeated our resident gambler Merlin? Heck yeah, I never trusted him anyways, heard he always cheated at Blackjack.",
						"Wow, all 8 badges! Can't believe you struck down the gyms! I got just the reward for a champion in the making like youse!"
					};
					for (int i = 0; i < p.badges; i++) {
						if (!p.coinBadges[i]) {
							Task.addTask(Task.DIALOGUE, npc, messages[i]);
							int coins = i > 4 ? 25 : i > 2 ? 20 : i > 0 ? 10 : 5;
							Task.addTask(Task.TEXT, "You received " + coins + " coins!");
							p.coinBadges[i] = true;
							p.coins += coins;
						}
					}
				} else {
					if (p.coins > 0) {
						Task t = Task.addTask(Task.GAME_STATE, "");
						t.counter = GamePanel.COIN_STATE;
					} else {
						Task.addTask(Task.DIALOGUE, npc, "Looks like your stash just ran dry, so I can't exchange air for anything. Come back when you're... a little richer.");
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
				if (p != null && p.type1 != PType.FIRE && p.type2 != PType.FIRE) {
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
				Egg egg = new Egg(id, 3);
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
			Task t = Task.addTask(Task.ITEM, "Obtained HM07 Rock Climb!");
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

		scriptMap.put(146.0, (npc) -> { // TODO splinkty gauntlet
			int selected = p.getAmountSelected();
			String message = "Are you ready to fight as soon as you step into this room?";
			if (selected < Player.GAUNTLET_BOX_SIZE) { // Not enough selected
				message = "You don't have 10 Pokemon selected to bring! You'll be at a huge disadvantage!\nYou can choose what Pokemon to bring using the Gauntlet Box in this PC (press [\u2191] when selecting a box).\n" + message;
			}
			for (String s : message.split("\n")) {
				Task.addTask(Task.TEXT, s);
			}
			Task.addTask(Task.CONFIRM, "There won't be any leaving until it's clear! Are you SURE you're ready?", 0);
		});
	}
	
	public int getUnregisteredBasePokemon(Random random) {
		int id = 0;
		int counter = 0;
		do {
			id = Pokemon.getRandomBasePokemon(random);
			counter++;
		} while (p.pokedex[id] == 2 && counter < 100);
		
		return id;
	}

	public void runScript(Entity npc) {
		double key = npc.scriptIndex;
		boolean error = false;
		if (key < 0) error = true;
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
