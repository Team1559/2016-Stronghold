package org.usfirst.frc.team1559.robot;

public class SensorCarnageProtection {

	/*
	 * This class is intended to be a safety net for the driver. Sensors carnage will
	 * happen. This class puts override control of the sensors and systems in the copilot's
	 * hands if need be.
	 * 
	 * Kyle will need to be briefed heavily about the functionality and strategy involved in 
	 * debugging the robot on the fly (during gameplay)
	 * 
	 * things Kyle can override:
	 * - Lifter Limit Switch
	 * - Gatherer Gyro (we will likely switch to full manual control in the near future anyway)
	 * 
	 * TODO: Add things
	 * 
	 */
	
	private boolean gathererDead;
	private boolean encodersDead;
	
	
	public SensorCarnageProtection(){
		gathererDead = true;
		encodersDead = false;
	}
	
	public void killGatherer(){
		gathererDead = true;
	}
	
	public void unKillGatherer(){
		gathererDead = false;
	}
	
	public boolean gathererDead(){
		return gathererDead;
	}
	
	public boolean encodersDead(){
		return encodersDead;
	}
	
	public void killEncoders(){
		encodersDead = true;
	}
	
	public void unKillEncoders(){
		encodersDead = false;
	}
}
