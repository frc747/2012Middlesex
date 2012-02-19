//Chinn's streamlined Simple Robot Template, ill comment stuff on the bus :D
//Drive motors are Victors, wheel lift motors are Jaguars.(Addison 2/12/12)
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.ADXL345_I2C;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends RobotBase {

    // Declare variable for the robot drive system
    RobotDrive m_robotDrive;		// robot will use PWM 1-4 for drive motors

    // camera variable
    //AxisCamera camera;
    // cRIO info
    DriverStation ds;
    // timer (stopwatch)
    Timer clock;
    // output to driver console
    DriverStationLCD dsLCD;

    // Declare variables for the 3 joysticks (right/left for tank, 3rd stick for the operator
    Joystick m_rightStick, m_leftStick, m_operatorStick;
    
    // limit switch variable
    DigitalInput limitSwitch1;
    DigitalInput limitSwitch2;
    DigitalInput limitSwitch3;
    DigitalInput limitSwitch4;
    
    Victor m_motor1;
    Victor m_motor2;
    Victor m_motor3;
    Victor m_motor4;
    Jaguar m_liftmotor1;
    Jaguar m_liftmotor2;
    
    ADXL345_I2C accelerometer;
    
    Gyro gyro;
	
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

    // constructor for robot variables
    public RobotTemplate() {
        super(); // Invokes the RobotBase constructor [aka private RobotBase()]

        // Create a robot using standard right/left robot drive on PWMS 1, 2, 3, and #4
        
       // m_robotDrive = new RobotDrive(1,2,3,4);
        
        //m_robotDrive.setExpiration(15);

        // Define joysticks being used at USB port #1 and USB port #2 on the Driver Station
        m_rightStick = new Joystick(1);
        m_leftStick = new Joystick(2);
        m_operatorStick = new Joystick(3);
        
        
        m_motor1 = new Victor(1);
        m_motor2 = new Victor(2);
        m_motor3 = new Victor(3);
        m_motor4 = new Victor(4);
        m_liftmotor1 = new Jaguar(5);
        m_liftmotor2 = new Jaguar(6);
        
        m_robotDrive = new RobotDrive(m_motor1, m_motor2, m_motor3, m_motor4);

        // must get instance of these because you are not creating it
        // it already exists and we need to access it in our program

        // get the cRIO instance
        ds = DriverStation.getInstance();
        
        // get the driver station instance
        dsLCD = DriverStationLCD.getInstance();

        // get the camera instance
        //camera = AxisCamera.getInstance();
		
        // create a stopwatch timer
        clock = new Timer();
        
        limitSwitch1 = new DigitalInput(1);
        limitSwitch2 = new DigitalInput(2);
        limitSwitch3 = new DigitalInput(3);
        limitSwitch4 = new DigitalInput(4);
        
        accelerometer = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k16G);
        
        gyro = new Gyro(1);
        // let the drivers know that we have contructed the variables
       dsPrint("Constructor Completed");
    }
    
    /**
     * Robot-wide initialization code should go here.
     *
     * Default Robot-wide initialization which will
     * be called when the robot is first powered on.
     *
     * Called exactly 1 time when the competition starts.
     */
    protected void robotInit() {				
        // Actions which would be performed once (and only once) upon initialization of the
        // robot would be put here.
        

        // let the drivers know that we have initialized the robot
      dsPrint("Robot Initialized");
    }

    /**
     * Disabled should go here.
     * Run code that should run while the field is disabled.
     *
     * Called repeatedly while the robot is in the disabled state.
     */
    protected void disabled() {
        // while disabled, printout the duration of current disabled mode in seconds
       dsPrint(2,"Disabled seconds: " + clock.get());

        // kill drive
        m_robotDrive.tankDrive(0,0);
    }
	
    protected void autonomous() {
	dsPrint(2,"Autonomous seconds: " + clock.get());
                m_robotDrive.tankDrive(0,0);
	}

    /**
     * Operator control (tele-operated) code should go here.
     * Users should add Operator Control code to this method that should run while the field is
     * in the Operator Control (tele-operated) period.
     *
     * Called repeatedly while the robot is in the operator-controlled state.
     */
    public void operatorControl() {
        dsPrint(2,"Operator seconds: " + clock.get());
        
       
        if (m_rightStick.getRawButton(1) == true && m_leftStick.getRawButton(1) == true) {
            //Reduce the maximum speed allowed to go to motors for balancing
                m_rightStick.getAxis(Joystick.AxisType.kY);
//                m_motor1.enableDeadbandElimination(true); //hopefully removes the deadband from pwm control
                m_motor1.setBounds(160, 129, 128, 127, 96);
//                m_motor3.enableDeadbandElimination(true);
                m_motor3.setBounds(160, 129, 128, 127, 96);
//                m_rightStick.getAxis(Joystick.AxisType.kY);
//                m_motor2.enableDeadbandElimination(true);
                m_motor2.setBounds(160, 129, 128, 127, 96);
//                m_motor4.enableDeadbandElimination(true);
                m_motor4.setBounds(160, 129, 128, 127, 96);
        
                dsPrint(3,"25% speed");
        }
        else{
               m_motor1.setBounds(255, 129, 128, 127, 0);
               m_motor2.setBounds(255, 129, 128, 127, 0);
               m_motor3.setBounds(255, 129, 128, 127, 0);
               m_motor4.setBounds(255, 129, 128, 127, 0);
               dsPrint(3,"100% speed");
        }     
                
        m_robotDrive.tankDrive(m_leftStick, m_rightStick);
        if (m_operatorStick.getRawButton(1) == true) {
            limitSwitch1.get();
            limitSwitch2.get();
            limitSwitch3.get();
            limitSwitch4.get();
        }
        
        if (m_rightStick.getRawButton(3) == true && limitSwitch1.get() && limitSwitch2.get() ) {
            m_liftmotor1.set(255);
            m_liftmotor2.set(255);
        }
        if (m_rightStick.getRawButton(4) == true && limitSwitch3.get() && limitSwitch4.get() ) {
            m_liftmotor1.set(0);
            m_liftmotor2.set(0);
        }
//        if (m_leftStick.getRawButton(8) == true) {
            ADXL345_I2C.AllAxes axises = accelerometer.getAccelerations();
            dsPrint(4,axises.XAxis+" "+axises.YAxis+" "+axises.ZAxis);
//        }
            double gyroValue = gyro.getAngle();
            dsPrint(5,Double.toString(gyroValue));
                    
    }

    /**
     * Start a competition.
     * This code tracks the order of the field starting to ensure that everything happens
     * in the right order. Repeatedly run the correct method, either Autonomous or OperatorControl
     * when the robot is enabled. After running the correct method, wait for some state to change,
     * either the other mode starts or the robot is disabled. Then go back and wait for the robot
     * to be enabled again.
     */
    public void startCompetition() {
        // first and one-time initialization
        robotInit();
        dsLCD.updateLCD(); // actually print all of the dsPrint strings, send to driver computer
        int state = 0;

        while (true) {
            if (isDisabled()) {
                // if we were not in disabled before reset the stopwatch
                if (state!=1){
                    clock.stop();
                    clock.reset();
                    clock.start();
                }
                state = 1; // set disabled state
                disabled(); // run disabled code
            } else if (isAutonomous()) {
                // if we were not in autonomous before reset the stopwatch
                if (state!=2){
                    clock.stop();
                    clock.reset();
                    clock.start();
                }
                state = 2; // set autonomous state
                autonomous(); // run the autonomous code
            } else {
                // if we were not in teleoperated before reset the stopwatch
                if (state!=3){
                    clock.stop();
                    clock.reset();
                    clock.start();
                }
                state = 3; // set teleoperated state
                operatorControl(); // run teleoperated code
            }
//           dsLCD.println(1, , ERRORS_TO_DRIVERSTATION_PROP)
            dsLCD.updateLCD(); // actually print all of the dsPrint strings, send to driver computer
        } /* while loop */
    }
}
