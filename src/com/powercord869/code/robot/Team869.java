//Drive motors are Victors, wheel lift motors are Jaguars.(Addison 2/12/12)
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
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
public class Team869 extends RobotBase {
    //Constants    
    private int BATTERY = 50;
    private double FINFWD = .5;
    private double FINBACK = -.4;
    private double LIFT = 1;
    
    //PWMs
        //Drive
            private int frontLeftMotor = 4;
            private int rearLeftMotor = 5;
            private int frontRightMotor = 1;
            private int rearRightMotor = 2;
        //Lift
            private int liftMotorFront = 7;
            private int liftMotorBack = 8;
        //Fin
            private int finMotor = 6;
        //Weight
            private int batteryMotor = 3;
    //DI/Os
        //Lift
            private int liftLimitFrontUp = 3;
            private int liftLimitFrontDown = 4;
            private int liftLimitBackUp = 1;
            private int liftLimitBackDown = 2;
        //Fin
            private int finLimitForward = 6;
            private int finLimitBack = 5;
        //Weight
            private int batteryLimitFwd = 8;
            private int batteryLimitBck = 7;
            
    // Declare variables SO MANY VARIABLES!!!
    private int mode;
    private DriverStation ds;
    private AxisCamera camera;
    private Timer stopwatch;
    private Joystick rightStick, leftStick, operatorStick;
    
    private boolean recording;
    private Recorder recorder;
    private Loader loader;
    
    private Drive drive;
    private Lift lift;
    private Fin fin;
    private WeightShifter weight;
    
    //unused right now
    private ADXL345_I2C accel;
    private Gyro gyro;
    private Encoder batteryEncoder;

    // constructor for robot variables
    public Team869() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        
        // must get instance of these because you are not creating it
        // it already exists and we need to access it in our program
        // get the cRIO instance
        ds = DriverStation.getInstance();
        
        //camera setup in theory should just work because we are already doing LCD.update()
        camera = AxisCamera.getInstance();
        camera.writeResolution(AxisCamera.ResolutionT.k320x240);
        camera.writeBrightness(0);
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        drive = new Drive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor);
        lift = new Lift(LIFT, liftMotorFront, liftMotorBack, liftLimitFrontUp, liftLimitFrontDown, liftLimitBackUp, liftLimitBackDown);
        fin = new Fin(FINFWD, FINBACK, finMotor, finLimitForward, finLimitBack);
        weight = new WeightShifter(batteryMotor, batteryLimitFwd, batteryLimitBck);
        
        /******************************** USB  ********************************/
        // Define joysticks on the Driver Station
        rightStick = new Joystick(1);
        leftStick = new Joystick(2);
        operatorStick = new Joystick(3);
        
        /******************************** PWM  ********************************/
        //PORTS CAN BE FROM 1-10
        
        
        /**************************** DIGITAL I/O  ****************************/
        //PORTS CAN BE FROM 1-14
        
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
        LCD.print("Constructor Completed");
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private void drive(int percent) {
        drive.percentTankDrive(leftStick.getY(), rightStick.getY(), percent);
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
            if(!recording) {
                if(loader == null) {
                    loader = new Loader("file:///auto1.log");
                }
//                drive(loader.rDouble(),loader.rDouble());
            }
        } catch (EOFException ex) {
            LCD.print(6,"end of recording");
        } catch (IOException ex) {
            LCD.print(6,"playback error");
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
        // let the drivers know that we have initialized the robot
        LCD.print("Robot Initialized");
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
        
        LCD.update(); 
    }

    //do this while we are disabled
    protected void disabled() {
        // kill movement
        drive.stop();
        //kill lift
        lift.stop();
        //kill fin
        fin.stop();
        //kill battery
        weight.stop();
        //commenting out for now, very possibly could break things
//        calebrateGyro();
    }

    //run autonomous code for begining of match
    protected void autonomous() {
//        if(this.stopwatch.get() < 2) {
//            drive(100); //forward
//            setFinMotor(FWD); //push the fin all the way forward
//        } else {
            drive.stop();
            //drive(100); //forward
            fin.stop(); //push the fin all the way forward
//        }
        //kill lift
        lift.stop();
        //kill battery
        weight.stop();
        
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
            lift.down();
        } else if (rightStick.getRawButton(1) == true || leftStick.getRawButton(1) == true) {
            drive(50);
            lift.down();
        } else {
            drive(100);
            if(rightStick.getRawButton(2)) {
                lift.down();
            } else {
                lift.up();
            }
        }
        //fin code
        if (operatorStick.getRawButton(3)){
            fin.forward();
        } else if (operatorStick.getRawButton(2)) {
            fin.backward();
        } else {
            fin.stop();
        }
        
        //battery code
        if(operatorStick.getRawButton(1)) {
            weight.drive(BATTERY);
        } else {
            weight.drive(100);
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
                LCD.print("Disable " + stopwatch.get() + " sec"); //I ran out of lines to print to D:
                disabled(); // run disabled code
            } else if (isAutonomous() && isEnabled()) {
                changeMode(1);
                LCD.print("Auto " + stopwatch.get() + " sec");
                autonomous(); // run the autonomous code
            } else if (isOperatorControl() && isEnabled()) {
                changeMode(2);
                LCD.print("Teleop " + stopwatch.get() + " sec");
                teleoperated(); // run teleoperated code
            }
        } // end while loop SHOULD NEVER HAPPEN OR THE ROBOT IS DEAD
    }
    
    //mode changer for general robot modes
    private void changeMode(int newMode) {
        // if we were not in disabled before reset the stopwatch
        if (mode!=newMode){
            stopwatch.reset();
            mode = newMode; // set disabled state
        }
    }
}
