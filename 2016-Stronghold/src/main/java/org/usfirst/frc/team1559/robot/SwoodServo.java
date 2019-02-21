package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.PWM;

public class SwoodServo extends PWM{

	/*
	 * Dear WPI,
	 * 
	 * You ultra-suck!
	 * 
	 * Love, 
	 * Cody
	 */
	public SwoodServo(int channel, double min_width, double max_width){
		super(channel);
		double mid = (min_width - max_width)/2;
		setBounds(max_width, 0, min_width + mid, 0, min_width);
	}
	
}
