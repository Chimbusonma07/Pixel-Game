package OBJ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import entity.Entity;
import main.GamePanel;

// Health Coin: walk over it to pick it up. Coins stack in one inventory slot;
// open the inventory (Shift), select the coin and press Enter to restore life.
public class OBJHealthCoin extends Entity {

	public static final String NAME = "Health Coin";
	public static final int HEAL_AMOUNT = 1; // half a heart

	public OBJHealthCoin(GamePanel gp) {
		super(gp);
		name = NAME;
		down1 = coinImage(gp.tileSize);
		down2 = down1; // Entity.draw shows down2 for objects
		collision = false; // walk over it to collect it
		description = "[" + name + "]\nRestores half a heart.\nMaze only: Enter to use.";
	}

	// There's no coin sprite, so draw a gold coin with a red cross.
	static BufferedImage coinImage(int size) {
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int m = size / 6, d = size - 2 * m;
		g.setColor(new Color(150, 95, 10));            // dark rim
		g.fillOval(m, m, d, d);
		g.setColor(new Color(245, 195, 45));           // gold face
		g.fillOval(m + 3, m + 3, d - 6, d - 6);
		g.setColor(new Color(255, 235, 140));          // shine
		g.setStroke(new BasicStroke(2));
		g.drawArc(m + 7, m + 7, d - 14, d - 14, 100, 70);
		int c = size / 2, arm = size / 7, len = size / 4; // red health cross
		g.setColor(new Color(200, 30, 40));
		g.fillRect(c - arm / 2, c - len, arm, len * 2);
		g.fillRect(c - len, c - arm / 2, len * 2, arm);
		g.dispose();
		return img;
	}
}
