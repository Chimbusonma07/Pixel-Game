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
        
        //TITLE STATE
        if(gp.gameState == gp.titleState) {
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
    
    public void gameOverState(int code) {
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