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
import Filter from '../Filter';

it('renders', () => {
  expect(shallowRender()).toMatchSnapshot();
});

it('renders header and footer', () => {
  expect(shallowRender({ header: <header />, footer: <footer /> })).toMatchSnapshot();
});

it('renders no results', () => {
  expect(shallowRender({ options: [] })).toMatchSnapshot();
});

it('highlights under', () => {
  expect(shallowRender({ highlightUnder: 1 })).toMatchSnapshot();
});

it('renders selected', () => {
  expect(shallowRender({ value: 2 })).toMatchSnapshot();
});

it('hightlights under selected', () => {
  expect(shallowRender({ highlightUnder: 1, value: 2 })).toMatchSnapshot();
});

it('renders multiple selected', () => {
  expect(shallowRender({ value: [1, 2] })).toMatchSnapshot();
});

it('renders facet bar chart', () => {
  expect(
    shallowRender({
      getFacetValueForOption: (facet: any, option: any) => facet[option],
      facet: { a: 17, b: 15, c: 24 },
      maxFacetValue: 24,
      options: ['a', 'b', 'c']
    })
  ).toMatchSnapshot();
});

function shallowRender(props?: any) {
  return shallow(
    <Filter
      options={[1, 2, 3]}
      property="foo"
      query={{}}
      renderOption={option => option}
      {...props}
    />
  );
}
