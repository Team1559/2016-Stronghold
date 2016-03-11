package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;

public class Flashlight {

	DigitalOutput daLight;
	int counterThing = 0;
	public Flashlight(){
		daLight = new DigitalOutput(Wiring.FLASH_DIO);
		turnOff();
	}
	
	public void updateLight(Joystick stick){
		if(stick.getRawButton(Wiring.FLASH_BUTT_ON)){
			turnOn();
			System.out.println("ON!");
		} else if(stick.getRawButton(Wiring.FLASH_BUTT_STROBE)){
			counterThing++;
		} else {
			turnOff();
			counterThing = 0;
		}
		if(counterThing % 2 == 1){
			turnOn();
		}
		else if(counterThing % 2 == 0){
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
