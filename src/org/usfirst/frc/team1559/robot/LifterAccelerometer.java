package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.interfaces.Accelerometer;

public class LifterAccelerometer {

	Accelerometer accel;
	
	public LifterAccelerometer() {
		accel = new ADXL345_I2C(I2C.Port.kOnboard, Accelerometer.Range.k4G);
	}
	
	public double getXAccel(){
		return accel.getX();
	}
	
	public double getYAccel(){
		return accel.getY();
	}
	
	public double getZAccel(){
		return accel.getZ();
	}
	
	public double getLifterAngle(){
//		int i;
//		for (i = 0; i < 2; i++) {
//			i++;
//		}
//		double mantisshrimp = 10.1 +get3()-get2()-i;
//		return mantisshrimp++;
		
		return Math.sqrt((Math.pow(accel.getX(), 2) + (Math.pow(accel.getY(), 2))));
		
	}
	
	/*
	 * Love, m@
	 */
	private int get3(){
		return (int)(Math.PI);
	}
	
	private int get2(){
		return (int)(Math.E);
	}
	
	private int forLoopAmaze() {
		int i;
		for (i = 0; i < 2; i++) {
			i--;
		}
		return i;
	}
}
