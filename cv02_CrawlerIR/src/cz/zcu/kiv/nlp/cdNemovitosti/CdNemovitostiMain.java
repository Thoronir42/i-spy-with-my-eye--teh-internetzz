package cz.zcu.kiv.nlp.cdNemovitosti;

import cz.zcu.kiv.nlp.ir.AbstractHTMLDownloader;
import cz.zcu.kiv.nlp.ir.HTMLDownloaderSelenium;
import cz.zcu.kiv.nlp.tools.Utils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CdNemovitostiMain class acts as a controller. You should only adapt this file to serve your needs.
 * Created by Tigi on 31.10.2014.
 */
public class CdNemovitostiMain {
    /**
     * Xpath expressions to extract and their descriptions.
     */
    private final static Map<String, XPathEvaluator> xpathMap = createXpathMap();

    private static final String STORAGE = "./storage/CD-Nemovitosti";

    private static final Logger log = Logger.getLogger(CdNemovitostiMain.class);

    private static Map<String, XPathEvaluator> createXpathMap() {
        HashMap<String, String> map = new HashMap<>();

        map.put("allText", "//div[@class='property_info']/div[@class='box']/allText()");
        map.put("html", "//div[@class='property_info']/div[@class='box']/html()");
        map.put("tidyText", "//div[@class='property_info']/div[@class='box']/tidyText()");


        return map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> Xsoup.compile(e.getValue())));
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        //Initialization
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        if (!ensureOutputAvailable(STORAGE)) {
            System.exit(1);
        }

//        HTMLDownloader downloader = new HTMLDownloader();
        AbstractHTMLDownloader downloader = new HTMLDownloaderSelenium("./cv02_CrawlerIR/chromedriver.exe");

        CdNemovitostiCrawler crawler = new CdNemovitostiCrawler(downloader)
                .setPolitenessInterval(1200); // Be polite and don't send requests too often.



        Collection<String> urlsSet = crawler.retrieveLinks(STORAGE + "_urls.txt");

        for (String name : xpathMap.keySet()) {
            String fileName = STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + name + ".txt";
            crawler.openPrintStream(name, fileName);

        }

        int count = 0;
        for (String url : urlsSet) {
            crawler.processResultUrl(url, xpathMap);

            if (++count % 100 == 0) {
                log.info(count + " / " + urlsSet.size() + " = " + count / (0.0 + urlsSet.size()) + "% done.");
            }
        }


        // Save links that failed in some way.
        // Be sure to go through these and explain why the process failed on these links.
        // Try to eliminate all failed links - they consume your time while crawling data.
        reportProblems(downloader.getFailedLinks());
        downloader.emptyFailedLinks();
        log.info("-----------------------------");
        //close print streams
        crawler.close();


//        // Print some information.
//        for (String key : results.keySet()) {
//            Map<String, List<String>> map = results.get(key);
//            Utils.saveFile(new File(STORAGE + "/" + Utils.SDF.format(System.currentTimeMillis()) + "_" + key + "_final.txt"),
//                    map, idMap);
//            log.info(key + ": " + map.size());
//        }


        System.exit(0);
    }

    private static boolean ensureOutputAvailable(String directory) {
        File outputDir = new File(directory);
        if (!outputDir.exists()) {
            boolean mkdirs = outputDir.mkdirs();
            if (!mkdirs) {
                log.error("Output directory can't be created! Please either create it or change the STORAGE parameter.\nOutput directory: " + outputDir);
                return false;
            }
            log.info("Output directory created: " + outputDir);
        }

        return true;
    }


    /**
     * Save file with failed links for later examination.
     *
     * @param failedLinks links that couldn't be downloaded, extracted etc.
     */
    private static void reportProblems(Set<String> failedLinks) {
        if (!failedLinks.isEmpty()) {

            Utils.saveFile(new File(STORAGE + Utils.SDF.format(System.currentTimeMillis()) + "_undownloaded_links_size_" + failedLinks.size() + ".txt"),
                    failedLinks);
            log.info("Failed links: " + failedLinks.size());
        }
    }


}
