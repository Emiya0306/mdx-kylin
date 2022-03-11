/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.kylin.mdx.insight.core.service;

import io.kylin.mdx.insight.common.SemanticException;
import io.kylin.mdx.insight.core.entity.SqlQuery;

import java.util.List;

/**
 * @author
 */
public interface SqlQueryService {
    /**
     *
     * @param sqlQuery
     * @return
     * @throws SemanticException
     */
    String insertSqlQuery(SqlQuery sqlQuery) throws SemanticException;

    /**
     *
     * @param pageNum
     * @param pageSize
     * @param mdxQueryId
     * @param status
     * @return
     */
    List<SqlQuery> selectSqlQueryByPage(Integer pageNum, Integer pageSize, String mdxQueryId, Boolean status);
}
