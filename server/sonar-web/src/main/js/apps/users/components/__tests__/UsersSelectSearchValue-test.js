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
import React from 'react';
import { shallow } from 'enzyme';
import UsersSelectSearchValue from '../UsersSelectSearchValue';

const user = {
  login: 'admin',
  name: 'Administrator',
  avatar: '7daf6c79d4802916d83f6266e24850af'
};

const user2 = {
  login: 'admin',
  name: 'Administrator',
  email: 'admin@admin.ch'
};

it('should render correctly with a user', () => {
  const wrapper = shallow(
    <UsersSelectSearchValue value={user}>{user.name}</UsersSelectSearchValue>
  );
  expect(wrapper).toMatchSnapshot();
});

it('should render correctly with email instead of hash', () => {
  const wrapper = shallow(
    <UsersSelectSearchValue value={user2}>{user2.name}</UsersSelectSearchValue>
  );
  expect(wrapper).toMatchSnapshot();
});

it('should render correctly without value', () => {
  const wrapper = shallow(<UsersSelectSearchValue />);
  expect(wrapper).toMatchSnapshot();
});
