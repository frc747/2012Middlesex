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
    
    // Declare variables SO MANY VARIABLES!!!
    int mode;
    int autoDrive, autoOperate, balancing;
    RobotDrive robotDrive;
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
    DataLogger datalogger;
    DataLoader dataloader;

    // constructor for robot variables
    public RobotTemplate() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        
        autoDrive = AUTOOFF;
        autoOperate = AUTOOFF;
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
        
        //setup robot drive class with our drive speed controllers
        robotDrive = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        
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
        liftLimitFrontUp = new DigitalInput(1,1);
        liftLimitFrontDown = new DigitalInput(1,2);
        liftLimitBackUp = new DigitalInput(1,3);
        liftLimitBackDown = new DigitalInput(1,4);
        
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
    
    //set our drive motor bounds uniformly
    private void setDriveBounds(int max, int deadbandMax, int center, int deadbandMin, int min) {
        frontLeftMotor.setBounds(max,deadbandMax,center,deadbandMin,min);
        rearLeftMotor.setBounds(max,deadbandMax,center,deadbandMin,min);
        frontRightMotor.setBounds(max,deadbandMax,center,deadbandMin,min);
        rearRightMotor.setBounds(max,deadbandMax,center,deadbandMin,min);
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private void drive(int percent) {
        //tell the driver the current drive percentage
        dsPrint(4, percent+"% speed");
        //take joystick inputs and drive the robot
        robotDrive.tankDrive((rightStick.getY()*(percent/100)), (leftStick.getY()*(percent/100)));
        
        //autolift
//        if(autoDrive != AUTOOFF){
//            if(0 != leftStick.getY() && 0 != rightStick.getY()) {
//                //joysticks in same direction
//                if((leftStick.getY() > 0 && rightStick.getY() > 0) || (leftStick.getY() < 0 && rightStick.getY() < 0)) {
//                    autoDrive = BACK;
//                } else {
//                    autoDrive = FWD;
//                }
//            }
//            setLiftMotors(autoDrive);
//        }
    }
    
    //set the batery speed controller with the given value if we can move it in that direction
    private boolean setBattery(double value) {
        if(value>0) {
            if(!batteryLimitFwd.get()) {
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
        return setBattery(operatorStick.getY()*(percent/100));
    }
    
    //setting our lift motors in different directions keeping in mind the limits
    //(like a spike would, btw the lift speed controllers
    //can be swapped with spikes if needed)
    private void setLiftMotors(int direction) {
        if(direction==UP && liftLimitFrontUp.get()){
            liftMotorFront.set(1);
        } else if (direction==DWN && liftLimitFrontDown.get()){
            liftMotorFront.set(-1);
        } else {
            liftMotorFront.set(0);
        }
        
        if(direction==UP && liftLimitBackUp.get()) {
            liftMotorBack.set(1);
        } else if (direction==DWN && liftLimitBackDown.get()) {
            liftMotorBack.set(-1);
        } else {
            liftMotorBack.set(0);
        }
        
        if(autoDrive!=AUTOOFF && ((!liftLimitFrontUp.get() && !liftLimitBackUp.get())||(!liftLimitFrontDown.get() && !liftLimitBackDown.get()))) {
            autoDrive = STOP;
        }
    }
    
    private void downLift() {
        if(liftLimitFrontDown.get()) {
            liftMotorFront.set(-.3);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitFrontDown.get()) {
            liftMotorBack.set(-.3);
        } else {
            liftMotorBack.set(0);
        }
    }
    
    private void upLift() {
        if(liftLimitFrontUp.get()) {
            liftMotorFront.set(.3);
        } else {
            liftMotorFront.set(0);
        }
        
        if(liftLimitFrontUp.get()) {
            liftMotorBack.set(.3);
        } else {
            liftMotorBack.set(0);
        }
    }
    
    //setting our fin motor in different directions keeping in mind the limits
    //(like a spike would, btw the fin speed controller 
    //can be swapped with a spike if needed)
    private void setFinMotor(int direction) {
        if(direction==FWD && (finLimitFwd.get())){
            finMotor.set(.5);
        } else if (direction==BACK && (finLimitBck.get())){
            finMotor.set(-.5);
        } else {
            finMotor.set(0);
            if(autoOperate != AUTOOFF) {
                autoOperate = STOP;
            }
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
                            robotDrive.drive(.25,0);
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
                            robotDrive.drive(-.25,0);
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
        if(recording && datalogger != null) {
            try {
                datalogger.wDouble(leftStick.getY());
                datalogger.wDouble(rightStick.getY());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    //playback the robot log
    private void playback() {
        try {
            if(dataloader == null) {
                dataloader = new DataLoader("auto1.log");
            }
            robotDrive.tankDrive(dataloader.rDouble(),dataloader.rDouble());
        } catch (EOFException ex) {
            dsPrint("end of recording");
        } catch (IOException ex) {
            dsPrint("playback error");
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
//        if(!ds.isFMSAttached()) {
//            //if we press the button once start recording, press it again close the recording
//            if(operatorStick.getRawButton(11)) {
//                try {
//                    if(!recording) {
//                        datalogger = new DataLogger("auto1.log");
//                    } else {
//                        datalogger.close();
//                    }
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        } else {
//            recording = false;
//        }
        
        //set auto modes in any mode
//        if(leftStick.getRawButton(3)) {
//            if(autoDrive==AUTOOFF) {
//                autoDrive = 0;
//            } else {
//                autoDrive = AUTOOFF;
//            }
//        }
//        
//        if (operatorStick.getRawButton(7)){
//            if(autoOperate == AUTOOFF){ 
//                autoOperate = 0;
//            } else {
//                autoOperate = AUTOOFF;
//            }
//        }
        
        //print auto mode
//        String drv = "off";
//        if(autoDrive==UP) {
//            drv = "up";
//        } else if (autoDrive==DWN) {
//            drv = "down";
//        } else if (autoDrive==STOP) {
//            drv = "stop";
//        }
//        
//        String oper = "off";
//        if(autoOperate==STOP) {
//            oper = "stop";
//        } else if (autoOperate==FWD) {
//            oper = "fwd";
//        } else if (autoOperate==BACK) {
//            oper = "back";
//        }
//        dsPrint(2,"Drv: "+drv+" Opr: "+oper);
//        dsPrint(3,"AutoBalSig: "+balancing);
        
        //test sensor data
        {
//            ADXL345_I2C.AllAxes axises = accel.getAccelerations();
//            dsPrint(6,axises.XAxis+" "+axises.YAxis+" "+axises.ZAxis);
            
//            boolean limit1 = liftLimitFrontUp.get();
//            boolean limit2 = liftLimitFrontDown.get();
//            boolean limit3 = liftLimitBackUp.get();
//            boolean limit4 = liftLimitBackDown.get();
//            dsPrint(6,"1: "+(limit1?"1":"0")+" 2: "+(limit2?"1":"0")+" 3: "+(limit3?"1":"0")+" 4: "+(limit4?"1":"0"));
        dsPrint(2,(liftLimitFrontUp.get()?"fu1":"fu0")+(liftLimitFrontDown.get()?"fd1":"fd0")+(liftLimitBackUp.get()?"bu1":"bu0")+(liftLimitBackDown.get()?"bd1":"bd0"));
        dsPrint(3,(finLimitFwd.get()?"ff1":"ff0")+(finLimitBck.get()?"fb1":"fb0")+(batteryLimitFwd.get()?"bf1":"bf0")+(batteryLimitBck.get()?"bb1":"bb0"));
        }
        
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD(); 
    }

    //do this while we are disabled
    protected void disabled() {
        // kill movement
        robotDrive.drive(0,0);
        setLiftMotors(0);
        setFinMotor(0);
        batteryMotor.set(0);
        //commenting out for now, very possibly could break things
//        calebrateGyro();
    }

    //run autonomous code for begining of match
    protected void autonomous() {
        if(this.stopwatch.get() < 5) {
            robotDrive.drive(1,0); //forward
            setFinMotor(1); //push the fin all the way forward
        }
        //playback recorded autonomous
        //playback();
    }

    //run code for drivers
    protected void teleoperated() {
        //only will record if we were pulling the operator trigger on boot
//        record();
        
        // drive code
        if (rightStick.getRawButton(1) == true && leftStick.getRawButton(1) == true) {
            drive(25);
        } else {
            drive(100);
        }

        // lift code if we are not automatically doing this lift while driving
//        if(autoDrive == AUTOOFF) {
//            if (rightStick.getRawButton(2)) {
//                setLiftMotors(1);
//            } else {
//                setLiftMotors(-1);
//            }
//        }
//        if (leftStick.getRawButton(3) && !liftLimitFrontUp.get()){
//            liftMotorFront.set(.1);
//        } else if(leftStick.getRawButton(2) && !liftLimitFrontDown.get()){
//            liftMotorFront.set(-.1);
//        } else {
//            liftMotorFront.set(0);
//        }
//        
//        if (rightStick.getRawButton(3) && liftLimitBackUp.get()){
//            liftMotorBack.set(.1);
//        } else if(rightStick.getRawButton(2) && liftLimitBackDown.get()){
//            liftMotorBack.set(-.1);
//        } else {
//            liftMotorBack.set(0);
//        }
        
        if(rightStick.getRawButton(2)) {
            downLift();
        } else {
            upLift();
        }

        //fin code
        if (operatorStick.getRawButton(3) || autoOperate==UP){
            if (autoOperate==UP) {
                autoOperate = STOP;
            } else if(autoOperate == STOP) {
                autoOperate = UP;
            }
            setFinMotor(1);
        } else if (operatorStick.getRawButton(2) || autoOperate==DWN){
            if (autoOperate==DWN) {
                autoOperate = STOP;
            } else if(autoOperate == STOP) {
                autoOperate = DWN;
            }
            setFinMotor(-1);
        } else {
            setFinMotor(0);
        }
        
        //battery code
        if(operatorStick.getRawButton(1)) {
            driveBattery(25);
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
