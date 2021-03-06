package org.usfirst.frc.team1559.robot.subsystems;

import org.usfirst.frc.team1559.robot.Wiring;

import edu.wpi.first.wpilibj.Talon;

public class Climber {

	private boolean everythingIsPrivate = true;
	private boolean armed = false;
	// Insert code here

	private Talon winch;

	public Climber() {

		winch = new Talon(Wiring.CLIMBER_TALON);

	}

	// public boolean checkTime(){
	// return DriverStation.getInstance().getMatchTime() < 30; //Hi Petre Megers
	// }

	public void arm() {
		armed = true;
	}

	public void disarm() {
		armed = false;
	}

	public boolean isArmed() {

		return armed;

	}

	public void drive() {
		winch.set(1.0);
	}

	public void stopIt() {
		winch.set(0.0);
	}
	
	public void reverse() {
		winch.set(-1.0);
	}
}
