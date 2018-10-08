package com.dancingcloudservices.heapdumpreader;

import com.dancingcloudservices.heapdumpreader.utils.Tag;
import com.dancingcloudservices.heapdumpreader.utils.Utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HeapDumpMain {
    private static final Set<Tag> tagsToShow = Set.of(
//            Tag.STRING_UTF8,
            Tag.LOAD_CLASS,
//            Tag.UNLOAD_CLASS,
//            Tag.STACK_FRAME,
//            Tag.STACK_TRACE,
//            Tag.ALLOC_SITES,
//            Tag.HEAP_SUMMARY,
//            Tag.START_THREAD,
//            Tag.END_THREAD,
//            Tag.HEAP_DUMP,
//            Tag.HEAP_DUMP_SEGMENT,
//            Tag.HEAP_DUMP_END,
//            Tag.CPU_SAMPLES,
//            Tag.CONTROL_SETTINGS
            Tag.BAD_TAG
    );

    public static boolean showThisTag(Tag t) {
        return tagsToShow.contains(t);
    }

    public static boolean acceptableDumpFormat(String s) {
        return s.equals("JAVA PROFILE 1.0.2");
    }

    public static void main(String[] args) throws Throwable {
        Map<Long, StringRecord> stringRecordMap = new TreeMap<>();
        Map<Long, ClassRecord> classRecordMap = new TreeMap<>();

        String filename = args.length > 0 ? args[0]
                : "/media/simon/2cffd6bf-24ea-453a-acef-a5ca32fe8929/JAMF-Data/Week 1 - Data collection/Monday - 10-01-2018/std-pagetia1-tc-4/std-pagetia1-tc-4_monday.dump";
        FileInputStream fis = new FileInputStream(filename);
        InputStream dumpInput = /*new BufferedInputStream(*/fis/*)*/;
        String fileHeader = Utils.readNullTermString(dumpInput);
        if (!acceptableDumpFormat(fileHeader)) {
            System.err.println("Cannot handle " + fileHeader);
            System.exit(1);
        }
        long identifierSize = Utils.readU4(dumpInput);
        Utils.setIdentifierSize(identifierSize);
        Utils.debug("identifier size: " + identifierSize);

        long createTimeMillis = Utils.readU8(dumpInput);
        Instant createTime = Instant.ofEpochMilli(createTimeMillis);
        Utils.debug("file creation time: " + createTime);

        long records = 0;
        while (dumpInput.available() > 0
//            && records < 10
        ) {
            ++records;
            TaggedRecord tr = TaggedRecord.builder()
                    .stringMap(stringRecordMap)
                    .classMap(classRecordMap)
                    .source(dumpInput)
                    .build();

            if (showThisTag(tr.getTag())){
                Utils.debug("****" + tr);
            }
        }
        Utils.debug(records + " top-level records read");
//        stringRecordMap.forEach((k, v) -> System.err.println("String " + v + " id is " + k));
    }
}
