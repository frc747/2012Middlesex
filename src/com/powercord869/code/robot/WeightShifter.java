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
public class WeightShifter implements RobotFunction {
    private Victor batteryMotor;
    private DigitalInput batteryLimitFwd, batteryLimitBck;
    
    public WeightShifter(int pwm, int limF, int limB) {
        //setup battery speed controller
        batteryMotor = new Victor(1,pwm);
        //battery limits
        batteryLimitFwd = new DigitalInput(1,limF);
        batteryLimitBck = new DigitalInput(1,limB);
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    public boolean move(double value, double percent) {
        double val = value*percent;
        //tell the driver the current drive percentage
        LCD.print(5, val+" value");
        //take joystick inputs and drive the robot
        return move(val);
    }
    
    //set the batery speed controller with the given value if we can move it in that direction
    public boolean move(double value) {
        if(value>0) {
            if(batteryLimitFwd.get()) {
                batteryMotor.set(0);
                return false;
            }
        } else if(value<0) {
            if(!batteryLimitBck.get()) {
                batteryMotor.set(0);
                return false;
            }
        }
        batteryMotor.set(value);
        return true;
    }
    
    public void stop() {
        batteryMotor.set(0);
    }
}
