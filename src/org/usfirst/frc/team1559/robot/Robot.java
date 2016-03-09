package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends IterativeRobot {

	RobotDrive drive;
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

	BallClamp clamp;
	Gatherer gatherer;
	// USBCamera cam;
	CameraServer cs;

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
		// shooter.initShooter(gatherer.shouldNotShoot());
		if (Wiring.hasBallClamp) {
			clamp = new BallClamp();
		}

		// magneticSensor = new DigitalInput(Wiring.MAGNET);
		// cam = new USBCamera("cam0");
		cs = CameraServer.getInstance();
		drive.setExpiration(Double.MAX_VALUE);

		leftM.setInverted(true);
		rightM.setInverted(true);

		// encoder stuff
		leftM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);
		rightM.configEncoderCodesPerRev(Wiring.PULSES_PER_INCH);

		leftM.enableBrakeMode(true);
		rightM.enableBrakeMode(true);
		/*
		 * 
		 * 
		 * 
		 * 
		 * CHANGE THIS LATER!!!!!
		 */
		leftS.enableBrakeMode(false);
		rightS.enableBrakeMode(false);

		rightM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		leftM.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		rightS.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);
		leftS.setVoltageRampRate(Wiring.VOLTAGE_RAMP_RATE);

		waffle = new WFFL(Wiring.WFFL_NAME, drive, tranny);

		dio1 = new DigitalOutput(0);
		dio2 = new DigitalOutput(1);

		tranny.resetEncoders();

		if (Wiring.hasGatherer) {
			gatherer = new Gatherer(Wiring.GATHERER_LIFT,
					Wiring.GATHERER_ROTATE, stick);
			// gatherer.initLifterPID(Wiring.GATHERER_PID_P,
			// Wiring.GATHERER_PID_I, Wiring.GATHERER_PID_D);
		}

		pdp = new PowerDistributionPanel(0);

	}

	public void autonomousInit() {
		waffle.reset();
		if (Wiring.hasArduino)
			arduino.writeSequence(1);
		System.out.println("AHRS: " + waffle.ahrs);
		waffle.ahrs.reset();
		waffle.interpret();
		// System.out.println("JFKDSLFIUESHF " +
		// waffle.list.get(waffle.list.size() - 1).command);
		leftM.setInverted(false);
		rightM.setInverted(false);
		waffle.length = 0;
		shooter.resetShooter(/* gatherer.shouldNotShoot() */false);
		tranny.resetEncoders();
		// givenAngle = false;
		// gatherer.updateAutoPosition();
	}

	// private boolean givenAngle = false;
	private double angle = 0.0;
	private int nate = 0;
	private int aimPause = 0;
	private double desiredYarAngle = 0.0;
	private int goodFrames = 0;
	private double oldAngle = 0.0;
	private boolean tick = true;

	public void autonomousPeriodic() {

		// int rawError = sc.getSerialIn();

		// SmartDashboard.putNumber("Displacement", (tranny.getLDisplacement() +
		// tranny.getRDisplacement()) / 2);
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
			
			System.out.println("YAY" + goodFrames);
			
			if(tick){
			
			switch (nate) {
			case 0:
//					aimPause = 0;
//					System.out.println("Trying to turn...");
//					oldAngle = angle;
					angle = sc.grabAngle(); 
					
					if(angle != -1000){
						desiredYarAngle = waffle.ahrs.getYaw() + angle;
					} else {
						desiredYarAngle = waffle.ahrs.getYaw();
					}
					

//					System.out.println(angle);
					
					if (Math.abs(angle) <= Wiring.CAMERA_TOLERANCE) {
						 goodFrames++;
						 
						if(goodFrames >= 4){
							nate = 2;
						}
						
						
						System.out.println("GOING TO SHOOT WILL:" + angle + " YAR " + waffle.ahrs.getYaw());
					} else {

						nate = 1;
						goodFrames = 0;
						// waffle.keepTurning = true; //this should only be
						// controlled by WFFLDrive
						waffle.turnToAngle(desiredYarAngle); 
					}

				break;
			case 1:
				
				if (waffle.keepTurning) {
					waffle.turnToAngle(desiredYarAngle);
					System.out.println("KEEP TURNING");
				} else {
					nate = 2;
				}

				// if (Math.abs(sc.getSerialIn()) > 2){ //ADD THIS TO WIRING
				// CLASS!
				// if(aimPause++ > 5){
				// System.out.println("REGETTING");
				// waffle.keepTurning = true;
				// nate = 0;
				// }
				// } else {
				// nate = 2;
				//
				// }
				break;
			case 2:
				if (!shooter.isShootDone()) {
					System.out.println("SHOOT RIGHT MEOW");
					/*
					 * THIS IS NOT SAFE
					 */
					// TODO: MAKE IT SAFE PLEASE
					shooter.autoShoot(true, /* gatherer.shouldNotShoot() */
							false);
				} else {
					nate = 3;
					
				}
				break;
			case 3:
				current.done = true;
				tranny.resetEncoders();
				System.out.println("DONE SHOOTING!");
				break;
			}
			
			tick = !tick;
			
		} else {
			tick = !tick;
//			System.out.println("nope.");
			
		}
			
			waffle.turnToAngle(desiredYarAngle);

		} else if (current.command.equals("STOP")) {
			leftM.set(0);
			rightM.set(0);
			shooter.setSolenoids(false, /* gatherer.shouldNotShoot() */false);
//			System.out.println("Motor Stoppage achieved!");
		} else if (current.command.equals("DEFENSE")) {
			String id = current.id;
			if (id.equals("lowbar")) {
				gatherer.lowbarify(); // this should work to get us under the
										// low bar.
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
			if (Wiring.hasGatherer)
				gatherer.homify();// make sure the gatherer is out of the way

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
		// arduino.writeSequence(2);
		// isInverted = true;
		// waffle.left.setInverted(true);
		// waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);
		// centerWithAngle();
		cs.startAutomaticCapture();
		rightM.setEncPosition(0);
		leftM.setEncPosition(0);
		leftM.setInverted(true);
		rightM.setInverted(true);
		shooter.resetShooter(/* gatherer.shouldNotShoot() */false);
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

	}

	public void teleopPeriodic() {
		// int lastVal = 0;
		// sendRecieveCenterValues();
		// waffle.myRobot.arcadeDrive(stick); //FOR THE TEST CHASSIS
		// TODO: transmission "gear" variable was never really implemented,
		// rendering this if statement pointless. Reconfirm that driving still
		// works ok.
		if (tranny.getGear() == 1) {
			drive.arcadeDrive(stick.getY() * Wiring.LOW_SPEED_MULTIPLIER,
					-stick.getRawAxis(4) * Wiring.LOW_SPEED_MULTIPLIER);
		} else {
			drive.arcadeDrive(stick.getY(), -stick.getRawAxis(4));
		}

		// SmartDashboard.putBoolean("BALL IN!", !clamp.isOpen());

		// recordPeriodic();
		// playbackIterative();

		tranny.updateShifting();

		if (Wiring.hasGatherer) {
			if (stick.getRawButton(Wiring.BTN_GATHERER_OVERRIDE)) {
				// gatherer.disableLifterPID();
				gatherer.manualControl();
			} else {
				// gatherer.gathererTalon();
				// gatherer.manualControl(); // TODO: what?
				// gatherer.updateAutoPosition();
			}
		}

		System.out.println(pdp.getCurrent(0) + " " + pdp.getCurrent(1) + " "
				+ pdp.getCurrent(2) + " " + pdp.getCurrent(3));

		shooter.updateShooter(stick, /* gatherer.shouldNotShoot() */false);

		if (Wiring.hasBallClamp)
			clamp.updateBallClamp(shooter.isShooting());
		// ADD THIS IN IF YOU WANT TO MANUALLY DRIVE THE BALL CLAMP
		// clamp.updateBallClamp(shooter.shooting ||
		// stick.getRawButton(Wiring.BALL_CLAMP_OVERRIDE_BUTT));

		if (Wiring.hasGatherer) {
			if (stick.getRawButton(1) && clamp.isOpen()) {
				gatherer.setSpark(0.5);
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
		// System.out.println("Gatherer Gyro:" + gatherer.getGyro().getAngle());
		// System.out.println("BALL SENSOR " + clamp.opSensor.getVoltage());
		// SmartDashboard.putBoolean("Gatherer LS",
		// gatherer.isLimitSwitchTripped());
		// System.out.println(gatherer.isLimitSwitchTripped());
		// gatherer.manualControl();
		// gatherer.updatePosition();
		// gatherer.gathererTalon();
		// if (stick.getRawButton(6)) {
		// clamp.close();
		// } else {
		// clamp.open();
		// }
		// clamp.updateBallClamp(shooter.isShooting());
		// System.out.println(shooter.shooting);
		// if (stick.getRawButton(5) && clamp.open) {
		// gatherer.setSpark(0.5);
		// } else {
		// gatherer.setSpark(0.0);
		// }

		if (tranny.getGear() == 1) {
			drive.arcadeDrive(stick.getY() * Wiring.LOW_SPEED_MULTIPLIER,
					-stick.getRawAxis(4) * Wiring.LOW_SPEED_MULTIPLIER);
		} else {
			drive.arcadeDrive(stick.getY(), -stick.getRawAxis(4));
		}

		if (stick.getRawButton(1)) {
			tranny.gear1();
		} else if (stick.getRawButton(2)) {
			tranny.gear2();
		}

		System.out.println(leftM.getEncPosition());

		// shooter.updateShooter(stick, /*gatherer.shouldNotShoot()*/false);
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
