package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.I2C;



public class I2CServer {
	
	I2C I2CMaster;
	
	public I2CServer() {
		
		I2CMaster = new I2C(I2C.Port.kOnboard,0);		
	}
	
	public String read() {
		
		byte[] biteMe = new byte[10];
		
		I2CMaster.readOnly(biteMe, biteMe.length);
		
		//parse crap later
		
		return "rekt";
		
	}
}
