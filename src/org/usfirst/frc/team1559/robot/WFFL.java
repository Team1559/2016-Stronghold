package org.usfirst.frc.team1559.robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;

public class WFFL {

	byte everything;
	String raw;
	String command;
	String path;
	Scanner s;
	File file;
	double yaw = 0.0;
	final double kpturn = 0.009;
	double gyro_yaw;
	double kpBase = 0.05;
	final double maxError = 1;
	final double tolerance = .001;
	int length = 0;
	double yawError;
	double unchangedYawError;
	double gyro_angle;
	AHRS ahrs;
	Talon right = new Talon(1);
	Talon left = new Talon(0);
	RobotDrive myRobot = new RobotDrive(left, right);
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

	public WFFL(String path) {
		this.path = path;
		file = new File(path);
		left.setInverted(true);
		right.setInverted(true);
		try {
			s = new Scanner(file);
			ahrs = new AHRS(SPI.Port.kMXP);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void interpret() {
		raw = s.nextLine() + " "; // pads the string so this next stuff

		// works.
		command = raw.substring(0, raw.indexOf(" "));

		if (command.equals("GO")) {
			temp = raw.substring(13);
			temp = temp.substring(0, temp.indexOf("\""));
			dist = Double.valueOf(temp);
			System.out.println(dist);
			temp = raw.substring(raw.indexOf("speed=\"") + 7, raw.length() - 2);
			speed = Double.valueOf(temp);
			global_startTime = System.currentTimeMillis() / 1000;
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("GO", dist, speed, 0, 0, "", false, ""));
		} else if (command.equals("WAIT")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			time = Double.valueOf(temp);
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("WAIT", 0, 0, time, 0, "", false, ""));
		} else if (command.equals("TURN")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			angle = Double.valueOf(temp);
			global_startTime = System.currentTimeMillis() / 1000;
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("TURN", 0, Wiring.OPTIMAL_TURNT_SPEED, 0, angle, "", false, ""));

		} else if (command.equals("SHOOT")) {
//			System.out.println("SHOOT!");
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("SHOOT", 0, 0, 0, 0, "", false, ""));
		} else if (command.equals("DEFENSE")) {
			temp = raw.substring(12);
			temp = temp.substring(0, temp.indexOf("\""));
			id = temp;

			temp = raw
					.substring(raw.indexOf("active=\"") + 8, raw.length() - 2);
			active = Boolean.valueOf(temp);
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("DEFENSE", 0, 0, 0, 0, id, active, ""));
		} else if (command.equals("LIGHTS")) {
			temp = raw.substring(raw.indexOf("\"") + 1, raw.length() - 2);
			pattern = temp;
			
			//(String command, double dist, double speed, double time, double angle, String id, boolean active, String pattern)
			list.add(new Command("LIGHTS", 0, 0, 0, 0, "", false, pattern));
		} else if (command.equals("PRINT")) {
			temp = raw.substring(raw.indexOf(" ") + 1);
			System.out.println(temp);
		} else if (command.equals("NOTE")) {
			System.out.println(raw);
		} else {
			System.err.println("UNKNOWN COMMAND!");
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

	public void reset() {
		s = null;
		try {
			s = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean drive(double angle, double seconds, double startTime,
			double speed) {
		double kp = 0;

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

		if (speed < 0) {
			kp *= -1;
		}
		// 0.1 - 0.4 x7
		// 0.4 - 0.6 x1
		// 0.6 - 0.7 x.33
		// 0.7 - 1.0 /20

		unchangedYawError = ahrs.getYaw() - angle;
		yawError = yaw - angle;

		if (yawError > 180) {
			yawError = yawError - 360;
		} else if (yawError < -180) {
			yawError = (yawError + 360);
		}
		if ((length >= startTime * 50)
				&& (length <= (seconds + startTime) * 50)) {
			if ((Math.abs(yawError)) >= tolerance) {
				if ((Math.abs(yawError * kp)) < maxError) {
					myRobot.drive(speed, -(yawError * kp));
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
		if ((System.currentTimeMillis() / 1000) < (seconds + global_startTime)) {
			drive(0, seconds, global_startTime, speed);
			return true;
		}
		return false;
	}
}
