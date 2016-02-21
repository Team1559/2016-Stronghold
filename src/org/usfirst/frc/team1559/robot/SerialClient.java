package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

public class SerialClient extends Thread{
	private final int BAUDRATE = 115200;
	private final String sendChar = "s";
	SerialPort sp;
	private int args = -321;
	public SerialClient(){
		sp = new SerialPort(BAUDRATE, Port.kMXP);
	}
	public int getSerialIn(){
		synchronized(this){
			return args;
		}
	}
	public void setSerialIn(String in){
		synchronized(this){
//			System.out.println(in);
			try{
				args = Integer.parseInt(in);
			} catch (Exception e){
				System.out.println("ERROR PARSING");
				e.printStackTrace();
			}
		}
	}
	public void run(){
		System.out.println("Thread is alive!");
		String str = "";
		while(!isInterrupted() && isAlive()){
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
			try{
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("We should never see this");
	}

}
