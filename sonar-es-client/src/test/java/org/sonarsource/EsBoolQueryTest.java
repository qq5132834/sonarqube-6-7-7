package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;

/***
 * 复合过滤器，将你的多个查询条件，以一定的逻辑组合在一起
 *
 * 1、must：所有的条件，用must组合在一起，表示  and 意思；
 * 2、must_not : 将must_not 中的条件，全部都不匹配，表示 not 的意思；
 * 3、should：所有的条件，用should组合在一起，表示 or 的意思。
 *
 */
public class EsBoolQueryTest {

    private Client client;

    public static String index = "person";
    public static String type = "man";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }



    /***
     * GET components/component/_search
     * {
     *   "query" : {
     *      "bool": {
     *        "should": [
     *          {
     *            "term": {
     *              "language": {
     *                "value": "java"
     *              }
     *            }
     *          },
     *          {
     *            "term": {
     *              "language": {
     *                "value": "txt"
     *              }
     *            }
     *          }
     *        ],
     *        "must_not": [
     *          {
     *            "prefix": {
     *              "uuid": {
     *                "value": "sonar"
     *              }
     *            }
     *          }
     *        ],
     *        "must": [
     *          {
     *            "term": {
     *              "qualifier": {
     *                "value": "FIL"
     *              }
     *            }
     *          }
     *        ]
     *      }
     *   }
     * }
     */
    @Test
    public void boolQuery() throws Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //添加should
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.termQuery("language", "java"));
        boolQueryBuilder.should(QueryBuilders.termQuery("language", "txt"));

        //添加must_not
        boolQueryBuilder.mustNot(QueryBuilders.prefixQuery("uuid", "sonar"));

        //添加must
        boolQueryBuilder.must(QueryBuilders.termQuery("qualifier", "FIL"));


        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boolQueryBuilder);
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
