package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Includes methods for shooting and gathering.
 */
public class Shooter {

	boolean shootDone = false;

	// final int fireButton = 6;
	// final int YellowButton = 4;
	// final int BlueButton = 3;
	// final int RedButton = 2;
	// final int GreenButton = 1;
	//
	// private RobotDrive robot;

	// private CANTalon leftM;
	// private CANTalon leftS;
	// private CANTalon rightM;
	// private CANTalon rightS;
	// private Joystick stick;
	// private Solenoid shift1;
	// private Solenoid shift2;
	// private Solenoid cocked;
	// private Relay led;

	Solenoid fireShooter, downShooter;

	public Shooter() {
		fireShooter = new Solenoid(Wiring.SHOOTER_UP_SOLENOID);
		downShooter = new Solenoid(Wiring.SHOOTER_DOWN_SOLENOID);
	}

	//
	// public void robotInit() {
	// // ahrs = new AHRS(SPI.Port.kMXP);
	// leftM = new CANTalon(15);
	// rightM = new CANTalon(16);
	// leftS = new CANTalon(17);
	// rightS = new CANTalon(18);
	// gatherLift = new Talon(0);
	// gatherRotate = new Talon(1);
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
	// led = new Relay(0);
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
	public int shootState = 0;
	private int shooterCount = 0;

	/**
	 * Enable/disable the shooter based on joystick input.
	 * 
	 * @param input
	 *            The joystick used to control the shooter.
	 */

	public void setSolenoids(boolean s) {
		fireShooter.set(s);
		downShooter.set(!s);
	}

	public void updateShooter(boolean b) {

		SmartDashboard.putBoolean("SOLENOID", fireShooter.get());
		SmartDashboard.putNumber("Shoot State", shootState);

		switch (shootState) {
		case 0: // waiting for fire button
			if (b) {
				setSolenoids(true);
				shootState = 1;
				shooterCount = 0;
			}
			break;
		case 1: // waiting for catapult to move
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_UP_DELAY) {
				shootState = 2;
				shooterCount = 0;
			}
			break;
		case 2: // recock the catapult
			setSolenoids(false);
			shootState = 3;
			break;
		case 3: // wait for catapult to cock
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_DOWN_DELAY) {
				shootState = 4;
				shooterCount = 0;
			}
			break;
		case 4: // wait for button to go false
			shootDone = true;
			shootState = 5;
			break;
		default:
			setSolenoids(false);
			shootDone = true;
			break;
		}
	}

	public void updateShooter(Joystick input) {

		SmartDashboard.putBoolean("SOLENOID", fireShooter.get());
		SmartDashboard.putNumber("Shoot State", shootState);

		switch (shootState) {
		case 0: // waiting for fire button
			if (input.getRawAxis(3) >= .9) {
				setSolenoids(true);
				shootState = 1;
				shooterCount = 0;
			}
			break;
		case 1: // waiting for catapult to move
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_UP_DELAY) {
				shootState = 2;
				shooterCount = 0;
			}
			break;
		case 2: // recock the catapult
			setSolenoids(false);
			shootState = 3;
			break;
		case 3: // wait for catapult to cock
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_DOWN_DELAY) {
				shootState = 4;
				shooterCount = 0;
			}
			break;
		case 4: // wait for button to go false
			if (!input.getRawButton(Wiring.BTN_SHOOT))
				shootState = 0;
			break;
		}
	}

	public void initShooter() {
		setSolenoids(false);
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