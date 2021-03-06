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
jest.mock('../../permissions/project/views/ApplyTemplateView');

import * as React from 'react';
import { shallow } from 'enzyme';
import Projects from '../Projects';
import ApplyTemplateView from '../../permissions/project/views/ApplyTemplateView';
import { Visibility } from '../../../app/types';

const organization = { key: 'org', name: 'org', projectVisibility: 'public' };
const projects = [
  { key: 'a', name: 'A', qualifier: 'TRK', visibility: Visibility.Public },
  { key: 'b', name: 'B', qualifier: 'TRK', visibility: Visibility.Public }
];
const selection = ['a'];

it('renders list of projects', () => {
  expect(shallowRender({ projects, selection })).toMatchSnapshot();
});

it('selects and deselects project', () => {
  const onProjectDeselected = jest.fn();
  const onProjectSelected = jest.fn();
  const wrapper = shallowRender({ onProjectDeselected, onProjectSelected, projects });

  wrapper
    .find('ProjectRow')
    .first()
    .prop<Function>('onProjectCheck')(projects[0], true);
  expect(onProjectSelected).toBeCalledWith('a');

  wrapper
    .find('ProjectRow')
    .first()
    .prop<Function>('onProjectCheck')(projects[0], false);
  expect(onProjectDeselected).toBeCalledWith('a');
});

it('opens modal to apply permission template', () => {
  const wrapper = shallowRender({ projects });
  wrapper
    .find('ProjectRow')
    .first()
    .prop<Function>('onApplyTemplate')(projects[0]);
  expect(ApplyTemplateView).toBeCalledWith({ organization, project: projects[0] });
});

function shallowRender(props?: any) {
  return shallow(
    <Projects
      currentUser={{ login: 'foo' }}
      onProjectDeselected={jest.fn()}
      onProjectSelected={jest.fn()}
      organization={organization}
      selection={[]}
      {...props}
    />
  );
}
