package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.ir.crawling.DocumentEvaluator;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.sdutends.kiwi.ir.ACrawler;
import cz.zcu.sdutends.kiwi.ted.model.Talk;
import org.apache.log4j.Logger;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TedCrawler extends ACrawler {
    private static Logger log = Logger.getLogger(TedCrawler.class);

    private String listingSuffix;
    private Function<DocumentEvaluator, Talk> evalMetadata = createEvalFunction();


    protected TedCrawler(IHtmlDownloader downloader) {
        super(downloader, "https://www.ted.com");
        this.listingSuffix = "talks";
    }

    public Collection<String> fetchTalkLinks() {
        XPathEvaluator talkLinkXpath = Xsoup.compile("//*[@class='talk-link']//*[contains(@class, 'image')]/*[@class='ga-link']/@href");

        AtomicInteger max = new AtomicInteger(10); // todo: optimize initial page count?
        String listingUrl = this.siteRoot + '/' + this.listingSuffix + "?page=";

        Collection<String> links = downloader.processUrl(listingUrl + 1, (de) -> {
            // 5 bc on first page the links are 2, 3, 4, 5, ... <lastPage>
            String lastPageLink = de.string("//*[@class='pagination']//*[contains(@class,'pagination__link')][5]/@href");
            max.set(Integer.parseInt(lastPageLink.split("=")[1]));
            log.info("Max page = " + max.get() + " (" + lastPageLink + "=" + lastPageLink + ")");

            return de.strings(talkLinkXpath);
        });

        for (int i = 2; i <= max.get(); i++) {
            List<String> items = downloader.processUrl(listingUrl + i, talkLinkXpath);
            this.bePolite();
            links.addAll(items);
        }

        log.info("Crawled " + links.size() + " talk links");

        return links;
    }

    public Talk retrieveTalk(String url) {
        bePolite();
        Talk talk = downloader.processUrl(normalizeUrl(url), evalMetadata);

        bePolite();
        Collection<String> transcript = downloader.processUrl(normalizeUrl(url + "/transcript"), (de) ->
                de.strings("//div[@id='content']//section/*[contains(@class, 'Grid')]/allText()"));


        talk.setTranscript(String.join("\n", transcript));

        return talk;
    }


    private static Function<DocumentEvaluator, Talk> createEvalFunction() {
        return (de) -> {
            String url = de.getUrl().substring(26); // skip "https://www.ted.com/talks/"
            Talk talk = new Talk()
                    .setUrl(url);

            talk.setTitle(de.string("//*[@id='content']//h1/allText()"))
                    .setTalker(de.string("//*[@id='content']//*[contains(@class, 'f:.9 m-b:.4 m-t:.5 d:i-b')]/allText()"))
                    .setDateRecorded(de.string("//div[@id='content']//div[@class='f:.9 p-x:3@md c:black t-a:l']/div[@class='m-b:2']/div/span[1]/allText()"));

            talk.setIntroduction(de.string("//div[@id='content']//div[@class='Grid__cell w:3of4@md']/p[@class='l-h:n m-b:1']/allText()"));

            return talk;
        };
    }
}
