package org.usfirst.frc.team1559.robot;

import java.io.*;
import java.net.*;

public class SocketClient {
	

    
	public SocketClient() {
	
	}
        
	
	public void read(){ //for testing now I guess
		try {
  
	    	String sentence;
	
	        Socket clientSocket = new Socket("10.15.59.7", 23);
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	        sentence = inFromServer.readLine();
	        System.out.println(sentence);
	        clientSocket.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public double grabAngle() {
        
		double result = -2000;
		
		try {
	        Socket clientSocket = new Socket("10.15.59.7", 23);
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
	        String in = inFromServer.readLine();
			
			if (in.endsWith(" ")) {
				in = in.substring(0, in.indexOf(" "));
				try {
					result = Double.parseDouble(in);
				} catch (Exception e) {
					System.out.println("Bad Jetson");
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			System.out.println("PLUG IN THE DAMN CABLE");
			e.printStackTrace();
		}

		return result;
	}
	
	public double getAdjustedAngle(double xPos, double yPos) {

		double d = Wiring.CAMERA_ROTATION_OFFSET;
		double camAngle = grabAngle() + Wiring.SHOOTER_SUCKS_CONSTANT; //degrees
		double ret = 0.0;

		ret = Math.sqrt(Math.pow(yPos - d, 2) + Math.pow(xPos, 2));
		ret *= Math.sin(Math.toRadians(180 - camAngle));
		ret /= Math.sqrt(Math.pow(xPos, 2) + Math.pow(yPos, 2));
		ret = Math.asin(ret);
	
		return Math.toDegrees(ret);
	}
	
	public void send(String str){
		
		try{
			Socket clientSocket = new Socket("10.15.59.7", 23);
	
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(str);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
