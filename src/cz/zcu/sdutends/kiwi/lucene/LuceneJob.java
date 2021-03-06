package cz.zcu.sdutends.kiwi.lucene;

import cz.zcu.kiv.nlp.ir.preprocessing.StopwordsLoader;
import cz.zcu.sdutends.kiwi.IEntity;
import cz.zcu.sdutends.kiwi.IrJob;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

public final class LuceneJob<TEntity extends IEntity> extends IrJob {
    private static Logger log = Logger.getLogger(LuceneJob.class);

    private final LuceneSettings settings;
    private final LuceneModule<TEntity> module;

    private QueryParser qp;

    private IndexReader reader;
    private IndexSearcher searcher;

    public LuceneJob(LuceneModule<TEntity> module, String... args) {
        this.settings = new LuceneSettings(args);


        settings
//                .setDocumentsDirectory("./storage/ted/talks") // todo: to index documents, uncomment
                .setIndexStorage("./storage/ted/luceneIndex")
                .setPromptApp("Ted Talks")
                .setStopWordsFile("en.txt");
        this.module = module;
    }

    @Override
    public boolean execute() {
        Directory index;
        try {
            index = FSDirectory.open(new File(settings.getIndexStorage()).toPath());
        } catch (IOException e) {
            log.error("Failed opening FSDirectory: " + e.toString());
            return false;
        }

        Analyzer analyzer = new EnglishAnalyzer(loadStopwords(settings.getStopWordsFile()));

        if (settings.indexDocuments()) {

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            try (IndexWriter w = new IndexWriter(index, config)) {
                List<TEntity> entities = module.loadEntities(settings.getDocumentsDirectory());
                log.info("Loaded " + entities.size() + " entities");

                int indexed = this.indexEntities(w, entities);
                log.info("Indexed " + indexed + " entities");
            } catch (IOException ex) {
                log.warn(ex.toString());
            }
        }

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the executeQuery.
        this.qp = new QueryParser("title", analyzer);

        // 3. search
        try (IndexReader reader = DirectoryReader.open(index)) {
            this.reader = reader;
            searcher = new IndexSearcher(reader);

            log.info("Started lucene over " + reader.numDocs() + " documents");

            LuceneCli cli = new LuceneCli()
                    .setPromptApp(settings.getPromptApp())
                    .setOnQuery(this::executeQuery);

            int queriesExecuted = cli.start(System.in);
            log.info("Queries executed: " + queriesExecuted);

        } catch (IOException ex) {
            log.error("Searcher error:" + ex.toString());
            return false;
        } finally {
            this.reader = null;
        }

        return true;
    }

    private CharArraySet loadStopwords(String file) {
        CharArraySet stopwords = new CharArraySet(0, false);
        stopwords.addAll(StopwordsLoader.load(file));
        return stopwords;
    }

    public int indexEntities(IndexWriter writer, Collection<TEntity> entities) {
        int n = 0;

        for (TEntity entity : entities) {
            try {
                log.debug("Indexing " + entity.getUrl());
                writer.addDocument(this.module.entityToDocument(entity));
                n++;
            } catch (Exception ex) {
                log.warn("Entity " + entity.getUrl() + " could not be indexed: " + ex.toString());
            }
        }


        return n;
    }

    private void executeQuery(String queryString, int page) {
        try {
            Query query = qp.parse(queryString);
            executeQuery(query, page);
        } catch (IOException | ParseException | IllegalArgumentException e) {
            log.error(e.toString());
            e.printStackTrace(System.out);
        }
    }

    private void executeQuery(Query query, int page) throws IOException {
        int resultsPerPage = settings.getHitsPerPage();
        if (--page < 0) {
            page = 0;
        }

        int skip = page * resultsPerPage;

        // 4. display results
        log.info("Creating query on " + reader.numDocs() + " documents");
        TopScoreDocCollector collector = TopScoreDocCollector.create(reader.numDocs());
        searcher.search(query, collector);
        TopDocs docs = collector.topDocs(skip, resultsPerPage);

        System.out.format("Found %d hits (skipping %d) out of total %d:\n\n", docs.scoreDocs.length, skip, docs.totalHits);
        for (int i = 0; i < docs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = docs.scoreDocs[i];
            System.out.format("%3d. %s", (i + skip + 1), this.module.entityDocumentToString(searcher.doc(scoreDoc.doc)));
        }
    }
}
