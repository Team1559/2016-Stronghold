package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Servo;

public class BallClamp {
	Servo servoLeft;
	Servo servoRight;
	PWM pwm0;
	PWM pwm1;

	public BallClamp() {
		servoLeft = new Servo(Wiring.CLAMP_LEFT_ID);
		servoRight = new Servo(Wiring.CLAMP_RIGHT_ID);
		pwm0 = new PWM(Wiring.CLAMP_SENSOR_ID_0);
		pwm1 = new PWM(Wiring.CLAMP_SENSOR_ID_1);
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
		if (pwm0.getRaw() >= 190 || pwm1.getRaw() >= 0) {
			if (input.getRawButton(Wiring.BTN_SHOOT)) {
				open();
			} else {
				close();
			}
		} else {
			open();
		}
	}
}