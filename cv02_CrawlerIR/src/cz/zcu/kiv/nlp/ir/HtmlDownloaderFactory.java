package cz.zcu.kiv.nlp.ir;

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
                return new HTMLDownloaderSelenium("./cv02_CrawlerIR/chromedriver.exe");

            default:
            case Direct:
                return new HTMLDownloader();

        }
    }

    public enum Type {
        Direct, Selenium
    }
}
