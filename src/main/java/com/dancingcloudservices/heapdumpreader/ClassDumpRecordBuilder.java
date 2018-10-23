package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ClassDumpRecordBuilder implements HeapDumpRecordBuilder {
    @Override
    public HeapDumpRecord build(HeapDumpTag tag, InputStream is, Map<Long, StringRecord> strings, Map<Long, ClassRecord> classes, Map<Long, Long> objects) {
        // skip 7 ids and 1 U4 add 1 U4
        // then read another u4 which is the instance size
        long bytesConsumed = 0;
        try {
            long classObjectId = Utils.readIdentifier(is);
//            Utils.debug("Heap Record for Class " + classes.get(classObjectId));
            int stackTraceSerialNumber = (int) Utils.readU4(is);
            long superClassObject = Utils.readIdentifier(is);
//            Utils.debug("  - ParentClass " + classes.get(superClassObject));
            long classLoaderObject = Utils.readIdentifier(is);
            long signersObject = Utils.readIdentifier(is);
            long protectionDomainObject = Utils.readIdentifier(is);
            Utils.readIdentifier(is); // dummy
            Utils.readIdentifier(is); // dummy
            int instanceSize = (int) Utils.readU4(is); // is this size of one object of this class?
            int constantPoolEntries = (int) Utils.readU2(is);
            bytesConsumed = 7 * Utils.identifierSize + 10;
            bytesConsumed += skipConstantPoolEntries(constantPoolEntries, is);
            int staticFields = (int) Utils.readU2(is);
            bytesConsumed += skipStaticFieldEntries(staticFields, is) + 2;
            int instanceFields = (int) Utils.readU2(is);
            bytesConsumed += skipInstanceFieldEntries(instanceFields, is) + 2;
        } catch (IOException ioe) {
            throw new RuntimeException("Fatal, problem reading data", ioe);
        }
        final long totalBytesConsumed = bytesConsumed;
        return new HeapDumpRecord() {
            @Override
            public long getBytesRead() {
                return totalBytesConsumed;
            }
        }; // not keeping Class info
    }

    private static long skipInstanceFieldEntries(int count, InputStream is) throws IOException {
        long rv = count * (Utils.identifierSize + 1);
        while (count-- > 0) {
            long fieldNameStringID = Utils.readIdentifier(is);
            int fieldType = (int) Utils.readU1(is);
        }
        return rv;
    }

    private static long skipStaticFieldEntries(int count, InputStream is) throws IOException {
        long rv = 0;
        while (count-- > 0) {
            long fieldNameStringID = Utils.readIdentifier(is);
            int entryType = (int) Utils.readU1(is);
            long value = Utils.readBasicType(entryType, is);
            rv += Utils.identifierSize + 1 + Utils.basicTypeSizes[entryType];
        }
        return rv;
    }

    private static long skipConstantPoolEntries(int count, InputStream is) throws IOException {
        long rv = 0;
        while (count-- > 0) {
            int poolIndex = (int) Utils.readU2(is);
            int entryType = (int) Utils.readU1(is);
            long value = Utils.readBasicType(entryType, is);
            rv += 3 + Utils.basicTypeSizes[entryType];
        }
        return rv;
    }
}
