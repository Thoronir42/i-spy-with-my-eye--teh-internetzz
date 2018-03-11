package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;

import java.io.File;

public class MiscHelper {
    public static void striptHtml(String path) {
        AdvancedIO<Talk> aio = new AdvancedIO<>(new TalkSedes());

        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            Talk item = aio.loadFromFile(file);

            item.setTitle(Utils.stripHtml(item.getTitle()));
            item.setTalker(Utils.stripHtml(item.getTalker(), true));
            item.setIntroduction(Utils.stripHtml(item.getIntroduction()));
            item.setDateRecorded(Utils.stripHtml(item.getDateRecorded(), true));

            aio.save(file, item);
        }
    }
}
