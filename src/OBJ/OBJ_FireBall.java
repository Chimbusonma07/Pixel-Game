package OBJ;

import entity.FireBall;
import main.GamePanel;

public class OBJ_FireBall extends FireBall{
	GamePanel gp;
	public OBJ_FireBall(GamePanel gp) {
		super(gp);
		this.gp = gp;
		name = "Fireball";
		speed = 5;
		attack = 2;
		maxLife = 240; // safety range (~25 tiles); normally it flies until it hits a wall or a monster
		life = maxLife;
		alive = false;

		// small hitbox in the middle of the sprite so it fits down 1-tile corridors
		solidArea.x = 12;
		solidArea.y = 16; // same top as the player's hitbox, so the fireball fits wherever the player fits
		solidArea.width = 24;
		solidArea.height = 24;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
		getImage();
	}
	
	public void getImage() {
		up1 = setup("/fireball/up1", gp.tileSize, gp.tileSize);
		up2 = setup("/fireball/up2", gp.tileSize, gp.tileSize);
		down1 = setup("/fireball/down1", gp.tileSize, gp.tileSize);
		down2 = setup("/fireball/down2", gp.tileSize, gp.tileSize);
		left_stand = setup("/fireball/left1", gp.tileSize, gp.tileSize);
		left_run = setup("/fireball/left2", gp.tileSize, gp.tileSize);
		right_stand = setup("/fireball/right1", gp.tileSize, gp.tileSize);
		right_run = setup("/fireball/right2", gp.tileSize, gp.tileSize);
	}

}
