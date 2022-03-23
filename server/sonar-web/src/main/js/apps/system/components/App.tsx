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
import Helmet from 'react-helmet';
import ClusterSysInfos from './ClusterSysInfos';
import PageHeader from './PageHeader';
import StandaloneSysInfos from './StandaloneSysInfos';
import SystemUpgradeNotif from './system-upgrade/SystemUpgradeNotif';
import { translate } from '../../../helpers/l10n';
import { ClusterSysInfo, getSystemInfo, SysInfo } from '../../../api/system';
import { getSystemLogsLevel, isCluster, parseQuery, Query, serializeQuery } from '../utils';
import { RawQuery } from '../../../helpers/query';
import '../styles.css';

interface Props {
  location: { pathname: string; query: RawQuery };
}

interface State {
  loading: boolean;
  sysInfoData?: SysInfo;
}

export default class App extends React.PureComponent<Props, State> {
  mounted: boolean;
  state: State = { loading: true };

  static contextTypes = {
    router: PropTypes.object
  };

  componentDidMount() {
    this.mounted = true;
    this.fetchSysInfo();
  }

  componentWillUnmount() {
    this.mounted = false;
  }

  fetchSysInfo = () => {
    this.setState({ loading: true });
    getSystemInfo().then(
      (sysInfoData: SysInfo) => {
        if (this.mounted) {
          this.setState({ loading: false, sysInfoData });
        }
      },
      () => {
        if (this.mounted) {
          this.setState({ loading: false });
        }
      }
    );
  };

  toggleSysInfoCards = (toggledCard: string) => {
    const query = parseQuery(this.props.location.query);
    let expandedCards;
    if (query.expandedCards.includes(toggledCard)) {
      expandedCards = query.expandedCards.filter(card => card !== toggledCard);
    } else {
      expandedCards = query.expandedCards.concat(toggledCard);
    }
    this.updateQuery({ expandedCards });
  };

  updateQuery = (newQuery: Query) => {
    const query = serializeQuery({ ...parseQuery(this.props.location.query), ...newQuery });
    this.context.router.replace({ pathname: this.props.location.pathname, query });
  };

  renderSysInfo() {
    const { sysInfoData } = this.state;
    if (!sysInfoData) {
      return null;
    }

    const query = parseQuery(this.props.location.query);
    if (isCluster(sysInfoData)) {
      return (
        <ClusterSysInfos
          expandedCards={query.expandedCards}
          sysInfoData={sysInfoData as ClusterSysInfo}
          toggleCard={this.toggleSysInfoCards}
        />
      );
    }
    return (
      <StandaloneSysInfos
        expandedCards={query.expandedCards}
        sysInfoData={sysInfoData}
        toggleCard={this.toggleSysInfoCards}
      />
    );
  }

  render() {
    const { loading, sysInfoData } = this.state;
    return (
      <div className="page page-limited">
        <Helmet title={translate('system_info.page')} />
        <SystemUpgradeNotif />
        <PageHeader
          loading={loading}
          isCluster={isCluster(sysInfoData)}
          logLevel={getSystemLogsLevel(sysInfoData)}
          showActions={sysInfoData !== undefined}
          onLogLevelChange={this.fetchSysInfo}
        />
        {this.renderSysInfo()}
      </div>
    );
  }
}
