package OBJ;

import entity.Entity;
import main.GamePanel;

public class OBJKey extends Entity {
	
	public OBJKey(GamePanel gp){
		super(gp);
		name = "Key";
		down1 = setup("/objects/key", gp.tileSize, gp.tileSize);
		down2 = down1; // Entity.draw shows down2 for objects, so set both to make the key visible
		description = "[" + name + "]\nUse it to unlock treasure";
	}

}
