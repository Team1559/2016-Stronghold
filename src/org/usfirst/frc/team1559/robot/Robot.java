package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.CANTalon;
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {

	Climber climber;
	RobotDrive drive;
	int camWait = 0;
	PowerDistributionPanel pdp;
	boolean shootDone = false;
	int arduinoCounterForAlison = 0; // dont blame john pls
	CANTalon leftM;
	CANTalon leftS;
	CANTalon rightM;
	CANTalon rightS;
	Joystick stick;
	Joystick coStick;
	Timer timer;
	double desiredHeading = 0;
	boolean isInverted;
	WFFL waffle;
	Arduino arduino;
	Transmission tranny;
	int listPos = 0;
	SerialClient sc = new SerialClient();// using serial now because it's good
	double leftVelocity, rightVelocity;
	Shooter shooter;
	final boolean shooterInversion = false;
	// DigitalInput magneticSensor;
	DigitalOutput dio2;
	DigitalOutput dio1;
	File fillet;
	FileWriter filletWrite;
	int counter = 0;
	int gatherLevel = 0;

	BallClamp clamp;
//	Gatherer gatherer;
	GathererManualPID gatherer;
	// USBCamera cam;
	// CameraServer cs;
	SensorCarnageProtection scp;
	Flashlight deLight;
	public final double kP = 0.0135;
	int lineNum = 0;

	// Comments are for a 4 motor drive system whereas uncommented code just
	// does 2

	public void robotInit() {
		if (Wiring.hasArduino)
			arduino = new Arduino(8);
		leftM = new CANTalon(Wiring.LEFT_MASTER_TALON);
		rightM = new CANTalon(Wiring.RIGHT_MASTER_TALON);
		leftS = new CANTalon(Wiring.LEFT_SLAVE_TALON);
		rightS = new CANTalon(Wiring.RIGHT_SLAVE_TALON);

		leftS.changeControlMode(CANTalon.TalonControlMode.Follower);// sets
																	// motors to
																	// followers
		leftS.set(Wiring.LEFT_MASTER_TALON);// sets to ID of master(leftM)

		rightS.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightS.set(Wiring.RIGHT_MASTER_TALON);

		drive = new RobotDrive(leftM, rightM);
		stick = new Joystick(Wiring.JOYSTICK0);
		coStick = new Joystick(Wiring.JOYSTICK1);
		tranny = new Transmission(stick, leftM, rightM);
		shooter = new Shooter();

		deLight = new Flashlight();

		fillet = new File("/media/sda1/camlog.csv");
		try {
			filletWrite = new FileWriter(fillet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// shooter.initShooter(gatherer.shouldNotShoot());
		if (Wiring.hasBallClamp) {
			clamp = new BallClamp();
		}
		
		if(Wiring.HAS_CLIMBER)
			climber = new Climber();

		// magneticSensor = new DigitalInput(Wiring.MAGNET);
		// cam = new USBCamera("cam0");
		// cs = CameraServer.getInstance();
		drive.setExpiration(Double.MAX_VALUE);

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

		waffle = new WFFL(Wiring.WFFL_NAME, drive, tranny);

		dio1 = new DigitalOutput(0);
		dio2 = new DigitalOutput(1);

		tranny.resetEncoders();

		if (Wiring.hasGatherer) {
			gatherer = new GathererManualPID(Wiring.GATHERER_LIFT, Wiring.GATHERER_ROTATE, stick);
			// gatherer.initLifterPID(Wiring.GATHERER_PID_P,
			// Wiring.GATHERER_PID_I, Wiring.GATHERER_PID_D);
		}

		pdp = new PowerDistributionPanel(0);

		scp = new SensorCarnageProtection();

	}

	public void autonomousInit() {
		waffle.reset();
		if (Wiring.hasArduino)
			arduino.writeSequence(1);
		System.out.println("AHRS: " + waffle.getAHRS());
		waffle.resetAHRS();
		sc.reset();
		waffle.interpret();
		// System.out.println("JFKDSLFIUESHF " +
		// waffle.list.get(waffle.list.size() - 1).command);
		leftM.setInverted(false);
		rightM.setInverted(false);
		// waffle.resetLength();
		// shooter.resetShooter(gatherer.shouldNotShoot());
		tranny.resetEncoders();
		// givenAngle = false;
		// gatherer.updateAutoPosition();
	}

	// private boolean givenAngle = false;

	private int nate = 0;
	private int aimPause = 0;
	private double desiredYarAngle = 0.0;
	private int goodFrames = 0;
	private double oldAngle = 0.0;
	private boolean tick = true;
	private double ahrsAngle = 0.0;
	double cameraAngle = 0.0;
	
	private int angle = 0;

	public void autonomousPeriodic() {
		
		//TODO Add something to handle lowbar here ayy
//		gatherer.lowbarify();
		//potentially add something to hold the gatherer at thit position
		
		sc.sp.flush();
		 clamp.updateBallClampAbsolute(shooter.isShooting());

		// int rawError = sc.getSerialIn();

		// SmartDashboard.putNumber("Displacement", (tranny.getLDisplacement() +
		// tranny.getRDisplacement()) / 2);
		Command current = waffle.getList().get(listPos);
		if (current.command.equals("TURN")) {
			waffle.turnToAngle(current.angle);
			if (waffle.isTurning() == false) {
				desiredHeading = current.angle;
				current.done = true;
				// waffle.resetLength();
				tranny.resetEncoders();
			}
		} else if (current.command.equals("GO")) {
			waffle.drive(desiredHeading, current.dist, current.speed);
			System.out.println(tranny.getLDisplacement() + " "
					+ tranny.getRDisplacement());
			if (waffle.isRunning() == false) {
				leftM.set(0);
				rightM.set(0);
				current.done = true;
				tranny.resetEncoders();
				System.out.println("PLZ STOP NOW");
			}
		} else if (current.command.equals("SHOOT")) {

			System.out.println(waffle.getYawError());

			switch (nate) {

			case 0:
				sc.send("s");
				nate++;
				break;
			case 1:
				cameraAngle = sc.grabAngle();
				ahrsAngle = waffle.getCurrentAngle();
				if (counter > 3) {
					nate++;
					counter = 0;
				} else {
					if (Math.abs(cameraAngle) < 30) {
						counter++;
						nate = 0;
					} else {
						counter = 0;
					}
				}
				break;
			case 2:
				desiredYarAngle = cameraAngle + ahrsAngle;
				waffle.turnToAngle(desiredYarAngle);
				nate++;
				break;
			case 3:
				if (waffle.isTurning()) {
					waffle.turnToAngle(desiredYarAngle);
				} else {
					nate++;
				}
				break;
			case 4:
				// System.out.println("running again");

				if (camWait++ > 12) { //change this
					
					
					if(Math.abs(cameraAngle) <= Wiring.CAMERA_TOLERANCE){
						nate++;
						System.out.println("SHOOTING ROBITS" + cameraAngle);
						shooter.autoShoot(true, false); //maybe change this to gatherer.shouldnotshoot (?)
					} else {
						nate = 0;
					}
			
					camWait = 0;
				}

				break;
			case 5:
				if(!shooter.isShootDone()){
					shooter.autoShoot(true, false);
				} else {
					nate++;
				}
				break;
			case 6:
//				System.out.println("");
				break;

			}

		} else if (current.command.equals("STOP")) {
			leftM.set(0);
			rightM.set(0);
			// shooter.setSolenoids(false, gatherer.shouldNotShoot());
			// System.out.println("Motor Stoppage achieved!");
		} else if (current.command.equals("DEFENSE")) {
			String id = current.id;
			if (id.equals("rock_wall")) {
				gatherer.homify();
			} else {
				gatherer.lowbarify();
			}
			
			if (!following) {
				playbackSetup(id);
			}
			playbackIterative();
			current.done = doneFollowing;
		} else if (current.command.equals("WAIT")) {

			leftM.set(0);
			rightM.set(0);

			long brandonThomasBoje = System.currentTimeMillis();
			brandonThomasBoje += current.time * 1000;

			System.out.println("waiting...");

			while (System.currentTimeMillis() < brandonThomasBoje)
				;
			// sup ladies

			current.done = true;

		}

		// when a command is completed, we reset and move on to the next one
		if ((current.done == true)) {
			System.out.println("Current done boi");
			current.done = false;
			// if (Wiring.hasGatherer)
			// gatherer.homify();// make sure the gatherer is out of the way

			if (waffle.getList().size() - 1 > listPos) {
				listPos++;
				// waffle.resetLength();
				waffle.setRunning(true);
				waffle.setTurning(true);
			} else {
				leftM.set(0);
				rightM.set(0);

			}

		}

		if (fillet.exists()) {
			try {
				filletWrite.write(String.valueOf(nate) + ",");
				filletWrite.write(Double.toString(cameraAngle) + ",");
				filletWrite.write(Double.toString(waffle.getLeftM()) + ",");
				filletWrite.write(Double.toString(waffle.getRightM()) + ",");
				filletWrite.write(String.valueOf(waffle.isTurning()) + ",");
				filletWrite.write(String.valueOf(waffle.getYawError()));
				if (shooter.isShooting()) {
					filletWrite.write("SHOOTING!,");
				}
				filletWrite.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("well crap, no file :(");
		}

		tick = !tick;
	}

	public void teleopInit() {
		// arduino.writeSequence(2);
		// isInverted = true;
		// waffle.left.setInverted(true);
		// waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);
		// centerWithAngle();
		// cs.startAutomaticCapture();
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);
		leftM.setInverted(true);
		rightM.setInverted(true);
		// shooter.resetShooter(gatherer.shouldNotShoot());
		tranny.resetEncoders();
		// initRecord();
		// playbackSetup();

		if (DriverStation.getInstance().getAlliance() == DriverStation.Alliance.Blue) {
			dio1.set(true);
			dio2.set(false);
		} else {
			dio1.set(false);
			dio2.set(true);
		}

		desiredYarAngle = 0;

	}

	public void teleopPeriodic() {
		// recordPeriodic();
		// playbackIterative();

		shooter.updateShooter(stick, false); //should not shoot if true
		
		if (tranny.getGear() == 1) {
			drive.arcadeDrive(stick.getY() * Wiring.LOW_SPEED_MULTIPLIER,
					-stick.getRawAxis(4) * Wiring.LOW_SPEED_MULTIPLIER);
		} else {
			drive.arcadeDrive(stick.getY(), -stick.getRawAxis(4));
		}

		if (coStick.getRawButton(11)) {
			scp.killEncoders();
		} else if (coStick.getRawButton(12)) {
			scp.unKillEncoders();
		}
		// SmartDashboard.putBoolean("BALL IN!", !clamp.isOpen());

		deLight.updateLight(coStick);

		if (!scp.encodersDead()) {
			tranny.updateShifting();
		} else {
			tranny.limpShifting();
		}

		
		//kick me
		if(coStick.getRawButton(4)){
			climber.arm();
		} else {
			climber.disarm();
		}
		
		if(climber.isArmed()) {
			if(coStick.getRawAxis(1) <= -.8) {
				climber.drive();				
			} else if (coStick.getRawAxis(1) >= 0.8){
				climber.reverse();
			} else {
				climber.stopIt();
			}
		} else {
			climber.stopIt();
		}
		
		if (Wiring.hasGatherer) {
			if (!coStick.getRawButton(1)) {
				gatherer.manualControl();
				
				
				/*
				gatherer.updatePID();
				switch(gatherLevel){
				case 0:
					//at home
					if(stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)){
						gatherer.setSetpoint(Wiring.GATHER_MID_TARGET);
					} 
					break;
				case 1:
					//mid
					
					if(stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)){
						gatherer.setSetpoint(Wiring.GATHER_HOME_TARGET);
					} else if(stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)){
						gatherer.setSetpoint(Wiring.GATHER_BOTTOM_TARGET); //bottom lol (like butt)
						
					}
					
					break;
				case 2:
					//bott (butt) lol
					if(stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)){
						gatherer.setSetpoint(Wiring.GATHER_MID_TARGET);
					} 
					break;
				}
				*/
				
				System.out.println(gatherer.getPot());
			} else {
				if (coStick.getRawAxis(1) > .5) {
					gatherer.copilotManualControlDOWN();
				} else if (coStick.getRawAxis(1) < -.5) {
					gatherer.copilotManualControlUP();
				} else {
					gatherer.stopDriving();
				}
			}
			// gatherer.manualControl();
		}

		// shooter.updateShooter(stick, /*gatherer.shouldNotShoot()*/ false);
		// //this is correct!

		if (Wiring.hasBallClamp){
			
			clamp.updateBallClampAbsolute(stick.getRawButton(3) || shooter.isShooting());
			
			//kicker
			if(clamp.isOpen()){
				if(stick.getRawButton(3)){
					clamp.kick();
				} else {
					clamp.resetKick();
				}
			}
			
		}
			

		// ADD THIS IN IF YOU WANT TO MANUALLY DRIVE THE BALL CLAMP
		// clamp.updateBallClamp(shooter.shooting ||
		// stick.getRawButton(Wiring.BALL_CLAMP_OVERRIDE_BUTT));

		if (Wiring.hasGatherer) {
			if (stick.getRawButton(1)) {
				gatherer.setSpark(0.5);
			} else if(stick.getRawButton(4)) {
				gatherer.setSpark(-0.5);
			} else {
				gatherer.setSpark(0.0);
			}

		}

		// if (coStick.getRawButton(1)) {
		// arduino.writeSequence(3);
		// }

		// if (DriverStation.getInstance().getMatchTime() <= 20) {
		// arduino.writeSequence(4);
		// }
		//
		// if (arduinoCounterForAlison - lastVal == 150) {
		// arduino.writeSequence(1);
		// }
		arduinoCounterForAlison++;
	}

	// private final int CAMERA_BAND = 10;

	public double centerWithAngle(int error) {
		System.out.println(error);
		return error / 13.0;// 30 = half of horizontal fov of camera
	}

	public void testInit() {

	}

	public void testPeriodic() {
		
//		System.out.println(gatherer.getPot());
//		gatherer.manualControl();
		System.out.println(clamp.readSensor());
		
	}

	public void disabledInit() {
		// arduino.writeSequence(0);
	}

	public void disabledPeriodic() {
		// roit gets rekt
		// git noodled
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
			johnKennethDunaske = new Scanner(new File("/media/sda1/" + id
					+ ".wfflt"));
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
