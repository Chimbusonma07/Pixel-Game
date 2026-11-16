package Monsters;


import main.GamePanel;

public class Beetle extends Monster{
	public Beetle(GamePanel gp) {
		super(gp);
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

		//AI: skittish swarmer - chases, but runs away when badly hurt
		wanderSpeed = 1;
		chaseSpeed = 2;
		sightRange = 5;
		fleesWhenHurt = true;
		retreatTime = 180; // skittish - runs for the full 3 seconds
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
}
