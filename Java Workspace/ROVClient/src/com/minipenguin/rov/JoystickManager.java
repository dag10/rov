package com.minipenguin.rov;

import com.minipenguin.rov.ROVClient;
import de.hardcode.jxinput.Axis;
import de.hardcode.jxinput.Button;
import de.hardcode.jxinput.Directional;
import de.hardcode.jxinput.JXInputDevice;
import de.hardcode.jxinput.JXInputManager;
import de.hardcode.jxinput.event.JXInputAxisEvent;
import de.hardcode.jxinput.event.JXInputAxisEventListener;
import de.hardcode.jxinput.event.JXInputButtonEvent;
import de.hardcode.jxinput.event.JXInputButtonEventListener;
import de.hardcode.jxinput.event.JXInputDirectionalEvent;
import de.hardcode.jxinput.event.JXInputDirectionalEventListener;
import de.hardcode.jxinput.event.JXInputEventManager;

enum AxisID {
	Slide,
	Pitch,
	Throttle,
	Rotation,
	Elevation
}

enum BtnID {
	ToggleLights
}

enum DirID {
	CloseClaw,
	OpenClaw,
	ExtendClaw,
	RetractClaw,
	StopClaw
}

public class JoystickManager implements Runnable {
	private ROVClient client = null;
	private JXInputDevice device = null;
	
	public JoystickManager(ROVClient client) {
		this.client = client;
	}
	
	public void run() {		
		for (int i = 0; i < JXInputManager.getNumberOfDevices(); i++) {
			JXInputDevice device = JXInputManager.getJXInputDevice(i);
			if (device.getName().equals("T.Flight Hotas X")) {
				this.device = device;
				break;
			}
		}
		
		if (device == null) {
			client.log(LogType.Error, LogDevice.Computer, "Failed to find joystick: \"T.Flight Hotas X\"");
			return;
		} else {
			client.log(LogType.Info, LogDevice.Computer, "Found joystick: \"T.Flight Hotas X\"");
		}
		
		JXInputEventManager.setTriggerIntervall(1);
		
		for (int i = 0; i < device.getMaxNumberOfAxes(); i++)
			new AxisListener(device.getAxis(i), this);
		
		for (int i = 0; i < device.getMaxNumberOfButtons(); i++)
			new ButtonListener(device.getButton(i), this);
		
		for (int i = 0; i < device.getMaxNumberOfDirectionals(); i++)
			new DirectionalListener(device.getDirectional(i), this);
		
		while (true) {
			JXInputEventManager.trigger();
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				client.logException(e);
			}
		}
	}
	
	public void axisChanged(Axis axis) {
		//client.log(LogType.Info, LogDevice.Computer, axis.getName() + ": " + axis.getValue());
		
		String name = axis.getName();
		AxisID id = null;
		
		if (name.equals("X Axis"))
			id = AxisID.Slide;
		else if (name.equals("Y Axis"))
			id = AxisID.Pitch;
		else if (name.equals("Z Axis"))
			id = AxisID.Throttle;
		else if (name.equals("Z Rotation"))
			id = AxisID.Rotation;
		else if (name.equals("Slider"))
			id = AxisID.Elevation;
		
		if (id != null)
			client.axisUpdate(id, (double)(Math.round(axis.getValue() * 100)) / 1);
	}
	
	public void buttonChanged(Button button) {
		//client.log(LogType.Info, LogDevice.Computer, button.getName() + ": " + (button.getState() ? "pressed" : "released"));
		
		String name = button.getName();
		BtnID id = null;
		
		if (name.equals("Button 4"))
			id = BtnID.ToggleLights;
		
		if (id != null)
			client.buttonUpdate(id, button.getState());
	}
	
	public void directionalChanged(Directional directional) {
		//client.log(LogType.Info, LogDevice.Computer, directional.getName() + ": " + (directional.isCentered() ? "-" : directional.getDirection()));
		
		String name = directional.getName();
		DirID id = null;
		
		if (name.equals("Hat Switch")) {
			if (directional.isCentered()) {
				id = DirID.StopClaw;
			} else {
				switch (directional.getDirection()) {
				case 0:
					id = DirID.ExtendClaw;
					break;
				case 18000:
					id = DirID.RetractClaw;
					break;
				case 27000:
					id = DirID.CloseClaw;
					break;
				case 9000:
					id = DirID.OpenClaw;
					break;
				default:
					id = DirID.StopClaw;
					break;
				}
			}
		}
		
		if (id != null)
			client.directionalUpdate(id);
	}
}

class AxisListener implements JXInputAxisEventListener {
	private JoystickManager manager = null;
	
	public AxisListener(Axis axis, JoystickManager manager) {
		if (axis == null || manager == null) return;
		
		this.manager = manager;
		JXInputEventManager.addListener(this, axis, 0.01);
	}

	public void changed(JXInputAxisEvent ev) {
		if (manager != null)
			manager.axisChanged(ev.getAxis());
	}
}

class ButtonListener implements JXInputButtonEventListener {
	private JoystickManager manager = null;
	
	public ButtonListener(Button button, JoystickManager manager) {
		if (button == null || manager == null) return;
		
		this.manager = manager;
		JXInputEventManager.addListener(this, button);
	}

	public void changed(JXInputButtonEvent ev) {
		if (manager != null)
			manager.buttonChanged(ev.getButton());
	}
}

class DirectionalListener implements JXInputDirectionalEventListener {
	private JoystickManager manager = null;
	
	public DirectionalListener(Directional directional, JoystickManager manager) {
		if (directional == null || manager == null) return;
		
		this.manager = manager;
		JXInputEventManager.addListener(this, directional);
	}

	public void changed(JXInputDirectionalEvent ev) {
		if (manager != null)
			manager.directionalChanged(ev.getDirectional());
	}
}