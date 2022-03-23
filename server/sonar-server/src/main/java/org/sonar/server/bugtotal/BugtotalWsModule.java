package org.sonar.server.bugtotal;

import org.sonar.core.platform.Module;

public class BugtotalWsModule extends Module {
    @Override
    protected void configureModule() {
        add(
            BugtotalWs.class,
            BugtotalSubmitAction.class
        );
    }
}
