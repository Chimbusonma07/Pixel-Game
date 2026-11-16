package Monsters;

import entity.Entity;
import entity.FireBall;
import main.GamePanel;
import main.PathFinder;

/**
 * Shared monster brain. Monsters only make decisions when standing exactly on a tile, so they
 * travel cleanly down corridors and turn at junctions. Each kind switches on the behaviours it uses:
 *
 *  - wander:  patrol corridors, prefer going straight, pause now and then (all monsters)
 *  - chase:   once the player is seen (line of sight), follow the shortest path; remember them
 *             for a few seconds after losing sight
 *  - flee:    run away when badly hurt (Beetle)
 *  - ranged:  keep distance and spit a projectile when lined up with the player (Blue)
 *  - guard:   stay near the spawn point, charge fast, and walk back home when lured too far (Ogre)
 */
public class Monster extends Entity {
	protected GamePanel gp;

	//TUNING (set by each monster kind)
	protected int wanderSpeed = 1;
	protected int chaseSpeed = 2;     // speeds must divide 48 so monsters land exactly on tiles
	protected int sightRange = 6;     // tiles
	protected int giveUpSteps = 15;   // stop chasing when the path to the player is longer than this
	protected boolean fleesWhenHurt = false;
	protected boolean ranged = false;
	protected boolean guardsHome = false;
	protected int leashRange = 7;     // guards: furthest they follow you from home (tiles of path)
	protected int returnSpeed = 2;
	protected int patrolRadius = 3;  // guards: how far (tiles of path) they wander from their post
	protected FireBall missile;       // ranged monsters only
	protected int retreatTime = 180;  // frames spent backing off after being hit (3 seconds)

	//STATE
	int memory = 0;          // frames left before forgetting the player after losing sight
	int pauseFrames = 0;
	int shotCooldown = 0;
	int stuckFrames = 0;
	int retreatFrames = 0;   // > 0 while backing off after being hit
	int homeCol = -1, homeRow = -1;
	int[][] homeDist;
	boolean returningHome = false;

	static final int MEMORY_FRAMES = 180; // 3 seconds

	public Monster(GamePanel gp) {
		super(gp);
		this.gp = gp;
		type = 2;
	}

	public void setAction() {
		int tile = gp.tileSize;
		PathFinder pf = gp.pathFinder;

		if (shotCooldown > 0) shotCooldown--;
		if (retreatFrames > 0) retreatFrames--;
		boolean sees = pf.canSeePlayer(this, sightRange);
		if (sees) {
			memory = MEMORY_FRAMES;
		}
		else if (memory > 0) {
			memory--;
		}

		// decide only when standing exactly on a tile
		if (worldX % tile != 0 || worldY % tile != 0) {
			return;
		}
		int col = worldX / tile, row = worldY / tile;
		if (homeCol < 0) {
			homeCol = col;
			homeRow = row;
		}

		//RETREAT: just got hit, back away from the player for a few seconds (cornered = stand and fight)
		if (retreatFrames > 0) {
			pauseFrames = 0;
			if (stepUphill(pf.toPlayer, col, row)) {
				speed = chaseSpeed;
				return;
			}
		}

		int stepsToPlayer = pf.toPlayer[col][row];
		boolean aware = memory > 0 && stepsToPlayer >= 0 && stepsToPlayer <= giveUpSteps;

		//GUARD: don't get lured away from home
		if (guardsHome) {
			if (homeDist == null) {
				homeDist = pf.distanceMapFrom(homeCol, homeRow);
			}
			if (aware && homeDist[col][row] > leashRange) {
				returningHome = true;
				memory = 0;
			}
			if (returningHome) {
				if (homeDist[col][row] <= 1) {
					returningHome = false;
				}
				else {
					speed = returnSpeed;
					stepDownhill(homeDist, col, row);
					return;
				}
			}
		}

		if (aware) {
			pauseFrames = 0;

			//FLEE: badly hurt monsters run (if cornered they turn and fight)
			if (fleesWhenHurt && life <= maxLife / 3 && stepUphill(pf.toPlayer, col, row)) {
				speed = chaseSpeed;
				return;
			}

			//RANGED: keep a gap and shoot down straight corridors
			if (ranged && rangedAction(col, row, sees)) {
				return;
			}

			//CHASE
			speed = chaseSpeed;
			if (stepsToPlayer == 0) {
				faceTowardPlayer(col, row);
			}
			else {
				stepDownhill(pf.toPlayer, col, row);
			}
			return;
		}

		wander(col, row);
	}

	// Ranged behaviour. Returns true if it decided what to do this tile.
	boolean rangedAction(int col, int row, boolean sees) {
		PathFinder pf = gp.pathFinder;
		int dc = pf.playerCol - col, dr = pf.playerRow - row;
		int dist = Math.abs(dc) + Math.abs(dr);

		// too close: back off
		if (dist <= 2 && stepUphill(pf.toPlayer, col, row)) {
			speed = chaseSpeed;
			return true;
		}
		// lined up in a straight, clear corridor: stand still and spit
		if (sees && (dc == 0 || dr == 0) && dist <= sightRange) {
			faceTowardPlayer(col, row);
			speed = 0;
			if (shotCooldown == 0 && missile != null && missile.alive == false) {
				missile.shooter = this;
				missile.set(worldX, worldY, direction, true);
				gp.projectileList.add(missile);
				shotCooldown = 120;
			}
			return true;
		}
		return false; // otherwise approach normally until lined up
	}

