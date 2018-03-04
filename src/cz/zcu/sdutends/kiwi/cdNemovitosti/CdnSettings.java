package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.kiv.nlp.tools.Utils;

public class MainSettings {

    private String linksDataFile = "urls.txt2018-02-20_23_13_670_links_size_336.txt";
    private DataSource linksSource = DataSource.Load;
    private Mode mode = Mode.Structured;

    private String storage = "./storage/CD-Nemovitosti";

    public MainSettings process(String[] args) {
        return this;
    }

    public DataSource getLinksSource() {
        return linksSource;
    }

    public String getLinksDataFile() {
        return linksDataFile;
    }

    public Mode getMode() {
        return mode;
    }


    public String getStorage() {
        return storage;
    }

    public String getStorageFile(String filename) {
        return getStorageFile(filename, false);
    }

    public String getStorageFile(String filename, boolean prependCurrentTime) {
        String result = storage + "/";
        if(prependCurrentTime) {
            result += Utils.SDF.format(System.currentTimeMillis()) + "_";
        }
        return result + filename;
    }




    public enum Mode {
        Dump, Structured
    }

    public enum DataSource {
        Fetch, Load
    }
}
