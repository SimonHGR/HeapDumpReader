package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ObjectArrayDumpRecordBuilder implements HeapDumpRecordBuilder{
    @Override
    public HeapDumpRecord build(HeapDumpTag tag, InputStream is, Map<Long, StringRecord> strings, Map<Long, ClassRecord> classes, Map<Long, Long> objects) {
        final long totalBytesConsumed;
        try {
            long objectId = Utils.readIdentifier(is);
            int stackTraceSerial = (int)Utils.readU4(is);
            int elementCount = (int)Utils.readU4(is);
            long classObjectId = Utils.readIdentifier(is);
//            Utils.debug("Found array of type ", classes.get(classObjectId));
            int bytesToSkip = (int)Utils.identifierSize * elementCount;
            is.skip(bytesToSkip);
            totalBytesConsumed = Utils.identifierSize * 2 + 8 + bytesToSkip;
        } catch (IOException ioe) {
            throw new RuntimeException("Fatal, can't read file", ioe);
        }
        //        throw new RuntimeException("Object Array not yet handled");
        return new HeapDumpRecord() {
            @Override
            public long getBytesRead() {
                return totalBytesConsumed;
            }
        };
    }
}
