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
    private double speed;
    private DigitalInput liftLimitFrontUp, liftLimitFrontDown, liftLimitBackUp, liftLimitBackDown;
    private Jaguar liftMotorFront, liftMotorBack;
    
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
        setFront(0);
        setBack(0);
    }
    
    public void setFront(double thisSpeed) {
        liftMotorFront.set(thisSpeed);
    }
    
    public void setBack(double thisSpeed) {
        liftMotorBack.set(thisSpeed);
    }
    
    public void down() {
        if(liftLimitFrontDown.get()) {
            setFront(-speed);
        } else {
            setFront(0);
        }
        
        if(liftLimitBackDown.get()) {
            setBack(-speed);
        } else {
            setBack(0);
        }
    }
    
    public void up() {
        if(liftLimitFrontUp.get()) {
            setFront(speed);
        } else {
            setFront(0);
        }
        
        if(liftLimitBackUp.get()) {
            setBack(speed);
        } else {
            setBack(0);
        }
    }
}
