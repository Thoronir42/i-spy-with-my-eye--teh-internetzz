package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.utils.AdvancedIO;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscHelper {
    private static Pattern htmlTagContentRegex = Pattern.compile("<[\\w\\s\"=:._\\-]+>(.+)<\\/\\w+>");

    public static void striptHtmlWithin(String path) {
        AdvancedIO<Talk> aio = new AdvancedIO<>(new TalkSedes());

        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            Talk item = aio.loadFromFile(file);

            item.setTitle(stripHtml(item.getTitle()));
            item.setTalker(stripHtml(item.getTalker(), true));
            item.setIntroduction(stripHtml(item.getIntroduction()));
            item.setDateRecorded(stripHtml(item.getDateRecorded(), true));

            aio.save(file, item);
        }
    }

    public static String stripHtml(String string) {
        return stripHtml(string, false);
    }

    public static String stripHtml(String string, boolean replaceNewlines) {
        if(string == null) {
            System.err.println("StripHtml recieved null");
            return "";
        }
        if(replaceNewlines) {
            string = string
                    .replace("\n", "")
                    .replaceAll("&\\w+;", "");
        }

        Matcher matcher = htmlTagContentRegex.matcher(string);
        if(!matcher.matches()) {
            return string;
        }

        return matcher.group(1).trim();
    }
}
