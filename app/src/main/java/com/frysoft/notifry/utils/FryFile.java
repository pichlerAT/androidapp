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

    protected int readIndex = 0;

    protected int version = 0;

    public abstract boolean saveToStream(OutputStream outputStream);

    public abstract boolean loadFromStream(InputStream inputStream);

    public abstract boolean loadFromString(String str);

    public abstract int size();

    public abstract byte readByte();

    public abstract char readChar();

    public abstract short readShort();

    public abstract int readInt();

    public abstract long readLong();

    public abstract String readString();

    public abstract int readArrayLength();

    public abstract void writeByte(byte b);

    public abstract void writeChar(char c);

    public abstract void writeShort(short s);

    public abstract void writeInt(int i);

    public abstract void writeLong(long l);

    public abstract void writeString(String s);

    public abstract void writeArrayLength(int length);

    public abstract void writeFryables(final Fryable[] fry);

    public abstract void writeObjects(final Object[] fry);

    public abstract void writeObjects(final ArrayList<?> list);

    public abstract void writeObjects(final SearchableList<?> list);

    public abstract void writeUnsignedByte(byte b);

    public abstract void writeUnsignedShort(short s);

    public abstract void writeUnsignedInt(int i);

    public abstract void writeUnsignedLong(long l);

    public abstract String getWrittenString();

    public abstract byte readUnsignedByte();

    public abstract short readUnsignedShort();

    public abstract int readUnsignedInt();

    public abstract boolean hasNext();

    public abstract int getWrittenLength();

    public final int readId() {
        return readUnsignedInt();
    }

    public final void writeId(int id) {
        writeUnsignedInt(id);
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public boolean save(File file) {
        File dir = file.getParentFile();
        if(!dir.exists() && !dir.mkdirs()) {
            return false;
        }
        try{
            return saveToStream(new FileOutputStream(file));
        } catch(FileNotFoundException ex) {
            return false;
        }
    }

    public boolean load(File file) {
        if(!file.exists()) {
            return false;
        }
        try{
            return loadFromStream(new FileInputStream(file));
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
        byte[] b = new byte[readArrayLength()];
        for (int k = 0; k < b.length; ++k) {
            b[k] = readByte();
        }
        return b;
    }

    public char[] readCharArray() {
        char[] c = new char[readArrayLength()];
        for (int k = 0; k < c.length; ++k) {
            c[k] = readChar();
        }
        return c;
    }

    public short[] readShortArray() {
        short[] s = new short[readArrayLength()];
        for (int k = 0; k < s.length; ++k) {
            s[k] = readShort();
        }
        return s;
    }

    public int[] readIntArray() {
        int[] i = new int[readArrayLength()];
        for (int k = 0; k < i.length; ++k) {
            i[k] = readInt();
        }
        return i;
    }

    public String[] readStringArray() {
        String[] str = new String[readArrayLength()];
        for (int k = 0; k < str.length; ++k) {
            str[k] = readString();
        }
        return str;
    }

    public String readDecoded(String code, int codeOffset) {
        if(codeOffset >= code.length()) {
            codeOffset = code.length() % codeOffset;
        }

        String str = readString();
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

    public void writeFryable(final Fryable fry) {
        fry.writeTo(this);
    }

    public void writeByteArray(final byte[] b) {
        writeArrayLength(b.length);
        for (byte bi : b) {
            writeByte(bi);
        }
    }

    public void writeCharArray(final char[] c) {
        writeArrayLength(c.length);
        for (char ci : c) {
            writeChar(ci);
        }
    }

    public void writeShortArray(final short[] s) {
        writeArrayLength(s.length);
        for (short si : s) {
            writeShort(si);
        }
    }

    public void writeIntArray(final int[] i) {
        writeArrayLength(i.length);
        for (int ii : i) {
            writeInt(ii);
        }
    }

    public void writeStringArray(final String[] str) {
        writeArrayLength(str.length);
        for (String stri : str) {
            writeString(stri);
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
        writeString(string);
    }



    public static class Compact extends FryFile {

        protected char[] readLine = null;

        protected StringBuilder writeLine = new StringBuilder();

        protected void writeChars(char... c) {
            writeLine.append(c);
        }

        @Override
        public boolean saveToStream(OutputStream outputStream) {
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
        public boolean loadFromStream(InputStream inputStream) {
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
        public boolean loadFromString(String str) {
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
        public byte readByte() {
            return (byte) readChar();
        }

        @Override
        public char readChar() {
            return readLine[readIndex++];
        }

        @Override
        public short readShort() {
            return (short) readChar();
        }

        @Override
        public int readInt() {
            return (readChar() | (readChar() << 16));
        }

        @Override
        public long readLong() {
            return (readChar() | (readChar() << 16) | ((long) readChar() << 32) | ((long) readChar() << 48));
        }

        @Override
        public String readString() {
            char[] string= readCharArray();
            if(string.length == 1 && string[0] == 1) {
                return null;
            }
            return new String(string);
        }

        @Override
        public int readArrayLength() {
            return readChar();
        }

        @Override
        public void writeByte(byte b) {
            writeChar((char) b);
        }

        @Override
        public void writeChar(char c) {
            writeLine.append(c);
        }

        @Override
        public void writeShort(short s) {
            writeChar((char) s);
        }

        @Override
        public void writeInt(int i) {
            writeChars((char) i, (char) (i >> 16));
        }

        @Override
        public void writeLong(long l) {
            writeChars((char) l, (char) (l >> 16), (char) (l >> 32), (char) (l >> 48));
        }

        @Override
        public void writeString(String str) {
            if(str == null) {
                writeChars((char)1, (char)1);
            }else {
                writeArrayLength(str.length());
                writeLine.append(str);
            }
        }

        @Override
        public void writeArrayLength(int length) {
            writeChar((char)length);
        }

        @Override
        public void writeFryables(final Fryable[] fries) {
            writeArrayLength(fries.length);
            for (Fryable fry : fries) {
                fry.writeTo(this);
            }
        }

        @Override
        public void writeObjects(final Object[] fries) {
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
        public void writeObjects(final ArrayList<?> list) {
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
        public void writeObjects(final SearchableList<?> list) {
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
        public void writeUnsignedByte(byte b) {
            writeByte(b);
        }

        @Override
        public void writeUnsignedShort(short s) {
            writeShort(s);
        }

        @Override
        public void writeUnsignedInt(int i) {
            writeInt(i);
        }

        @Override
        public void writeUnsignedLong(long l) {
            writeLong(l);
        }

        @Override
        public String getWrittenString() {
            return writeLine.toString();
        }

        @Override
        public byte readUnsignedByte() {
            return readByte();
        }

        @Override
        public short readUnsignedShort() {
            return readShort();
        }

        @Override
        public int readUnsignedInt() {
            return readInt();
        }

        @Override
        public boolean hasNext() {
            return (readLine != null && readIndex < readLine.length);
        }

        @Override
        public int getWrittenLength() {
            return writeLine.length();
        }

    }



    public static class Split extends FryFile {

        public static final int COMPILER_DEFAULT = 0;

        public static final int COMPILER_INCLUDES_COMPACT = 1;

        protected static final String emptyString = "" + (char)0;

        protected final String splitString;

        protected final int compile_mode;

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
        public boolean saveToStream(OutputStream outputStream) {
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
        public boolean loadFromStream(InputStream inputStream) {
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
        public boolean loadFromString(String str) {
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
        public byte readByte() {
            return Byte.parseByte(readLine.get(readIndex++));
        }

        @Override
        public char readChar() {
            return readLine.get(readIndex++).charAt(0);
        }

        @Override
        public short readShort() {
            return Short.parseShort(readLine.get(readIndex++));
        }

        @Override
        public int readInt() {
            return Integer.parseInt(readLine.get(readIndex++));
        }

        @Override
        public long readLong() {
            return Long.parseLong(readLine.get(readIndex++));
        }

        @Override
        public String readString() {
            String s = readLine.get(readIndex++);

            if(s.length() == 1 && s.charAt(0) == (char)1) {
                return null;
            }

            if(compile_mode == COMPILER_INCLUDES_COMPACT) {
                if(s.equals(emptyString)) {
                    return "";
                }
            }

            return s;
        }

        @Override
        public int readArrayLength() {
            return readInt();
        }

        @Override
        public void writeByte(byte b) {
            writeString("" + b);
        }

        @Override
        public void writeChar(char c) {
            if(writeLine.length() == 0) {
                writeLine += c;
            }else {
                writeLine += splitString + c;
            }
        }

        @Override
        public void writeShort(short s) {
            writeString("" + s);
        }

        @Override
        public void writeInt(int i) {
            writeString("" + i);
        }

        @Override
        public void writeLong(long l) {
            writeString("" + l);
        }

        @Override
        public void writeString(String s) {
            if(s == null) {
                writeLine += splitString + (char)1;

            }else {
                if (compile_mode == COMPILER_INCLUDES_COMPACT) {
                    if (s.isEmpty()) {
                        s += emptyString;
                    }
                }
                if (writeLine.length() == 0) {
                    writeLine = s;
                } else {
                    writeLine += splitString + s;
                }
            }
        }

        @Override
        public void writeArrayLength(int length) {
            writeInt(length);
        }

        @Override
        public void writeFryables(Fryable[] fry) {

        }

        @Override
        public void writeObjects(Object[] fry) {

        }

        @Override
        public void writeObjects(ArrayList<?> list) {

        }

        @Override
        public void writeObjects(SearchableList<?> list) {

        }

        @Override
        public void writeUnsignedByte(byte b) {
            writeShort((short)(b + (b < 0 ? 256 : 0)));
        }

        @Override
        public void writeUnsignedShort(short s) {
            writeInt(s + (s < 0 ? 65536 : 0));
        }

        @Override
        public void writeUnsignedInt(int i) {
            writeLong(i + (i < 0 ? 4294967296L : 0L));
        }

        @Override
        public void writeUnsignedLong(long l) {

        }

        @Override
        public String getWrittenString() {
            return writeLine;
        }

        @Override
        public byte readUnsignedByte() {
            return (byte) readShort();
        }

        @Override
        public short readUnsignedShort() {
            return (short) readInt();
        }

        @Override
        public int readUnsignedInt() {
            return (int) readLong();
        }

        @Override
        public boolean hasNext() {
            return (readLine != null && readIndex < readLine.size());
        }

        @Override
        public int getWrittenLength() {
            return writeLine.length();
        }

        public Compact getCompact() {
            Compact comp = new Compact();
            comp.loadFromString(readLine.get(readIndex++));
            return comp;
        }

    }

    public static class Tag extends FryFile {

        protected String readLine;

        @Override
        public boolean saveToStream(OutputStream outputStream) {
            return false;
        }

        @Override
        public boolean loadFromStream(InputStream inputStream) {
            return false;
        }

        @Override
        public boolean loadFromString(String str) {
            readLine = str;
            readIndex = 0;
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public byte readByte() {
            return 0;
        }

        @Override
        public char readChar() {
            return readLine.charAt(readIndex++);
        }

        @Override
        public short readShort() {
            return 0;
        }

        @Override
        public int readInt() {
            return 0;
        }

        @Override
        public long readLong() {
            int v = 1;
            char c = readLine.charAt(readIndex);
            if(c == '+') {
                c = readLine.charAt(readIndex);
            }else if(c == '-') {
                c = readLine.charAt(readIndex);
                v = -1;
            }

            long n = 0;
            int i;
            while((i = getNumber(c)) != -1) {
                n = 10 * n + i;
                c = readLine.charAt(readIndex++);
            }

            return (n * v);
        }

        @Override
        public String readString() {
            return null;
        }

        @Override
        public int readArrayLength() {
            return 0;
        }

        @Override
        public void writeByte(byte b) {

        }

        @Override
        public void writeChar(char c) {

        }

        @Override
        public void writeShort(short s) {

        }

        @Override
        public void writeInt(int i) {

        }

        @Override
        public void writeLong(long l) {

        }

        @Override
        public void writeString(String s) {

        }

        @Override
        public void writeArrayLength(int length) {

        }

        @Override
        public void writeFryables(Fryable[] fry) {

        }

        @Override
        public void writeObjects(Object[] fry) {

        }

        @Override
        public void writeObjects(ArrayList<?> list) {

        }

        @Override
        public void writeObjects(SearchableList<?> list) {

        }

        @Override
        public void writeUnsignedByte(byte b) {

        }

        @Override
        public void writeUnsignedShort(short s) {

        }

        @Override
        public void writeUnsignedInt(int i) {

        }

        @Override
        public void writeUnsignedLong(long l) {

        }

        @Override
        public String getWrittenString() {
            return null;
        }

        @Override
        public byte readUnsignedByte() {
            return 0;
        }

        @Override
        public short readUnsignedShort() {
            return 0;
        }

        @Override
        public int readUnsignedInt() {
            return 0;
        }

        @Override
        public boolean hasNext() {
            return (readIndex < readLine.length());
        }

        @Override
        public int getWrittenLength() {
            return 0;
        }

        protected int getNumber(char c) {
            if(c > 47 && c < 58) {
                return (c - 48);
            }
            return -1;
        }

    }

}
