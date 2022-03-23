package org.sonar.server.custom;

import org.sonar.core.platform.Module;

public class CustomModule extends Module {
    @Override
    protected void configureModule() {
        add(
                CustomSubmitAction.class
        );
    }
}
