package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.elastic.ElasticClient;
import cz.zcu.sdutends.kiwi.elastic.ElasticDao;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TedElasticDao extends ElasticDao<Talk> {

    public TedElasticDao(ElasticClient client, String index) {
        super(client, index, "talk");
    }

    public Map<String, Object> toJson(Talk talk) {
        HashMap<String, Object> json = new HashMap<>();

        json.put("url", talk.getUrl());
        json.put("title", talk.getTitle());
        json.put("talker", talk.getTalker());
        json.put("dateRecorded", talk.getDateRecorded());

        json.put("introduction", talk.getIntroduction());
        json.put("transcript", talk.getTranscript());

        return json;
    }

    public XContentBuilder toXcb(Talk talk) throws IOException {
        return XContentFactory.jsonBuilder()
                .startObject()
                .field("url", talk.getUrl())
                .field("title", talk.getTitle())
                .field("talker", talk.getTalker())
                .field("dateRecorded", talk.getDateRecorded())
                .field("introduction", talk.getIntroduction())
                .field("transcript", talk.getTranscript())
                .endObject();
    }


    public GetResponse getTalkDocument(String id) {
        return client.getDocument(this.index, "talk", id);
    }

    //allows partial updates  - not whole doc
    public UpdateResponse updateTalkPartial(String id, String field, Object newValue) throws IOException, ExecutionException, InterruptedException {
        XContentBuilder source = XContentFactory.jsonBuilder()
                .startObject()
                .field(field, newValue)
                .endObject();
        UpdateRequest updateRequest = new UpdateRequest()
                .index(this.index).type("talk").id(id)
                .doc(source);

        return transport().update(updateRequest).get();
    }

    public SearchResponse searchTalkDocument(String field, String value) {
        return client.searchDocument(index, "talk", field, value);
    }

    public SearchResponse searchTalkPhrase(String field, String value) {
        return client.searchPhrase(index, "talk", field, value);
    }

    public void deleteTalkDocument(String id) {
        client.deleteDocument(index, "talk", id);
    }
}
