package main;

import java.awt.BasicStroke;

import entity.Entity;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import OBJ.OBJHealthCoin;
import OBJ.OBJLife;

public class UserInterface {
	BufferedImage image, heart_full, heart_half, heart_empty;
	GamePanel gp;
	Graphics2D g2;
	Font maruMonica;
	public boolean messageOn = false;
	public boolean gameEnd = false;
	public ArrayList<String> message = new ArrayList<>();
	public ArrayList<Integer> messageCounter = new ArrayList<>();
	public String currentDialogue = "";
	public BufferedImage dialogueImage; // optional picture shown on the left of the dialogue bubble
	public BufferedImage doorImage;
	public Entity talkingTo; // NPC whose conversation is on screen (null for one-off messages)
	String images[] = new String[11];
	int imageChangeIndex = 0;
	int imageChangeCounter = 0;
	int cursor = 0;
	int subTitleState = 0; //The number indicates the sub window you're in
	String loading[] = new String[6];
	int loadingIndex = 0;
	int loadingCounter = 0;
	// 5 loading-bar images x 480 frames at 60 FPS = 40 seconds on the instructions screen
	static final int LOADING_FRAMES_PER_STEP = 480;
	public int slotCol = 0;
	public int slotRow = 0;
	
	
	public UserInterface(GamePanel gp) {
		this.gp = gp;
		setImageChanger();
		setLoadingBarImage();
		
		try {
			InputStream is = getClass().getResourceAsStream("/font/Retro Gaming.ttf");
			maruMonica = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			doorImage = ImageIO.read(getClass().getResourceAsStream("/objects/door.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Entity heart = new OBJLife(gp);
		heart_full = heart.image;
		heart_half = heart.image2;
		heart_empty = heart.image3;
	}
	
	// Opens a dialogue bubble, with an optional picture (null for text only).
	public void showDialogue(String text, BufferedImage picture) {
		currentDialogue = text;
		dialogueImage = picture;
		gp.gameState = gp.dialogueState;
	}

	public void addMessage(String text) {
		message.add(text);
		messageCounter.add(0);
	} 
	
	public void setImageChanger() {
		images[0] = "/objects/key";
		images[1] = "/Tiles/earth";
		images[2] = "/Tiles/brick";
		images[3] = "/objects/door";
		images[4] = "/Tiles/tree";
		images[5] = "/Tiles/road00";
		images[6] = "/objects/treasure chest";
		images[7] = "/Tiles/water00";
	}

	public void setLoadingBarImage() {
		loading[0] = "/loadingBar/loading bar 0";
		loading[1] = "/loadingBar/loading bar 25";
		loading[2] = "/loadingBar/loading bar 50";
		loading[3] = "/loadingBar/loading bar 75";
		loading[4] = "/loadingBar/loading bar 100";
	}
	
	public void draw(Graphics2D g2) {
		if(gameEnd == true) {
			this.g2 = g2;
			g2.setFont(maruMonica);
			g2.setColor(new Color(0, 0, 0, 170));
			g2.fillRect(0, 0, gp.maxScreenWidth, gp.maxScreenHeight);

			int x;
			int y;
			String text;

			//TREASURE PICTURE
			int size = gp.tileSize * 3;
			y = gp.tileSize;
			g2.drawImage(gp.player.setup("/objects/treasure chest", gp.tileSize, gp.tileSize),
					gp.maxScreenWidth/2 - size/2, y, size, size, null);

			//TITLE
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 56F));
			text = "CONGRATULATIONS!";
			x = getXForCenteredText(text);
			y += size + gp.tileSize * 2;
			g2.setColor(Color.black);
			g2.drawString(text, x + 5, y + 5);
			g2.setColor(Color.yellow);
			g2.drawString(text, x, y);

			//SUBTITLES
			g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
			g2.setColor(Color.white);
			text = "You opened the Grand Treasure!";
			x = getXForCenteredText(text);
			y += gp.tileSize * 1.5;
			g2.drawString(text, x, y);

			text = "Level " + gp.player.level + " hero - you win!";
			x = getXForCenteredText(text);
			y += gp.tileSize;
			g2.drawString(text, x, y);

			gp.gameThread = null;
		}
		else {
			this.g2 = g2;	
			g2.setFont(maruMonica);	
			g2.setColor(Color.white);
			
			//Title
			if (gp.gameState == gp.titleState) {
				titleScreen();
			}
			
			//Play
			if (gp.gameState == gp.playState) {
				playerLife();
				drawMessage();
			}
			
			//Pause
			if (gp.gameState == gp.pauseState) {
				playerLife();
				pauseScreen();
			}
			
			//Dialogue
			if (gp.gameState == gp.dialogueState) {
				playerLife();
				dialogueScreen();
			}
			
			//Character Status
			if (gp.gameState == gp.characterState) {
				characterScreen();
				inventoryScreen();
			}
			
			//Game Over
			if (gp.gameState == gp.gameOverState) {
				gameOverScreen();
			}
		}
	}
	
	public void gameOverScreen() {
		g2.setColor(new Color(0, 0, 0, 140));
		g2.fillRect(0, 0, gp.maxScreenWidth, gp.maxScreenHeight);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 65F));
		int x;
		int y;
		String text; 
		text = "GAME OVER";
		x = getXForCenteredText(text);
		y = gp.tileSize * 3;
		g2.setColor(Color.black);
		g2.drawString(text, x + 5, y + 5);
		g2.setColor(Color.white);
		g2.drawString(text, x, y);
		
