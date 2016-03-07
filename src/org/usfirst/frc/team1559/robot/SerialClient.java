package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class SerialClient{
	private final int BAUDRATE = 115200;
	private final String sendChar = "s";
	SerialPort sp;
	private double args = -1000.0;
	public SerialClient(){
		sp = new SerialPort(BAUDRATE, Port.kMXP);
	}
	public double getSerialIn(){
		synchronized(this){
			return args;
		}
	}
	public void setSerialIn(String in){
		synchronized(this){
//			System.out.println(in);
			try{
				args = Double.valueOf(in);
			} catch (Exception e){
				System.out.println("ERROR PARSING");
				e.printStackTrace();
			}
		}
	}
	public void run(){
		sp.reset();
		System.out.println("Thread is alive!");
		String str = "";
//		while(!isInterrupted() && isAlive()){
			String in = "";
			if (sp.writeString(sendChar) > 0){
				while(true){
					in = sp.readString(1);
					if (!in.equals("t")){
						str = str + in;
					} else {
						setSerialIn(str);
//						System.out.println(str);
						str = "";
						break;
					}
				}
			} else {
				args = -1000;
			}
//		}
//		System.out.println("We should never see this");
	}

}
