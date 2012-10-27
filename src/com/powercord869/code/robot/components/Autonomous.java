package com.powercord869.code.robot.components;

import com.powercord869.code.robot.LCD;
import com.powercord869.code.robot.RobotFunction;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import java.util.Vector;

/**
 *
 * @author programing
 */
public class Autonomous extends RobotFunction {
    //create a singleton of this class
    private static final Autonomous instance = new Autonomous();
    //positions
    private final int STOP = 0;
    private final int CENTER = 1;
    private final int LEFT = 2;
    private final int RIGHT = 3;
    private final int SUPERLEFT = 4;
    private final int FULLHUMP = 5;
    private final int ALLIANCE = 6;
    //LCD tag
    private final String tag = "auto";
    //encoder calculations
    private final int ticks = 250;
    private final double circumference = Math.PI*6;
    private final double ratio = 26.0/12.0;
    private final double robotCircumference = Math.PI*24;
    //speed
    private final double full = 1;
    private final double slower = .95;
    private final double wait = 4;
    private final int turnDegree = 89;
    
    private Encoder left,right;
    private DriverStation ds;
    private Drive drive;
    private Fin fin;
    private Lift lift;
    private WeightShifter weight;
    private Vector unused;
    private double avg;
    private int stage, mode;
    private int desiredCount, leftCount, rightCount;
    private Timer stopwatch;
    
    private Autonomous() {
        stopwatch = new Timer();
        left = new Encoder(1,leftADIO,1,leftBDIO,false);
        right = new Encoder(1,rightADIO,1,rightBDIO,true);
        unused = new Vector();
        ds = DriverStation.getInstance();
        drive = Drive.getInstance();
        fin = Fin.getInstance();
        lift = Lift.getInstance();
        weight = WeightShifter.getInstance();
        unused.addElement(weight);
        stage = 0;
    }
    
    /**
     * get the autonomous mode so we are not creating multiple encoders
     * @return Autonomous singleton
     */
    public static Autonomous getInstance() {
        return instance;
    }
    
    /**
     * start the encoders
     */
    public void start() {
        left.start();
        right.start();
    }
    
    /**
     * reset the autonomous routine (for testing purposes mostly)
     */
    public void reset() {
        left.reset();
        right.reset();
        stage = 0;
    }
    
    /**
     * print values to the driver station lcd
     */
    public void encoderVals() {
        LCD.print(2,"l:"+left.get()+" "+"r:"+right.get()+"w:"+desiredCount);
        LCD.print(6,"m:"+mode+"s:"+stage);
    }

    /**
     * As a robot function we need to be able to all robot functionality we are working with
     */
    public void stop() {
        LCD.print(3,tag+" stop");
        drive.stop();
        fin.stop();
        lift.stop();
        left.reset();
        right.reset();
        stage = STOP;
    }
    
