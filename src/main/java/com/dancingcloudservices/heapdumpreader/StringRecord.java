package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;

public class StringRecord extends Record {
    public final long id;
    public final String value;
    public StringRecord(long id, String value) {
        this.id = id;
        this.value = value;
    }
    public StringRecord(InputStream is, long dataLength,
                        Map<Long, StringRecord> strings,
                        Map<Long, ClassRecord> classes,
                        Map<Long, Long> objects) {
//        Utils.debug("building StringRecord from length " + dataLength);

        long idToUse = -1;
        String valueToUse = "Failed to read...";
        try {
            idToUse = Utils.readIdentifier(is);
        } catch (IOException ioe) {
            System.err.println("Problem reading StringID, faking it");
        }
        try {
            int charsToRead = (int) (dataLength - Utils.identifierSize);
            char[] chars = new char[charsToRead];
            valueToUse = Utils.readUTF8(is, charsToRead);
        } catch (IOException ioe) {
            System.err.println("Problem reading UTF-8 " + ioe.getMessage());
        }
        id = idToUse;
        value = valueToUse;
        strings.put(id, this);
    }

    @Override
    public String toString() {
        return "String record, id " + id + " text: " + value;
    }
}
