package OBJ;

import entity.Entity;
import main.GamePanel;

public class OBJChest extends Entity{
	
	public OBJChest(GamePanel gp){
		super(gp);
		name = "Treasure";
		down1 = setup("/objects/treasure chest", gp.tileSize, gp.tileSize);
	}

}
