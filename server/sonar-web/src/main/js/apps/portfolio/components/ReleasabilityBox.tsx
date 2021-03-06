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
import RatingFreshness from './RatingFreshness';
import Rating from '../../../components/ui/Rating';
import Measure from '../../../components/measure/Measure';
import { translate } from '../../../helpers/l10n';
import { getComponentDrilldownUrl } from '../../../helpers/urls';

interface Props {
  component: string;
  measures: { [key: string]: string | undefined };
}

export default function ReleasabilityBox({ component, measures }: Props) {
  const rating = measures['releasability_rating'];
  const lastReleasabilityChange = measures['last_change_on_releasability_rating'];
  const effort = measures['releasability_effort'];

  return (
    <div className="portfolio-box portfolio-releasability">
      <h2 className="portfolio-box-title">{translate('metric_domain.Releasability')}</h2>

      {rating && (
        <Link
          to={getComponentDrilldownUrl(component, 'alert_status')}
          className="portfolio-box-rating">
          <Rating value={rating} />
        </Link>
      )}

      <RatingFreshness lastChange={lastReleasabilityChange} />

      {effort &&
      Number(effort) > 0 && (
        <div className="portfolio-effort">
          <Link to={getComponentDrilldownUrl(component, 'alert_status')}>
            <span>
              <Measure
                measure={{ metric: { key: 'projects', type: 'SHORT_INT' }, value: effort }}
              />{' '}
              {Number(effort) === 1 ? 'project' : 'projects'}
            </span>
          </Link>{' '}
          <span className="level level-ERROR level-small">{translate('metric.level.ERROR')}</span>
        </div>
      )}
    </div>
  );
}
