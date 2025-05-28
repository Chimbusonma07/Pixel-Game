package main;

import javax.swing.JFrame;

public class MainGame{
    public static void main(String[] args){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to make sure the window closes well when 'x' is pressed
        window.setResizable(false);
        window.setTitle("2D Adventure"); 

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        
        window.pack(); //fit the layout of GamePanel

        window.setLocationRelativeTo(null); //displayed at the center of the screen
        window.setVisible(true);
        
        gamePanel.setupGame();

        gamePanel.startGameThread();
    }
}