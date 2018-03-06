package cz.zcu.sdutends.kiwi.ted.lucene;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class TedLuceneSettings extends IrJobSettings {

    private String documentsDirectory;

    private String indexStorage;

    private int hitsPerPage = 10;

    public TedLuceneSettings(String ...args) {
        super();
        this.process(args);
    }

    public String getDocumentsDirectory() {
        return documentsDirectory;
    }

    public TedLuceneSettings setDocumentsDirectory(String documentsDirectory) {
        this.documentsDirectory = documentsDirectory;
        return this;
    }

    public String getIndexStorage() {
        return indexStorage;
    }

    public TedLuceneSettings setIndexStorage(String indexStorage) {
        this.indexStorage = indexStorage;
        return this;
    }

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public boolean indexDocuments() {
        return documentsDirectory != null;
    }
}
