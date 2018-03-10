package cz.zcu.sdutends.kiwi.ted.lucene;

import cz.zcu.kiv.nlp.ir.preprocessing.StopwordsLoader;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.lucene.LuceneCli;
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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class TedLuceneJob extends IrJob {
    private static Logger log = Logger.getLogger(TedLuceneJob.class);

    private final TedLuceneSettings settings;

    private QueryParser qp;

    private IndexReader reader;
    private IndexSearcher searcher;

    public TedLuceneJob(String... args) {
        this.settings = new TedLuceneSettings(args);


        settings
//                .setDocumentsDirectory("./storage/ted/(talks") // todo: to index documents, uncomment
                .setIndexStorage("./storage/ted/luceneIndex");
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

        Analyzer analyzer = new EnglishAnalyzer(loadStopwords());

        if (settings.indexDocuments()) {
            Collection<Talk> talks = loadTalks();
            log.info("Loaded " + talks.size() + " talks");

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            int indexed = indexTalks(index, config, talks);
            log.info("Indexed " + indexed + " talks");
        }

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the executeQuery.
        this.qp = new QueryParser("title", analyzer);

        // 3. search
        try (IndexReader reader = DirectoryReader.open(index)) {
            this.reader = reader;
            searcher = new IndexSearcher(reader);

            LuceneCli cli = new LuceneCli()
                    .setOnQuery(this::executeQuery);

            int queriesExecuted = cli.start(System.in);
            log.info("Queries executed: " + queriesExecuted);

        } catch (IOException ex) {
            log.error("Searcher error:" + ex.toString());
        } finally {
            this.reader = null;
        }
    }

    private CharArraySet loadStopwords() {
        CharArraySet stopwords = new CharArraySet(0, false);
        stopwords.addAll(StopwordsLoader.load("en.txt"));
        return  stopwords;
    }

    private List<Talk> loadTalks() {
        AdvancedIO<Talk> aio = new AdvancedIO<>(Talk.class);
        return aio.loadFromDirectory(settings.getDocumentsDirectory());
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

    private void executeQuery(String queryString, int page) {
        try {
            Query query = qp.parse(queryString);
            executeQuery(query, page);
        } catch (IOException | ParseException e) {
            log.error(e.toString());
        }
    }

    private void executeQuery(Query query, int page) throws IOException {
        int resultsPerPage = settings.getHitsPerPage();
        if (--page < 0) {
            page = 0;
        }

        int skip = page * resultsPerPage;

        // 4. display results
        TopScoreDocCollector collector = TopScoreDocCollector.create(reader.numDocs());
        searcher.search(query, collector);
        TopDocs docs = collector.topDocs(skip, resultsPerPage);

        System.out.format("Found %d hits (skipping %d) out of total %d:\n\n", docs.scoreDocs.length, skip, docs.totalHits);
        for (int i = 0; i < docs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = docs.scoreDocs[i];
            printDocument(i + skip + 1, searcher.doc(scoreDoc.doc));
        }
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

    private static void printDocument(int n, Document d) {
        System.out.format("%2d. %s: %s [%s]\n", n, d.get("talker"), d.get("title"), d.get("dateRecorded"));
    }
}
