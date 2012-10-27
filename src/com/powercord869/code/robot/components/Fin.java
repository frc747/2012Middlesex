package com.powercord869.code.robot.components;

import com.powercord869.code.robot.RobotControlable;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;

/**
 * Fin function of the robot, used in 2012 to allow the robot to push the bridge down
 * @author programing
 */
public class Fin extends RobotControlable {
    //create a singleton of this class
    private static final Fin instance = new Fin();
    //speeds for speed controller
    public static final double forwardSpeed = .5;
    public static final double backSpeed = -.4;
    
    private RobotControls controls;
    private Victor motor;
    private DigitalInput limitForward, limitBack;
    
    private Fin() {
        controls = RobotControls.getInstance();
        //setup fin speed controller
        motor = new Victor(1,finMotorPWM);
        //fin limits
        limitForward = new DigitalInput(1,finLimitForwardDIO);
        limitBack = new DigitalInput(1,finLimitBackDIO);
    }
    
    /**
     * Get the fin so we do not allocate hardware more than once
     * @return fin functionality
     */
    public static Fin getInstance() {
        return instance;
    }
    
    /**
     * driver control the fin
     * joystick 3 is forward
     * joystick 2 is backward
     * else stop the fin
     */
    public void control() {
        if (controls.operatorStick().getRawButton(3)){
            forward();
        } else if (controls.operatorStick().getRawButton(2)) {
            backward();
        } else {
            stop();
        }
    }
    
    /**
     * make the fin go forward
     * @return true if able to move, false if at limit
     */
    public boolean forward() {
        if(!limitForward.get()) {
            motor.set(forwardSpeed);
            return true;
        } else {
            stop();
            return false;
        }
    }
    
    /**
     * make the fin go backward
     * @return true if able to move, false if at limit
     */
    public boolean backward() {
        if(!limitBack.get()) {
            motor.set(backSpeed);
            return true;
        } else {
            stop();
            return false;
        }
    }
    
    /**
     * stop the motor
     */
    public void stop() {
        motor.set(0);
    }
}
