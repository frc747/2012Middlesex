/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code;

import edu.wpi.first.wpilibj.Timer;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author programing
 */
public class Balance {
//    int smoothing, loop;
//    double smoothed, lastUpdate, balanceVal, precision;
//    boolean calibrating;
//    Hashtable rate;
//    double gryoOffset;
    
    //NO CONFIDENCE IN THIS CODE RIGHT NOW SO COMMENTING OUT
        //auto balance code operator and driver must agree to go?!
        //Bunca should we do this? or just have the operator/driver control it
//        if(operatorStick.getRawButton(10) && rightStick.getRawButton(10)){
//            if(balancing==BALANCED) {
//                balancing = STARTUP;
//            } else if(balancing==READY) {
//                balancing = GO;
//            }
//        }
//        
//        autoBalance();
    
//        balancing = BALANCED;
//        
//        //low pass filter stuff
//        smoothed = 0;
//        balanceVal = 0;
//        smoothing = 10;
//        lastUpdate = 0;
//        precision = 1;
//        rate = new Hashtable();
//    //low pass filter for gyro filtering (thank you internet)
//    double lowPassFilter( double newValue ){
//        double now = stopwatch.get();
//        double elapsedTime = now - lastUpdate;
//        smoothed += elapsedTime * ( newValue - smoothed ) / smoothing;
//        lastUpdate = now;
//        return smoothed;
//    }
//    //based on our gyroOffset get our adjusted angle;
//    private double getAdjustedGyroAngle() {
//        return gyro.getAngle()-(gryoOffset*Timer.getFPGATimestamp());
//    }
//    
//    //rough auto balance code... its 1am i should review this lol
//    private void autoBalance() {
//        //initialize values for balancing before we hit the ramp
//        if(balancing==STARTUP) {
//            //smoothed = gyro.getAngle();
//            smoothed = getAdjustedGyroAngle();
//            balancing = READY;
//        } else if(balancing==GO) {
//            // if our gyro is at our balance value we are good
//            lastUpdate = stopwatch.get();
////            double currentAngle = lowPassFilter(gyro.getAngle());
//            double currentAngle = lowPassFilter(getAdjustedGyroAngle());
//            if(currentAngle>balanceVal-precision && currentAngle<balanceVal+precision) {
//                balancing = BALANCED;
//            } else {
//                //if we are above our balanced value
//                if(smoothed>balanceVal) {
//                    //move the battery up .25
//                    if(!weight.move(.25)) {
//                        //we are maxed out move the battery back to centerish and drive forward
//                        double start = stopwatch.get();
//                        while(stopwatch.get() < start+1) {
//                            weight.move(-.25);
////                            robotDrive.drive(.25,0);
//                        }
//                        //continue moving the battery center?
//                        while(stopwatch.get() < start+5) {
//                            weight.move(-.25);
//                        }
//                    }
//                } else {
//                    if(!weight.move(-.25)) {
//                        //we are mined out move the battery back to centerish and drive forward
//                        double start = stopwatch.get();
//                        while(stopwatch.get() < start+1) {
//                            weight.move(.25);
////                            robotDrive.drive(-.25,0);
//                        }
//                        //continue moving the battery center?
//                        while(stopwatch.get() < start+5) {
//                            weight.move(.25);
//                        }
//                    }
//                }
//            }
//        }
//    }
//    
//    private void calebrateGyro() {
//        if(calibrating) {
//            double time = stopwatch.get();
//            if(time < 1){
//                //for 1 sec capture gyro values
//                rate.put(new Double(time), new Double(gyro.getAngle()));
//                loop++;
//            } else if(rate.size()>0) {
//                //get the average rate of change of the gyro
//                double times[] = new double[rate.size()];
//                double values[] = new double[rate.size()];
//                loop = 0;
//                for (Enumeration e = rate.keys() ; e.hasMoreElements() ;) {
//                    times[loop] = ((Double)e.nextElement()).doubleValue();
//                    values[loop] = ((Double)rate.get(e.nextElement())).doubleValue();
//                    ++loop;
//                }
//                gryoOffset = 0;
//                if(loop>1){
//                    //add the slopes of all the values v times and get the average per second
//                    for(int i = 1;i<loop;++i){
//                        double rise = values[i]-values[i-1];
//                        double run = times[i]-times[i-1];
//                        gryoOffset += rise/run;
//                    }
//                    gryoOffset /= loop-1;
//                } else {
//                    //fallback we only have one gyro value so that is what we have to deal with
//                    gryoOffset = values[0];
//                }
//                calibrating = false;
//            } else {
//                //extreme fallback if for some reason we never even captured a single value
//                gryoOffset = gyro.getAngle();
//                calibrating = false;
//            }
//        }
//    }
}
