package cz.zcu.kiv.nlp.ir.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * This class is a demonstration of how crawler4j can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public class HTMLDownloaderSelenium extends AbstractHTMLDownloader {

    private WebDriver driver;

    /** Time to wait between document request and evaluation */
    private int documentFetchMillis = 100;

    /**
     * Constructor
     *
     * @param driver
     */
    public HTMLDownloaderSelenium(WebDriver driver) {
        super();
        this.driver = driver;
    }

    /**
     * Quit driver/browser
     */
    @Override
    public void close() {
        driver.quit();
    }

    @Override
    protected Document getDocument(String url) {
        driver.get(url);
        try {
            Thread.sleep(this.documentFetchMillis);
        } catch (InterruptedException e) {
            log.warn("Sleep failed: " + e.toString());
        }
        String dom = driver.getPageSource();
        if (dom == null) {
            log.info("Couldn't fetch the content of the page.");
            failedLinks.add(url);
            return null;
        }

        return Jsoup.parse(dom);
    }

    public HTMLDownloaderSelenium setDocumentFetchMillis(int documentFetchMillis) {
        this.documentFetchMillis = documentFetchMillis;
        return this;
    }
}



