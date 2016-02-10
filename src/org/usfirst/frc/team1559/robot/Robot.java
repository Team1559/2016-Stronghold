package org.usfirst.frc.team1559.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANSpeedController;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
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
	double leftVelocity, rightVelocity;

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
		
//		leftM.setInverted(true);
//		rightM.setInverted(false);
		
		//encoder stuff
		leftM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);
		rightM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);
		
		leftM.enableBrakeMode(true);
		rightM.enableBrakeMode(true);
		leftS.enableBrakeMode(true);
		rightS.enableBrakeMode(true);
		
		rightM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		leftM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		robot.setExpiration(1000000);
		
		leftM.changeControlMode(TalonControlMode.Speed);
		leftM.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		leftM.reverseSensor(false);
		leftM.configNominalOutputVoltage(+0.0f, -0.0f);
		leftM.configPeakOutputVoltage(+12.0f, -12.0f);
		leftM.setProfile(0);
		leftM.setF(1);
		leftM.setP(0);
		leftM.setI(0);
		leftM.setD(0);
		
		rightM.changeControlMode(TalonControlMode.Speed);
		rightM.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightM.reverseSensor(false);
		rightM.configNominalOutputVoltage(+0.0f, -0.0f);
		rightM.configPeakOutputVoltage(+12.0f, -12.0f);
		rightM.setProfile(0);
		rightM.setF(1);
		rightM.setP(0);
		rightM.setI(0);
		rightM.setD(0);

	}

	public void autonomousInit() {
		waffle.reset();
		waffle.ahrs.reset();
		waffle.interpret();
//		waffle.left.setInverted(false);
//		waffle.right.setInverted(false);
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
//		isInverted = true;
//		waffle.left.setInverted(true);
//		waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);

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
		
		leftVelocity = leftM.getEncVelocity() / Wiring.PULSES_PER_INCH * 10;
		rightVelocity = (rightM.getEncVelocity() / Wiring.PULSES_PER_INCH * 10) / 12;
		
		
		SmartDashboard.putNumber("Left Encoder", leftM.getEncPosition());
		SmartDashboard.putNumber("Right Encoder", leftM.getPosition());
		SmartDashboard.putNumber("Current", leftM.getOutputCurrent());
	}

	public void sendRecieveCenterValues(String in) {
    	String cx = in.substring(0, (in.indexOf(",")));
    	String cy = in.substring(in.indexOf(",") + 1);
    	System.out.println(cx + " " + cy);
    	try{
    		waffle.cx = Integer.parseInt(cx);
    		waffle.cy = Integer.parseInt(cy);
    	} catch (NumberFormatException e){
    		return;
    	}
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
