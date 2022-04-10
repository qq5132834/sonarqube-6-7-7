package org.sonar.es.index;

import org.sonar.api.utils.System2;
import org.sonar.es.MyEsClient;
import org.sonar.es.UserSessionRule;
import org.sonar.server.es.SearchIdResult;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.measure.index.ProjectMeasuresIndex;
import org.sonar.server.measure.index.ProjectMeasuresQuery;
import org.sonar.server.permission.index.AuthorizationTypeSupport;

import java.net.UnknownHostException;

public class MyProjectMeasuresIndex {
    public static void main(String[] args) throws Exception {
        MyProjectMeasuresIndex myProjectMeasuresIndex = new MyProjectMeasuresIndex();
        myProjectMeasuresIndex.search();
    }

    /***
     * .../api/components/search_projects项目查询中抽取es的查询功能和结果返回
     * @throws UnknownHostException
     */
    public void search() throws UnknownHostException {
        ProjectMeasuresIndex projectMeasuresIndex = new ProjectMeasuresIndex(MyEsClient.getEsClient(MyEsClient.getTransportClient()),
                                                            new AuthorizationTypeSupport(new UserSessionRule()),
                                                            System2.INSTANCE);

        ProjectMeasuresQuery query = new ProjectMeasuresQuery();
        query.setQueryText("f9d5a50ca66a4d7c99e9801b1173bae5");
        query.setSort("analysisDate");
        query.setAsc(true);
        SearchIdResult<String> stringSearchIdResult = projectMeasuresIndex.search(query, new SearchOptions().setOffset(0).setLimit(50));
        System.out.println();
    }
}
