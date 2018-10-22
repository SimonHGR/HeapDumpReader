package com.dancingcloudservices.heapdumpreader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

public class Utils {
    public static final boolean DEBUG = true;
    public static long identifierSize;
    public static boolean identifierSizeSet = false;

    public static void debug(String msg, Object... more) {
        if (DEBUG) {
            StringBuilder buff = new StringBuilder("DEBUG: " + msg);
            Arrays.asList(more).forEach(o -> {
                buff.append(", ");
                buff.append(o.toString());
            });
            System.err.println(buff.toString());
        }
    }

    public static String readNullTermString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int aByte = is.read();
        while (aByte > 0) {
            sb.append((char) aByte);
            aByte = is.read();
        }
        return sb.toString();
    }

    public static long readUbytes(InputStream is, int size) throws IOException {
        byte[] ba = new byte[size];
        is.read(ba);
        int counter = 0;
        long rv = 0;
        while (counter < size) {
            rv <<= 8;
            rv += ((int)ba[counter]) & 0x00FF;
            counter++;
        }
        return rv;
    }

    public static long readU8(InputStream is) throws IOException {
        return readUbytes(is, 8);
    }

    public static long readU4(InputStream is) throws IOException {
        return readUbytes(is, 4);
    }

    public static long readU2(InputStream is) throws IOException {
        return readUbytes(is, 2);
    }

    public static long readU1(InputStream is) throws IOException {
        return readUbytes(is, 1);
    }

    private static CharsetDecoder utf8Decoder = Charset.forName("UTF-8").newDecoder();

    public static String readUTF8(InputStream is, int count) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(count);
        for (int i = 0; i < count; i++) {
            bb.put((byte)is.read());
        }
        bb.flip();
        return utf8Decoder.decode(bb).toString();
//        try {
//            return utf8Decoder.decode(bb).toString();
//        } catch (IOException e) {
//            String asAscii = new String(bb.array(), 0);
//            System.err.println("ByteBufferfailed to convert to UTF-8. As ASCII looks like: " + asAscii + " ");
//            System.err.println("Exception is " + e.getMessage());
//            throw e;
//        }
    }

    public static void setIdentifierSize(long size) {
        if (identifierSizeSet) throw new IllegalStateException("Can only set identifier size once");
        identifierSize = size;
        identifierSizeSet = true;
    }

    public static long readIdentifier(InputStream is) throws IOException {
        if (!identifierSizeSet) throw new IllegalStateException("Can't read identifier before size is known");
        return readUbytes(is, (int)identifierSize);
    }
}
