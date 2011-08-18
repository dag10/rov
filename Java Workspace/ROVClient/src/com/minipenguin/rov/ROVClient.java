package com.minipenguin.rov;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ROVClient {
	public static final int WINDOW_WIDTH	= 800;
	public static final int WINDOW_HEIGHT	= 600;
	public static final int PANEL_MARGIN	= 10;
	public static final int CONTROL_MARGIN	= 5;
	public static final int LABEL_HEIGHT	= 12;
	
	private JFrame window = null;
	
	private JPanel jsPanel = null;
	private JLabel jsThrottleLabel = null;
	private JLabel jsRotateLabel = null;
	
	private JPanel rovPanel = null;
	private JLabel rovThruster1Label = null;
	private JLabel rovThruster2Label = null;

	private JPanel conPanel = null;
	private JTextArea conOutput = null;
	
	public static void main(String[] args) {
		new ROVClient();
	}
	
	public ROVClient() {
		// Initialize window
		
		window = new JFrame("ROV Controller");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocation(10, 10);
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setResizable(false);
    	window.getContentPane().setLayout(null);
		
		// Initialize joystick panel
    	
		window.getContentPane().add(jsPanel = new JPanel());
		jsPanel.setBounds(
				PANEL_MARGIN,
				PANEL_MARGIN, 
				(window.getWidth() / 2) - (PANEL_MARGIN * 2),
				(window.getHeight() / 2) - (PANEL_MARGIN * 2)
				);
		jsPanel.setBorder(BorderFactory.createTitledBorder("Joystick"));
		jsPanel.setLayout(null);
		
		jsPanel.add(jsThrottleLabel = new JLabel("Throttle: 0"));
		jsThrottleLabel.setBounds(
				jsThrottleLabel.getParent().getInsets().left + CONTROL_MARGIN,
				jsThrottleLabel.getParent().getInsets().top + CONTROL_MARGIN,
				jsThrottleLabel.getParent().getWidth() - CONTROL_MARGIN * 2,
				LABEL_HEIGHT
				);
		
		jsPanel.add(jsRotateLabel = new JLabel("Joystick rotation: 0"));
		jsRotateLabel.setBounds(
				jsRotateLabel.getParent().getInsets().left + CONTROL_MARGIN,
				CONTROL_MARGIN + jsThrottleLabel.getY() + jsThrottleLabel.getHeight(),
				jsRotateLabel.getParent().getWidth() - CONTROL_MARGIN * 2,
				LABEL_HEIGHT
				);
		
		// Initialize rov panel
    	
		window.getContentPane().add(rovPanel = new JPanel());
		rovPanel.setBounds(
				PANEL_MARGIN + jsPanel.getX() + jsPanel.getWidth(),
				PANEL_MARGIN, 
				(window.getWidth() / 2) - (PANEL_MARGIN * 2),
				(window.getHeight() / 2) - (PANEL_MARGIN * 2)
				);
		rovPanel.setBorder(BorderFactory.createTitledBorder("ROV"));
		rovPanel.setLayout(null);
		
		rovPanel.add(rovThruster1Label = new JLabel("Thruster 1: 0"));
		rovThruster1Label.setBounds(
				rovThruster1Label.getParent().getInsets().left + CONTROL_MARGIN,
				rovThruster1Label.getParent().getInsets().top + CONTROL_MARGIN,
				rovThruster1Label.getParent().getWidth() - CONTROL_MARGIN * 2,
				LABEL_HEIGHT
				);
		
		rovPanel.add(rovThruster2Label = new JLabel("Thruster 2: 0"));
		rovThruster2Label.setBounds(
				rovThruster2Label.getParent().getInsets().left + CONTROL_MARGIN,
				CONTROL_MARGIN + rovThruster1Label.getY() + rovThruster1Label.getHeight(),
				rovThruster2Label.getParent().getWidth() - CONTROL_MARGIN * 2,
				LABEL_HEIGHT
				);
		
		// Initialize console panel
    	
		window.getContentPane().add(conPanel = new JPanel());
		conPanel.setBounds(
				PANEL_MARGIN,
				PANEL_MARGIN + jsPanel.getY() + jsPanel.getHeight(),
				window.getWidth() - (PANEL_MARGIN * 3),
				(window.getHeight() / 2) - (PANEL_MARGIN * 4)
				);
		conPanel.setBorder(BorderFactory.createTitledBorder("Console"));
		conPanel.setLayout(null);
		
		conPanel.add(conOutput = new JTextArea());
		conOutput.setBounds(
				conOutput.getParent().getInsets().left + CONTROL_MARGIN,
				conOutput.getParent().getInsets().top + CONTROL_MARGIN,
				(
						conOutput.getParent().getWidth()
						- (CONTROL_MARGIN * 2)
						- conOutput.getParent().getInsets().left
						- conOutput.getParent().getInsets().right
				),
				(
						conOutput.getParent().getHeight()
						- (CONTROL_MARGIN * 2)
						- conOutput.getParent().getInsets().top
						- conOutput.getParent().getInsets().bottom
				)
				);
		//conOutput.setEditable(false);
		conOutput.setLineWrap(true);
		conOutput.setWrapStyleWord(true);
		conOutput.setAutoscrolls(true);
		conOutput.setText("Hello, World!");
		
		JScrollPane conOutputScroll;
		conPanel.add(conOutputScroll = new JScrollPane(conOutput));
		conOutputScroll.setBounds(conOutput.getBounds());
		conOutputScroll.setBorder(BorderFactory.createEtchedBorder());
		conOutputScroll.setAutoscrolls(true);
		
		// Display window
		
		window.setVisible(true);
	}
}
