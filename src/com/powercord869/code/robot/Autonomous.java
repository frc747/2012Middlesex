package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

public class Autonomous {
    public static final int CENTER = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    
    private final String tag = "auto";
    private final int ticks = 250;
    private Encoder left,right;
    private DriverStation ds;
    private Drive drive;
    private Fin fin;
    private double circumference;
    private double ratio;
    private int stage;
    
    public Autonomous(int leftA, int leftB, int rightA, int rightB, double diameter, double driveRatio) {
        circumference = Math.PI * diameter;
        ratio = driveRatio;
        left = new Encoder(1,leftA,1,leftB,false);
        right = new Encoder(1,rightA,1,rightB,true);
        ds = RobotMain.ds;
        drive = RobotMain.drive;
        fin = RobotMain.fin;
        stage = 0;
    }
    
    public void auto() {
        double distance1 = ds.getAnalogIn(1)*10;
        double distance2 = ds.getAnalogIn(2)*10;
        int mode = (ds.getDigitalIn(1)?1:0)+(ds.getDigitalIn(2)?1:0);
        switch(mode) {
            case CENTER:
                switch(stage) {
                    case 0:
                        autoFwd(distance1*.75);
                        fin.stop();
                        break;
                    case 1:
                        fin.forward();
                        autoFwd(distance1*.25);
                        break;
                    default:
                        drive.stop();
                        break;
                }
                break;
            case LEFT:
                switch(stage) {
                    case 0:
                        autoFwd(distance1);
                        fin.stop();
                        break;
                    case 1:
                        autoTurn90(true);
                        fin.stop();
                        break;
                    case 2:
                        fin.forward();
                        autoFwd(distance2);
                        break;
                    default:
                        drive.stop();
                        break;
                }
                break;
            case RIGHT:
                switch(stage) {
                    case 0:
                        autoFwd(distance1);
                        fin.stop();
                        break;
                    case 1:
                        autoTurn90(false);
                        fin.stop();
                        break;
                    case 2:
                        fin.forward();
                        autoFwd(distance2);
                        break;
                    default:
                        drive.stop();
                        break;
                }
                break;
            default:
                drive.stop();
                fin.stop();
                break;
        }
    }
    
    public void autoFwd(double distance) {
        int desiredCount = calcCount(distance);
        int leftCount = left.get();
        int rightCount = right.get();
        double avg = (leftCount+rightCount)/2;
        if(avg < desiredCount) {
            if(leftCount > rightCount) {
                LCD.print(3,tag+" right");
                drive.tankDrive(0, .5);
            } else if(leftCount < rightCount) {
                LCD.print(3,tag+" left");
                drive.tankDrive(.5, 0);
            } else {
                LCD.print(3,tag+" forward");
                drive.tankDrive(.5,.5);
            }
        } else {
            LCD.print(3,tag+" complete");
            drive.stop();
            ++stage;
        }
    }
    
    public void autoTurn90(boolean turnRight) {
        int desiredCount = calcCount(ds.getAnalogIn(3)*10);
        int leftCount = left.get();
        int rightCount = right.get();
        if(turnRight && leftCount < desiredCount) {
            if(leftCount > -rightCount) {
                LCD.print(3,tag+" right");
                drive.tankDrive(0, -.5);
            } else if(leftCount < -rightCount) {
                LCD.print(3,tag+" left");
                drive.tankDrive(.5, 0);
            } else {
                LCD.print(3,tag+" turn right");
                drive.tankDrive(.5,-.5);
            }
        } else if(rightCount < desiredCount){
            if(rightCount > -leftCount) {
                LCD.print(3,tag+" left");
                drive.tankDrive(-.5, 0);
            } else if(rightCount < -leftCount) {
                LCD.print(3,tag+" right");
                drive.tankDrive(0, .5);
            } else {
                LCD.print(3,tag+" turn left");
                drive.tankDrive(-.5,.5);
            }
        } else {
            LCD.print(3,tag+" complete");
            drive.stop();
            ++stage;
        }
    }
    
    private int calcCount(double distance) {
        double desiredWheelRev = distance/circumference;
        double desiredEncoderRev = ratio * desiredWheelRev;
        return (int) (desiredEncoderRev * ticks);
    }
    
    public void start() {
        left.start();
        right.start();
    }
    
    public void reset() {
        left.reset();
        right.reset();
    }
    
    public String encoderVals() {
        return "l: "+left.get()+" "+" r: "+right.get();
    }
}
