package org.sonarsource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/***
 *
 */
public class ESDocTest {

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }


    /***
     * 增加
     * @throws Exception
     */
    @Test
    public void createDoc() throws Exception {

        //准备json
        ESIndexTest.PersonMan personMan = new ESIndexTest.PersonMan();
        personMan.setId(1);
        personMan.setAge(23);
        personMan.setName("张三");
        personMan.setBirthday(new Date());
        String json = mapper.writeValueAsString(personMan);
        System.out.println(json);

        //准备requeset对象，手动指定id
        IndexRequest indexRequest = new IndexRequest(ESIndexTest.index, ESIndexTest.type, personMan.getId().toString());
        indexRequest.source(json, XContentType.JSON);

        //通过client执行添加
        IndexResponse indexResponse = this.client.index(indexRequest).get();

        //输出
        System.out.println(indexResponse.getResult().toString());

    }

    /***
     * 删除
     * @throws Exception
     */
    @Test
    public void deleteDoc() throws Exception{
        //创建request对象封装数据
        String docId = "1";
        DeleteRequest deleteRequest = new DeleteRequest(ESIndexTest.index, ESIndexTest.type, docId);

        //client执行
        DeleteResponse response = this.client.delete(deleteRequest).get();

        //
        System.out.println(response.getResult().toString());
    }

    /***
     * 修改
     * @throws Exception
     */
    @Test
    public void updateDoc() throws Exception{
        //1.创建一个map，指定需要修改的内容
        Map<String, Object> doc = new HashMap<String, Object>();
        doc.put("name", "张大三");
        String docId = "1";

        //创建request对象，封装对象
        UpdateRequest updateRequest = new UpdateRequest(ESIndexTest.index, ESIndexTest.type, docId);
        updateRequest.doc(doc);

        //通过client对象执行
        UpdateResponse updateResponse = this.client.update(updateRequest).get();

        //
        System.out.println(updateResponse.getResult().toString());

    }







}
