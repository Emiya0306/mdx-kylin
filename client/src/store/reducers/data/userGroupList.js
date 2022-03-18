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
    pageSize: configs.pageCount.userList,
    totalCount: 0,
    orderBy: null,
    direction: null,
    isLoading: false,
    isFullLoading: false,
  };
}

export default function userGroupList(state = getInitialState(), action) {
  const { type, ...payload } = action;

  switch (action.type) {
    case actionTypes.INIT_USER_GROUP_LIST: {
      return getInitialState();
    }
    case actionTypes.SET_USER_GROUP_LIST: {
      return { ...state, ...payload };
    }
    case actionTypes.PUSH_USER_GROUP_LIST: {
      const { data } = payload;
      return { ...state, ...payload, data: [...state.data, ...data] };
    }
    case actionTypes.SET_USER_GROUP_LIST_LOADING: {
      return { ...state, ...payload };
    }
    case actionTypes.SET_USER_GROUP_LIST_FULL_LOADING: {
      return { ...state, ...payload };
    }
    default:
      return state;
  }
}
