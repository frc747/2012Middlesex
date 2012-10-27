package com.powercord869.code.robot;

import com.powercord869.code.robot.components.Autonomous;
import com.powercord869.code.robot.components.Drive;
import com.powercord869.code.robot.components.Fin;
import com.powercord869.code.robot.components.Lift;
import com.powercord869.code.robot.components.WeightShifter;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import java.util.Vector;

/**
 * This class is the main class for our robot code, startCompetition is called when cRIO is turned on
 * @author programing
 */
public class RobotMain extends RobotBase {
    private int mode;
    private final Timer stopwatch;
    private final Drive drive;
    private final Lift lift;
    private final Fin fin;
    private final WeightShifter weight;
    private final Autonomous auto;
    private Vector components, controlable;

    // constructor for robot variables
    public RobotMain() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]
        
        //variables
        mode = -1;
        
        //vectors to keep all our functions in so we can iterate through them later
        components = new Vector();
        controlable = new Vector();
        
        // create a stopwatch timer
        stopwatch = new Timer();
        
        //robot functions
        drive = Drive.getInstance();
        components.addElement(drive);
        controlable.addElement(drive);
        
        lift = Lift.getInstance();
        components.addElement(lift);
        
        fin = Fin.getInstance();
        components.addElement(fin);
        controlable.addElement(fin);
        
        weight = WeightShifter.getInstance();
        components.addElement(weight);
        controlable.addElement(weight);
        
        //autonomous setup
        auto = Autonomous.getInstance();
        components.addElement(auto);
        
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
        //loop through all our components and stop them for saftey reasons
        for(int i = 0; i<components.size();++i) {
            ((RobotFunction)components.elementAt(i)).stop();
        }
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
     * most likely will never need to be touched as all functional code is in functions above
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
            } else if (isAutonomous() && isEnabled()) {
                changeMode(1);
                LCD.print("Auto " + stopwatch.get() + " sec");
                autonomous(); // run the autonomous code
            } else if (isOperatorControl() && isEnabled()) {
                changeMode(2);
                LCD.print("Teleop " + stopwatch.get() + " sec");
                teleoperated(); // run teleoperated code
            } else {
                LCD.print("Unknown " + stopwatch.get() + " sec");
            }
            Timer.delay(0.001); //FIRST's fix for maxing CPU
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
