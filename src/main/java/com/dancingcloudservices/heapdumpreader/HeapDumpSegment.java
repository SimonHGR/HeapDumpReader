package com.dancingcloudservices.heapdumpreader;

import java.io.InputStream;
import java.util.Map;

public class HeapDumpSegment {
    public HeapDumpSegment(InputStream is, long dataLength,
                           Map<Long, StringRecord> strings,
                           Map<Long, ClassRecord> classes,
                           Map<Long, Long> objects) {
        System.err.println("Building a heapdump segment with length " + dataLength);
        /*
        need to:
         - keep track of how much of our alloted data has been read (so we know when we've finished processing
           our block)
         - Read the tag, then build the HeapDumpRecord (or just skip it if it's a dummy)
         - the builder for this probably needs to return the amount of file it read/skipped so
           the top level here can keep track of the remaining data that must be processed.
         - whenever we come across an instance record, it'll need to store that in the objects table.
           NOTE: don't actually want the objects, just to increment the count of instances per-class
         - I think the other HeapDumpRecord types are really only smart "skip this record" events.
         - I might need to keep track of arrays, as they can eat a lot of memory quite distinct from
           the objects they contain.
         */
    }
}
