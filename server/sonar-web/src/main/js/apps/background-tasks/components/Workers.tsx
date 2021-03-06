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
import WorkersForm from './WorkersForm';
import NoWorkersSupportPopup from './NoWorkersSupportPopup';
import Tooltip from '../../../components/controls/Tooltip';
import { getWorkers } from '../../../api/ce';
import { translate } from '../../../helpers/l10n';
import HelpIcon from '../../../components/icons-components/HelpIcon';
import BubblePopupHelper from '../../../components/common/BubblePopupHelper';

interface State {
  canSetWorkerCount: boolean;
  formOpen: boolean;
  loading: boolean;
  noSupportPopup: boolean;
  workerCount: number;
}

export default class Workers extends React.PureComponent<{}, State> {
  mounted: boolean;
  state: State = {
    canSetWorkerCount: false,
    formOpen: false,
    loading: true,
    noSupportPopup: false,
    workerCount: 1
  };

  componentDidMount() {
    this.mounted = true;
    this.loadWorkers();
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  loadWorkers = () => {
    this.setState({ loading: true });
    getWorkers().then(({ canSetWorkerCount, value }) => {
      if (this.mounted) {
        this.setState({
          canSetWorkerCount,
          loading: false,
          workerCount: value
        });
      }
    });
  };

  closeForm = (newWorkerCount?: number) =>
    newWorkerCount
      ? this.setState({ formOpen: false, workerCount: newWorkerCount })
      : this.setState({ formOpen: false });

  handleChangeClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    this.setState({ formOpen: true });
  };

  handleHelpClick = (event: React.SyntheticEvent<HTMLAnchorElement>) => {
    event.preventDefault();
    event.stopPropagation();
    this.toggleNoSupportPopup();
  };

  toggleNoSupportPopup = (show?: boolean) => {
    if (show != undefined) {
      this.setState({ noSupportPopup: show });
    } else {
      this.setState(state => ({ noSupportPopup: !state.noSupportPopup }));
    }
  };

  render() {
    const { canSetWorkerCount, formOpen, loading, workerCount } = this.state;

    return (
      <div>
        {!loading &&
        workerCount > 1 && (
          <Tooltip overlay={translate('background_tasks.number_of_workers.warning')}>
            <i className="icon-alert-warn little-spacer-right bt-workers-warning-icon" />
          </Tooltip>
        )}

        {translate('background_tasks.number_of_workers')}

        {loading ? (
          <i className="spinner little-spacer-left" />
        ) : (
          <strong className="little-spacer-left">{workerCount}</strong>
        )}

        {!loading &&
        canSetWorkerCount && (
          <Tooltip overlay={translate('background_tasks.change_number_of_workers')}>
            <a className="icon-edit spacer-left" href="#" onClick={this.handleChangeClick} />
          </Tooltip>
        )}

        {!loading &&
        !canSetWorkerCount && (
          <span className="spacer-left">
            <a className="link-no-underline" href="#" onClick={this.handleHelpClick}>
              <HelpIcon className="text-text-bottom" fill="#cdcdcd" />
            </a>
            <BubblePopupHelper
              isOpen={this.state.noSupportPopup}
              position="bottomright"
              popup={<NoWorkersSupportPopup />}
              togglePopup={this.toggleNoSupportPopup}
            />
          </span>
        )}

        {formOpen && <WorkersForm onClose={this.closeForm} workerCount={this.state.workerCount} />}
      </div>
    );
  }
}
