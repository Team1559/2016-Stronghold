package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.DigitalOutput;



public class RaspiKillSwitch {
	
	DigitalOutput output;
	
	
	public RaspiKillSwitch() {
//		output = new DigitalOutput(Wiring.PI_KILL_SWITCH);
	}
	
	
	public void kill() {
		output.set(true);
	}

}
