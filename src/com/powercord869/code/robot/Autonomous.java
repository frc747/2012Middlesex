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
    private Lift lift;
    private double circumference;
    private double ratio;
    private int stage;
    private int desiredCount;
    private final double full = .5;
    private final double slower = .4;
    private int mode;
    private int comp;
    private int compCount;
    
    public Autonomous(int leftA, int leftB, int rightA, int rightB, double diameter, double driveRatio) {
        circumference = Math.PI * diameter;
        ratio = driveRatio;
        left = new Encoder(1,leftA,1,leftB,false);
        right = new Encoder(1,rightA,1,rightB,true);
        ds = RobotMain.ds;
        drive = RobotMain.drive;
        fin = RobotMain.fin;
        lift = RobotMain.lift;
        stage = 0;
        comp = 0;
    }
    
    public void auto() {
        double distance1 = ds.getAnalogIn(1)*100;
        double distance2 = ds.getAnalogIn(2)*100;
        if(ds.getDigitalIn(1)) {
            mode = LEFT;
        } else if (ds.getDigitalIn(2)) {
            mode = RIGHT;
        } else {
            mode = CENTER;
        }
        switch(mode) {
            case CENTER:
                switch(stage) {
                    case 0:
                        fin.forward();
                        lift.down();
                        autoFwd(distance1);
                        break;
                    default:
                        drive.stop();
                        lift.up();
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
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        if(!fin.forward()) {
                            ++stage;
                        }
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        lift.down();
                        ++stage;
                        break;
                    case 23:
                        autoFwd(distance2);
                        lift.down();
                        break;
                    default:
                        drive.stop();
                        lift.up();
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
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        if(!fin.forward()) {
                            ++stage;
                        }
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        if(lift.down()==0) {
                            ++stage;
                        }
                        break;
                    case 23:
                        lift.down();
                        autoFwd(distance2);
                        break;
                    default:
                        drive.stop();
                        lift.up();
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
        desiredCount = calcCount(distance);
        int leftCount = left.get();
        int rightCount = right.get();
        double avg = (leftCount+rightCount)/2;
        if(avg < desiredCount-60) {
            if(leftCount > rightCount+10) {
                LCD.print(3,tag+" right");
                drive.tankDrive(-slower, -full);
            } else if(leftCount < rightCount-10) {
                LCD.print(3,tag+" left");
                drive.tankDrive(-full, -slower);
            } else {
                LCD.print(3,tag+" forward");
                drive.tankDrive(-full,-full);
            }
        } else {
            LCD.print(3,tag+" complete");
            drive.stop();
            ++stage;
            left.reset();
            right.reset();
        }
    }
    
    public void autoTurn90(boolean turnRight) {
        desiredCount = calcCount((Math.PI*27)/4);
        System.out.println(Double.toString(desiredCount));
        int leftCount = left.get();
        int rightCount = right.get();
        double avg = Math.abs(leftCount)+Math.abs(rightCount)/2;
        if(turnRight && avg < desiredCount) {
            if(Math.abs(leftCount) > Math.abs(rightCount)) {
                LCD.print(3,tag+" right right");
                drive.tankDrive(-slower, full);
            } else if(Math.abs(leftCount) < Math.abs(rightCount)) {
                LCD.print(3,tag+" right left");
                drive.tankDrive(-full, slower);
            } else {
                LCD.print(3,tag+" turn right");
                drive.tankDrive(-full,full);
            }
        } else if(avg < desiredCount){
            if(Math.abs(rightCount) < Math.abs(leftCount)) {
                LCD.print(3,tag+" left right");
                drive.tankDrive(slower, -full);
            } else if(Math.abs(rightCount) > Math.abs(leftCount)) {
                LCD.print(3,tag+" left left");
                drive.tankDrive(full, -slower);
            } else {
                LCD.print(3,tag+" turn left");
                drive.tankDrive(full,-full);
            }
        } else {
            LCD.print(3,tag+" complete");
            drive.stop();
            ++stage;
            left.reset();
            right.reset();
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
        stage = 0;
    }
    
    public void encoderVals() {
        LCD.print(2,"l:"+left.get()+" "+"r:"+right.get()+"w:"+desiredCount);
        LCD.print(6,"m:"+mode+"s:"+stage);
    }
}
