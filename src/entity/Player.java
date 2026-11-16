package entity;
import main.GamePanel;
import main.KeyHandler;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import OBJ.OBJHealthCoin;
import OBJ.OBJ_FireBall;
import OBJ.OBJ_Sword;

public class Player extends Entity{
    KeyHandler key;
    public final int screenX;
    public final int screenY;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;
    public boolean haveKey = false;
    public boolean haveChestKey = false; // the wizard's key to the Grand Treasure in the maze
    public int healthCoins = 0;          // stacked in a single inventory slot
    int count = 0;

    public Player(GamePanel gp, KeyHandler key){
    	super(gp);
        this.key = key;
        
        screenX = gp.maxScreenWidth/2 - (gp.tileSize/2);
        screenY = gp.maxScreenHeight/2 - (gp.tileSize/2);
         
        solidArea = new Rectangle();
        solidArea.x = 9;
        solidArea.y = 16;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 30;
        solidArea.height = 32;
        attackArea.width = 36;
        attackArea.height = 36;
        direction = "down";

        SetDefaultValues();
        getPlayerImage();
        getPlayerAttackImage();
        setInventory();
    }

    public void SetDefaultValues(){
        worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        speed = 4;
        //PLAYER LIFE
        maxLife = 6;
        life = maxLife;
        //PLAYER ATTRIBUTES
        level = 1;
    	strength = 1;
    	exp = 0;
    	nextLevelExp = 3;
    	projectile = new OBJ_FireBall(gp);
    	currentWeapon = new OBJ_Sword(gp);
    	attack = getAttack(); //Attack is determined by strength and the sword's attack value
    	defense = 1; //Defense is determined by the dexterity and the shield's defense value
    	haveKey = false;
    	haveChestKey = false;
    	healthCoins = 0;
    }
    
    public void setDefaultPosition() {
    	worldX = gp.tileSize * 23;
        worldY = gp.tileSize * 21;
        direction = "down";
    }
    
    public void setLife() {
    	life = maxLife;
    	noDamage = false;
    }
    
    public int getAttack() {
    	return attack = strength * currentWeapon.attackValue;
    }
    
    public void setInventory() {
    	inventory.clear();
    	inventory.add(currentWeapon);
    }
    
    public void getPlayerImage() {
  		up2 = setup("/player/up 2", gp.tileSize, gp.tileSize);
		up1 = setup("/player/up 1", gp.tileSize, gp.tileSize);
		down1 = setup("/player/down 1", gp.tileSize, gp.tileSize);
		down2 = setup("/player/down 2", gp.tileSize, gp.tileSize);
		left_run = setup("/player/left run", gp.tileSize, gp.tileSize);
		left_stand = setup("/player/left stand", gp.tileSize, gp.tileSize);
		right_stand = setup("/player/right stand", gp.tileSize, gp.tileSize);
		right_run = setup("/player/right run", gp.tileSize, gp.tileSize);
		down_stand = setup("/player/down_stand", gp.tileSize, gp.tileSize);
		up_stand = setup("/player/up_stand", gp.tileSize, gp.tileSize); 	
    }
    
    public void getPlayerAttackImage() {
    	attackUp1 = setup("/player/attack up1", gp.tileSize, gp.tileSize);
    	attackUp2 = setup("/player/attack up2", gp.tileSize, gp.tileSize*2);
    	
    	attackDown1 = setup("/player/attack down1", gp.tileSize, gp.tileSize);
    	attackDown2 = setup("/player/attack down3", gp.tileSize*2, gp.tileSize*2);
    	
    	attackLeft1 = setup("/player/attack left1", gp.tileSize*2, gp.tileSize);
    	attackLeft2 = setup("/player/attack left2", gp.tileSize*2, gp.tileSize);
    	
    	attackRight1 = setup("/player/attack right1", gp.tileSize*2, gp.tileSize);
    	attackRight2 = setup("/player/attack right2", gp.tileSize*2, gp.tileSize);
    }
    
