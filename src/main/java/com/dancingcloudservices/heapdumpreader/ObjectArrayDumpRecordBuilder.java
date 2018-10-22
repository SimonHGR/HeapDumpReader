package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;

import java.io.InputStream;
import java.util.Map;

public class ObjectArrayDumpRecordBuilder implements HeapDumpRecordBuilder{
    @Override
    public HeapDumpRecord build(HeapDumpTag tag, InputStream is, Map<Long, StringRecord> strings, Map<Long, ClassRecord> classes, Map<Long, Long> objects) {
        throw new RuntimeException("ObjectArray not yet handled");
    }
}
