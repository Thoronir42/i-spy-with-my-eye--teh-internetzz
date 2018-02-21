package cz.zcu.kiv.nlp.ir;

import org.jsoup.nodes.Document;
import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

import java.util.List;

public class DocumentEvaluator {
    private final Document document;
    private String url;

    public DocumentEvaluator(Document document, String url){
        this.document = document;
        this.url = url;
    }

    public String string(String xPath) {
        return string(Xsoup.compile(xPath));
    }

    public String string(XPathEvaluator xPath) {
        return xPath.evaluate(document).get();
    }

    public List<String> strings(String xPath) {
        return strings(Xsoup.compile(xPath));
    }

    public List<String> strings(XPathEvaluator xPath) {
        return xPath.evaluate(document).list();
    }

    public Document getDocument() {
        return document;
    }

    public String getUrl() {
        return url;
    }
}
