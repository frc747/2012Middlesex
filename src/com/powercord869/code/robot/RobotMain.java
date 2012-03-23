//Drive motors are Victors, wheel lift motors are Jaguars.(Addison 2/12/12)
package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import java.io.EOFException;
import java.io.IOException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends RobotBase {
    //Constants    
    private final double BATTERY = .5;
    private final double FINFWD = .5;
    private final double FINBACK = -.4;
    private final double LIFT = 1;
    
    //PWMs
        //Drive
            private final int frontLeftMotor = 5;
            private final int rearLeftMotor = 6;
            private final int frontRightMotor = 1;
            private final int rearRightMotor = 2;
        //Lift
            private final int liftMotorFront = 8;
            private final int liftMotorBack = 7;
        //Fin
            private final int finMotor = 3;
        //Weight
            private final int batteryMotor = 4;
    //DI/Os
        //Lift
            private final int liftLimitFrontUp = 3;
            private final int liftLimitFrontDown = 4;
            private final int liftLimitBackUp = 2;
            private final int liftLimitBackDown = 1;
        //Fin
            private final int finLimitForward = 8;
            private final int finLimitBack = 9;
        //Weight
            private final int batteryLimitFwd = 10;
            private final int batteryLimitBck = 7;
        //Encoder
            private final int rightA = 11;
            private final int rightB = 12;
            private final int leftA = 13;
            private final int leftB = 14;
            
    //Other
        //Encoder Values
            private final double diameter = 6;
            private final double driveRatio = 26.0/12.0;
            private final double distance = 10;
            private final int ticks = 250;
            
    // Declare variables SO MANY VARIABLES!!!
    private int mode;
    public static DriverStation ds;
    public static AxisCamera camera;
    public static Timer stopwatch;
    public static Joystick leftStick, rightStick, operatorStick;
    public static FRCKinect kinect;
    
    private boolean recording;
    private Recorder recorder;
    private Loader loader;
    
    public static AutoDrive drive;
    public static Lift lift;
    public static Fin fin;
    public static WeightShifter weight;
    
    //autonomous flags
    private boolean prepLeft, prepRight;

    // constructor for robot variables
    public RobotMain() {
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
        camera.writeBrightness(50);
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        kinect = new FRCKinect();
        prepLeft = false;
        prepRight = false;
        
        drive = new AutoDrive(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor, rightA, rightB, leftA, leftB, diameter, driveRatio, distance, ticks);
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
        
        
        /***************************** ANALOG I/O *****************************/
        //PORTS CAN BE FROM 1-8
        
        
        /******************************* OTHER  *******************************/
        
        
        /****************************** COMPLETE ******************************/
        // let the drivers know that we have contructed the variables
        LCD.print("Constructor Completed");
    }
    
    //record different things about the robot
    private void record() {
        if(recording && recorder != null) {
            try {
                recorder.wDouble(leftStick.getY());
                recorder.wDouble(rightStick.getY());
            } catch (IOException ex) {
                //only stacktrace if we are not on the field
                if(!ds.isFMSAttached()) {
                    ex.printStackTrace();
                }
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
//                drive.tankDrive(loader.rDouble(),loader.rDouble());
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
        
        drive.start();
        
        // let the drivers know that we have initialized the robot
        LCD.print("Robot Initialized");
    }
    
    //common code to be run in all modes every cycle
    protected void common(){
        //makes sure we are not on the field
//        if(!ds.isFMSAttached()) {
//            try {
//            //if we press the button once start recording, press it again close the recording
//                if(operatorStick.getRawButton(11)) {
//                    if(!recording) {
//                        recorder = new Recorder("file:///auto1.log");
//                        recording = true;
//                    } 
//                } else if(operatorStick.getRawButton(10)) {
//                    if(recording) {
//                        recorder.close();
//                        recording = false;
//                    } 
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            recording = false;
//        }
        
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
        
        drive.reset();
    }

    //run autonomous code for begining of match
    protected void autonomous() {
        //if person is ready in front of kinect
        if(kinect.getEnabled()) {
            //left arm is left drive, right arm is right drive
            drive.tankDrive(kinect.getLeftY(),kinect.getRightY(),.25);
            
            // this IN THEORY:
            // should prep a kick by moving back then
            // make the fin go all the way forward or back depending on the leg kicked
            // we can keep the fin in that direction because the limits will stop it
            if(!prepRight && kinect.getBody(FRCKinect.rightLegBack)) {
                prepRight = true;
            } else if(prepRight && kinect.getBody(FRCKinect.rightLegForward)) {
                prepRight = false;
                fin.forward();
            } else if(!prepLeft && kinect.getBody(FRCKinect.leftLegBack)) {
                prepLeft = true;
            } else if(prepLeft && kinect.getBody(FRCKinect.leftLegForward)) {
                prepLeft = false;
                fin.backward();
            }
        } else {
            //if the person is not in front or not with arms out to their side kill drive
            fin.stop();
            drive.auto();
        }
        //kill everything else
        lift.stop();
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
            drive(.75);
            lift.down();
        } else if (rightStick.getRawButton(1) == true || leftStick.getRawButton(1) == true) {
            drive(.50);
            lift.down();
        } else {
            drive(1);
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
            driveBattery(BATTERY);
        } else {
            driveBattery(1);
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
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private void drive(double percent) {
        drive.tankDrive(leftStick.getY(), rightStick.getY(), percent);
    }
    
    //set our drive motor bounds by percent uniformly and drive the robot
    private boolean driveBattery(double percent) {
        //take joystick inputs and drive the battery
        return weight.move(operatorStick.getY(), percent);
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
