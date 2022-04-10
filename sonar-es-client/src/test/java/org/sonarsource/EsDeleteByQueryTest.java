package org.sonarsource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Before;
import org.junit.Test;
import org.sonar.es.MyEsClient;

/***
 * 根据term、match等查询方式去删除大量的文档
 *
 * ps：如果你需要删除的内容，是index下的大部分数据。推荐创建一个全新的index，将需要保留的文档复制到新的索引中。
 */
public class EsDeleteByQueryTest {
    private String index = "components";
    private String type = "component";

    private Client client;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void createClient() throws Exception {
        this.client = MyEsClient.getTransportClient();
    }

    /***
     * GET components/component/_delete_query
     * {
     *   "query" : {
     *     "term": {
     *       "uuid": {
     *         "value": "AYAKLoMMFOKFjRhLYHeD"
     *       }
     *     }
     *   }
     * }
     *
     * 根据条件删除
     */
    @Test
    public void deleteByQuery() throws Exception {

        //
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termQuery("uuid", "AYAKLoMMFOKFjRhLYHeD"));


        //创建 DeleteByQueryRequest
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(searchRequest);
        deleteByQueryRequest.types(type);

        //好像不支持这种方式

    }


}
