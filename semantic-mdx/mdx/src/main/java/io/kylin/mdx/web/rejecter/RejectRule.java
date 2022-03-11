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


package io.kylin.mdx.web.rejecter;

import org.olap4j.PreparedOlapStatement;

public interface RejectRule {

    /**
     * 获取一个拒绝策略
     *
     * @return 拒绝策略
     */
    RejectRule getOrNew();

    /**
     * 策略名称，同名的会被覆盖
     *
     * @return  策略名称
     */
    String name();

    /**
     * 尝试执行拒绝策略
     *
     * @return 拒绝策略执行结果
     */
    boolean reject(PreparedOlapStatement statement);

}
