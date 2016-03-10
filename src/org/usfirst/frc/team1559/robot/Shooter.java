package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Includes methods for shooting and gathering.
 */
public class Shooter {

	public static boolean bored = true;
	private boolean shootDone = false;
	private boolean shooting = false;

	Solenoid fireShooter, downShooter;

	public Shooter() {
		fireShooter = new Solenoid(Wiring.SHOOTER_UP_SOLENOID);
		downShooter = new Solenoid(Wiring.SHOOTER_DOWN_SOLENOID);
	}

	public int shootState = 0;
	private int shooterCount = 0;

	/**
	 * Enable/disable the shooter based on joystick input.
	 * 
	 * @param input The joystick used to control the shooter.
	 */

	public void setSolenoids(boolean s, boolean override) {
		if (!override) {
			fireShooter.set(s);
			downShooter.set(!s);
		}
	}

	public void autoShoot(boolean b, boolean override) {

		bored = false;
		SmartDashboard.putBoolean("SOLENOID", fireShooter.get());
		SmartDashboard.putNumber("Shoot State", shootState);

		switch (shootState) {
		case 0: // waiting for fire button
			if (b) {
				shooting = true;
			}
			if (shooting) {
				shooterCount++;
			}
			if (shooterCount >= Wiring.SHOOTER_FIRE_DELAY) {// delaying so we don't destroy ball clamps
				setSolenoids(true, override);
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
			setSolenoids(false, override);
			shootState = 3;
			break;
		case 3: // wait for catapult to cock
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_DOWN_DELAY) {
				shootState = 4;
				shooterCount = 0;
			}
			shooting = false;
			break;
		case 4: // wait for button to go false
			shootState = 5;
			shootDone = true;
			break;
		default:
			setSolenoids(false, override);
			shootDone = true;
			bored = true;
			break;
		}
	}

	public void updateShooter(Joystick input, boolean override) {

		bored = false;
		SmartDashboard.putBoolean("SOLENOID", fireShooter.get());
		SmartDashboard.putNumber("Shoot State", shootState);

		switch (shootState) {
		case 0: // waiting for fire button
			if (input.getRawAxis(Wiring.BTN_SHOOT) >= .9) {
				shooting = true;
			}
			if (shooting) {
				shooterCount++;
			}
			if (shooterCount >= Wiring.SHOOTER_FIRE_DELAY) {
				setSolenoids(true, override);
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
			setSolenoids(false, override);
			shootState = 3;
			break;
		case 3: // wait for catapult to cock
			shooterCount++;
			if (shooterCount >= Wiring.SHOOTER_DOWN_DELAY) {
				shootState = 4;
				shooterCount = 0;
			}
			shooting = false;
			break;
		case 4: // wait for button to go false
			if (!(input.getRawAxis(Wiring.BTN_SHOOT) >= .9)) {
				shootState = 0;
				bored = true;
			}
			break;
		}
	}

	public void resetShooter(boolean override) {
		setSolenoids(false, override);
	}

	public boolean isShooting() {
		return shooting;
	}
	
	public boolean isShootDone() {
		return shootDone;
	}
}