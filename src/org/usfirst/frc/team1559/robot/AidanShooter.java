package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class AidanShooter extends IterativeRobot {

	final int fireButton = 6;
	final int YellowButton = 4;
	final int BlueButton = 3;
	final int RedButton = 2;
	final int GreenButton = 1;
	final int catDelay = 5;
	final int liftTopButton = 7;
	final int liftBotButton = 9;
	final int liftMidButton = 8;

	final double liftUp = 0.6;
	final double liftDown = -0.6;
	final double liftStop = 0.0;

	enum armStates {
		ATTOP, TOPMIDDOWN, ATMID, MIDBOTDOWN, ATBOT, BOTMIDUP, MIDTOPUP,
	}; // from, to and direction ex. from top to mid going down

	private RobotDrive robot;
	private Talon gatherLift;
	private Talon gatherRotate;
	private CANTalon leftM;
	private CANTalon leftS;
	private CANTalon rightM;
	private CANTalon rightS;
	private Joystick stick;
	private Solenoid shift1;
	private Solenoid shift2;
	private Solenoid fired;
	private Solenoid cocked;
	private Relay led;
	private DigitalInput catTop;
	private DigitalInput catBot;
	private DigitalInput gatherTop;
	private DigitalInput gatherBot;
	private PowerDistributionPanel pdp;
	private Counter counter;
	private DigitalInput magSensor;
	private boolean flag = false;

	public void robotInit() {
		// ahrs = new AHRS(SPI.Port.kMXP);
		leftM = new CANTalon(15);
		rightM = new CANTalon(16);
		leftS = new CANTalon(17);
		rightS = new CANTalon(18);

		gatherLift = new Talon(0);
		gatherRotate = new Talon(1);
		catTop = new DigitalInput(0);
		catBot = new DigitalInput(1);
		gatherTop = new DigitalInput(2);
		gatherBot = new DigitalInput(3);

		robot = new RobotDrive(leftM, rightM);
		pdp = new PowerDistributionPanel();
		stick = new Joystick(0);

		magSensor = new DigitalInput(9);
		counter = new Counter(magSensor);

		shift1 = new Solenoid(0);
		shift2 = new Solenoid(1);

		leftS.changeControlMode(CANTalon.TalonControlMode.Follower);// sets
		// motor to follower
		leftS.set(15);// sets to ID of master(leftM)
		rightS.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightS.set(16);

		led = new Relay(0);
		fired = new Solenoid(2);

		fired.set(false);
		gatherLift.set(0.0);
		gatherRotate.set(0.0);
		counter.reset();
	}

	public void autonomousInit() {
		cocked.set(true);
		catCount = 0;
		catState = 0;
	}

	public void autonomousPeriodic() {
		// fires catapult
		catapult(true);
		catapult(false);
	}

	double last = 0.0;

	public void teleopInit() {
		leftM.setInverted(true);
		led.setDirection(Direction.kForward);
		led.set(Value.kOff);
		gatherLift.set(0.0);
		gatherRotate.set(0.0);
		last = 0.0;
		counter.reset();
		flag = false;
	}

	int delay = 0;

	public void teleopPeriodic() {
		switch (delay) {
		case 0:
			if (stick.getRawButton(BlueButton))
				led.set((led.get() == Value.kOn) ? Value.kOff : Value.kOn);
			delay++;
			break;
		case 1:
			delay++;
			break;
		case 2:
			if (!stick.getRawButton(BlueButton))
				delay = 3;
			break;
		case 3:
			delay = 0;
			break;
		}
		// catapult(stick.getRawButton(fireButton));
		catapult1(stick.getRawButton(fireButton));

		theOptimalGatherer();

		if (pdp.getCurrent(13) != last) {
			System.out.println(pdp.getCurrent(13));
			last = pdp.getCurrent(13);
		}
		robot.arcadeDrive(stick);
		// we might want the robot to start in a certain gear

		if (stick.getRawButton(GreenButton)) {

			gear1();

		} else if (stick.getRawButton(RedButton)) {

			gear2();

		}
	}

	// switch high to low low to high gear
	int x = 1;
	int y = 1;

	public void gear1() {
		shift1.set(false);
		shift2.set(true);
		if (y == 1) {
			System.out.println("GEAR 1");
			y++;
		}
		x = 1;
	}

	public void gear2() {
		shift1.set(true);
		shift2.set(false);
		if (x == 1) {
			System.out.println("GEAR 2");
			x++;
		}
		y = 1;
	}

	public void testInit() {
		gatherLift.set(0.0);
		gatherRotate.set(0.0);
		counter.reset();
	}

	public void testPeriodic() {
		if (counter.get() != 0) {
			System.out.println("Transient captured");
			counter.reset();
		}
		if (!magSensor.get())
			System.out.println("Magnet sensed");
		if (!gatherTop.get())
			System.out.println("top");
		if (!gatherBot.get())
			System.out.println("bot");
	}

	public void disabledInit() {
		gatherLift.set(0.0);
		gatherRotate.set(0.0);
	}

	public void disabledPeriodic() {

	}

	private int catState = 0;
	private int catCount = 0;

	public void catapult1(boolean fire) {
		switch (catState) {
		case 0: // waiting for fire button
			if (fire) {
				fired.set(true);
				catState = 1;
				catCount = 0;
			}
			break;
		case 1: // waiting for catapult to move
			catCount++;
			if (catCount >= catDelay) {
				catState = 2;
				catCount = 0;
			}
			break;
		case 2: // recock the catapult
			fired.set(false);
			catState = 3;
			break;
		case 3: // wait for catapult to cock
			catCount++;
			if (catCount >= catDelay) {
				catState = 4;
				catCount = 0;
			}

		case 4: // wait for button to go false
			if (!fire)
				catState = 0;
			break;
		}
	}

	public void catapult(boolean fire) {
		switch (catState) {
		case 0:
			if (fire) { // add && ball sensor
				fired.set(true);
				catState = 1;
			}
			break;
		case 1:
			if (!catTop.get()) {
				fired.set(false);
				catState = 2;
			}
			break;
		case 2:
			if (!catBot.get()) {
				catState = 3;
			}
			break;
		case 3:
			if (!fire) {
				catState = 0;
			}
			break;
		}
	}

	int spinnerCount = 0;

	public void gathererGatheringSpinnyThingy() {
		switch (spinnerCount) {
		case 0:
			gatherRotate.set(0.0);
			if (stick.getRawButton(43))
				spinnerCount = 1;
			if (stick.getRawButton(42))
				spinnerCount = 2;
			break;
		case 1:
			gatherRotate.set(0.4);
			if (!stick.getRawButton(43))
				spinnerCount = 0;
			break;
		case 2:
			gatherRotate.set(0.4);
			if (!stick.getRawButton(42))
				spinnerCount = 0;
			break;
		case 3:

			break;
		}
	}

	armStates arm = armStates.ATTOP;
	armStates target = armStates.ATTOP;

	public void theOptimalGatherer() { // is talon based
		switch (arm) {
		case ATTOP:
			counter.reset();
			if (!flag) {
				System.out.println("At top");
				flag = true;
			}
			if (stick.getRawButton(liftBotButton)) {
				target = armStates.ATBOT;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
				flag = false;
			}
			if (stick.getRawButton(liftMidButton)) {
				target = armStates.ATMID;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
				flag = false;
			}
			break;
		case TOPMIDDOWN:
			if (!flag) {
				System.out.println("From top going mid");
				flag = true;
			}
			if (counter.get() != 0) {
				if (target == armStates.ATMID) {
					gatherLift.set(liftStop);
					arm = armStates.ATMID;
					flag = false;
				} else {
					arm = armStates.MIDBOTDOWN;
					flag = false;
				}
			}
			// change of plans
			if (stick.getRawButton(liftTopButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATTOP;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
				flag = false;
			}
			if (stick.getRawButton(liftBotButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATBOT;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
				flag = false;
			}

			break;
		case ATMID:
			counter.reset();
			if (!flag) {
				System.out.println("At middle");
				flag = true;
			}
			if (stick.getRawButton(liftTopButton)) {
				target = armStates.ATTOP;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
				flag = false;
			}
			if (stick.getRawButton(liftBotButton)) {
				target = armStates.ATBOT;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
				flag = false;
			}
			break;
		case MIDBOTDOWN:
			if (!flag) {
				System.out.println("From mid going bot");
				flag = true;
			}
			if (!gatherBot.get()) {
				arm = armStates.ATBOT;
				gatherLift.set(liftStop);
				flag = false;
			}
			// change of plans
			if (stick.getRawButton(liftMidButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATMID;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
				flag = false;
			}
			if (stick.getRawButton(liftTopButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATTOP;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
				flag = false;
			}
			break;
		case ATBOT:
			counter.reset();
			if (!flag) {
				System.out.println("At top");
				flag = true;
			}
			if (stick.getRawButton(liftTopButton)) {
				target = armStates.ATTOP;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
				flag = false;
			}
			if (stick.getRawButton(liftMidButton)) {
				target = armStates.ATMID;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
				flag = false;
			}
			break;
		case BOTMIDUP:
			if (!flag) {
				System.out.println("From bot going mid");
				flag = true;
			}
			if (counter.get() != 0) {
				if (target == armStates.ATMID) {
					gatherLift.set(liftStop);
					arm = armStates.ATMID;
					flag = false;
				} else {
					arm = armStates.MIDTOPUP;
					flag = false;
				}
			}
			// change of plans
			if (stick.getRawButton(liftTopButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATTOP;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
				flag = false;
			}
			if (stick.getRawButton(liftMidButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATMID;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
				flag = false;
			}

			break;
		case MIDTOPUP:
			if (!flag) {
				System.out.println("From mid going top");
				flag = true;
			}
			if (!gatherTop.get()) {
				arm = armStates.ATTOP;
				gatherLift.set(liftStop);
				flag = false;
			}
			// change of plans
			if (stick.getRawButton(liftMidButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATMID;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
				flag = false;
			}
			if (stick.getRawButton(liftBotButton)) {
				flag = false;
				if (!flag) {
					System.out.println("Changed");
					flag = true;
				}
				target = armStates.ATBOT;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
				flag = false;
			}
			break;
		}
	}
}