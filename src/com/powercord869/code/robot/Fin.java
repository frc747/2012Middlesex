/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author programing
 */
public class Fin {
    private double forwardSpeed, backSpeed;
    private Victor finMotor;
    private DigitalInput finLimitForward;
    private DigitalInput finLimitBack;
    
    public Fin(double fwdSpeed, double bckSpeed) {
        forwardSpeed = fwdSpeed;
        backSpeed = bckSpeed;
        //setup fin speed controller
        finMotor = new Victor(1,6);
        //fin limits
        finLimitForward = new DigitalInput(1,6);
        finLimitBack = new DigitalInput(1,5);
    }
    
    public void forward() {
        if(finLimitForward.get()) {
            finMotor.set(forwardSpeed);
        }
    }
    
    public void backward() {
        if(finLimitBack.get()) {
            finMotor.set(backSpeed);
        }
    }
    
    public void stop() {
        finMotor.set(0);
    }
}
