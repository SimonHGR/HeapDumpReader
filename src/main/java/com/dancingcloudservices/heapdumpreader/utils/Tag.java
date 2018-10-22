package com.dancingcloudservices.heapdumpreader.utils;

import com.dancingcloudservices.heapdumpreader.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum Tag {
    BAD_TAG(0xFF, null),
    STRING_UTF8(0x01, StringRecord::new),
    LOAD_CLASS(0x02, ClassRecord::new),
    UNLOAD_CLASS(0x03, null),
    STACK_FRAME(0x04, null),
    STACK_TRACE(0x05, null),
    ALLOC_SITES(0x06, null),
    HEAP_SUMMARY(0x07, null),
    START_THREAD(0x0A, null),
    END_THREAD(0x0B, null),
    HEAP_DUMP(0x0C, null),
    HEAP_DUMP_SEGMENT(0x1C, HeapDumpSegment::new),
    HEAP_DUMP_END(0x2C, null),
    CPU_SAMPLES(0x0D, null),
    CONTROL_SETTINGS(0x0E, null)
    ;
    private static Map<Integer, Tag> lookup = new HashMap<>();
    static {
        for (Tag t : values()) {
            lookup.put(t.value, t);
        }
    }

    public static Tag ofID(int id) {
        Tag t = lookup.get(id);
        if (t != null) return t;
        else return BAD_TAG;
    }


    public final int value;
    public final RecordBuilder builder;

    private Tag(int value, RecordBuilder rb) {
        this.value = value;
        builder =  rb;
    }

}
