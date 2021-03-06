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
	
	
	// DriveTrain
	public static final int FRONT_LEFT_LEAD_TALON = 13;
	public static final int FRONT_RIGHT_LEAD_TALON = 12;
	public static final int BACK_LEFT_FOLLOW_TALON = 11;
	public static final int BACK_RIGHT_FOLLOW_TALON = 10;
	
	public static final int VOLTAGE_RAMP_RATE = 40;
	public static final int PULSES_PER_INCH = 447;

	
	// Transmission
	public static final int SHIFT_1 = 3;
	public static final int SHIFT_2 = 4;
	
	public static final double SHIFT_UP_SPEED = 4.6; // m/s
	public static final double SHIFT_DOWN_SPEED = 4.4; // m/s
	public static final double LOW_SPEED_MULTIPLIER = 1.0 ; //Reccommended 2.2, Chris didn't like it....

	
	// Joysticks
	public static final int PILOT_JOYSTICK = 0;
	public static final int COPILOT_JOYSTICK = 1;

	// NAVx stuff
	public static final double OPTIMAL_TURNT_SPEED = 1;	

	// gatherer/shooter controls
	public static final int BTN_GATHERER_TO_TOP = 7;
	public static final int BTN_GATHERER_TO_MID = 5;
	public static final int BTN_GATHERER_TO_BOT = 8;
	public static final int BTN_SHOOT = 3;
	
	
	//GATHERER PID, devices
	public static final double GATHERER_PID_P = -.1;
	public static final double GATHERER_PID_I = 0;
	public static final double GATHERER_PID_D = 0;
	public static final double GATHERER_PID_TOLERANCE = 1;
	
	public static final int GATHERER_ANALOG_INPUT = 3;
	public static final int PDP_CHANNEL_GATHERER = 3;
	public static final double GATHERER_SPEED = 1.0;
	public static final int GATHERER_LIMIT_ID = 2;
	public static final int GATHERER_LIFT = 2;
	public static final int GATHERER_ROTATE = 1;
	public static final int GATHERER_SAFE_SHOOT_ANGLE = 75;
	
	public static final int BTN_GATHER_UP_LEVEL = 6;
	public static final int BTN_GATHER_DOWN_LEVEL = 5;
	public static final int BTN_GATHERER_OVERRIDE = 4;
	
	public static final double GATHER_MID_TARGET = 2300; //this will change!
	public static final double GATHER_HOME_TARGET = 3850;
	public static final double GATHER_BOTTOM_TARGET = 1776; //freedom
	
	
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
	public static final double CLAMP_LOW = 1.81;
	public static final double CLAMP_HIGH = .8;
	
	//angles
	public static final double LEFT_OUT_POS = 60; //60 deg
	public static final double LEFT_CLAMP_POS = 130; //130 deg
	public static final double RIGHT_OUT_POS = 130; //130 deg
	public static final double RIGHT_CLAMP_POS = 60; //60 deg
	
	public static final double MAX_PWM = 1.850; //1850 uS
	public static final double MIN_PWM = 1.200; //1200 uS
	
	//camera stuffs
	public static final int HORIZONTAL_RESOLUTION = 800;
	public static final double HORIZONTAL_FOV = 60.0;
	public static final int THRESHOLD = 40;
	public static final double CAMERA_TOLERANCE = .5;
	public static final int CAMERA_ROTATION_OFFSET = 15; //(inches, like wffl)
	public static final double SHOOTER_SUCKS_CONSTANT = .5; //1/2 degree left (negative means right)

	
	//Robot configuration
	public static final boolean hasGatherer = true;
	public static final boolean hasArduino = false;
	public static final boolean HAS_CLIMBER = true;
	public static final boolean hasBallClamp = true;
	
	
	//de-Light
	public static final int FLASH_RELAY = 0;//should actually be a relay because spikes are H-Bridges
	public static final int FLASH_BUTT_ON = 2;
	public static final int FLASH_BUTT_STROBE = 8;

	
	//pneumatic kicker
	public static final int KICK_ME = 0;
	public static final int UN_KICK_ME = 1;

	//climber
	public static final int CLIMBER_TALON = 3; // liam approved


	public static final double WFFL_TARGET_OFFSET = 20; //inches
	
	//public static final int PI_KILL_SWITCH = 8; //picked a random number for now
	
	

}