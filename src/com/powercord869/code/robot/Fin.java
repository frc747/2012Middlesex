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
    private Victor motor;
    private DigitalInput limitForward, limitBack;
    
    public Fin(double fwdSpeed, double bckSpeed, int pwm, int limF, int limB) {
        forwardSpeed = fwdSpeed;
        backSpeed = bckSpeed;
        //setup fin speed controller
        motor = new Victor(1,pwm);
        //fin limits
        limitForward = new DigitalInput(1,limF);
        limitBack = new DigitalInput(1,limB);
    }
    
    public void forward() {
        if(limitForward.get()) {
            motor.set(forwardSpeed);
        }
    }
    
    public void backward() {
        if(limitBack.get()) {
            motor.set(backSpeed);
        }
    }
    
    public void stop() {
        motor.set(0);
    }
}
