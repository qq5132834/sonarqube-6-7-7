package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

/***
 *
 * from + size分页原理：
 * ES查询数据的方式，
 * 第一步将用户指定的关键字进行分词；
 * 第二步将词汇去分词库中进行检索，得到多个文档id；
 * 第三步去各个分片中去拉取指定的数据；【耗时】
 * 第四步将数据根据score进行排序；
 * 第五步from的值将查询到的数据舍弃一部分；
 * 第六步返回结果
 *
 * from + size分页的缺陷：from、size是有限制的，from和size二者之和不能超过1W
 *=============================================================
 *
 * scroll深分页， 默认排序方式id排序
 * scroll + size分页原理：
 * 1、第一步现将用户指定的关键字进行分词；
 * 2、将词汇去分词库中进行检索，得到多个文档id；
 * 3、将文档的id存在一个ES的上下文中；
 * 4、根据你指定的size的个数去ES检索指定个数的数据，拿完数据的文档id，会从上下文中移除；
 * 5、如果需要下一页数据，直接去ES的上下文中找后续内容。
 *
 * scroll查询方式的缺点，不适合做实时的查询
 *
 */
public class EsScrollQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }


    /***
     * GET components/component/_search?scroll=1m    #在内存中保存时间1分钟
     * {
     *   "query" : {
     *     "match_all": {}
     *   },
     *   "size": 2,
     *   "sort": [
     *     {
     *       "name": {
     *         "order": "desc"    #不采用默认id排序，而是指定排序方式
     *       }
     *     }
     *   ]
     * }
     *
     * ####################################
     *
     *
     * 返回结果中存在 scroll_id 的返回值，查询下一页直接到上scroll_id即可直接查询。
     *
     * GET /_search/scroll
     * {
     *   "scroll_id" : "DnF1ZXJ5VGhlbkZldGNoBQAAAAAAADNHFjB0TzVQajNRUXZTWFUxZzQ0b2JiTEEAAAAAAAAzSBYwdE81UGozUVF2U1hVMWc0NG9iYkxBAAAAAAAAM0kWMHRPNVBqM1FRdlNYVTFnNDRvYmJMQQAAAAAAADNKFjB0TzVQajNRUXZTWFUxZzQ0b2JiTEEAAAAAAAAzSxYwdE81UGozUVF2U1hVMWc0NG9iYkxB",
     *   "scroll" : 1m  #需要再次指定保存时长，不然查询完后就会从内容中失效
     * }
     *
     * #####################################
     *
     * 删除scroll
     *
     * DELETE /_search/scroll/DnF1ZXJ5VGhlbkZldGNoBQAAAAAAADNHFjB0TzVQajNRUXZTWFUxZzQ0b2JiTEEAAAAAAAAzSBYwdE81UGozUVF2U1hVMWc0NG9iYkxBAAAAAAAAM0kWMHRPNVBqM1FRdlNYVTFnNDRvYmJMQQAAAAAAADNKFjB0TzVQajNRUXZTWFUxZzQ0b2JiTEEAAAAAAAAzSxYwdE81UGozUVF2U1hVMWc0NG9iYkxB
     */
    @Test
    public void scrollQuery() throws Exception {

        final int SIZE = 4;

        //创建 SearchRequest
        SearchRequest searchRequest = new SearchRequest(this.index);
        searchRequest.types(type);

        //指定scroll信息
        searchRequest.scroll(TimeValue.timeValueMinutes(1));

        //指定查询条件
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(SIZE);
        builder.sort("name", SortOrder.DESC);
        builder.query(QueryBuilders.matchAllQuery());

        searchRequest.source(builder);

        //指定查询
        SearchResponse searchResponse = this.client.search(searchRequest).get();

        //获取scrollId
        String scrollId = searchResponse.getScrollId();
        for (SearchHit hit : searchResponse.getHits()) {
            System.out.println(hit.getSourceAsMap());
        }

        //循环读取下一页
        while (true) {
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(TimeValue.timeValueMillis(1)); //指定保存时间1分钟

            SearchResponse searchScrollResponse = client.searchScroll(searchScrollRequest).get();
            if(searchScrollResponse.getHits().getHits() != null
                    && searchResponse.getHits().getHits().length > 0){
                System.out.println("----------------下一页------------");
                for (SearchHit hit : searchScrollResponse.getHits()) {
                    System.out.println(hit.getSourceAsMap());
                }
            }
            else {
                System.out.println("----------------结--束------------");
                break;
            }
        }

        //删除scroll
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest).get();
        System.out.println(clearScrollResponse.getNumFreed());

    }

}
