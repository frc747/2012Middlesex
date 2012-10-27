/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot.components;

import com.powercord869.code.robot.LCD;
import com.powercord869.code.robot.RobotControlable;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author programing
 */
public class Drive extends RobotControlable {
    //create a singleton of this class
    private static final Drive instance = new Drive();
    //drive speed controllers
    private Victor frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    private RobotControls controls;
    //drive control also controls the lift
    private Lift lift;
    
    private Drive() {
        controls = RobotControls.getInstance();
        lift = Lift.getInstance();
        //setup drive speed controllers
        frontLeftMotor = new Victor(1,frontLeftMotorPWM);
        rearLeftMotor = new Victor(1,rearLeftMotorPWM);
        frontRightMotor = new Victor(1,frontRightMotorPWM);
        rearRightMotor = new Victor(1,rearRightMotorPWM);
    }
    
    /**
     * get the drive function of the robot
     * @return 
     */
    public static Drive getInstance() {
        return instance;
    }
    
    /**
     * drive the robot via operator control
     */
    public void control() {
        if (controls.rightStick().getRawButton(1) == true && controls.leftStick().getRawButton(1) == true) {
            controlTankDrive(.75);
            lift.down();
        } else if (controls.rightStick().getRawButton(1) == true || controls.leftStick().getRawButton(1) == true) {
            controlTankDrive(.50);
            lift.down();
        } else {
            controlTankDrive(1);
            if(controls.rightStick().getRawButton(2)) {
                lift.down();
            } else {
                lift.up();
            }
        }
    }

    private void controlTankDrive(double percent) {
        tankDrive(controls.leftStick().getY(),controls.rightStick().getY(),percent);
    }
    
    /**
     * drive the robot like a tank but scaled by a percent
     * @param left speed -1 - 1
     * @param right speed -1 - 1
     * @param percent scale 0 - 1
     */
    public void tankDrive(double left, double right, double percent) {
        //tell the driver the current drive percentage
        LCD.print(4, percent*100+"% speed");
        //take values and drive the robot
        double percentRight = (right*percent);
        double percentLeft = (left*percent);
        tankDrive(percentLeft,percentRight);
    }
    
    /**
     * drive the robot like a tank
     * @param left speed -1 - 1
     * @param right speed -1 - 1
     */
    public void tankDrive(double left, double right) {
        frontLeftMotor.set(-left);
        rearLeftMotor.set(-left);
        frontRightMotor.set(right);
        rearRightMotor.set(right);
    }
    
    /**
     * stop the entire drive train
     */
    public void stop() {
        frontLeftMotor.set(0);
        rearLeftMotor.set(0);
        frontRightMotor.set(0);
        rearRightMotor.set(0);
    }
}
