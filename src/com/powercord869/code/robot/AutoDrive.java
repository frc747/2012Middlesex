/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.Encoder;

/**
 *
 * @author mechinn
 */
public class AutoDrive extends Drive {
    private Encoder left, right;
    private Fin fin;
    private double desiredCount;
    private String sig = "auto";
    
    public AutoDrive(int fl, int rl, int fr, int rr, int rightA, int rightB, int leftA, int leftB, double diameter, double driveRatio, double distance, int ticks) {
        super(fl, rl, fr, rr);
        fin = RobotMain.fin;
        left = new Encoder(1,leftA,1,leftB,true);
        right = new Encoder(1,rightA,1,rightB,false);
        double circumference = Math.PI * diameter;
        double desiredWheelRev = distance/circumference;
        double desiredEncoderRev = driveRatio * desiredWheelRev;
        desiredCount = desiredEncoderRev * ticks;
    }
    
    public void start() {
        left.start();
        right.start();
    }
    
    public void reset() {
        left.reset();
        right.reset();
    }
    
    public void auto() {
        int leftCount = left.get();
        int rightCount = right.get();
        double avg = (leftCount+rightCount)/2;
        if(avg < desiredCount) {
            //simple fin code once we are 75% there
//            if(avg > desiredCount*.75) {
//                fin.forward();
//            }
            if(leftCount > rightCount) {
                LCD.print(sig+" right");
                this.tankDrive(0, .5);
            } else if(leftCount < rightCount) {
                LCD.print(sig+" left");
                this.tankDrive(.5, 0);
            } else {
                LCD.print(sig+" forward");
                this.tankDrive(.5,.5);
            }
        } else {
            LCD.print(sig+" complete");
            this.stop();
        }
    }
}
