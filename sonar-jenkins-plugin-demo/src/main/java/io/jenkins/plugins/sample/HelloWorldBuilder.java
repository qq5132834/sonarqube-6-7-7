package io.jenkins.plugins.sample;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.Proc;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.*;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    public static String ErrorsMissingName = "请填写名字";
    public static String WarningsTooShort = "名字是不是太短了？";
    public static String WarningsReallyFrench = "你真的是法国人吗？";
    public static String DisplayName= "说：你好，世界";

    private final String name;
    private boolean useFrench;
    private final Integer age;

    @DataBoundConstructor
    public HelloWorldBuilder(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public boolean isUseFrench() {
        return useFrench;
    }

    @DataBoundSetter
    public void setUseFrench(boolean useFrench) {
        System.out.println("HelloWorldBuilder.setUseFrench:" + useFrench);
        this.useFrench = useFrench;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

//        Proc proc = launcher.launch().cmds("echo \"xiix\"").start();
//        BufferedReader br = null;
//        br = new BufferedReader(new InputStreamReader(proc.getStdout(), "UTF-8"));
//        String line;
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//        br.close();

        System.out.println("HelloWorldBuilder.perform");
        System.out.println("HelloWorldBuilder.perform.workspace:" + workspace.getName() + "," + workspace.getRemote());
        run.addAction(new HelloWorldAction(name));
        if (this.useFrench) {
            listener.getLogger().println("Bonjour, " + this.name + ",年龄：" + this.age);
        } else {
            listener.getLogger().println("Hello, " + this.name + ",年龄：" + this.age);
        }

    }

    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckName(@QueryParameter String value, @QueryParameter boolean useFrench)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(ErrorsMissingName);
            if (value.length() < 4)
                return FormValidation.warning(WarningsTooShort);
            if (!useFrench && value.matches(".*[éáàç].*")) {
                return FormValidation.warning(WarningsReallyFrench);
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return DisplayName;
        }

    }

}
