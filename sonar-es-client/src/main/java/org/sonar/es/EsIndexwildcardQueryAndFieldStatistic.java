package org.sonar.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.*;

/***
 * https://blog.csdn.net/m0_47333020/article/details/108803321
 *
 * http://192.168.32.140:5601/app/kibana#/dev_tools/console?_g=()
 */
public class EsIndexwildcardQueryAndFieldStatistic {
    private static Client client;
    private static String index = "blog21";
    private static String type = "article21";

    // 注意区分5.X系列的ES版本client初始化
    static {
        try {
            client = new EsClient().client();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // 创建索引
        CreateIndexResponse indexResponse = client.admin().indices()
                .prepareCreate(index).get();
        // 建立映射
        XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
                .startObject("properties").startObject("PolicyCode")
                .field("type", "keyword").field("store", "yes").endObject()
                .startObject("ServiceId").field("type", "keyword")
                .field("store", "yes").endObject().startObject("CreateTime")
                .field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss")
                .field("store", "yes").endObject().endObject().endObject();
        PutMappingRequest mappingRequest = Requests.putMappingRequest(index)
                .source(mapping).type(type);
        client.admin().indices().putMapping(mappingRequest).actionGet();

        // 插入数据
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            if (i % 2 == 0) {
                hashMap.put("PolicyCode", "5674504720");
                hashMap.put("ServiceId", "SE2");
                hashMap.put("CreateTime", "2016-08-21 00:00:01");
            } else {
                hashMap.put("PolicyCode", "666666666");
                hashMap.put("ServiceId", "SE3");
                hashMap.put("CreateTime", "2016-10-21 00:00:01");
            }
            client.prepareIndex(index, type).setSource(hashMap).get();
        }
        // 精确查询
        QueryBuilder qb1 = QueryBuilders.termQuery("PolicyCode", "5674504720");
        // 模糊查询， “*”表示0到多个字符，而使用“？”表示一个字符
        QueryBuilder qb2 = QueryBuilders.wildcardQuery("ServiceId", "*SE*");
        QueryBuilder qb3 = QueryBuilders.wildcardQuery("ServiceId", "SE?");
        QueryBuilder qb4 = QueryBuilders.wildcardQuery("ServiceId", "?SE?");
        QueryBuilder qb5 = QueryBuilders.wildcardQuery("ServiceId", "SE2?");
        QueryBuilder qb6 = QueryBuilders.wildcardQuery("ServiceId", "SE2*");
        QueryBuilder qb7 = QueryBuilders.wildcardQuery("ServiceId", "*SE3*");

//       SearchResponse response = client.prepareSearch(index)
//       .setTypes(type)
//       .setQuery(qb3)
//       .execute()
//       .actionGet();

        try {
            Thread.sleep(2000);     //ES在新建 索引库后并往里面插入数据时是异步的，需要等待一定时间，才能查询到索引库中的数据
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SearchResponse response = client.prepareSearch(index).setTypes(type)
                .setQuery(QueryBuilders.boolQuery().must(qb2)).get();

        SearchHits hits = response.getHits();
        if (hits.totalHits > 0) {
            for (SearchHit hit : hits) {
                System.out.println("score:" + hit.getScore() + ":\t"
                        + hit.getId());
            }
        } else {
            System.out.println("搜到0条结果");
        }
        //获取索引库字段
        List<String> fieldList = new ArrayList<String>();
        ClusterState cs = client.admin().cluster().prepareState()
                .setIndices(index).execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index(index);
        MappingMetaData mdd = imd.mapping(type);
        Map<String, Object> mapProperties = new HashMap<String, Object>();

        try {
            mapProperties = mdd.getSourceAsMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fieldList = getIndexFieldList("", mapProperties);

        System.out.println("Field List:");
        for (String field : fieldList) {
            System.out.println(field);
        }

        client.close();
    }

    private static List<String> getIndexFieldList(String fieldName,
                                                  Map<String, Object> mapProperties) {
        List<String> fieldList = new ArrayList<String>();
        Map<String, Object> map = (Map<String, Object>) mapProperties
                .get("properties");
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (((Map<String, Object>) map.get(key)).containsKey("type")) {
                fieldList.add(fieldName + "" + key);
            } else {
                List<String> tempList = getIndexFieldList(fieldName + "" + key
                        + ".", (Map<String, Object>) map.get(key));
                fieldList.addAll(tempList);
            }
        }
        return fieldList;
    }

}
