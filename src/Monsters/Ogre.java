package Monsters;

import main.GamePanel;

public class Ogre extends Monster{
	public Ogre(GamePanel gp) {
		super(gp);
		name = "Ogre";
		speed = 2;
		maxLife = 6;
		life = maxLife;
		attack = 3;
		defense = 0;
		exp = 1;
		
		solidArea.x = 3;
		solidArea.y = 18;
		solidArea.width = 42;
		solidArea.height = 30;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;

		//AI: guard - stays near its post, charges when it sees you, walks back if lured away
		wanderSpeed = 1;
		chaseSpeed = 3;
		sightRange = 6;
		guardsHome = true;
		leashRange = 7;
		returnSpeed = 2;
		retreatTime = 60; // ogres shake it off fast (1 second) - they feel aggressive
		getImage();
	}
	
	
	public void getImage() {
		up1 = setup("/Ogre/up1", gp.tileSize, gp.tileSize);
		up2 = setup("/Ogre/up2", gp.tileSize, gp.tileSize);
		down1 = setup("/Ogre/down1", gp.tileSize, gp.tileSize);
		down2 = setup("/Ogre/down2", gp.tileSize, gp.tileSize);
		left_run = setup("/Ogre/left1", gp.tileSize, gp.tileSize);
		left_stand = setup("/Ogre/left2",gp.tileSize, gp.tileSize);
		right_stand = setup("/Ogre/right1", gp.tileSize, gp.tileSize);
		right_run = setup("/Ogre/right2", gp.tileSize, gp.tileSize);
	}
}
