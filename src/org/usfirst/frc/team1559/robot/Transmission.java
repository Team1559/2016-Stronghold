package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Solenoid;

public class Transmission {

	Solenoid shift1;
	Solenoid shift2;
	
	public Transmission(){
		
		shift1 = new Solenoid(Wiring.SHIFT_1);
		shift2 = new Solenoid(Wiring.SHIFT_2);
		
	}
	
	public void gear1() {

		shift1.set(false);
		shift2.set(true);
		System.out.println("GEAR 1");

	}

	public void gear2() {

		shift1.set(true);
		shift2.set(false);
		System.out.println("GEAR 1");

	}
	
	public void auto(){
		
		
		
	}
}
