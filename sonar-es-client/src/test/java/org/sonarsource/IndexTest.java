package org.sonarsource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;


/***
 * 索引的增、存在、删
 */
public class IndexTest {

    private Client client;

    public static String index = "person";
    public static String type = "man";

    @Before
    public void createClient() throws UnknownHostException {
        this.client = MyEsClient.getTransportClient();
    }


    /***
     * 创建索引
     */
    @Test
    public void createIndex() throws Exception {
        //准备关于索引的setting
        Settings settings =  Settings.builder()
                                    .put("number_of_shards", "3")  //分片数
                                    .put("number_of_replicas", "1") //副本
                                    .build();
        //准备关于索引结果的mappings
        XContentBuilder mappings = JsonXContent.contentBuilder()
                                                .startObject() //novel
                                                    .startObject("properties")
                                                        .startObject("name")
                                                            .field("type", "text")
                                                        .endObject()
                                                        .startObject("age")
                                                            .field("type", "integer")
                                                        .endObject()
                                                        .startObject("birthday")
                                                            .field("type", "date")
                                                            .field("format", "yyyy-MM-dd")
                                                        .endObject()
                                                    .endObject()
                                                .endObject();

        //封装
        CreateIndexRequest request =
                new CreateIndexRequest(this.index)
                .settings(settings)
                .mapping(this.type, mappings);

        //通过client创建索引
        CreateIndexResponse createIndexResponse = this.client.admin().indices().create(request).get();

        //输出
        System.out.println(createIndexResponse.isAcknowledged());

    }

    /***
     * 索引是否存在
     */
    @Test
    public void existIndex() throws Exception {
        IndicesExistsRequest indicesExistsRequest = new IndicesExistsRequest();
        indicesExistsRequest.indices(this.index);
        IndicesExistsResponse indicesExistsResponse = this.client.admin().indices().exists(indicesExistsRequest).get();
        System.out.println(indicesExistsResponse.isExists());
    }

    /***
     * 索引删除
     * @throws Exception
     */
    @Test
    public void deleteIndex() throws Exception{
        DeleteIndexRequest request = new DeleteIndexRequest();
        request.indices(this.index);
        DeleteIndexResponse deleteIndexResponse = this.client.admin().indices().delete(request).get();
        System.out.println(deleteIndexResponse.isAcknowledged()); //是否响应
    }


    public static class PersonMan {
        @JsonIgnore
        private Integer id;
        private String name;
        private Integer age;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date birthday;

        public PersonMan(){}

        public PersonMan(Integer id, String name, Integer age, Date birthday){
            this.id = id;
            this.name = name;
            this.age = age;
            this.birthday = birthday;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }
    }

}
