package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
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

public class EsIdQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * 根据一个id直接查询，where id = xxxx
     *
     * 例如：
     * GET /components/component/AX_8gu_waPZnazQacr1C
     */
    @Test
    public void idQuery() throws Exception {
        GetRequest getRequest = new GetRequest(this.index, type, "AX_8gu_waPZnazQacr1C");

        GetResponse getResponse = this.client.get(getRequest).get();
        System.out.println(getResponse.getSourceAsMap());

    }

    /***
     * 根据多个ids查询, where id in ('xxx1','xxx2')
     *
     * 例如：
     * GET /components/component/_search
     * {
     *   "query": {
     *     "ids": {
     *       "values": ["AX_8VRMXL1eC4oq6-LEu", "AX_8VWZbZd7P2uTtoVJm"]
     *     }
     *   }
     * }
     */
    @Test
    public void idsQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.idsQuery().addIds("AX_8VRMXL1eC4oq6-LEu","AX_8VWZbZd7P2uTtoVJm"));

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
