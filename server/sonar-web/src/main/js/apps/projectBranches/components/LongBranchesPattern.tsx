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
import LongBranchesPatternForm from './LongBranchesPatternForm';
import { getValues, SettingValue } from '../../../api/settings';
import ChangeIcon from '../../../components/icons-components/ChangeIcon';
import { translate } from '../../../helpers/l10n';

interface Props {
  project: string;
}

interface State {
  formOpen: boolean;
  setting?: SettingValue;
}

export const LONG_BRANCH_PATTERN = 'sonar.branch.longLivedBranches.regex';

export default class LongBranchesPattern extends React.PureComponent<Props, State> {
  mounted: boolean;
  state: State = { formOpen: false };

  componentDidMount() {
    this.mounted = true;
    this.fetchSetting();
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  fetchSetting() {
    return getValues(LONG_BRANCH_PATTERN, this.props.project).then(
      settings => {
        if (this.mounted) {
          this.setState({ setting: settings[0] });
        }
      },
      () => {}
    );
  }

  closeForm = () => {
    if (this.mounted) {
      this.setState({ formOpen: false });
    }
  };

  handleChangeClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    event.currentTarget.blur();
    this.setState({ formOpen: true });
  };

  handleChange = () => {
    if (this.mounted) {
      this.fetchSetting().then(this.closeForm, this.closeForm);
    }
  };

  render() {
    const { setting } = this.state;

    if (!setting) {
      return null;
    }

    return (
      <div className="pull-right text-right">
        {translate('branches.long_living_branches_pattern')}
        {': '}
        <strong>{setting.value}</strong>
        <a
          className="display-inline-block spacer-left link-no-underline"
          href="#"
          onClick={this.handleChangeClick}>
          <ChangeIcon />
        </a>
        {this.state.formOpen && (
          <LongBranchesPatternForm
            onClose={this.closeForm}
            onChange={this.handleChange}
            project={this.props.project}
            setting={setting}
          />
        )}
      </div>
    );
  }
}
