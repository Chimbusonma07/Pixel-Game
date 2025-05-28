package entity;

import java.awt.AlphaComposite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTools;

public class Entity {
	
	GamePanel gp;
    public BufferedImage up2, up1, down1, down2, left_stand, left_run, right_stand, right_run,
    up_stand, down_stand, left1, left2, right1, right2;
    public BufferedImage image, image2, image3;
    public BufferedImage attackUp1, attackUp2, attackUp3, attackUp4, attackLeft1, 
    attackLeft2, attackLeft3, attackLeft4, attackRight1, attackRight2, attackRight3, attackRight4,
    attackDown1, attackDown2, attackDown3, attackDown4;
    public boolean collision = false;
    public Rectangle attackArea = new Rectangle(0,0,0,0);
	public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
	public int solidAreaDefaultX, solidAreaDefaultY;
	String dialogues[] = new String[20];
	
	public int worldX, worldY;
	public String direction = "down";
    public int spriteNumber = 1;
    public boolean collisionOn = false;
    int dialogueIndex = 0;
	public boolean noDamage = false;
	boolean playerAttacking = false;
	public boolean alive = true;
	public boolean dying = false;
	public boolean healthBarOn = false;
		
	public int actionCounter = 0;
	public int noDamageCounter = 0;
	public int spriteCounter = 0;
	public int dyingCounter = 0;
	int healthBarCounter = 0;
	public int shotCounter = 0;

	//PLAYER AND OTHER CHARACTER LIFE SETTING
	public int type; //0 - player, 1 - npc, 2 - monsters
	public int maxLife;
	public int life;
	public String name;
	public int speed;
	public int level;
	public int strength;
	public int attack;
	public int defense;
	public int exp;
	public int nextLevelExp;
	public Entity currentWeapon;
	public FireBall projectile;
	
	//ITEM ATTRIBUTES
	public int attackValue;
	public int defenseValue;
	public String description = " ";
	
	public Entity(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setAction() {}
	
	public void damageReaction() {}
	
	public void speak() {
		if(dialogues[dialogueIndex] == null) {
			dialogueIndex = 0;
		}
		gp.user.currentDialogue = dialogues[dialogueIndex];
		dialogueIndex ++;
		switch(gp.player.direction) {
		case "up":
			direction ="down";
			break;
		case "down":
			direction ="up";
			break;
		case "left":
			direction ="right";
			break;
		case "right":
			direction ="left";
			break;
		}
	}
	
	public void update() {
		setAction();
		
		collisionOn = false;
		gp.checker.checkTile(this);
		gp.checker.checkEntity(this, gp.npc);
		gp.checker.checkEntity(this, gp.monsters);
		gp.checker.checkObject(this, false);
		boolean contactPlayer = gp.checker.checkPlayer(this);
		
		if(this.type == 2 && contactPlayer == true) {
			if(gp.player.noDamage == false) {
				int damage = attack - gp.player.defense;
    			if(damage < 0) {
    				damage = 0;
    			}
				gp.player.life -= damage;
				gp.player.noDamage = true;
			}
		}
		
        if (collisionOn == false) {
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
        
        if(noDamage == true) {
    		noDamageCounter ++;
    		if(noDamageCounter > 40) {
    			noDamage = false;
    			noDamageCounter = 0;
    		}
    	}
	}
	
	public void draw(Graphics2D g2) {
		
		BufferedImage image = null;
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;
		
		//If in camera frame
		if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
				worldX - gp.tileSize < gp.player.worldX + gp.player.screenX && 
				worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
				worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
			
			switch (direction) {
	    	case "up":
	    		if (spriteNumber==1) {
	    			image = up2;
	    		}
	    		if (spriteNumber==2) {
	    			image = up1;
	    		}
	       		break;
	       		
	    	case "down":
	    		if (spriteNumber==1) {
	    			image = down2;
	    		}
	    		if (spriteNumber==2) {
	    			image = down1;
	    		}
	    		break;
	    		
	    	case "left":
	    		if (spriteNumber==1) {
	    			image = left_stand;
	    		}
	    		if (spriteNumber==2) {
	    			image = left_run;
	    		}
	       		break;
	       		
	    	case "right":
	    		if (spriteNumber==1) {
	    			image = right_stand;
	    		}
	    		if (spriteNumber==2) {
	    			image = right_run;
	    		}
	    		break;
	    	} 
			
			//monster health bar
			if (type == 2 && healthBarOn == true) {
				double barScale = (double) gp.tileSize/maxLife; //Splits the health bar into even divisions based on monster max life
				double healthBarLength = barScale * life;// gives the length of the bar based on the lives remaining
				
				//OUTLINE
				g2.setColor(Color.black);
				g2.fillRect(screenX - 1, screenY - 3, gp.tileSize + 2, 10);
				
				g2.setColor(Color.red);
				g2.fillRect(screenX, screenY - 2, (int) healthBarLength, 8);
				
				healthBarCounter ++;
				
				if(healthBarCounter > 100) {
					healthBarCounter = 0;
					healthBarOn = false;
				}
			}
			
			if (noDamage == true) {
				healthBarOn = true;
				healthBarCounter = 0;
				transparency(g2, 0.4f);
	    	}
			
			if(dying == true) {
				dyingAnimation(g2);
			}
			
			g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
			
			transparency(g2, 1f);
		}		
	}
	
	public void dyingAnimation(Graphics2D g2) {
		int i = 5;
		dyingCounter++;
		if(dyingCounter <= i) {transparency(g2, 0f);}
		if(dyingCounter > i && dyingCounter <= i*2) {transparency(g2, 1f);}
		if(dyingCounter > i*2 && dyingCounter <= i*3) {transparency(g2, 0f);}
		if(dyingCounter > i*3 && dyingCounter <= i*4) {transparency(g2, 1f);}
		if(dyingCounter > i*4 && dyingCounter <= i*5) {transparency(g2, 0f);}
		if(dyingCounter > i*5 && dyingCounter <= i*6) {transparency(g2, 1f);}
		if(dyingCounter > i*6 && dyingCounter <= i*7) {transparency(g2, 0f);}
		if(dyingCounter > i*7 && dyingCounter <= i*8) {transparency(g2, 1f);}
		if(dyingCounter > i*8) {
			alive = false;
			dying = false;
		}
	}
	
	public void transparency(Graphics2D g2, float num) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, num));
	}
	
	public BufferedImage setup(String imagePath, int width, int height) {   	
    	UtilityTools ut = new UtilityTools();
    	BufferedImage image = null;   	
    	try {
    		image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
    		image = ut.scaleImage(image, width, height);
    	} catch(IOException e){
    		e.printStackTrace();
    	}  	
    	return image;
    }
}