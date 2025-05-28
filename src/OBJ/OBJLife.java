package OBJ;

import entity.Entity;
import main.GamePanel;

public class OBJLife extends Entity{
	
	public OBJLife(GamePanel gp){
		super(gp);
		name = "Player Life";
		image = setup("/objects/life full", gp.tileSize, gp.tileSize);
		image2 = setup("/objects/life half", gp.tileSize, gp.tileSize);
		image3 = setup("/objects/life empty", gp.tileSize, gp.tileSize);
	}

}
