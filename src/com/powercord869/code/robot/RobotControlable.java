package com.powercord869.code.robot;
/**
 * All controllable functions of the robot need this function
 * @author programing
 */
public abstract class RobotControlable extends RobotFunction {
    /**
     * By calling this method the user controls the robot function in the given way
     */
    public abstract void control();
}
