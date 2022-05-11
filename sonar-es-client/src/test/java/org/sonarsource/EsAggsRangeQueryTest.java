package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/***
 * 聚合查询，返回查询
 */
public class EsAggsRangeQueryTest {

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
     *       "range": {
     *         "field": "user_id",
     *         "ranges": [
     *           {
     *             "from": 0,
     *             "to": 100
     *           }
     *         ]
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void rangeQuery() throws ExecutionException, InterruptedException {
        //
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        RangeAggregationBuilder rangeAggregationBuilder =
                AggregationBuilders.range("agg").addRange(0d, 100d).field("user_id");
        builder.aggregation(rangeAggregationBuilder);
        //
        searchRequest.source(builder);
        //
        SearchResponse searchResponse = client.search(searchRequest).get();
        Range range = searchResponse.getAggregations().get("agg");
        System.out.println(range.getBuckets().get(0).getDocCount());

    }

}
