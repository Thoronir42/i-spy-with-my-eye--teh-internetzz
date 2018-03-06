package cz.zcu.sdutends.kiwi.ir;

import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import org.apache.log4j.Logger;

public class ACrawler {
    public static final Logger log = Logger.getLogger(ACrawler.class);

    // fixme: public access only for GenericCrawler
    public final IHtmlDownloader downloader;

    protected final String siteRoot;
    private int politenessInterval;

    protected ACrawler(IHtmlDownloader downloader, String siteRoot) {
        this.downloader = downloader;
        this.siteRoot = siteRoot;
        this.politenessInterval = 100;
    }


    public String normalizeUrl(String url) {
        return url.contains(siteRoot) ? url : siteRoot + url;
    }

    public void bePolite() {
        try {
            Thread.sleep(politenessInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setPolitenessInterval(int milliseconds) {
        this.politenessInterval = milliseconds;
    }

    public void close() {
        log.info("Closing crawler");
        downloader.close();
    }
}
