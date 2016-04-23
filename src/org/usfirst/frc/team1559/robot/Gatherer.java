package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

public class Gatherer {
	// commenth8yhj

	enum ArmState {
		STALLED, ATTOP, TOPMIDDOWN, TOPBOTDOWN, ATMID, MIDBOTDOWN, ATBOT, BOTMIDUP, BOTTOPUP, MIDTOPUP,
	}; // from, to and direction ex. from top to mid going down

	private static final double TOP_TARGET = 0; // home position
	private static final double MID_TARGET = 88; // gathering position
	private static final double BOTTOM_TARGET = 110; // low bar position
	
	DebounceButton dbUP, dbDOWN;

	private Talon gatherLift;
	private Spark gatherRotate;
	private PIDController pidController;
	private DigitalInput diGathererTop;
	private PowerDistributionPanel pdp = new PowerDistributionPanel();
	// private int gatherState = 0;
	// private final double LOWBAR_POSITION = 105;
	// private final double talonIdleCurrent = 1.875;
	private final double talonStallCurrent = 12.0;
	// private final double sparkIdleCurrent = 1.625;
	private final double liftUp = 0.6;
	private final double liftDown = -0.6;
	private final double liftStop = 0.0;
	private final int stallCount = 10;
	private int counter = 0;
	private AnalogGyro gyro;
	private Joystick stick;
	int gatherPosition = 0;

	/**
	 * Initializes the two motor controls necessary for gatherer usage.
	 * 
	 * @param liftId The id for the {@link Talon}
	 * @param rotateId The id for the {@link Spark}
	 */

	public Gatherer(int liftId, int rotateId, Joystick joy) {
		gatherLift = new Talon(liftId);
		gatherRotate = new Spark(rotateId);
		gyro = new AnalogGyro(Wiring.GATHERER_ANALOG_INPUT);
		gyro.reset();
		stick = joy;
		diGathererTop = new DigitalInput(Wiring.GATHERER_LIMIT_ID);
		dbUP = new DebounceButton(stick, Wiring.BTN_GATHER_UP_LEVEL);
		dbDOWN = new DebounceButton(stick, Wiring.BTN_GATHER_DOWN_LEVEL);
	}

	/**
	 * Reads input from the joystick in order to control the lifting part of the gatherer, which operates on a three-tier elevator-like system. Three different buttons are read from the joystick in order to move the {@link Talon} to one of
	 * the three tiers. Update periodically.
	 */

	ArmState arm = ArmState.ATTOP;
	double target = TOP_TARGET;

	public void setSpark(double d) {
		gatherRotate.set(d);
	}

	public void lowbarify() {
		if (gyro.getAngle() >= BOTTOM_TARGET) {
			gatherLift.set(liftStop);
			arm = ArmState.ATBOT;
		} else {
			gatherLift.set(liftDown);
		}
	}

	public void homify() {
		if (gyro.getAngle() <= TOP_TARGET) {
			gatherLift.set(liftStop);
			arm = ArmState.ATTOP;
		} else {
			gatherLift.set(liftUp);
		}
	}
	//@TODO: Make this work.
	public void gatherPos(){

	}

