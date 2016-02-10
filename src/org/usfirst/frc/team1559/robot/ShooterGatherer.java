package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 * Includes methods for shooting and gathering.
 */
public class ShooterGatherer {

	// final int fireButton = 6;
	// final int YellowButton = 4;
	// final int BlueButton = 3;
	// final int RedButton = 2;
	// final int GreenButton = 1;
	final int CAT_DELAY = 5;
	//
	// private RobotDrive robot;
	private Talon gatherLift;
	private Talon gatherRotate;
	// private CANTalon leftM;
	// private CANTalon leftS;
	// private CANTalon rightM;
	// private CANTalon rightS;
	// private Joystick stick;
	// private Solenoid shift1;
	// private Solenoid shift2;
	private Solenoid solShooter;
	// private Solenoid cocked;
	// private Relay led;
	private DigitalInput diShooterTop;
	private DigitalInput diShooterBot;
	private DigitalInput diGathererTop;
	private DigitalInput diGathererBot;
	// private PowerDistributionPanel pdp;
	private Counter counter;
	//
	// public void robotInit() {
	// // ahrs = new AHRS(SPI.Port.kMXP);
	// leftM = new CANTalon(15);
	// rightM = new CANTalon(16);
	// leftS = new CANTalon(17);
	// rightS = new CANTalon(18);
	// gatherLift = new Talon(0);
	// gatherRotate = new Talon(1);
	// catTop = new DigitalInput(0);
	// catBot = new DigitalInput(1);
	// gatherTop = new DigitalInput(2);
	// gatherBot = new DigitalInput(3);
	// robot = new RobotDrive(leftM, rightM);
	// pdp = new PowerDistributionPanel();
	// stick = new Joystick(0);
	// counter = new Counter();
	// shift1 = new Solenoid(0);
	// shift2 = new Solenoid(1);
	// leftS.changeControlMode(CANTalon.TalonControlMode.Follower);// sets
	// // motor to follower
	// leftS.set(15);// sets to ID of master(leftM)
	// rightS.changeControlMode(CANTalon.TalonControlMode.Follower);
	// rightS.set(16);
	// fired = new Solenoid(2);
	// led = new Relay(0);
	// fired.set(false);
	// gatherLift.set(0.0);
	// gatherRotate.set(0.0);
	// counter.reset();
	// }
	//
	// public void autonomousInit() {
	// cocked.set(true);
	// catCount = 0;
	// catState = 0;
	// }
	//
	// public void autonomousPeriodic() {
	// // fires catapult
	// catapult(true);
	// catapult(false);
	// }
	//
	// double last = 0.0;
	//
	// public void teleopInit() {
	// leftM.setInverted(true);
	// led.setDirection(Direction.kForward);
	// led.set(Value.kOff);
	// gatherLift.set(0.0);
	// gatherRotate.set(0.0);
	// last = 0.0;
	// gatherCount = 0;
	// counter.reset();
	// }
	//
	// int delay = 0;
	//
	// public void teleopPeriodic() {
	// switch (delay) {
	// case 0:
	// if (stick.getRawButton(BlueButton))
	// led.set((led.get() == Value.kOn) ? Value.kOff : Value.kOn);
	// delay++;
	// break;
	// case 1:
	// delay++;
	// break;
	// case 2:
	// if (!stick.getRawButton(BlueButton))
	// delay = 3;
	// break;
	// case 3:
	// delay = 0;
	// break;
	// }
	// // catapult(stick.getRawButton(fireButton));
	// catapult1(stick.getRawButton(fireButton));
	// gathererSpark();
	// if (pdp.getCurrent(13) != last) {
	// System.out.println(pdp.getCurrent(13));
	// last = pdp.getCurrent(13);
	// }
	// robot.arcadeDrive(stick);
	// // we might want the robot to start in a certain gear
	//
	// if (stick.getRawButton(GreenButton)) {
	//
	// gear1();
	//
	// } else if (stick.getRawButton(RedButton)) {
	//
	// gear2();
	//
	// }
	// }
	//
	// // switch high to low low to high gear
	// int x = 1;
	// int y = 1;
	//
	// public void gear1() {
	// shift1.set(false);
	// shift2.set(true);
	// if (y == 1) {
	// System.out.println("GEAR 1");
	// y++;
	// }
	// x = 1;
	// }
	//
	// public void gear2() {
	// shift1.set(true);
	// shift2.set(false);
	// if (x == 1) {
	// System.out.println("GEAR 2");
	// x++;
	// }
	// y = 1;
	// }
	//
	// public void testInit() {
	// gatherLift.set(0.0);
	// gatherRotate.set(0.0);
	// }
	//
	// public void testPeriodic() {
	// if (!gatherTop.get())
	// System.out.println("top");
	// if (!gatherBot.get())
	// System.out.println("bot");
	// }
	//
	// public void disabledInit() {
	// gatherLift.set(0.0);
	// gatherRotate.set(0.0);
	// }
	//
	// public void disabledPeriodic() {
	//
	// }
	//
	private int shootState = 0;
	private int catCount = 0;

