/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.bugtotal;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;

public class BugtotalSubmitAction implements BugtotalWsAction {

  private static final String name = "name";

  @Override
  public void define(WebService.NewController controller) {
    WebService.NewAction action = controller.createAction("submit")
      .setDescription("helloworld.")
      .setPost(false)
      .setInternal(true)
      .setSince("5.2")
      .setHandler(this);

    action.createParam(name)
      .setDescription("Key of the organization the project belongs to")
      .setExampleValue("my-org")
      .setSince("6.3")
      .setInternal(true);

  }

  @Override
  public void handle(Request wsRequest, Response wsResponse) throws Exception {
    JsonWriter jsonWriter =  wsResponse.newJsonWriter();
    jsonWriter.beginObject();
    jsonWriter.prop("hello", "world");
    jsonWriter.endObject();
    jsonWriter.close();
  }


}
