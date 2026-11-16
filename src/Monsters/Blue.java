package Monsters;

import OBJ.OBJ_SlimeBall;
import main.GamePanel;

public class Blue extends Monster{
	public Blue(GamePanel gp) {
		super(gp);
		name = "Blue";
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

		//AI: spitter - keeps its distance and shoots slime down straight corridors
		wanderSpeed = 1;
		chaseSpeed = 1;
		sightRange = 7;
		ranged = true;
		missile = new OBJ_SlimeBall(gp);
		retreatTime = 120; // backs off 2 seconds, then returns to spitting range
		getImage();
	}
	
	
	public void getImage() {
		up1 = setup("/Monsters/blue1", gp.tileSize, gp.tileSize);
		up2 = setup("/Monsters/blue2", gp.tileSize, gp.tileSize);
		down1 = setup("/Monsters/blue1", gp.tileSize, gp.tileSize);
		down2 = setup("/Monsters/blue2", gp.tileSize, gp.tileSize);
		left_run = setup("/Monsters/blue1", gp.tileSize, gp.tileSize);
		left_stand = setup("/Monsters/blue2",gp.tileSize, gp.tileSize);
		right_stand = setup("/Monsters/blue1", gp.tileSize, gp.tileSize);
		right_run = setup("/Monsters/blue2", gp.tileSize, gp.tileSize);
	}
}
