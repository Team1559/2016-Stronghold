package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;

public class Transmission {

	Solenoid shift1;
	Solenoid shift2;
	PowerDistributionPanel pdp;
	Encoder encoder = new Encoder();
	int gear;
	Joystick joy;

	public Transmission(Joystick joy) {

		shift1 = new Solenoid(Wiring.SHIFT_1);
		shift2 = new Solenoid(Wiring.SHIFT_2);
		pdp = new PowerDistributionPanel();
		gear = 1;
		this.joy = joy;
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

	public void auto() {

		switch (gear) {

		case 1:
			System.out.println("1ST GEAR");
			// normal driving

			// do we need to shift?
			if ((getSpeed() >= .5) && !joy.getRawButton(2)) { // hold button 2
																// to push
				gear2();
				gear = 2;
			}
			break;
		case 2:
			System.out.println("2ND GEAR");
			if (((getSpeed() <= .5) && (getThrottle() >= 1))) {
				gear1();
				gear = 1;
			}

			if (getSpeed() <= .1) {

				gear1();
				gear = 1;

			}
			break;

		}

	}

	public double getSpeed() {
		return 10.1;
	}
	
	public double getThrottle(){
		return joy.getY();
	}

}
