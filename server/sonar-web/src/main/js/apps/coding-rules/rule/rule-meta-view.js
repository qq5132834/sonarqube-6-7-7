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
import $ from 'jquery';
import escapeHtml from 'escape-html';
import { difference, union } from 'lodash';
import Marionette from 'backbone.marionette';
import RuleFilterMixin from './rule-filter-mixin';
import Template from '../templates/rule/coding-rules-rule-meta.hbs';
import { getRuleTags } from '../../../api/rules';

export default Marionette.ItemView.extend(RuleFilterMixin).extend({
  template: Template,

  modelEvents: {
    change: 'render'
  },

  ui: {
    tagsChange: '.coding-rules-detail-tags-change',
    tagInput: '.coding-rules-detail-tag-input',
    tagsEdit: '.coding-rules-detail-tag-edit',
    tagsEditDone: '.coding-rules-detail-tag-edit-done',
    tagsEditCancel: '.coding-rules-details-tag-edit-cancel',
    tagsList: '.coding-rules-detail-tag-list'
  },

  events: {
    'click @ui.tagsChange': 'changeTags',
    'click @ui.tagsEditDone': 'editDone',
    'click @ui.tagsEditCancel': 'cancelEdit',
    'click .js-rule-filter': 'onRuleFilterClick'
  },

  onRender() {
    this.$('[data-toggle="tooltip"]').tooltip({
      container: 'body'
    });
  },

  onDestroy() {
    this.$('[data-toggle="tooltip"]').tooltip('destroy');
  },

  changeTags() {
    getRuleTags({ organization: this.options.app.organization }).then(
      tags => {
        this.ui.tagInput.select2({
          tags: difference(difference(tags, this.model.get('tags')), this.model.get('sysTags')),
          width: '300px',
          escapeMarkup: escapeHtml,
          formatResult: result => result.text
        });

        this.ui.tagsEdit.removeClass('hidden');
        this.ui.tagsList.addClass('hidden');
        this.tagsBuffer = this.ui.tagInput.select2('val');
        this.ui.tagInput.select2('open');
      },
      () => {}
    );
  },

  cancelEdit() {
    this.ui.tagsList.removeClass('hidden');
    this.ui.tagsEdit.addClass('hidden');
    if (this.ui.tagInput.select2) {
      this.ui.tagInput.select2('val', this.tagsBuffer);
      this.ui.tagInput.select2('close');
    }
  },

  editDone() {
    const that = this;
    const tags = this.ui.tagInput.val();
    const data = { key: this.model.get('key'), tags };
    if (this.options.app.organization) {
      data.organization = this.options.app.organization;
    }
    return $.ajax({
      type: 'POST',
      url: window.baseUrl + '/api/rules/update',
      data
    })
      .done(r => {
        that.model.set('tags', r.rule.tags);
        that.cancelEdit();
      })
      .always(() => {
        that.cancelEdit();
      });
  },

  serializeData() {
    const permalinkPath = this.options.app.organization
      ? `/organizations/${this.options.app.organization}/rules`
      : '/coding_rules';

    return {
      ...Marionette.ItemView.prototype.serializeData.apply(this, arguments),
      canCustomizeRule: this.options.app.canWrite,
      allTags: union(this.model.get('sysTags'), this.model.get('tags')),
      permalink: window.baseUrl + permalinkPath + '#rule_key=' + encodeURIComponent(this.model.id)
    };
  }
});
