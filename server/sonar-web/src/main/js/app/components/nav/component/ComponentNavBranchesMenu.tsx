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
import * as PropTypes from 'prop-types';
import { Link } from 'react-router';
import ComponentNavBranchesMenuItem from './ComponentNavBranchesMenuItem';
import { Branch, Component } from '../../../types';
import {
  sortBranchesAsTree,
  isLongLivingBranch,
  isShortLivingBranch
} from '../../../../helpers/branches';
import { translate } from '../../../../helpers/l10n';
import { getProjectBranchUrl } from '../../../../helpers/urls';
import Tooltip from '../../../../components/controls/Tooltip';

interface Props {
  branches: Branch[];
  canAdmin?: boolean;
  component: Component;
  currentBranch: Branch;
  onClose: () => void;
}

interface State {
  query: string;
  selected: string | null;
}

export default class ComponentNavBranchesMenu extends React.PureComponent<Props, State> {
  private node: HTMLElement | null;
  state = { query: '', selected: null };

  static contextTypes = {
    router: PropTypes.object
  };

  componentDidMount() {
    window.addEventListener('click', this.handleClickOutside);
  }

  componentWillUnmount() {
    window.removeEventListener('click', this.handleClickOutside);
  }

  getFilteredBranches = () =>
    sortBranchesAsTree(this.props.branches).filter(branch =>
      branch.name.toLowerCase().includes(this.state.query.toLowerCase())
    );

  handleClickOutside = (event: Event) => {
    if (!this.node || !this.node.contains(event.target as HTMLElement)) {
      this.props.onClose();
    }
  };

  handleSearchChange = (event: React.SyntheticEvent<HTMLInputElement>) =>
    this.setState({ query: event.currentTarget.value, selected: null });

  handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    switch (event.keyCode) {
      case 13:
        event.preventDefault();
        this.openSelected();
        return;
      case 27:
        event.preventDefault();
        this.props.onClose();
        return;
      case 38:
        event.preventDefault();
        this.selectPrevious();
        return;
      case 40:
        event.preventDefault();
        this.selectNext();
        return;
    }
  };

  openSelected = () => {
    const selected = this.getSelected();
    const branch = this.getFilteredBranches().find(branch => branch.name === selected);
    if (branch) {
      this.context.router.push(this.getProjectBranchUrl(branch));
    }
  };

  selectPrevious = () => {
    const selected = this.getSelected();
    const branches = this.getFilteredBranches();
    const index = branches.findIndex(branch => branch.name === selected);
    if (index > 0) {
      this.setState({ selected: branches[index - 1].name });
    }
  };

  selectNext = () => {
    const selected = this.getSelected();
    const branches = this.getFilteredBranches();
    const index = branches.findIndex(branch => branch.name === selected);
    if (index >= 0 && index < branches.length - 1) {
      this.setState({ selected: branches[index + 1].name });
    }
  };

  handleSelect = (branch: Branch) => {
    this.setState({ selected: branch.name });
  };

  getSelected = () => {
    if (this.state.selected) {
      return this.state.selected;
    }

    const branches = this.getFilteredBranches();
    if (branches.find(b => b.name === this.props.currentBranch.name)) {
      return this.props.currentBranch.name;
    }

    if (branches.length > 0) {
      return branches[0].name;
    }

    return undefined;
  };

  getProjectBranchUrl = (branch: Branch) => getProjectBranchUrl(this.props.component.key, branch);

  isSelected = (branch: Branch) => branch.name === this.getSelected();

  renderSearch = () => (
    <div className="search-box menu-search">
      <button className="search-box-submit button-clean">
        <i className="icon-search-new" />
      </button>
      <input
        autoFocus={true}
        className="search-box-input"
        onChange={this.handleSearchChange}
        onKeyDown={this.handleKeyDown}
        placeholder={translate('search_verb')}
        type="search"
        value={this.state.query}
      />
    </div>
  );

  renderBranchesList = () => {
    const branches = this.getFilteredBranches();
    const selected = this.getSelected();

    if (branches.length === 0) {
      return <div className="menu-message note">{translate('no_results')}</div>;
    }

    const menu: JSX.Element[] = [];
    branches.forEach((branch, index) => {
      const isOrphan = isShortLivingBranch(branch) && branch.isOrphan;
      const previous = index > 0 ? branches[index - 1] : null;
      const isPreviousOrphan = isShortLivingBranch(previous) ? previous.isOrphan : false;
      if (isLongLivingBranch(branch) || (isOrphan && !isPreviousOrphan)) {
        menu.push(<li key={`divider-${branch.name}`} className="divider" />);
      }
      if (isOrphan && !isPreviousOrphan) {
        menu.push(
          <li className="dropdown-header" key="orphans">
            {translate('branches.orphan_branches')}
            <Tooltip overlay={translate('branches.orphan_branches.tooltip')}>
              <i className="icon-help spacer-left" />
            </Tooltip>
          </li>
        );
      }
      menu.push(
        <ComponentNavBranchesMenuItem
          branch={branch}
          component={this.props.component}
          key={branch.name}
          onSelect={this.handleSelect}
          selected={branch.name === selected}
        />
      );
    });

    return <ul className="menu menu-vertically-limited">{menu}</ul>;
  };

  render() {
    const { component } = this.props;
    const showManageLink =
      component.qualifier === 'TRK' &&
      component.configuration &&
      component.configuration.showSettings;

    return (
      <div className="dropdown-menu" ref={node => (this.node = node)}>
        {this.renderSearch()}
        {this.renderBranchesList()}
        {showManageLink && (
          <div className="dropdown-bottom-hint text-right">
            <Link
              className="text-muted"
              to={{ pathname: '/project/branches', query: { id: component.key } }}>
              {translate('branches.manage')}
            </Link>
          </div>
        )}
      </div>
    );
  }
}
