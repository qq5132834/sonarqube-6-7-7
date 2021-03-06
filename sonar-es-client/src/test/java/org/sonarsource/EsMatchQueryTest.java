package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.util.Map;

/***
 * match查询属于高层查询，他会根据你查询的字段类型不一样，采用不同的查询方式
 * 1.查询的是日期或者是数值的话，他会将你基于的字符串查询转换为日期或数值对待；
 * 2.如果查询的内容是一个不能被分词的内容（keyword），match查询不会对你的查询关键字进行分词；
 * 3.如果查询的内容是一个可以被分词的内容（text），match会将你指定的查询内容根据一定的方式去分词。去分词库中匹配指定的内容；
 *
 * 4.match查询，底层实际就是多个term查询，将多个term查询的结果给你封装到了一起；
 * 5.match_all查询；
 * 6、match查询；
 * 7、布尔match查询；
 */
public class EsMatchQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * 多个 field
     * 例如：
     * GET components/component/_search
     * {
     *   "query": {
     *      "multi_match": {
     *        "query": "test scanner",
     *        "fields": ["qualifier", "name"]
     *      }
     *     }
     *   }
     * }
     *
     */
    @Test
    public void multi_matchQuery() throws Exception{

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.multiMatchQuery("test scanner", "qualifier", "name"));

        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
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


    /***
     * GET components/component/_search
     * {
     *   "query": {
     *     "match": {
     *       "name": "test scanner"
     *     }
     *   }
     * }
     *
     * 这里会对查询字段 name的查询值进行分词为  test、 scanner 两个词，匹配包含test 或 scanner的。与下面的方式等价
     *
     * GET components/component/_search
     * {
     *   "query": {
     *     "match": {
     *       "name": {
     *         "query": "test scanner",
     *         "operator": "or"  # or、and
     *       }
     *     }
     *   }
     * }
     *
     */
    @Test
    public void matchQuery() throws Exception{

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.matchQuery("name", "test scanner"));
        //这里会对"test scanner"进行分词为 test、scanner 两个单词，只需要匹配其中一个即可。term 就不会进行分词
        builder.query(QueryBuilders.matchQuery("name", "test scanner").operator(Operator.OR));  //or的布尔match与上面等价
        builder.query(QueryBuilders.matchQuery("name", "test scanner").operator(Operator.AND)); //同事匹配两个分词结果

        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
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

    /***
     * 例如：
     * GET components/component/_search
     * {
     *   "query": {
     *     "match_all": {
     *
     *     }
     *   }
     * }
     */
    @Test
    public void matchAllQuery() throws Exception{

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(0);
        builder.size(5);
        builder.query(QueryBuilders.matchAllQuery());  //设置match_all查询

        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);
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
