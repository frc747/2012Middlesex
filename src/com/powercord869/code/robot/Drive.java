/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author programing
 */
public class Drive implements RobotFunction {
    private Victor frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    
    public Drive(int fl, int rl, int fr, int rr) {
        //setup drive speed controllers
        frontLeftMotor = new Victor(1,fl);
        rearLeftMotor = new Victor(1,rl);
        frontRightMotor = new Victor(1,fr);
        rearRightMotor = new Victor(1,rr);
    }
    
    public void tankDrive(double left, double right, double percent) {
        //tell the driver the current drive percentage
        LCD.print(4, percent*100+"% speed");
        //take values and drive the robot
        double percentRight = (right*percent);
        double percentLeft = (left*percent);
        tankDrive(percentLeft,percentRight);
    }
    
    public void tankDrive(double left, double right) {
        frontLeftMotor.set(-left);
        rearLeftMotor.set(-left);
        frontRightMotor.set(right);
        rearRightMotor.set(right);
    }
    
    public void stop() {
        frontLeftMotor.set(0);
        rearLeftMotor.set(0);
        frontRightMotor.set(0);
        rearRightMotor.set(0);
    }
}