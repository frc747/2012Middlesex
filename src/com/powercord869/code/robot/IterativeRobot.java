package com.powercord869.code.robot;

import com.sun.squawk.GC;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.communication.FRCControl;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * This class is the main class for our robot code, startCompetition is called when cRIO is turned on
 * @author programing
 */
public abstract class IterativeRobot extends RobotBase {

    private final static boolean TRACE_LOOP_ALLOCATIONS = false; // master tracing switch
    private final static boolean TRACE_LOOP_ALLOCATIONS_AFTER_INIT = true;  // trace before or after all phases initialize

    private boolean m_disabledInitialized;
    private boolean m_autonomousInitialized;
    private boolean m_teleopInitialized;
    private boolean m_testInitialized;
    
    private Timer stopwatch;
    
    public double getStopwatchTime() {
        return stopwatch.get();
    }

    /**
     * Constructor for RobotIterativeBase
     *
     * The constructor initializes the instance variables for the robot to indicate
     * the status of initialization for disabled, autonomous, and teleop code.
     */
    public IterativeRobot() {
        // set status for initialization of disabled, autonomous, and teleop code.
        m_disabledInitialized = false;
        m_autonomousInitialized = false;
        m_teleopInitialized = false;
        m_testInitialized = false;
        stopwatch = new Timer();
    }

    /**
     * Provide an alternate "main loop" via startCompetition().
     *
     */
    public void startCompetition() {
        UsageReporting.report(UsageReporting.kResourceType_Framework, UsageReporting.kFramework_Iterative);

        robotInit();

        // tracing support:
        final int TRACE_LOOP_MAX = 100;
        int loopCount = TRACE_LOOP_MAX;
        Object marker = null;
        boolean didDisabledPeriodic = false;
        boolean didAutonomousPeriodic = false;
        boolean didTeleopPeriodic = false;
        boolean didTestPeriodic = false;
        if (TRACE_LOOP_ALLOCATIONS) {
            GC.initHeapStats();
            if (!TRACE_LOOP_ALLOCATIONS_AFTER_INIT) {
                System.out.println("=== Trace allocation in competition loop! ====");
                marker = new Object(); // start counting objects before any loop starts - includes class initialization
            }
        }

        // loop forever, calling the appropriate mode-dependent function
        LiveWindow.setEnabled(false);
        while (true) {
            if (TRACE_LOOP_ALLOCATIONS && didDisabledPeriodic && didAutonomousPeriodic && didTeleopPeriodic && --loopCount <= 0) {
                System.out.println("!!!!! Stop loop!");
                break;
            }
            // Call the appropriate function depending upon the current robot mode
            if (isDisabled()) {
                // call DisabledInit() if we are now just entering disabled mode from
                // either a different mode or from power-on
                if (!m_disabledInitialized) {
                    LiveWindow.setEnabled(false);
                    restartStopwatch();
                    disabledInit();
                    m_disabledInitialized = true;
                    // reset the initialization flags for the other modes
                    m_autonomousInitialized = false;
                    m_teleopInitialized = false;
                    m_testInitialized = false;
                }
                if (nextPeriodReady()) {
                    FRCControl.observeUserProgramDisabled();
                    disabledPeriodic();
                    didDisabledPeriodic = true;
                }
            } else if (isTest()) {
                // call TestInit() if we are now just entering test mode from either
                // a different mode or from power-on
                if (!m_testInitialized) {
                    LiveWindow.setEnabled(true);
                    restartStopwatch();
                    testInit();
                    m_testInitialized = true;
                    m_autonomousInitialized = false;
                    m_teleopInitialized = false;
                    m_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                    FRCControl.observeUserProgramTest();
                    testPeriodic();
                    didTestPeriodic = true;
                }
            } else if (isAutonomous()) {
                // call Autonomous_Init() if this is the first time
                // we've entered autonomous_mode
                if (!m_autonomousInitialized) {
                    LiveWindow.setEnabled(false);
                    // KBS NOTE: old code reset all PWMs and relays to "safe values"
                    // whenever entering autonomous mode, before calling
                    // "Autonomous_Init()"
                    restartStopwatch();
                    autonomousInit();
                    m_autonomousInitialized = true;
                    m_testInitialized = false;
                    m_teleopInitialized = false;
                    m_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                    getWatchdog().feed();
                    FRCControl.observeUserProgramAutonomous();
                    autonomousPeriodic();
                    didAutonomousPeriodic = true;
                }
            } else {
                // call Teleop_Init() if this is the first time
                // we've entered teleop_mode
                if (!m_teleopInitialized) {
                    LiveWindow.setEnabled(false);
                    restartStopwatch();
                    teleopInit();
                    m_teleopInitialized = true;
                    m_testInitialized = false;
                    m_autonomousInitialized = false;
                    m_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                    getWatchdog().feed();
                    FRCControl.observeUserProgramTeleop();
                    teleopPeriodic();
                    didTeleopPeriodic = true;
                }
            }
            LCD.update();
            if (TRACE_LOOP_ALLOCATIONS && TRACE_LOOP_ALLOCATIONS_AFTER_INIT &&
                    didDisabledPeriodic && didAutonomousPeriodic && didTeleopPeriodic && loopCount == TRACE_LOOP_MAX) {
                System.out.println("=== Trace allocation in competition loop! ====");
                marker = new Object(); // start counting objects after 1st loop completes - ignore class initialization
            }
            Timer.delay(0.001);
            m_ds.waitForData();
        }
        if (TRACE_LOOP_ALLOCATIONS) {
            GC.printHeapStats(marker, false);
        }
    }
    
    /**
     * restarts the stopwatch
     */
    private void restartStopwatch() {
        stopwatch.reset();
        stopwatch.start();
    }

    /**
     * Determine if the appropriate next periodic function should be called.
     * Call the periodic functions whenever a packet is received from the Driver Station, or about every 20ms.
     */
    private boolean nextPeriodReady() {
        return m_ds.isNewControlData();
    }

    /**
     * Robot-wide initialization code should go here. It will be called exactly 1 time.
     */
    public abstract void robotInit();

    /**
     * Initialization code for disabled mode should go here.
     */
    public abstract void disabledInit();

    /**
     * Initialization code for autonomous mode should go here.
     */
    public abstract void autonomousInit();

    /**
     * Initialization code for teleop mode should go here.
     */
    public abstract void teleopInit();
    
    /**
     * Initialization code for test mode should go here.
     */
    public abstract void testInit();
    
    /**
     * Periodic code for disabled mode should go here.
     */
    public abstract void disabledPeriodic();

    /**
     * Periodic code for autonomous mode should go here.
     */
    public abstract void autonomousPeriodic();

    /**
     * Periodic code for teleop mode should go here.
     */
    public abstract void teleopPeriodic();
    
    /**
     * Periodic code for test mode should go here
     */
    public abstract void testPeriodic();
}
