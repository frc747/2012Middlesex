package com.powercord869.code.robot.components;

import com.powercord869.code.robot.RobotFunction;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Jaguar;
/**
 * 
 * @author programing
 */
public class Lift extends RobotFunction {
    //create a singleton of this class
    private static final Lift instance = new Lift();
    public static final double speed = 1;
    
    private DigitalInput liftLimitFrontUp, liftLimitFrontDown, liftLimitBackUp, liftLimitBackDown;
    private Jaguar liftMotorFront, liftMotorBack;
    
    private Lift() {
        //setup lift speed controllers
        liftMotorFront = new Jaguar(1,liftMotorFrontPWM);
        liftMotorBack = new Jaguar(1,liftMotorBackPWM);
        
        //lift limits
        liftLimitFrontUp = new DigitalInput(1,liftLimitFrontUpDIO);
        liftLimitFrontDown = new DigitalInput(1,liftLimitFrontDownDIO);
        liftLimitBackUp = new DigitalInput(1,liftLimitBackUpDIO);
        liftLimitBackDown = new DigitalInput(1,liftLimitBackDownDIO);
    }
    
    /**
     * get the lift singleton so we do not allocate hardware twice
     * @return 
     */
    public static Lift getInstance() {
        return instance;
    }
    
    /**
     * stop the lift from moving
     */
    public void stop() {
        liftMotorFront.set(0);
        liftMotorBack.set(0);
    }
    
    /**
     * move the lift down to put all 8 wheels on the ground
     * @return b11 if both front and back are moving, b10 if front moving, b01 if back moving, else b00
     */
    public int down() {
        if(liftLimitFrontDown.get()) {
            liftMotorFront.set(-speed);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitBackDown.get()) {
            liftMotorBack.set(-speed);
        } else {
            liftMotorBack.set(0);
        }
        
        if(liftLimitBackDown.get()&&liftLimitFrontDown.get()) {
            return 3; // b11
        } else if(liftLimitFrontDown.get()) {
            return 2; // b10
        } else if(liftLimitBackDown.get()) {
            return 1; // b01
        } else {
            return 0; // b00
        }
    }
    /**
     * move the lift up to put only inner 4 wheels on the ground
     * @return b11 if both front and back are moving, b10 if front moving, b01 if back moving, else b00
     */
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
            return 3; // b11
        } else if(liftLimitFrontUp.get()) {
            return 2; // b10
        } else if(liftLimitBackUp.get()) {
            return 1; // b01
        } else {
            return 0; // b00
        }
    }
}
