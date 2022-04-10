package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

/***
 * range范围查询，只针对数值类型，对某一个field进行大于小于范围指定。
 */
public class EsRangeQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /**
     * 年龄大于等于10，小于等于20
     * 例如：
     * GET components/component/_search
     * {
     *   "query" : {
     *     "range": {
     *       "age": {
     *         "gte": 10,   //大于等于    gt 大于
     *         "lte": 20    //小于等于    lt 小于
     *       }
     *     }
     *   }
     * }
     * */
    @Test
    public void rangeTest() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.rangeQuery("age").gte(10).lte(20));  //大于等于10，小于等于20

        //
        searchRequest.source(builder);

        //
        SearchResponse searchResponse = this.client.search(searchRequest).get();

        //
        long total = searchResponse.getHits().totalHits;
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }
    }


}
