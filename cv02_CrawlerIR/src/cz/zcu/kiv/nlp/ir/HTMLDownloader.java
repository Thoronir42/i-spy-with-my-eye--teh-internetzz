package cz.zcu.kiv.nlp.ir;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a demonstration of how crawler4j can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public class HTMLDownloader extends AbstractHTMLDownloader {

    private final Parser parser;
    private final PageFetcher pageFetcher;

    public HTMLDownloader() {
        super();
        CrawlConfig config = new CrawlConfig();
        parser = new Parser(config);
        pageFetcher = new PageFetcher(config);

        config.setMaxDepthOfCrawling(0);
        config.setResumableCrawling(false);
    }


    private Page getPage(String url) {
        try {
            return download(url);
        } catch (InterruptedException | PageBiggerThanMaxSizeException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Downloads given url
     *
     * @param url page url
     * @return object representation of the html page on given url
     */
    private Page download(String url) throws InterruptedException, PageBiggerThanMaxSizeException, IOException {
        WebURL curURL = new WebURL();
        curURL.setURL(url);
        PageFetchResult fetchResult = null;
        try {
            fetchResult = pageFetcher.fetchPage(curURL);
            if (fetchResult.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY) {
                curURL.setURL(fetchResult.getMovedToUrl());
                fetchResult = pageFetcher.fetchPage(curURL);
            }
            if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {

                Page page = new Page(curURL);
                fetchResult.fetchContent(page);
                parser.parse(page, curURL.getURL());
                return page;

            }
        } catch (ParseException | NotAllowedContentException e) {
            e.printStackTrace();
        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }
        }
        return null;
    }

    @Override
    protected Document getDocument(String url) {
        Page page = getPage(url);

        if (page == null) {
            log.info("Couldn't fetch the content of the page.");
            failedLinks.add(url);
            return null;
        }
        ParseData parseData = page.getParseData();
        if (parseData == null) {
            log.info("Couldn't parse the content of the page.");
            return null;
        }
        if (!(parseData instanceof HtmlParseData)) {
            log.info("Parse data is not HTML");
            return null;
        }

        return Jsoup.parse(((HtmlParseData) parseData).getHtml());
    }
}



