/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1559.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc.team1559.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class Robot extends TimedRobot {
	public boolean isShifted;
	private DriveTrain drive;
	private OperatorInterface oi;

	@Override
	public void robotInit() {
		isShifted = false;
		drive = new DriveTrain();
	}

	@Override
	public void autonomousInit() {

	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopPeriodic() {
		drive.arcadeDrive(oi.getPilotY(), oi.getPilotZ());

		if (oi.pilot.getRawButton(9)) {
			isShifted = !isShifted;
		}
		if(isShifted) {
			drive.setGear2();
		} else {
			drive.setGear1();
		}
		/*
		transmission.limpShifting();
		gatherer.manualControl();
		shooter.updateShooter(joy, false);
		if(joy.getRawButton(4)) { //outake
			gatherer.setSpark(-0.5);
		} else if(joy.getRawButton(1)) {
			gatherer.setSpark(0.5);
		} else {
			gatherer.setSpark(0);
		}
		*/
	}

	@Override
	public void testPeriodic() {
	}
}
