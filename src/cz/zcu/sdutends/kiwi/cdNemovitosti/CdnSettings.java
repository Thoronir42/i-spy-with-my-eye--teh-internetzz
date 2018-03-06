package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class CdnSettings extends IrJobSettings{


    private Mode mode = Mode.Structured;

    CdnSettings(String ...args) {
        this.process(args);
        this.setStorage("./storage/CD-Nemovitosti");
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        Dump, Structured
    }
}
