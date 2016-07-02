package fry.oldschool.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FryFile {

    protected int readIndex = 0;

    protected char[] readLine = null;

    protected StringBuilder writeLine = new StringBuilder();

    public void saveUTF8(File file) throws IOException {
        OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8").newEncoder());
        os.write(writeLine.toString());
        os.close();
    }

    public void save(File file) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter(file));
        bw.write(writeLine.toString());
        bw.close();
    }

    public void loadUTF8(File file) throws IOException {
        InputStreamReader is = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8").newDecoder());

        int bred;
        char[] buffer = new char[1024];
        String bufferLine = "";

        while((bred = is.read(buffer)) != -1) {
            if(bred < buffer.length) {
                for(int k=0; k<bred; ++k) {
                    bufferLine += buffer[k];
                }
            }else {
                bufferLine += buffer;
            }
        }
        readLine = bufferLine.toCharArray();
        /*
        for(char c : readLine) {
            System.out.println(c + "|" + (int)c);
        }
        */
        is.close();
    }

    public void load(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        readLine = br.readLine().toCharArray();
        br.close();
    }

    public boolean moreToRead() {
        return (readLine != null && readIndex < readLine.length);
    }

    public byte getByte() {
        return (byte)getChar();
    }

    public char getChar() {
        return readLine[readIndex++];
    }

    public short getShort() {
        return (short)getChar();
    }

    public int getInt() {
        return (getChar() | (getChar() << 16));
    }

    public String getString() {
        int length = getChar();
        String str = "";
        for(int k=0; k<length; ++k) {
            str += getChar();
        }
        return str;
    }

    public byte[] getByteArray() {
        byte[] b = new byte[getChar()];
        for(int k=0; k<b.length; ++k) {
            b[k] = getByte();
        }
        return b;
    }

    public char[] getCharArray() {
        char[] c = new char[getChar()];
        for(int k=0; k<c.length; ++k) {
            c[k] = getChar();
        }
        return c;
    }

    public short[] getShortArray() {
        short[] s = new short[getChar()];
        for(int k=0; k<s.length; ++k) {
            s[k] = getShort();
        }
        return s;
    }

    public int[] getIntArray() {
        int[] i = new int[getChar()];
        for(int k=0; k<i.length; ++k) {
            i[k] = getInt();
        }
        return i;
    }

    public String[] getStringArray() {
        String[] str = new String[getChar()];
        for(int k=0; k<str.length; ++k) {
            str[k] = getString();
        }
        return str;
    }

    public void write(byte b) {
        write((char)b);
    }

    public void write(char c) {
        writeLine.append(c);
    }

    public void write(short s) {
        write((char)s);
    }

    public void write(int i) {
        write((char)i);
        write((char)(i >> 16));
    }

    public void write(final String str) {
        write((char)str.length());
        writeLine.append(str);
    }

    public void write(final Fryable fry) {
        fry.writeTo(this);
    }

    public void write(final byte[] b) {
        write((char)b.length);
        for(byte bi : b) {
            write(bi);
        }
    }

    public void write(final char[] c) {
        write((char)c.length);
        for(char ci : c) {
            write(ci);
        }
    }

    public void write(final short[] s) {
        write((char)s.length);
        for(short si : s) {
            write(si);
        }
    }

    public void write(final int[] i) {
        write((char)i.length);
        for(int ii : i) {
            write(ii);
        }
    }

    public void write(final String[] str) {
        write((char)str.length);
        for(final String stri : str) {
            write(stri);
        }
    }

    public void write(final Fryable[] fry) {
        write((char)fry.length);
        for(final Fryable f : fry) {
            f.writeTo(this);
        }
    }

    public void write(final Object[] fry) {
        int index = writeLine.length();
        write((char)fry.length);
        int length = 0;
        for(final Object obj : fry) {
            if(obj instanceof Fryable) {
                ((Fryable)obj).writeTo(this);
                ++length;
            }
        }
        writeLine.setCharAt(index, (char)length);
    }

    public void write(final ArrayList<?> list) {
        int index = writeLine.length();
        write((char)list.size());
        int length = 0;
        for(final Object obj : list) {
            if(obj instanceof Fryable) {
                ((Fryable)obj).writeTo(this);
                ++length;
            }
        }
        writeLine.setCharAt(index, (char)length);
    }

    public void write(final SearchableList<?> list) {
        int index = writeLine.length();
        write((char)list.baseLength());
        int length = 0;
        for(int i=0; i<list.baseLength(); ++i) {
            Object obj = list.getBase(i);
            if(obj instanceof Fryable) {
                ((Fryable)obj).writeTo(this);
                ++length;
            }
        }
        writeLine.setCharAt(index, (char)length);
    }

}
