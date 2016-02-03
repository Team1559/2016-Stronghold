package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

public class AidanShooter {

	final int fireButton = 6;
	final int YellowButton = 4;
	final int BlueButton = 3;
	final int RedButton = 2;
	final int GreenButton = 1;
	final int catDelay = 5;
	
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
		counter = new Counter();
		shift1 = new Solenoid(0);
		shift2 = new Solenoid(1);
		leftS.changeControlMode(CANTalon.TalonControlMode.Follower);// sets
		// motor to follower
		leftS.set(15);// sets to ID of master(leftM)
		rightS.changeControlMode(CANTalon.TalonControlMode.Follower);
		rightS.set(16);
		fired = new Solenoid(2);
		led = new Relay(0);
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
		gatherCount = 0;
		counter.reset();
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
		//	catapult(stick.getRawButton(fireButton));
		catapult1(stick.getRawButton(fireButton));
		gathererSpark();
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
	}

	public void testPeriodic() {
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

	private int gatherState = 0;

	public void gathererTalon() {
		switch (gatherState) {
		case 0:
			if (stick.getRawButton(7) && gatherTop.get()) {
				gatherLift.set(0.6);
				System.out.println("Gatherer up");
				gatherCount = 0;
				gatherState = 1;
			} else if (stick.getRawButton(8) && gatherBot.get()) {
				gatherLift.set(-0.6);
				System.out.println("Gatherer down");
				gatherCount = 0;
				gatherState = 3;
			} else if (stick.getRawButton(5)) {
				if (!gatherTop.get()) {
					gatherLift.set(-0.6);
					System.out.println("Gatherer down");
					gatherCount = 0;
					gatherState = 5;
				} else if (!gatherBot.get()) {
					gatherLift.set(0.6);
					System.out.println("Gatherer up");
					gatherCount = 0;
					gatherState = 6;
				}
			}
			break;
		case 1:
			if (!gatherTop.get()) {
				gatherLift.set(0.0);
				System.out.println("Top limit");
				gatherState = 2;
			}
			break;
		case 2:
			if (!stick.getRawButton(7)) {
				gatherState = 0;
			}
			break;
		case 3:
			if (!gatherBot.get()) {
				gatherLift.set(0.0);
				System.out.println("Down limit");
				gatherState = 4;
			}
			break;
		case 4:
			if (!stick.getRawButton(8)) {
				gatherState = 0;
			}
			break;
		case 5:
			gatherLift.set(-0.6);
			if (counter.get() >= 1) {
				gatherLift.set(0.0);
				counter.reset();
				gatherState = 0;
			}
			break;
		case 6:
			gatherLift.set(0.6);
			if (counter.get() >= 1) {
				gatherLift.set(0.0);
				counter.reset();
				gatherState = 0;
			}
			break;
		}
	}

	int gatherCount = 0;

	public void gathererSpark() {
		switch (gatherState) {
		case 0:
			if (stick.getRawButton(7)) {
				gatherLift.set(0.6);
				System.out.println("Gatherer up");
				gatherState = 1;
			} else if (stick.getRawButton(8)) {
				gatherLift.set(-0.6);
				System.out.println("Gatherer down");
				gatherState = 4;
			} else if (stick.getRawButton(5)) {

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
			if (!stick.getRawButton(7)) {
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
				System.out.println("Down limit");
				gatherState = 6;
			}
			break;
		case 6:
			if (!stick.getRawButton(8)) {
				gatherCount = 0;
				gatherState = 0;
			}
			break;
		}
	}

	int spinnerCount = 0;

	public void gathererGatheringSpinnyThingy() {
		switch (spinnerCount) {
		case 0:
			gatherRotate.set(0.0);
			if (stick.getRawButton(9))
				spinnerCount = 1;
			if (stick.getRawButton(10))
				spinnerCount = 2;
			break;
		case 1:
			gatherRotate.set(0.4);
			if (!stick.getRawButton(9))
				spinnerCount = 0;
			break;
		case 2:
			gatherRotate.set(0.4);
			if (!stick.getRawButton(10))
				spinnerCount = 0;
			break;
		case 3:

			break;
		}
	}
	
}
