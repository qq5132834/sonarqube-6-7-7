package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

/***
 * 模糊查询，不同于sql中的like
 */
public class EsFuzzyQueryTest {

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
     *   "query" : {
     *     "fuzzy": {
     *       "name": {
     *         "value": "sonor",
     *         "prefix_length": 3    # 前面 3 个字符是不允许错的
     *       }
     *     }
     *   }
     * }
     *
     * 注意其中的sonor写错了，应该sonar。一样可以查询到结构
     */
    @Test
    public void fuzzyQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.fuzzyQuery("name", "sonor").prefixLength(3)); //前3个字符不允许错

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
