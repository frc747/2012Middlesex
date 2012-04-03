package com.powercord869.code.robot.singletons;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class RobotDriverStation {
    //create a singleton of this class
    private static final RobotDriverStation instance = new RobotDriverStation();
    
    private DriverStation ds;
    private static Joystick leftStick, rightStick, operatorStick;
    
    private RobotDriverStation() {
        ds = DriverStation.getInstance();
        rightStick = new Joystick(1);
        leftStick = new Joystick(2);
        operatorStick = new Joystick(3);
    }
    
    /**
     * get the user controls so we do not create multiple hardware references
     * @return the user controls
     */
    public static RobotDriverStation getInstance() {
        return instance;
    }
    
    /**
     * get the driver station
     * @return driver station
     */
    public DriverStation ds() {
        return ds;
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
