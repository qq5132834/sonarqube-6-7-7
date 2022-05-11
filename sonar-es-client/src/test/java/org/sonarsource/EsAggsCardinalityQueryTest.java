package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/***
 * 聚合查询【去重计数】
 */
public class EsAggsCardinalityQueryTest {
    private Client client;

    private String index = "components";
    private String type = "component";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * GET components/component/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "cardinality": {
     *         "field": "name"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void cardinalityTest() throws ExecutionException, InterruptedException {
        //
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        CardinalityAggregationBuilder cardinalityAggregationBuilder =
                AggregationBuilders.cardinality("agg").field("name");
        builder.aggregation(cardinalityAggregationBuilder);
        //
        searchRequest.source(builder);
        //
        SearchResponse searchResponse = client.search(searchRequest).get();
        Cardinality cardinality = searchResponse.getAggregations().get("agg");
        System.out.println(cardinality.getValue());
    }

}
