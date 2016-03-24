package org.usfirst.frc.team1559.robot;

/*
 * Use this class for any constant values you have in your code. It will be easier to
 * change once here than 1021 times elsewhere in the code. 
 * 
 * Thou shalt not use magic numbers!
 */
public class Wiring {

	/*
	 * Format: public static final type VALUE_IN_CAPS = 23;
	 */

	public static final String WFFL_NAME = "/media/sda1/comp.wffl";
	
	// Talon Slavery
	public static final int LEFT_MASTER_TALON = 13;
	public static final int RIGHT_MASTER_TALON = 12;
	public static final int LEFT_SLAVE_TALON = 11;
	public static final int RIGHT_SLAVE_TALON = 10;

	// Transmission
	public static final int SHIFT_1 = 3;
	public static final int SHIFT_2 = 4;
	
	// determined after consulting Mr. Petilli
	public static final double SHIFT_UP_SPEED = 4.6; // m/s
	public static final double SHIFT_DOWN_SPEED = 4.4; // m/s
	public static final double LOW_SPEED_MULTIPLIER = 1.0 ; //Reccommended 2.2, Chris didn't like it....

	// Joysticks
	public static final int JOYSTICK0 = 0;
	public static final int JOYSTICK1 = 1;
	public static final int JOYSTICK2 = 2;
	public static final int JOYSTICK3 = 3;

	// NAVx stuff
	public static final double OPTIMAL_TURNT_SPEED = 1;

	// driving stuff
	public static final int VOLTAGE_RAMP_RATE = 40;
	public static final int PULSES_PER_INCH = 447;

	// gatherer/shooter controls
	public static final int BTN_GATHERER_TO_TOP = 7;
	public static final int BTN_GATHERER_TO_MID = 5;
	public static final int BTN_GATHERER_TO_BOT = 8;
	public static final int BTN_SHOOT = 3;
	public static final int GATHERER_ANALOG_INPUT = 3;
	public static final int PDP_CHANNEL_GATHERER = 3;
	public static final double GATHERER_PID_P = -.1;
	public static final double GATHERER_PID_I = 0;
	public static final double GATHERER_PID_D = 0;
	public static final double GATHERER_PID_TOLERANCE = 1;
	
	// shooter constants
	public static final int SHOOTER_UP_DELAY = 50;
	public static final int SHOOTER_DOWN_DELAY = 5;
	public static final int SHOOTER_FIRE_DELAY = 25;
	public static final int SHOOTER_UP_SOLENOID = 6;
	public static final int SHOOTER_DOWN_SOLENOID = 5;

	// ball clamp
	public static final int CLAMP_LEFT_ID = 7;
	public static final int CLAMP_RIGHT_ID = 6;
	public static final int CLAMP_BALL_SENSOR_ID = 0;
	public static final double CLAMP_SENSOR_THRESHOLD = 2.5;
	public static final double CLAMP_LOW = 2.5;
	public static final double CLAMP_HIGH = 3.0;
	
	// gatherer constants
	public static final double GATHERER_SPEED = 1.0;
	public static final int GATHERER_LIMIT_ID = 2;
	public static final int GATHERER_LIFT = 2;
	public static final int GATHERER_ROTATE = 1;
	public static final int GATHERER_SAFE_SHOOT_ANGLE = 75;
	public static final int BTN_GATHER_UP_LEVEL = 6;
	public static final int BTN_GATHER_DOWN_LEVEL = 5;

	public static final int BTN_GATHERER_OVERRIDE = 4;
	
	//camera stuffs
	public static final int HORIZONTAL_RESOLUTION = 800;
	public static final double HORIZONTAL_FOV = 60.0;
	public static final int THRESHOLD = 40;

//	public static final int BTN_BALL_CLAMP_OVERRIDE = 2;
	
	public static final boolean hasGatherer = true;
	public static final boolean hasArduino = false;
	public static boolean hasBallClamp = true;
	
	public static final double CAMERA_TOLERANCE = .61;
	
	public static final int FLASH_DIO = 3;//dio
	public static final int FLASH_BUTT_ON = 7;
	public static final int FLASH_BUTT_STROBE = 8;

	public static final double GATHER_MID_TARGET = 2300; //this will change!
	public static final double GATHER_HOME_TARGET = 3850;
	public static final double GATHER_BOTTOM_TARGET = 1776; //freedom

	public static final int KICK_ME = 0;
	public static final int UN_KICK_ME = 1;
}