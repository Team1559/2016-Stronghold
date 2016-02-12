package org.usfirst.frc.team1559.robot;

/**
 * A command taken from a .wffl file.
 */
public class Command {
	
	
	String command;
	double dist;
	double speed;
	double time;
	double angle;
	String id;
	boolean active;
	String pattern;
	
	boolean done;
	
	/**
	 * 
	 * @param command
	 * @param dist
	 * @param speed
	 * @param time
	 * @param angle
	 * @param id
	 * @param active
	 * @param pattern
	 */
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
