/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.KinectStick;
import edu.wpi.first.wpilibj.Skeleton;

/**
 *
 * @author mechinn
 */
public class FRCKinect {
    public static final int headRight = 1;
    public static final int headLeft = 2;
    public static final int rightLegRight = 3;
    public static final int leftLegLeft = 4;
    public static final int rightLegForward = 5;
    public static final int rightLegBack = 6;
    public static final int leftLegForward = 7;
    public static final int leftLegBack = 8;
    public static final int enabled = 9;
    
    private KinectStick leftArm, rightArm;
    public FRCKinect() {
        // Define kinect "joysticks" aka the left and right arm of the persons body
        leftArm = new KinectStick(1);
        rightArm = new KinectStick(2);
    }
    
    public double getLeftY() {
        return leftArm.getY();
    }
    
    public double getRightY() {
        return rightArm.getY();
    }
    /**
     * Get other body part's movement as buttons
     * @param part one of the static FRCKinect ints
     * @return 
     */
    public boolean getBody(int part) {
        return leftArm.getRawButton(part);
    }
    /**
     * 
     * @return 
     */
    public boolean getEnabled() {
        return leftArm.getRawButton(enabled);
    }
}
