package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public boolean upPressed, downPressed, rightPressed, leftPressed, enterPressed, shootPressed;
    
    GamePanel gp;
    
    public KeyHandler(GamePanel gp) {
    	this.gp = gp;
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
        	enterHeld = true;
        }
        
        //VICTORY SCREEN
        if (gp.gameState == gp.victoryState) {
        	victoryState(code);
        }
        
        //TITLE STATE
        else if(gp.gameState == gp.titleState) {
        	titleState(code);
        }
        
        //PLAY STATE
        else if (gp.gameState == gp.playState) {
        	playState(code);
        }
        
        //PAUSE STATE
        else if (gp.gameState == gp.pauseState) {
        	pauseState(code);
        }
        
        //DIALOGUE STAGE
        else if (gp.gameState == gp.dialogueState) {
        	dialogueState(code);
        }
        
        //CHARACTER STATUS STAGE
        else if (gp.gameState == gp.characterState) {
        	characterState(code);
        }
        
        //GAME OVER STAGE
        else if (gp.gameState == gp.gameOverState) {
        	gameOverState(code);
        }
    }
    
    //GAME OVER INPUT GUARD
    // Enter is also the attack key, so a player hammering/holding Enter when they die would instantly
    // pick an option (keyboard auto-repeat). The menu ignores input for a moment, and Enter only counts
    // once it has been let go and pressed again.
    boolean enterHeld = false;          // is the Enter key physically down right now?
    boolean gameOverEnterArmed = false; // has Enter been released since the Game Over screen opened?
    long gameOverOpenedAt = 0;
    static final long GAME_OVER_INPUT_DELAY_MS = 700;
    
    public void gameOverOpened() {
    	gameOverOpenedAt = System.currentTimeMillis();
    	gameOverEnterArmed = !enterHeld;
    	enterPressed = false;
    }
    
    // Let go of every key (used when starting a brand-new game).
    public void releaseAll() {
    	upPressed = downPressed = leftPressed = rightPressed = enterPressed = shootPressed = false;
    	gameOverEnterArmed = false;
    	gameOverOpenedAt = 0;
    }
    
    // Victory screen: Enter (after the same short guard as Game Over) starts a brand-new game.
    public void victoryState(int code) {
    	if (System.currentTimeMillis() - gameOverOpenedAt < GAME_OVER_INPUT_DELAY_MS || !gameOverEnterArmed) {
    		return;
    	}
    	if (code == KeyEvent.VK_ENTER) {
    		gp.restart(); // back to the title screen, everything as at the very beginning
    	}
    }
    
    public void gameOverState(int code) {
        	if (System.currentTimeMillis() - gameOverOpenedAt < GAME_OVER_INPUT_DELAY_MS) {
        		return; // give the player a moment to see the screen
        	}
        	if (code == KeyEvent.VK_ENTER && !gameOverEnterArmed) {
        		return; // this Enter was already held down when they died (auto-repeat)
        	}
        	if (code == KeyEvent.VK_UP){
                gp.user.cursor --;
                if (gp.user.cursor < 0) {
                	gp.user.cursor = 1;
                }
             }

             if (code == KeyEvent.VK_DOWN){
                gp.user.cursor ++;
                if (gp.user.cursor > 1) {
                	gp.user.cursor = 0;
                }
             }
             
             if (code == KeyEvent.VK_ENTER){                 
                 if (gp.user.cursor == 0) {
                	 gp.gameState = gp.playState;
                	 gp.retry();
                 }
                 
                 if (gp.user.cursor == 1) {
                	 gp.user.subTitleState = 0;
                     gp.gameState = gp.titleState;
                     gp.restart();
                 }
             }
    }
    
    public void titleState(int code) {
    	if(gp.user.subTitleState == 0) {
        	if (code == KeyEvent.VK_UP){
                gp.user.cursor --;
                if (gp.user.cursor < 0) {
                	gp.user.cursor = 1;
                }
             }

             if (code == KeyEvent.VK_DOWN){
                gp.user.cursor ++;
                if (gp.user.cursor > 1) {
                	gp.user.cursor = 0;
                }
             }
             
             if (code == KeyEvent.VK_ENTER){
                 if (gp.user.cursor == 0) {
                	 gp.user.subTitleState = 1;
                 }
                
                 if (gp.user.cursor == 1) {
                     System.exit(0);
                 }
             }
    	}
    	// INSTRUCTIONS / LOADING SCREEN: Enter skips straight into the game
    	else if (gp.user.subTitleState == 1 && code == KeyEvent.VK_ENTER) {
    		gp.user.skipLoading();
    	}
    }
    
    public void playState(int code) {
    	if (code == KeyEvent.VK_UP){
            upPressed = true;
         }

         if (code == KeyEvent.VK_DOWN){
            downPressed = true;
         }

         if (code == KeyEvent.VK_RIGHT){
            rightPressed = true;
         }

         if (code == KeyEvent.VK_LEFT){
            leftPressed = true;
         }
         
         if (code == KeyEvent.VK_SHIFT){
        	 gp.gameState = gp.characterState;
         }
         
         if (code == KeyEvent.VK_SPACE){
             gp.gameState = gp.pauseState;
          }
         
         if (code == KeyEvent.VK_ENTER){
             enterPressed = true;
          }
         
         if (code == KeyEvent.VK_S){
             shootPressed = true;
          }
    }
    
    public void pauseState(int code) {
    	if (code == KeyEvent.VK_SPACE){
            gp.gameState = gp.playState;
         }
    }

    public void dialogueState(int code) {
    	if (code == KeyEvent.VK_ENTER){
            gp.user.dialogueImage = null; // the next bubble starts without a picture

            // in a conversation: Enter shows the next line, and closes it after the last one
            if (gp.user.talkingTo != null && gp.user.talkingTo.nextLine()) {
            	return;
            }
            if (gp.user.talkingTo != null) {
            	gp.user.talkingTo = null;
            	gp.player.talkLocked = true; // must step away and bump the NPC again to re-talk
            }
            gp.gameState = gp.playState;
         }
    }
    
    public void characterState(int code) {
    	if (code == KeyEvent.VK_SHIFT){
            gp.gameState = gp.playState;
         }
    	
    	if (code == KeyEvent.VK_ENTER){
    		gp.player.useSelectedItem(); // e.g. use one Health Coin
    	}
    	
    	if (code == KeyEvent.VK_UP){
    		if (gp.user.slotRow != 0) {
    			gp.user.slotRow --;
    		}
    	}

         if (code == KeyEvent.VK_DOWN){
        	 if (gp.user.slotRow != 3) {
        		 gp.user.slotRow ++;
     		}
         }

         if (code == KeyEvent.VK_RIGHT){
        	 if(gp.user.slotCol != 4) {
        		 gp.user.slotCol ++;
        	 }
         }

         if (code == KeyEvent.VK_LEFT){
        	 if(gp.user.slotCol != 0) {
        		 gp.user.slotCol --;
        	 }
         }
    }

    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
        	enterHeld = false;
        	gameOverEnterArmed = true;
        }
         if (code == KeyEvent.VK_UP){
            upPressed = false;
         }

         if (code == KeyEvent.VK_DOWN){
            downPressed = false;
         }

         if (code == KeyEvent.VK_RIGHT){
            rightPressed = false;
         }

         if (code == KeyEvent.VK_LEFT){
            leftPressed = false;
         }
         
         if (code == KeyEvent.VK_S){
             shootPressed = false;
          }
    }
}