    public void update(){
    	if(playerAttacking == true) {
    		attacking();
    	}
    	else if (key.upPressed==true||key.downPressed == true||
    			key.leftPressed == true||key.rightPressed == true || key.enterPressed == true) {
    		if (key.upPressed == true){
            	direction = "up";
            }

            if (key.downPressed == true){
            	direction = "down";
            }

            if (key.leftPressed == true){
            	direction = "left";
            }

            if (key.rightPressed == true){
            	direction = "right";
            }
                      
            collisionOn = false;
            //CHECK TILE COLLISION
            gp.checker.checkTile(this);
            boolean blockedByTile = collisionOn;

            //CHECK NPC COLLISION
            int npcIndex = gp.checker.checkEntity(this, gp.npc);
            interactNPC(npcIndex);
            
            //CHECK OBJECT COLLISION
            int objIndex = gp.checker.checkObject(this, true);
            pickUpObject(objIndex);
            
            //CHECK MONSTER COLLISION
            int monsterIndex = gp.checker.checkEntity(this, gp.monsters);
            contactMonster(monsterIndex);

            
            //CHECK EVENT
            gp.eventH.checkEvent();
            
            //IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (collisionOn == false && key.enterPressed == false) {
            	switch(direction) {
            	case "up":
            		worldY -= speed;
            		break;
            	case "down":
            		 worldY += speed;
            		break;
            	case "left":
            		worldX -= speed;
            		break;
            	case "right":
            		worldX += speed;
            		break;
            	}
            }
            //CORNER ASSIST: only a wall is in the way (no monster, NPC or object), so try sliding round the corner
            else if (blockedByTile && key.enterPressed == false && npcIndex == 999 && objIndex == 999 && monsterIndex == 999) {
            	cornerAssist();
            }

            gp.key.enterPressed = false;
            
            spriteCounter ++;
            if (spriteCounter > 10) {
            	if (spriteNumber == 1) {
            		spriteNumber = 2;
            	}
            	else if (spriteNumber == 2) {
            		spriteNumber = 1;
            	}
            	spriteCounter = 0;
            }
    	}
    	
    	if (gp.key.shootPressed == true && projectile.alive == false && shotCounter == 30) {
    		//alive makes sure only one fire ball is shot at a time as long as it's true,you can't shoot another one.
    		projectile.set(worldX, worldY, direction, true);
    		gp.projectileList.add(projectile);
    		shotCounter = 0;
    	}
    	
    	if (shotCounter < 30) {
    		shotCounter++;
    	}
    	
    	if(noDamage == true) {
    		noDamageCounter ++;
    		if(noDamageCounter > 60) {
    			noDamage = false;
    			noDamageCounter = 0;
    		}
    	}
    	
