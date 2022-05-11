package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/***
 * 统计field的最大值、最小值、平均值、平方和等所有度量类型的统计
 */
public class EsAggsStatisticsQueryTest {

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
     *       "extended_stats": {
     *         "field": "user_id"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void extendedStats() throws ExecutionException, InterruptedException {
        //
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        ExtendedStatsAggregationBuilder extendedStatsAggregationBuilder =
                AggregationBuilders.extendedStats("agg").field("user_id");
        builder.aggregation(extendedStatsAggregationBuilder);
        //
        searchRequest.source(builder);
        //
        SearchResponse searchResponse = client.search(searchRequest).get();
        ExtendedStats extendedStats = searchResponse.getAggregations().get("agg");
        System.out.println(extendedStats.getMax());
        System.out.println(extendedStats.getMin());

    }



}
