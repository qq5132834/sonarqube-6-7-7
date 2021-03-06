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
import * as classNames from 'classnames';
import ProfileInheritanceBox from './ProfileInheritanceBox';
import ChangeParentForm from './ChangeParentForm';
import { translate } from '../../../helpers/l10n';
import { getProfileInheritance } from '../../../api/quality-profiles';
import { Profile } from '../types';

interface Props {
  onRequestFail: (reason: any) => void;
  organization: string | null;
  profile: Profile;
  profiles: Profile[];
  updateProfiles: () => Promise<void>;
}

interface ProfileInheritanceDetails {
  activeRuleCount: number;
  isBuiltIn: boolean;
  key: string;
  language: string;
  name: string;
  overridingRuleCount?: number;
}

interface State {
  ancestors?: Array<ProfileInheritanceDetails>;
  children?: Array<ProfileInheritanceDetails>;
  formOpen: boolean;
  loading: boolean;
  profile?: ProfileInheritanceDetails;
}

export default class ProfileInheritance extends React.PureComponent<Props, State> {
  mounted: boolean;

  state: State = {
    formOpen: false,
    loading: true
  };

  componentDidMount() {
    this.mounted = true;
    this.loadData();
  }

  componentDidUpdate(prevProps: Props) {
    if (prevProps.profile !== this.props.profile) {
      this.loadData();
    }
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  loadData() {
    getProfileInheritance(this.props.profile.key).then((r: any) => {
      if (this.mounted) {
        const { ancestors, children } = r;
        this.setState({
          children,
          ancestors: ancestors.reverse(),
          profile: r.profile,
          loading: false
        });
      }
    });
  }

  handleChangeParentClick = (event: React.SyntheticEvent<HTMLElement>) => {
    event.preventDefault();
    this.setState({ formOpen: true });
  };

  closeForm = () => {
    this.setState({ formOpen: false });
  };

  handleParentChange = () => {
    this.props.updateProfiles();
    this.closeForm();
  };

  render() {
    const { profile, profiles } = this.props;
    const { ancestors } = this.state;

    const highlightCurrent =
      !this.state.loading &&
      ancestors != null &&
      this.state.children != null &&
      (ancestors.length > 0 || this.state.children.length > 0);

    const currentClassName = classNames('js-inheritance-current', {
      selected: highlightCurrent
    });

    const extendsBuiltIn = ancestors != null && ancestors.some(profile => profile.isBuiltIn);

    return (
      <div className="boxed-group quality-profile-inheritance">
        {profile.actions &&
        profile.actions.edit &&
        !profile.isBuiltIn && (
          <div className="boxed-group-actions">
            <button className="pull-right js-change-parent" onClick={this.handleChangeParentClick}>
              {translate('quality_profiles.change_parent')}
            </button>
          </div>
        )}

        <header className="boxed-group-header">
          <h2>{translate('quality_profiles.profile_inheritance')}</h2>
        </header>

        <div className="boxed-group-inner">
          {this.state.loading ? (
            <i className="spinner" />
          ) : (
            <table className="data zebra">
              <tbody>
                {ancestors != null &&
                  ancestors.map((ancestor, index) => (
                    <ProfileInheritanceBox
                      className="js-inheritance-ancestor"
                      depth={index}
                      key={ancestor.key}
                      language={profile.language}
                      organization={this.props.organization}
                      profile={ancestor}
                    />
                  ))}

                {this.state.profile != null && (
                  <ProfileInheritanceBox
                    className={currentClassName}
                    depth={ancestors ? ancestors.length : 0}
                    displayLink={false}
                    extendsBuiltIn={extendsBuiltIn}
                    language={profile.language}
                    organization={this.props.organization}
                    profile={this.state.profile}
                  />
                )}

                {this.state.children != null &&
                  this.state.children.map(child => (
                    <ProfileInheritanceBox
                      className="js-inheritance-child"
                      depth={ancestors ? ancestors.length + 1 : 0}
                      key={child.key}
                      language={profile.language}
                      organization={this.props.organization}
                      profile={child}
                    />
                  ))}
              </tbody>
            </table>
          )}
        </div>

        {this.state.formOpen && (
          <ChangeParentForm
            onChange={this.handleParentChange}
            onClose={this.closeForm}
            onRequestFail={this.props.onRequestFail}
            profile={profile}
            profiles={profiles.filter(p => p !== profile && p.language === profile.language)}
          />
        )}
      </div>
    );
  }
}
