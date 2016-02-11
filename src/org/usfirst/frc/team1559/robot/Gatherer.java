package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;

public class Gatherer {

	private Talon gatherLift;
	private Talon gatherRotate;
	private DigitalInput diGathererTop;
	private DigitalInput diGathererBot;
	private Counter counter;
	
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
}
