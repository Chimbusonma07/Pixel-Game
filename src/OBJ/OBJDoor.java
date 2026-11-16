package OBJ;

import entity.Entity;
import main.GamePanel;

public class OBJDoor extends Entity {

	public OBJDoor(GamePanel gp) {
		super(gp);
		name = "Door";
		down1 = setup("/objects/door", gp.tileSize, gp.tileSize);
		down2 = down1; // Entity.draw shows down2 for objects, so set both to make the door visible
		collision = true;
		description = "[" + name + "]\nLeads to the wizard's house";
	}
}
