/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.Encoder;

/**
 *
 * @author mechinn
 */
public class AutoDrive extends Drive {
    private Encoder left, right;
    private DriverStationEnhancedIO dsIO;
    private Fin fin;
    private double driveRatio, circumference, desiredWheelRev, desiredEncoderRev, desiredCount;
    private int ticks, distance;
    private String sig = "auto";
    
    public AutoDrive(int fl, int rl, int fr, int rr, int rightA, int rightB, int leftA, int leftB, double diameter, double getRatio, int getTicks) {
        super(fl, rl, fr, rr);
        dsIO = RobotMain.dsIO;
        fin = RobotMain.fin;
        left = new Encoder(1,leftA,1,leftB,true);
        right = new Encoder(1,rightA,1,rightB,false);
        driveRatio = getRatio;
        ticks = getTicks;
        circumference = Math.PI * diameter;
        //use the default value so we have our values all set just in case
        distance = 10;
        calcCount();
    }
    
    private void calcCount() {
        desiredWheelRev = distance/circumference;
        desiredEncoderRev = driveRatio * desiredWheelRev;
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
        //takes the "encoder" value from the i/o panel of the driver station
        //and will use that as our distance, we should be able to preset this
        //while setting up the driver station on the field
        try {
            distance = dsIO.getEncoder(1);
        } catch (DriverStationEnhancedIO.EnhancedIOException ex) {
            System.out.println(ex.toString());
            //fallback
            distance = 10;
        }
        calcCount();
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
