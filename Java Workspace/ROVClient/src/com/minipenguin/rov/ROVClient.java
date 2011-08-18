package com.minipenguin.rov;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ROVClient {
	public static final int WINDOW_WIDTH	= 800,
					 		WINDOW_HEIGHT	= 600;
	
	private JFrame window = null;
	
	public static void main(String[] args) {
		new ROVClient();
	}
	
	public ROVClient() {
		// Initialize window
		window = new JFrame("ROV Controller");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(800, 600);
		window.setResizable(false);
		
		// Initialize GUI
		window.getContentPane().add(new JLabel("Hello, World!", null, JLabel.CENTER), BorderLayout.CENTER);
		
		// Display window
		window.setVisible(true);
	}
}
