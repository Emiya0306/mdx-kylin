/*
  Copyright (C) 2021 Kyligence Inc. All rights reserved.

  http://kyligence.io

  This software is the confidential and proprietary information of
  Kyligence Inc. ("Confidential Information"). You shall not disclose
  such Confidential Information and shall use it only in accordance
  with the terms of the license agreement you entered into with
  Kyligence Inc.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import { UserGroupService } from '../../services';
import * as actionTypes from '../types';
import { configs } from '../../constants';
import { storeHelper } from '../../utils';

let fetchAllData = null;

/**
 * Action: 获取所有用户数据
 */
export function getAllUserGroups(options) {
  return async dispatch => {
    if (!fetchAllData) {
      fetchAllData = storeHelper.createFetchAllData(configs.batchDataCount.userGroup);
    }
    return dispatch(fetchAllData({
      options,
      fetchApi: UserGroupService.fetchUserGroups,
      initListAction: actionTypes.INIT_USER_GROUP_LIST,
      pushDataAction: actionTypes.PUSH_USER_GROUP_LIST,
      setLoadingAction: actionTypes.SET_USER_GROUP_LIST_LOADING,
      setFullLoadingAction: actionTypes.SET_USER_GROUP_LIST_FULL_LOADING,
    }));
  };
}
