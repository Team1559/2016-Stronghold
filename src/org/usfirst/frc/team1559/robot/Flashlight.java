package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;

public class Flashlight {

	Relay daLight;
	int counterThing = 0;
	public Flashlight(){
		daLight = new Relay(Wiring.FLASH_RELAY);
		configForward();
		turnOff();
	}
	
	public void updateLight(Joystick stick){
		if(stick.getRawAxis(Wiring.FLASH_BUTT_ON) > 0.5){
			System.out.println("ON!");
			turnOn();
		} else {
			turnOff();
			System.out.println("OFF!");
		}
	}
	
	public void turnOn(){
		daLight.set(Relay.Value.kOn);
	}
	
	public void turnOff(){
		daLight.set(Relay.Value.kOff);
	}
	
	public void configForward(){
		daLight.setDirection(Relay.Direction.kForward);
	}
	
	public void configBackward(){
		daLight.setDirection(Relay.Direction.kReverse);
	}
	
}
