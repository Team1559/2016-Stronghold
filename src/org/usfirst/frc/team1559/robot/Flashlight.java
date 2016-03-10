package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;

public class Flashlight {

	DigitalOutput daLight;
	public Flashlight(){
		daLight = new DigitalOutput(Wiring.FLASH_DIO);
	}
	
	public void updateLight(Joystick stick){
		if(stick.getRawButton(Wiring.FLASH_BUTT_ON)){
			turnOn();
		} else {
			turnOff();
		}
	}
	
	public void turnOn(){
		daLight.set(true);
	}
	
	public void turnOff(){
		daLight.set(false);
	}
	
}
