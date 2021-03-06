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
// @flow
import React from 'react';
import { Link } from 'react-router';
import classNames from 'classnames';
import { translate } from '../../../helpers/l10n';
import ContextNavBar from '../../../components/nav/ContextNavBar';
import NavBarTabs from '../../../components/nav/NavBarTabs';
import OrganizationIcon from '../../../components/icons-components/OrganizationIcon';
import { getQualityGatesUrl } from '../../../helpers/urls';
/*:: import type { Organization } from '../../../store/organizations/duck'; */

const ADMIN_PATHS = [
  'edit',
  'groups',
  'delete',
  'permissions',
  'permission_templates',
  'projects_management'
];

export default class OrganizationNavigation extends React.PureComponent {
  /*:: props: {
    location: { pathname: string },
    organization: Organization
  };
*/

  renderAdministration(adminActive /*: boolean */) {
    const { organization } = this.props;

    return (
      <li className="dropdown">
        <a
          className={classNames('dropdown-toggle', { active: adminActive })}
          data-toggle="dropdown"
          id="organization-navigation-admin"
          href="#">
          {translate('layout.settings')}&nbsp;<i className="icon-dropdown" />
        </a>
        <ul className="dropdown-menu">
          {this.renderAdminExtensions()}
          <li>
            <Link to={`/organizations/${organization.key}/groups`} activeClassName="active">
              {translate('user_groups.page')}
            </Link>
          </li>
          <li>
            <Link to={`/organizations/${organization.key}/permissions`} activeClassName="active">
              {translate('permissions.page')}
            </Link>
          </li>
          <li>
            <Link
              to={`/organizations/${organization.key}/permission_templates`}
              activeClassName="active">
              {translate('permission_templates')}
            </Link>
          </li>
          <li>
            <Link
              to={`/organizations/${organization.key}/projects_management`}
              activeClassName="active">
              {translate('projects_management')}
            </Link>
          </li>
          <li>
            <Link to={`/organizations/${organization.key}/edit`} activeClassName="active">
              {translate('edit')}
            </Link>
          </li>
          {organization.canDelete && (
            <li>
              <Link to={`/organizations/${organization.key}/delete`} activeClassName="active">
                {translate('delete')}
              </Link>
            </li>
          )}
        </ul>
      </li>
    );
  }

  renderAdminExtensions() {
    const extensions = this.props.organization.adminPages || [];
    return extensions.map(this.renderExtension);
  }

  renderExtension = (extension /*: { key: string, name: string } */) => {
    const { organization } = this.props;
    const pathname = `/organizations/${organization.key}/extension/${extension.key}`;
    return (
      <li key={extension.key}>
        <Link to={pathname} activeClassName="active">
          {extension.name}
        </Link>
      </li>
    );
  };

  renderExtensions(moreActive /*: boolean */) {
    const extensions = this.props.organization.pages || [];
    if (extensions.length > 0) {
      return (
        <li className={moreActive ? 'active' : ''}>
          <a
            className="dropdown-toggle"
            id="organization-navigation-more"
            data-toggle="dropdown"
            href="#">
            {translate('more')}&nbsp;<i className="icon-dropdown" />
          </a>
          <ul className="dropdown-menu">{extensions.map(this.renderExtension)}</ul>
        </li>
      );
    } else {
      return null;
    }
  }

  render() {
    const { organization, location } = this.props;

    const isHomeActive =
      location.pathname === `organizations/${organization.key}/projects` ||
      location.pathname === `organizations/${organization.key}/projects/favorite`;

    const adminPathsWithExtensions = (organization.adminPages || [])
      .map(e => `extension/${e.key}`)
      .concat(ADMIN_PATHS);

    const adminActive = adminPathsWithExtensions.some(path =>
      location.pathname.endsWith(`organizations/${organization.key}/${path}`)
    );
    const moreActive = !adminActive && location.pathname.includes('/extension/');

    return (
      <ContextNavBar id="context-navigation" height={65}>
        <div className="navbar-context-header">
          <h1 className="display-inline-block">
            <OrganizationIcon className="little-spacer-right" />
            <Link
              to={`/organizations/${organization.key}`}
              className="link-base-color link-no-underline">
              <strong>{organization.name}</strong>
            </Link>
          </h1>
          {organization.description != null && (
            <div className="navbar-context-description">
              <p className="text-limited text-top" title={organization.description}>
                {organization.description}
              </p>
            </div>
          )}
        </div>

        <div className="navbar-context-meta">
          {!!organization.avatar && (
            <img src={organization.avatar} height={30} alt={organization.name} />
          )}
          {organization.url != null && (
            <div>
              <p className="text-limited text-top">
                <a
                  className="link-underline"
                  href={organization.url}
                  title={organization.url}
                  rel="nofollow">
                  {organization.url}
                </a>
              </p>
            </div>
          )}
        </div>

        <NavBarTabs>
          <li>
            <Link
              to={`/organizations/${organization.key}/projects`}
              className={isHomeActive ? 'active' : ''}>
              {translate('projects.page')}
            </Link>
          </li>
          <li>
            <Link
              to={{
                pathname: `/organizations/${organization.key}/issues`,
                query: { resolved: 'false' }
              }}
              activeClassName="active">
              {translate('issues.page')}
            </Link>
          </li>
          <li>
            <Link
              to={`/organizations/${organization.key}/quality_profiles`}
              activeClassName="active">
              {translate('quality_profiles.page')}
            </Link>
          </li>
          <li>
            <Link to={`/organizations/${organization.key}/rules`} activeClassName="active">
              {translate('coding_rules.page')}
            </Link>
          </li>
          <li>
            <Link to={getQualityGatesUrl(organization.key)} activeClassName="active">
              {translate('quality_gates.page')}
            </Link>
          </li>
          <li>
            <Link to={`/organizations/${organization.key}/members`} activeClassName="active">
              {translate('organization.members.page')}
            </Link>
          </li>
          {this.renderExtensions(moreActive)}
          {organization.canAdmin && this.renderAdministration(adminActive)}
        </NavBarTabs>
      </ContextNavBar>
    );
  }
}
