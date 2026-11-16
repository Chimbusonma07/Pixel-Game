package main;

import OBJ.OBJKey;

public class EventHandler {
	GamePanel gp;
	EventRectangle eventRect[][];
	
	int previousEventX, previousEventY;
	boolean touchEvent = false;
	public boolean doorHintShown = false; // the "find the wizard" reminder near the maze door, once per maze visit

	public EventHandler(GamePanel gp) {
		this.gp = gp;	
		eventRect = new EventRectangle[gp.MAX_WORLD_COL][gp.MAX_WORLD_ROW];//sets an event rectangle on every single tile on the map
		
		int col = 0;
		int row = 0;
		
		while (col < gp.MAX_WORLD_COL && row < gp.MAX_WORLD_ROW) {
			
			eventRect[col][row] = new EventRectangle();
			eventRect[col][row].x = 23;
			eventRect[col][row].y = 23;
			eventRect[col][row].width = 2;
			eventRect[col][row].height = 2;
			eventRect[col][row].eventRectDefaultX = eventRect[col][row].x;
			eventRect[col][row].eventRectDefaultY = eventRect[col][row].y;
			
			col ++;
			
			if (col == gp.MAX_WORLD_COL) {
				col = 0;
				row ++;
			}
		}
	}
	
	public void checkEvent() {
		
		//Check if the player is more than a tile away from the previous event
		int xDistance = Math.abs(previousEventX - gp.player.worldX);
		int yDistance = Math.abs(previousEventY - gp.player.worldY);
		int distance = Math.max(xDistance, yDistance);
		
		if (distance > gp.tileSize) {
			touchEvent = true;
		}
		
		if (touchEvent == true && gp.currentMap == 0) { // WorldVer2 teleports only
			if(checkHit (27, 17, "right") == true) {
				teleportTile(27, 17, gp.dialogueState);
			}
			
			if (gp.player.level > 1) {
				if(checkHit (33, 7, "left") == true) {
					secondTeleportTile(33, 7, gp.dialogueState);
				}
			}
		}

		// MAZE: reminder bubble with the door picture when the player gets close to the door
		if (gp.currentMap == 1 && doorHintShown == false && gp.player.haveChestKey == false) {
			if (tilesAway(AssetSetter.MAZE_DOOR_COL * gp.tileSize, AssetSetter.MAZE_DOOR_ROW * gp.tileSize) <= 1.5) { // 1.5 so it can't fire through a wall
				doorHintShown = true;
				gp.user.showDialogue("Find the wizard! He is waiting behind this door.", gp.user.doorImage);
			}
		}

		// HOUSE: walking up to the wizard gets you the key to the second chest
		if (gp.currentMap == 2 && gp.player.haveChestKey == false && gp.npc[0] != null) {
			if (tilesAway(gp.npc[0].worldX, gp.npc[0].worldY) <= 1.5) {
				giveChestKey();
			}
		}
	}

	// Straight-line distance from the player to a world position, in tiles.
	private double tilesAway(int worldX, int worldY) {
		double dx = gp.player.worldX - worldX;
		double dy = gp.player.worldY - worldY;
		return Math.sqrt(dx*dx + dy*dy) / gp.tileSize;
	}

	public void giveChestKey() {
		gp.player.haveChestKey = true;

		OBJKey chestKey = new OBJKey(gp);
		chestKey.name = "Chest Key";
		chestKey.description = "[Chest Key]\nOpens the chest\ndeep in the maze";
		if (gp.player.inventory.size() < gp.player.maxInventorySize) {
			gp.player.inventory.add(chestKey);
		}

		gp.npc[0].direction = "down"; // face the player coming up the carpet
		gp.user.addMessage("Chest Key obtained!!");
		gp.user.showDialogue("Here is the key to the second chest.", chestKey.down1);
	}
	
	public boolean checkHit(int eventCol, int eventRow, String reqDirection) {
		//reqDirection refers to the required direction the player must face when it interacts with the event rectangle
		boolean hit = false;
		
		//FIND PLAYER POSITION
		gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
		gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
		
		//FIND EVENT POSITION
		eventRect[eventCol][eventRow].x = eventCol * gp.tileSize + eventRect[eventCol][eventRow].x;
		eventRect[eventCol][eventRow].y = eventRow *  gp.tileSize + eventRect[eventCol][eventRow].y;
		
		if(gp.player.solidArea.intersects(eventRect[eventCol][eventRow])) {
			if(gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
				hit = true;
				
				previousEventX = gp.player.worldX;
				previousEventY = gp.player.worldY;
			}
		}
		
		gp.player.solidArea.x = gp.player.solidAreaDefaultX;
		gp.player.solidArea.y = gp.player.solidAreaDefaultY;
		eventRect[eventCol][eventRow].x = eventRect[eventCol][eventRow].eventRectDefaultX;
		eventRect[eventCol][eventRow].y = eventRect[eventCol][eventRow].eventRectDefaultY;
		
		return hit;
	}
	
	public void teleportTile(int col, int row, int gameState) {
		gp.gameState = gameState;
		gp.user.currentDialogue = "Teleport!!!";
		gp.player.worldX = 35 * gp.tileSize;
		gp.player.worldY = 10 * gp.tileSize;
		touchEvent = false;
	}
	
	public void secondTeleportTile(int col, int row, int gameState) {
		gp.gameState = gameState;
		gp.user.currentDialogue = "Teleport!!!";
		gp.player.worldX = 23 * gp.tileSize;
		gp.player.worldY = 17 * gp.tileSize;
		touchEvent = false;
	}
	
//	public void damagePit (int col, int row,int gameState) {
//		gp.gameState = gameState;
//		gp.user.currentDialogue = "You fell into a pit";
//		gp.player.life -= 1;
//		touchEvent = false;
//	}
//	
//	public void healingPool (int col, int row,int gameState) {
//		if (gp.key.enterPressed == true) {
//			gp.gameState = gameState;
//			gp.user.currentDialogue = "You've reached the healing pool!";
//			gp.player.life = gp.player.maxLife;
//		}
//		
//	}
}
