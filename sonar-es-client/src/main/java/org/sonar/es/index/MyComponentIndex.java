package org.sonar.es.index;

import org.sonar.api.utils.System2;
import org.sonar.es.MyEsClient;
import org.sonar.es.UserSessionRule;
import org.sonar.server.component.index.ComponentIndex;
import org.sonar.server.component.index.ComponentQuery;
import org.sonar.server.es.SearchIdResult;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.permission.index.AuthorizationTypeSupport;

/***
 *
 */
public class MyComponentIndex {

    public static void main(String[] args) throws Exception {
        MyComponentIndex myComponentIndex = new MyComponentIndex();
        myComponentIndex.search();
    }

    public void search() throws Exception {
        ComponentIndex componentIndex = new ComponentIndex(MyEsClient.getEsClient(MyEsClient.getTransportClient()),
                new AuthorizationTypeSupport(new UserSessionRule()),
                System2.INSTANCE);
        ComponentQuery query = ComponentQuery.builder()
                                           // .setQuery("f9d5a50ca66a4d7c99e9801b1173bae5")
                                            .build();
        //默认按name降序排列
        SearchIdResult<String> stringSearchIdResult = componentIndex.search(query, new SearchOptions().setOffset(0).setLimit(50));
        System.out.println();
    }


}
