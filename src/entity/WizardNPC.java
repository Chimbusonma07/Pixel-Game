package entity;

import main.GamePanel;

public class WizardNPC extends Entity {
	GamePanel gp;

	public WizardNPC(GamePanel gp) {
		super(gp);
		this.gp = gp;
		direction = "down";
		speed = 1;
		getImage();
		setDialogue();
	}
	
	public void getImage() {
  		up2 = setup("/NPCs/wizard up 2", gp.tileSize, gp.tileSize);
		up1 = setup("/NPCs/wizard up 1", gp.tileSize, gp.tileSize);
		down1 = setup("/NPCs/wizard down 1", gp.tileSize, gp.tileSize);
		down2 = setup("/NPCs/wizard down 2", gp.tileSize, gp.tileSize);
		left_run = setup("/NPCs/wizard left run", gp.tileSize, gp.tileSize);
		left_stand = setup("/NPCs/wizard left", gp.tileSize, gp.tileSize);
		right_stand = setup("/NPCs/wizard right", gp.tileSize, gp.tileSize);
		right_run = setup("/NPCs/wizard right run", gp.tileSize, gp.tileSize);
		down_stand = setup("/NPCs/wizard down", gp.tileSize, gp.tileSize);
		up_stand = setup("/NPCs/wizard up", gp.tileSize, gp.tileSize); 
//		wizard_head = setup("/NPCs/wizard head"); 
    }
	
	public void setDialogue() {
		dialogues[0] = "Hello, young adventurer.";
		dialogues[1] = "If you want to unlock the treasure, you must have two things..";
		dialogues[2] = "Find the hidden teleportation tile and the hidden key in the location it takes you.";
		dialogues[3] = "You must reach the third level to use the key to unlock the treasure. Oh, and one more thing..";
		dialogues[4] = "You must be above level 1 to teleport back because teleportation takes a lot of energy. Good luck!!";
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
		
	public void speak() {
		super.speak();
	}
}













