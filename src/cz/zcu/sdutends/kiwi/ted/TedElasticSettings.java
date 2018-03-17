package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class TedElasticSettings extends IrJobSettings {

    private String index;
    private String clusterName;

    TedElasticSettings(String ...args) {
        this.process(args);
    }

    public String getIndex() {
        return index;
    }

    public TedElasticSettings setIndex(String index) {
        this.index = index;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public TedElasticSettings setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }
}
