package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.IrJobSettings;

class TedSettings extends IrJobSettings {

    private int limit = 500;
    private int skip = 0;

    public TedSettings(String... args) {
        this.process(args);
    }

    @Override
    protected boolean setArgument(int n, String arg) {
        return super.setArgument(n, arg);
    }

    @Override
    protected int setOption(String name, String[] args) {
        return super.setOption(name, args);
    }

    public int getLimit() {
        return limit;
    }

    public TedSettings setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getSkip() {
        return skip;
    }

    public TedSettings setSkip(int skip) {
        this.skip = skip;
        return this;
    }
}
