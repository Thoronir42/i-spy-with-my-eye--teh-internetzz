package cz.zcu.kiv.nlp.ir;

import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is a demonstration of how crawler can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public interface IHTMLDownloader {

    /**
     * Get failed links.
     *
     * @return failed links
     */
    public Set<String> getFailedLinks();

    /**
     * Empty the empty links set
     */
    public void emptyFailedLinks();


    /**
     * Downloads given url page and extracts xpath expressions.
     *
     * @param url      page url
     * @param xpathMap pairs of description and xpath expression
     * @return pairs of descriptions and extracted values
     */
    public Map<String, List<String>> processUrl(String url, Map<String, XPathEvaluator> xpathMap);

    public List<String> processUrl(String url, String xPath);
    public List<String> processUrl(String url, XPathEvaluator xPath);

    public <T> T processUrl(String url, Function<DocumentEvaluator, T> transform);


    /**
     * Quit driver/browser
     */
    public void close();
}



