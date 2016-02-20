package org.usfirst.frc.team1559.robot;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;

public class BallClamp {
	Servo servoLeft;
	Servo servoRight;
	AnalogInput opSensor;
	public BallClamp() {
		servoLeft = new Servo(Wiring.CLAMP_LEFT_ID);
		servoRight = new Servo(Wiring.CLAMP_RIGHT_ID);
		opSensor = new AnalogInput(Wiring.CLAMP_BALL_SENSOR_ID);
	}

	public void close() {
		servoLeft.set(1);
		servoRight.set(1);
	}

	public void open() {
		servoLeft.set(0);
		servoRight.set(0);
	}

	public void updateBallClamp(Joystick input) {
		if(opSensor.getVoltage() >= Wiring.CLAMP_SENSOR_THRESHOLD){
			close();
		}else{
			open();
		}
	}
	
}