package cz.zcu.kiv.nlp.ir.crawling;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class HtmlDownloaderFactory {
    public IHtmlDownloader create(String type) {
        switch (type.toLowerCase()) {
            default:
                System.err.println("Invalid html downloader type provided");
            case "direct":
                return create(Type.Direct);
            case "selenium":
                return create(Type.Selenium);
        }
    }

    public IHtmlDownloader create(Type type) {
        switch (type) {
            case Selenium:
                System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
                WebDriver driver = new ChromeDriver();
                return new HTMLDownloaderSelenium(driver)
                        .setDocumentFetchMillis(350); // ensure the page is fully loaded and JS-initialized

            default:
            case Direct:
                return new HTMLDownloader();

        }
    }

    public enum Type {
        Direct, Selenium
    }
}
