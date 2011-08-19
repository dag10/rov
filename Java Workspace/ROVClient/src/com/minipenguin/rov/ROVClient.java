package com.minipenguin.rov;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ROVClient {
	public static final int WINDOW_WIDTH	= 800;
	public static final int WINDOW_HEIGHT	= 600;
	public static final int PANEL_MARGIN	= 10;
	public static final int CONTROL_MARGIN	= 5;
	public static final int LABEL_HEIGHT	= 15;
	
	private static SimpleDateFormat logDateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private JFrame window = null;
	
	private JPanel jsPanel = null;
	private static final Map<String, LableInfo> jsLabelMap = new HashMap<String, LableInfo>();
	private static LableInfo lastJsLabel = null;
	
	private JPanel rovPanel = null;
	private static final Map<String, LableInfo> rovLabelMap = new HashMap<String, LableInfo>();
	private static LableInfo lastRovLabel = null;

	private JPanel conPanel = null;
	private JTextArea conOutput = null;
	private JScrollPane conOutputScroll = null;
	private boolean conAutoScroll = true;
	
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
		
		conPanel.add(conOutput = new JTextArea() {
			private static final long serialVersionUID = 1L;
			public void scrollRectToVisible(Rectangle aRect) {
				if (conAutoScroll)
					super.scrollRectToVisible(aRect);
			}
		});
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
		conOutput.setEditable(false);
		conOutput.setLineWrap(true);
		conOutput.setWrapStyleWord(true);
		conOutput.setAutoscrolls(true);
		
		conPanel.add(conOutputScroll = new JScrollPane(conOutput));
		conOutputScroll.setBounds(conOutput.getBounds());
		conOutputScroll.setBorder(BorderFactory.createEtchedBorder());
		conOutputScroll.setAutoscrolls(true);
		conOutputScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent event) {
				JScrollBar scrollBar = (JScrollBar)event.getSource();
				
				if (!event.getValueIsAdjusting()) return;
				
				if ((scrollBar.getValue() + scrollBar.getVisibleAmount()) == scrollBar.getMaximum())
					conAutoScroll = true;
				else if (conAutoScroll)
					conAutoScroll = false;
			}
		});
		
		// Set default label text
		
		addJsLabel("Throttle", new LableInfo("Throttle"));
		addJsLabel("Pitch", new LableInfo("Pitch"));
		addJsLabel("Slide", new LableInfo("Slide"));
		addJsLabel("Rotation", new LableInfo("Rotation"));
		addJsLabel("Elevation", new LableInfo("Elevation"));
		addJsLabel("ClawAction", new LableInfo("Claw Action", "None"));
		addJsLabel("ToggleLights", new LableInfo("Toggle Lights", "Released"));
		
		addRovLabel("Thruster1", new LableInfo("Forward Thruster A"));
		addRovLabel("Thruster2", new LableInfo("Forward Thruster B"));
		
		// Display window
		
		window.setVisible(true);
		
		// Begin program

		log(LogType.Info, LogDevice.Computer, "Program started");
		
		(new Thread(new JoystickManager(this))).start();
	}

	public void log(LogType type, LogDevice device, String message) {
		if (type == null || device == null || message == null)
			return;
		
		conOutput.append("[" + logDateFormat.format(Calendar.getInstance().getTime()) + "] ");
		conOutput.append("[" + device.toString() + "] ");
		conOutput.append("[" + type.toString() + "] ");
		conOutput.append(message + "\n");
		
		if (conAutoScroll) conOutput.setCaretPosition(conOutput.getDocument().getLength() - 1);
	}
	
	public void logException(Exception e) {
		StackTraceElement[] trace = e.getStackTrace();
		for (StackTraceElement element : trace)
			log(LogType.Exception, LogDevice.Computer, element.toString());
		e.printStackTrace();
	}
	
	public void axisUpdate(AxisID axis, double value) {
		if (Math.abs(value) < 5) value = 0;
		
		switch (axis) {
		case Throttle:
			value *= -1;
			setJsLabel("Throttle", String.valueOf((int)value));
			break;
		case Rotation:
			setJsLabel("Rotation", String.valueOf((int)value));
			break;
		case Elevation:
			value -= 50;
			value *= 2;
			setJsLabel("Elevation", String.valueOf((int)value));
			break;
		case Pitch:
			setJsLabel("Pitch", String.valueOf((int)value));
			break;
		case Slide:
			setJsLabel("Slide", String.valueOf((int)value));
			break;
		}
	}
	
	public void buttonUpdate(BtnID button, boolean value) {
		switch (button) {
		case ToggleLights:
			setJsLabel("ToggleLights", (value ? "Pressed" : "Released"));
			break;
		}
	}
	
	public void directionalUpdate(DirID dir) {
		switch (dir) {
		case OpenClaw:
			setJsLabel("ClawAction", "Open");
			break;
		case CloseClaw:
			setJsLabel("ClawAction", "Close");
			break;
		case ExtendClaw:
			setJsLabel("ClawAction", "Extend");
			break;
		case RetractClaw:
			setJsLabel("ClawAction", "Retract");
			break;
		case StopClaw:
			setJsLabel("ClawAction", "None");
			break;
		}
	}
	
	private void addJsLabel(String referenceName, LableInfo type) {
		jsLabelMap.put(referenceName, type);


		JLabel label = new JLabel();
		jsPanel.add(label);
		if (lastJsLabel == null)
			label.setBounds(
					label.getParent().getInsets().left + CONTROL_MARGIN,
					label.getParent().getInsets().top + CONTROL_MARGIN,
					label.getParent().getWidth() - CONTROL_MARGIN * 2,
					LABEL_HEIGHT
					);
		else
			label.setBounds(
					label.getParent().getInsets().left + CONTROL_MARGIN,
					CONTROL_MARGIN + lastJsLabel.getLabel().getY() + lastJsLabel.getLabel().getHeight(),
					label.getParent().getWidth() - CONTROL_MARGIN * 2,
					LABEL_HEIGHT
					);

		type.setLabel(label);
		lastJsLabel = type;
		
		label.setText(type.getLabelName() + ": " + type.getDefaultValue());
	}

	private void setJsLabel(String name, String value) {
		LableInfo label = jsLabelMap.get(name);
		if (label == null) return;
		
		label.getLabel().setText(label.getLabelName() + ": " + value);
	}
	
	private void addRovLabel(String referenceName, LableInfo type) {
		rovLabelMap.put(referenceName, type);


		JLabel label = new JLabel();
		rovPanel.add(label);
		if (lastRovLabel == null)
			label.setBounds(
					label.getParent().getInsets().left + CONTROL_MARGIN,
					label.getParent().getInsets().top + CONTROL_MARGIN,
					label.getParent().getWidth() - CONTROL_MARGIN * 2,
					LABEL_HEIGHT
					);
		else
			label.setBounds(
					label.getParent().getInsets().left + CONTROL_MARGIN,
					CONTROL_MARGIN + lastRovLabel.getLabel().getY() + lastRovLabel.getLabel().getHeight(),
					label.getParent().getWidth() - CONTROL_MARGIN * 2,
					LABEL_HEIGHT
					);

		type.setLabel(label);
		lastRovLabel = type;
		
		label.setText(type.getLabelName() + ": " + type.getDefaultValue());
	}

	private void setRovLabel(String name, String value) {
		LableInfo label = rovLabelMap.get(name);
		if (label == null) return;
		
		label.getLabel().setText(label.getLabelName() + ": " + value);
	}
}

enum LogType { Info, Warning, Error, Exception };
enum LogDevice { Computer, Arduino };

class LableInfo {
	private String labelName = null;
	private String defaultValue = null;
	private JLabel label = null;
	
	public LableInfo(String labelName, String defaultValue) {
		this.labelName = labelName;
		this.defaultValue = defaultValue;
	}
	
	public LableInfo(String labelName) {
		this(labelName, "0");
	}
	
	public String getLabelName() {
		return labelName;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public JLabel getLabel() {
		return label;
	}
	
	public void setLabel(JLabel label) {
		this.label = label;
	}
}