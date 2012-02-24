//Drive motors are Victors, wheel lift motors are Jaguars.(Addison 2/12/12)
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends RobotBase {
    // Declare variables SO MANY VARIABLES!!!
    int mode;
    String autoDrive, autoOperate;
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
    Encoder finEncoder;
    double smoothed;
    int smoothing;
    double lastUpdate;
    double balanceVal;
    String balancing;

    // constructor for robot variables
    public RobotTemplate() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        
        autoDrive = null;
        autoOperate = null;
        balancing = "balanced";
        
        //low pass filter stuff
        smoothed = 0;
        balanceVal = 0;
        smoothing = 10;
        lastUpdate = 0;
        
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
        frontLeftMotor = new Victor(1);
        rearLeftMotor = new Victor(2);
        frontRightMotor = new Victor(4);
        rearRightMotor = new Victor(5);
        
        //setup robot drive class with our drive speed controllers
        robotDrive = new RobotDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        
        //setup fin speed controller
        finMotor = new Victor(6);
        
        //setup battery speed controller
        batteryMotor = new Victor(3);
        
        //setup lift speed controllers
        liftMotorFront = new Jaguar(7);
        liftMotorBack = new Jaguar(8);
        
        /**************************** DIGITAL I/O  ****************************/
        //PORTS CAN BE FROM 1-14
        
        //lift limits
        liftLimitFrontUp = new DigitalInput(1);
        liftLimitFrontDown = new DigitalInput(2);
        liftLimitBackUp = new DigitalInput(3);
        liftLimitBackDown = new DigitalInput(4);
        
        //fin limits
        finLimitFwd = new DigitalInput(5);
        finLimitBck = new DigitalInput(6);
        
        //battery limits
        batteryLimitFwd = new DigitalInput(7);
        batteryLimitBck = new DigitalInput(8);
        
        //encoder
        batteryEncoder = new Encoder(9,10);
        finEncoder = new Encoder(11,12);
        
        /***************************** ANALOG I/O *****************************/
        //PORTS CAN BE FROM 1-8
        //setup gyro sensor
        gyro = new Gyro(1);
        
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
        dsPrint(3, percent+"% speed");
        //take joystick inputs and drive the robot
        robotDrive.tankDrive((leftStick.getY()/(percent/100)), (rightStick.getY()/(percent/100)));
        
        //autolift
        if(autoDrive != null){
            if(0 != leftStick.getY() && 0 != rightStick.getY()) {
                //joysticks in same direction
                if((leftStick.getY() > 0 && rightStick.getY() > 0) || (leftStick.getY() < 0 && rightStick.getY() < 0)) {
                    autoDrive = "dwn";
                } else {
                    autoDrive = "up";
                }
            }
            setLiftMotors(autoDrive);
        }
    }
    
    //set the batery speed controller with the given value if we can move it in that direction
    private boolean setBattery(double value) {
        if(value>0) {
            if(batteryLimitFwd.get()) {
                return false;
            }
        } else {
            if(batteryLimitBck.get()) {
                return false;
            }
        }
        batteryMotor.set(value);
        return true;
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private boolean driveBattery(int percent) {
        //tell the driver the current drive percentage
        dsPrint(4, percent+"% speed");
        //take joystick inputs and drive the robot
        return setBattery(operatorStick.getY()/(percent/100));
    }
    
    //setting our lift motors in different directions keeping in mind the limits
    //(like a spike would, btw the lift speed controllers
    //can be swapped with spikes if needed)
    private void setLiftMotors(String direction) {
        if(direction.equals("up") && (!liftLimitFrontUp.get() || !liftLimitBackUp.get())){
            liftMotorFront.set(1);
            liftMotorBack.set(1);
        } else if (direction.equals("dwn") && (!liftLimitFrontDown.get() || !liftLimitBackDown.get())){
            liftMotorFront.set(-1);
            liftMotorBack.set(-1);
        } else {
            liftMotorFront.set(0);
            liftMotorBack.set(0);
            if(autoDrive!=null) {
                autoDrive = "stp";
            }
        }
    }
    
    //setting our fin motor in different directions keeping in mind the limits
    //(like a spike would, btw the fin speed controller 
    //can be swapped with a spike if needed)
    private void setFinMotor(String direction) {
        if(direction.equals("fwd") && (!finLimitFwd.get())){
            finMotor.set(1);
        } else if (direction.equals("bck") && (!finLimitBck.get())){
            finMotor.set(-1);
        } else {
            finMotor.set(0);
            if(autoOperate != null) {
                autoOperate = "stp";
            }
        }
    }
    
    //rough auto balance code... its 1am i should review this lol
    private void autoBalance() {
        //initialize values for balancing before we hit the ramp
        if(balancing.equals("start")) {
            lastUpdate = stopwatch.get();
            smoothed = gyro.getAngle();
            balancing = "position";
        } else if(balancing.equals("balancing")) {
            // if our gyro is at our balance value we are good
            //(probably should make this more robust with a zone that we are good with (like +/- 1 degree))
            if(lowPassFilter(gyro.getAngle())==balanceVal) {
                balancing = "balanced";
            } else {
                //if we are above our balanced value
                if(smoothed>balanceVal) {
                    //move the battery up .25
                    if(!setBattery(.25)) {
                        //we are maxed out move the batter back to centerish and drive forward
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
                        //we are mined out move the batter back to centerish and drive forward
                        double start = stopwatch.get();
                        while(stopwatch.get() < start+1) {
                            setBattery(.25);
                            robotDrive.drive(-.25,0);
                        }
                        //contineu moving the battery center?
                        while(stopwatch.get() < start+5) {
                            setBattery(.25);
                        }
                    }
                }
            }
        }
    }
    
    //initalize functions on the robot like turning on an air compressor
    // Actions which would be performed once (and only once) upon initialization of the
    // robot would be put here.
    protected void robotInit() {
        stopwatch.start();
        // let the drivers know that we have initialized the robot
        dsPrint("Robot Initialized");
    }
    
    //common code to be run in all modes every cycle
    protected void common(){
        //set auto modes in any mode
        if(leftStick.getRawButton(3)) {
            if(autoDrive==null) {
                autoDrive = "stp";
            } else {
                autoDrive = null;
            }
        }
        
        if (operatorStick.getRawButton(10)){
            if(autoOperate == null){ 
                autoOperate = "stp";
            } else {
                autoOperate = null;
            }
        }
        
        //print auto mode
        dsPrint(1,"Drv: "+autoDrive==null?"off":autoDrive+" Opr: "+autoOperate==null?"off":autoOperate);
        dsPrint(2,"AutoBalSig: "+balancing);
        
        //test sensor data
        {
            ADXL345_I2C.AllAxes axises = accel.getAccelerations();
            dsPrint(5,axises.XAxis+" "+axises.YAxis+" "+axises.ZAxis);
            
            boolean limit1 = liftLimitFrontUp.get();
            boolean limit2 = liftLimitFrontDown.get();
            boolean limit3 = liftLimitBackUp.get();
            boolean limit4 = liftLimitBackDown.get();
            dsPrint(6,"1: "+(limit1?"1":"0")+" 2: "+(limit2?"1":"0")+" 3: "+(limit3?"1":"0")+" 4: "+(limit4?"1":"0"));
        }
        
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD(); 
    }

    //do this while we are disabled
    protected void disabled() {
        // kill movement
        robotDrive.drive(0,0);
        setLiftMotors("off");
        setFinMotor("off");
        batteryMotor.set(0);
    }

    //run autonomous code for begining of match
    protected void autonomous() {
        while(this.stopwatch.get() < 5) {
            robotDrive.drive(1,0); //forward
            setFinMotor("fwd"); //push the fin all the way forward
        }
    }

    //run code for drivers
    protected void teleoperated() {
        // drive code
        if (rightStick.getRawButton(1) == true && leftStick.getRawButton(1) == true) {
            drive(25);
        } else {
            drive(100);
        }

        // lift code if we are not automatically doing this lift while driving
        if(autoDrive == null) {
            if (rightStick.getRawButton(3)) {
                setLiftMotors("up");
            } else if (rightStick.getRawButton(2)) {
                setLiftMotors("dwn");
            } else {
                setLiftMotors("stp");
            }
        }

        //fin code
        if (operatorStick.getRawButton(3) || autoOperate.equals("fwd")){
            if (autoOperate.equals("fwd")) {
                autoOperate = "stp";
            } else if(autoOperate != null) {
                autoOperate = "fwd";
            }
            setFinMotor("fwd");
        } else if (operatorStick.getRawButton(2) || autoOperate.equals("bck")){
            if (autoOperate.equals("bck")) {
                autoOperate = "stp";
            } else if(autoOperate != null) {
                autoOperate = "bck";
            }
            setFinMotor("bck");
        } else {
            setFinMotor("stp");
        }
        
        //battery code
        if(operatorStick.getRawButton(1)) {
            driveBattery(25);
        } else {
            driveBattery(100);
        }
        
        //auto balance code operator and driver must agree to go?!
        //Bunca should we do this? or just have the operator/driver control it
        if(operatorStick.getRawButton(10) && rightStick.getRawButton(10)){
            if(balancing==null) {
                balancing = "start";
            } else if(balancing.equals("position")) {
                balancing = "balancing";
            }
            autoBalance();
        }
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
//                dsPrint("Disable " + stopwatch.get() + " sec"); //I ran out of lines to print to D:
                disabled(); // run disabled code
            } else if (isAutonomous()) {
                changeMode(1);
//                dsPrint("Auto " + stopwatch.get() + " sec");
                autonomous(); // run the autonomous code
            } else { //if (isTeleoperated()) {
                changeMode(2);
//                dsPrint("Teleop " + stopwatch.get() + " sec");
                teleoperated(); // run teleoperated code
            }
        } // end while loop SHOULD NEVER HAPPEN OR THE ROBOT IS DEAD
    }
}
