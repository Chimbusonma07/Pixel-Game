package main;

import Monsters.Beetle;
import Monsters.Blue;
import Monsters.Ogre;
import OBJ.OBJChest;
import OBJ.OBJKey;
import entity.WizardNPC;

public class AssetSetter {
	
	GamePanel gp;

	public AssetSetter(GamePanel gp){
		this.gp = gp;
	}
	
	public void setObjects() {
		gp.obj[1] = new OBJKey(gp);
		gp.obj[1].worldX = gp.tileSize * 40;
		gp.obj[1].worldY = gp.tileSize * 10;
		
		gp.obj[2] = new OBJChest(gp);
		gp.obj[2].worldX = gp.tileSize * 10;
		gp.obj[2].worldY = gp.tileSize * 8;
	}
	
	public void setNPC() {
		
		gp.npc[0] = new WizardNPC(gp);		
		gp.npc[0].worldX = gp.tileSize * 21;
		gp.npc[0].worldY = gp.tileSize * 21;
		
	}
	
	public void setMonsters() {
		
		gp.monsters[0] = new Beetle(gp);	
		gp.monsters[0].worldX = gp.tileSize * 38;
		gp.monsters[0].worldY = gp.tileSize * 11;
		
		gp.monsters[1] = new Beetle(gp);	
		gp.monsters[1].worldX = gp.tileSize * 37;
		gp.monsters[1].worldY = gp.tileSize * 12;
		
		gp.monsters[2] = new Beetle(gp);	
		gp.monsters[2].worldX = gp.tileSize * 40;
		gp.monsters[2].worldY = gp.tileSize * 12;
		
		gp.monsters[3] = new Blue(gp);	
		gp.monsters[3].worldX = gp.tileSize * 10;
		gp.monsters[3].worldY = gp.tileSize * 38;
		
		gp.monsters[4] = new Ogre(gp);	
		gp.monsters[4].worldX = gp.tileSize * 23;
		gp.monsters[4].worldY = gp.tileSize * 40;
		
		gp.monsters[5] = new Ogre(gp);	
		gp.monsters[5].worldX = gp.tileSize * 23;
		gp.monsters[5].worldY = gp.tileSize * 38;
		
		gp.monsters[6] = new Blue(gp);	
		gp.monsters[6].worldX = gp.tileSize * 18;
		gp.monsters[6].worldY = gp.tileSize * 31;
		
		gp.monsters[7] = new Blue(gp);	
		gp.monsters[7].worldX = gp.tileSize * 12;
		gp.monsters[7].worldY = gp.tileSize * 33;
	}
}
