package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class SerialClient{
	private final int BAUDRATE = 115200;
	private final String sendChar = "s";
	SerialPort sp;
	public SerialClient(){
		sp = new SerialPort(BAUDRATE, Port.kMXP);
	}
	public double grabAngle(){
		sp.reset();
		System.out.println("sc.run() is working...");//yeah it is
		String str = "";
			String in = "";
			if (sp.writeString(sendChar) > 0){//send the pi 's', which sends the current angle
				/*
				 * The only reason this isn't threaded is 
				 * because the roborio thread manager is actually slower 
				 * than this while loop
				 * THIS HAS BEEN TESTED MULTIPLE TIMES, SO EVERYONE SHUT UP
				 * Also, thsi is a while loop because we need to construct the value 
				 * one character at a time, so this should never go through more than 5 or 6 loops.
				 */
				while(true){
					in = sp.readString(1);
					if (!in.equals("t")){
						str = str + in;
					} else {
						double ret = Double.parseDouble(str);
						str = "";
						return ret;
					}
				}
			} else {
				return -1000;//-1000 means we don't see anything
			}
	}

}
