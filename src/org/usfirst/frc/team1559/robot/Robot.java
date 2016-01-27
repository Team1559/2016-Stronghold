package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.print.DocFlavor.READER;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	AHRS ahrs;
	SmartDashboard board;
	StringBuilder directions = new StringBuilder();
	RobotDrive myRobot;
	Joystick stick;
	Talon right;
	Talon left;
	File filename = new File("/home/lvuser/juan.txt");

	double last_accel_X;

	double lastx = 0.0;
	double lasty = 0.0;
	double lastz = 0.0;

	double lastgyrox = 0.0;
	double lastgyroy = 0.0;
	double lastgyroz = 0.0;

	double last_accel_Y;
	int countx = 0;
	// Gyro Stuff\\
	double time = 0.0;
	double yaw = 0.0;
	final double kpturn = 0.009;
	double gyro_yaw;
	final double kp = 0.1;
	final double maxError = 1;
	final double tolerance = .001;
	int length = 0;
	double yawError;
	double unchangedYawError;
	double gyro_angle;
	PIDController pid;
	// End of gyro\\
	int angleLength = 0;
	WFFL waffle = new WFFL("/home/lvuser/juan.txt");
	char [] a;
	final static double kCollisionThreshold_DeltaG = 0.5f;
	
	final double yawposthreshold = 2;
	final double yawnegthreshold = 2;

	public Robot() {
		try {
			ahrs = new AHRS(SPI.Port.kMXP);
		} catch (Exception e) {
			e.getMessage();
		}
		stick = new Joystick(0);

	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		board = new SmartDashboard();
		ahrs.reset();
		right = new Talon(1);
		left = new Talon(0);
		myRobot = new RobotDrive(left, right);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	public void autonomousInit() {
		
		ahrs.reset();
		length = 0;
		right.setInverted(false);
		left.setInverted(false);
		time = 0.0;
		yawError = 0.0;
		
		
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@SuppressWarnings("static-access")
	public void autonomousPeriodic() {
		waffle = new WFFL("/home/lvuser/juan.txt");
		yaw = ahrs.getYaw();
		time++;
		double timeSec = time / 50;
		
		
		try {
			waffle.printAll();
			waffle = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		board.putNumber("Gyro X: ", lastgyrox);
		board.putNumber("Gyro Y: ", lastgyroy);
		board.putNumber("Gyro Z: ", lastgyroz);
		board.putNumber("Accel X: ", lastx);
		board.putNumber("Accel Y: ", lasty);
		board.putNumber("Accel Z: ", lastz);
		board.putNumber("Yaw: ", yaw); // The important one
		board.putNumber("Time:", timeSec);

		//drive(180, 2, 0, .6);
		//drive(-90, 2, 2, .4);
		//drive(0, 2, 4, .4);
		length++;
		// go distance="4" <-- time
		// turn 180 <-- degrees
	}

	@SuppressWarnings("static-access")
	public void drive(int angle, double seconds, double startTime, double speed) {
		// yawError = (ahrs.getYaw() - angle);

		if ((angle == 180) && (yaw < -0.1)) {
			yaw = 360 + ahrs.getYaw();
		} else if ((angle == -180) && (yaw > 0.1)) {
			yaw = ahrs.getYaw() - 360;
		}

		unchangedYawError = ahrs.getYaw() - angle;
		yawError = yaw - angle;

		if (yawError > 180) {
			yawError = yawError - 360;
		} else if (yawError < -180) {
			yawError = (yawError + 360);

		if ((length >= startTime * 50)
				&& (length <= (seconds + startTime) * 50)) {
			if ((Math.abs(yawError)) >= tolerance) {
				if ((Math.abs(yawError * kpturn)) < maxError) {
					myRobot.drive(speed, -(yawError * kpturn));
				} else {
					if (yawError < 0) {
						myRobot.drive(speed, maxError);
					} else {
						myRobot.drive(speed, -maxError);
					}
				}
			} else {
				myRobot.drive(speed, 0);
			}
		}

		board.putNumber("Seconds Running: ", seconds);
		board.putNumber("Start Time: ", startTime);
		board.putNumber("YawError", yawError);

		SmartDashboard.putNumber("length: ", length);
		SmartDashboard.putNumber("Unchanged Yaw Error", unchangedYawError);
	}
	}
	
	

	public void teleopInit() {
		ahrs.reset();
		right.setInverted(true);
		left.setInverted(true);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@SuppressWarnings("static-access")
	public void teleopPeriodic() {

		double current_accel_x = ahrs.getRawAccelX();
		double current_accel_y = ahrs.getRawAccelY();
		double current_accel_z = ahrs.getRawAccelZ();
		double current_gyro_x = ahrs.getRawGyroZ();
		double current_gyro_y = ahrs.getRawGyroY();
		double current_gyro_z = ahrs.getRawGyroZ();
		double current_yaw = ahrs.getYaw();

		countx++;
		myRobot.arcadeDrive(stick);

		if (countx == 50) {
			if (!ahrs.isCalibrating()) {
				/**
				 * System.out.println("Accel X: " + lastx);
				 * System.out.println("Accel Y: " + lasty);
				 * System.out.println("Accel Z: " + lastz);
				 * System.out.println("Gyro X: " + lastgyrox);
				 * System.out.println("Gyro Y: " + lastgyroy);
				 * System.out.println("Gyro Z: " + lastgyroz);
				 **/
				lastx = current_accel_x;
				lasty = current_accel_y;
				lastz = current_accel_z;
				board.putNumber("Gyro X: ", lastgyrox);
				board.putNumber("Gyro Y: ", lastgyroy);
				board.putNumber("Gyro Z: ", lastgyroz);
				board.putNumber("Accel X: ", lastx);
				board.putNumber("Accel Y: ", lasty);
				board.putNumber("Accel Z: ", lastz);
				board.putNumber("Yaw: ", current_yaw); // The important one
				board.putNumber("Gyro X Diff: ", current_gyro_x - lastgyrox);
				board.putNumber("Gyro Y Diff: ", current_gyro_y - lastgyroy);
				board.putNumber("Gyro Z Diff: ", current_gyro_z - lastgyroz);
				lastgyrox = current_gyro_x;
				lastgyroy = current_gyro_y;
				lastgyroz = current_gyro_z;
				countx = 0;

			} else {
				System.out.println("Still calibrating m9...");
			}

			if (left.getSpeed() == 0 || right.getSpeed() == 0) {
				float Heading = ahrs.getCompassHeading();
				board.putNumber("Heading: ", Heading);
				board.putBoolean("Moving: ", false);
			} else {
				board.putBoolean("Moving: ", true);
			}
		}

		/**
		 * if ((Math.abs(current_jerkX) > kCollisionThreshold_DeltaG) ||
		 * (Math.abs(current_jerkY) > kCollisionThreshold_DeltaG)) { collision =
		 * true; }
		 **/

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

	public void disabledPeriodic() {
		// yawError = ahrs.getYaw();
	}

	public void disabledInit() {
		System.out.print(ahrs.isConnected());
	}

}
