package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class UtilityTools {
	
	public BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
		
		BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());//creates a blank canvas
		Graphics2D g2 = scaledImage.createGraphics();//adds whatever is drawn to scaledImage
		g2.drawImage(originalImage, 0, 0, width, height, null);//draws the image with specified width and height
		g2.dispose();
		
		return scaledImage;
	}
}
