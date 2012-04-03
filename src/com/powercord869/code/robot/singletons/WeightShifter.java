/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot.singletons;

import com.powercord869.code.robot.LCD;
import com.powercord869.code.robot.RobotControlable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author programing
 */
public class WeightShifter extends RobotControlable {
    //create a singleton of this class
    private static final WeightShifter instance = new WeightShifter();
    public static final double speed = .5;
    
    private RobotDriverStation ds;
    private Victor weightShifterMotor;
    private DigitalInput weightShifterLimitFwd, weightShifterLimitBck;
    
    private WeightShifter() {
        ds = RobotDriverStation.getInstance();
        //setup battery speed controller
        weightShifterMotor = new Victor(1,weightMotorPWM);
        //battery limits
        weightShifterLimitFwd = new DigitalInput(1,weightLimitFwdDIO);
        weightShifterLimitBck = new DigitalInput(1,weightLimitBckDIO);
    }
    
    /**
     * get the weight shifter so we do not create multiple instances of hardware
     * @return weight shifter
     */
    public static WeightShifter getInstance() {
        return instance;
    }
    
    /**
     * allow the drivers to control the weight shifter
     * operator stick trigger pulled makes the weight shifter move slower
     * the operator stick y axis controls the weight shifter movement forward and backward
     */
    public void control() {
        if(ds.operatorStick().getRawButton(1)) {
            controlMove(speed);
        } else {
            controlMove(1);
        }
    }
    
    /**
     * allow the drivers to control the weight shifter with the y axis
     * @param percent to multiply the y axis value by
     * @return if the weight shifter is moving or not
     */
    private boolean controlMove(double percent) {
        return move(ds.operatorStick().getY(),percent);
    }
    
    /**
     * set our drive motor bounds by percent uniformly and drive the robot
     * @param value speed -1 - 1
     * @param percent scale 0 - 1
     * @return true moving, false limit
     */
    public boolean move(double value, double percent) {
        double val = value*percent;
        //tell the driver the current drive percentage
        LCD.print(5, val+" value");
        //take joystick inputs and drive the robot
        return move(val);
    }
    
    /**
     * set the batery speed controller with the given value if we can move it in that direction
     * @param value speed -1 - 1
     * @return true moving, false limit
     */
    public boolean move(double value) {
        if(value>0) {
            if(weightShifterLimitFwd.get()) {
                weightShifterMotor.set(0);
                return false;
            }
        } else if(value<0) {
            if(!weightShifterLimitBck.get()) {
                weightShifterMotor.set(0);
                return false;
            }
        }
        weightShifterMotor.set(value);
        return true;
    }
    
    /**
     * stop the weight shifter
     */
    public void stop() {
        weightShifterMotor.set(0);
    }
}
