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

    public void search() throws UnknownHostException {
        ProjectMeasuresIndex projectMeasuresIndex = new ProjectMeasuresIndex(MyEsClient.getEsClient(MyEsClient.getTransportClient()),
                                                            new AuthorizationTypeSupport(new UserSessionRule()),
                                                            System2.INSTANCE);

        ProjectMeasuresQuery query = new ProjectMeasuresQuery();
        SearchIdResult<String> stringSearchIdResult = projectMeasuresIndex.search(query, new SearchOptions().setOffset(0).setLimit(50));
        System.out.println();
    }
}
