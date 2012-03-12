package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 * Helper to print to the driver station lcd
 * @author Michael Chinn
 */
public class LCD {
    // get the driver station instance
    private static DriverStationLCD dsLCD = DriverStationLCD.getInstance();
    
    //Driver Station Printing helper functions
    public static void print(String string) {print(1, string, 1);}
    public static void print(int line, String string) {
        StringBuffer s = new StringBuffer(string);
        s.setLength(DriverStationLCD.kLineLength); //21 currently
        printToCol(line, s.toString(), 1);
    }
    public static void print(int line, String string, int column) {
        printToCol(line,string,column);
    }
    private static void printToCol(int l, String s, int c) {
        switch (l) {
            case 6:
                dsLCD.println(DriverStationLCD.Line.kUser6, c, s);
                break;
            case 5:
                dsLCD.println(DriverStationLCD.Line.kUser5, c, s);
                break;
            case 4:
                dsLCD.println(DriverStationLCD.Line.kUser4, c, s);
                break;
            case 3:
                dsLCD.println(DriverStationLCD.Line.kUser3, c, s);
                break;
            case 2:
                dsLCD.println(DriverStationLCD.Line.kUser2, c, s);
                break;
            default:
                dsLCD.println(DriverStationLCD.Line.kMain6, c, s);
        }
    }
    public static void update() {
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD();
    }
}
