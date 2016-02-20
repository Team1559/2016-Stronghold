package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;

public class Transmission {

	// will eventually be in its own thread to shift gears while driving is
	// occurring.

	Solenoid shift1;
	Solenoid shift2;
	PowerDistributionPanel pdp;
	int gear;
	Joystick joy;
	CANTalon leftM, rightM;

	public Transmission(Joystick joy, CANTalon rightM, CANTalon leftM) {

		shift1 = new Solenoid(Wiring.SHIFT_1);
		shift2 = new Solenoid(Wiring.SHIFT_2);
		pdp = new PowerDistributionPanel();
		gear = 1;
		this.joy = joy;
		this.rightM = rightM;
		this.leftM = leftM;
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

	//
	// public double getSpeed() {
	// /*ADD REAL CODE!*/
	// return 10.1;
	// }
	//
	public int getRDisplacement() {
		return (rightM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}

	public int getLDisplacement() {
		return -(leftM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}

	public int getRVelocity() {
		return (rightM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}

	public int getLVelocity() {
		return -(leftM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}

	public void resetEncoders() {
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);
	}

	public void updateShifting() {
		double velocity = (getRVelocity() + (double) getLVelocity()) / 2;

		if (velocity >= Wiring.SHIFT_UP_SPEED && joy.getRawButton(9)) {
			gear2();
		} else if ((velocity <= Wiring.SHIFT_DOWN_SPEED) || !joy.getRawButton(9)) {
			gear1();
		}

	}

	public PowerDistributionPanel getPDP() {
		return pdp;
	}
}