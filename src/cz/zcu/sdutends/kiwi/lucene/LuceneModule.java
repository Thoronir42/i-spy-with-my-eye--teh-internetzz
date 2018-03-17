package cz.zcu.sdutends.kiwi.lucene;

import cz.zcu.sdutends.kiwi.IEntity;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import java.util.List;

public abstract class LuceneModule<T extends IEntity> {
    protected static final Logger log = Logger.getLogger(LuceneModule.class);


    public LuceneModule(Class<T> tClass) {
    }

    public abstract List<T> loadEntities(String directory);

    protected abstract Document entityToDocument(T talk);

    protected abstract String  entityDocumentToString(Document d);


}
