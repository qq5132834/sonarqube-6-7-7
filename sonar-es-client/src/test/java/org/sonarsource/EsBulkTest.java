package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.Date;

/***
 * 批量操作
 */
public class EsBulkTest {

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * 批量添加
     * @throws Exception
     */
    @Test
    public void bulkCreateDoc() throws Exception {
        //1.准备多个数据
        ESIndexTest.PersonMan p1 = new ESIndexTest.PersonMan(1, "张三", 20, new Date());
        ESIndexTest.PersonMan p2 = new ESIndexTest.PersonMan(2, "李四", 21, new Date());
        ESIndexTest.PersonMan p3 = new ESIndexTest.PersonMan(3, "王五", 22, new Date());

        String json1 = mapper.writeValueAsString(p1);
        String json2 = mapper.writeValueAsString(p2);
        String json3 = mapper.writeValueAsString(p3);

        //2.创建request，封装数据
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.add(new IndexRequest(ESIndexTest.index, ESIndexTest.type, p1.getId().toString()).source(json1, XContentType.JSON));
        bulkRequest.add(new IndexRequest(ESIndexTest.index, ESIndexTest.type, p2.getId().toString()).source(json2, XContentType.JSON));
        bulkRequest.add(new IndexRequest(ESIndexTest.index, ESIndexTest.type, p3.getId().toString()).source(json3, XContentType.JSON));

        //3、用client执行
        BulkResponse bulkResponse = this.client.bulk(bulkRequest).get();

        //
        System.out.println(bulkResponse.getItems().length);
    }

    /***
     * 批量删除
     * @throws Exception
     */
    @Test
    public void bulkDeleteDoc() throws Exception {
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest(ESIndexTest.index, ESIndexTest.type, "1"));
        request.add(new DeleteRequest(ESIndexTest.index, ESIndexTest.type, "2"));
        request.add(new DeleteRequest(ESIndexTest.index, ESIndexTest.type, "3"));

        BulkResponse bulkResponse = this.client.bulk(request).get();

        System.out.println(bulkResponse.getItems().length);
    }

}
