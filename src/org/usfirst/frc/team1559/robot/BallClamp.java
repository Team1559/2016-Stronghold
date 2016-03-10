package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Servo;

public class BallClamp {

	private Servo servoLeft, servoRight;
	private AnalogInput opSensor;
	private boolean open = false;
	private boolean ballIn = false;
	private int clampy = 0;
	private int counter = 0;
	
	
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

	public void resetClampy(){
		clampy = 0;
	}
	
	public void updateBallClampAbsolute(boolean override){
		switch(clampy){
		case 0:
			//don't have ball :(
			if(!override){
				if (((opSensor.getAverageVoltage() >= Wiring.CLAMP_LOW) && (opSensor.getAverageVoltage() <= Wiring.CLAMP_HIGH))) {
					ballIn = true;
					close();
					clampy++;
				}
				
			} else {
				open();
			}
			break;
		case 1:
			//holding a ball
			if(!override){
				close();
			} else {
				open();
				clampy = 2;
			}
			break;
		case 2:
			//just wait
			if(Shooter.bored){
				clampy = 0;
			}
			break;
		}

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

	public boolean gitBallIn() {
		return ballIn;
	}

	public void setBallIn(boolean ballIn) {
		this.ballIn = ballIn;
	}
}