package cz.zcu.sdutends.kiwi.ted.lucene;

import cz.zcu.kiv.nlp.ir.preprocessing.StopwordsLoader;
import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.RecordIO;
import cz.zcu.sdutends.kiwi.ted.Talk;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class TedLuceneJob extends IrJob {
    private static Logger log = Logger.getLogger(TedLuceneJob.class);

    private final TedLuceneSettings settings;

    public TedLuceneJob(String... args) {
        this.settings = new TedLuceneSettings(args);


        settings
//                .setDocumentsDirectory("./storage/ted/(talks") // todo: to index documents, uncomment
                .setIndexStorage("./storage/ted/luceneIndex");
    }

    private void striptHtml() {
        String path = settings.getDocumentsDirectory();
        RecordIO recordIO = new RecordIO();

        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            Talk item = recordIO.loadItem(file, Talk.class);

            item.setTitle(Utils.stripHtml(item.getTitle()));
            item.setTalker(Utils.stripHtml(item.getTalker(), true));
            item.setIntroduction(Utils.stripHtml(item.getIntroduction()));
            item.setDateRecorded(Utils.stripHtml(item.getDateRecorded(), true));

            recordIO.save(file, item);
        }
    }

    @Override
    public void run() {
        Directory index;
        try {
            index = FSDirectory.open(new File(settings.getIndexStorage()).toPath());
        } catch (IOException e) {
            log.error("Failed opening FSDirectory: " + e.toString());
            return;
        }

        CharArraySet stopwords = new CharArraySet(0, false);
        stopwords.addAll(StopwordsLoader.load("en.txt"));

        Analyzer analyzer = new EnglishAnalyzer(stopwords);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        if (settings.indexDocuments()) {
            Collection<Talk> talks = loadTalks();
            log.info("Loaded " + talks.size() + " talks");

            int indexed = indexTalks(index, config, talks);
            log.info("Indexed " + indexed + " talks");
        }


        runQueries(analyzer, index);

    }

    private List<Talk> loadTalks() {
        AdvancedIO<Talk> aio = new AdvancedIO<>(Talk.class);
        List<Talk> talks = aio.loadFromDirectory(settings.getDocumentsDirectory());
        return talks;
    }

    private int indexTalks(Directory index, IndexWriterConfig config, Collection<Talk> talks) {
        int n = 0;
        try (IndexWriter w = new IndexWriter(index, config)) {
            for (Talk talk : talks) {
                try {
                    w.addDocument(talkToDocument(talk));
                    n++;
                } catch (Exception ex) {
                    log.warn("Talk " + talk.getUrl() + " could not be indexed: " + ex.toString());
                }
            }

        } catch (IOException ex) {
            log.warn(ex.toString());
        }
        return n;
    }

    private int runQueries(Analyzer analyzer, Directory index) {
        // 3. search
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);

            return new LuceneCli(reader, searcher, "title", analyzer)
                    .start(System.in);


        } catch (IOException ex) {
            log.error("Searcher error:" + ex.toString());
        }
        return -1;
    }

    private static Document talkToDocument(Talk talk) {
        Document doc = new Document();

        doc.add(new TextField("title", talk.getTitle(), Field.Store.YES));
        doc.add(new StringField("talker", talk.getTalker(), Field.Store.YES));
        doc.add(new StringField("dateRecorded", talk.getDateRecorded(), Field.Store.YES));

        doc.add(new TextField("introduction", talk.getIntroduction(), Field.Store.YES));
        doc.add(new TextField("transcript", talk.getTranscript(), Field.Store.YES));

        return doc;
    }
}
