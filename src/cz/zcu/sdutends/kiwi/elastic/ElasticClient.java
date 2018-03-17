package cz.zcu.sdutends.kiwi.elastic;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterIndexHealth;
import org.elasticsearch.index.query.QueryBuilders;

public class ElasticClient {
    private static final Logger log = Logger.getLogger(ElasticClient.class);

    protected final TransportClient client;

    public ElasticClient(TransportClient client) {
        this.client = client;
    }

    public GetResponse getDocument(String index, String type, String id) {

        GetResponse getResponse = client.prepareGet(index, type, id).get();

        if (!getResponse.isExists()) {
            log.info("Document with id:" + id + " not found");
            return null;
        }

        return getResponse;
    }

    public void deleteDocument(String index, String type, String id) {
        DeleteResponse response = client.prepareDelete(index, type, id).get();
        log.info("Information on the deleted document:");
        log.info("Index: " + response.getIndex());
        log.info("Type: " + response.getType());
        log.info("Id: " + response.getId());
        log.info("Version: " + response.getVersion());
    }

    public String printConnectionInfo() {
        ClusterHealthResponse healthResponse = client.admin().cluster().prepareHealth().get();
        String clusterName = healthResponse.getClusterName();

        StringBuilder conInfo = new StringBuilder("Connected to Cluster: " + clusterName);
        conInfo.append(" Indices in cluster: ");
        for (ClusterIndexHealth health : healthResponse.getIndices().values()) {
            conInfo.append(String.format("Index: %10s; Status: %5s; Shards: %5d; Replicas: %5d;",
                    health.getIndex(), health.getStatus().toString(),
                    health.getNumberOfShards(), health.getNumberOfReplicas()));
        }

        return conInfo.toString();
    }

    public SearchResponse searchDocument(String index, String type, String field, String value) {

        //SearchType.DFS_QUERY_THEN_FETCH - more
        //https://www.elastic.co/blog/understanding-query-then-fetch-vs-dfs-query-then-fetch
        log.info("Searching \"" + value + "\" in field:" + "\"" + field + "\"");
        return client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)     //try change to SearchType.QUERY_THEN_FETCH - see the change in score
                .setQuery(QueryBuilders.matchQuery(field, value)) //Query match - simplest query
                .setFrom(0).setSize(30)                         //can be used for pagination
                .setExplain(true)
                .get();

    }

    public SearchResponse searchPhrase(String index, String type, String field, String value) {
        log.info("Searching phrase \"" + value + "\" in field:" + "\"" + field + "\"");
        return client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchPhraseQuery(field, value))
                .setFrom(0).setSize(30)
                .setExplain(true)
                .get();
    }

    TransportClient transport() {
        return client;
    }
}
