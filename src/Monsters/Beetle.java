package Monsters;


import entity.Entity;
import main.GamePanel;

public class Beetle extends Entity{
	GamePanel gp;
	public Beetle(GamePanel gp) {
		super(gp);
		this.gp = gp;
		type = 2;
		name = "Beetle";
		speed = 1;
		maxLife = 4;
		life = maxLife;
		attack = 2;
		defense = 0;
		exp = 1;
		
		solidArea.x = 3;
		solidArea.y = 18;
		solidArea.width = 42;
		solidArea.height = 30;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		getImage();
	}
	
	
	public void getImage() {
		up1 = setup("/Monsters/beetle", gp.tileSize, gp.tileSize);
		up2 = setup("/Monsters/beetle1", gp.tileSize, gp.tileSize);
		down1 = setup("/Monsters/beetle", gp.tileSize, gp.tileSize);
		down2 = setup("/Monsters/beetle1", gp.tileSize, gp.tileSize);
		left_run = setup("/Monsters/beetle", gp.tileSize, gp.tileSize);
		left_stand = setup("/Monsters/beetle1",gp.tileSize, gp.tileSize);
		right_stand = setup("/Monsters/beetle3", gp.tileSize, gp.tileSize);
		right_run = setup("/Monsters/beetle4", gp.tileSize, gp.tileSize);
	}
	
	public void setAction() {
			
			actionCounter ++;
			
			if (actionCounter == 120) {
				int i = (int) ((Math.random()*100) + 1);
				
				if(i <= 25) {
					direction = "up";
				}
				
				if(i > 25 && i <= 50) {
					direction = "down";
				}
				
				if(i > 50 && i <= 75) {
					direction = "left";
				}
				
				if(i > 75 && i <= 100) {
					direction = "right";
				}
				actionCounter = 0; //reset the counter
			}
		}
	
	public void damageReaction() {
		actionCounter = 0;
		direction = gp.player.direction;
	}
}
