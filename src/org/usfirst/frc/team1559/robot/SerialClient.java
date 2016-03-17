package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class SerialClient {
	private final int BAUDRATE = 115200;
	private final String sendChar = "s";
	SerialPort sp;

	public SerialClient() {
		sp = new SerialPort(BAUDRATE, Port.kMXP);
	}

	public double grabAngle() {
		//double ret = -1000; 
		//int bcount = sp.getBytesReceived();
		//if (bcount > 0) {
			//String in = sp.readString();
			//if (in.endsWith("t")) {
				//in = in.substring(0, in.charAt('t'));
				//try {
					//ret = Double.parseDouble(in);
				//} catch (Exception e) {
				//}
			//}
		//}
		
		//return ret;
		
		
		//well everyone else was re-writing this so i figured why not
		
		double ang = -2000; //return a -2000 if the pi goes rip
		String str = null;
		
		for(int i = 1; i < 11; i++){
			String read = sp.readString(1);
			
			if (!read.equals("t")){
				str = str + read;
			}else{
				ang = Double.parseDouble(str);
				str = null;
			}
		}
		return ang;
		
		
		
		
		
		
		// while (true) {
		// in = sp.readString(1);
		// if (!in.equals("t")) {
		// str = str + in;
		// } else {
		// double ret = Double.parseDouble(str);
		// str = "";
		// return ret;
		// }
		// }
		// } else {
		// return -1000;// -1000 means we don't see anything
		// }
		
	}

	public void send(String msg) {
		sp.writeString(msg);
	}
}
