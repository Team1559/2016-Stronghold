package org.usfirst.frc.team1559.robot;

import java.io.*;
import java.net.*;

public class SocketClient {
    
	public SocketClient() {
		
	}
        
	
	public String[] read(){
		try {
  
	    	String sentence;
	        String modifiedSentence;
	        String sentence1;
	        String modifiedSentence1;
	        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	
	        Socket clientSocket = new Socket("10.15.59.6", 15559);
	        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
	        sentence = inFromUser.readLine();
	        sentence1 = inFromUser.readLine();
	        outToServer.writeBytes(sentence + '\n');
	        outToServer.writeBytes(sentence1 + '\n');
	        modifiedSentence = inFromServer.readLine();
	        modifiedSentence1 = inFromServer.readLine();
	        String[] args = new String[2];
	        args[0] = modifiedSentence;
	        args[1] = modifiedSentence1;
	        clientSocket.close();
	        return args;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
