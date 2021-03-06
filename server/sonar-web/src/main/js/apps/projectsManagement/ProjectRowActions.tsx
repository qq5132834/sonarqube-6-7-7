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
import * as React from 'react';
import { Link } from 'react-router';
import RestoreAccessModal from './RestoreAccessModal';
import { Project } from './utils';
import { getComponentShow } from '../../api/components';
import { getComponentNavigation } from '../../api/nav';
import { translate } from '../../helpers/l10n';
import { getComponentPermissionsUrl } from '../../helpers/urls';

export interface Props {
  currentUser: { login: string };
  onApplyTemplate: (project: Project) => void;
  project: Project;
}

interface State {
  hasAccess?: boolean;
  loading: boolean;
  restoreAccessModal: boolean;
}

export default class ProjectRowActions extends React.PureComponent<Props, State> {
  mounted: boolean;
  state: State = { loading: false, restoreAccessModal: false };

  componentDidMount() {
    this.mounted = true;
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  fetchPermissions = () => {
    this.setState({ loading: false });
    // call `getComponentNavigation` to check if user has the "Administer" permission
    // call `getComponentShow` to check if user has the "Browse" permission
    Promise.all([
      getComponentNavigation(this.props.project.key),
      getComponentShow(this.props.project.key)
    ]).then(
      ([navResponse]) => {
        if (this.mounted) {
          const hasAccess = Boolean(
            navResponse.configuration && navResponse.configuration.showPermissions
          );
          this.setState({ hasAccess, loading: false });
        }
      },
      () => {
        if (this.mounted) {
          this.setState({ hasAccess: false, loading: false });
        }
      }
    );
  };

  handleDropdownClick = () => {
    if (this.state.hasAccess === undefined && !this.state.loading) {
      this.fetchPermissions();
    }
  };

  handleApplyTemplateClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    event.currentTarget.blur();
    this.props.onApplyTemplate(this.props.project);
  };

  handleRestoreAccessClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    event.currentTarget.blur();
    this.setState({ restoreAccessModal: true });
  };

  handleRestoreAccessClose = () => this.setState({ restoreAccessModal: false });

  handleRestoreAccessDone = () => {
    this.setState({ hasAccess: true, restoreAccessModal: false });
  };

  render() {
    const { hasAccess, loading } = this.state;

    return (
      <div className="dropdown">
        <button
          className="dropdown-toggle"
          data-toggle="dropdown"
          onClick={this.handleDropdownClick}>
          {translate('actions')} <i className="icon-dropdown" />
        </button>
        {loading ? (
          <div className="dropdown-menu dropdown-menu-right">
            <i className="spinner spacer-left" />
          </div>
        ) : (
          <ul className="dropdown-menu dropdown-menu-right">
            {hasAccess === true && (
              <li>
                <Link to={getComponentPermissionsUrl(this.props.project.key)}>
                  {translate('edit_permissions')}
                </Link>
              </li>
            )}

            {hasAccess === false && (
              <li>
                <a className="js-restore-access" href="#" onClick={this.handleRestoreAccessClick}>
                  {translate('global_permissions.restore_access')}
                </a>
              </li>
            )}

            <li>
              <a className="js-apply-template" href="#" onClick={this.handleApplyTemplateClick}>
                {translate('projects_role.apply_template')}
              </a>
            </li>
          </ul>
        )}

        {this.state.restoreAccessModal && (
          <RestoreAccessModal
            currentUser={this.props.currentUser}
            onClose={this.handleRestoreAccessClose}
            onRestoreAccess={this.handleRestoreAccessDone}
            project={this.props.project}
          />
        )}
      </div>
    );
  }
}
