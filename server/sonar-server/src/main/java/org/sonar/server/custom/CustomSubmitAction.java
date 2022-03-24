package org.sonar.server.custom;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.ws.WsAction;

public class CustomSubmitAction implements WsAction {


    @Override
    public void define(WebService.NewController controller) {
        WebService.NewAction action = controller.createAction("api/custom/submit")
                .setDescription("Submits a scanner report to the queue. Report is processed asynchronously. Requires analysis permission. " +
                        "If the project does not exist, then the provisioning permission is also required.")
                .setPost(true)
                .setInternal(true)
                .setSince("5.2")
                .setHandler(this)
                .setResponseExample(getClass().getResource("submit-example.json"));

    }

    @Override
    public void handle(Request wsRequest, Response wsResponse) throws Exception {

    }

}
