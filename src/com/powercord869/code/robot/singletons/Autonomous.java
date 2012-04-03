package com.powercord869.code.robot.singletons;

import com.powercord869.code.robot.LCD;
import com.powercord869.code.robot.RobotFunction;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import java.util.Vector;

public class Autonomous extends RobotFunction {
    //create a singleton of this class
    private static final Autonomous instance = new Autonomous();
    //positions
    private final int CENTER = 0;
    private final int LEFT = 1;
    private final int RIGHT = 2;
    private final int SUPERLEFT = 3;
    private final int STOP = 4;
    //LCD tag
    private final String tag = "auto";
    //encoder calculations
    private final int ticks = 250;
    private final double circumference = Math.PI*6;
    private final double ratio = 26.0/12.0;
    private final double robotCircumference = Math.PI*27;
    //speed
    private final double full = .5;
    private final double slower = .4;
    
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
    
    private Autonomous() {
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
    }
    
    /**
     * main autonomous function
     */
    public void auto() {
        double distance1 = ds.getAnalogIn(1)*100;
        double distance2 = ds.getAnalogIn(2)*100;
        if(ds.getDigitalIn(3)) {
            mode = CENTER;
        } else if(ds.getDigitalIn(2)) {
            mode = RIGHT;
        } else if(ds.getDigitalIn(1)) {
            mode = LEFT;
        } else if(ds.getDigitalIn(4)) {
            mode = SUPERLEFT;
        } else {
            mode = STOP;
        }
        switch(mode) {
            case CENTER:
                switch(stage) {
                    case 0: //forward, lift down, fin forward
                        fin.forward();
                        lift.down();
                        autoFwd(distance1);
                        break;
                    default://stop
                        lift.up();
                        drive.stop();
                        break;
                }
                break;
            case LEFT:
            case RIGHT:
                switch(stage) {
                    case 0: //forward
                        autoFwd(distance1);
                        fin.stop();
                        lift.up();
                        break;
                    case 1: //turn left/right depending on mode
                        autoTurn(mode==LEFT,90);
                        fin.stop();
                        lift.up();
                        break;
                    case 2: //fin out, lift down
                        if(!fin.forward() && lift.down()==0) {
                            Timer.delay(0.001);
                            if(!fin.forward() && lift.down()==0) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: //forward to hit the bridge
                        lift.down();
                        autoFwd(distance2);
                        break;
                    default://stop
                        drive.stop();
                        lift.up();
                        break;
                }
                break;
            case SUPERLEFT:
                switch(stage) {
                    case 0: //forward
                        autoFwd(distance1);
                        fin.stop();
                        lift.up();
                        break;
                    case 1: //turn right
                        autoTurn(true,90);
                        fin.stop();
                        lift.up();
                        break;
                    case 2: //fin out, lift down
                        if(!fin.forward() && lift.down()==0) {
                            Timer.delay(0.001);
                            if(!fin.forward() && lift.down()==0) {
                                ++stage;
                            }
                        }
                        break;
                    case 3: // forward
                        lift.down();
                        autoFwd(distance2);
                        break;
                    case 4: //wait for balls to come off bridge
                        Timer.delay(1);
                        ++stage;
                        break;
                    case 5: //move backwards same distance we moved forward before
                        lift.down();
                        autoFwd(-distance2);
                        break;
                    case 6: //fin back in and lift up to spin
                        if(!fin.backward() && lift.up()==0) {
                            Timer.delay(0.001);
                            if(!fin.backward() && lift.up()==0) {
                                ++stage;
                            }
                        }
                        break;
                    case 7: //spin 180 right
                        autoTurn(true,180);
                        fin.stop();
                        lift.up();
                        break;
                    case 8: //fin out, lift down
                        if(!fin.forward() && lift.down()==0) {
                            Timer.delay(0.001);
                            if(!fin.forward() && lift.down()==0) {
                                ++stage;
                            }
                        }
                        break;
                    case 9: //forward to hit other bridge
                        lift.down();
                        autoFwd(distance2);
                        break;
                    default://stop
                        drive.stop();
                        lift.up();
                        break;
                }
                break;
            default:
                stop();
                break;
        }
        stopOtherFunctions();
    }
    
    /**
     * drive the robot forward
     * @param distance inches
     */
    private void autoFwd(double distance) {
        desiredCount = calcCount(distance);
        leftCount = left.get();
        rightCount = right.get();
        avg = (leftCount+rightCount)/2;
        if(avg < desiredCount) {
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
    
    /**
     * stop our unused robot functions
     */
    private void stopOtherFunctions() {
        for(int i = 0; i<unused.size();++i) {
            ((RobotFunction)unused.elementAt(i)).stop();
        }
    }
}
