package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class InstanceDumpRecordBuilder implements HeapDumpRecordBuilder {
    @Override
    public HeapDumpRecord build(HeapDumpTag tag, InputStream is,
                                Map<Long, StringRecord> strings,
                                Map<Long, ClassRecord> classes,
                                Map<Long, Long> objects) {
        long bytesConsumed = 0;
        try {
            long objectId = Utils.readIdentifier(is);
            int stackTraceSerial = (int)Utils.readU4(is);
            long classObjectId = Utils.readIdentifier(is);
//            Utils.debug("Found object of type ", classes.get(classObjectId));
            int bytesToSkip = (int)Utils.readU4(is);
            bytesConsumed = Utils.identifierSize * 2 + 8;
            is.skip(bytesToSkip);
            bytesConsumed += bytesToSkip;
            objects.compute(classObjectId, (k, v) -> v == null ? 1 : v + 1);
        } catch (IOException ioe) {
            throw new RuntimeException("Fatal, can't read file", ioe);
        }
//        throw new RuntimeException("Instance Dump not yet handled");
        final long totalBytesConsumed = bytesConsumed;
        return new HeapDumpRecord() {
            @Override
            public long getBytesRead() {
                return totalBytesConsumed;
            }
        };
    }
}
