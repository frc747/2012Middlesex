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
public class Drive {
    
    Victor frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    
    public Drive() {
        //setup drive speed controllers
        frontLeftMotor = new Victor(1,4);
        rearLeftMotor = new Victor(1,5);
        frontRightMotor = new Victor(1,1);
        rearRightMotor = new Victor(1,2);
    }
    
    public void percentTankDrive(double left, double right, int percent) {
        //tell the driver the current drive percentage
        LCD.print(4, percent+"% speed");
        //take values and drive the robot
        double percentRight = ((-right)*percent/100);
        double percentLeft = (left*percent/100);
        tankDrive(percentLeft,percentRight);
    }
    
    public void tankDrive(double left, double right) {
        frontLeftMotor.set(left);
        rearLeftMotor.set(left);
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