    	if(life <= 0) {
    		gp.gameState = gp.gameOverState;
    	}
    }
    
    // How far (pixels) the player may be off a corridor opening and still get slid into it.
    static final int CORNER_ASSIST_RANGE = 24;

    // Walking into a wall next to an opening: slide sideways toward the opening (one step per frame)
    // instead of stopping dead, so turning into 1-tile corridors doesn't need pixel-perfect timing.
    void cornerAssist() {
    	boolean vertical = direction.equals("up") || direction.equals("down");
    	for (int dist = 1; dist <= CORNER_ASSIST_RANGE; dist++) {
    		for (int sign = -1; sign <= 1; sign += 2) {
    			int shift = dist * sign;
    			int testX = vertical ? worldX + shift : worldX;
    			int testY = vertical ? worldY : worldY + shift;
    			if (tileFreeAt(testX, testY, direction)) {
    				// opening found: take one sideways step toward it, if that step isn't into a wall
    				String side = vertical ? (sign < 0 ? "left" : "right") : (sign < 0 ? "up" : "down");
    				int step = Math.min(speed, dist);
    				if (tileFreeAt(worldX, worldY, side)) {
    					if (vertical) worldX += step * sign;
    					else worldY += step * sign;
    				}
    				return;
    			}
    		}
    	}
    }

    // Would moving one step in `dir` from (x, y) be free of solid tiles?
    boolean tileFreeAt(int x, int y, String dir) {
    	int oldX = worldX, oldY = worldY;
    	String oldDir = direction;
    	worldX = x;
    	worldY = y;
    	direction = dir;
    	collisionOn = false;
    	gp.checker.checkTile(this);
    	boolean free = !collisionOn;
    	worldX = oldX;
    	worldY = oldY;
    	direction = oldDir;
    	collisionOn = true; // we were blocked; keep it that way for the rest of this frame
    	return free;
    }

    public void attacking() {
    	spriteCounter++;
    	if(spriteCounter <= 5) {
    		spriteNumber = 1;
    	}
    	if(spriteCounter > 5 && spriteCounter <= 25) {
    		spriteNumber = 2;
    		
    		//store current values
    		int currentWorldX = worldX;
    		int currentWorldY = worldY;
    		int solidAreaWidth = solidArea.width;
    		int solidAreaHeight = solidArea.height;
    		
    		//Adjust the world y for the attack area
    		switch(direction) {
    		case "up":
    			worldY -= attackArea.height;
    			break;
    		case "down":
    			worldY += attackArea.height;
    			break;
    		case "left":
    			worldX -= attackArea.width;
    			break;
    		case "right":
    			worldX += attackArea.width;
    			break;
    		}
    		
    		//Attack area becomes solid Area
    		solidArea.width = attackArea.width;
    		solidArea.height = attackArea.height;
    		
    		int monsterIndex = gp.checker.checkEntity(this, gp.monsters);
    		damageMonster(monsterIndex, attack);
    		worldX = currentWorldX;
    		worldY = currentWorldY;
    		solidArea.width = solidAreaWidth;
    		solidArea.height = solidAreaHeight;
    		
    	}
    	if(spriteCounter > 25) {
    		spriteNumber = 1;
    		spriteCounter = 0;
    		playerAttacking = false;
    	}
    }
    
    public void pickUpObject(int i) {
    	if(i != 999) {
    		String text = "";

    		//DOOR: maze <-> wizard's house
    		if ("Door".equals(gp.obj[i].name)) {
    			if (gp.currentMap == 1) {
    				gp.changeMap(2);
    			}
    			else if (gp.currentMap == 2) {
    				gp.exitHouse();
    			}
    			return;
    		}

    		//HEALTH COIN: stacks in one inventory slot
    		if (OBJHealthCoin.NAME.equals(gp.obj[i].name)) {
    			if (healthCoins == 0 && inventory.size() >= maxInventorySize) {
    				text = "Your inventory is full";
    				if (!gp.user.message.contains(text)) {
    					gp.user.addMessage(text);
    				}
    				return;
    			}
    			if (healthCoins == 0) {
    				inventory.add(gp.obj[i]);
    			}
    			healthCoins++;
    			gp.aSet.coinCollected(gp.obj[i]);
    			gp.obj[i] = null;
    			gp.user.addMessage("Health Coin obtained!! (x" + healthCoins + ")");
    			return;
    		}

    		//GRAND TREASURE: needs the wizard's key and Level 6, then the game is won
    		if ("Grand Treasure".equals(gp.obj[i].name)) {
    			if (haveChestKey == false) {
    				text = "It's locked... find the wizard!";
    			}
    			else if (level < 6) {
    				text = "You must be Level 6 to open it (you are Level " + level + ")";
    			}
    			else {
    				gp.user.gameEnd = true;
    				return;
    			}
    			if (!gp.user.message.contains(text)) { // don't stack the same message every frame
    				gp.user.addMessage(text);
    			}
    			return;
    		}

    	    if (gp.obj[i].name == "Treasure") {
    	    	if(haveKey == true && level >= 3) { // >= so a higher level can never lock the player out
    	    		// the key is used up on the first chest
    	    		inventory.removeIf(item -> item != null && "Key".equals(item.name));
    	    		inventory.add(gp.obj[i]);
        			haveKey = false;
        			gp.obj[i] = null;
        			gp.changeMap(1);
    	    	}
    		}
    		else if (inventory.size() != maxInventorySize && gp.obj[i].name != "Treasure") {
    			inventory.add(gp.obj[i]);
    			text = gp.obj[i].name + " obtained!!";
    			haveKey = true;
    			gp.obj[i] = null;
    		}
    		else {
    			text = "Your inventory is full";
    		}
    	    gp.user.addMessage(text);
    	}  
    }
    
    // After a conversation ends, the player has to step away from the NPC and bump into them again
    // to hear it again (otherwise standing against them would restart it straight away).
    public boolean talkLocked = false;

    // Enter in the inventory: use the selected item. Only Health Coins can be used.
    public void useSelectedItem() {
    	int index = gp.user.getIndexforSlot();
    	if (index >= inventory.size()) {
    		return;
    	}
    	Entity item = inventory.get(index);
    	if (!OBJHealthCoin.NAME.equals(item.name) || healthCoins <= 0) {
    		return;
    	}
    	if (life >= maxLife) {
    		gp.user.showInventoryNotice("Your life is already full!");
    		return;
    	}
    	life = Math.min(maxLife, life + OBJHealthCoin.HEAL_AMOUNT);
    	healthCoins--;
    	if (healthCoins == 0) {
    		inventory.remove(index); // last coin used: free the slot
    	}
    	gp.user.showInventoryNotice("Life restored! " + healthCoins + " coin" + (healthCoins == 1 ? "" : "s") + " left");
    }

    public void interactNPC(int i) {
    	if (i == 999) {
    		talkLocked = false; // not touching an NPC any more: the next bump starts a new conversation
    		if (key.enterPressed == true) {
    			playerAttacking = true;
    		}
    		return;
    	}
    	// bumping into an NPC starts the whole conversation; Enter goes through the lines
    	if (talkLocked == false) {
    		gp.npc[i].startConversation();
    	}
    }
    
    public void contactMonster(int i) {
    	if(i != 999) {
    		if(noDamage == false && gp.monsters[i].dying == false) {
    			int damage = gp.monsters[i].attack - defense;
    			if(damage < 0) {
    				damage = 0;
    			}
    			life -= damage;
    			noDamage = true;
    		}
    	}
    }
    
    public void damageMonster(int i, int attack) {
    	if(i != 999) {
    		if(gp.monsters[i].noDamage == false) {
    			
    			int damage = attack - gp.monsters[i].defense;
    			if(damage < 0) {
    				damage = 0;
    			}
    			gp.monsters[i].life -= damage;
    			gp.user.addMessage(damage + " Damage!");
    			gp.monsters[i].noDamage = true;
    			gp.monsters[i].damageReaction();
    			
    			if(gp.monsters[i].life <= 0) {
    				gp.monsters[i].dying = true;
    				gp.user.addMessage(gp.monsters[i].name + " killed!");
    				gp.user.addMessage("Exp + " + gp.monsters[i].exp);
    				exp += gp.monsters[i].exp;
    				capWorldVer2Exp();
    				levelUp();
    			}
    		}
    	}
    }
    
    // WorldVer2 tops out at Level 3: XP stops one short of Level 4 until the player goes through the chest.
    // (Without this, dying and choosing RETRY respawns the monsters while keeping XP, so players could farm Level 4.)
    public static final int WORLDVER2_MAX_LEVEL = 3;

    void capWorldVer2Exp() {
    	if (gp.currentMap == 0 && level >= WORLDVER2_MAX_LEVEL && exp >= nextLevelExp) {
    		exp = nextLevelExp - 1;
    		String text = "Max level here! Open the chest to grow stronger";
    		if (!gp.user.message.contains(text)) {
    			gp.user.addMessage(text);
    		}
    	}
    }

    public void levelUp() {
    	if(exp >= nextLevelExp) {
    		level ++;
    		strength ++;
    		attack = getAttack();
    		life = maxLife;
    		nextLevelExp *= 2;
    		
    		gp.gameState = gp.dialogueState;
    		gp.user.currentDialogue = "You are now Level " + level;
    	}
    }

    public void draw(Graphics2D g2){
    	BufferedImage image = null;
    	int tempScreenX = screenX;
    	int tempScreenY = screenY;
    	switch (direction) {
    	case "up":
    		if (playerAttacking == false) {
    			if (spriteNumber == 1) {
        			image = up2;
        		}
        		if (spriteNumber==2) {
        			image = up1;
        		}
        		if ((key.upPressed == false)||(gp.gameState == gp.pauseState)) {
        			image = up_stand;
        		}
    		}
    		
    		if (playerAttacking == true) {
    			if (spriteNumber == 1) {
        			image = attackUp1;
        		}
    			
        		if (spriteNumber==2) {
        			tempScreenY = screenY - (gp.tileSize);
        			image = attackUp2;
        		}
    		}
    		
       		break;
    	case "down":
    		if (playerAttacking == false) {
    			if (spriteNumber==1) {
        			image = down2;
        		}
        		if (spriteNumber==2) {
        			image = down1;
        		}
        		if ((key.downPressed == false)||(gp.gameState == gp.pauseState)) {
        			image = down_stand;
        		}
    		}
    		
    		if (playerAttacking == true) {
    			if (spriteNumber == 1) {
        			image = attackDown1;
        		}
        		if (spriteNumber==2) {
        			tempScreenY = screenY - (gp.tileSize/2);
        			tempScreenX = screenX - (gp.tileSize/2);
        			image = attackDown2;
        		}
    		}
    		
    		break;
    	case "left":
    		if (playerAttacking == false) {
    			if (spriteNumber==1) {
        			image = left_stand;
        		}
        		if (spriteNumber==2) {
        			image = left_run;
        		}
        		if ((key.leftPressed == false)||(gp.gameState == gp.pauseState)) {
        			image = left_stand;
        		}
    		}
    		
    		if (playerAttacking == true) {
    			if (spriteNumber == 1) {
    				tempScreenX = screenX - (gp.tileSize/2);
        			image = attackLeft1;
        		}
        		if (spriteNumber==2) {
        			tempScreenX = screenX - (gp.tileSize/2);
        			image = attackLeft2;
        		}
        		
    		}
       		break;
    	case "right":
    		if (playerAttacking == false) {
    			if (spriteNumber==1) {
        			image = right_stand;
        		}
        		if (spriteNumber==2) {
        			image = right_run;
        		}
        		if ((key.rightPressed == false)||(gp.gameState == gp.pauseState)) {
        			image = right_stand;
        		}
    		}
    		
    		if (playerAttacking == true) {
    			if (spriteNumber == 1) {
    				tempScreenX = screenX - (gp.tileSize/2);
        			image = attackRight1;
        		}
        		if (spriteNumber==2) {
        			tempScreenX = screenX - (gp.tileSize/2);
        			image = attackRight2;
        		}
            }
    		break;
    	}    	
    	
    	//MAKES PLAYER MORE TRANSPARENT WHEN HIT TO HAVE HITTING EFFECT
    	if (noDamage == true) {
    		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
    	}
    	
    	g2.drawImage(image, tempScreenX, tempScreenY, null);
    	
    	//RESET
    	g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}