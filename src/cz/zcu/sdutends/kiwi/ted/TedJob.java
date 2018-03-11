package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.ir.crawling.HtmlDownloaderFactory;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.CrawlJobSettings;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.utils.ProgressRunnable;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

public class TedJob extends IrJob {

    private final TedCrawlerSettings settings;

    public TedJob(String... args) {
        this.settings = new TedCrawlerSettings(args);

        this.settings.setStorage("./storage/ted");

        settings.setLinksSource(CrawlJobSettings.DataSource.Load);
        settings.setLinksDataFile("2018-03-04_20_32_935_links_size_2707.txt");

        settings.setSkip(300).setLimit(10);
    }

    @Override
    public void run() {
        boolean allDirsExist = this.ensureDirectoriesExist(
                this.settings.getStorage(),
                this.settings.getStorage() + "/talks"
        );
        if(!allDirsExist) {
            return;
        }


        try (IHtmlDownloader downloader = new HtmlDownloaderFactory().create(HtmlDownloaderFactory.Type.Selenium)) {
            TedCrawler crawler = new TedCrawler(downloader);
            crawler.setPolitenessInterval(settings.getPoliteInterval());

            Collection<String> urls = getUrlSet(crawler);
            urls = urls.stream()
                    .skip(settings.getSkip()).limit(settings.getLimit())
                    .collect(Collectors.toList());

            new ProgressRunnable<>(urls)
                    .run((url) -> {
                        Talk talk = crawler.retrieveTalk(url);
                        String filename = Utils.time() + "_talk_" + talk.getUrl() + ".txt";
                        io.save(settings.getStorageFile("talks", filename), talk);
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    protected Collection<String> getUrlSet(TedCrawler crawler) {
        switch (settings.getLinksSource()) {
            case Load:
                return loadUrls(settings.getStorageFile(settings.getLinksDataFile()));
            case Fetch:
                Collection<String> urlsSet = crawler.fetchTalkLinks();
                Utils.saveFile(new File(settings.getStorageFile(Utils.time() + "_links_size_" + urlsSet.size() + ".txt")),
                        urlsSet);
                return urlsSet;
            default:
                throw new UnsupportedOperationException("Links source method not supported");

        }
    }
}
