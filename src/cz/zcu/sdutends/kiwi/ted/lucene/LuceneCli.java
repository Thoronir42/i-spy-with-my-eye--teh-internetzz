package cz.zcu.sdutends.kiwi.ted.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuceneCli {
    private final IndexReader reader;
    private final IndexSearcher searcher;
    private final QueryParser qp;

    private int resultsPerPage = 10;

    public LuceneCli(IndexReader reader, IndexSearcher searcher, String defaultQueryField, Analyzer analyzer) {
        this.reader = reader;
        this.searcher = searcher;

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the executeQuery.
        this.qp = new QueryParser(defaultQueryField, analyzer);
    }

    public int start(InputStream in) {
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));
        String query;
        int queriesExecuted = 0;
        Pattern p = Pattern.compile("(\\d+)\\|.*");

        try {
            while ((query = prompt(bin)) != null) {
                if ("/exit".equals(query)) {
                    break;
                }
                try {
                    int page = 0;
                    Matcher matcher = p.matcher(query);
                    if (matcher.matches()) {
                        page = Integer.parseInt(matcher.group(1));
                        query = query.substring(matcher.end(1) + 1);
                        System.out.println("Page: " + page + " rq: " + query);
                    }
                    executeQuery(searcher, qp.parse(query), page);
                    queriesExecuted++;
                } catch (IOException | ParseException e) {
                    System.err.println(e.toString());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return queriesExecuted;
    }

    private String prompt(BufferedReader br) throws IOException {
        System.out.print("Lucene-TedTalks >");
        return br.readLine();
    }

    private void executeQuery(IndexSearcher searcher, Query query, int page) throws IOException {
        int skip = page * resultsPerPage;
        int take = Math.min(reader.numDocs() - skip, resultsPerPage);

        TopDocs docs = searcher.search(query, skip + take);

        ScoreDoc[] hits = docs.scoreDocs;
        take = Math.min(hits.length - skip, resultsPerPage);

        // 4. display results
        System.out.format("Found %d hits (skipping %d).\n", docs.scoreDocs.length, skip);
        for (int i = 0; i < take; i++) {
            int docId = hits[skip + i].doc;
            Document d = searcher.doc(docId);
            System.out.format("%2d. %s: %s [%s]\n", i + skip + 1, d.get("talker"), d.get("title"), d.get("dateRecorded"));
        }
    }
}
