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
import { FormattedMessage } from 'react-intl';
import { User } from './ProfilePermissions';
import { removeUser } from '../../../api/quality-profiles';
import SimpleModal, { ChildrenProps } from '../../../components/controls/SimpleModal';
import DeleteIcon from '../../../components/icons-components/DeleteIcon';
import Avatar from '../../../components/ui/Avatar';
import { translate } from '../../../helpers/l10n';

interface Props {
  onDelete: (user: User) => void;
  organization?: string;
  profile: { language: string; name: string };
  user: User;
}

interface State {
  deleteModal: boolean;
}

export default class ProfilePermissionsUser extends React.PureComponent<Props, State> {
  mounted: boolean;
  state: State = { deleteModal: false };

  componentDidMount() {
    this.mounted = true;
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  handleDeleteClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    event.currentTarget.blur();
    this.setState({ deleteModal: true });
  };

  handleDeleteModalClose = () => {
    if (this.mounted) {
      this.setState({ deleteModal: false });
    }
  };

  handleDelete = () => {
    const { organization, profile, user } = this.props;

    return removeUser({
      language: profile.language,
      login: user.login,
      organization,
      qualityProfile: profile.name
    }).then(() => {
      this.handleDeleteModalClose();
      this.props.onDelete(user);
    });
  };

  renderDeleteModal = (props: ChildrenProps) => (
    <div>
      <header className="modal-head">
        <h2>{translate('users.remove')}</h2>
      </header>

      <div className="modal-body">
        <FormattedMessage
          defaultMessage={translate('users.remove.confirmation')}
          id="users.remove.confirmation"
          values={{
            user: <strong>{this.props.user.name}</strong>
          }}
        />
      </div>

      <footer className="modal-foot">
        {props.submitting && <i className="spinner spacer-right" />}
        <button className="button-red" disabled={props.submitting} onClick={props.onSubmitClick}>
          {translate('remove')}
        </button>
        <a href="#" onClick={props.onCloseClick}>
          {translate('cancel')}
        </a>
      </footer>
    </div>
  );

  render() {
    const { user } = this.props;

    return (
      <div className="clearfix big-spacer-bottom">
        <a
          className="pull-right spacer-top spacer-left spacer-right button-icon"
          href="#"
          onClick={this.handleDeleteClick}>
          <DeleteIcon />
        </a>
        <Avatar className="pull-left spacer-right" hash={user.avatar} name={user.name} size={32} />
        <div className="overflow-hidden">
          <strong>{user.name}</strong>
          <div className="note">{user.login}</div>
        </div>

        {this.state.deleteModal && (
          <SimpleModal
            header={translate('users.remove')}
            onClose={this.handleDeleteModalClose}
            onSubmit={this.handleDelete}>
            {this.renderDeleteModal}
          </SimpleModal>
        )}
      </div>
    );
  }
}
