package org.usfirst.frc.team1559.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Drive system class for shifting and reading encoders.
 */
public class Transmission {

	private Solenoid shift1, shift2;
	private int gear;
	private Joystick stick;
	private TalonSRX leftM, rightM;
	private boolean isShifted;
	
	public Transmission(Joystick joy, TalonSRX rightM, TalonSRX leftM) {
		isShifted = false;
		shift1 = new Solenoid(Wiring.SHIFT_1);
		shift2 = new Solenoid(Wiring.SHIFT_2);
		gear = 1;
		this.stick = joy;
		this.leftM = leftM;
		this.rightM = rightM;
	}

	public void gear1() {
		gear = 1;
		shift1.set(false);
		shift2.set(true);
		System.out.println("GEAR 1");
	}

	public void gear2() {
		gear = 2;
		shift1.set(true);
		shift2.set(false);
		System.out.println("GEAR 2");
	}

	public int getGear() {
		return gear;
	}

	public int getRDisplacement() {
		return 0;//(rightM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}

	public int getLDisplacement() {
		return 0;//-(leftM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}

	public int getRVelocity() {
		return 0;//(rightM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}

	public int getLVelocity() {
		return 0;//-(leftM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}

	public void resetEncoders() {
		
		//leftM.setEncPosition(0);
	}

	public void updateShifting() {
		double velocity = (getRVelocity() + (double) getLVelocity()) / 2;
		
		if (velocity >= Wiring.SHIFT_UP_SPEED && stick.getRawButton(9)) {
			//System.out.println("AlekTheSoccerKid");
			gear2();
		} else if ((velocity <= Wiring.SHIFT_DOWN_SPEED) || !stick.getRawButton(9)) {
			gear1();
		}
		
	}
	
	public void limpShifting() {
		
		if (stick.getRawButton(9)) {
			isShifted = !isShifted;
		}
		if(isShifted) {
			gear2();
		} else {
			gear1();
		}
	}
	
	public TalonSRX getLeftMotor() {
		return leftM;
	}
	
	public TalonSRX getRightMotor() {
		return rightM;
	}
}