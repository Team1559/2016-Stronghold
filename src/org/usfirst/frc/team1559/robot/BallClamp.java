package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Servo;

public class BallClamp {

	private Servo servoLeft, servoRight;
	private AnalogInput opSensor;
	private boolean open = false;
	
	
	public BallClamp() {
		servoLeft = new Servo(Wiring.CLAMP_LEFT_ID);
		servoRight = new Servo(Wiring.CLAMP_RIGHT_ID);
		opSensor = new AnalogInput(Wiring.CLAMP_BALL_SENSOR_ID);
		servoLeft.set(1);
		servoRight.set(0);
	}

	public void close() {
		servoLeft.set(0);
		servoRight.set(1);
		open = false;
	}

	public void open() {
		servoLeft.set(1);
		servoRight.set(0);
		open = true;
	}

	public void updateBallClamp(boolean override) {
		if (((opSensor.getAverageVoltage() >= Wiring.CLAMP_LOW) && (opSensor.getAverageVoltage() <= Wiring.CLAMP_HIGH)) && !override) {
			close();
		} else {
			open();
		}
	}

	public double readSensor(){
		return opSensor.getAverageVoltage();
	}
	
	public boolean isOpen() {
		return open;
	}
}