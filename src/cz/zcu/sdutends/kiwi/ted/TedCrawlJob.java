package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.kiv.nlp.ir.crawling.HtmlDownloaderFactory;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.sdutends.kiwi.CrawlJobSettings;
import cz.zcu.sdutends.kiwi.ir.CrawlJob;
import cz.zcu.sdutends.kiwi.ted.model.Talk;
import cz.zcu.sdutends.kiwi.ted.serdes.TalkSerDes;
import cz.zcu.sdutends.kiwi.utils.AdvancedIO;
import cz.zcu.sdutends.kiwi.utils.ProgressRunnable;

import java.util.Collection;
import java.util.stream.Collectors;

public class TedCrawlJob extends CrawlJob {

    private final TedCrawlerSettings settings;
    private final AdvancedIO<Talk> aio;

    public TedCrawlJob(String... args) {
        this.settings = new TedCrawlerSettings(args);

        this.settings.setStorage("./storage/ted");

        settings.setLinksSource(CrawlJobSettings.DataSource.Load);
        settings.setLinksDataFile("2018-03-04_20_32_935_links_size_2707.txt");

        settings.setSkip(300).setLimit(10);

        this.aio = new AdvancedIO<>(new TalkSerDes());
    }

    @Override
    public boolean execute() {
        String talksDirectory = "talks-" + this.time();

        boolean allDirsExist = this.ensureDirectoriesExist(
                this.settings.getStorage(),
                this.settings.getStorage() + "/" + talksDirectory
        );
        if (!allDirsExist) {
            return false;
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
                        String filename = this.time() + "_talk_" + talk.getUrl() + ".txt";

                        aio.save(settings.getStorageFile(talksDirectory, filename), talk);
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    protected Collection<String> getUrlSet(TedCrawler crawler) {
        switch (settings.getLinksSource()) {
            case Load:
                return this.loadUrls(settings.getStorageFile(settings.getLinksDataFile()));

            case Fetch:
                Collection<String> urlsSet = crawler.fetchTalkLinks();
                String fileName = "links_size_" + urlsSet.size() + "__" + this.time() + ".txt";
                this.saveUrls(settings.getStorageFile(fileName), urlsSet);
                return urlsSet;

            default:
                throw new UnsupportedOperationException("Links source method not supported");

        }
    }
}
