package org.usfirst.frc.team1559.robot;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import edu.wpi.first.wpilibj.I2C;



public class I2CServer {
	
	I2C I2CMaster;
	
	public I2CServer() {
		
		I2CMaster = new I2C(I2C.Port.kOnboard,0);
	}
	
	
	public String read() {
		
		boolean isData = true;
		
		byte[] biteMe = new byte[10];	
		I2CMaster.readOnly(biteMe, biteMe.length);
		
		String data = new String("");
		
		try {
			data = new String(biteMe, "ISO-8859-1"); //use UTF-8 encoding??
		} catch (UnsupportedEncodingException e) {
			isData = false;
			System.out.println("Error reading data");
		}
		
		if (isData == true) {
			return data;
		}
		else{
			return "error";
		}
		
		//String data = new String(Base64.encodeBase64(biteMe));
	}
}
