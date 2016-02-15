package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	
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
	SocketClient sc = new SocketClient();
	double leftVelocity, rightVelocity;
	Shooter shooter;
	final boolean shooterInversion = false;
//	DigitalInput magneticSensor;
	DigitalOutput dio2;
	DigitalOutput dio1;
	

	// Comments are for a 4 motor drive system whereas uncommented code just
	// does 2

	public void robotInit() {
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
		tranny = new Transmission(stick, leftM, rightM);
		shooter = new Shooter();
		shooter.initShooter();
//		magneticSensor = new DigitalInput(Wiring.MAGNET);
		
		robot.setExpiration(Double.MAX_VALUE);

		leftM.setInverted(true);
		rightM.setInverted(true);

		// encoder stuff
		leftM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);
		rightM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);

		leftM.enableBrakeMode(true);
		rightM.enableBrakeMode(true);
		leftS.enableBrakeMode(true);
		rightS.enableBrakeMode(true);

		rightM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		leftM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);

		// leftM.changeControlMode(TalonControlMode.Speed);
		// leftM.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		// leftM.reverseSensor(false);
		// leftM.configNominalOutputVoltage(+0.0f, -0.0f);
		// leftM.configPeakOutputVoltage(+12.0f, -12.0f);
		// leftM.setProfile(0);
		// leftM.setF(1);
		// leftM.setP(0);
		// leftM.setI(0);
		// leftM.setD(0);
		//
		// rightM.changeControlMode(TalonControlMode.Speed);
		// rightM.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		// rightM.reverseSensor(false);
		// rightM.configNominalOutputVoltage(+0.0f, -0.0f);
		// rightM.configPeakOutputVoltage(+12.0f, -12.0f);
		// rightM.setProfile(0);
		// rightM.setF(1);
		// rightM.setP(0);
		// rightM.setI(0);
		// rightM.setD(0);

		waffle = new WFFL("/media/sda1/waffle.wffl", robot, rightM, leftM, tranny);
		
		dio1 = new DigitalOutput(0);
		dio2 = new DigitalOutput(1);
		
	}

	public void autonomousInit() {
		waffle.reset();
		waffle.ahrs.reset();
		waffle.interpret();
		leftM.setInverted(false);
		rightM.setInverted(false);
		waffle.length = 0;
		shooter.initShooter();

	}

	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Yaw:", waffle.yaw);
		System.out.println(waffle.ahrs.getYaw());
		Command current = waffle.list.get(listPos);
		if (current.command.equals("TURN")) {
			waffle.turnToAngle(current.angle);
			if (waffle.keepTurning == false) {
				desiredHeading = current.angle;
				current.done = true;
				waffle.length = 0;
			}
		} else if (current.command.equals("GO")) {
			waffle.drive(desiredHeading, current.dist, current.speed);
			if (waffle.keepRunning == false) {
				current.done = true;
			}
		} else if (current.command.equals("SHOOT")) {
			// sendRecieveCenterValues("");
			leftM.set(0);
			rightM.set(0);
			shooter.updateShooter(true);
			if (shooter.shootDone) {
				current.done = true;
				System.out.println("DONE SHOOTING!");
			}

		} else if (current.command.equals("STOP")) {
			leftM.set(0);
			rightM.set(0);
			shooter.setSolenoids(false);
		} else if(current.equals("DEFENSE")){
			String id = current.id;
			
		} else if (current.command.equals("WAIT")) {
		
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
				leftM.set(0);
				rightM.set(0);
			}

		}
	}

	public void teleopInit() {
		// isInverted = true;
		// waffle.left.setInverted(true);
		// waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);
		leftM.setInverted(true);
		rightM.setInverted(true);
		shooter.initShooter();
		initRecord();
//		playbackSetup();
		
		if(DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue){
			dio1.set(true);
			dio2.set(false);
		} else {
			dio1.set(false);
			dio2.set(true);
		}
		

	}

	public void teleopPeriodic() {
		// sendRecieveCenterValues();
		// waffle.myRobot.arcadeDrive(stick); //FOR THE TEST CHASSIS
		robot.arcadeDrive(stick.getY(), -stick.getRawAxis(4));
		recordPeriodic();
//		playbackIterative();
		
		// waffle.Traction();
		
//		System.out.println(magneticSensor.get());
//		SmartDashboard.putNumber("Right Encoder", getRVelocity());
//		SmartDashboard.putNumber("Left Encoder", getLVelocity());

//		if (stick.getRawButton(3)) {
//
//			tranny.gear1();
//
//		} else if (stick.getRawButton(4)) {
//
//			tranny.gear2();
//
//		}
		
//		tranny.updateShifting();
//
//		shooter.updateShooter(stick);


	}

	public void sendRecieveCenterValues(String in) {
		String cx = in.substring(0, (in.indexOf(",")));
		String cy = in.substring(in.indexOf(",") + 1);
		System.out.println(cx + " " + cy);
		try {
			waffle.cx = Integer.parseInt(cx);
			waffle.cy = Integer.parseInt(cy);
		} catch (NumberFormatException e) {
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
		//roit gets rekt
	}

	public int getRDisplacement() {
		return -(rightM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}

	public int getLDisplacement() {
		return (leftM.getEncPosition() / Wiring.PULSES_PER_INCH);
	}
	
	public int getRVelocity() {
		return -(rightM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}

	public int getLVelocity() {
		return (leftM.getEncVelocity() / Wiring.PULSES_PER_INCH);
	}
	
    
	File file = new File("/media/sda1/belgian.wfflt");
	FileWriter writer;
	public void initRecord(){
		try {
			file.createNewFile();
			writer = new FileWriter(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void recordPeriodic(){
		try {
			writer.write(String.valueOf(leftM.get()) + ",");
			System.out.println("writing fo' real!");
			writer.write(String.valueOf(rightM.get()) + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Scanner johnKennethDunaske;
	String[] ryanWilliamLuu;
	boolean doneFollowing;
	public void playbackSetup(){
		doneFollowing = false;
		try {
			johnKennethDunaske = new Scanner(new File("/media/sda1/rockwall.wfflt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	int wffltPos = 0;
	public void playbackIterative(){
		
		if(johnKennethDunaske.hasNextLine()){
			String raw = johnKennethDunaske.nextLine();
			ryanWilliamLuu = raw.split(",");
			
			double leftVal = Double.valueOf(ryanWilliamLuu[wffltPos]);
			wffltPos++;
			double rightVal = Double.valueOf(ryanWilliamLuu[wffltPos]);
			wffltPos = 0;
			rightM.set(-rightVal);
			leftM.set(-leftVal);
		} else {
			doneFollowing = true;
			leftM.set(0);
			rightM.set(0);
		}
	}
}
