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

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class RulesPage extends Navigation {

  public RulesPage() {
    $(By.cssSelector(".coding-rules")).should(Condition.exist);
  }

  public int getTotal() {
    // warning - number is localized
    return Integer.parseInt($("#coding-rules-total").text());
  }

  public ElementsCollection getSelectedFacetItems(String facetName) {
    SelenideElement facet = $(".search-navigator-facet-box[data-property='"+ facetName+"']").shouldBe(Condition.visible);
    return facet.$$(".js-facet.active");
  }

  public RulesPage shouldHaveTotalRules(Integer total) {
    $("#coding-rules-total").shouldHave(text(total.toString()));
    return this;
  }

  public RulesPage openFacet(String facet) {
    $(".search-navigator-facet-box[data-property=\"" + facet + "\"] .js-facet-toggle").click();
    return this;
  }

  public RulesPage selectFacetItemByText(String facet, String itemText) {
    $$(".search-navigator-facet-box[data-property=\"" + facet + "\"] .js-facet")
      .findBy(text(itemText)).click();
    return this;
  }

  public RuleDetails openFirstRule() {
    $$(".js-rule").first().click();
    $(".coding-rules-details").shouldBe(visible);
    return new RuleDetails($(".coding-rules-details"));
  }

}
