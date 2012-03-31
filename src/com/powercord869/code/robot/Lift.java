/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;

/**
 *
 * @author programing
 */
public class Lift implements RobotFunction {
    private final double speed;
    private DigitalInput liftLimitFrontUp, liftLimitFrontDown, liftLimitBackUp, liftLimitBackDown;
    private Jaguar liftMotorFront, liftMotorBack;
    private boolean front, back;
    
    public Lift(double speed, int front, int back, int limFU, int limFD, int limBU, int limBD) {
        this.speed = speed;
        //lift limits
        liftLimitFrontUp = new DigitalInput(1,limFU);
        liftLimitFrontDown = new DigitalInput(1,limFD);
        liftLimitBackUp = new DigitalInput(1,limBU);
        liftLimitBackDown = new DigitalInput(1,limBD);
        
        //setup lift speed controllers
        liftMotorFront = new Jaguar(1,front);
        liftMotorBack = new Jaguar(1,back);
    }
    
    public void stop() {
        liftMotorFront.set(0);
        liftMotorFront.set(0);
    }
    
    public int down() {
        if(liftLimitFrontDown.get()) {
            liftMotorFront.set(-speed);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitBackDown.get()) {
            liftMotorBack.set(-speed);
            back = true;
        } else {
            liftMotorBack.set(0);
        }
        
        if(liftLimitBackDown.get()&&liftLimitFrontDown.get()) {
            return 3;
        } else if(liftLimitFrontDown.get()) {
            return 2;
        } else if(liftLimitBackDown.get()) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public int up() {
        if(liftLimitFrontUp.get()) {
            liftMotorFront.set(speed);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitBackUp.get()) {
            liftMotorBack.set(speed);
        } else {
            liftMotorBack.set(0);
        }
        
        if(liftLimitBackUp.get()&&liftLimitFrontUp.get()) {
            return 3;
        } else if(liftLimitFrontUp.get()) {
            return 2;
        } else if(liftLimitBackUp.get()) {
            return 1;
        } else {
            return 0;
        }
    }
}