	/**
	 * 
	 * @param
	 */
	public void shooter1(Joystick input) {
		switch (shootState) {
		case 0: // waiting for fire button
			if (input.getRawButton(Wiring.BTN_SHOOT)) {
				solShooter.set(true);
				shootState = 1;
				catCount = 0;
			}
			break;
		case 1: // waiting for catapult to move
			catCount++;
			if (catCount >= CAT_DELAY) {
				shootState = 2;
				catCount = 0;
			}
			break;
		case 2: // recock the catapult
			solShooter.set(false);
			shootState = 3;
			break;
		case 3: // wait for catapult to cock
			catCount++;
			if (catCount >= CAT_DELAY) {
				shootState = 4;
				catCount = 0;
			}
		case 4: // wait for button to go false
			if (!input.getRawButton(Wiring.BTN_SHOOT))
				shootState = 0;
			break;
		}
	}

	public void shooter(boolean fire) {
		switch (shootState) {
		case 0:
			if (fire) { // add && ball sensor
				solShooter.set(true);
				shootState = 1;
			}
			break;
		case 1:
			if (!diShooterTop.get()) {
				solShooter.set(false);
				shootState = 2;
			}
			break;
		case 2:
			if (!diShooterBot.get()) {
				shootState = 3;
			}
			break;
		case 3:
			if (!fire) {
				shootState = 0;
			}
			break;
		}
	}

	private int gatherState = 0;

	/**
	 * Initializes the two motor controls necessary for gatherer usage.
	 * 
	 * @param liftId The id for the {@link Talon}
	 * @param rotateId The id for the {@link Spark}
	 */
	public void initGatherers(int liftId, int rotateId) {
		gatherLift = new Talon(liftId);
		gatherRotate = new Talon(rotateId);
	}

