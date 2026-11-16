package entity;

import main.GamePanel;

public class FireBall extends Entity{

	public FireBall(GamePanel gp) {
		super(gp);
	}

	public void set(int worldX, int worldY, String direction, boolean alive) {
		this.worldX = worldX;
		this.worldY = worldY;
		this.direction = direction;
		this.alive = alive;
		this.life = this.maxLife;
	}
	
	public boolean fromPlayer = true; // false for monster projectiles (they hurt the player instead)
	public Entity shooter;            // the monster that fired it (its own shots never hit it)

	// A stray monster shot: the hit monster glitches (flashes, health bar shows) and loses life.
	// It's not the player's doing, so no XP, no messages, and it doesn't make the monster
	// retreat or notice the player. It weakens the monster but never kills it (stays at 1+ life).
	void hitMonsterByMistake(Entity target) {
		if (target.noDamage) {
			return; // still flashing from the last hit
		}
		int damage = Math.max(attack - target.defense, 1);
		target.life = Math.max(target.life - damage, 1);
		target.noDamage = true; // the existing hit effect: flicker + health bar
	}

	public void update() {
		if (fromPlayer) {
			int monsterIndex = gp.checker.checkEntity(this, gp.monsters);
			// fly through monsters that are already dying instead of fizzling out on them
			if (monsterIndex != 999 && gp.monsters[monsterIndex].dying == false) {
				gp.player.damageMonster(monsterIndex, attack);
				alive = false;
				return;
			}
		}
		else if (gp.checker.checkPlayer(this)) {
			if (gp.player.noDamage == false) {
				int damage = Math.max(attack - gp.player.defense, 0);
				gp.player.life -= damage;
				gp.player.noDamage = true;
			}
			alive = false;
			return;
		}
		else {
			// FRIENDLY FIRE: a monster's shot that misses the player can hit another monster instead
			int monsterIndex = gp.checker.checkEntity(this, gp.monsters, shooter);
			if (monsterIndex != 999 && gp.monsters[monsterIndex].dying == false) {
				hitMonsterByMistake(gp.monsters[monsterIndex]);
				alive = false;
				return;
			}
		}

		// stop at walls (brick, trees, water) instead of passing through them
		collisionOn = false;
		gp.checker.checkTile(this);
		if (collisionOn == true) {
			alive = false;
			return;
		}

		switch(direction) {
		case "up": 
			worldY -= speed; 
			break;
		case "down": 
			worldY += speed; 
			break;
		case "left": 
			worldX -= speed; 
			break;
		case "right": 
			worldX += speed; 
			break;
		}
		
		life--;
		if(life <= 0) {
			alive = false;
		}
		
		spriteCounter++;
		if(spriteCounter > 12) {
			if(spriteNumber == 1) {
				spriteNumber = 2;
			}
			else if(spriteNumber == 2) {
				spriteNumber = 1;
			}
			spriteCounter = 0;
		}
	}
}
