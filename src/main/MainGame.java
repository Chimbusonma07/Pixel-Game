package main;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class MainGame{
    // Optional arguments (the web page passes them; on desktop just run with none):
    //   web   - running in a browser: borderless window that fills the page and follows its size
    //   touch - phone/tablet: hints say "Tap A" (the on-screen buttons are part of the web page)
    public static void main(String[] args){
        boolean web = false, touch = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("web")) web = true;
            if (arg.equalsIgnoreCase("touch")) touch = true;
        }

        final JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to make sure the window closes well when 'x' is pressed
        window.setTitle("2D Adventure");

        GamePanel gamePanel = new GamePanel();
        gamePanel.touchMode = touch;
        window.add(gamePanel);

        if (web) {
            // fill the browser page; the game scales itself to fit
            window.setUndecorated(true);
            fitToScreen(window);
            // the page can be resized or the phone rotated: keep filling it
            new javax.swing.Timer(500, e -> fitToScreen(window)).start();
        }
        else {
            window.setResizable(true); // the game scales to any window size, keeping its shape
            window.pack(); //fit the layout of GamePanel
            window.setLocationRelativeTo(null); //displayed at the center of the screen
        }
        window.setVisible(true);
        gamePanel.requestFocusInWindow();

        gamePanel.setupGame();

        gamePanel.startGameThread();
    }

    static void fitToScreen(JFrame window) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (!screen.equals(window.getSize())) {
            window.setBounds(0, 0, screen.width, screen.height);
            window.validate();
        }
    }
}
