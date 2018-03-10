package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.RecordIO;

import java.io.File;

public class MiscHelper {
    public static void striptHtml(String path) {
        RecordIO recordIO = new RecordIO();

        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            Talk item = recordIO.loadItem(file, Talk.class);

            item.setTitle(Utils.stripHtml(item.getTitle()));
            item.setTalker(Utils.stripHtml(item.getTalker(), true));
            item.setIntroduction(Utils.stripHtml(item.getIntroduction()));
            item.setDateRecorded(Utils.stripHtml(item.getDateRecorded(), true));

            recordIO.save(file, item);
        }
    }
}
