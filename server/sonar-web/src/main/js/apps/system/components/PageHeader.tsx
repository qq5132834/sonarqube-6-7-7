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
import PageActions from './PageActions';
import { translate } from '../../../helpers/l10n';

interface Props {
  isCluster: boolean;
  loading: boolean;
  logLevel: string;
  showActions: boolean;
  onLogLevelChange: () => void;
}

export default function PageHeader(props: Props) {
  return (
    <header className="page-header">
      <h1 className="page-title">{translate('system_info.page')}</h1>
      {props.showActions && (
        <PageActions
          canDownloadLogs={!props.isCluster}
          canRestart={!props.isCluster}
          cluster={props.isCluster}
          logLevel={props.logLevel}
          onLogLevelChange={props.onLogLevelChange}
        />
      )}
      {props.loading && (
        <div className="page-actions">
          <i className="spinner" />
        </div>
      )}
    </header>
  );
}
