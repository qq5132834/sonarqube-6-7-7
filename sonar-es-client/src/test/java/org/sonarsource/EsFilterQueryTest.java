package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.util.concurrent.ExecutionException;

/***
 * query，根据你的查询条件，去计算文档的匹配度得到一个分数，并且根据分数进行排序，不会做缓存；
 *
 * filter，根据查询条件去查询文档，不去计算分数，而且filter会对经常被过滤的数据进行缓存。性能高。
 */
public class EsFilterQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * GET components/component/_search
     * {
     *   "query": {
     *     "bool": {
     *       "filter": [
     *         {
     *           "match": {
     *             "name": "Main.java"
     *           }
     *         }
     *       ]
     *     }
     *   }
     * }
     */
    @Test
    public void filterQuery() throws ExecutionException, InterruptedException {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.matchQuery("name", "Main.java"));

        builder.query(boolQueryBuilder);

        searchRequest.source(builder);

        SearchResponse searchResponse = this.client.search(searchRequest).get();
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }

}