	/**
	 * Reads input from the joystick in order to control the lifting part of the gatherer, which operates on a three-tier elevator-like system. Three different buttons are read from the joystick in order to move the {@link Talon} to one of
	 * the three tiers. Update periodically.
	 * 
	 * @param input The joystick that will be used to control the gatherer.
	 */
	public void gathererTalon(Joystick input) {
		if (gatherLift == null) {
			System.err.println("The gatherer motor controllers have not been initialized. Call this class's method \"initGatherers()\" in order to do so.");
			return;
		}
		switch (gatherState) {
		case 0: // checking for input
			if (input.getRawButton(Wiring.BTN_GATHERER_TO_TOP) && diGathererTop.get()) {
				gatherLift.set(0.6);
				System.out.println("Gatherer up");
				gatherCount = 0;
				gatherState = 1;
			} else if (input.getRawButton(Wiring.BTN_GATHERER_TO_BOT) && diGathererBot.get()) {
				gatherLift.set(-0.6);
				System.out.println("Gatherer down");
				gatherCount = 0;
				gatherState = 3;
			} else if (input.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				if (!diGathererTop.get()) {
					gatherLift.set(-0.6);
					System.out.println("Gatherer down");
					gatherCount = 0;
					gatherState = 5;
				} else if (!diGathererBot.get()) {
					gatherLift.set(0.6);
					System.out.println("Gatherer up");
					gatherCount = 0;
					gatherState = 6;
				}
			}
			break;
		case 1: // should be going up, wait until it hits the top
			if (!diGathererTop.get()) {
				gatherLift.set(0.0);
				System.out.println("Top limit");
				gatherState = 2;
			}
			break;
		case 2: // at top, waiting for driver to stop holding the button
			if (!input.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				gatherState = 0;
			}
			break;
		case 3: // should be going down, wait until it hist the bottom
			if (!diGathererBot.get()) {
				gatherLift.set(0.0);
				System.out.println("Bottom limit");
				gatherState = 4;
			}
			break;
		case 4: // at bottom, waiting for driver to stop holding the button
			if (!input.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				gatherState = 0;
			}
			break;
		case 5: // go down to mid
			gatherLift.set(-0.6);
			if (counter.get() >= 1) {
				gatherLift.set(0.0); // stahp
				counter.reset();
				gatherState = 0;
			}
			break;
		case 6: // go up to mid
			gatherLift.set(0.6);
			if (counter.get() >= 1) {
				gatherLift.set(0.0); // stahp
				counter.reset();
				gatherState = 0;
			}
			break;
		}
	}

	int gatherCount = 0;

	public void gathererSpark(Joystick input, PowerDistributionPanel pdp) {
		switch (gatherState) {
		case 0:
			if (input.getRawButton(7)) {
				gatherLift.set(0.6);
				System.out.println("Gatherer up");
				gatherState = 1;
			} else if (input.getRawButton(8)) {
				gatherLift.set(-0.6);
				System.out.println("Gatherer down");
				gatherState = 4;
			} else if (input.getRawButton(5)) {

			}
			break;
		case 1:
			if (pdp.getCurrent(13) > 1.25) {
				System.out.println("Motor Running");
				gatherState = 2;
			} else if (gatherCount > 3) {
				gatherState = 3;
				System.out.println("Gather Timeout");
			}
			gatherCount++;
			break;
		case 2:
			if (pdp.getCurrent(13) <= 1.25) {
				gatherLift.set(0.0);
				System.out.println("Top limit");
				gatherState = 3;
			}
			break;
		case 3:
			if (!input.getRawButton(7)) {
				gatherCount = 0;
				gatherState = 0;
			}
			break;
		case 4:
			if (pdp.getCurrent(13) > 1.25) {
				System.out.println("Motor Running");
				gatherState = 5;
			} else if (gatherCount > 3) {
				gatherState = 6;
				System.out.println("Gather Timeout");
			}
			gatherCount++;
			break;
		case 5:
			if (pdp.getCurrent(13) <= 1.25) {
				gatherLift.set(0.0);
				System.out.println("Bottom limit");
				gatherState = 6;
			}
			break;
		case 6:
			if (!input.getRawButton(8)) {
				gatherCount = 0;
				gatherState = 0;
			}
			break;
		}
	}
	//
	// int spinnerCount = 0;
	//
	// public void gathererGatheringSpinnyThingy() {
	// switch (spinnerCount) {
	// case 0:
	// gatherRotate.set(0.0);
	// if (stick.getRawButton(9))
	// spinnerCount = 1;
	// if (stick.getRawButton(10))
	// spinnerCount = 2;
	// break;
	// case 1:
	// gatherRotate.set(0.4);
	// if (!stick.getRawButton(9))
	// spinnerCount = 0;
	// break;
	// case 2:
	// gatherRotate.set(0.4);
	// if (!stick.getRawButton(10))
	// spinnerCount = 0;
	// break;
	// case 3:
	//
	// break;
	// }
	// }
}