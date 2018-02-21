package cz.zcu.kiv.nlp.ir;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is a demonstration of how crawler can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public abstract class AbstractHTMLDownloader implements IHTMLDownloader {

    static final Logger log = Logger.getLogger(AbstractHTMLDownloader.class);
    Set<String> failedLinks = new HashSet<>();


    public final List<String> processUrl(String url, String xPath) {
        return processUrl(url, Xsoup.compile(xPath));
    }

    public final List<String> processUrl(String url, XPathEvaluator xPath) {
        log.info("Processing url: " + url);

        Document document = getDocument(url);
        if(document == null) {
            log.info("Failed to get document from url '" + url + "'");
            return null;
        }

        return xPath.evaluate(document).list();
    }

    /**
     * Downloads given url page and extracts xpath expressions.
     *
     * @param url      page url
     * @param xpathMap pairs of description and xpath expression
     * @return pairs of descriptions and extracted values
     */
    public Map<String, List<String>> processUrl(String url, Map<String, XPathEvaluator> xpathMap) {
        log.info("Processing url: " + url);

        Document document = getDocument(url);
        if(document == null) {
            log.warn("Failed to get document from url '" + url + "'");
            return null;
        }

        return xpathMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().evaluate(document).list()));
    }

    @Override
    public <T> T processUrl(String url, Function<DocumentEvaluator, T> transform) {
        log.info("Processing url: " + url);

        Document document = getDocument(url);
        if(document == null) {
            log.warn("Failed to get document from url '" + url + "'");
            return null;
        }

        return transform.apply(new DocumentEvaluator(document, url));
    }

    protected abstract Document getDocument(String url);

    /**
     * Get failed links.
     *
     * @return failed links
     */
    public Set<String> getFailedLinks() {
        return failedLinks;
    }

    /**
     * Empty the empty links set
     */
    public void emptyFailedLinks() {
        failedLinks.clear();
    }

    @Override
    public void close() {
    }
}



