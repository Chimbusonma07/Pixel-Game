package main;

import java.awt.Color;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    AssetSetter aSet = new AssetSetter(this);
    public UserInterface user = new UserInterface(this);
    public EventHandler eventH = new EventHandler(this);
    
    //ENTITY AND OBJECT
    public Player player = new Player(this, key);
    public Entity obj[] = new Entity[10];
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

    //SET PLAYER'S POSITIONS
    public GamePanel(){
        this.setPreferredSize(new Dimension (maxScreenWidth, maxScreenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); //Makes game rendering better
        this.addKeyListener(key);
        this.setFocusable(true);
        }
    
    public void setupGame() {
    	aSet.setObjects();
    	aSet.setNPC();
    	aSet.setMonsters();
    	gameState = titleState;
    }
    
    public void retry() {
    	player.setDefaultPosition();
    	player.setLife();
    	aSet.setNPC();
    	aSet.setMonsters();
    }
    
    public void restart() {
    	player.SetDefaultValues();
    	player.setDefaultPosition();
    	player.setLife();
    	player.setInventory();
    	aSet.setObjects();
    	aSet.setNPC();
    	aSet.setMonsters();
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

    public void paintComponent(Graphics g){
        super.paintComponent(g); // 'super' refers to the parent class which is JPanel
        Graphics2D g2 = (Graphics2D)g;
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
        
        g2.dispose();
    }
    
  }