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
import { uniq } from 'lodash';
import QualityGate from '../qualityGate/QualityGate';
import ApplicationQualityGate from '../qualityGate/ApplicationQualityGate';
import BugsAndVulnerabilities from '../main/BugsAndVulnerabilities';
import CodeSmells from '../main/CodeSmells';
import Coverage from '../main/Coverage';
import Duplications from '../main/Duplications';
import Meta from '../meta/Meta';
import throwGlobalError from '../../../app/utils/throwGlobalError';
import { getMeasuresAndMeta } from '../../../api/measures';
import { getAllTimeMachineData } from '../../../api/time-machine';
import { parseDate } from '../../../helpers/dates';
import { enhanceMeasuresWithMetrics } from '../../../helpers/measures';
import { getLeakPeriod } from '../../../helpers/periods';
import { getCustomGraph, getGraph } from '../../../helpers/storage';
import { METRICS, HISTORY_METRICS_LIST } from '../utils';
import { DEFAULT_GRAPH, getDisplayedHistoryMetrics } from '../../projectActivity/utils';
import { getBranchName } from '../../../helpers/branches';
/*:: import type { Component, History, MeasuresList, Period } from '../types'; */
import '../styles.css';

/*::
type Props = {
  branch?: { name: string },
  component: Component,
  onComponentChange: {} => void
};
*/

/*::
type State = {
  history?: History,
  historyStartDate?: Date,
  loading: boolean,
  measures: MeasuresList,
  periods?: Array<Period>
};
*/

export default class OverviewApp extends React.PureComponent {
  /*:: mounted: boolean; */
  /*:: props: Props; */
  state /*: State */ = {
    loading: true,
    measures: []
  };

  componentDidMount() {
    this.mounted = true;
    const domElement = document.querySelector('html');
    if (domElement) {
      domElement.classList.add('dashboard-page');
    }
    this.loadMeasures().then(this.loadHistory);
  }

  componentDidUpdate(prevProps /*: Props */) {
    if (
      this.props.component.key !== prevProps.component.key ||
      this.props.branch !== prevProps.branch
    ) {
      this.loadMeasures().then(this.loadHistory);
    }
  }

  componentWillUnmount() {
    this.mounted = false;
    const domElement = document.querySelector('html');
    if (domElement) {
      domElement.classList.remove('dashboard-page');
    }
  }

  loadMeasures() {
    const { branch, component } = this.props;
    this.setState({ loading: true });

    return getMeasuresAndMeta(component.key, METRICS, {
      additionalFields: 'metrics,periods',
      branch: branch && getBranchName(branch)
    }).then(
      r => {
        if (this.mounted) {
          this.setState({
            loading: false,
            measures: enhanceMeasuresWithMetrics(r.component.measures, r.metrics),
            periods: r.periods
          });
        }
      },
      error => {
        throwGlobalError(error);
        if (this.mounted) {
          this.setState({ loading: false });
        }
      }
    );
  }

  loadHistory = () => {
    const { branch, component } = this.props;

    let graphMetrics = getDisplayedHistoryMetrics(getGraph(), getCustomGraph());
    if (!graphMetrics || graphMetrics.length <= 0) {
      graphMetrics = getDisplayedHistoryMetrics(DEFAULT_GRAPH, []);
    }

    const metrics = uniq(HISTORY_METRICS_LIST.concat(graphMetrics));
    return getAllTimeMachineData(component.key, metrics, {
      branch: branch && getBranchName(branch)
    }).then(r => {
      if (this.mounted) {
        const history /*: History */ = {};
        r.measures.forEach(measure => {
          const measureHistory = measure.history.map(analysis => ({
            date: parseDate(analysis.date),
            value: analysis.value
          }));
          history[measure.metric] = measureHistory;
        });
        const historyStartDate = history[HISTORY_METRICS_LIST[0]][0].date;
        this.setState({ history, historyStartDate });
      }
    }, throwGlobalError);
  };

  getApplicationLeakPeriod = () =>
    this.state.measures.find(measure => measure.metric.key === 'new_bugs') ? { index: 1 } : null;

  renderLoading() {
    return (
      <div className="text-center">
        <i className="spinner spinner-margin" />
      </div>
    );
  }

  render() {
    const { branch, component } = this.props;
    const { loading, measures, periods, history, historyStartDate } = this.state;

    if (loading) {
      return this.renderLoading();
    }

    const leakPeriod =
      component.qualifier === 'APP' ? this.getApplicationLeakPeriod() : getLeakPeriod(periods);
    const branchName = branch && getBranchName(branch);
    const domainProps = {
      branch: branchName,
      component,
      measures,
      leakPeriod,
      history,
      historyStartDate
    };

    return (
      <div className="page page-limited">
        <div className="overview page-with-sidebar">
          <div className="overview-main page-main">
            {component.qualifier === 'APP' ? (
              <ApplicationQualityGate component={component} />
            ) : (
              <QualityGate branch={branchName} component={component} measures={measures} />
            )}

            <div className="overview-domains-list">
              <BugsAndVulnerabilities {...domainProps} />
              <CodeSmells {...domainProps} />
              <Coverage {...domainProps} />
              <Duplications {...domainProps} />
            </div>
          </div>

          <div className="page-sidebar-fixed">
            <Meta
              branch={branchName}
              component={component}
              history={history}
              measures={measures}
              onComponentChange={this.props.onComponentChange}
            />
          </div>
        </div>
      </div>
    );
  }
}
