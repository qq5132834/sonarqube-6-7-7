package org.sonar.server.bugtotal;

import org.sonar.api.server.ws.WebService;
import org.sonar.server.ws.WsAction;

public interface BugtotalWsAction extends WsAction {
    @Override
    void define(WebService.NewController controller);
}