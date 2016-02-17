package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class SerialClient {
	private final int BAUDRATE = 115200;
	SerialPort sp;
	public SerialClient(){
		sp = new SerialPort(BAUDRATE, Port.kMXP);
	}
	public String read(){
		String args = "";
		try{
			int x = sp.getBytesReceived();
			if (x > 0)
				args = sp.readString();
		}catch (Exception e){
				System.out.println("Ayy lmao");
			}
		return args;
	}

}
