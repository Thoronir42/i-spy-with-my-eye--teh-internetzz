package cz.zcu.sdutends.kiwi.lucene;

public interface OnQuery {

    void apply(String query, int page);
}
