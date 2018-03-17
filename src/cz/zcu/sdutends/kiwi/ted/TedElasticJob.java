package cz.zcu.sdutends.kiwi.ted;

import com.thedeanda.lorem.LoremIpsum;
import cz.zcu.sdutends.kiwi.IrJob;
import cz.zcu.sdutends.kiwi.elastic.ElasticClient;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class TedElasticJob extends IrJob {

    private static final Logger log = Logger.getLogger(TedElasticJob.class);

    private final TedElasticSettings settings;

    public TedElasticJob(String ...args) {
        this.settings = new TedElasticSettings(args)
                .setClusterName("elastic-kiwi")
                .setIndex("kiwi-ted");
    }

    @Override
    public boolean execute() throws IOException {

        //if cluster name is different than "elasticsearch"
        Settings settings = Settings.builder()
                .put("cluster.name", this.settings.getClusterName())
                .put("network.bind_host", 0)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);

        client.addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        ElasticClient ec = new ElasticClient(client);
        TedElasticDao tem = new TedElasticDao(ec, this.settings.getIndex());

        log.info(ec.printConnectionInfo());

        IndexResponse response;

        response = tem.prepareIndex(randTalk("url")).get();
        printResponse(response);


        response = tem.prepareIndexXcb(randTalk("elser")).get();
        printResponse(response);

        response = tem.prepareIndexXcb(randTalk()).get();
        printResponse(response);


        //get document by id
        printDocument(tem.getTalkDocument("url"));
        printDocument(tem.getTalkDocument("elser"));


        //update documents
        try {
            tem.updateTalkPartial("url", "title", "ElasticSearch: Java API");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        printDocument(tem.getTalkDocument("url"));


        try {
            tem.updateTalkPartial("url", "tags", new String[]{"bigdata", "really"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        printDocument(tem.getTalkDocument("url"));

        //searching documents
        System.out.println("");
        printSearchResponse(tem.searchTalkDocument("title", "ElasticSearch"));
        printSearchResponse(tem.searchTalkDocument("transcript", "provides Java API"));
        printSearchResponse(tem.searchTalkPhrase("transcript", "provides Java API"));


        //delete documents
        tem.deleteTalkDocument("url");
        printDocument(tem.getTalkDocument("url"));

        //bulk example
        //bulkIndex(docsXContent, client);

        //close connection
        client.close();

        return true;
    }

    private static void printDocument(GetResponse resp) {
        if(resp == null) {
            log.warn("Response is null");
            return;
        }
        
        Map<String, Object> source = resp.getSource();

        log.info("------------------------------");
        log.info("Retrieved document");
        log.info("Index: " + resp.getIndex());
        log.info("Type: " + resp.getType());
        log.info("Id: " + resp.getId());
        log.info("Version: " + resp.getVersion());
        log.info("Document title: " + source.get("title"));
        log.info(source.toString());
        log.info("------------------------------");

        //parsing - mannualy, deserialize JSON to object...
        String title = (String) source.get("title");
        String content = (String) source.get("content");
    }

    private static void printSearchResponse(SearchResponse response) {
        SearchHit[] results = response.getHits().getHits();
        log.info("Search complete");
        log.info("Search took: " + response.getTook().getMillis() + " ms");
        log.info("Found documents: " + response.getHits().totalHits);

        for (SearchHit hit : results) {
            log.info("--------");
            log.info("Doc id: " + hit.getId());
            log.info("Score: " + hit.getScore());
            String result = hit.getSourceAsString();
            log.info(result);
            //hit.getSourceAsMap();
        }
        log.info("------------------------------");
        System.out.println("");
    }

    private static void printResponse(IndexResponse response) {
        // Index name
        String _index = response.getIndex();
        // Type name
        String _type = response.getType();
        // Document ID (generated or not)
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)
        long _version = response.getVersion();
        // isCreated() is true if the document is a new one, false if it has been updated
        boolean created = response.status() == RestStatus.CREATED;
        log.info("Doc indexed to index: " + _index + " type: " + _type + " id: " + _id + " version: " + _version + " created: " + created);
    }

    private static Talk randTalk() {
        return randTalk(null);
    }

    private static Talk randTalk(String url) {
        LoremIpsum li = LoremIpsum.getInstance();
        if(url == null) {
            url = li.getWords(1);
        }

        return new Talk()
                .setUrl(url)
                .setTitle(li.getWords(4, 10))
                .setDateRecorded("when")
                .setTalker(li.getName())
                .setIntroduction(li.getParagraphs(1, 3))
                .setTranscript(li.getParagraphs(6, 10));
    }

    //alternative update
    public static void prepareUpdateDocument(Client client, String index, String type,
                                             String id, String field, Object newValue) throws IOException {
        client.prepareUpdate(index, type, id)
                .setDoc(XContentFactory.jsonBuilder()
                        .startObject()
                        .field(field, newValue)
                        .endObject())
                .get();
    }

}
