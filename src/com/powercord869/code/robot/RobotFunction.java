package com.powercord869.code.robot;

/**
 * All functions of the robot need these methods
 * @author Michael Chinn
 */
public abstract class RobotFunction {
    //Constants, must not have duplicate hardware ports
        //PWMs
            //Drive
                protected final int frontLeftMotorPWM = 5;
                protected final int rearLeftMotorPWM = 6;
                protected final int frontRightMotorPWM = 1;
                protected final int rearRightMotorPWM = 2;
            //Lift
                protected final int liftMotorFrontPWM = 8;
                protected final int liftMotorBackPWM = 7;
            //Fin
                protected final int finMotorPWM = 3;
            //Weight
                protected final int weightMotorPWM = 4;
        //DI/Os
            //Lift
                protected final int liftLimitFrontUpDIO = 3;
                protected final int liftLimitFrontDownDIO = 4;
                protected final int liftLimitBackUpDIO = 2;
                protected final int liftLimitBackDownDIO = 1;
            //Fin
                protected final int finLimitForwardDIO = 8;
                protected final int finLimitBackDIO = 9;
            //Weight
                protected final int weightLimitFwdDIO = 10;
                protected final int weightLimitBckDIO = 7;
            //Encoder
                protected final int rightADIO = 11;
                protected final int rightBDIO = 12;
                protected final int leftADIO = 13;
                protected final int leftBDIO = 14;
    /**
     * every function should have a simple way to stop it for disabled mode
     */
    public abstract void stop();
}
