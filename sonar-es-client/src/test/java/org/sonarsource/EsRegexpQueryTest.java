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
 * 正则查询，通过编写的正则表达式去匹配内容
 *
 * prefix、fuzzy、wildCard、regexp 查询效率比较低
 */
public class EsRegexpQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }


    @Test
    public void regexpTest() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.regexpQuery("mobile", "180[0-9]{8}")); //电话号是180开头的电话号码

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
