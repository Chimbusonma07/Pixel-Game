package OBJ;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import entity.FireBall;
import main.GamePanel;

// Projectile spat by Blue monsters. There's no sprite for it, so the green blob is drawn in code.
public class OBJ_SlimeBall extends FireBall {

	public OBJ_SlimeBall(GamePanel gp) {
		super(gp);
		name = "Slime Ball";
		fromPlayer = false;
		speed = 4;
		attack = 2;
		maxLife = 120;
		life = maxLife;
		alive = false;

		solidArea.x = 14;
		solidArea.y = 14;
		solidArea.width = 20;
		solidArea.height = 20;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;

		BufferedImage big = blob(gp.tileSize, 11);
		BufferedImage small = blob(gp.tileSize, 9); // two sizes so it wobbles
		up1 = down1 = left_stand = right_stand = big;
		up2 = down2 = left_run = right_run = small;
	}

	static BufferedImage blob(int size, int radius) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int c = size / 2;
		g.setColor(new Color(20, 90, 30));
		g.fillOval(c - radius - 2, c - radius - 2, (radius + 2) * 2, (radius + 2) * 2);
		g.setColor(new Color(90, 200, 70));
		g.fillOval(c - radius, c - radius, radius * 2, radius * 2);
		g.setColor(new Color(200, 255, 180));
		g.fillOval(c - radius / 2, c - radius / 2 - 2, radius / 2, radius / 2);
		g.dispose();
		return img;
	}
}
