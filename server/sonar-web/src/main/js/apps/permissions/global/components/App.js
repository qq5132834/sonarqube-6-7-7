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
import Helmet from 'react-helmet';
import PageHeader from './PageHeader';
import AllHoldersList from './AllHoldersList';
import PageError from '../../shared/components/PageError';
import { translate } from '../../../../helpers/l10n';
import '../../styles.css';

export default function App(props /*: { organization?: {} } */) {
  return (
    <div className="page page-limited">
      <Helmet title={translate('global_permissions.permission')} />
      <PageHeader organization={props.organization} />
      <PageError />
      <AllHoldersList organization={props.organization} />
    </div>
  );
}
