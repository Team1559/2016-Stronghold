package org.usfirst.frc.team1559.robot;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class WFFL {

	String path;
	Scanner s;
	File file;
	
	public WFFL(String path) throws FileNotFoundException{
		this.path = path;
		file = new File(path);
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	public void interpret(){
		
		String raw = s.nextLine() + " "; //pads the string so this next stuff works.
		String command = raw.substring(0,raw.indexOf(" "));
		String time;
		String dist;
		String speed;
		String id;
		String temp;
		String angle; //clockwise
		boolean active;
		String pattern;
		
		if(command.equals("GO")){
			temp = raw.substring(13);
			temp = temp.substring(0, temp.indexOf("\""));
			dist = temp;
			
			temp = raw.substring(raw.indexOf("speed=\"") + 7, raw.length()-2);
			speed = temp;
		} else if(command.equals("WAIT")){
			temp = raw.substring(raw.indexOf(" ") + 1);
			time = temp;
		} else if(command.equals("TURN")){
			temp = raw.substring(raw.indexOf(" ") + 1);
			angle = temp;
		} else if(command.equals("SHOOT")){
			System.out.println("SHOOT!");
		} else if(command.equals("DEFENSE")){
			temp = raw.substring(12);
			temp = temp.substring(0, temp.indexOf("\""));
			id = temp;

			temp = raw.substring(raw.indexOf("active=\"") + 8, raw.length()-2);
			active = Boolean.valueOf(temp);
		} else if(command.equals("LIGHTS")){
			temp = raw.substring(raw.indexOf("\"") + 1, raw.length()-2);
			pattern = temp;
		} else if(command.equals("PRINT")){
			temp = raw.substring(raw.indexOf(" ") + 1);
			System.out.println(temp);
		} else if(command.equals("NOTE")){
			System.out.println(raw);
		} else {
			System.err.println("UNKNOWN COMMAND!");
		}
		
		if(s.hasNextLine()){
			interpret();
		}
	}
	
	public void printAll() throws IOException {
		System.out.println(s.nextLine());
		if(s.hasNextLine()){
			printAll();
		}
		
	}
	
}
