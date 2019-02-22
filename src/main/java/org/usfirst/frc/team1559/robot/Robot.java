/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team1559.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class Robot extends IterativeRobot {
	private Joystick joy;
	private DifferentialDrive drive;
	private WPI_TalonSRX masterLeft, masterRight, slaveLeft, slaveRight;
	private Shooter shooter;
	private Gatherer gatherer;
	private Transmission transmission;
	
	@Override
	public void robotInit() {
		joy = new Joystick(1);
		shooter = new Shooter();
		gatherer = new Gatherer(Wiring.GATHERER_LIFT, Wiring.GATHERER_ROTATE, joy);
		masterLeft = new WPI_TalonSRX(Wiring.LEFT_MASTER_TALON);
		masterRight = new WPI_TalonSRX(Wiring.RIGHT_MASTER_TALON);
		slaveLeft = new WPI_TalonSRX(Wiring.LEFT_SLAVE_TALON);
		slaveLeft.set(ControlMode.Follower, 0);
		slaveRight = new WPI_TalonSRX(Wiring.RIGHT_SLAVE_TALON);
		slaveRight.set(ControlMode.Follower, 0);
		drive = new DifferentialDrive(masterLeft, masterRight);
		masterRight.setInverted(true);
		masterLeft.setInverted(true);
		slaveLeft.setInverted(true);
		slaveRight.setInverted(true);
		transmission = new Transmission(joy, masterRight, masterLeft);
	}

	@Override
	public void autonomousInit() {

	}

	@Override
	public void autonomousPeriodic() {

	}

	@Override
	public void teleopPeriodic() {
		drive.arcadeDrive(joy.getY(), joy.getRawAxis(4));
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
	}

	@Override
	public void testPeriodic() {
	}
}
