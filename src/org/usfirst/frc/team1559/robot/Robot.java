package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.CANTalon;
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
	Arduino arduino;
	Transmission tranny;
	int listPos = 0;
	SerialClient sc = new SerialClient();//using serial now because it's good
	double leftVelocity, rightVelocity;
	Shooter shooter;
	DriverStation driverstation;
	final boolean shooterInversion = false;
	// DigitalInput magneticSensor;
	DigitalOutput dio2;
	DigitalOutput dio1;
	Joystick coStick;
	BallClamp clamp;
	Gatherer gatherer;

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
		coStick = new Joystick(Wiring.JOYSTICK1);
		tranny = new Transmission(stick, leftM, rightM);
		shooter = new Shooter();
		shooter.initShooter();
		clamp = new BallClamp();
		// magneticSensor = new DigitalInput(Wiring.MAGNET);

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
		rightS.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		leftS.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);

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

		waffle = new WFFL(Wiring.WFFL_NAME, robot, rightM, leftM, tranny);

		dio1 = new DigitalOutput(0);
		dio2 = new DigitalOutput(1);
		
		tranny.resetEncoders();
		
		gatherer = new Gatherer();
		gatherer.initGatherers(Wiring.GATHERER_LIFT, Wiring.GATHERER_ROTATE, stick);

	}

	public void autonomousInit() {
		waffle.reset();
		arduino.writeSequence(1);
		waffle.ahrs.reset();
		waffle.interpret();
		System.out.println("JFKDSLFIUESHF " + waffle.list.get(waffle.list.size() - 1).command);
		leftM.setInverted(false);
		rightM.setInverted(false);
		waffle.length = 0;
		shooter.initShooter();
		tranny.resetEncoders();
	}

	public void autonomousPeriodic() {
		SmartDashboard.putNumber("Displacement", (tranny.getLDisplacement() + tranny.getRDisplacement())/2);
		Command current = waffle.list.get(listPos);
		if (current.command.equals("TURN")) {
			waffle.turnToAngle(current.angle);
			if (waffle.keepTurning == false) {
				desiredHeading = current.angle;
				current.done = true;
				waffle.length = 0;
				tranny.resetEncoders();
			}
		} else if (current.command.equals("GO")) {
			waffle.drive(desiredHeading, current.dist, current.speed);
			if (waffle.keepRunning == false) {
				leftM.set(0);
				rightM.set(0);
				current.done = true;
				tranny.resetEncoders();
				System.out.println("PLZ STOP NOW");
			}
		} else if (current.command.equals("SHOOT")) {
			// sendRecieveCenterValues("");
			leftM.set(0);
			rightM.set(0);
			shooter.updateShooter(true);
			if (shooter.shootDone) {
				current.done = true;
				tranny.resetEncoders();
				System.out.println("DONE SHOOTING!");
			}

		} else if (current.command.equals("STOP")) {
			leftM.set(0);
			rightM.set(0);
			shooter.setSolenoids(false);
			System.out.println("Motor Stoppage achieved!");
		} else if (current.command.equals("DEFENSE")) {
			String id = current.id;
			if(!following) {
				playbackSetup(id);
			}
			playbackIterative();
			current.done = doneFollowing;
		} else if (current.command.equals("WAIT")) {

			leftM.set(0);
			rightM.set(0);
			
			long brandonThomasBoje = System.currentTimeMillis();
			brandonThomasBoje += current.time*1000;
			
			System.out.println("waiting...");
			
			while(System.currentTimeMillis() < brandonThomasBoje);
			//sup ladies			
			
			current.done = true;
		}

		// when a command is completed, we reset and move on to the next one
		if ((current.done == true)) {
			System.out.println("Current done boi");
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
		arduino.writeSequence(2);
		// isInverted = true;
		// waffle.left.setInverted(true);
		// waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);
//		centerWithAngle();
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);
		leftM.setInverted(true);
		rightM.setInverted(true);
		shooter.initShooter();
		tranny.resetEncoders();
//		initRecord();
		// playbackSetup();

		if (DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue) {
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
		if(tranny.gear == 1){
			robot.arcadeDrive(stick.getY()*Wiring.LOW_SPEED_MULTIPLIER, -stick.getRawAxis(4)*Wiring.LOW_SPEED_MULTIPLIER);
		} else {
			robot.arcadeDrive(stick.getY(), -stick.getRawAxis(4));
		}
		
//		recordPeriodic();
		// playbackIterative();

		SmartDashboard.putNumber("Voltage", (leftM.getOutputVoltage() + rightM.getOutputVoltage())/2);
		SmartDashboard.putNumber("Velocity", (tranny.getRVelocity() + tranny.getLVelocity())/2);
		
		tranny.updateShifting();
		

		shooter.updateShooter(stick);
		clamp.updateBallClamp(stick);
		if (coStick.getRawButton(1)) {
			arduino.writeSequence(3);
		}
		
		 if (driverstation.getInstance().getMatchTime() <= 20) {
			 arduino.writeSequence(4);
		 }
	}
	
	public void centerWithAngle(){// comes in as error,angle,distance
		String in = sc.read();
		String err = in.substring(0, in.indexOf(","));
		String temp = in.substring(in.indexOf(","));
		String ang = temp.substring(0, temp.indexOf(","));
		String dist = temp.substring(temp.indexOf(","));
		int error = 0;
		double angle = 0;
		try{
			error = Integer.parseInt(err);
			angle = Double.parseDouble(ang);
		} catch (Exception e){
			
		}
		if (error < 0){
			angle = angle * -1;
		}
		double currentAngle = waffle.getCurrentAngle();
		waffle.turnToAngle(currentAngle + angle);
	}
	
	public void testInit() {

	}

	public void testPeriodic() {

	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {
		// roit gets rekt
		arduino.writeSequence(0);
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

	public void initRecord() {
		try {
			file.createNewFile();
			writer = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void recordPeriodic() {
		try {
			writer.write(String.valueOf(leftM.get()) + ",");
			System.out.println("writing fo' real!");
			writer.write(String.valueOf(rightM.get()) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Scanner johnKennethDunaske;
	String[] ryanWilliamLuu;
	boolean doneFollowing = false;
	boolean following = false;

	public void playbackSetup(String id) {
		doneFollowing = false;
		System.out.println(id);
		try {
			johnKennethDunaske = new Scanner(new File("/media/sda1/" + id + ".wfflt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		following = true;
	}

	int wffltPos = 0;

	public void playbackIterative() {
		if (johnKennethDunaske.hasNextLine()) {
			doneFollowing = false;
			String raw = johnKennethDunaske.nextLine();
			ryanWilliamLuu = raw.split(",");

			double leftVal = Double.valueOf(ryanWilliamLuu[wffltPos]);
			wffltPos++;
			double rightVal = Double.valueOf(ryanWilliamLuu[wffltPos]);
			wffltPos = 0;
			rightM.set(rightVal);
			leftM.set(leftVal);
		} else {
			following = false;
			doneFollowing = true;
			leftM.set(0);
			rightM.set(0);
			tranny.resetEncoders();
		}
	}
}
