package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.HeapDumpTag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HeapDumpSegment implements Record {
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

        // don't read past end of our allocated chunk (should end perfectly, might warrant an assertion)
        long heapRecordsProcessed = 0;
        while (dataLength > 0) {
            try {
                HeapDumpTag tag = HeapDumpTag.ofID((int)Utils.readU1(is));
                HeapDumpRecordBuilder builder = tag.builder;
                HeapDumpRecord record = builder.build(tag, is, strings, classes, objects);
                dataLength -= record.getBytesRead();
                heapRecordsProcessed++;
                // TODO Currently crashes after record 13570539
                if (heapRecordsProcessed > 13570530) {
                    Utils.debug("Heap records processed: " + heapRecordsProcessed);
                }
            } catch (IOException ioe) {
                // io exceptions are fatal to the entire remaining processing (they'll break the
                // synchronization of "how many more bytes" even if we get back on track
                throw new RuntimeException("Fatal, file reading failed", ioe);
            }
        }
    }
}
