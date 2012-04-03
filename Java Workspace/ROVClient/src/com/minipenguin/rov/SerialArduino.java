package com.minipenguin.rov;

import java.io.*;
import java.util.LinkedList;

public class SerialArduino extends Thread {
	private static String portName = "com7:";
	private LinkedList<ArduinoCommand> commands;
	private boolean shouldStop = false;
	
	public void run() {
		commands = new LinkedList<ArduinoCommand>();
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		
		String cmd[] = {
			"c:\\windows\\system32\\cmd.exe", "/c",
			"start", "/min",
			"c:\\windows\\system32\\mode.com", portName,
			"baud=9600", "parity=n", "data=8",
			"stop=1", 
		};
		
		try {
			p = rt.exec(cmd);
			if (p.waitFor() != 0) {
				System.out.println("Error executing command: " + cmd );
				System.exit( -1 );
			}
			
			FileOutputStream fos = new FileOutputStream(portName);
			
			while (!shouldStop) {
				if (commands.size() == 0) {
					Thread.sleep(5);
					continue;
				}
				
				byte[] data = commands.poll().getData();
				fos.write(data, 0, data.length);
			}
			
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addCommand(ArduinoCommand command) {
		commands.offer(command);
	}
	
	public void requestStop() {
		shouldStop = true;
	}
}

interface ArduinoCommand {
	public byte[] getData();
}

class MotorSpeedCommand implements ArduinoCommand {
	private int motor, speed;
	
	public MotorSpeedCommand(int motor, int speed) {
		this.motor = motor;
		this.speed = speed;
	}
	
	public byte[] getData() {
		return (motor + "," + speed + "\n").getBytes();
	}
}