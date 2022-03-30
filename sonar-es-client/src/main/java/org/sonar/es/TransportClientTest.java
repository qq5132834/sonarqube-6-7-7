package org.sonar.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TransportClientTest {

    private static String index = "blog21";
    private static String type = "article21";
    private TransportClient transportClient;

    public TransportClientTest() {
        try {
            this.transportClient = new EsClient().client();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        TransportClientTest transportClientTest = new TransportClientTest();
        transportClientTest.testAdd();
    }

    public void createIndex() throws Exception {
        String index = "blog21";
        String type = "article21";
        // 创建索引
        CreateIndexResponse indexResponse = transportClient.admin().indices()
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
        transportClient.admin().indices().putMapping(mappingRequest).actionGet();
    }

    public void testAdd() throws Exception {
        String index = "people";
        String type = "student";

        // 要新增的文档参数
        String name = "rkyao";
        String address = "SD";
        int age = 25;
        Date birthday = new Date();
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("name", name)
                .field("address", address)
                .field("age", age)
                .field("birthday", birthday.getTime())
                .endObject();

        // 发送新增请求
        IndexResponse response = transportClient.prepareIndex(index, type)
                .setSource(builder)
                .get();
        // 新增文档的id
        String id = response.getId();
        System.out.println(id);
    }

    public void testGet() throws Exception {
        String index = "people";
        String type = "student";

        String id = "AXNn8Ab1Gys1ttDyexel";
        // 发送查询请求
        GetResponse response = transportClient.prepareGet(index, type, id).get();
        if (!response.isExists()) {
            System.out.println("Not found.");
            return;
        }
        // 查询结果
        Map<String, Object> resultMap = response.getSource();
        System.out.println(resultMap);
    }

    public void testQuery() throws Exception {
        String index = "people";
        String type = "student";

        String address = "SD";

        // 根据年龄范围查询 [20, 25]
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age").from(20).to(25);

        // 根据地址查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("address", address))
                .filter(rangeQueryBuilder);

        SearchRequestBuilder builder = transportClient.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10); // 查询10条数据
        // 发送查询请求
        SearchResponse response = builder.get();

        // 查询结果
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (SearchHit hit : response.getHits()) {
            resultList.add(hit.getSource());
        }

        System.out.println(resultList);
    }

    public void testDelete() throws Exception {
        String index = "people";
        String type = "student";

        String id = "AXNn8Ab1Gys1ttDyexel";

        // 发送删除请求
        DeleteResponse response = transportClient.prepareDelete(index, type, id).get();
        System.out.println(response.getResult().toString());
    }

    public void testUpdate() throws Exception {
        String index = "people";
        String type = "student";

        // 要修改的文档参数
        String id = "AXNn8Ab1Gys1ttDyexel";
        String address = "SD";
        int age = 23;

        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .field("address", address)
                .field("age", age)
                .endObject();

        UpdateRequest request = new UpdateRequest(index, type, id);
        request.doc(builder);

        UpdateResponse response = transportClient.update(request).get();
        System.out.println(response.getResult().toString());
    }

}