    /**
     * main autonomous function
     */
    public void auto() {
        double centerFwd = ds.getAnalogIn(1)*100;
        double lRfwd = ds.getAnalogIn(2)*100;
        double atCenter = ds.getAnalogIn(3)*100;
        double atAlliance = ds.getAnalogIn(4)*100;
        if(ds.getDigitalIn(1)) {
            mode = CENTER;
        } else if(ds.getDigitalIn(2)) {
            mode = RIGHT;
        } else if(ds.getDigitalIn(3)) {
            mode = LEFT;
        } else if(ds.getDigitalIn(4)) {
            mode = SUPERLEFT;
        } else if(ds.getDigitalIn(5)) {
            mode = FULLHUMP;
        } else if(ds.getDigitalIn(6)) {
            mode = ALLIANCE;
        } else {
            mode = STOP;
        }
        switch(mode) {
            case CENTER:
                switch(stage) {
                    case 0: //forward, lift down, fin forward
                        stopwatch.start();
                        ++stage;
                        break;
                    case 1: //forward, lift down, fin forward
//                        if(stopwatch.get()>.5) {
//                            ++stage;
//                        }
                        fin.forward();
                        autoFwd(centerFwd);
                        break;
                    case 2:
                        ++stage;
                        break;
                    default://stop
                        stopwatch.stop();
                        fin.stop();
                        drive.stop();
                        break;
                }
                lift.up();
                weight.stop();
                break;
            case LEFT:
            case RIGHT:
                switch(stage) {
                    case 0: //forward
                        autoFwd(lRfwd);
                        break;
                    case 1: //turn left/right depending on mode
                        autoTurn(mode==LEFT,turnDegree);
                        break;
                    case 2: //fin out
                        if(!fin.forward()) {
                            Timer.delay(0.001);
                            if(!fin.forward()) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: //forward to hit the bridge
                        autoFwd(atCenter);
                        break;
                    default://stop
                        drive.stop();
                        fin.stop();
                        break;
                }
                lift.up();
                weight.stop();
                break;
            case SUPERLEFT:
                switch(stage) {
                    case 0: //forward
                        autoFwd(lRfwd);
                        break;
                    case 1: //turn right
                        autoTurn(true,turnDegree);
                        break;
                    case 2: //fin out
                        if(!fin.forward()) {
                            Timer.delay(0.001);
                            if(!fin.forward()) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: // forward
                        autoFwd(atCenter);
                        break;
                    case 4: //wait for balls to come off bridge
                        Timer.delay(wait);
                        ++stage;
                        break;
                    case 5: //move backwards same distance we moved forward before
                        autoFwd(true,atCenter);
                        break;
                    case 6: //turn right
                        autoTurn(true,turnDegree);
                        break;
                    case 7: //turn right
                        autoTurn(true,35);
                        break;
                    case 8: //forward to hit other bridge
                        autoFwd(atAlliance);
                        break;
                    default://stop
                        drive.stop();
                        fin.stop();
                        break;
                }
                lift.up();
                weight.stop();
                break;
            case FULLHUMP:
                switch(stage) {
                    case 0: //forward
                        autoFwd(lRfwd);
                        break;
                    case 1: //turn right
                        autoTurn(true,turnDegree);
                        break;
                    case 2: //fin out
                        if(!fin.forward()) {
                            Timer.delay(0.001);
                            if(!fin.forward()) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: // forward
                        autoFwd(atCenter);
                        break;
                    case 4: //wait for balls to come off bridge
                        Timer.delay(wait);
                        ++stage;
                        break;
                    case 5: //move backwards same distance we moved forward before
                        autoFwd(true,atCenter);
                        break;
                    case 6: //turn right
                        autoTurn(true,turnDegree);
                        break;
                    case 7: //turn right
                        autoTurn(true,38);
                        break;
                    case 8: //forward to hit other bridge
                        autoFwd(atAlliance);
                        break;
                    case 9: //wait for balls to come off bridge
                        Timer.delay(wait);
                        ++stage;
                        break;
                    case 10: //move backwards same distance we moved forward before
                        autoFwd(true,atAlliance);
                        break;
                    case 11: //fin out
                        if(!fin.backward()) {
                            Timer.delay(0.01);
                            if(!fin.backward()) {
                                ++stage;
                            }
                        }
                        break;
                    case 12: //spin 90 right
                        autoTurn(true,55);
                        break;
                    default://stop
                        drive.stop();
                        fin.stop();
                        break;
                }
                lift.up();
                weight.stop();
                break;
            case ALLIANCE:
                switch(stage) {
                    case 0: //forward
                        autoFwd(lRfwd);
                        break;
                    case 1: //turn left/right depending on mode
                        autoTurn(false,turnDegree);
                        break;
                    case 2: //fin out
                        if(!fin.forward()) {
                            Timer.delay(0.001);
                            if(!fin.forward()) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: //forward to hit the bridge
                        autoFwd(atAlliance);
                        break;
                    default://stop
                        drive.stop();
                        fin.stop();
                        break;
                }
                lift.up();
                weight.stop();
                break;
            case STOP:
            default:
                stop();
                break;
        }
    }
    
    private void autoFwd(double distance) {
        autoFwd(false,distance);
    }
    
    /**
     * drive the robot forward
     * @param distance inches
     */
    private void autoFwd(boolean backward, double distance) {
        desiredCount = calcCount(distance);
        leftCount = left.get();
        rightCount = right.get();
        avg = Math.abs((leftCount+rightCount)/2);
        if(avg < desiredCount) {
            if(leftCount > rightCount+10) {
                LCD.print(3,tag+" right");
                if(backward) {
                    drive.tankDrive(slower, full);
                } else {
                    drive.tankDrive(-slower, -full);
                }
            } else if(leftCount < rightCount-10) {
                LCD.print(3,tag+" left");
                if(backward) {
                    drive.tankDrive(full, slower);
                } else {
                    drive.tankDrive(-full, -slower);
                }
            } else {
                LCD.print(3,tag+" forward");
                if(backward) {
                    drive.tankDrive(full, full);
                } else {
                    drive.tankDrive(-full,-full);
                }
            }
        } else { //avg >= desiredCount
            next();
        }
    }
    
    /**
     * turn the robot given number of degrees right/left
     * @param turnRight if true turn right, else turn left
     * @param degrees degrees to turn
     */
    private void autoTurn(boolean turnRight, int degrees) {
        desiredCount = calcCount(robotCircumference/(360/degrees));
        System.out.println(Double.toString(desiredCount));
        leftCount = left.get();
        rightCount = right.get();
        avg = (Math.abs(leftCount)+Math.abs(rightCount))/2;
        if(avg < desiredCount) {
            if(turnRight) { //turn right
                if(Math.abs(leftCount) > Math.abs(rightCount)+10) {
                    LCD.print(3,tag+" right right");
                    drive.tankDrive(-slower, full);
                } else if(Math.abs(leftCount) < Math.abs(rightCount)-10) {
                    LCD.print(3,tag+" right left");
                    drive.tankDrive(-full, slower);
                } else {
                    LCD.print(3,tag+" turn right");
                    drive.tankDrive(-full,full);
                }
            } else { // turn left
                if(Math.abs(rightCount) > Math.abs(leftCount)+10) {
                    LCD.print(3,tag+" left left");
                    drive.tankDrive(full, -slower);
                } else if(Math.abs(rightCount) < Math.abs(leftCount)-10) {
                    LCD.print(3,tag+" left right");
                    drive.tankDrive(slower, -full);
                } else {
                    LCD.print(3,tag+" turn left");
                    drive.tankDrive(full,-full);
                }
            }
        } else { //avg >= desiredCount, done
            next();
        }
    }
    
    /**
     * calculate the number of encoder clicks we want based on given distance
     * @param distance inches
     * @return encoder tick count
     */
    private int calcCount(double distance) {
        double desiredWheelRev = distance/circumference;
        double desiredEncoderRev = ratio * desiredWheelRev;
        return (int) (desiredEncoderRev * ticks);
    }
    
    /**
     * stop the robot from moving
     * reset the encoder values
     * and move to our next stage
     */
    private void next() {
        drive.stop();
        left.reset();
        right.reset();
        ++stage;
    }
}
