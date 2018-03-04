package cz.zcu.sdutends.kiwi.cdNemovitosti;

import cz.zcu.kiv.nlp.ir.crawling.DocumentEvaluator;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.kiv.nlp.tools.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class ACrawler extends cz.zcu.sdutends.kiwi.ir.ACrawler {
    private static Logger log = Logger.getLogger(ACrawler.class);

    private static String SEARCH_SUFFIX = "/Results.aspx?fgroup=L";

    // fixme: public access only for GenericCrawler
    public final IHtmlDownloader downloader;

    private Function<DocumentEvaluator, Estate> evalFunction = getDocumentEvaluatorEstateFunction();



    ACrawler(IHtmlDownloader downloader) {
        super("http://nemovitosti.ceskedrahy.cz");
        this.downloader = downloader;
    }

    public Collection<String> loadEstateLinks(String path) {
        try {
            List<String> strings = Utils.readLines(new File(path));
            log.info("Loaded " + strings.size() + " estate links");
            return strings;
        } catch (IOException e) {
            return null;
        }
    }

    public Collection<String> fetchEstateLinks() {
        String estateLinkXPath = "//div[contains(@class,'viewerBox')]/div[contains(@class, 'itm')]//a/@href";

        AtomicInteger max = new AtomicInteger(10); // todo: optimize initial page count?
        String listingUrl = this.siteRoot + SEARCH_SUFFIX + "&page=" + 0;

        Collection<String> links =  downloader.processUrl(listingUrl, (de) -> {
            max.set(de.integer(""));
            return de.strings(estateLinkXPath);
        });

        for (int i = 1; i < max.get(); i++) {
            listingUrl = this.siteRoot + SEARCH_SUFFIX + "&page=" + i;

            List<String> items = downloader.processUrl(listingUrl, estateLinkXPath);
            links.addAll(items);
        }

        return links;
    }

    public Estate retrieveEstate(String url) {
        bePolite();
        return downloader.processUrl(normalizeUrl(url), evalFunction);
    }


    public void close() {
        log.info("Closing crawler");
        downloader.close();
    }


    private static Function<DocumentEvaluator, Estate> getDocumentEvaluatorEstateFunction() {
        return (de) -> {
            Estate property = new Estate()
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
    }
}