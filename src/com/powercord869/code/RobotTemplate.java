//Drive motors are Victors, wheel lift motors are Jaguars.(Addison 2/12/12)
package com.powercord869.code;

import edu.wpi.first.wpilibj.*;
import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends RobotBase {
    //Constants
    int AUTOOFF = 869; //needed a magic value other than -1, 0, and 1
    int STOP = 0;
    int FWD = 1;
    int BACK = -1;
    int UP = 1;
    int DWN = -1;
    
    int BALANCED = 0;
    int STARTUP = 1;
    int READY = 2;
    int GO = 3;
    
    int BATTERY = 50;
    double FINFWD = .5;
    double FINBACK = -.4;
    double LIFT = 1;
    
    // Declare variables SO MANY VARIABLES!!!
    int mode;
    int balancing;
    DriverStation ds;
    Timer stopwatch;
    DriverStationLCD dsLCD;
    Joystick rightStick, leftStick, operatorStick;
    DigitalInput liftLimitFrontUp, liftLimitFrontDown, liftLimitBackUp, liftLimitBackDown;
    DigitalInput finLimitFwd, finLimitBck;
    DigitalInput batteryLimitFwd, batteryLimitBck;
    Victor frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor;
    Victor finMotor;
    Victor batteryMotor;
    Jaguar liftMotorFront;
    Jaguar liftMotorBack;
    ADXL345_I2C accel;
    Gyro gyro;
    Encoder batteryEncoder;
    int smoothing, loop;
    double smoothed, lastUpdate, balanceVal, precision;
    boolean calibrating;
    Hashtable rate;
    double gryoOffset;
    boolean recording;
    Recorder recorder;
    Loader loader;

    // constructor for robot variables
    public RobotTemplate() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        balancing = BALANCED;
        
        //low pass filter stuff
        smoothed = 0;
        balanceVal = 0;
        smoothing = 10;
        lastUpdate = 0;
        precision = 1;
        rate = new Hashtable();
        
        // must get instance of these because you are not creating it
        // it already exists and we need to access it in our program
        // get the cRIO instance
        ds = DriverStation.getInstance();

        // get the driver station instance
        dsLCD = DriverStationLCD.getInstance();
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        /******************************** USB  ********************************/
        // Define joysticks on the Driver Station
        rightStick = new Joystick(1);
        leftStick = new Joystick(2);
        operatorStick = new Joystick(3);
        
        /******************************** PWM  ********************************/
        //PORTS CAN BE FROM 1-10
        
        //setup drive speed controllers
        frontLeftMotor = new Victor(1,4);
        rearLeftMotor = new Victor(1,5);
        frontRightMotor = new Victor(1,1);
        rearRightMotor = new Victor(1,2);
        
        //setup fin speed controller
        finMotor = new Victor(1,6);
        
        //setup battery speed controller
        batteryMotor = new Victor(1,3);
        
        //setup lift speed controllers
        liftMotorFront = new Jaguar(1,7);
        liftMotorBack = new Jaguar(1,8);
        
        /**************************** DIGITAL I/O  ****************************/
        //PORTS CAN BE FROM 1-14
        
        //lift limits
        liftLimitFrontUp = new DigitalInput(1,3);
        liftLimitFrontDown = new DigitalInput(1,4);
        liftLimitBackUp = new DigitalInput(1,1);
        liftLimitBackDown = new DigitalInput(1,2);
        
        //fin limits
        finLimitFwd = new DigitalInput(1,6);
        finLimitBck = new DigitalInput(1,5);
        
        //battery limits
        batteryLimitFwd = new DigitalInput(1,8);
        batteryLimitBck = new DigitalInput(1,7);
        
        //encoder
        batteryEncoder = new Encoder(1,9,1,10);
        
        /***************************** ANALOG I/O *****************************/
        //PORTS CAN BE FROM 1-8
        //setup gyro sensor
        gyro = new Gyro(1,1);
        
        /******************************* OTHER  *******************************/
        
        //2Gs of force gives us the most resolution at low forces
        //which is what we will be dealing with balancing
        accel = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k2G);
        
        /****************************** COMPLETE ******************************/
        // let the drivers know that we have contructed the variables
        dsPrint("Constructor Completed");
    }
    
    //low pass filter for gyro filtering (thank you internet)
    double lowPassFilter( double newValue ){
        double now = stopwatch.get();
        double elapsedTime = now - lastUpdate;
        smoothed += elapsedTime * ( newValue - smoothed ) / smoothing;
        lastUpdate = now;
        return smoothed;
    }
    
    //Driver Station Printing helper functions
    private void dsPrint(String s) {dsPrint(1, s, 1);}
    private void dsPrint(int line, String s) {dsPrint(line, s, 1);}
    private void dsPrint(int line, String s, int column) {
        StringBuffer string = new StringBuffer(s);
        string.setLength(DriverStationLCD.kLineLength); //21 currently
        switch (line) {
            case 6:
                dsLCD.println(DriverStationLCD.Line.kUser6, column, string);
                break;
            case 5:
                dsLCD.println(DriverStationLCD.Line.kUser5, column, string);
                break;
            case 4:
                dsLCD.println(DriverStationLCD.Line.kUser4, column, string);
                break;
            case 3:
                dsLCD.println(DriverStationLCD.Line.kUser3, column, string);
                break;
            case 2:
                dsLCD.println(DriverStationLCD.Line.kUser2, column, string);
                break;
            default:
                dsLCD.println(DriverStationLCD.Line.kMain6, column, string);
        }
    }
    
    //mode changer for general robot modes
    private void changeMode(int newMode) {
        // if we were not in disabled before reset the stopwatch
        if (mode!=newMode){
            stopwatch.reset();
            mode = newMode; // set disabled state
        }
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private void drive(int percent) {
        //tell the driver the current drive percentage
        dsPrint(4, percent+"% speed");
        //take joystick inputs and drive the robot
        double right = ((-leftStick.getY())*percent/100);
        double left = (rightStick.getY()*percent/100);
        frontLeftMotor.set(left);
        rearLeftMotor.set(left);
        frontRightMotor.set(right);
        rearRightMotor.set(right);
    }
    
    //set the batery speed controller with the given value if we can move it in that direction
    private boolean setBattery(double value) {
        if(value>0) {
            if(batteryLimitFwd.get()) {
                batteryMotor.set(0);
                return false;
            }
        } else if(value<0) {
            if(!batteryLimitBck.get()) {
                batteryMotor.set(0);
                return false;
            }
        }
        batteryMotor.set(value);
        return true;
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private boolean driveBattery(int percent) {
        //tell the driver the current drive percentage
        dsPrint(5, percent+"% speed");
        //take joystick inputs and drive the robot
        return setBattery(operatorStick.getY()*percent/100);
    }
    
    private void downLift() {
        System.out.println("downLift " + -LIFT);
        if(liftLimitFrontDown.get()) {
            liftMotorFront.set(-LIFT);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitBackDown.get()) {
            liftMotorBack.set(-LIFT);
        } else {
            liftMotorBack.set(0);
        }
    }
    
    private void upLift() {
        
        if(liftLimitFrontUp.get()) {
            System.out.println("front up" + LIFT);
            liftMotorFront.set(LIFT);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitBackUp.get()) {
            System.out.println("back up" + LIFT);
            liftMotorBack.set(LIFT);
        } else {
            liftMotorBack.set(0);
        }
    }
    
    //setting our fin motor in different directions keeping in mind the limits
    //(like a spike would, btw the fin speed controller 
    //can be swapped with a spike if needed)
    private void setFinMotor(int direction) {
        if(direction==FWD && finLimitFwd.get()){
            finMotor.set(FINFWD);
        } else if (direction==BACK && finLimitBck.get()){
            finMotor.set(FINBACK);
        } else {
            finMotor.set(0);
        }
    }
    
    //based on our gyroOffset get our adjusted angle;
    private double getAdjustedGyroAngle() {
        return gyro.getAngle()-(gryoOffset*Timer.getFPGATimestamp());
    }
    
    //rough auto balance code... its 1am i should review this lol
    private void autoBalance() {
        //initialize values for balancing before we hit the ramp
        if(balancing==STARTUP) {
            //smoothed = gyro.getAngle();
            smoothed = getAdjustedGyroAngle();
            balancing = READY;
        } else if(balancing==GO) {
            // if our gyro is at our balance value we are good
            lastUpdate = stopwatch.get();
//            double currentAngle = lowPassFilter(gyro.getAngle());
            double currentAngle = lowPassFilter(getAdjustedGyroAngle());
            if(currentAngle>balanceVal-precision && currentAngle<balanceVal+precision) {
                balancing = BALANCED;
            } else {
                //if we are above our balanced value
                if(smoothed>balanceVal) {
                    //move the battery up .25
                    if(!setBattery(.25)) {
                        //we are maxed out move the battery back to centerish and drive forward
                        double start = stopwatch.get();
                        while(stopwatch.get() < start+1) {
                            setBattery(-.25);
//                            robotDrive.drive(.25,0);
                        }
                        //continue moving the battery center?
                        while(stopwatch.get() < start+5) {
                            setBattery(-.25);
                        }
                    }
                } else {
                    if(!setBattery(-.25)) {
                        //we are mined out move the battery back to centerish and drive forward
                        double start = stopwatch.get();
                        while(stopwatch.get() < start+1) {
                            setBattery(.25);
//                            robotDrive.drive(-.25,0);
                        }
                        //continue moving the battery center?
                        while(stopwatch.get() < start+5) {
                            setBattery(.25);
                        }
                    }
                }
            }
        }
    }
    
    private void calebrateGyro() {
        if(calibrating) {
            double time = stopwatch.get();
            if(time < 1){
                //for 1 sec capture gyro values
                rate.put(new Double(time), new Double(gyro.getAngle()));
                loop++;
            } else if(rate.size()>0) {
                //get the average rate of change of the gyro
                double times[] = new double[rate.size()];
                double values[] = new double[rate.size()];
                loop = 0;
                for (Enumeration e = rate.keys() ; e.hasMoreElements() ;) {
                    times[loop] = ((Double)e.nextElement()).doubleValue();
                    values[loop] = ((Double)rate.get(e.nextElement())).doubleValue();
                    ++loop;
                }
                gryoOffset = 0;
                if(loop>1){
                    //add the slopes of all the values v times and get the average per second
                    for(int i = 1;i<loop;++i){
                        double rise = values[i]-values[i-1];
                        double run = times[i]-times[i-1];
                        gryoOffset += rise/run;
                    }
                    gryoOffset /= loop-1;
                } else {
                    //fallback we only have one gyro value so that is what we have to deal with
                    gryoOffset = values[0];
                }
                calibrating = false;
            } else {
                //extreme fallback if for some reason we never even captured a single value
                gryoOffset = gyro.getAngle();
                calibrating = false;
            }
        }
    }
    
    //record different things about the robot
    private void record() {
        if(recording && recorder != null) {
            try {
                recorder.wDouble(leftStick.getY());
                recorder.wDouble(rightStick.getY());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //playback the robot log
    private void playback() {
        try {
            if(loader == null) {
                loader = new Loader("file:///auto1.log");
            }
//            drive(dataloader.rDouble(),dataloader.rDouble());
        } catch (EOFException ex) {
            dsPrint(6,"end of recording");
        } catch (IOException ex) {
            dsPrint(6,"playback error");
            //only stacktrace if we are not on the field
            if(!ds.isFMSAttached()) {
                ex.printStackTrace();
            }
        }
    }
    
    //initalize functions on the robot like turning on an air compressor
    // Actions which would be performed once (and only once) upon initialization of the
    // robot would be put here.
    protected void robotInit() {
        stopwatch.start();
        calibrating = true;
        loop = 0;
        // let the drivers know that we have initialized the robot
        dsPrint("Robot Initialized");
    }
    
    //common code to be run in all modes every cycle
    protected void common(){
        //makes sure we are not on the field
        if(!ds.isFMSAttached()) {
            try {
            //if we press the button once start recording, press it again close the recording
                if(operatorStick.getRawButton(11)) {
                    if(!recording) {
                        recorder = new Recorder("file:///auto1.log");
                        recording = true;
                    } 
                } else if(operatorStick.getRawButton(10)) {
                    if(recording) {
                        recorder.close();
                        recording = false;
                    } 
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            recording = false;
        }
        
        //test sensor data
        {
//            ADXL345_I2C.AllAxes axises = accel.getAccelerations();
//            dsPrint(6,axises.XAxis+" "+axises.YAxis+" "+axises.ZAxis);
//            dsPrint(2,(liftLimitFrontUp.get()?"fu1":"fu0")+(liftLimitFrontDown.get()?"fd1":"fd0")+(liftLimitBackUp.get()?"bu1":"bu0")+(liftLimitBackDown.get()?"bd1":"bd0"));
//            dsPrint(3,(finLimitFwd.get()?"ff1":"ff0")+(finLimitBck.get()?"fb1":"fb0")+(batteryLimitFwd.get()?"bf1":"bf0")+(batteryLimitBck.get()?"bb1":"bb0"));
        }
        
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD(); 
    }

    //do this while we are disabled
    protected void disabled() {
        // kill movement
        frontLeftMotor.set(0);
        rearLeftMotor.set(0);
        frontRightMotor.set(0);
        rearRightMotor.set(0);
        //kill lift
        liftMotorFront.set(0);
        liftMotorBack.set(0);
        liftMotorFront.set(0);
        liftMotorBack.set(0);
        //kill fin
        finMotor.set(0);
        //kill battery
        batteryMotor.set(0);
        //commenting out for now, very possibly could break things
//        calebrateGyro();
    }

    //run autonomous code for begining of match
    protected void autonomous() {
//        if(this.stopwatch.get() < 2) {
//            drive(100); //forward
//            setFinMotor(FWD); //push the fin all the way forward
//        } else {
            frontLeftMotor.set(0);
            rearLeftMotor.set(0);
            frontRightMotor.set(0);
            rearRightMotor.set(0);
            //drive(100); //forward
            setFinMotor(STOP); //push the fin all the way forward
//        }
        
        //kill lift
        liftMotorFront.set(0);
        liftMotorBack.set(0);
        liftMotorFront.set(0);
        liftMotorBack.set(0);
        
        
        //kill battery
        batteryMotor.set(0);
        
        //playback recorded autonomous
        //playback();
    }

    //run code for drivers
    protected void teleoperated() {
        //only will record if we were pulling the operator trigger on boot
//        record();
        
        // drive code
        if (rightStick.getRawButton(1) == true && leftStick.getRawButton(1) == true) {
            drive(75);
            downLift();
        } else if (rightStick.getRawButton(1) == true || leftStick.getRawButton(1) == true) {
            drive(50);
            downLift();
        } else {
            drive(100);
            if(rightStick.getRawButton(2)) {
                downLift();
            } else {
                upLift();
            }
        }
        
        

        //fin code
        if (operatorStick.getRawButton(3)){
            setFinMotor(FWD);
        } else if (operatorStick.getRawButton(2)) {
            setFinMotor(BACK);
        } else {
            setFinMotor(STOP);
        }
        
        //battery code
        if(operatorStick.getRawButton(1)) {
            driveBattery(50);
        } else {
            driveBattery(100);
        }
        
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
    }

    //main function from our perspective, this is what the robot calls when starting
    public void startCompetition() {
        // first and one-time initialization
        robotInit();
        //loop FOREVER
        while (true) {
            common(); // run common code
            if (isDisabled()) {
                changeMode(0);
                dsPrint("Disable " + stopwatch.get() + " sec"); //I ran out of lines to print to D:
                disabled(); // run disabled code
            } else if (isAutonomous()) {
                changeMode(1);
                dsPrint("Auto " + stopwatch.get() + " sec");
                autonomous(); // run the autonomous code
            } else { //if (isTeleoperated()) {
                changeMode(2);
                dsPrint("Teleop " + stopwatch.get() + " sec");
                teleoperated(); // run teleoperated code
            }
        } // end while loop SHOULD NEVER HAPPEN OR THE ROBOT IS DEAD
    }
}
