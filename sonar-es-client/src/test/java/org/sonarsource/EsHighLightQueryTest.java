package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.sonar.es.MyEsClient;

/***
 * 高亮查询就是你用户输入的关键字，以一定的特殊样式展示给用户，让用户知道为什么这个结果被检索出来。
 *
 * 高亮展示的数据，本身就是文档中的一个field，单独将field以highlight的形式返回给你
 *
 * ES提供了一个highlight属性，和query同级别的
 * 1、fragment_size： 指定高亮数据展示多个字符回来；
 * 2、pre_tags：指定前缀标签<font color="red">
 * 3、post_tags：指定后缀标签</font>
 *
 */
public class EsHighLightQueryTest {

    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }


}
