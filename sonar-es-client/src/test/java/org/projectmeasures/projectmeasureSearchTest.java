package org.projectmeasures;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class projectmeasureSearchTest {
    private Client client;

    public static String index = "projectmeasures";
    public static String type = "projectmeasure";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    @Test
    public void search() throws ExecutionException, InterruptedException {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(builder);

        SearchResponse searchResponse = this.client.search(searchRequest).get();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        long total = searchResponse.getHits().getTotalHits();
        for (SearchHit hit : searchHits) {
            System.out.println(hit.getSourceAsMap());
        }

    }

}
