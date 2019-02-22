package org.usfirst.frc.team1559.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import org.usfirst.frc.team1559.robot.Constants;
import org.usfirst.frc.team1559.robot.Wiring;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain
{
    private WPI_TalonSRX frontRightLead, frontLeftLead, backLeftFollow, backRightFollow;
    private SpeedControllerGroup rightSide, leftSide;
    private DifferentialDrive drive;
    private Solenoid shift1, shift2;
    private int currentGear;

    public DriveTrain()
    {
        currentGear = 1;
        frontRightLead = new WPI_TalonSRX(Wiring.FRONT_RIGHT_LEAD_TALON);
        frontLeftLead = new WPI_TalonSRX(Wiring.FRONT_LEFT_LEAD_TALON);

        backRightFollow = new WPI_TalonSRX(Wiring.BACK_RIGHT_FOLLOW_TALON);
            backRightFollow.set(ControlMode.Follower, Constants.TIMEOUT);
        backLeftFollow = new WPI_TalonSRX(Wiring.BACK_LEFT_FOLLOW_TALON);
            backLeftFollow.set(ControlMode.Follower, Constants.TIMEOUT);

        frontRightLead.setInverted(true);
        frontLeftLead.setInverted(true);
        backRightFollow.setInverted(true);
        backLeftFollow.setInverted(true);

        leftSide = new SpeedControllerGroup(frontLeftLead, backLeftFollow);
        rightSide = new SpeedControllerGroup(frontRightLead, backRightFollow);

        drive = new DifferentialDrive(leftSide, rightSide);

        shift1 = new Solenoid(Wiring.SHIFT_1);
        shift2 = new Solenoid(Wiring.SHIFT_2);
    }

    public void setGear1() 
    {
        currentGear = 1;
		shift1.set(false);
		shift2.set(true);
    }

    public void setGear2() 
    {
        currentGear = 2;
        shift1.set(true);
		shift2.set(false);
    }

    public int getCurrentGear()
    {
        return currentGear;
    }

    public void arcadeDrive(double xSpeed, double zRotation)
    {
        drive.arcadeDrive(xSpeed, zRotation);
    }
}