	public void gathererTalon() {
		double angle = gyro.getAngle();
		if (pdp.getCurrent(Wiring.PDP_CHANNEL_GATHERER) > talonStallCurrent) {
			counter++;
			if (counter > stallCount) {
				gatherLift.set(liftStop);
				if (angle > TOP_TARGET || angle < BOTTOM_TARGET) {
					arm = ArmState.STALLED;
				}
			}
			return;
		} else {
			counter = 0;
		}

		switch (arm) {
		case ATTOP:
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
			// target = BOTTOM_TARGET;
			// gatherLift.set(liftDown);
			// arm = ArmState.TOPBOTDOWN;
			// }

			if (dbDOWN.get()) {
				target = MID_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.TOPMIDDOWN;
				System.out.println("MIDDLE OR BUST");
			}
			break;
		case TOPMIDDOWN:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = ArmState.ATMID;
			}
			// change of plans
			if (dbUP.get()) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = ArmState.MIDTOPUP;
			}
			if (dbDOWN.get()) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.TOPBOTDOWN;
			}
			break;
		case TOPBOTDOWN:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = ArmState.ATBOT;
			}
			// change of plans
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
			// target = TOP_TARGET;
			// gatherLift.set(liftUp);
			// arm = ArmState.BOTTOPUP;
			// }
			if (dbUP.get()) {
				target = BOTTOM_TARGET;
				gatherLift.set((angle <= MID_TARGET) ? liftDown : liftUp);
				arm = ArmState.BOTMIDUP;
			}
			break;
		case ATMID:
			if (dbUP.get()) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = ArmState.MIDTOPUP;
			}
			if (dbDOWN.get()) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.MIDBOTDOWN;
			}
			break;
		case MIDBOTDOWN:
			if (angle >= target) {
				arm = ArmState.ATBOT;
				gatherLift.set(liftStop);
				System.out.println("GOING DOWWWWWWN");
			}
			// change of plans
			if (dbUP.get()) {
				target = MID_TARGET;
				gatherLift.set(liftUp);
				arm = ArmState.BOTMIDUP;
			}
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
			// target = TOP_TARGET;
			// gatherLift.set(liftUp);
			// arm = ArmState.MIDTOPUP;
			// }
			break;
		case ATBOT:
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
			// target = TOP_TARGET;
			// gatherLift.set(liftUp);
			// arm = ArmState.BOTMIDUP;
			// }
			if (dbUP.get()) {
				target = MID_TARGET;
				gatherLift.set(liftUp);
				arm = ArmState.BOTMIDUP;
			}
			break;
		case BOTMIDUP:
			if (angle <= target) {
				gatherLift.set(liftStop);
				arm = ArmState.ATMID;
			}
			// change of plans
			if (dbUP.get()) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = ArmState.BOTMIDUP;
			}
			if (dbDOWN.get()) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.MIDBOTDOWN;
			}

			if (!diGathererTop.get()) {
				gatherLift.set(liftStop);
				arm = ArmState.ATTOP;
				gyro.reset();
			}
			break;
		case BOTTOPUP:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = ArmState.ATTOP;
			}
			// change of plans
			if (dbDOWN.get()) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.MIDBOTDOWN;
			}
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
			// target = MID_TARGET;
			// gatherLift.set((angle >= MID_TARGET) ? liftDown : liftUp);
			// arm = ArmState.BOTMIDUP;
			// }
			//
			if (!diGathererTop.get()) {
				gatherLift.set(liftStop);
				arm = ArmState.ATTOP;
				gyro.reset();
			}
			break;
		case MIDTOPUP:
			if (angle <= TOP_TARGET) {
				arm = ArmState.ATTOP;
				gatherLift.set(liftStop);
			}
			// change of plans
			if (dbDOWN.get()) {
				target = MID_TARGET;
				gatherLift.set(liftDown);
				arm = ArmState.TOPMIDDOWN;
			}
			// if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
			// target = BOTTOM_TARGET;
			// gatherLift.set(liftDown);
			// arm = ArmState.MIDBOTDOWN;
			// }

			if (!diGathererTop.get()) {
				gatherLift.set(liftStop);
				arm = ArmState.ATTOP;
				gyro.reset();
			}
			break;
		case STALLED:
			break;
		default:
			break;
		}
	}

	public AnalogGyro getGyro() {
		return gyro;
	}

	public boolean isLimitSwitchTripped() {
		return diGathererTop.get();
	}

	public void manualControl() {
		if (stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)) {
			gatherLift.set(liftUp);
		} else if (stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)) {
			gatherLift.set(liftDown);
		} else if (stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL) && stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)) {
			gatherPos();
		}else {
			gatherLift.set(liftStop);
		}
	}
	
	public void copilotManualControlDOWN(){
		gatherLift.set(liftDown);
	}
	
	public void copilotManualControlUP(){
		if(diGathererTop.get()){
			gatherLift.set(liftUp);
		} else {
			gatherLift.set(liftStop);
		}
		
	}
	
	public void stopDriving(){
		gatherLift.set(liftStop);
	}

	public boolean shouldNotShoot() {
		return gyro.getAngle() <= Wiring.GATHERER_SAFE_SHOOT_ANGLE;
	}

	public void updatePosition() {

		if ((!diGathererTop.get() && pidController.getSetpoint() != TOP_TARGET) || gyro.getAngle() >= BOTTOM_TARGET) {// stop the errors cody!!
			disableLifterPID();
			gatherLift.set(0.0);
		} else {
			enableLifterPID();
			switch (gatherPosition) {
			case 0:
				if (stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)) {
					pidController.setSetpoint(MID_TARGET);
					gatherPosition++;

				}
				break;
			case 1:
				if (stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)) {
					pidController.setSetpoint(TOP_TARGET);
					gatherPosition--;

				} else if (stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)) {
					pidController.setSetpoint(BOTTOM_TARGET);
					gatherPosition++;

				}
				break;
			case 2:
				if (stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)) {
					pidController.setSetpoint(MID_TARGET);
					gatherPosition--;

				}
				break;
			}
		}

	}

	public void updateAutoPosition() {
		pidController.setSetpoint(MID_TARGET);
		enableLifterPID();
	}

	/*
	 * PID loop for the gatherer
	 */
	public void initLifterPID(double p, double i, double d) {
		// Shaquisha
		pidController = new PIDController(p, i, d, gyro, gatherLift);
		pidController.setSetpoint(TOP_TARGET);
		pidController.setAbsoluteTolerance(Wiring.GATHERER_PID_TOLERANCE);
	}

	public void enableLifterPID() {
		pidController.enable();
	}

	public void disableLifterPID() {
		pidController.disable();
	}
}
