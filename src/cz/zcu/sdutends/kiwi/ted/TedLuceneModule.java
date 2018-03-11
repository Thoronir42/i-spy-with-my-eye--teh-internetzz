package cz.zcu.sdutends.kiwi.ted;


import cz.zcu.sdutends.kiwi.lucene.LuceneModule;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class TedLuceneModule extends LuceneModule<Talk> {

    public TedLuceneModule() {
        super(Talk.class);
    }

    @Override
    protected Document entityToDocument(Talk talk) {
        Document doc = new Document();

        doc.add(new TextField("title", talk.getTitle(), Field.Store.YES));
        doc.add(new StringField("talker", talk.getTalker(), Field.Store.YES));
        doc.add(new StringField("dateRecorded", talk.getDateRecorded(), Field.Store.YES));

        doc.add(new TextField("introduction", talk.getIntroduction(), Field.Store.YES));
        doc.add(new TextField("transcript", talk.getTranscript(), Field.Store.YES));

        return doc;
    }

    @Override
    protected String entityDocumentToString(Document d) {
        return String.format("%s: %s [%s]\n", d.get("talker"), d.get("title"), d.get("dateRecorded"));
    }
}
