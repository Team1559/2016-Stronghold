package org.usfirst.frc.team1559.robot;

/*
 * Use this class for any constant values you have in your code. It will be easier to
 * change once here than 1021 times elsewhere in the code. 
 * 
 * Thou shalt not use magic numbers!
 */
public class Wiring {
	
	/*
	 * 
	 * Format:
	 * public static final type VALUE_IN_CAPS = 23;
	 * 
	 */
	
	//Drive System
	public static final int LEFT_FRONT_CAN_TALON = 10;
	public static final int RIGHT_FRONT_CAN_TALON = 11;
	public static final int LEFT_REAR_CAN_TALON = 12;
	public static final int RIGHT_REAR_CAN_TALON = 13;
	
	//Talon Slavery
	public static final int LEFT_MASTER_TALON = 15;
	public static final int RIGHT_MASTER_TALON = 16;
	public static final int LEFT_SLAVE_TALON = 17;
	public static final int RIGHT_SLAVE_TALON = 18;
	
	//Transmission
	public static final int SHIFT_1 = 0;
//	public static final int SHIFT_2 = 1;
	//determined after consulting Mr. Petilli
	public static final double SHIFT_UP_SPEED = 4.6; // m/s
	public static final double SHIFT_DOWN_SPEED = 4.4; // m/s
	
	//Joysticks
	public static final int JOYSTICK0 = 0;
	public static final int JOYSTICK1 = 1;
	public static final int JOYSTICK2 = 2;
	public static final int JOYSTICK3 = 3;
	
	//NAVx stuff
	public static final double OPTIMAL_TURNT_SPEED = 1;
	
}
