package org.sonarsource.gitlab;

import org.eclipse.jgit.util.StringUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;

import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

/***
 * git branch -a
 */
public class GitBranchA implements GitCommand {

    private final Builder builder;
    private Set<String> branchs;
    private String namespace;
    private String path;

    private GitBranchA(Builder builder) {
        this.builder = builder;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Set<String> getBranchs() {
        return branchs;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public void exec() throws Exception {
        URL url = new URL(this.builder.getGitURL());
        final GitLabApi gitLabApi = GitLabApi.oauth2Login(this.getUrlHead(url), this.builder.getUsername(), this.builder.getPassword());
        Project project = gitLabApi.getProjectApi().getProject(this.getNamespacePath(url));
        if(project != null){
            this.namespace = project.getNamespace().getPath();
            this.path = project.getPath();
            this.branchs = gitLabApi.getRepositoryApi().getBranches(project).stream().map(e->e.getName()).collect(Collectors.toSet());
        }
    }

    private String getUrlHead(URL url){
        return url.getProtocol() + "://" + url.getAuthority();
    }

    private String getNamespacePath(URL url) throws Exception{
        String path = url.getPath();
        if(!StringUtils.isEmptyOrNull(path)){
            if (path.endsWith(".git")){
                path = path.substring(0, path.lastIndexOf(".git"));
            }
            return this.getPath(path);
        }
        throw new Exception("git url path is bank.");
    }

    private String getPath(String path){
        if(path.startsWith("/")){
            path = path.substring(1);
            this.getPath(path);
        }
        return path;
    }

    public static class Builder{
        private String username;
        private String password;
        private String gitURL;
        public GitBranchA build(){
            return new GitBranchA(this);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getGitURL() {
            return gitURL;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setGitURL(String gitURL) {
            this.gitURL = gitURL;
            return this;
        }
    }
}
