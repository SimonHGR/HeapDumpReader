package com.dancingcloudservices.heapdumpreader.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

public class MyBufferTest {
    @Test
    public void testSmallReadBigBuffer() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes());
        MyBufferedInputStream uut = new MyBufferedInputStream(source);
        byte [] dest = new byte[12];
        byte [] expected = "1234567890\u0000\u0000".getBytes();
        uut.read(dest, 0, 10);
        Assert.assertArrayEquals(expected, dest);
    }

    @Test
    public void testBigReadSmallBuffer() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes());
        MyBufferedInputStream uut = new MyBufferedInputStream(source, 8);
        byte [] dest = new byte[64];
        byte [] expected = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        int count = uut.read(dest, 0, 64);
        Assert.assertArrayEquals(expected, dest);
        Assert.assertEquals(62, count);
    }

    @Test
    public void testIncompleteReadSmallData() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("1234567890".getBytes());
        MyBufferedInputStream uut = new MyBufferedInputStream(source);
        byte [] dest = new byte[12];
        byte [] expected = "1234567890\u0000\u0000".getBytes();
        int count = uut.read(dest, 0, 12);
        Assert.assertArrayEquals(expected, dest);
        Assert.assertEquals(10, count);
    }

    private void arrayEqualsCount(byte[] expect, int expectOff, byte[] actual, int actualOff, int count) {
        for (int idx = 0; idx < count; idx++) {
            Assert.assertEquals(expect[expectOff + idx], actual[actualOff + idx]);
        }
    }

    @Test
    public void testArrayEqualsCount() {
        byte [] arrayOne = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        byte [] arrayTwo = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        byte [] arrayThree = "0234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        byte [] arrayFour = "xxxABCDEFGHIJKLMNOPQRSTUVWXYZxxx".getBytes();
        arrayEqualsCount(arrayOne, 0, arrayTwo, 0, 64);
        arrayEqualsCount(arrayOne, 10, arrayFour, 3, 26);
        boolean success = false;
        try {
            arrayEqualsCount(arrayOne, 0, arrayThree, 0, 64);
        } catch (Throwable t) {
            success = true;
        }
        Assert.assertTrue(success);
    }

    @Test
    public void testMarkReset() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes());
        byte [] expected = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        MyBufferedInputStream uut = new MyBufferedInputStream(source, 8);
        byte [] dest = new byte[64];
        int count = uut.read(dest, 0, 10);
//        System.err.println("Read " + new String(dest, 0, 10));
        arrayEqualsCount(expected, 0, dest, 0, 10);
        uut.mark(6);
        count = uut.read(dest, 0, 6);
//        System.err.println("Read " + new String(dest, 0, 6));
        arrayEqualsCount(expected, 10, dest, 0, 6);
        uut.reset();
        count = uut.read(dest, 0, 20);
//        System.err.println("Read " + new String(dest, 0, 20));
        arrayEqualsCount(expected, 10, dest, 0, 20);
    }

    @Test
    public void testMarkResetSkip() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".getBytes());
        byte [] expected = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\u0000\u0000".getBytes();
        MyBufferedInputStream uut = new MyBufferedInputStream(source, 8);
        byte [] dest = new byte[64];
        uut.read(dest, 0, 10);
        uut.mark(6);
        uut.skip(6);
        uut.reset();
        uut.read(dest, 0, 20);
//        System.err.println("Read " + new String(dest, 0, 20));
        arrayEqualsCount(expected, 10, dest, 0, 20);
    }
//    @Test
//    public void testStringReader() throws IOException {
//        StringReader sr = new StringReader("12345");
//        char [] buff = new char[10];
//        int count = sr.read(buff, 0, 10);
//        Assert.assertEquals(5, count);
//        char [][]buffs = new char[10][2];
//        Assert.assertEquals(2, buffs.length);
//    }
}
