package main;

import Monsters.Beetle;
import Monsters.Blue;
import Monsters.Monster;
import Monsters.Ogre;
import OBJ.OBJChest;
import OBJ.OBJDoor;
import OBJ.OBJHealthCoin;
import OBJ.OBJKey;
import entity.Entity;
import entity.WizardNPC;

public class AssetSetter {
	
	GamePanel gp;

	public AssetSetter(GamePanel gp){
		this.gp = gp;
	}
	
	public void setObjects() {
		gp.obj[1] = new OBJKey(gp);
		gp.obj[1].worldX = gp.tileSize * 40;
		gp.obj[1].worldY = gp.tileSize * 10;

		setChest();
		// (no health coins here: they only exist in the maze)
	}

	// ---------------- HEALTH COINS ----------------

	// Coins already picked up ("map:col:row"), so they don't come back when a map reloads.
	// Cleared on RESTART.
	public java.util.Set<String> collectedCoins = new java.util.HashSet<>();
	static final int FIRST_COIN_SLOT = 5; // obj[5..] hold coins; lower slots are keys/chests/doors

	void placeCoins(int map, int[][] spots) {
		int slot = FIRST_COIN_SLOT;
		for (int[] s : spots) {
			if (collectedCoins.contains(map + ":" + s[0] + ":" + s[1])) {
				continue;
			}
			gp.obj[slot] = new OBJHealthCoin(gp);
			gp.obj[slot].worldX = gp.tileSize * s[0];
			gp.obj[slot].worldY = gp.tileSize * s[1];
			slot++;
		}
	}

	public void coinCollected(Entity coin) {
		collectedCoins.add(gp.currentMap + ":" + coin.worldX / gp.tileSize + ":" + coin.worldY / gp.tileSize);
	}
	
	public void setChest() {
		gp.obj[2] = new OBJChest(gp);
		gp.obj[2].worldX = gp.tileSize * 10;
		gp.obj[2].worldY = gp.tileSize * 8;
	}
	
	public void setNPC() {
		
		gp.npc[0] = new WizardNPC(gp);		
		gp.npc[0].worldX = gp.tileSize * 21;
		gp.npc[0].worldY = gp.tileSize * 21;
		
	}
	
	// ---------------- MAZE (dungeon.txt) ----------------

	// Door: the farthest dead end from the maze centre (top of the maze).
	public static final int MAZE_DOOR_COL = 22, MAZE_DOOR_ROW = 8;
	// Tile right next to the door, where the player reappears when leaving the house.
	public static final int MAZE_DOOR_EXIT_COL = 21, MAZE_DOOR_EXIT_ROW = 8;

	public void setMazeObjects() {
		gp.obj[0] = new OBJDoor(gp);
		gp.obj[0].worldX = gp.tileSize * MAZE_DOOR_COL;
		gp.obj[0].worldY = gp.tileSize * MAZE_DOOR_ROW;

		// Grand Treasure: dead end in the opposite (bottom-right) corner, guarded by ogres.
		gp.obj[1] = new OBJChest(gp);
		gp.obj[1].name = "Grand Treasure";
		gp.obj[1].down2 = gp.obj[1].down1; // make it visible (no chest tile underneath here)
		gp.obj[1].collision = true;
		gp.obj[1].worldX = gp.tileSize * 38;
		gp.obj[1].worldY = gp.tileSize * 38;

		// Health coins: each hidden at the end of a blind corridor (3+ tiles, no side branches),
		// with a guard posted 2 tiles back in that corridor (see setMazeMonsters).
		placeCoins(1, MAZE_COINS);
	}

	// {col, row} of each coin, and {kind, col, row} of the monster guarding it (same order).
	static final int[][] MAZE_COINS = {
		{8, 8},     // far north-west corner
		{12, 16},   // west, off the long route to the door
		{18, 20},   // middle-west
		{14, 34},   // south-west
		{34, 28},   // east
		{30, 10},   // north-east
	};
	static final int[][] COIN_GUARDS = {
		{2, 10, 8},   // Ogre
		{1, 12, 18},  // Blue - spits straight down the corridor at you
		{2, 16, 20},  // Ogre
		{0, 14, 36},  // Beetle
		{1, 36, 28},  // Blue
		{0, 30, 12},  // Beetle
	};

