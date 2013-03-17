package com.powercord869.code.robot;

import edu.wpi.first.wpilibj.DriverStationLCD;

/**
 * Helper to print to the driver station lcd
 * @author mechinn
 */
public class LCD {
    private static final String tagging = ": ";
    
    // get the driver station instance
    private static final DriverStationLCD dsLCD = DriverStationLCD.getInstance();
    
    /**
     * Print string on line 1
     * @param string 
     */
    public static void print(String string) {
        print(1, string, 1);
    }
    /**
     * Print tag and string on line 1
     * @param tag
     * @param string 
     */
    public static void print(String tag, String string) {
        print(tag+tagging+string);
    }
    /**
     * print string and tag on given line number
     * @param line
     * @param tag
     * @param string 
     */
    public static void print(int line, String tag, String string) {
        print(line,tag+tagging+string);
    }
    /**
     * print string on line number
     * @param line
     * @param string 
     */
    public static void print(int line, String string) {
        StringBuffer s = new StringBuffer(string);
        s.setLength(DriverStationLCD.kLineLength); //21 currently
        printToCol(line, s.toString(), 1);
    }
    /**
     * print tag and string on given line number starting at given column
     * @param line
     * @param tag
     * @param string
     * @param column 
     */
    public static void print(int line, String tag, String string, int column) {
        print(line,tag+tagging+string,column);
    }
    /**
     * print string on given line number starting at given column
     * @param line
     * @param string
     * @param column 
     */
    public static void print(int line, String string, int column) {
        printToCol(line,string,column);
    }
    
    private static void printToCol(int l, String s, int c) {
        switch (l) {
            default:
            case 1:
                dsLCD.println(DriverStationLCD.Line.kUser1, c, s);
                break;
            case 2:
                dsLCD.println(DriverStationLCD.Line.kUser2, c, s);
                break;
            case 3:
                dsLCD.println(DriverStationLCD.Line.kUser3, c, s);
                break;
            case 4:
                dsLCD.println(DriverStationLCD.Line.kUser4, c, s);
                break;
            case 5:
                dsLCD.println(DriverStationLCD.Line.kUser5, c, s);
                break;
            case 6:
                dsLCD.println(DriverStationLCD.Line.kUser6, c, s);
                break;
        }
    }
    /**
     * Must be called to display any prints
     */
    public static void update() {
        // actually print all of the dsPrint strings, send to driver computer
        dsLCD.updateLCD();
    }
}
