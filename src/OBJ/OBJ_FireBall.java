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
		maxLife = 80;
		life = maxLife;
		alive = false;
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
