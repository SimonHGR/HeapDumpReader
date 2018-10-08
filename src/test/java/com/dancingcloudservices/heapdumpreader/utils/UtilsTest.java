package com.dancingcloudservices.heapdumpreader.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class UtilsTest {
    @Test
    public void testReadNullTermString() throws Throwable {
        String text = "Hello";
        byte[] ba = new byte[text.length() + 1];
        System.arraycopy("Hello".getBytes(), 0, ba, 0, text.length());

        InputStream is = new ByteArrayInputStream(ba);
        String s = Utils.readNullTermString(is);
        Assert.assertEquals("Should read " + text, text, s);
    }

    @Test
    public void testU4() throws Throwable {
        byte[] ba = {0, 0, 0, 10};
        InputStream is = new ByteArrayInputStream(ba);
        Assert.assertEquals("Should be 10", 10, Utils.readU4(is));
    }

    @Test
    public void testU8() throws Throwable {
        byte[] ba = {0, 0, 0, 0, 0, 0, 16, 0};
        InputStream is = new ByteArrayInputStream(ba);
        Assert.assertEquals("Should be 4096", 4096, Utils.readU8(is));
    }
}
