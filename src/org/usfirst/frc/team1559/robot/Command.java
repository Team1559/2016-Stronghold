package org.usfirst.frc.team1559.robot;

public class Command {
	
	
	String command;
	double dist;
	double speed;
	double time;
	double angle;
	String id;
	boolean active;
	String pattern;
	
	
	public Command(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern) {
		
		this.command = command;
		this.dist = dist;
		this.speed = speed;
		this.time = time;
		this.angle = angle;
		this.id = id;
		this.active = active;
		this.pattern = pattern;
		
	}
	
}
