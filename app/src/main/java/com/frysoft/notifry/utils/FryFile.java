package com.frysoft.notifry.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

public abstract class FryFile {

    public abstract boolean save(OutputStream outputStream);

    public abstract boolean load(InputStream inputStream);

    public abstract boolean load(String str);

    public abstract int size();

    public abstract byte getByte();

    public abstract char getChar();

    public abstract short getShort();

    public abstract int getInt();

    public abstract String getString();

    public abstract int getArrayLength();

    public abstract void write(byte b);

    public abstract void write(char c);

    public abstract void write(short s);

    public abstract void write(int i);

    public abstract void write(long l);

    public abstract void write(String s);

    public abstract void writeArrayLength(int length);

    public abstract void write(final Fryable[] fry);

    public abstract void write(final Object[] fry);

    public abstract void write(final ArrayList<?> list);

    public abstract void write(final SearchableList<?> list);

    public abstract void writeSigned(byte b);

    public abstract void writeSigned(short s);

    public abstract void writeSigned(int i);

    public abstract String getWrittenString();

    public boolean save(File file) {
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) {
            return false;
        }
        try{
            return save(new FileOutputStream(file));
        } catch(FileNotFoundException ex) {
            return false;
        }
    }

    public boolean load(File file) {
        if(!file.exists()) {
            return false;
        }
        try{
            return load(new FileInputStream(file));
        } catch(FileNotFoundException ex) {
            return false;
        }
    }

    public boolean loadFile(String filePath) {
        return load(new File(filePath));
    }

    public boolean saveFile(String filePath) {
        return save(new File(filePath));
    }

    public byte[] getByteArray() {
        byte[] b = new byte[getArrayLength()];
        for (int k = 0; k < b.length; ++k) {
            b[k] = getByte();
        }
        return b;
    }

    public char[] getCharArray() {
        char[] c = new char[getArrayLength()];
        for (int k = 0; k < c.length; ++k) {
            c[k] = getChar();
        }
        return c;
    }

    public short[] getShortArray() {
        short[] s = new short[getArrayLength()];
        for (int k = 0; k < s.length; ++k) {
            s[k] = getShort();
        }
        return s;
    }

    public int[] getIntArray() {
        int[] i = new int[getArrayLength()];
        for (int k = 0; k < i.length; ++k) {
            i[k] = getInt();
        }
        return i;
    }

    public String[] getStringArray() {
        String[] str = new String[getArrayLength()];
        for (int k = 0; k < str.length; ++k) {
            str[k] = getString();
        }
        return str;
    }

    public String getDecoded(String code, int codeOffset) {
        if(codeOffset >= code.length()) {
            codeOffset = code.length() % codeOffset;
        }

        String str = getString();
        String string = "";
        int codeIndex = codeOffset;

        for(int i=0; i<str.length(); ++i) {
            string += (char)((int)str.charAt(i) + 61 - (int)code.charAt(codeIndex++));

            if(codeIndex >= code.length()) {
                codeIndex = 0;
            }
        }
        return string;
    }

    public void write(final Fryable fry) {
        fry.writeTo(this);
    }

    public void write(final byte[] b) {
        writeArrayLength(b.length);
        for (byte bi : b) {
            write(bi);
        }
    }

    public void write(final char[] c) {
        writeArrayLength(c.length);
        for (char ci : c) {
            write(ci);
        }
    }

    public void write(final short[] s) {
        writeArrayLength(s.length);
        for (short si : s) {
            write(si);
        }
    }

    public void write(final int[] i) {
        writeArrayLength(i.length);
        for (int ii : i) {
            write(ii);
        }
    }

    public void write(final String[] str) {
        writeArrayLength(str.length);
        for (String stri : str) {
            write(stri);
        }
    }

    public void writeEncoded(String str, String code, int codeOffset) {
        if(codeOffset >= code.length()) {
            codeOffset = code.length() % codeOffset;
        }

        String string = "";
        int codeIndex = codeOffset;

        for(int i=0; i<str.length(); ++i) {
            string += (char)((int)str.charAt(i) + (int)code.charAt(codeIndex) - 61);

            ++codeIndex;
            if(codeIndex >= code.length()) {
                codeIndex = 0;
            }
        }
        write(string);
    }



    public static class Compact extends FryFile {

        protected int readIndex = 0;

        protected char[] readLine = null;

        protected StringBuilder writeLine = new StringBuilder();

        @Override
        public boolean save(OutputStream outputStream) {
            try{
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8").newEncoder());
                writer.write(writeLine.toString());
                writer.close();

                return true;
            } catch(IOException ex) {
                return false;
            }
        }

        @Override
        public boolean load(InputStream inputStream) {
            try{
                InputStreamReader is = new InputStreamReader(inputStream, Charset.forName("UTF-8").newDecoder());

                int bred;
                char[] buffer = new char[1024];
                String bufferLine = "";

                while ((bred = is.read(buffer)) != -1) {
                    if (bred < buffer.length) {
                        for (int k = 0; k < bred; ++k) {
                            bufferLine += buffer[k];
                        }
                    } else {
                        bufferLine += new String(buffer);
                    }
                }
                readLine = bufferLine.toCharArray();
                is.close();

                return true;
            } catch(IOException ex) {
                return false;
            }
        }

        @Override
        public boolean load(String str) {
            readLine = str.toCharArray();
            return true;
        }

        @Override
        public int size() {
            if(readLine == null) {
                return writeLine.length();
            }
            return readLine.length;
        }

        @Override
        public byte getByte() {
            return (byte) getChar();
        }

        @Override
        public char getChar() {
            return readLine[readIndex++];
        }

        @Override
        public short getShort() {
            return (short) getChar();
        }

        @Override
        public int getInt() {
            return (getChar() | (getChar() << 16));
        }

        @Override
        public String getString() {
            return new String(getCharArray());
        }

        @Override
        public int getArrayLength() {
            return getChar();
        }

        @Override
        public void write(byte b) {
            write((char) b);
        }

        @Override
        public void write(char c) {
            writeLine.append(c);
        }

        @Override
        public void write(short s) {
            write((char) s);
        }

        @Override
        public void write(int i) {
            write((char) i);
            write((char) (i >> 16));
        }

        @Override
        public void write(long l) {
            write((char) l);
            write((char) (l >> 16));
            write((char) (l >> 32));
            write((char) (l >> 48));
        }

        @Override
        public void write(String str) {
            write((char) str.length());
            writeLine.append(str);
        }

        @Override
        public void writeArrayLength(int length) {
            write((char)length);
        }

        @Override
        public void write(final Fryable[] fries) {
            write((char) fries.length);
            for (Fryable fry : fries) {
                fry.writeTo(this);
            }
        }

        @Override
        public void write(final Object[] fries) {
            int index = writeLine.length();
            writeArrayLength(fries.length);
            int length = 0;
            for (Object obj : fries) {
                if (obj instanceof Fryable) {
                    ((Fryable) obj).writeTo(this);
                    ++length;
                }
            }
            writeLine.setCharAt(index, (char) length);
        }

        @Override
        public void write(final ArrayList<?> list) {
            int index = writeLine.length();
            writeArrayLength(list.size());
            int length = 0;
            for (Object obj : list) {
                if (obj instanceof Fryable) {
                    ((Fryable) obj).writeTo(this);
                    ++length;
                }
            }
            writeLine.setCharAt(index, (char) length);
        }

        @Override
        public void write(final SearchableList<?> list) {
            int index = writeLine.length();
            writeArrayLength(list.baseLength());
            int length = 0;
            for (int i = 0; i < list.baseLength(); ++i) {
                Object obj = list.getBase(i);
                if (obj instanceof Fryable) {
                    ((Fryable) obj).writeTo(this);
                    ++length;
                }
            }
            writeLine.setCharAt(index, (char) length);
        }

        @Override
        public void writeSigned(byte b) {
            write(b);
        }

        @Override
        public void writeSigned(short s) {
            write(s);
        }

        @Override
        public void writeSigned(int i) {
            write(i);
        }

        @Override
        public String getWrittenString() {
            return writeLine.toString();
        }

    }



    public static class Split extends FryFile {

        public static final int COMPILER_DEFAULT = 0;

        public static final int COMPILER_INCLUDES_COMPACT = 1;

        protected static final String emptyString = "" + (char)0;

        protected final String splitString;

        protected final int compile_mode;

        protected int readIndex = 0;

        protected ArrayList<String> readLine = null;

        protected String writeLine = "";

        public Split(String splitString, int compile_mode) {
            this.splitString = splitString;
            this.compile_mode = compile_mode;
        }

        public Split(String splitString) {
            this(splitString, COMPILER_DEFAULT);
        }

        public Split(char splitChar, int compile_mode) {
            this("" + splitChar, compile_mode);
        }

        public Split(char splitChar) {
            this(splitChar, COMPILER_DEFAULT);
        }

        @Override
        public boolean save(OutputStream outputStream) {
            try{
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8").newEncoder());
                writer.write(writeLine);
                writer.close();

                return true;
            } catch(IOException ex) {
                return false;
            }
        }

        @Override
        public boolean load(InputStream inputStream) {
            try{
                InputStreamReader is = new InputStreamReader(inputStream, Charset.forName("UTF-8").newDecoder());

                int bred;
                char[] buffer = new char[1024];
                String bufferString = "";
                readLine = new ArrayList<>(100);

                char[] split = splitString.toCharArray();
                int matches = 0;

                switch(compile_mode) {

                    case COMPILER_DEFAULT:
                        while ((bred = is.read(buffer)) != -1) {
                            for (int k = 0; k < bred; ++k) {

                                if (buffer[k] == split[matches]) {
                                    matches++;

                                } else {
                                    bufferString += buffer[k];
                                    matches = 0;
                                }

                                if (matches == split.length) {
                                    matches = 0;
                                    readLine.add(bufferString);
                                    bufferString = "";
                                }

                            }
                        }

                    case COMPILER_INCLUDES_COMPACT:
                        while ((bred = is.read(buffer)) != -1) {
                            for (int k = 0; k < bred; ++k) {

                                if (bufferString.length() > 0 && buffer[k] == split[matches]) {
                                    matches++;

                                } else {
                                    bufferString += buffer[k];
                                    matches = 0;
                                }

                                if (matches == split.length) {
                                    matches = 0;
                                    readLine.add(bufferString);
                                    bufferString = "";
                                }

                            }
                        }


                }

                is.close();

                readLine.add(bufferString);
                readLine.trimToSize();

                return true;

            } catch(IOException ex) {
                return false;
            }
        }

        @Override
        public boolean load(String str) {
            String bufferString = "";
            readLine = new ArrayList<>(100);

            char[] split = splitString.toCharArray();
            int matches = 0;

            for (int k = 0; k < str.length(); ++k) {
                char c = str.charAt(k);

                if(bufferString.length() > 0 && c == split[matches]) {
                    matches++;

                }else {
                    bufferString += c;
                    matches = 0;
                }

                if(matches == split.length) {
                    matches = 0;
                    readLine.add(bufferString);
                    bufferString = "";
                }

            }
            readLine.add(bufferString);

            readLine.trimToSize();

            return true;
        }

        @Override
        public int size() {
            if(readLine == null) {
                return writeLine.length();
            }
            return readLine.size();
        }

        @Override
        public byte getByte() {
            return Byte.parseByte(readLine.get(readIndex++));
        }

        @Override
        public char getChar() {
            return readLine.get(readIndex++).charAt(0);
        }

        @Override
        public short getShort() {
            return Short.parseShort(readLine.get(readIndex++));
        }

        @Override
        public int getInt() {
            return (int)Long.parseLong(readLine.get(readIndex++));
        }

        @Override
        public String getString() {
            String s = readLine.get(readIndex++);

            if(compile_mode == COMPILER_INCLUDES_COMPACT) {
                if(s.equals(emptyString)) {
                    return "";
                }
            }

            return s;
        }

        @Override
        public int getArrayLength() {
            return getInt();
        }

        @Override
        public void write(byte b) {
            write("" + b);
        }

        @Override
        public void write(char c) {
            writeLine += c + splitString;
        }

        @Override
        public void write(short s) {
            write("" + s);
        }

        @Override
        public void write(int i) {
            write("" + i);
        }

        @Override
        public void write(long l) {
            write("" + l);
        }

        @Override
        public void write(String s) {
            if(compile_mode == COMPILER_INCLUDES_COMPACT) {
                if (s.isEmpty()) {
                    s += emptyString;
                }
            }
            writeLine += s + splitString;
        }

        @Override
        public void writeArrayLength(int length) {
            write(length);
        }

        @Override
        public void write(Fryable[] fry) {

        }

        @Override
        public void write(Object[] fry) {

        }

        @Override
        public void write(ArrayList<?> list) {

        }

        @Override
        public void write(SearchableList<?> list) {

        }

        @Override
        public void writeSigned(byte b) {
            write((short)(b + (b < 0 ? 256 : 0)));
        }

        @Override
        public void writeSigned(short s) {
            write(s + (s < 0 ? 65536 : 0));
        }

        @Override
        public void writeSigned(int i) {
            write(i + (i < 0 ? 4294967296L : 0L));
        }

        @Override
        public String getWrittenString() {
            return writeLine;
        }

        public Compact getCompact() {
            Compact comp = new Compact();
            comp.load(readLine.get(readIndex++));
            return comp;
        }

    }

}
