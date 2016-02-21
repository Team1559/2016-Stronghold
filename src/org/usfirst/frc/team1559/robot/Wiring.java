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

	public static final String WFFL_NAME = "/media/sda1/rally.wffl";
	
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
	public static final int GATHERER_ANALOG_INPUT = 1;
	public static final int PDP_CHANNEL_GATHERER = 3;
	
	// shooter constants
	public static final int SHOOTER_UP_DELAY = 50;
	public static final int SHOOTER_DOWN_DELAY = 5;
	public static final int SHOOTER_UP_SOLENOID = 6;
	public static final int SHOOTER_DOWN_SOLENOID = 5;
	public static final int MAGNET = 0;

	// ball clamp
	public static final int CLAMP_LEFT_ID = 7;
	public static final int CLAMP_RIGHT_ID = 6;
	public static final int CLAMP_BALL_SENSOR_ID = 0;
	public static final double CLAMP_SENSOR_THRESHOLD = 2.5;
	
	// gatherer constants
	public static final double GATHERER_SPEED = 1.0;
	public static final int GATHERER_LIMIT_ID = 2;
	public static final int GATHERER_LIFT = 2;
	public static final int GATHERER_ROTATE = 1;
	public static final int GATHERER_SAFE_SHOOT_ANGLE = 75;
	public static final int GATHER_UP_LEVEL_BUT = 6;
	public static final int GATHER_DOWN_LEVEL_BUT = 5;

	public static final double GATHERER_GATHER_POSITION = 88;
}