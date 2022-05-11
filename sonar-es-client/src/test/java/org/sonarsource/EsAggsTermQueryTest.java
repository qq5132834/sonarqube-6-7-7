package org.sonarsource;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/***
 * term聚合：词条的聚合
 *
 * terms聚合为字段中每个词条返回一个桶。
 * 这允许你生成字段每个值的统计：例如每年出版多少书。
 * 类似于sql语句：count(book) group by year
 */
public class EsAggsTermQueryTest {

    private Client client;

    private String index = "projectmeasures";
    private String type = "projectmeasure";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * GET projectmeasures/projectmeasure/_search
     * {
     *   "size": 0,
     *   "aggs": {
     *     "agg": {
     *       "nested": {
     *         "path": "measures"
     *       },
     *       "aggs": {
     *         "term": {
     *           "terms": {
     *             "field": "measures.key",
     *             "size": 100
     *           }
     *         }
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

        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("agg", "measures")
                        .subAggregation(AggregationBuilders.terms("term").field("measures.key").size(100));

        builder.aggregation(nestedAggregationBuilder);

        //
        searchRequest.source(builder);
        //
        SearchResponse searchResponse = client.search(searchRequest).get();
        Aggregations aggregations = searchResponse.getAggregations();
        InternalNested internalNested = (InternalNested) aggregations.asMap().get("agg");
        InternalTerms internalTerms = internalNested.getAggregations().get("term");
        Collection<InternalTerms.Bucket> testBuckets= internalTerms.getBuckets();
        for(InternalTerms.Bucket bucket : testBuckets){
            bucket.getKey();
            bucket.getDocCount();
            System.out.println(bucket.getKey() + "," + bucket.getDocCount());
        }
        System.out.println("");
    }

}
