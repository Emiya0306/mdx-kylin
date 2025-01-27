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
function findSemanticModel(json, usageModel) {
  return json.models.find(model => model.model_name === usageModel.model_name);
}

function checkIsAsPublicTable(semanticTables, usageTable) {
  return !semanticTables.find(semanticTable => (semanticTable.name === usageTable.table_name));
}

function checkIsNotParentRelation(modelRelation, parentModels = []) {
  const modelLeft = modelRelation.model_left;
  const modelRight = modelRelation.model_right;
  return !parentModels.some(model => [modelLeft, modelRight].includes(model));
}

function checkIsRelatedRelation(modelRelation, modelName) {
  return modelRelation.model_left === modelName || modelRelation.model_right === modelName;
}

function checkHasRelatedTable(relations, tableName) {
  return relations.some(relation => (relation.left === tableName || relation.right === tableName));
}

function getRelatedModel(modelRelation, modelName) {
  const modelLeft = modelRelation.model_left;
  const modelRight = modelRelation.model_right;
  return modelName === modelLeft ? modelRight : modelLeft;
}

function getRelatedRelations(json, modelName, tableName) {
  return json.model_relations.filter(modelRelation => (
    checkIsRelatedRelation(modelRelation, modelName) &&
    checkHasRelatedTable(modelRelation.relation, tableName)
  ));
}

function findRelatedModels(json, modelName, tableName, parentModels = []) {
  let relatedModels = [];
  const modelRelations = getRelatedRelations(json, modelName, tableName);

  for (const modelRelation of modelRelations) {
    if (!relatedModels.includes(modelName)) {
      relatedModels = [...relatedModels, modelName];
    }
    if (checkIsNotParentRelation(modelRelation, parentModels)) {
      const nextParents = [...parentModels, modelName];
      const nextModel = getRelatedModel(modelRelation, modelName);
      const nextRelatedModels = findRelatedModels(json, nextModel, tableName, nextParents);
      relatedModels = [...relatedModels, ...nextRelatedModels];
    }
  }

  return relatedModels;
}

function findPublicRoot(json, relatedModels = [], tableName) {
  return json.models.find(model => {
    const isRelatedModel = relatedModels.includes(model.model_name);
    const hasPublicTable = model.dimension_tables.some(table => table.name === tableName);
    return isRelatedModel && hasPublicTable;
  }).model_name;
}

function findPublicRelationIdx(json, publicModel) {
  return json.model_relations.findIndex(
    modelRelations => modelRelations.model_left === publicModel,
  );
}

function getNoPublicRelations(json, publicRelationIdxs) {
  return json.model_relations.filter((item, idx) => !publicRelationIdxs.includes(idx));
}

function getPublicRelations(json, publicRelationIdxs) {
  return json.model_relations.filter((item, idx) => publicRelationIdxs.includes(idx));
}

function getOrderedPublicRoot(publicRelations, currentRelation, direction) {
  const reverseDirection = direction === 'model_left' ? 'model_right' : 'model_left';
  const nextRelations = publicRelations.filter(modelRelation => (
    (
      modelRelation.model_left === currentRelation[direction] ||
      modelRelation.model_right === currentRelation[direction]
    ) && (
      modelRelation.model_left !== currentRelation[reverseDirection] &&
      modelRelation.model_right !== currentRelation[reverseDirection]
    )
  ));
  const resultRelations = [currentRelation];

  for (const nextRelation of nextRelations) {
    const nextDirection = nextRelation.model_left === currentRelation[direction] ? 'model_right' : 'model_left';
    resultRelations.push(...getOrderedPublicRoot(publicRelations, nextRelation, nextDirection));
  }

  return resultRelations;
}

function getOrderedPublicRelations(json, publicRelationIdxs, fullModels = []) {
  const orderedPublicRelations = [];
  const publicRelations = getPublicRelations(json, publicRelationIdxs);
  const publicRoots = publicRelations.filter(
    modelRelation => fullModels.includes(modelRelation.model_left),
  );

  for (const publicRoot of publicRoots) {
    const orderedPublicRoot = getOrderedPublicRoot(publicRelations, publicRoot, 'model_right');
    orderedPublicRelations.push(...orderedPublicRoot);
  }

  return orderedPublicRelations;
}

/* eslint-disable no-console */
export function upgradeDatasetV02(json) {
  try {
    const publicRelationIdxs = [];
    const fullModels = [];

    // 从维度用法中还原该模型下所有的数据源表
    for (const usageModel of json.dim_table_model_relations) {
      const semanticModel = findSemanticModel(json, usageModel);
      const semanticTables = semanticModel.dimension_tables;

      let isFullModel = true;

      for (const usageTable of usageModel.table_relations) {
        // 该模型下所有的数据源表是否在models的维度表中
        const isAsPublicTable = checkIsAsPublicTable(semanticTables, usageTable);
        // 如果不在，那么该表已经在其他模型中设置为公共维表
        // 1. 找出所有与这张表，这个模型相关联的所有模型
        // 2. 在相关联的所有模型中，查找公共维表所在的那个模型
        // 3. 查找 公共维表所在模型 的 模型关系 位置
        if (isAsPublicTable) {
          const modelName = usageModel.model_name;
          const tableName = usageTable.table_name;
          const relatedModels = findRelatedModels(json, modelName, tableName, []);
          const publicModel = findPublicRoot(json, relatedModels, tableName);
          const publicRelationIdx = findPublicRelationIdx(json, publicModel);
          publicRelationIdxs.push(publicRelationIdx);
          isFullModel = false;
        }
      }

      // 如果在DimensionUsage中没有缺失的table，肯定是一张根节点公共维表模型
      if (isFullModel) {
        fullModels.push(usageModel.model_name);
      }
    }

    const publicRelations = getOrderedPublicRelations(json, publicRelationIdxs, fullModels);
    const noPublicRelations = getNoPublicRelations(json, publicRelationIdxs);
    return {
      ...json,
      model_relations: [
        ...publicRelations,
        ...noPublicRelations,
      ],
    };
  } catch (e) {
    console.warn(`Upgrade dataset [${json.project}].[${json.dataset_name}] to v0.2 failed. Please contact with your administrator.`);
    console.warn(e);
    return json;
  }
}
