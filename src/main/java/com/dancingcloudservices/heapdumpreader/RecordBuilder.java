package com.dancingcloudservices.heapdumpreader;

import java.io.InputStream;
import java.util.Map;

public interface RecordBuilder {
    Record build(InputStream is, long dataLength,
                 Map<Long, StringRecord> strings,
                 Map<Long, ClassRecord> classes,
                 Map<Long, Long> objects
    );
}
