package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.duplications.detector.suffixtree.Search;
import org.sonar.es.MyEsClient;

import java.util.Map;

/***
 * term查询是完全匹配，搜索之前不会对你搜索的关键字进行分词，对你的关键字去文档分词库中去匹配内容
 *
 */

public class EsTermQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

/**
 * 例如：kibana
 * GET components/component/_search
 * {
 *   "from": 0,
 *   "size": 20,
 *   "query": {
 *     "term": {
 *       "uuid": {
 *         "value": "AX_8VRMXL1eC4oq6-LEu"
 *       }
 *     }
 *   }
 * }
 */
    @Test
    public void termQuery() throws Exception {

        //指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        QueryBuilder term = QueryBuilders.termQuery("uuid", "AX_8VRMXL1eC4oq6-LEu");
        builder.query(term);

        //创建request
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        //执行查询条件
        searchRequest.source(builder);

        //执行查询
        SearchResponse searchResponse = this.client.search(searchRequest).get();

        //
        long total = searchResponse.getHits().getTotalHits();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        for (SearchHit searchHit : searchHits) {
            Map<String, Object> map = searchHit.getSourceAsMap();
            System.out.println(map.toString());
        }
    }

}
