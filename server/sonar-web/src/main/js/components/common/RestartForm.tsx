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
import Modal from 'react-modal';
import { restartAndWait } from '../../api/system';
import { translate } from '../../helpers/l10n';

interface Props {
  onClose: () => void;
}

interface State {
  restarting: boolean;
}

export default class RestartForm extends React.PureComponent<Props, State> {
  state: State = { restarting: false };

  handleCancelClick = (event: React.SyntheticEvent<HTMLElement>) => {
    event.preventDefault();
    this.props.onClose();
  };

  handleFormSubmit = (event: React.SyntheticEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!this.state.restarting) {
      this.setState({ restarting: true });
      restartAndWait().then(
        () => document.location.reload(),
        () => this.setState({ restarting: false })
      );
    }
  };

  render() {
    const { restarting } = this.state;
    const header = translate('system.restart_server');
    return (
      <Modal
        isOpen={true}
        contentLabel={header}
        className="modal"
        overlayClassName="modal-overlay"
        onRequestClose={this.props.onClose}
        shouldCloseOnOverlayClick={!restarting}>
        <form id="restart-form" onSubmit={this.handleFormSubmit}>
          <div className="modal-head">
            <h2>{header}</h2>
          </div>
          <div className="modal-body">
            <p className={classNames('spacer-top spacer-bottom', { 'text-center': restarting })}>
              {translate(restarting ? 'system.is_restarting' : 'system.are_you_sure_to_restart')}
            </p>
            {restarting && (
              <p className="big-spacer-top spacer-bottom text-center">
                <i className="spinner" />
              </p>
            )}
          </div>
          {!restarting && (
            <div className="modal-foot">
              <button id="restart-server-submit">{translate('restart')}</button>
              <a
                href="#"
                className="js-modal-close"
                id="restart-server-cancel"
                onClick={this.handleCancelClick}>
                {translate('cancel')}
              </a>
            </div>
          )}
        </form>
      </Modal>
    );
  }
}
