package org.usfirst.frc.team1559.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
//	AHRS ahrs;
	RobotDrive robot;
	boolean shootDone = false;
	 CANTalon leftM;
	 CANTalon leftS;
	 CANTalon rightM;
	 CANTalon rightS;
	Joystick stick;
	Timer timer;
	double desiredHeading = 0;
	boolean isInverted;
	WFFL waffle;
	Transmission tranny;
	int listPos = 0;
	SmartDashboard smrt = new SmartDashboard();
	SocketClient sc = new SocketClient();

	// Comments are for a 4 motor drive system whereas uncommented code just
	// does 2

	public void robotInit() {
//		ahrs = new AHRS(SPI.Port.kMXP);
		leftM = new CANTalon(Wiring.LEFT_MASTER_TALON);
		rightM = new CANTalon(Wiring.RIGHT_MASTER_TALON);
		leftS = new CANTalon(Wiring.LEFT_SLAVE_TALON);
		rightS = new CANTalon(Wiring.RIGHT_SLAVE_TALON);
		leftS.changeControlMode(CANTalon.TalonControlMode.Follower);// sets
		// motor to follower
		leftS.set(Wiring.LEFT_MASTER_TALON);// sets to ID of master(leftM)
		rightS.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightS.set(Wiring.RIGHT_MASTER_TALON);
		robot = new RobotDrive(leftM, rightM);
		stick = new Joystick(Wiring.JOYSTICK0);
		tranny = new Transmission(stick);
//		waffle = new WFFL("/media/sda1/runthis.wffl");
		
		leftM.setInverted(false);
		rightM.setInverted(false);
		
		//encoder stuff
		leftM.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		rightM.setFeedbackDevice(FeedbackDevice.QuadEncoder);

	}

	public void autonomousInit() {
		waffle.reset();
		waffle.ahrs.reset();
		waffle.interpret();
		waffle.left.setInverted(false);
		waffle.right.setInverted(false);
		waffle.length = 0;

	}

	public void autonomousPeriodic() {
		smrt.putNumber("Yaw:", waffle.yaw);
		
		Command current = waffle.list.get(listPos);
		if (current.command.equals("TURN")) {
			waffle.turnToAngle(current.angle);
			if (waffle.keepTurning == false) {
				desiredHeading = current.angle;
				current.done = true;
				waffle.length = 0;
			}
			System.out.println("Works YAY");
		} else if (current.command.equals("GO")) {
			waffle.drive(desiredHeading, current.dist, 0, current.speed);
			if (waffle.keepRunning == false) {
				current.done = true;
			}
		} else if (current.command.equals("SHOOT")) {
			sendRecieveCenterValues();
			boolean shootDone = waffle.center();
			if (shootDone) {
				current.done = true;
				System.out.println("DONE SHOOTING!");
			}

		} else if (current.command.equals("STOP")) {
			waffle.left.set(0);
			waffle.right.set(0);
		} else if (current.command.equals("WAIT")){
			System.out.println("waiting...");
			Timer.delay(current.time);
			current.done = true;
		}

		if ((current.done == true)) {
			current.done = false;

			if (waffle.list.size() - 1 > listPos) {
				listPos++;
				waffle.length = 0;
				waffle.keepRunning = true;
				waffle.keepTurning = true;
			} else {
				waffle.left.set(0);
				waffle.right.set(0);
			}
			

		}
		waffle.length++;
	}

	public void teleopInit() {
		isInverted = true;
//		waffle.left.setInverted(true);
//		waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);

	}

	public void teleopPeriodic() {
//		sendRecieveCenterValues();
//		waffle.myRobot.arcadeDrive(stick); //FOR THE TEST CHASSIS
		 robot.arcadeDrive(stick);
//		waffle.Traction();
		if (stick.getRawButton(3)) {

			tranny.gear1();

		} else if (stick.getRawButton(4)) {

			tranny.gear2();

		}
		SmartDashboard.putNumber("Encoder", rightM.getEncPosition());
	}

	public void sendRecieveCenterValues() {
		// String [] center = sc.read();
		// int cx = Integer.parseInt(center[0]);
		// int cy = Integer.parseInt(center[1]);
		// waffle.cx = cx;
		// waffle.cy = cy;
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
