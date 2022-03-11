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


package io.kylin.mdx.insight.core.entity;

import com.alibaba.fastjson.JSON;
import io.kylin.mdx.insight.common.util.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@Table(name = "role_info")
public class RoleInfo {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "extend")
    private String extend;

    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "modify_time")
    private Long modifyTime;

    public RoleInfo(String name, List<VisibleAttr> visible, String description) {
        this.name = name;
        this.extend = new EntityContains().withContains(visible).take();
        if (description != null) {
            this.description = description;
        } else {
            this.description = "";
        }
        this.description = description;
        this.createTime = Utils.currentTimeStamp();
        this.modifyTime = createTime;
    }

    public RoleInfo(Integer id) {
        this.id = id;
    }

    public RoleInfo(String name) {
        this.name = name;
    }

    public List<VisibleAttr> extractVisibleFromExtend() {
        EntityContains nsExtend = JSON.parseObject(extend, EntityContains.class);
        return nsExtend == null ? Collections.emptyList() : nsExtend.getContains() == null ? Collections.emptyList() : nsExtend.getContains();
    }

}
