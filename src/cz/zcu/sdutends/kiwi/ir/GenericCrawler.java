package cz.zcu.sdutends.kiwi.ir;

import cz.zcu.sdutends.kiwi.cdNemovitosti.CdNemovitostiCrawler;
import org.apache.log4j.Logger;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public final class GenericCrawler {
    private static Logger log = Logger.getLogger(GenericCrawler.class);

    private final ACrawler crawler;
    private Map<String, PrintStream> printStreamMap = new HashMap<>();

    private Map<String, XPathEvaluator> actions = new HashMap<>();

    public GenericCrawler(ACrawler crawler) {
        this.crawler = crawler;
    }

    public void addAction(String action, String xPath) {
        this.addAction(action, Xsoup.compile(xPath));
    }

    public void addAction(String action, XPathEvaluator evaluator) {
        this.actions.put(action, evaluator);
    }

    public void processResultUrl(String url) {
        Map<String, Map<String, List<String>>> results = new HashMap<>();
        for (String key : actions.keySet()) {
            results.put(key, new HashMap<>());
        }

        String link = crawler.normalizeUrl(url);

        //Download and extract data according to xpathMap
        crawler.bePolite();
        Map<String, List<String>> httpResult = crawler.downloader.processUrl(link, (de) -> actions.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().evaluate(de.getDocument()).list())));

        for (Map.Entry<String, Map<String, List<String>>> action : results.entrySet()) {
            // map of given action
            Map<String, List<String>> map = action.getValue();

            List<String> list = httpResult.get(action.getKey());
            if (list == null) {
                continue;
            }

            map.put(url, list);
            log.info(Arrays.toString(list.toArray()));
            //print
            PrintStream printStream = printStreamMap.get(action.getKey());
            for (String result : list) {
                printStream.println(url + "\t" + result);
            }
        }
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

    public void closePrintStreams() {
        printStreamMap.values().forEach(PrintStream::close);
        printStreamMap.clear();
    }

    public Collection<String> actionNames() {
        return actions.keySet();

    }
}
