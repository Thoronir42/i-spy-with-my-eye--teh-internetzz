package cz.zcu.sdutends.kiwi.lucene;

import cz.zcu.sdutends.kiwi.ted.Talk;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;
import org.apache.lucene.document.Document;

import java.util.List;

public abstract class LuceneModule<T extends IEntity> {

    private final Class<T> tClass;

    public LuceneModule(Class<T> tClass) {
        this.tClass = tClass;
    }

    public List<T> loadEntities(String directory) {
        AdvancedIO<T> aio = new AdvancedIO<>(tClass);
        return aio.loadFromDirectory(directory);
    }

    protected abstract Document entityToDocument(T talk);

    protected abstract String  entityDocumentToString(Document d);
}
