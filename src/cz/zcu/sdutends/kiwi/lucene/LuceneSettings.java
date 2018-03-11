package cz.zcu.sdutends.kiwi.lucene;

import cz.zcu.sdutends.kiwi.IrJobSettings;

public class LuceneSettings extends IrJobSettings {

    private String documentsDirectory;

    private String indexStorage;

    private int hitsPerPage = 10;
    private String stopWordsFile;
    private String promptApp;

    public LuceneSettings(String ...args) {
        super();
        this.process(args);
    }

    public String getDocumentsDirectory() {
        return documentsDirectory;
    }

    public LuceneSettings setDocumentsDirectory(String documentsDirectory) {
        this.documentsDirectory = documentsDirectory;
        return this;
    }

    public String getIndexStorage() {
        return indexStorage;
    }

    public LuceneSettings setIndexStorage(String indexStorage) {
        this.indexStorage = indexStorage;
        return this;
    }

    public int getHitsPerPage() {
        return hitsPerPage;
    }

    public boolean indexDocuments() {
        return documentsDirectory != null;
    }

    public void setStopWordsFile(String stopWordsFile) {
        this.stopWordsFile = stopWordsFile;
    }

    public String getStopWordsFile() {
        return stopWordsFile;
    }

    public LuceneSettings setPromptApp(String promptApp) {
        this.promptApp = promptApp;
        return this;
    }

    public String getPromptApp() {
        return promptApp;
    }
}
