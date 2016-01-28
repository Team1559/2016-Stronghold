
package org.usfirst.frc.team1559.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {
	AHRS ahrs;
	RobotDrive robot;
	CANTalon leftF;
	//CANTalon leftR;
	CANTalon rightF;
	//CANTalon rightR;
	Joystick stick;
	Timer timer;
	boolean isInverted;
	Solenoid shift1;
	Solenoid shift2;
	WFFL waffle;

//Comments are for a 4 motor drive system whereas uncommented code just does 2
	
	public void robotInit() {
		ahrs = new AHRS(SPI.Port.kMXP);
		leftF = new CANTalon(Wiring.LEFT_FRONT_CAN_TALON);
		rightF = new CANTalon(Wiring.RIGHT_FRONT_CAN_TALON);
//	    leftR = new CANTalon(Wiring.LEFT_REAR_CAN_TALON);
//      rightR = new CANTalon(Wiring.RIGHT_REAR_CAN_TALON);
		robot = new RobotDrive(leftF,rightF);
		//robot = new RobotDrive(leftF,leftR,rightF,rightR);
		stick = new Joystick(Wiring.JOYSTICK0);
		shift1 = new Solenoid(Wiring.SHIFT_1);
		shift2 = new Solenoid(Wiring.SHIFT_2);
		waffle = new WFFL("/home/lvuser/format.wffl");
	}

	public void autonomousInit() {
		waffle.interpret();
	}

	public void autonomousPeriodic() {

	}

	public void teleopInit() {
		isInverted = true;
		leftF.setInverted(isInverted);
		
	}

	public void teleopPeriodic() {
		robot.arcadeDrive(stick);
		if (stick.getRawButton(6)) {

			gear1();

		} else if (stick.getRawButton(5)) {

			gear2();

		}
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
	public void testInit() {

	}

	public void testPeriodic() {

	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {

	}
}
