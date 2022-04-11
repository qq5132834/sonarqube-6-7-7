package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;

/***
 * 去重计数
 */
public class EsCardinalityAggTest {

    private Client client;

    public static String index = "components";
    public static String type = "component";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * 例如：
     * GET components/component/_search
     * {
     *   "aggs": {
     *     "agg": {
     *       "cardinality": {
     *         "field": "language"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void cardinalityAgg() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.aggregation(AggregationBuilders.cardinality("agg").field("language"));

        searchRequest.source(builder);

        SearchResponse searchResponse = this.client.search(searchRequest).get();

        Cardinality cardinality = searchResponse.getAggregations().get("agg");
        System.out.println(cardinality.getValue());

    }


}
