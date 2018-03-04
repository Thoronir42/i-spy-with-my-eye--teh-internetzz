package cz.zcu.kiv.nlp.ir.crawling;

import us.codecraft.xsoup.XPathEvaluator;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is a demonstration of how crawler can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public interface IHtmlDownloader extends AutoCloseable{

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


    public List<String> processUrl(String url, String xPath);
    public List<String> processUrl(String url, XPathEvaluator xPath);

    public <T> T processUrl(String url, Function<DocumentEvaluator, T> evaluate);


    /**
     * Quit driver/browser
     */
    public void close();
}



