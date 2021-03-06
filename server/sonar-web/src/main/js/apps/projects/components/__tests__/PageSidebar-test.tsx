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
import { shallow } from 'enzyme';
import PageSidebar from '../PageSidebar';

it('should render correctly', () => {
  const sidebar = shallow(
    <PageSidebar query={{ size: '3' }} view="overall" visualization="risk" isFavorite={true} />
  );
  expect(sidebar).toMatchSnapshot();
});

it('should render `leak` view correctly', () => {
  const sidebar = shallow(
    <PageSidebar query={{ view: 'leak' }} view="leak" visualization="risk" isFavorite={false} />
  );
  expect(sidebar).toMatchSnapshot();
});

it('reset function should work correctly with view and visualizations', () => {
  const sidebar = shallow(
    <PageSidebar
      query={{ view: 'visualizations', visualization: 'bugs' }}
      view="visualizations"
      visualization="bugs"
      isFavorite={false}
    />
  );
  expect(sidebar.find('.projects-facets-reset')).toMatchSnapshot();
  sidebar.setProps({ query: { size: '3' } });
  expect(sidebar.find('.projects-facets-reset')).toMatchSnapshot();
});
