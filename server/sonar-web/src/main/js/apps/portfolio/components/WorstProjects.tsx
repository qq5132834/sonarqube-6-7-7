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
import { max } from 'lodash';
import { SubComponent } from '../types';
import Measure from '../../../components/measure/Measure';
import QualifierIcon from '../../../components/shared/QualifierIcon';
import { translate, translateWithParameters } from '../../../helpers/l10n';
import { formatMeasure } from '../../../helpers/measures';
import { getProjectUrl } from '../../../helpers/urls';

interface Props {
  component: string;
  subComponents: SubComponent[];
  total: number;
}

export default function WorstProjects({ component, subComponents, total }: Props) {
  const count = subComponents.length;

  if (!count) {
    return null;
  }

  const maxLoc = max(
    subComponents.map(component => Number(component.measures['ncloc'] || 0))
  ) as number;

  const projectsPageUrl = { pathname: '/code', query: { id: component } };

  return (
    <div className="panel panel-white portfolio-sub-components" id="portfolio-sub-components">
      <table className="data zebra">
        <thead>
          <tr>
            <th>&nbsp;</th>
            <th className="text-center portfolio-sub-components-cell">
              {translate('metric_domain.Releasability')}
            </th>
            <th className="text-center portfolio-sub-components-cell">
              {translate('metric_domain.Reliability')}
            </th>
            <th className="text-center portfolio-sub-components-cell">
              {translate('metric_domain.Security')}
            </th>
            <th className="text-center portfolio-sub-components-cell">
              {translate('metric_domain.Maintainability')}
            </th>
            <th className="text-center portfolio-sub-components-cell">
              {translate('metric.ncloc.name')}
            </th>
          </tr>
        </thead>
        <tbody>
          {subComponents.map(component => (
            <tr key={component.key}>
              <td>
                <Link
                  to={getProjectUrl(component.refKey || component.key)}
                  className="link-with-icon">
                  <QualifierIcon qualifier={component.qualifier} /> {component.name}
                </Link>
              </td>
              {component.qualifier === 'TRK' ? (
                renderCell(component.measures, 'alert_status', 'LEVEL')
              ) : (
                renderCell(component.measures, 'releasability_rating', 'RATING')
              )}
              {renderCell(component.measures, 'reliability_rating', 'RATING')}
              {renderCell(component.measures, 'security_rating', 'RATING')}
              {renderCell(component.measures, 'sqale_rating', 'RATING')}
              {renderNcloc(component.measures, maxLoc)}
            </tr>
          ))}
        </tbody>
      </table>

      {total > count && (
        <footer className="spacer-top note text-center">
          {translateWithParameters(
            'x_of_y_shown',
            formatMeasure(count, 'INT'),
            formatMeasure(total, 'INT')
          )}
          <Link to={projectsPageUrl} className="spacer-left">
            {translate('show_more')}
          </Link>
        </footer>
      )}
    </div>
  );
}

function renderCell(measures: { [key: string]: string | undefined }, metric: string, type: string) {
  return (
    <td className="text-center">
      <Measure measure={{ metric: { key: metric, type }, value: measures[metric] }} />
    </td>
  );
}

function renderNcloc(measures: { [key: string]: string | undefined }, maxLoc: number) {
  const ncloc = Number(measures['ncloc'] || 0);
  const barWidth = maxLoc > 0 ? Math.max(1, Math.round(ncloc / maxLoc * 50)) : 0;
  return (
    <td className="text-right">
      <span className="note">
        <Measure
          measure={{
            metric: { key: 'ncloc', type: 'SHORT_INT' },
            value: measures['ncloc']
          }}
        />
      </span>
      {maxLoc > 0 && (
        <svg width="50" height="16" className="spacer-left">
          <rect className="bar-chart-bar" x="0" y="3" width={barWidth} height="10" />
        </svg>
      )}
    </td>
  );
}
