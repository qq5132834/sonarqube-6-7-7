package org.sonarsource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
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

        //
        System.out.println(indexResponse.getResult().toString());


    }




}
