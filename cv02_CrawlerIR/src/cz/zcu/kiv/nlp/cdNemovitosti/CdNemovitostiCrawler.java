package cz.zcu.kiv.nlp.cdNemovitosti;

import cz.zcu.kiv.nlp.ir.DocumentEvaluator;
import cz.zcu.kiv.nlp.ir.IHTMLDownloader;
import cz.zcu.kiv.nlp.tools.Utils;
import org.apache.log4j.Logger;
import us.codecraft.xsoup.XPathEvaluator;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CdNemovitostiCrawler {
    private static Logger log = Logger.getLogger(CdNemovitostiCrawler.class);

    private static String SITE = "http://nemovitosti.ceskedrahy.cz";
    private static String SEARCH_SUFFIX = "/Results.aspx?fgroup=L";

    private int politenessInterval;

    private final IHTMLDownloader downloader;

    private Map<String, PrintStream> printStreamMap = new HashMap<>();

    CdNemovitostiCrawler(IHTMLDownloader downloader) {

        this.downloader = downloader;
        this.politenessInterval = 100;
    }

    public Collection<String> retrieveLinks(String storage) {
        Collection<String> urlsSet = new HashSet<>();
        //Try to load links
        File links = new File(storage);
        if (links.exists()) {
            try {
                List<String> lines = Files.lines(new File(storage).toPath()).collect(Collectors.toList());

                urlsSet.addAll(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int max = 2;
            for (int i = 0; i < max; i++) {
                String link = SITE + SEARCH_SUFFIX + "&page=" + i;
                List<String> items = downloader.processUrl(link, "//div[contains(@class,'viewerBox')]/div[contains(@class, 'itm')]//a/@href");

                urlsSet.addAll(items);
            }
            Utils.saveFile(new File(storage + Utils.SDF.format(System.currentTimeMillis()) + "_links_size_" + urlsSet.size() + ".txt"),
                    urlsSet);
        }

        return urlsSet;
    }

    public void openPrintStream(String name, String storage) {
        File file = new File(storage);
        PrintStream printStream = null;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        printStreamMap.put(name, printStream);
    }

    public void processResultUrl(String url, Map<String, XPathEvaluator> actions) {
        Map<String, Map<String, List<String>>> results = new HashMap<>();
        for (String key : actions.keySet()) {
            results.put(key, new HashMap<>());
        }

        String link = normalizeUrl(url);

        //Download and extract data according to xpathMap
        Map<String, List<String>> httpResult = downloader.processUrl(link, actions);

        for (Map.Entry<String, Map<String, List<String>>> action : results.entrySet()) {
            // map of given action
            Map<String, List<String>> map = action.getValue();

            List<String> list = httpResult.get(action.getKey());
            if (list != null) {
                map.put(url, list);
                log.info(Arrays.toString(list.toArray()));
                //print
                PrintStream printStream = printStreamMap.get(action.getKey());
                for (String result : list) {
                    printStream.println(url + "\t" + result);
                }
            }
        }

        try {
            Thread.sleep(politenessInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Property> retrieveProperties(Collection<String> urls) {
        List<Property> properties = new ArrayList<>();
        Function<DocumentEvaluator, Property> transformFunction = (de) -> {
            Property property = new Property()
                    .setUrl(de.getUrl())
                    .setTitle(de.string("//div[@class='property_container']/h1"))
                    .setEvidenceNumber(de.string("//div[@class='property_head']/div[@class='fleft']//strong"))
                    .setPricePerSquareMeter(de.string("//div[@class='priceBox']/p"));

            property
                    .setRegion(de.string("//div[contains(@class, 'generalBox')]/div[@class='itm'][1]/div[@class='val']"))
                    .setDistrict(de.string("//div[contains(@class, 'generalBox')]/div[@class='itm'][2]/div[@class='val']"))
                    .setCity(de.string("//div[contains(@class, 'generalBox')]/div[@class='itm'][3]/div[@class='val']"))
                    .setCatasterZone(de.string("//div[contains(@class, 'generalBox')]/div[@class='itm'][4]/div[@class='val']"))
                    .setSurface(de.string("//div[contains(@class, 'generalBox')]/div[@class='itm'][5]/div[@class='val']"));

            property
                    .setWater(de.string("//div[@class='attrsBox frm']/div[@class='itm'][1]/div[@class='val']"))
                    .setElectricity(de.string("//div[@class='attrsBox frm']/div[@class='itm'][2]/div[@class='val']"))
                    .setCanalization(de.string("//div[@class='attrsBox frm']/div[@class='itm'][3]/div[@class='val']"));


            return property;
        };

        int i = 0;
        for (String url : urls) {

            properties.add(downloader.processUrl(normalizeUrl(url), transformFunction));
            if (i++ > 10) {
                break;
            }
        }

        return properties;
    }

    public CdNemovitostiCrawler setPolitenessInterval(int milliseconds) {
        this.politenessInterval = milliseconds;
        return this;
    }

    private String normalizeUrl(String url) {
        return url.contains(SITE) ? url : SITE + url;
    }

    public void close() {
        log.info("Closing crawler");
        printStreamMap.values().forEach(PrintStream::close);
        downloader.close();
    }
}
