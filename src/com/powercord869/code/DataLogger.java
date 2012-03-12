/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code;

import java.io.*;
import javax.microedition.io.Connector;
/**
 * Data Logger Class
 * @author Jeremy Germita
 */
public class DataLogger {
    private DataLogger instance;
    private DataOutputStream output;

    /**
     * Constructor
     * @param fileName the filename to write to
     */
    public DataLogger(String fileName) throws IOException {
        output = Connector.openDataOutputStream(fileName);
    }

    /**
     * Get instance of datalogger
     * @param fileName the filename to write to
     * @return the instance of the DataLogger
     */
    public DataLogger getInstance(String fileName) throws IOException {
        if(instance == null) {
            instance = new DataLogger(fileName);
        }
        return instance;
    }

    /**
     * Print data, followed by a newline character to the output and flush it
     * @param data the data to print
     */
    public void print(String data) throws IOException {
        output.writeChars(data+"/n");
        output.flush();
    }
    
    public void wBoolean(boolean b) throws IOException {
        output.writeBoolean(b);
        output.flush();
    }
    
    public void wByte(byte b) throws IOException {
        output.writeByte(b);
        output.flush();
    }
    
    public void wChar(char c) throws IOException {
        output.writeChar(c);
        output.flush();
    }
    
    public void wDouble(double d) throws IOException {
        output.writeDouble(d);
        output.flush();
    }
    
    public void wFloat(float f) throws IOException {
        output.writeFloat(f);
        output.flush();
    }
    
    public void wInt(int i) throws IOException {
        output.writeInt(i);
        output.flush();
    }
    
    public void wLong(long l) throws IOException {
        output.writeLong(l);
        output.flush();
    }
    
    public void wShort(short s) throws IOException {
        output.writeShort(s);
        output.flush();
    }

    /**
     * Close the printStream
     */
    public void close() throws IOException {
        output.close();
    }

}