package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;

/**
 * Used to interpret and execute a .wffl file.
 */
public class WFFL {

	String raw;
	String command;
	String path;
	Scanner s;
	double distance = 0;
	File file;
	PowerDistributionPanel pdp = new PowerDistributionPanel();
	boolean keepRunning = true;
	boolean keepTurning = true;
	double yaw = 0.0;
	final double kpturn = 0.009;
	double gyro_yaw;
	double kpBase = 0.001;
	final double maxError = 1;
	final double tolerance = .001;
	final double turnTolerance = .60;// originally .5
	int length = 0;
	double yawError;
	double unchangedYawError;
	double gyro_angle;
	AHRS ahrs;
	long global_startTime = System.currentTimeMillis() / 1000;
	double time;
	double dist;
	double speed;
	String id;
	String temp;
	double angle; // clockwise
	boolean active;
	String pattern;
	ArrayList<Command> list = new ArrayList<Command>();
	public int cx, cy;
	RobotDrive rd;
	CANTalon rightM, leftM;
	Transmission tranny;

	public WFFL(String path, RobotDrive rd, Transmission tranny) {
		this.path = path;
		this.tranny = tranny;
		file = new File(path);
		try {
			s = new Scanner(file);
			ahrs = new AHRS(SPI.Port.kMXP);
			// System.out.println("HELLOW from a try catch block");
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.rd = rd;
		this.rightM = tranny.getRightMotor();
		this.leftM = tranny.getLeftMotor();
	}

	public void interpret() {
		raw = s.nextLine() + " "; // pads the string so this next stuff
		raw.replaceAll("^M", ""); // safety first

		// works.
		command = raw.substring(0, raw.indexOf(" "));

		if (raw.trim().equals("<<START>>")) {
			System.out.println("START");
		} else if (raw.trim().equals("<<STOP>>")) {
			System.out.println("<<STOP>>");
			list.add(new Command("STOP", 0, 0, 0, 0, "", false, ""));
		} else if (command.equals("GO")) {
			temp = raw.substring(13);
			temp = temp.substring(0, temp.indexOf("\""));
			dist = Double.valueOf(temp);
			System.out.println(dist);
			temp = raw.substring(raw.indexOf("speed=\"") + 7, raw.length() - 2);
			speed = Double.valueOf(temp);
			global_startTime = System.currentTimeMillis() / 1000;

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("GO", dist, speed, 0, 0, "", false, ""));
		} else if (command.equals("WAIT")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			time = Double.valueOf(temp);

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("WAIT", 0, 0, time, 0, "", false, ""));
		} else if (command.equals("TURN")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			angle = Double.valueOf(temp);
			global_startTime = System.currentTimeMillis() / 1000;

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("TURN", 0, Wiring.OPTIMAL_TURNT_SPEED, 0,
					angle, "", false, ""));

		} else if (command.equals("SHOOT")) {
			// System.out.println("SHOOT!");

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("SHOOT", 0, 0, 0, 0, "", false, ""));
		} else if (command.equals("DEFENSE")) {
			temp = raw.substring(12);
			temp = temp.substring(0, temp.indexOf("\""));
			id = temp;

			temp = raw
					.substring(raw.indexOf("active=\"") + 8, raw.length() - 2);
			active = Boolean.valueOf(temp);

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("DEFENSE", 0, 0, 0, 0, id, active, ""));
		} else if (command.equals("LIGHTS")) {
			temp = raw.substring(raw.indexOf("\"") + 1, raw.length() - 2);
			pattern = temp;

			// (String command, double dist, double speed, double time, double
			// angle, String id, boolean active, String pattern)
			list.add(new Command("LIGHTS", 0, 0, 0, 0, "", false, pattern));
		} else if (command.equals("PRINT")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			System.out.println(temp);
		} else if (command.equals("NOTE")) {
			System.out.println(raw);
		} else {
			System.err.println("UNKNOWN COMMAND:" + raw);
		}
		if (s.hasNextLine()) {
			interpret();
		}
	}

	public void printAll() throws IOException {
		System.out.println(s.nextLine());
		if (s.hasNextLine()) {
			printAll();
		}
	}

	public double getCurrentAngle() {
		return ahrs.getAngle();
	}

	public void reset() {
		s = null;
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void turnToAngle(double angle) {
		double kpturn = 0.1; // change to .15?
		yaw = ahrs.getYaw();

		if ((angle == 180) && (yaw < -0.1)) {
			yaw = 360 + ahrs.getYaw();
		} else if ((angle == -180) && (yaw > 0.1)) {
			yaw = ahrs.getYaw() - 360;
		}

		yawError = yaw - angle;

		if (yawError > 180) {
			yawError = yawError - 360;
		} else if (yawError < -180) {
			yawError = (yawError + 360);
		}

		kpturn = Math.abs((yawError / 180)) + .1;

		double correctionTurn = kpturn * yawError;
		if (correctionTurn >= 0.5) {
			correctionTurn = 0.5;
		} else if (correctionTurn <= -0.5) {
			correctionTurn = -0.5;
		} else if (correctionTurn <= .125 && correctionTurn > 0) {
			correctionTurn = .125;
		} else if (correctionTurn >= -.125 && correctionTurn < 0) {
			correctionTurn = -.125;
		} else { // well crap clause
			System.out.print("well crap = " + correctionTurn);
		}

		System.out.println(yawError);
		if ((Math.abs(yawError) > turnTolerance)) {
			keepTurning = true;
			if (yawError > 0) {
				leftM.set(correctionTurn * Wiring.OPTIMAL_TURNT_SPEED);
				rightM.set(correctionTurn * Wiring.OPTIMAL_TURNT_SPEED);
			} else if (yawError < 0) {
				leftM.set(correctionTurn * Wiring.OPTIMAL_TURNT_SPEED);
				rightM.set(correctionTurn * Wiring.OPTIMAL_TURNT_SPEED);

			}
		} else {
			leftM.set(0);
			rightM.set(0);
			keepTurning = false;
		}
	}

	public void drive(double angle, double inches, double speed) {
		double kp = 0;
		yaw = ahrs.getYaw();

		if ((angle == 180) && (yaw < -0.1)) {
			yaw = 360 + ahrs.getYaw();
		} else if ((angle == -180) && (yaw > 0.1)) {
			yaw = ahrs.getYaw() - 360;
		}

		if (Math.abs(speed) >= .1 && Math.abs(speed) <= .4) {
			kp = kpBase * 7;
		} else if (Math.abs(speed) > .4 && Math.abs(speed) <= .6) {
			kp = kpBase;
		} else if (Math.abs(speed) > .6 && Math.abs(speed) <= .7) {
			kp = kpBase * .33;
		} else if (Math.abs(speed) > .7 && Math.abs(speed) <= 1) {
			kp = kpBase * .05;
		}

		// 0.1 - 0.4 x7
		// 0.4 - 0.6 x1
		// 0.6 - 0.7 x.33
		// 0.7 - 1.0 x.05

		// math!
		// TODO: try it out, stick to what works though
		// kp = kpBase * 20.045 * (Math.pow(Math.E, -6.164 * speed));

		if (speed < 0) {
			kp *= -1;
		}

		unchangedYawError = ahrs.getYaw() - angle;
		yawError = yaw - angle;

		if (yawError > 180) {
			yawError = yawError - 360;
		} else if (yawError < -180) {
			yawError = (yawError + 360);
		}

		System.out.println((tranny.getLDisplacement() + tranny
				.getRDisplacement()) / 2);

		if (((tranny.getLDisplacement() + tranny.getRDisplacement()) / 2) <= inches) {
			keepRunning = true;
			if ((Math.abs(yawError)) >= tolerance) {
				if ((Math.abs(yawError * kp)) < maxError) {
					rd.drive(speed, (yawError * kp));
				} else {
					if (yawError < 0) {
						rd.drive(speed, -maxError);
					} else {
						rd.drive(speed, maxError);
					}
				}
			} else {
				rd.drive(speed, 0);
			}
		} else {
			keepRunning = false;
			tranny.resetEncoders();
		}
	}

	// maybe one day

	// public void traction() {
	// double accelVals[] = new double[25];
	// int runTime = 0;
	// double avg = 0;
	// boolean slip = false;
	// // 1.25
	//
	// accelVals[runTime] = ahrs.getWorldLinearAccelY();
	// for (int i = 0; i < accelVals.length; i++) {
	// avg += accelVals[i];
	// }
	//
	// avg /= accelVals.length;
	//
	// if (Math.abs(avg) < .007 && pdp.getCurrent(0) > 40) {
	// slip = true;
	// leftM.set(leftM.get() * .5);
	// } else if (Math.abs(avg) < .007 && pdp.getCurrent(1) > 40) {
	// slip = true;
	// rightM.set(rightM.get() * .5);
	// }
	//
	// if (runTime == (int) accelVals.length) {
	// runTime = 0;
	// }
	//
	// SmartDashboard.putBoolean("Slip: ", slip);
	// SmartDashboard.putNumber("AVG: ", avg);
	// SmartDashboard.putNumber("Current: ", pdp.getCurrent(0));
	// runTime++;
	// }
}