package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.kiv.nlp.ir.crawling.HtmlDownloaderFactory;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.RecordIO;
import cz.zcu.sdutends.kiwi.ir.GenericCrawler;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * CdNemovitostiMain class acts as a controller. You should only adapt this file to serve your needs.
 * Created by Tigi on 31.10.2014.
 */
public class CdNemovitostiMain extends IrJob {
    private static final Logger log = Logger.getLogger(CdNemovitostiMain.class);

    private final MainSettings settings;

    public CdNemovitostiMain(String ...args) {
        this.settings = new MainSettings().process(args);
    }

    @Override
    public void run() {
        //Initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);



        if (!Utils.ensureDirectoryExists(settings.getStorage())) {
            System.exit(1);
        }

        IHtmlDownloader downloader = new HtmlDownloaderFactory().create(HtmlDownloaderFactory.Type.Selenium);

        CdNemovitostiCrawler crawler = new CdNemovitostiCrawler(downloader);
        crawler.setPolitenessInterval(1200); // Be polite and don't send requests too often.

        RecordIO io = new RecordIO();

        Collection<String> urlsSet = null;
        switch (settings.getLinksSource()) {
            case Load:
                urlsSet = crawler.loadEstateLinks(settings.getStorageFile(settings.getLinksDataFile()));
                break;
            case Fetch:
                urlsSet = crawler.fetchEstateLinks();
                Utils.saveFile(new File(settings.getStorageFile("links_size_" + urlsSet.size() + ".txt", true)),
                        urlsSet);
                break;
            default:
                throw new UnsupportedOperationException("Links source method not supported");

        }
        if (urlsSet == null) {
            log.warn("Error occured during links retrival");
            System.exit(3);
        }

        switch (settings.getMode()) {
            case Dump:
                GenericCrawler gCrawler = new GenericCrawler(crawler);
                gCrawler.addAction("allText", "//div[@class='property_info']/div[@class='box']/allText()");
                gCrawler.addAction("html", "//div[@class='property_info']/div[@class='box']/html()");
                gCrawler.addAction("tidyText", "//div[@class='property_info']/div[@class='box']/tidyText()");

                for (String name : gCrawler.actionNames()) {
                    String fileName = settings.getStorageFile(name + ".txt", true);
                    gCrawler.openPrintStream(name, fileName);
                }
                runProgress(urlsSet, gCrawler::processResultUrl);
                //close print streams
                gCrawler.closePrintStreams();
                break;

            case Structured:
                Collection<Estate> estate = runProgress(urlsSet, crawler::retrieveEstate);
                io.save(settings.getStorageFile("serialized.txt", true), estate);
        }

        // Save links that failed in some way.
        // Be sure to go through these and explain why the process failed on these links.
        // Try to eliminate all failed links - they consume your time while crawling data.
        Set<String> failedLinks = downloader.getFailedLinks();
        if (!failedLinks.isEmpty()) {
            Utils.saveFile(new File(settings.getStorageFile("undownloaded_links__size_" + failedLinks.size() + ".txt", true)), failedLinks);
            log.info("Failed links: " + failedLinks.size());
            downloader.emptyFailedLinks();
        }

        log.info("-----------------------------");
        crawler.close();


//        // Print some information.
//        for (String key : results.keySet()) {
//            Map<String, List<String>> map = results.get(key);
//            Utils.saveFile(new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + "_final.txt"),
//                    map, idMap);
//            log.info(key + ": " + map.size());
//        }

    }
}
