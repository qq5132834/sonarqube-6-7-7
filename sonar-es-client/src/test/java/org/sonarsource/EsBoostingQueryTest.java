package org.sonarsource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoostingQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import javax.management.Query;
import java.net.UnknownHostException;

/***
 * boosting查询可以帮助我们去影响查询后的score分数
 *
 * 1、positive：只有匹配上positive的查询的内容，才会被放到返回的结果集中；
 * 2、negative: 如果匹配上的positive并且也匹配上了negative，将就降低这样的文档的score；
 * 3、negative_boost： 指定系统，必须小于1.0。
 *
 *
 * 关于查询时，分数是如何计算的：
 * 1、搜索的关键字在文档中出现的批次越高，分数就越高；
 * 2、指定的文档内容越短，分数就越高；
 * 3、我们在搜索时，指定的关键字也会被分词，这个被分词的内容，被分词库匹配的个数越多，分数越高。
 */
public class EsBoostingQueryTest {

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
     *     "boosting": {
     *       "positive": {
     *         "match": {
     *             "name": "Main.java"
     *         }
     *       },
     *       "negative": {
     *         "match": {
     *             "name": "Main.java"
     *         }
     *       },
     *       "negative_boost": 0.5
     *     }
     *   }
     * }
     */
    @Test
    public void boostingQueryTest() throws  Exception {
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //
        BoostingQueryBuilder boostingQueryBuilder =
                QueryBuilders.boostingQuery(QueryBuilders.matchQuery("name", "Main.java"),
                QueryBuilders.matchQuery("name", "Main.java")).negativeBoost(0.5f);

        //
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(boostingQueryBuilder);

        searchRequest.source(builder);

        SearchResponse searchResponse = this.client.search(searchRequest).get();

        //
        long total = searchResponse.getHits().totalHits;
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }


    }

}
