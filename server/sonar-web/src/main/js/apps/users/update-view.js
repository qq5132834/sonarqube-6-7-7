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
import FormView from './form-view';

export default FormView.extend({
  sendRequest() {
    const that = this;
    this.model.set({
      name: this.$('#create-user-name').val(),
      email: this.$('#create-user-email').val(),
      scmAccounts: this.getScmAccounts()
    });
    this.disableForm();
    return this.model
      .save(null, {
        statusCode: {
          // do not show global error
          400: null
        }
      })
      .done(() => {
        that.collection.refresh();
        that.destroy();
      })
      .fail(jqXHR => {
        that.enableForm();
        that.showErrors(jqXHR.responseJSON.errors, jqXHR.responseJSON.warnings);
      });
  }
});
