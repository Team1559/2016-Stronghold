
package org.usfirst.frc.team1559.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	AHRS ahrs = new AHRS(SPI.Port.kMXP);

	final double drive_speed = 0.5;
	final double gyroTolerance = 0.001;
	final double gyro_kP = 0.1;
	final double gyro_maxError = .5;
	double gyro_error;
	
	Talon right = new Talon(1);
	Talon left = new Talon(0);
	RobotDrive drive = new RobotDrive(left, right);
	int gyro_counter = 0;
	Joystick buttons = new Joystick(0);
	boolean A;
	boolean B;

	public void robotInit() {
		ahrs.reset();
	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
		gyro_error = ahrs.getYaw();
		SmartDashboard.putNumber("error", gyro_error);
	}

	public void autonomousInit() {
		ahrs.reset();
		left.setInverted(false);
		right.setInverted(false);
		gyro_counter = 0;
	}

	public void autonomousPeriodic() {
		SmartDashboard.putBoolean("Movement", ahrs.isMoving());

		drive(0, 2);
		drive(90, 1, 2);
	}

	public void drive(int angle, double seconds) {
		drive(angle, seconds, 0);
	}

	public void drive(int angle, double seconds, double startTime) {
		gyro_error = ahrs.getYaw() - angle;
		if (gyro_counter > startTime * 50 && gyro_counter < (seconds + startTime) * 50)
			if (Math.abs(gyro_error) > gyroTolerance) {
				if (Math.abs(gyro_error * gyro_kP) < gyro_maxError)
					drive.drive(drive_speed, -(gyro_error * gyro_kP));
				else {
					if (gyro_error < 0)
						drive.drive(drive_speed, gyro_maxError);
					else
						drive.drive(drive_speed, -gyro_maxError);
				}
			} else {
				drive.drive(drive_speed, 0);
			}
		gyro_counter++;
		SmartDashboard.putNumber("error", gyro_error);
	}

	public void teleopInit() {
		ahrs.reset();
		left.setInverted(true);
		right.setInverted(true);
	}

	public void teleopPeriodic() {
		drive.arcadeDrive(buttons);
		gyro_error = ahrs.getYaw();
		System.out.println(ahrs.getAngle());
		SmartDashboard.putNumber("error", gyro_error);
		A = buttons.getRawButton(1);
		B = buttons.getRawButton(2);
		SmartDashboard.putBoolean("A", A);
		SmartDashboard.putBoolean("B", B);
		
		if (B) {
			ahrs.reset();
		}
		
	}

	public void testInit() {

	}

	public void testPeriodic() {

	}

}
