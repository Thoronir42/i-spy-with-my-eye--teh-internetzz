package cz.zcu.kiv.nlp.ir.crawling;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

/**
 * This class is a demonstration of how crawler4j can be used to download a website
 * Created by Tigi on 31.10.2014.
 */
public class HTMLDownloaderSelenium extends AbstractHTMLDownloader {

    private WebDriver driver;

    /**
     * Constructor
     *
     * @param driverPath
     */
    public HTMLDownloaderSelenium(String driverPath) {
        super();
        System.setProperty("webdriver.chrome.driver", driverPath);
        driver = new ChromeDriver();
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

        String dom = driver.getPageSource();
        if (dom == null) {
            log.info("Couldn't fetch the content of the page.");
            failedLinks.add(url);
            return null;
        }

        return Jsoup.parse(dom);
    }
}



