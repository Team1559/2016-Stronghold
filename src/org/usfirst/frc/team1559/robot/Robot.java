package org.usfirst.frc.team1559.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	AHRS ahrs;
	 RobotDrive robot;
	 boolean shootDone = false;
	 CANTalon leftF;
	 CANTalon leftR;
	 CANTalon rightF;
	 CANTalon rightR;
	Joystick stick;
	Timer timer;
	boolean isInverted;
	WFFL waffle;
	Transmission tranny;
	int listPos = 0;
	SmartDashboard smrt = new SmartDashboard();
	SocketClient sc = new SocketClient();

	// Comments are for a 4 motor drive system whereas uncommented code just
	// does 2

	public void robotInit() {
		ahrs = new AHRS(SPI.Port.kMXP);
		 leftF = new CANTalon(Wiring.LEFT_FRONT_CAN_TALON);
		 rightF = new CANTalon(Wiring.RIGHT_FRONT_CAN_TALON);
//		 leftR = new CANTalon(Wiring.LEFT_REAR_CAN_TALON);
//		 rightR = new CANTalon(Wiring.RIGHT_REAR_CAN_TALON);
		 robot = new RobotDrive(leftF,rightF);
//		 robot = new RobotDrive(leftF,leftR,rightF,rightR);
		stick = new Joystick(Wiring.JOYSTICK0);
		tranny = new Transmission(stick);
		waffle = new WFFL("/home/lvuser/format.wffl");
						//will eventually be at /media/sda0/filename.wffl

	}

	public void autonomousInit() {
		waffle.reset();
		//ahrs.reset();
		waffle.interpret();
		waffle.left.setInverted(false);
		waffle.right.setInverted(false);
	}

	public void autonomousPeriodic() {
		
		smrt.putNumber("Yaw:" , waffle.yaw);

		Command current = waffle.list.get(listPos);
		if (current.command == "TURN") {
			waffle.turnToAngle(current.angle);
			current.done = true;
			System.out.println("Works YAY");
		} else if (current.command == "GO") {
			System.out.println("Works Drive");
			waffle.drive(current.angle, current.dist, 0, current.speed);
			if(waffle.keepRunning == false) {
				System.out.println("Works stop now");
				current.done = true;
			}
		} else if(current.command == "SHOOT"){
			sendRecieveCenterValues();
			boolean shootDone = waffle.center();
			if(shootDone){
				current.done = true;
				System.out.println("DONE SHOOTING!");
			}
			
		}
		
		
		if ((current.done == true)) {
			current.done = false;
			
			if(waffle.list.size() > listPos - 1){
				listPos++;
			} else {
				//stop
			}
			
		}
		waffle.length++;
	}

	public void teleopInit() {
		isInverted = true;
		waffle.left.setInverted(true);
		waffle.right.setInverted(true);
		// leftF.setInverted(isInverted);

	}

	public void teleopPeriodic() {
		sendRecieveCenterValues();
		waffle.myRobot.arcadeDrive(stick);
		waffle.Traction();
		if (stick.getRawButton(6)) {

			tranny.gear1();

		} else if (stick.getRawButton(5)) {

			tranny.gear2();

		}
		SmartDashboard.putNumber("Yaw:", ahrs.getYaw());
	}
	
	public void sendRecieveCenterValues(){
		//String [] center = sc.read();
		//int cx = Integer.parseInt(center[0]);
		//int cy = Integer.parseInt(center[1]);
		//waffle.cx = cx;
		//waffle.cy = cy;
	}
	

	public void testInit() {

	}

	public void testPeriodic() {

	}

	public void disabledInit() {

	}

	public void disabledPeriodic() {

	}
}
