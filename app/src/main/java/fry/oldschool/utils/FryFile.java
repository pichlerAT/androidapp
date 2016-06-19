package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FryFile {

    protected int readIndex = 0;

    protected int readLineIndex = 0;

    protected char[] currentReadLine = null;

    protected StringBuilder currentWriteLine = new StringBuilder();

    protected ArrayList<String> saveLines = new ArrayList<>();

    public void save(File file) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter(file));

        for(String line : saveLines) {
            bw.write(line);
            bw.newLine();
        }

        bw.close();
    }

    public void load(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while((line = br.readLine()) != null) {
            saveLines.add(line);
        }

        br.close();
    }

    public boolean readNextLine() {
        if(readLineIndex < saveLines.size()) {
            readIndex = 0;
            currentReadLine = saveLines.get(readLineIndex++).toCharArray();
            return true;
        }
        currentReadLine = null;
        return false;
    }

    public byte getByte() {
        return (byte)currentReadLine[readIndex++];
    }

    public char getChar() {
        return currentReadLine[readIndex++];
    }

    public short getShort() {
        return (short)currentReadLine[readIndex++];
    }

    public int getInt() {
        return (currentReadLine[readIndex++] | (currentReadLine[readIndex++] << 16));
    }

    public String getString() {
        int length = currentReadLine[readIndex++];
        String str = "";
        for(int i=0; i<length; ++i) {
            str += currentReadLine[readIndex++];
        }
        return str;
    }
/*
    public byte[] getByteArray() {
        int length = currentReadLine[readIndex++];
        byte[] bytes = new byte[length];
        for(int i=0; i<length; ++i) {
            bytes[i] = getByte();
        }
        return bytes;
    }

    public char[] getCharArray() {
        int length = currentReadLine[readIndex++];
        char[] chars = new char[length];
        for(int i=0; i<length; ++i) {
            chars[i] = getChar();
        }
        return chars;
    }

    public short[] getShortArray() {
        int length = currentReadLine[readIndex++];
        short[] shorts = new short[length];
        for(int i=0; i<length; ++i) {
            shorts[i] = getShort();
        }
        return shorts;
    }

    public int[] getIntArray() {
        int length = currentReadLine[readIndex++];
        int[] ints = new int[length];
        for(int i=0; i<length; ++i) {
            ints[i] = getInt();
        }
        return ints;
    }

    public String[] getStringArray() {
        int length = currentReadLine[readIndex++];
        String[] strs = new String[length];
        for(int i=0; i<length; ++i) {
            strs[i] = getString();
        }
        return strs;
    }
*/
    public void newLine() {
        saveLines.add(currentWriteLine.toString());
        currentWriteLine = new StringBuilder();
    }

    public void write(byte b) {
        write((char)b);
    }

    public void write(char c) {
        currentWriteLine.append(c);
    }

    public void write(short s) {
        write((char)s);
    }

    public void write(int i) {
        write((char)i);
        write((char)(i >> 16));
    }

    public void write(String str) {
        write((char)str.length());
        currentWriteLine.append(str);
    }

    public void write(Fryable fry) {
        fry.writeTo(this);
    }

    public void write(byte[] b) {
        write((char)b.length);
        for(byte bi : b) {
            write(bi);
        }
    }

    public void write(char[] c) {
        write((char)c.length);
        for(char ci : c) {
            write(ci);
        }
    }

    public void write(short[] s) {
        write((char)s.length);
        for(short si : s) {
            write(si);
        }
    }

    public void write(int[] i) {
        write((char)i.length);
        for(int ii : i) {
            write(ii);
        }
    }

    public void write(String[] str) {
        write((char)str.length);
        for(String stri : str) {
            write(stri);
        }
    }

    public void write(Object[] fry) {
        int index = currentWriteLine.length();
        write((char)fry.length);
        int length = 0;
        for(Object obj : fry) {
            if(obj instanceof Fryable) {
                ((Fryable)obj).writeTo(this);
                ++length;
            }
        }
        currentWriteLine.setCharAt(index, (char)length);
    }

}
