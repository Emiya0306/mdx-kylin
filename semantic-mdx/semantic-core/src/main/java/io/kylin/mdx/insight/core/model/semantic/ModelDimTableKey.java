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


package io.kylin.mdx.insight.core.model.semantic;

import io.kylin.mdx.insight.core.entity.CustomHierarchy;
import io.kylin.mdx.insight.core.entity.NamedDimTable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class ModelDimTableKey {
    private String modelName;

    private String dimTable;

    public ModelDimTableKey(NamedDimTable namedDimTable) {
        this.modelName = namedDimTable.getModel();
        this.dimTable = namedDimTable.getDimTable();
    }

    public ModelDimTableKey(CustomHierarchy customHierarchy) {
        this.modelName = customHierarchy.getModel();
        this.dimTable = customHierarchy.getDimTable();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ModelDimTableKey)) {
            return false;
        }
        ModelDimTableKey that = (ModelDimTableKey) o;

        return getModelName().equals(that.getModelName()) &&
                getDimTable().equals(that.getDimTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModelName(), getDimTable());
    }
}
