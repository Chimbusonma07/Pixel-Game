package main;

import java.awt.Color;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import entity.Entity;
import entity.Player;
import javax.swing.JPanel;
import Tile.TileManager;

//Game panel is a subclass of JPanel

// @SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable{
//SCREEN SETTINGS
    public final int originalTileSize = 16; // 16x16 tiles are standard in 2D games
    public final int scale = 3; 
    public final int tileSize = originalTileSize * scale; //Makes it 48x48 

    public final int maxScreenCol = 16; // 16 tiles column
    public final int maxScreenRow = 12; // 12 tiles in a row
    public final int maxScreenWidth = maxScreenCol * tileSize;
    public final int maxScreenHeight = maxScreenRow * tileSize;
    
//WORLD SETTINGS
    public final int MAX_WORLD_COL = 50; 
    public final int MAX_WORLD_ROW = 50; 
    public final int WORLD_WIDTH = MAX_WORLD_COL * tileSize;
    public final int WORLD_HEIGHT = MAX_WORLD_ROW * tileSize;


    //FPS
    int FPS = 60;
    
    //SYSTEM
    TileManager tileM = new TileManager(this);
    public KeyHandler key = new KeyHandler(this);
    Thread gameThread; // to enable the game run in real time, use the class 'Thread'
    public CheckCollision checker = new CheckCollision(this);
    public AssetSetter aSet = new AssetSetter(this);
    public UserInterface user = new UserInterface(this);
    public EventHandler eventH = new EventHandler(this);
    public PathFinder pathFinder = new PathFinder(this);
    public boolean touchMode = false; // phone/tablet in the browser: hints say "Tap A" (the buttons live in web/index.html)
    
    //ENTITY AND OBJECT
    public Player player = new Player(this, key);
    public Entity obj[] = new Entity[20]; // 0-4 keys, chests, doors; 5+ health coins
    public Entity npc[] = new Entity[10];
    public Entity monsters[] = new Entity[20];
    public ArrayList<Entity> projectileList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();
    
    //Game state e.g. Pause
    public int gameState;//the current state the game is in e.g. menu.
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int gameOverState = 5;

    //MAPS
    public int currentMap = 0; // 0 = WorldVer2, 1 = maze (dungeon.txt), 2 = wizard's house (World01.txt)

    //SET PLAYER'S POSITIONS
    public GamePanel(){
        this.setPreferredSize(new Dimension (maxScreenWidth, maxScreenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //Makes game rendering better
        this.addKeyListener(key);
        this.setFocusable(true);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
        	public void mousePressed(java.awt.event.MouseEvent e) {
        		requestFocusInWindow(); // clicking the game gives it the keyboard
        	}
        });
        }
    
    public void setupGame() {
    	aSet.setObjects();
    	aSet.setNPC();
    	aSet.setMonsters();
    	gameState = titleState;
    }
    
    // The player has died: show GAME OVER with RETRY selected. KeyHandler ignores input for a moment
    // (and any Enter that was already held down) so an attack key-press can't pick an option by accident.
    public void gameOver() {
    	gameState = gameOverState;
    	user.cursor = 0;
    	key.gameOverOpened();
    }

    // RETRY: carry on from where you died. Same map and spot, same level/XP/items/keys/coins, and the
    // world stays as it was (killed monsters stay dead, collected things stay collected, so nothing can
    // be farmed). Full life, plus a few seconds of invincibility to get away from whatever killed you.
    public void retry() {
    	player.setLife();
    	player.respawnShield = Player.RESPAWN_SHIELD_FRAMES;
    	player.stopAttacking();
    	projectileList.clear();
    	if (player.projectile != null) player.projectile.alive = false;
    	gameState = playState;
    }

    // RESTART: a brand-new game, exactly as when the game is first launched. Rebuilding the player,
    // interface, events and world from scratch guarantees nothing carries over (stats, items, keys,
    // coins, flags, menu cursors, messages...).
    public void restart() {
    	key.releaseAll();
    	player = new Player(this, key);
    	user = new UserInterface(this);
    	eventH = new EventHandler(this);
    	aSet = new AssetSetter(this);
    	pathFinder = new PathFinder(this);
    	currentMap = 0;
    	tileM.loadMap("/Maps/WorldVer2.txt");
    	clearEntities();
    	setupGame(); // objects, NPCs, monsters, and back to the title screen
    }
    
    public void changeMap(int map) {
    	currentMap = map;
    	if (map == 1) {
    		// pulled in by the first chest: land in the centre of the maze
    		respawnInMaze();
    		user.showDialogue("Find the wizard!", user.doorImage);
    	}
    	else if (map == 2) {
    		// through the maze door into the wizard's house
    		tileM.loadMap("/Maps/World01.txt");
    		clearEntities();
    		aSet.setHouse();
    		player.worldX = tileSize * AssetSetter.HOUSE_DOOR_COL; // just inside the entrance
    		player.worldY = tileSize * (AssetSetter.HOUSE_DOOR_ROW - 1);
    		player.direction = "up";
    	}
    	else {
    		tileM.loadMap("/Maps/WorldVer2.txt");
    	}
    }

    // Leave the house through the entrance and come back out next to the maze door.
    public void exitHouse() {
    	loadMaze();
    	player.worldX = tileSize * AssetSetter.MAZE_DOOR_EXIT_COL;
    	player.worldY = tileSize * AssetSetter.MAZE_DOOR_EXIT_ROW;
    	player.direction = "left"; // facing away from the door
    	eventH.doorHintShown = true; // they already know where the door is
    }

    public void respawnInMaze() {
    	loadMaze();
    	player.worldX = tileSize * 24; // centre of the maze
    	player.worldY = tileSize * 24;
    	player.direction = "down";
    }

    // Loads the maze map with a fresh set of monsters, the door and the Grand Treasure.
    private void loadMaze() {
    	currentMap = 1;
    	tileM.loadMap("/Maps/dungeon.txt");
    	clearEntities();
    	aSet.setMazeObjects();
    	aSet.setMazeMonsters();
    	eventH.doorHintShown = false;
    }

    private void clearEntities() {
    	java.util.Arrays.fill(obj, null);
    	java.util.Arrays.fill(npc, null);
    	java.util.Arrays.fill(monsters, null);
    	projectileList.clear();
    }

    public void startGameThread() {
        gameThread = new Thread(this); //this refers to the class 'GamePanel'
        gameThread.start();
    }

    public void run (){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;

        //Creating a game loop
        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            timer+=(currentTime - lastTime);
            lastTime = currentTime;
            
            if(delta >= 1){
                update();
                repaint();
                delta--;
            }

            if (timer >= 1000000000){
                timer = 0;
            }

            // Rest until the next frame is due instead of spinning. Without this the loop never lets
            // go of the CPU: on desktop that's one core at 100%, and in the browser (where Java threads
            // share the page's main thread) the page would freeze and never draw anything.
            long sleepMs = (long) ((1 - delta) * drawInterval / 1000000);
            try {
                Thread.sleep(Math.max(1, sleepMs));
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    public void update(){
    	if (gameState == playState) {
    		
    		//PLAYER
    		player.update();
    		
    		//NPC
    		for (int i = 0; i < npc.length; i++) {
            	if(npc[i] != null) {
            		npc[i].update();
            	}
            }
    		
    		//MONSTERS
    		pathFinder.update(); // fresh "distance to player" map for the monster AI
    		for (int i = 0; i < monsters.length; i++) {
    			if (monsters[i] != null) {
    				if(monsters[i].alive == true && monsters[i].dying == false) {
                		monsters[i].update();
                	}
        			if(monsters[i].alive == false) {
                		monsters[i] = null;
                	}
    			}
            }
    		
    		//PROJECTILE
    		for (int i = 0; i < projectileList.size(); i++) {
    			if (projectileList.get(i) != null) {
    				if(projectileList.get(i).alive == true) {
    					projectileList.get(i).update();
                	}
        			if(projectileList.get(i).alive == false) {
        				projectileList.remove(i);
                	}
    			}
            }
    	}
    	
    	if (gameState == pauseState) {
    		//nothing happens
    	}
    }

    //SCALING: the game is always drawn at 768 x 576 into `screen`, then scaled to fit the window
    //(a phone, a browser tab or the desktop window) keeping its shape, with black bars if needed.
    BufferedImage screen;
    double viewScale = 1;
    int offsetX = 0, offsetY = 0;
    
    public void paintComponent(Graphics g){
        super.paintComponent(g); // 'super' refers to the parent class which is JPanel (fills black)
        if (screen == null) {
        	screen = new BufferedImage(maxScreenWidth, maxScreenHeight, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D sg = screen.createGraphics();
        sg.setColor(Color.black);
        sg.fillRect(0, 0, maxScreenWidth, maxScreenHeight);
        drawGame(sg);
        sg.dispose();
        
        int w = getWidth() > 0 ? getWidth() : maxScreenWidth;
        int h = getHeight() > 0 ? getHeight() : maxScreenHeight;
        viewScale = Math.min(w / (double) maxScreenWidth, h / (double) maxScreenHeight);
        int drawW = (int) Math.round(maxScreenWidth * viewScale);
        int drawH = (int) Math.round(maxScreenHeight * viewScale);
        offsetX = (w - drawW) / 2;
        offsetY = (h - drawH) / 2;
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, viewScale >= 1
        		? RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR   // keep pixel art crisp when enlarging
        		: RenderingHints.VALUE_INTERPOLATION_BILINEAR);         // smooth when shrinking for small screens
        g2.drawImage(screen, offsetX, offsetY, drawW, drawH, null);
    }
    
    private void drawGame(Graphics2D g2){
        //TITLE SCREEN
        if (gameState == titleState) {
        	user.draw(g2);
        }
        
        //EVERYTHING ELSE
        else {
        	//TILE
            tileM.draw(g2);
            
            
            //ENTITY ARRAY LIST
            entityList.add(player);
            
            for(int i = 0; i < obj.length; i++) {
            	if(obj[i] != null) {
            		entityList.add(obj[i]);
//            		System.out.println("Not null!");
            	}
            }
            
            for(int i = 0; i < npc.length; i++) {
            	if(npc[i] != null) {
            		entityList.add(npc[i]);
            	}
            }
            
            for(int i = 0; i < monsters.length; i++) {
            	if(monsters[i] != null) {
            		entityList.add(monsters[i]);
            	}
            }
            
            for(int i = 0; i < projectileList.size(); i++) {
            	if(projectileList.get(i) != null) {
            		entityList.add(projectileList.get(i));
            	}
            }
            
            //ARRANGE FROM SMALLEST WORLD Y TO LARGEST WORLD Y
            Collections.sort(entityList, new Comparator<Entity>(){

				@Override
				public int compare(Entity o1, Entity o2) {
					int result = Integer.compare(o1.worldY, o2.worldY);
					return result;
				}	
            });
            
            //DRAW ENTITIES
            for(int i = 0; i < entityList.size(); i++) {
            	entityList.get(i).draw(g2);
//            	System.out.println(entityList.get(i));
            }
            
            //TO PREVENT THE GAME FROM LAGGING, WE AVOID THE ENTITLY LIST GETTING TOO LARGE AND EMPTY IT
            entityList.clear();           
            
            //UI
            user.draw(g2);
        }
    }
    
  }