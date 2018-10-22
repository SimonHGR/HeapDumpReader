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
