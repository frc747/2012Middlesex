package com.powercord869.code.robot;

import com.powercord869.code.robot.singletons.*;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import java.util.Vector;

/**
 * This class is the main class for our robot code, startCompetition is called when cRIO is turned on
 */
public class RobotMain extends RobotBase {
    private int mode;
    private final Timer stopwatch;
    private final Drive drive;
    private final Lift lift;
    private final Fin fin;
    private final WeightShifter weight;
    private final Autonomous auto;
    private Vector funcitons, controlable;

    // constructor for robot variables
    public RobotMain() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        
        //vectors to keep all our functions in so we can iterate through them later
        funcitons = new Vector();
        controlable = new Vector();
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        //robot functions
        drive = Drive.getInstance();
        funcitons.addElement(drive);
        controlable.addElement(drive);
        
        lift = Lift.getInstance();
        funcitons.addElement(lift);
        
        fin = Fin.getInstance();
        funcitons.addElement(fin);
        controlable.addElement(fin);
        
        weight = WeightShifter.getInstance();
        funcitons.addElement(weight);
        controlable.addElement(weight);
        
        //autonomous setup
        auto = Autonomous.getInstance();
        
        /****************************** COMPLETE ******************************/
        // let the drivers know that we have contructed the variables
        LCD.print("Constructor Completed");
    }
    
    /**
     * Actions which would be performed once (and only once) upon initialization of the
     * robot would be put here.
     */
    protected void robotInit() {
        stopwatch.start();
        auto.start();
        
        // let the drivers know that we have initialized the robot
        LCD.print("Robot Initialized");
    }
    
    /**
     * common code to be run in all modes every cycle
     */
    protected void common(){
        auto.encoderVals();
        LCD.update(); 
    }

    /**
     * do this while we are disabled
     */
    protected void disabled() {
        for(int i = 0; i<funcitons.size();++i) {
            ((RobotFunction)funcitons.elementAt(i)).stop();
        }
        
        auto.reset();
    }

    /**
     * run autonomous code for begining of match
     */
    protected void autonomous() {
        auto.auto();
    }

    /**
     * run code for drivers
     */
    protected void teleoperated() {
        for(int i = 0; i<controlable.size();++i) {
            ((RobotControlable)controlable.elementAt(i)).control();
        }
    }

    /**
     * main function from our perspective, this is what the robot calls when starting
     */
    public void startCompetition() {
        // first and one-time initialization
        robotInit();
        //loop FOREVER!
        while (true) {
            common(); // run common code
            if (isDisabled()) {
                changeMode(0);
                LCD.print("Disable " + stopwatch.get() + " sec");
                disabled(); // run disabled code
                Timer.delay(0.001);
            } else if (isAutonomous() && isEnabled()) {
                changeMode(1);
                LCD.print("Auto " + stopwatch.get() + " sec");
                autonomous(); // run the autonomous code
                Timer.delay(0.001);
            } else if (isOperatorControl() && isEnabled()) {
                changeMode(2);
                LCD.print("Teleop " + stopwatch.get() + " sec");
                teleoperated(); // run teleoperated code
                Timer.delay(0.001);
            } else {
                LCD.print("Unknown " + stopwatch.get() + " sec");
                Timer.delay(0.001);
            }
        } // end while loop SHOULD NEVER HAPPEN OR THE ROBOT IS DEAD
    }
    
    /**
     * mode changer for general robot modes
     * @param newMode if not already in mode reset stopwatch and remember new mode
     */
    private void changeMode(int newMode) {
        // if we were not in disabled before reset the stopwatch
        if (mode!=newMode){
            stopwatch.reset();
            mode = newMode; // set disabled state
        }
    }
}