		//RETRY OPTION
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
		
		String menu = "RETRY";
		x = getXForCenteredText(menu);
		y += gp.tileSize * 4;
		g2.drawString(menu, x, y);
		if (cursor == 0) {
			g2.drawString(">", x - gp.tileSize/2, y);
		}
		
		menu = "RESTART";
		x = getXForCenteredText(menu);
		y += gp.tileSize;
		g2.drawString(menu, x, y);
		if (cursor == 1) {
			g2.drawString(">", x - gp.tileSize/2, y);
		}
	}
	
	public void playerLife() {
		int x = gp.tileSize/2;
		int y = gp.tileSize/2;
		int i = 0;
		
		while(i < gp.player.maxLife/2) {
			g2.drawImage(heart_empty, x, y, null);
			x+= gp.tileSize;
			i ++;
		}
		
		//RESET THEM
		x = gp.tileSize/2;
		y = gp.tileSize/2;
		i = 0;

		while(i < gp.player.life) {
			g2.drawImage(heart_half, x, y, null);
			i++;
			if (i < gp.player.life) {
				g2.drawImage(heart_full, x, y, null);
			}
			x+= gp.tileSize;
			i ++;
		}	
	}
	
	public void drawMessage() {
		int messageX = gp.tileSize;
		int messageY = gp.tileSize * 5;
		
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25f));
//		g2.setColor(Color.white);
		
		for(int i = 0; i < message.size(); i++) {
			if (message.get(i) != null) {
				g2.setColor(Color.black);
				g2.drawString(message.get(i), messageX + 3, messageY + 3);
				
				g2.setColor(Color.white);
				g2.drawString(message.get(i), messageX, messageY);
				
				messageY += 50;
				int counter = messageCounter.get(i) + 1;
				messageCounter.set(i, counter);
				
				if(messageCounter.get(i) > 180) {
					message.remove(i);
					messageCounter.remove(i);
				}
			}
		}
	}
	
	public void titleScreen() {
		if (subTitleState == 0) {
			//TITLE NAME
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 65F));
			String title = "Adventure Game";
			int x = getXForCenteredText(title);
			int y = gp.tileSize * 2;
			
			//SHADOW
			g2.setColor(Color.gray);
			g2.drawString(title, x+5, y+5);
			
			//TITLE COLOR
			g2.setColor(Color.white);
			g2.drawString(title, x, y);
					
			//IMAGE CHANGE
			x = gp.maxScreenWidth/2 - gp.tileSize;
			y+= gp.tileSize + (gp.tileSize/2);
			
			if (images[imageChangeIndex] == null) {
				imageChangeIndex = 0;
			}
				
			g2.drawImage(gp.player.setup(images[imageChangeIndex], gp.tileSize, gp.tileSize), x, y, gp.tileSize*2, gp.tileSize*2, null);
			
			imageChangeCounter ++;
			if (imageChangeCounter == 80) {
				imageChangeIndex ++;
				imageChangeCounter = 0;
			}
				
			//MENU
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 25F));
			
			String menu0 = "Press Enter to Continue";
			x = getXForCenteredText(menu0);
			y += gp.tileSize * 4;
			g2.drawString(menu0, x, y);
					
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 32F));
					
			String menu1 = "NEW GAME";
			x = getXForCenteredText(menu1);
			y += gp.tileSize;
			g2.drawString(menu1, x, y);
			if (cursor == 0) {
				g2.drawString(">", x - gp.tileSize/2, y);
			}
			
			String menu2 = "QUIT";
			x = getXForCenteredText(menu2);
			y += gp.tileSize;
			g2.drawString(menu2, x, y);
			if (cursor == 1) {
				g2.drawString(">", x - gp.tileSize/2, y);
			}
		}
		
		else if (subTitleState == 1) {
			//SET INSTRUCTIONS
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
			String title = "Game Instructions";
			int x = getXForCenteredText(title);
			int y = gp.tileSize;
			
			//SHADOW
			g2.setColor(Color.gray);
			g2.drawString(title, x+5, y+5);
			
			//COLOR
			g2.setColor(Color.white);
			g2.drawString(title, x, y);
			
			//INSTRUCTIONS
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 20F));
			
			String text1 = "> Use the arrow keys for movement";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text1, x, y);
			
			String text2 = "> Walk into NPCs to talk to them, press Enter for";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text2, x, y);
			
			String text3 = "the next line";
			x = gp.tileSize/2;
			y += gp.tileSize/2;
			g2.drawString(text3, x, y);
			
			String text4 = "> Use the space bar to pause the game";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text4, x, y);
			
			String text5 = "> Press Enter to close dialogue or notifications";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text5, x, y);
			
			String text6 = "> Press Enter to activate weapons";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text6, x, y);
			
			String text7 = "> Shift: stats & inventory (Enter uses a Health Coin)";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text7, x, y);
			
			String text8 = "> Press 'S' to shoot fireballs";
			x = gp.tileSize/2;
			y += gp.tileSize;
			g2.drawString(text8, x, y);

			//LOADING BAR
			x = (gp.maxScreenWidth - gp.tileSize*15) + gp.tileSize * 2;
			y = (gp.maxScreenHeight - gp.tileSize*3) - gp.tileSize;
			
			if (loading[loadingIndex] == null) {
				gp.gameState = gp.playState;
				loadingIndex = 0;
			}
			
			g2.drawImage(gp.player.setup(loading[loadingIndex], gp.tileSize, gp.tileSize), x, y, gp.tileSize*10, gp.tileSize*6, null);
			
			loadingCounter ++;
			if (loadingCounter == LOADING_FRAMES_PER_STEP) {
				loadingIndex ++;
				loadingCounter = 0;
			}
		}
	}
		
	public void pauseScreen() {
		int x = 30;
		int y = 30;
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/objects/pause symbol.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g2.drawImage(image, x, y, gp.tileSize * 3, gp.tileSize * 3, null);
	}
	
	public void dialogueScreen() {

		int x = gp.tileSize;
		int y = gp.tileSize/2;
		int width = gp.maxScreenWidth - (gp.tileSize * 2);
		int height = gp.tileSize * 4;
		
		subWindow(x, y, width, height);
		
		int characterLimit = 30;

		//OPTIONAL PICTURE ON THE LEFT (e.g. the door), TEXT MOVES RIGHT TO MAKE ROOM
		if (dialogueImage != null) {
			int size = gp.tileSize * 2;
			g2.drawImage(dialogueImage, x + gp.tileSize/2, y + (height - size)/2, size, size, null);
			x += size + gp.tileSize/2;
			characterLimit = 24;
		}

		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 25F));
		x += (gp.tileSize);
		y += gp.tileSize;

		//AUTOMATICALLY SPLIT DIALOGUE BASED ON CHARACTER LIMIT
        int characterCount = 0;
        StringBuilder line = new StringBuilder();

        for(String word : currentDialogue.split(" ")) {
            characterCount += word.length();
            
            if(characterCount < characterLimit){
                line.append(word).append(" ");
            } 
            
            else if(characterCount!=characterLimit) {
                g2.drawString(line.toString(), x, y);
                y += 38;
                characterCount = word.length();
                line = new StringBuilder(word + " ");
            } 
            
            else {
                line.append(word).append(" ");
                g2.drawString(line.toString(), x, y);
                y += 38;
                characterCount = 0;
                line = new StringBuilder();
            }
        }
        if(!line.equals(new StringBuilder())) {
            g2.drawString(line.toString(), x, y);
        }		
	}
	
	public void characterScreen() {
		//DRAW CHARACTER STATUS SCREEN
		final int frameX = gp.tileSize * 9;
		final int frameY = gp.tileSize;
		final int frameWidth = gp.tileSize * 5;
		final int frameHeight = gp.tileSize * 8;
		subWindow(frameX, frameY, frameWidth, frameHeight);
		
		//DRAW TEXT
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(20F));
		
		int textX = frameX + 20;
		int textY = frameY + 40;
		final int lineDistance = 50;//Distance between the lines.
		
		g2.drawString("Level", textX, textY);
		textY += lineDistance;
		g2.drawString("Life", textX, textY);
		textY += lineDistance;
		g2.drawString("Strength", textX, textY);
		textY += lineDistance;
		g2.drawString("Attack", textX, textY);
		textY += lineDistance;
		g2.drawString("Defense", textX, textY);
		textY += lineDistance;
		g2.drawString("Exp", textX, textY);

		//DISPLAY VALUES OF CHARACTER ATTRIBUTES
		String value;
		textY = frameY + 40;
		int lineText = (frameX + frameWidth) - 30;
		
		value = String.valueOf(gp.player.level);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
		
		value = String.valueOf(gp.player.life + "/" + gp.player.maxLife);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
		
		value = String.valueOf(gp.player.strength);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
		
		value = String.valueOf(gp.player.attack);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
		
		value = String.valueOf(gp.player.defense);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
		
		value = String.valueOf(gp.player.exp);
		textX = getXForRightAlignedText(value, lineText);//returns the x of the text
		g2.drawString(value, textX, textY);
		textY += lineDistance;
	}
	
	public void inventoryScreen() {
		//DRAW INVENTORY SCREEN
		int frameX = gp.tileSize;
		int frameY = gp.tileSize;
		int frameWidth = gp.tileSize * 6;
		int frameHeight = gp.tileSize * 5;
		subWindow(frameX, frameY, frameWidth, frameHeight);
		
		//SLOTS IN INVENTORY SCREEN
		final int slotXStart = frameX + 20;
		final int slotYStart = frameY + 20;
		int slotX = slotXStart;
		int slotY = slotYStart;
		int slotSize = gp.tileSize + 3;
		
		//DRAW ITEMS
		for(int i = 0; i < gp.player.inventory.size(); i++) {
			g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);

			//STACK COUNT on the Health Coin slot
			if (OBJHealthCoin.NAME.equals(gp.player.inventory.get(i).name)) {
				String count = "x" + gp.player.healthCoins;
				g2.setFont(g2.getFont().deriveFont(Font.BOLD, 18F));
				int countX = slotX + gp.tileSize - (int) g2.getFontMetrics().getStringBounds(count, g2).getWidth();
				int countY = slotY + gp.tileSize - 2;
				g2.setColor(Color.black);
				g2.drawString(count, countX + 2, countY + 2);
				g2.setColor(Color.white);
				g2.drawString(count, countX, countY);
			}
			slotX += slotSize;
			
			if(i == 4 || i == 9 || i == 14) {
				slotX = slotXStart;
				slotY += slotSize;
			}
		}
		
		//DRAW CURSOR IN INVENTORY
		int cursorX = slotXStart + (slotSize * slotCol);
		int cursorY = slotYStart + (slotSize * slotRow);
		int cursorWidth = gp.tileSize;
		int cursorHeight = gp.tileSize;
		
		g2.setStroke(new BasicStroke(3));
		
		g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 20, 20);
		
		//DRAW DESCRIPTION BOX
		int dFrameX = frameX;
		int dFrameY = frameY + frameHeight;
		int dFrameWidth = frameWidth;
		int dFrameHeight = gp.tileSize * 3;
		
		
		//DRAW TEXT IN DESCRIPTION BOX
		int textX = dFrameX + 20;
		int textY = dFrameY + gp.tileSize;
		int itemIndex = getIndexforSlot();
		g2.setFont(g2.getFont().deriveFont(16F));
		
		if(itemIndex < gp.player.inventory.size()) {
			subWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);
			Entity item = gp.player.inventory.get(itemIndex);
			String description = item.description;
			if (OBJHealthCoin.NAME.equals(item.name)) {
				description = "[" + item.name + "] x" + gp.player.healthCoins
						+ "\nRestores 1 heart.\nPress Enter to use one.";
			}
			for(String line: description.split("\n")) {
				g2.drawString(line, textX, textY);
				textY += 32;
			}
		}

		//NOTICE after pressing Enter (e.g. "Life restored! 2 coins left"), shown for 2 seconds
		if (inventoryNoticeCounter > 0) {
			inventoryNoticeCounter--;
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 22F));
			int noticeY = dFrameY + dFrameHeight + gp.tileSize/2 + 8;
			g2.setColor(Color.black);
			g2.drawString(inventoryNotice, frameX + 2, noticeY + 2);
			g2.setColor(Color.white);
			g2.drawString(inventoryNotice, frameX, noticeY);
		}
	}

	String inventoryNotice = "";
	int inventoryNoticeCounter = 0;

	public void showInventoryNotice(String text) {
		inventoryNotice = text;
		inventoryNoticeCounter = 120;
	}
	
	public int getIndexforSlot() {
		int Index = slotCol + (slotRow*5);
		return Index;
	}
	
	public void subWindow(int x, int y, int width, int height) {
		Color color = new Color(0, 0, 0, 210); //the fourth number increases transparency from 255 to 0
		g2.setColor(color);
		g2.fillRoundRect(x, y, width, height, 30, 30);
		
		color = new Color(255, 255, 255);
		g2.setColor(color);
		g2.setStroke(new BasicStroke(5));//Defines the width of the outline of graphics in Graphics2D
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 20, 20);
	}
	
	public int getXForCenteredText(String text) {
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.maxScreenWidth/2 - length/2;
		return x;
	}
	
    public int getXForRightAlignedText(String text, int number) {
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = number - length;
		return x;
	} 
}
