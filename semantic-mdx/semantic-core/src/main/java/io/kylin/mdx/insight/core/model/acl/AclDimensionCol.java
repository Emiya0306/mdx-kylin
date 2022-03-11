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


package io.kylin.mdx.insight.core.model.acl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kylin.mdx.insight.core.model.semantic.SemanticDataset;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class AclDimensionCol extends AclAccessible {

    private String alias;

    @JsonProperty("name_column")
    private String nameColumn;

    @JsonProperty("value_column")
    private String valueColumn;

    private List<AclProperty> properties;

    public AclDimensionCol(SemanticDataset.AugmentedModel.AugmentDimensionTable.AugmentDimensionCol dimensionCol) {
        this.setName(dimensionCol.getName());
        this.alias = dimensionCol.getAlias();
        this.nameColumn = dimensionCol.getNameColumn();
        this.valueColumn = dimensionCol.getValueColumn();
        if (dimensionCol.getProperties() != null) {
            this.properties = new ArrayList<>();
            for (SemanticDataset.AugmentedModel.AugmentDimensionTable.AugmentDimensionCol.AugmentProperty property :
                    dimensionCol.getProperties()) {
                AclProperty aclProperty = new AclProperty();
                aclProperty.setName(property.getName());
                aclProperty.setColName(property.getName());
                aclProperty.setColAlias(property.getAttribute());
                this.properties.add(aclProperty);
            }
        }
    }

}
