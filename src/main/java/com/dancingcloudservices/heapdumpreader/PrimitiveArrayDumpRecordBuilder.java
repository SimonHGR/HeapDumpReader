package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PrimitiveArrayDumpRecordBuilder implements HeapDumpRecordBuilder {
    @Override
    public HeapDumpRecord build(HeapDumpTag tag, InputStream is, Map<Long, StringRecord> strings, Map<Long, ClassRecord> classes, Map<Long, Long> objects) {
        final long totalBytesConsumed;
        try {
            long objectId = Utils.readIdentifier(is);
            int stackTraceSerial = (int)Utils.readU4(is);
            int elementCount = (int)Utils.readU4(is);
            int basicType = (int)Utils.readU1(is);
//            Utils.debug("Found array of BasicType " + basicType);
            int elementSize = Utils.basicTypeSizes[basicType];
            int bytesToSkip = elementSize * elementCount;
            is.skip(bytesToSkip);
            totalBytesConsumed = Utils.identifierSize + 9 + bytesToSkip;
        } catch (IOException ioe) {
            throw new RuntimeException("Fatal, can't read file", ioe);
        }
        //        throw new RuntimeException("PrimitiveArray not yet handled");
        return new HeapDumpRecord() {
            @Override
            public long getBytesRead() {
                return totalBytesConsumed;
            }
        };
    }

}
