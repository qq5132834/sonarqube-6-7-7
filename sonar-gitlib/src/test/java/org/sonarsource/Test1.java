package org.sonarsource;

import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabSession;
import org.junit.Test;

import java.io.IOException;

public class Test1 {

    @Test
    public void test() throws IOException {

//        String accessToken = "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";
//        GitlabAPI gitlabAPI = GitlabAPI.connect("https://gitlab.com/", accessToken);
//        System.out.println(gitlabAPI.getUser().getUsername());
// 条件获取project
// nameSpace：项目的命名空间
// projectName：项目名称
//        GitlabProject project = gitlabAPI.getProject("qq5132834", "31paycenter-demo");


        GitlabSession gitlabSession = GitlabAPI.connect("https://gitlab.com/", "qq5132834", "Qq0290397282");
        System.out.println(gitlabSession.getUsername());

    }

}
