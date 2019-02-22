package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.Joystick;

public class OperatorInterface
{
    public Joystick pilot, copilot;

    public OperatorInterface()
    {
        pilot = new Joystick(Wiring.PILOT_JOYSTICK);
        copilot = new Joystick(Wiring.COPILOT_JOYSTICK);
    }

    public double getPilotY()
    {
        if((Math.abs(pilot.getY())/pilot.getY()) == 1)
        {
            return -1*(Math.pow(pilot.getY(), 2));
        }
        return (Math.pow(pilot.getY(), 2));
    }

    public double getPilotZ()
    {
        return pilot.getZ();
    }
}