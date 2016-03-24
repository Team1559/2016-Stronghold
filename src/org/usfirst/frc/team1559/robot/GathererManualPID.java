package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;

public class GathererManualPID {
	// commenth8yhj blame:night

	enum ArmState {
		STALLED, ATTOP, TOPMIDDOWN, TOPBOTDOWN, ATMID, MIDBOTDOWN, ATBOT, BOTMIDUP, BOTTOPUP, MIDTOPUP,
	}; // from, to and direction ex. from top to mid going down

	private static final boolean ENABLE_IN_CONSTRUCTOR = true;

	DebounceButton btnUp, btnDown;

	private boolean enabled;
	private double kP = 0.9;
	private double kI = 0.0;
	private double kD = 0.0;

	private double totalError = 0;
	private double error = 0;
	private double prevError = 0;

	private double maxOutput = 1;
	private double minOutput = -1;

	private double result = 0.0;
	private Talon gatherLift;
	private Spark gatherRotate;
	private DigitalInput diGathererTop;
	private PowerDistributionPanel pdp = new PowerDistributionPanel();
	private final double TOP_TARGET = 0;
	private final double MID_TARGET = Wiring.GATHER_MID_TARGET; // Wiring.GATHERER_SAFE_SHOOT_ANGLE
	private final double GATHER_TARGET = 88;
	private final double BOTTOM_TARGET = 110;
	private final double talonStallCurrent = 12.0;
	private final double liftUp = 0.6;
	private final double liftDown = -0.6;
	private final double liftStop = 0.0;
	private final int stallCount = 10;
	private AnalogInput pot;
	private Joystick stick;
	int gatherPosition = 0;
	final double topLimit = 1;
	final double midLimit = 0;
	final double bottomLimit = -.7;
	double sumI = 0.0;

	public GathererManualPID(int liftId, int rotateId, Joystick stick) {
		gatherLift = new Talon(liftId);
		gatherRotate = new Spark(rotateId);
		pot = new AnalogInput(Wiring.GATHERER_ANALOG_INPUT);
		this.stick = stick;
		diGathererTop = new DigitalInput(Wiring.GATHERER_LIMIT_ID);
		btnUp = new DebounceButton(stick, Wiring.BTN_GATHER_UP_LEVEL);
		btnDown = new DebounceButton(stick, Wiring.BTN_GATHER_DOWN_LEVEL);
		if (ENABLE_IN_CONSTRUCTOR) {
			enabled = true;
		}
	}

	ArmState arm = ArmState.ATTOP;
	double target = TOP_TARGET;

	private double setpoint = Wiring.GATHER_HOME_TARGET;

	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}

	public double getSetpoint() {
		return setpoint;
	}

	// public double getAccelY() {
	// return accel.getY();
	// }
	//
	// public double getError() {
	// return getSetpoint() - getAccelY();
	// }

	public void updatePID() {
		error = setpoint - pot.getAverageValue();
		
		if (enabled) {
			
			sumI += error * kI;
			result = (kP * error) + sumI;

			if (result > maxOutput) {
				result = maxOutput;
			} else if (result < minOutput) {
				result = minOutput;
			}
			
			if (isLimit()) {
				result = 0;
			}
			gatherLift.set(result);
		}
	}

	public double getPIDOutput() {
		return result;
	}

	public void enable() {
		enabled = true;
	}

	public void manualControl() {
		if (stick.getRawButton(Wiring.BTN_GATHER_UP_LEVEL)) {
			gatherLift.set(liftUp);
		} else if (stick.getRawButton(Wiring.BTN_GATHER_DOWN_LEVEL)) {
			gatherLift.set(liftDown);
		} else {
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
	
	public void setSpark(double d) {
		gatherRotate.set(d);
	}
	
	public void disable() {
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public double getPot(){
		
		double dankKush = pot.getAverageValue();
		return dankKush;
		
	}
	
	public boolean isLimit() {
		return diGathererTop.get() && setpoint == Wiring.GATHER_HOME_TARGET;
	}
}