package OBJ;

import entity.Entity;
import main.GamePanel;

public class OBJKey extends Entity {
	
	public OBJKey(GamePanel gp){
		super(gp);
		name = "Key";
		down1 = setup("/objects/key", gp.tileSize, gp.tileSize);
		description = "[" + name + "]\nUse it to unlock treasure";
	}

}
