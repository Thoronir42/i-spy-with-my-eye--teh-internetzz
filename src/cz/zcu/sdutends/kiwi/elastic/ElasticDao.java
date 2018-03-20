package cz.zcu.sdutends.kiwi.elastic;

import cz.zcu.sdutends.kiwi.IEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public abstract class ElasticDao<T extends IEntity> {
    private static Logger log = Logger.getLogger(ElasticDao.class);

    protected final ElasticClient client;
    protected final String index;
    protected final String type;

    public ElasticDao(ElasticClient client, String index, String type) {
        this.client = client;
        this.index = index;
        this.type = type;
    }

    public abstract XContentBuilder toXcb(T entity) throws IOException;

    protected TransportClient transport() {
        return client.transport();
    }

    //XContentBuilder - ES helper
    public IndexRequestBuilder prepareIndex(T talk) throws IOException {
        return transport().prepareIndex(this.index, "talk", talk.getUrl())
                .setSource(this.toXcb(talk));
    }


    public void bulkIndex(Collection<T> entities) {
        BulkRequestBuilder bulkRequest = transport().prepareBulk();

        for (T entity : entities) {
            try {
                bulkRequest.add(this.prepareIndex(entity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            log.warn("Bulk Indexing failures: " + bulkResponse.buildFailureMessage());
        } else {
            log.info("Bulk index request complete");
        }

        //update bulk etc..
        //bulkRequest.add(client.prepareUpdate(indexName,typeName,id))
    }
}
