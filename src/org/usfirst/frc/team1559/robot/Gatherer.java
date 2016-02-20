package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

public class Gatherer {

	private Talon gatherLift;
	private Talon gatherRotate;
	private DigitalInput diGathererTop;
	private DigitalInput diGathererBottom;
	private PowerDistributionPanel pdp = new PowerDistributionPanel();
	private int gatherState = 0;
	private final double MID_TARGET = 90;
	private final double TOP_TARGET = 0;
	private final double BOTTOM_TARGET = 120;
	private final double talonIdleCurrent = 6.25; // maybe??
	private final double talonStallCurrent = 2000.0;
	private final double sparkIdleCurrent = 1.25;
	private final double liftUp = 0.6;
	private final double liftDown = -0.6;
	private final double liftStop = 0.0;
	private final int stallCount = 10;
	private int counter = 0;
	private AnalogGyro gyro;
	private Joystick stick;

	enum armStates {
		STALLED, ATTOP, TOPMIDDOWN, TOPBOTDOWN, ATMID, MIDBOTDOWN, ATBOT, BOTMIDUP, BOTTOPUP, MIDTOPUP,
	}; // from, to and direction ex. from top to mid going down

	/**
	 * Initializes the two motor controls necessary for gatherer usage.
	 * 
	 * @param liftId
	 *            The id for the {@link Talon}
	 * @param rotateId
	 *            The id for the {@link Spark}
	 */

	public void initGatherers(int liftId, int rotateId, Joystick joy) {
		gatherLift = new Talon(liftId);
		gatherRotate = new Talon(rotateId);
		gyro = new AnalogGyro(Wiring.GATHERER_ANALOG_INPUT);
		gyro.reset();
		stick = joy;
	}

	/**
	 * Reads input from the joystick in order to control the lifting part of the
	 * gatherer, which operates on a three-tier elevator-like system. Three
	 * different buttons are read from the joystick in order to move the
	 * {@link Talon} to one of the three tiers. Update periodically.
	 * 
	 * @param input
	 *            The joystick that will be used to control the gatherer.
	 */

	armStates arm = armStates.ATTOP;
	double target = TOP_TARGET;

	public void gathererTalon(Joystick input) {
		double angle = gyro.getAngle();
		if (pdp.getCurrent(Wiring.PDP_CHANNEL_GATHERER) > talonStallCurrent) {
			counter++;
			if (counter > stallCount) {
				gatherLift.set(liftStop);
				if (angle > TOP_TARGET || angle < BOTTOM_TARGET) {
					arm = armStates.STALLED;
				}
			}
			return;
		} else {
			counter = 0;
		}

		if (!diGathererTop.get()) {
			gatherLift.set(liftStop);
			arm = armStates.ATTOP;
			gyro.reset();
		}
		switch (arm) {
		case ATTOP:
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.TOPBOTDOWN;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = MID_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
			}
			break;
		case TOPMIDDOWN:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = armStates.ATMID;
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.TOPBOTDOWN;
			}
			break;
		case TOPBOTDOWN:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = armStates.ATBOT;
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.BOTTOPUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = BOTTOM_TARGET;
				gatherLift.set((angle <= MID_TARGET) ? liftDown : liftUp);
				arm = armStates.BOTMIDUP;
			}
			break;
		case ATMID:
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
			}
			break;
		case MIDBOTDOWN:
			if (angle >= target) {
				arm = armStates.ATBOT;
				gatherLift.set(liftStop);
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = MID_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.MIDTOPUP;
			}
			break;
		case ATBOT:
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = MID_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
			}
			break;
		case BOTMIDUP:
			if (angle <= target) {
				gatherLift.set(liftStop);
				arm = armStates.ATMID;
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
				target = TOP_TARGET;
				gatherLift.set(liftUp);
				arm = armStates.BOTMIDUP;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
			}
			break;
		case BOTTOPUP:
			if (angle >= target) {
				gatherLift.set(liftStop);
				arm = armStates.ATTOP;
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = MID_TARGET;
				gatherLift.set((angle >= MID_TARGET) ? liftDown : liftUp);
				arm = armStates.BOTMIDUP;
			}
			break;
		case MIDTOPUP:
			if (angle <= TOP_TARGET) {
				arm = armStates.ATTOP;
				gatherLift.set(liftStop);
			}
			// change of plans
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
				target = MID_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.TOPMIDDOWN;
			}
			if (stick.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
				target = BOTTOM_TARGET;
				gatherLift.set(liftDown);
				arm = armStates.MIDBOTDOWN;
			}
			break;
		}
	}
}
