package cz.zcu.sdutends.kiwi;

import java.util.Arrays;

public class IrJobSettings {

    private String storage;

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



    public final String getStorage() {
        return storage;
    }
    public final void setStorage(String storage) {
        this.storage = storage;
    }
}
