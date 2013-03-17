/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

public class RobotMain extends IterativeRobot {
    public void robotInit() {
        
    }
    public void disabledInit() {
        
    }
    public void autonomousInit() {
        
    }
    public void teleopInit() {
        
    }
    public void testInit() {
        
    }
    public void disabledPeriodic() {
        LCD.print("Disabled: "+this.getStopwatchTime()+" sec");
    }
    public void autonomousPeriodic() {
        LCD.print("Auto: "+this.getStopwatchTime()+" sec");
    }
    public void teleopPeriodic() {
        LCD.print("Teleop: "+this.getStopwatchTime()+" sec");
    }
    public void testPeriodic() {
        LCD.print("Test: "+this.getStopwatchTime()+" sec");
    }
}
