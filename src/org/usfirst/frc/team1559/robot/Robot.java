
package org.usfirst.frc.team1559.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {


	//Called before robot runs. Instantiate your variables here...
    public void robotInit() {

    }

    //Run once before autonomous loop chunk
    public void autonomousInit() {

    }

    //Autonomous loop
    public void autonomousPeriodic() {

    }
    
    //run once before the teleop loop
    public void teleopInit(){
    	
    }
    
    //the teleop loop
    public void teleopPeriodic() {
        
    }
    
    //testing only
    public void testInit(){
    	
    }
    
    //testing
    public void testPeriodic() {
    
    }
    
    //not necessary, but useful.
    public void disabledInit(){
    	
    	/*
    	 * 
    	 * DON'T DO ANYTHING DANGEROUS HERE!
    	 * The 2014 Robot had some issues with transitioning from Disabled to Teleop during matches :(
    	 * Reading sensors and calculating PID values are very bad ideas...
    	 * 
    	 */
    	
    }
    
    public void disabledPeriodic(){
    	
    	/*
    	 * 
    	 * Reserved for bling!
    	 * 
    	 */
    	
    }
    
}
