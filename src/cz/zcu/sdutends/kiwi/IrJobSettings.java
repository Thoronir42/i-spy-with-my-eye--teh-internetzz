package cz.zcu.sdutends.kiwi;

import cz.zcu.kiv.nlp.tools.Utils;

import java.util.Arrays;
import java.util.Random;

public class IrJobSettings {

    // todo: move random to polite interval provider?
    private Random random;

    private String storage;

    private int politeIntervalMin, politeIntervalMax;

    private DataSource linksSource = DataSource.Fetch;
    private String linksDataFile;

    public IrJobSettings() {
        this.random = new Random();
        this.setPoliteInterval(800, 1200);
    }


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




    public String getStorageFile(String ...pathParts) {
        return getStorage() + "/" + String.join("/", pathParts);
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

    public int getPoliteInterval() {
        return politeIntervalMin + random.nextInt(politeIntervalMax - politeIntervalMin);
    }

    public IrJobSettings setPoliteInterval(int val) {
        this.politeIntervalMin = this.politeIntervalMax = val;
        return this;
    }

    public IrJobSettings setPoliteInterval(int min, int max) {
        this.politeIntervalMin = min;
        this.politeIntervalMax = max;

        return this;
    }


    public enum DataSource {
        Fetch, Load
    }
}
