package cz.zcu.sdutends.kiwi;

import cz.zcu.kiv.nlp.tools.Utils;

import java.util.Arrays;

public class IrJobSettings {

    private String storage;

    private DataSource linksSource = DataSource.Fetch;
    private String linksDataFile;


    public final void process(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.charAt(0) =='-') {
                int n = this.setOption(arg.substring(1), Arrays.copyOfRange(args, i + 1, args.length));
                i += n;
                continue;
            }
            setArgument(i, arg);
        }
    }




    public String getStorageFile(String filename) {
        return getStorageFile(filename, false);
    }

    public String getStorageFile(String filename, boolean prependCurrentTime) {
        String result = getStorage() + "/";
        if(prependCurrentTime) {
            result += Utils.SDF.format(System.currentTimeMillis()) + "_";
        }
        return result + filename;
    }



    protected boolean setArgument(int n, String  arg) {
        System.err.println("Set argument not implemented on " + this.getClass().getSimpleName());
        return true;
    }

    protected int setOption(String name, String[] args){
        System.err.println("Set option not implemented on " + this.getClass().getSimpleName());
        return 0;
    }



    public DataSource getLinksSource() {
        return linksSource;
    }
    public void setLinksSource(DataSource linksSource) {
        this.linksSource = linksSource;
    }

    public String getLinksDataFile() {
        return linksDataFile;
    }
    public void setLinksDataFile(String linksDataFile) {
        this.linksDataFile = linksDataFile;
    }

    public final String getStorage() {
        return storage;
    }
    public final void setStorage(String storage) {
        this.storage = storage;
    }


    public enum DataSource {
        Fetch, Load
    }
}
