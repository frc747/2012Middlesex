/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 *
 * @author programing
 */
public class LCD {
    // get the driver station instance
    private static DriverStationLCD dsLCD = DriverStationLCD.getInstance();
    
    //Driver Station Printing helper functions
    public static void print(String s) {print(1, s, 1);}
    public static void print(int line, String s) {print(line, s, 1);}
    public static void print(int line, String s, int column) {
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
    public static void update() {
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD();
    }
}
