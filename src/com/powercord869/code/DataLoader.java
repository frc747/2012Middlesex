/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powercord869.code;

import java.io.*;
import javax.microedition.io.Connector;

/**
 *
 * @author mechinn
 */
public class DataLoader {
    private DataInputStream input;
    /**
     * Constructor
     * @param fileName the filename to write to
     */
    public DataLoader(String fileName) throws IOException {
        input = Connector.openDataInputStream(fileName);
    }
    
    public String read() throws IOException, EOFException {
        char c;
        String s = "";
        while((c = input.readChar()) != '\n') {
            s+=c;
        }
        return s;
    }
    
    public int rInt() throws IOException, EOFException {
        return input.readInt();
    }
    
    public double rDouble() throws IOException, EOFException {
        return input.readDouble();
    }
    
    public byte rByte() throws IOException, EOFException {
        return input.readByte();
    }
    
    public boolean rBoolean() throws IOException, EOFException {
        return input.readBoolean();
    }
    
    public char rChar() throws IOException, EOFException {
        return input.readChar();
    }
    
    public float rFloat() throws IOException, EOFException {
        return input.readFloat();
    }
    
    public short rShort() throws IOException, EOFException {
        return input.readShort();
    }
}
