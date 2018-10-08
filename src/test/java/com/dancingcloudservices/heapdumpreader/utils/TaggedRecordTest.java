package com.dancingcloudservices.heapdumpreader.utils;

import com.dancingcloudservices.heapdumpreader.TaggedRecord;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class TaggedRecordTest {
    @Test
    public void testBuilder() throws Throwable {
        byte[] ba = {0x01,
                0, 0, 0, 100,
                0, 0, 0, 12,
                0, 0, 0, 0, 0, 0, 0, 99,
                'a', 'b', 'c', 'd'
        };
        InputStream is = new ByteArrayInputStream(ba);
        TaggedRecord tr = TaggedRecord.builder().source(is).build();
        Assert.assertEquals("Should be String type", Tag.STRING_UTF8, tr.getTag());
    }
}
