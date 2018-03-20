package cz.zcu.sdutends.kiwi.ted;

import cz.zcu.sdutends.kiwi.elastic.ElasticClient;
import cz.zcu.sdutends.kiwi.elastic.ElasticDao;
import cz.zcu.sdutends.kiwi.ted.model.TalkStructured;
import cz.zcu.sdutends.kiwi.ted.model.TranscriptBlock;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.time.ZoneId;
import java.util.concurrent.ExecutionException;

public class TedElasticDao extends ElasticDao<TalkStructured> {

    private ZoneId zoneId = ZoneId.systemDefault();

    public TedElasticDao(ElasticClient client, String index) {
        super(client, index, "talk");
    }

    public XContentBuilder toXcb(TalkStructured talk) throws IOException {


        XContentBuilder xb = XContentFactory.jsonBuilder()
                .startObject()
                .field("url", talk.getUrl())
                .field("title", talk.getTitle())
                .field("talker", talk.getTalker())
//                .field("dateRecorded", talk.getDateRecorded().atStartOfDay(zoneId).toEpochSecond())
                .field("dateRecorded", talk.getDateRecorded().atStartOfDay(zoneId).toEpochSecond() * 1000)
                .field("introduction", talk.getIntroduction());

        xb.startArray("transcript");
        for (TranscriptBlock transcriptBlock : talk.getTranscript()) {
            xb.startObject()
                    .field("time", transcriptBlock.getTime())
                    .field("text", transcriptBlock.getText())
                    .endObject();
        }
        xb.endArray();
        return xb.endObject();


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
