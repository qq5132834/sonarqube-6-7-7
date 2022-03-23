package org.sonar.server.bugtotal;

import org.sonar.api.server.ws.WebService;

public class BugtotalWs implements WebService {

    public static final String ENDPOINT = "api/bugtotal";

    private final BugtotalWsAction[] actions;

    public BugtotalWs(BugtotalWsAction... actions) {
        this.actions = actions;
    }

    @Override
    public void define(Context context) {
        NewController controller = context
                .createController(ENDPOINT)
                .setDescription("Get information on Compute Engine tasks.");
        for (BugtotalWsAction action : actions) {
            action.define(controller);
        }
        controller.done();
    }
}
