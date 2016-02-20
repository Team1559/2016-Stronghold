package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

public class Gatherer {

//	private enum Position {
//		TOP, MID, BOT, TOPMID, MIDBOT
//	}
	
//	private Position pos;
	private Talon gatherLift;
	private Talon gatherRotate;
//	private DigitalInput diGathererTop;
//	private DigitalInput diGathererMid;
//	private DigitalInput diGathererBot;
//	private AnalogInput analog = new AnalogInput(Wiring.GATHERER_ANALOG_INPUT);
//	private PowerDistributionPanel pdp = new PowerDistributionPanel();
	// private final double kP = .2;
	// private final double kI = .01;
	// private final double kD = 0.0;
//	private final double talonIdleCurrent = 6.25; // maybe??
//	private final double sparkIdleCurrent = 1.25;
	// private PIDController pid = new PIDController(kP, kI, kD, analog, gatherLift);

	/**
	 * Initializes the two motor controls necessary for gatherer usage.
	 * 
	 * @param liftId The id for the {@link Talon}
	 * @param rotateId The id for the {@link Spark}
	 */

	public void initGatherers(int liftId, int rotateId) {
		gatherLift = new Talon(liftId);
		gatherRotate = new Talon(rotateId);
		// pid.setSetpoint(MID_TARGET);
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
		
		// if (input.getRawButton(Wiring.BTN_GATHERER_TO_TOP)) {
		// pid.setSetpoint(TOP_TARGET);
		// pid.enable();
		// } else if (input.getRawButton(Wiring.BTN_GATHERER_TO_BOT)) {
		// pid.setSetpoint(BOTTOM_TARGET);
		// pid.enable();
		// } else if (input.getRawButton(Wiring.BTN_GATHERER_TO_MID)) {
		// pid.setSetpoint(MID_TARGET);
		// pid.enable();
		// }

//		if ((!diGathererTop.get()) || (!diGathererMid.get()) || (!diGathererBot.get())) {
//			// pid.disable();
//			gatherLift.set(0.0);
//		}

//		if (pdp.getCurrent(Wiring.PDP_CHANNEL_GATHERER) <= sparkIdleCurrent) {
//			// pid.disable();
//		}
	}
}