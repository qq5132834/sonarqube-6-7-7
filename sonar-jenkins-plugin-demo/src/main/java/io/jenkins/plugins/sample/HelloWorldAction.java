package io.jenkins.plugins.sample;


import hudson.model.Action;

import javax.annotation.CheckForNull;

public class HelloWorldAction implements Action {

    private String name;

    public HelloWorldAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        System.out.println("HelloWorldAction.getIconFileName");
        //return "document.png";
        return null;
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        System.out.println("HelloWorldAction.getDisplayName");
        return "Greeting";
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        System.out.println("HelloWorldAction.getUrlName");
        return "greeting";
    }

}
