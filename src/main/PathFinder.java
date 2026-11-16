package main;

import java.util.ArrayDeque;

import entity.Entity;

/**
 * Grid navigation for monsters.
 * Every frame it builds a distance map from the player's tile (breadth-first search over walkable
 * tiles), so any monster can step "downhill" to chase the player or "uphill" to run away.
 * It also answers line-of-sight questions so monsters can't see through walls or trees.
 */
public class PathFinder {
	GamePanel gp;

	public int[][] toPlayer; // [col][row] steps to the player's tile, -1 = unreachable
	public int playerCol, playerRow;

	public PathFinder(GamePanel gp) {
		this.gp = gp;
		toPlayer = new int[gp.MAX_WORLD_COL][gp.MAX_WORLD_ROW];
	}

	public void update() {
		// tile under the centre of the player's hitbox
		playerCol = (gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width/2) / gp.tileSize;
		playerRow = (gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height/2) / gp.tileSize;
		fillDistances(toPlayer, playerCol, playerRow);
	}

	public int[][] distanceMapFrom(int col, int row) {
		int[][] map = new int[gp.MAX_WORLD_COL][gp.MAX_WORLD_ROW];
		fillDistances(map, col, row);
		return map;
	}

	private void fillDistances(int[][] map, int startCol, int startRow) {
		for (int[] column : map) {
			java.util.Arrays.fill(column, -1);
		}
		if (!inBounds(startCol, startRow)) {
			return;
		}
		ArrayDeque<int[]> queue = new ArrayDeque<>();
		map[startCol][startRow] = 0;
		queue.add(new int[] {startCol, startRow});
		while (!queue.isEmpty()) {
			int[] p = queue.poll();
			for (int[] d : DIRS) {
				int c = p[0] + d[0], r = p[1] + d[1];
				if (walkable(c, r) && map[c][r] == -1) {
					map[c][r] = map[p[0]][p[1]] + 1;
					queue.add(new int[] {c, r});
				}
			}
		}
	}

	static final int[][] DIRS = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // up, down, left, right

	public boolean inBounds(int col, int row) {
		return col >= 0 && row >= 0 && col < gp.MAX_WORLD_COL && row < gp.MAX_WORLD_ROW;
	}

	// A tile a monster can stand on: no solid tile and no solid object (door, chest) on it.
	public boolean walkable(int col, int row) {
		if (!inBounds(col, row) || gp.tileM.tile[gp.tileM.mapTileNum[col][row]].collision) {
			return false;
		}
		for (Entity o : gp.obj) {
			if (o != null && o.collision && o.worldX / gp.tileSize == col && o.worldY / gp.tileSize == row) {
				return false;
			}
		}
		return true;
	}

	// True if the monster is within `range` tiles of the player and no solid tile is in between.
	public boolean canSeePlayer(Entity monster, int range) {
		int x1 = monster.worldX + gp.tileSize/2, y1 = monster.worldY + gp.tileSize/2;
		int x2 = gp.player.worldX + gp.tileSize/2, y2 = gp.player.worldY + gp.tileSize/2;
		double length = Math.hypot(x2 - x1, y2 - y1);
		if (length > range * gp.tileSize) {
			return false;
		}
		int steps = (int) (length / 8); // sample the line every 8 pixels
		for (int i = 1; i < steps; i++) {
			int x = x1 + (x2 - x1) * i / steps;
			int y = y1 + (y2 - y1) * i / steps;
			int col = x / gp.tileSize, row = y / gp.tileSize;
			if (!inBounds(col, row) || gp.tileM.tile[gp.tileM.mapTileNum[col][row]].collision) {
				return false;
			}
		}
		return true;
	}
}
