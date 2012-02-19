//Chinn's streamlined Simple Robot Template, ill comment stuff on the bus :D
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
    // Declare variables
    int mode;
    int drivePercent;
    
    //robot drive system
    RobotDrive robotDrive;

    // camera variable
    //AxisCamera camera;
    
    // cRIO info
    DriverStation ds;
    
    // timer
    Timer stopwatch;
    
    // output to driver console
    DriverStationLCD dsLCD;

    // Declare variables for the 3 joysticks (right/left for tank, 3rd stick for the operator
    Joystick rightStick, leftStick, operatorStick;
    
    // limit switch variable
    DigitalInput liftLimit1;
    DigitalInput liftLimit2;
    DigitalInput liftLimit3;
    DigitalInput liftLimit4;
    
    //drive motors
    Victor driveMotor1;
    Victor driveMotor2;
    Victor driveMotor3;
    Victor driveMotor4;
    
    //lift motors
    Jaguar liftMotor1;
    Jaguar liftMotor2;
    
    //accelerometer
    ADXL345_I2C accel;
    
    //gyroscope
    Gyro gyro;
    
    //Driver Station Printing helper functions
    private void dsPrint(String s) {
        dsPrint(1, s, 1);
    }

    private void dsPrint(int line, String s) {
        dsPrint(line, s, 1);
    }

    private void dsPrint(int line, String s, int column) {
        StringBuffer string = new StringBuffer(s);
        string.setLength(30);
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
    
    private void changeMode(int newMode) {
        // if we were not in disabled before reset the stopwatch
        if (mode!=newMode){
            stopwatch.reset();
            mode = newMode; // set disabled state
        }
    }

    // constructor for robot variables
    public RobotTemplate() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        mode = -1;
        drivePercent = 100;
        
        // Define joysticks being used at USB port #1 and USB port #2 on the Driver Station
        rightStick = new Joystick(1);
        leftStick = new Joystick(2);
        operatorStick = new Joystick(3);
        
        //setup drive speed controllers
        driveMotor1 = new Victor(1);
        driveMotor2 = new Victor(2);
        driveMotor3 = new Victor(3);
        driveMotor4 = new Victor(4);
        
        //setup robot drive class with our drive speed controllers
        robotDrive = new RobotDrive(driveMotor1, driveMotor2, driveMotor3, driveMotor4);
        
        //setup lift speed controllers
        liftMotor1 = new Jaguar(5);
        liftMotor2 = new Jaguar(6);

        // must get instance of these because you are not creating it
        // it already exists and we need to access it in our program
        // get the cRIO instance
        ds = DriverStation.getInstance();

        // get the driver station instance
        dsLCD = DriverStationLCD.getInstance();

        // get the camera instance
        //camera = AxisCamera.getInstance();
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        //lift limits
        liftLimit1 = new DigitalInput(1);
        liftLimit2 = new DigitalInput(2);
        liftLimit3 = new DigitalInput(3);
        liftLimit4 = new DigitalInput(4);
        
        //2Gs of force gives us the most resolution at low forces
        //which is what we will be dealing with balancing
        accel = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k2G);
        
        //setup gyro sensor
        gyro = new Gyro(1);
        
        // let the drivers know that we have contructed the variables
        dsPrint("Constructor Completed");
    }
    
    //set our drive motor bounds uniformly
    private void setDriveBounds(int max, int deadbandMax, int center, int deadbandMin, int min) {
        driveMotor1.setBounds(max,deadbandMax,center,deadbandMin,min);
        driveMotor3.setBounds(max,deadbandMax,center,deadbandMin,min);
        driveMotor2.setBounds(max,deadbandMax,center,deadbandMin,min);
        driveMotor4.setBounds(max,deadbandMax,center,deadbandMin,min);
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private void drive(int percent) {
        //if our new percent is different than our current one change our bounds
        if(percent!=drivePercent) {
            drivePercent = percent;
            int value = 127*drivePercent/100;
            setDriveBounds(128+value,128,127,126,127-value);
        }
        //tell the driver the current drive percentage
        dsPrint(2,drivePercent+"% speed");
        //take joystick inputs and drive the robot
        robotDrive.tankDrive(leftStick, rightStick);
    }
    
    private void setLiftMotors(int val) {
        liftMotor1.set(val);
        liftMotor2.set(val);
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
        //test sensor data
        {
            ADXL345_I2C.AllAxes axises = accel.getAccelerations();
            dsPrint(3,axises.XAxis+" "+axises.YAxis+" "+axises.ZAxis);
            double gyroValue = gyro.getAngle();
            dsPrint(4,Double.toString(gyroValue));
        }
        
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD(); 
    }

    //do this while we are disabled
    protected void disabled() {
        // kill drive
        robotDrive.tankDrive(0,0);
    }

    //run autonomous code for begining of match
    protected void autonomous() {
        disabled();
    }

    //run code for drivers
    protected void teleoperated() {
        // drive code
        if (rightStick.getRawButton(1) == true && leftStick.getRawButton(1) == true) {
            drive(25);
        } else {
            drive(100);
        }
        
        //lift code
        //TODO: prototype limit switch and lift motor values, most likely will change when testing
        if (rightStick.getRawButton(3) == true && liftLimit1.get() && liftLimit2.get() ) {
            setLiftMotors(255);
        } else if (rightStick.getRawButton(4) == true && liftLimit3.get() && liftLimit4.get() ) {
            setLiftMotors(0);
        } else {
            setLiftMotors(127);
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
                dsPrint("Disabled seconds: " + stopwatch.get());
                disabled(); // run disabled code
            } else if (isAutonomous()) {
                changeMode(1);
                dsPrint("Autonomous seconds: " + stopwatch.get());
                autonomous(); // run the autonomous code
            } else { //if (isTeleoperated()) {
                changeMode(2);
                dsPrint("Operator seconds: " + stopwatch.get());
                teleoperated(); // run teleoperated code
            }
        } // end while loop SHOULD NEVER HAPPEN OR THE ROBOT IS DEAD
    }
}
