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
    ROOT_JNI_LOCAL(0x02, getJniLocalBlockBuilder()),
    ROOT_JAVA_FRAME(0x03, getFixedBlockBuilder(1, 8)), // was 1,8
    ROOT_NATIVE_STACK(0x04, getFixedBlockBuilder(1, 0)), // was 1,4
    ROOT_STICKY_CLASS(0x05, getFixedBlockBuilder(1, 4)),
    ROOT_THREAD_BLOCK(0x06, getFixedBlockBuilder(1, 4)),
    ROOT_MONITOR_USED(0x07, getFixedBlockBuilder(1, 0)),
    ROOT_THREAD_OBJECT(0x08, getFixedBlockBuilder(1, 8)),
    CLASS_DUMP(0x20, new ClassDumpRecordBuilder()),
    INSTANCE_DUMP(0x21, new InstanceDumpRecordBuilder()),
    OBJECT_ARRAY_DUMP(0x22, new ObjectArrayDumpRecordBuilder()),
    PRIMITIVE_ARRAY_DUMP(0x23, new PrimitiveArrayDumpRecordBuilder());


    // Create a flexible reader for the weird JNI block
    private static HeapDumpRecordBuilder getJniLocalBlockBuilder() {
        return (tag, is, strings, classes, objects) -> {
            Utils.debug("Building wobbly length ROOT_JNI_LOCAL Heap Dump block");
            try {
                long bytesRead = Utils.identifierSize + 8;
                is.skip(Utils.identifierSize);
                is.mark(16); // limit is controlled by buffer size, this limit's irrelevant really.
                is.skip(8); // go hunting at "normal" offset
                HeapDumpTag proposedTag = HeapDumpTag.ofID((int)Utils.readU1(is)); // read tag from there...
                is.reset(); //
                if (proposedTag == BAD_TAG) {
                    bytesRead -= 8;
                } else {
                    is.skip(8); // if what was found is good, wind us back to be ready to re-read the tag
                }
                final long totalBytesRead = bytesRead;

                return new HeapDumpRecord() {
                    @Override
                    public long getBytesRead() {
                        return totalBytesRead;
                    }

                    @Override
                    public String toString() {
                        return "Jiggly JNI record (total bytes = " + totalBytesRead + ") tag: " + tag;
                    }
                };
            } catch (IOException ioe) {
                throw new RuntimeException("Failed reading fixed size HeapDump block ", ioe);
            }
        };
    }

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
            if (t.value != 0) { // don't put BAD_TAG into the map :)
                lookup.put(t.value, t);
            }
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