	public void setMazeMonsters() {
		// {kind, col, row}  kind: 0 = Beetle, 1 = Blue, 2 = Ogre
		int[][] spawns = {
			{2, 16, 8}, {2, 14, 10},          // ogres guarding the way to the door
			{2, 39, 40}, {2, 40, 36},         // ogres guarding the Grand Treasure
			{1, 8, 40}, {0, 38, 12}, {1, 25, 38}, {0, 24, 12}, {1, 31, 18}, {0, 40, 20}, // wanderers
		};
		int i = 0;
		for (int[] s : spawns) {
			gp.monsters[i++] = makeMonster(s);
		}
		// coin guards stay at their post (walk back if lured away), like the ogres
		for (int[] s : COIN_GUARDS) {
			Monster guard = makeMonster(s);
			guard.guardPost();
			gp.monsters[i++] = guard;
		}
	}

	private Monster makeMonster(int[] s) {
		Monster m;
		switch (s[0]) {
		case 0:  m = new Beetle(gp); toughen(m, 6, 2, 3); break;
		case 1:  m = new Blue(gp);   toughen(m, 8, 2, 4); break;
		default: m = new Ogre(gp);   toughen(m, 12, 3, 6); break;
		}
		m.worldX = gp.tileSize * s[1];
		m.worldY = gp.tileSize * s[2];
		return m;
	}

	// Maze monsters are stronger than WorldVer2's and give more XP so Level 6 is reachable.
	private void toughen(Entity m, int life, int attack, int exp) {
		m.maxLife = life;
		m.life = life;
		m.attack = attack;
		m.exp = exp;
	}

	// ---------------- WIZARD'S HOUSE (World01.txt) ----------------

	public static final int HOUSE_DOOR_COL = 24, HOUSE_DOOR_ROW = 31;

	public void setHouse() {
		gp.obj[0] = new OBJDoor(gp);
		gp.obj[0].worldX = gp.tileSize * HOUSE_DOOR_COL;
		gp.obj[0].worldY = gp.tileSize * HOUSE_DOOR_ROW;

		WizardNPC wizard = new WizardNPC(gp);
		wizard.setHouseMode();
		wizard.worldX = gp.tileSize * 24;
		wizard.worldY = gp.tileSize * 21;
		gp.npc[0] = wizard;

	}

	// ---------------- WORLDVER2 ----------------

	public void setMonsters() {
		
		gp.monsters[0] = new Beetle(gp);	
		gp.monsters[0].worldX = gp.tileSize * 38;
		gp.monsters[0].worldY = gp.tileSize * 11;
		
		gp.monsters[1] = new Beetle(gp);	
		gp.monsters[1].worldX = gp.tileSize * 37;
		gp.monsters[1].worldY = gp.tileSize * 12;
		
		gp.monsters[2] = new Beetle(gp);	
		gp.monsters[2].worldX = gp.tileSize * 40;
		gp.monsters[2].worldY = gp.tileSize * 12;
		
		gp.monsters[3] = new Blue(gp);	
		gp.monsters[3].worldX = gp.tileSize * 10;
		gp.monsters[3].worldY = gp.tileSize * 38;
		
		gp.monsters[4] = new Ogre(gp);	
		gp.monsters[4].worldX = gp.tileSize * 23;
		gp.monsters[4].worldY = gp.tileSize * 40;
		
		gp.monsters[5] = new Ogre(gp);	
		gp.monsters[5].worldX = gp.tileSize * 23;
		gp.monsters[5].worldY = gp.tileSize * 38;
		
		gp.monsters[6] = new Blue(gp);	
		gp.monsters[6].worldX = gp.tileSize * 18;
		gp.monsters[6].worldY = gp.tileSize * 31;
		
		gp.monsters[7] = new Blue(gp);	
		gp.monsters[7].worldX = gp.tileSize * 12;
		gp.monsters[7].worldY = gp.tileSize * 33;
	}
}
