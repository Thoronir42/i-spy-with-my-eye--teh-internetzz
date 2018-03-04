package cz.zcu.sdutends.kiwi.ted;

import com.google.common.base.Function;
import cz.zcu.kiv.nlp.ir.crawling.HtmlDownloaderFactory;
import cz.zcu.kiv.nlp.ir.crawling.IHtmlDownloader;
import cz.zcu.kiv.nlp.tools.Utils;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.IrJobSettings;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

public class TedJob extends IrJob {

    private final TedSettings settings;

    public TedJob(String ...args) {
        this.settings = new TedSettings(args);

        this.settings.setStorage("./storage/ted-talks");

        settings.setLinksSource(IrJobSettings.DataSource.Load);
        settings.setLinksDataFile("2018-03-04_20_32_935_links_size_2707.txt");

        settings.setSkip(245).setLimit(55);
    }

    @Override
    public void run() {
        if (!Utils.ensureDirectoryExists(settings.getStorage())) {
            System.exit(1);
        }

        try (IHtmlDownloader downloader = new HtmlDownloaderFactory().create(HtmlDownloaderFactory.Type.Selenium)){
            TedCrawler crawler = new TedCrawler(downloader);
            crawler.setPolitenessInterval(1200);

            Collection<String> urls = getUrlSet(crawler);
            urls = urls.stream()
                    .skip(settings.getSkip()).limit(settings.getLimit())
                    .collect(Collectors.toList());

            runProgress(urls, (url) ->{
                Talk talk = crawler.retrieveTalk(url);
                String filename = "talk_" + talk.getUrl() + ".txt";
                io.save(settings.getStorageFile(filename, true), talk);
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
                Utils.saveFile(new File(settings.getStorageFile("links_size_" + urlsSet.size() + ".txt", true)),
                        urlsSet);
                return urlsSet;
            default:
                throw new UnsupportedOperationException("Links source method not supported");

        }
    }
}
