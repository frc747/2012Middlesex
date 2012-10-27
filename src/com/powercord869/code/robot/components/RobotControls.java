package com.powercord869.code.robot.components;

import edu.wpi.first.wpilibj.Joystick;

/**
 * 
 * @author programing
 */
public class RobotControls {
    //create a singleton of this class
    private static final RobotControls instance = new RobotControls();
    
    private static Joystick leftStick, rightStick, operatorStick;
    
    private RobotControls() {
        rightStick = new Joystick(1);
        leftStick = new Joystick(2);
        operatorStick = new Joystick(3);
    }
    
    /**
     * get the user controls so we do not create multiple hardware references
     * @return the user controls
     */
    public static RobotControls getInstance() {
        return instance;
    }
    /**
     * get the left drive stick
     * @return left drive stick
     */
    public Joystick leftStick() {
        return leftStick;
    }
    
    /**
     * get the right drive stick
     * @return right drive stick
     */
    public Joystick rightStick() {
        return rightStick;
    }
    
    /**
     * get the operator stick
     * @return operator stick
     */
    public Joystick operatorStick() {
        return operatorStick;
    }
}
