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
package org.sonarqube.pageobjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SystemInfoPage {
  public SystemInfoPage() {
    $(".page-title").should(exist).shouldHave(text("System Info"));
  }

  public SystemInfoPage shouldHaveCard(String title) {
    $$(".system-info-health-card-title").find(text(title)).should(exist);
    return this;
  }

  public SystemInfoPage shouldHaveCards(String... titles) {
    $$(".system-info-health-card-title").shouldHave(CollectionCondition.texts(titles));
    return this;
  }

  public SystemInfoPageItem getCardItem(String card) {
    SelenideElement cardTitle = $$(".system-info-health-card-title").find(text(card)).should(exist);
    return new SystemInfoPageItem(cardTitle.parent().parent());
  }
}