package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ClassRecord extends Record {

    public final long objectId;
    public final String name;

    public ClassRecord(InputStream is, long dataLength,
                       Map<Long, StringRecord> strings,
                       Map<Long, ClassRecord> classes,
                       Map<Long, Long> objects) {
//        Utils.debug("building ClassRecord from length " + dataLength);

        long objectIdToUse = -1;
        long nameIDToUse = -1;
        try {
            Utils.readU4(is); // dump class serial number
            objectIdToUse = Utils.readIdentifier(is); // dump object id
            Utils.readU4(is); // dump stack trace serial
            nameIDToUse = Utils.readIdentifier(is);
        } catch (IOException ioe) {
            System.err.println("Problem reading (faking it??)");
        }
        objectId = objectIdToUse;
        name = strings.getOrDefault(nameIDToUse, new StringRecord(-1, "Unknown")).value;
    }

    @Override
    public String toString() {
        return "Class record, : " + name;
    }

}
