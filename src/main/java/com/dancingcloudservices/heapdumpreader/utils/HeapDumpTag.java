package com.dancingcloudservices.heapdumpreader.utils;

import com.dancingcloudservices.heapdumpreader.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public enum HeapDumpTag {
    // tag number, RecordBuilder, number of IDs, number of additional bytes
    BAD_TAG(0xFF, (tag, is, strings, classes, objects) -> {
        throw new RuntimeException("Bad HeapDump Element tag");
    }),
    ROOT_UNKNOWN(0xFF, getFixedBlockBuilder(1, 0)),
    ROOT_JNI_GLOBAL(0x01, getFixedBlockBuilder(2, 0)),
    ROOT_JNI_LOCAL(0x02, getFixedBlockBuilder(1, 8)),
    ROOT_JAVA_FRAME(0x03, getFixedBlockBuilder(1, 8)),
    ROOT_NATIVE_STACK(0x04, getFixedBlockBuilder(1, 4)),
    ROOT_STICKY_CLASS(0x05, getFixedBlockBuilder(1, 4)),
    ROOT_THREAD_BLOCK(0x06, getFixedBlockBuilder(1, 4)),
    ROOT_MONITOR_USED(0x07, getFixedBlockBuilder(1, 0)),
    ROOT_THREAD_OBJECT(0x08, getFixedBlockBuilder(1, 8)),
    CLASS_DUMP(0x20, new ClassDumpRecordBuilder()),
    INSTANCE_DUMP(0x21, new InstanceDumpRecordBuilder()),
    OBJECT_ARRAY_DUMP(0x22, new ObjectArrayDumpRecordBuilder()),
    PRIMITIVE_ARRAY_DUMP(0x23, new PrimitiveArrayDumpRecordBuilder());

    private static HeapDumpRecordBuilder getFixedBlockBuilder(int idCount, int byteCount) {
        return (tag, is, strings, classes, objects) -> {
            Utils.debug("Building fixed length Heap Dump block, tag is " + tag);
            try {
                final long totalBytesRead = Utils.identifierSize * idCount + byteCount;
                is.skip(totalBytesRead);
                return new HeapDumpRecord() {
                    @Override
                    public long getBytesRead() {
                        return totalBytesRead;
                    }

                    @Override
                    public String toString() {
                        return "HeapDump fixed record, tag: " + tag;
                    }
                };
            } catch (IOException ioe) {
                throw new RuntimeException("Failed reading fixed size HeapDump block ", ioe);
            }
        };
    }

    private static Map<Integer, HeapDumpTag> lookup = new HashMap<>();

    static {
        for (HeapDumpTag t : values()) {
            lookup.put(t.value, t);
        }
    }

    public static HeapDumpTag ofID(int id) {
        HeapDumpTag t = lookup.get(id);
        if (t != null) return t;
        else return BAD_TAG;
    }


    public final int value;
    public final HeapDumpRecordBuilder builder;

    private HeapDumpTag(int value, HeapDumpRecordBuilder rb) {
        this.value = value;
        builder = rb;
    }
}