	void wander(int col, int row) {
		PathFinder pf = gp.pathFinder;
		if (pauseFrames > 0) {
			pauseFrames--;
			speed = 0;
			return;
		}
		speed = wanderSpeed;

		// open directions, not turning back unless it's a dead end
		java.util.List<String> options = new java.util.ArrayList<>();
		for (String d : new String[] {"up", "down", "left", "right"}) {
			int[] n = neighbour(col, row, d);
			boolean nearHome = !guardsHome || homeDist == null || (homeDist[n[0]][n[1]] >= 0 && homeDist[n[0]][n[1]] <= patrolRadius);
			if (pf.walkable(n[0], n[1]) && nearHome && !d.equals(opposite(direction))) {
				options.add(d);
			}
		}
		if (options.isEmpty()) {
			int[] back = neighbour(col, row, opposite(direction));
			if (pf.walkable(back[0], back[1])) {
				direction = opposite(direction);
			}
			else {
				speed = 0;
			}
			return;
		}
		if (!(options.contains(direction) && Math.random() < 0.6)) {
			direction = options.get((int) (Math.random() * options.size()));
		}
		if (Math.random() < 0.08) {
			pauseFrames = 40 + (int) (Math.random() * 60); // stop and "look around" now and then
		}
	}

	// Step to the neighbouring tile with a smaller distance (towards the map's origin).
	boolean stepDownhill(int[][] map, int col, int row) {
		int here = map[col][row];
		for (String d : new String[] {"up", "down", "left", "right"}) {
			int[] n = neighbour(col, row, d);
			if (gp.pathFinder.inBounds(n[0], n[1]) && map[n[0]][n[1]] >= 0 && map[n[0]][n[1]] < here) {
				direction = d;
				return true;
			}
		}
		return false;
	}

	// Step to the neighbouring tile that is furthest away (away from the map's origin).
	boolean stepUphill(int[][] map, int col, int row) {
		int best = map[col][row];
		String bestDir = null;
		for (String d : new String[] {"up", "down", "left", "right"}) {
			int[] n = neighbour(col, row, d);
			if (gp.pathFinder.inBounds(n[0], n[1]) && map[n[0]][n[1]] > best) {
				best = map[n[0]][n[1]];
				bestDir = d;
			}
		}
		if (bestDir == null) {
			return false;
		}
		direction = bestDir;
		return true;
	}

	void faceTowardPlayer(int col, int row) {
		int dc = gp.pathFinder.playerCol - col, dr = gp.pathFinder.playerRow - row;
		if (Math.abs(dc) > Math.abs(dr)) {
			direction = dc > 0 ? "right" : "left";
		}
		else if (dr != 0) {
			direction = dr > 0 ? "down" : "up";
		}
	}

	static int[] neighbour(int col, int row, String d) {
		switch (d) {
		case "up":   return new int[] {col, row - 1};
		case "down": return new int[] {col, row + 1};
		case "left": return new int[] {col - 1, row};
		default:     return new int[] {col + 1, row};
		}
	}

	static String opposite(String d) {
		switch (d) {
		case "up":   return "down";
		case "down": return "up";
		case "left": return "right";
		default:     return "left";
		}
	}

	public void update() {
		int oldX = worldX, oldY = worldY;
		super.update();

		// blocked between two tiles (e.g. by another monster) for a while: turn back
		boolean betweenTiles = worldX % gp.tileSize != 0 || worldY % gp.tileSize != 0;
		if (betweenTiles && oldX == worldX && oldY == worldY && speed > 0) {
			stuckFrames++;
			if (stuckFrames > 40) {
				direction = opposite(direction);
				stuckFrames = 0;
			}
		}
		else {
			stuckFrames = 0;
		}
	}

	// Make any monster a guard: it stays within a few tiles of where it spawned (e.g. next to a
	// health coin), chases intruders, and walks back to its post if lured more than 5 tiles away.
	public void guardPost() {
		guardsHome = true;
		leashRange = 5;
		patrolRadius = 2; // stays right by the coin
	}

	// Getting hit makes a monster back off for a few seconds, then come back for you
	// (it remembers you, and a guard stops walking home).
	public void damageReaction() {
		memory = MEMORY_FRAMES * 2;
		returningHome = false;
		pauseFrames = 0;
		retreatFrames = retreatTime;

		// if it's between tiles and heading at the player, turn round right away
		// (reversing keeps it on its corridor; other turns wait for the next tile)
		boolean betweenTiles = worldX % gp.tileSize != 0 || worldY % gp.tileSize != 0;
		if (betweenTiles) {
			String toward;
			if (direction.equals("left") || direction.equals("right")) {
				toward = gp.player.worldX > worldX ? "right" : "left";
			}
			else {
				toward = gp.player.worldY > worldY ? "down" : "up";
			}
			if (direction.equals(toward)) {
				direction = opposite(direction);
			}
		}
	}
}
