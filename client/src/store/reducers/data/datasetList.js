/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
import * as actionTypes from '../../types';
import { configs } from '../../../constants';

function getInitialState() {
  return {
    data: [],
    columns: [],
    pageOffset: 0,
    pageSize: configs.pageCount.datasetList,
    totalCount: 0,
    orderBy: null,
    direction: null,
    isLoading: false,
  };
}

export default function datasetList(state = getInitialState(), action) {
  const { type, ...payload } = action;

  switch (action.type) {
    case actionTypes.SET_DATASET_LIST: {
      return { ...state, ...payload };
    }
    case actionTypes.SET_DATASET_LIST_LOADING: {
      return { ...state, ...payload };
    }
    default:
      return state;
  }
}
