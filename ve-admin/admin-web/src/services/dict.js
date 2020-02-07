/*
 * @Author: 一日看尽长安花
 * @since: 2020-01-13 23:12:07
 * @LastEditTime : 2020-02-05 17:03:34
 * @LastEditors  : 一日看尽长安花
 * @Description: 与业务有关的非纯api的js
 */
import { getDictsByParent } from '@/api/dict';

/**
 * @description: 异步载入级联器的数据
 * @param {node} 参照cascader的lazyLoad同名参数
 * @param {resolve} 同上
 * @param {type} 第一级字典类型
 */
export function loadDictCascaderData(node, resolve, type) {
  const { root, data } = node;
  const { id } = data || {};
  let datas = [];
  if (!root) {
    type = null;
  }
  let reqParam = {
    parentId: id,
    type: type
  };
  getDictsByParent(reqParam)
    .then(result => {
      const { code, data } = { ...result };
      // 通过调用resolve将子节点数据返回，通知组件数据加载完成
      datas = data;
    })
    .finally(() => {
      resolve(datas);
    });
}

/**
 * @description: 处理级联器的选中值,级联器选中值是个数组，需要处理为指定key-value
 * @param {obj} 包含选中值的对象
 * @param {modelKey} 级联器 cascader 的 v-model 选中值的key名称
 * @param {keyNames} 转换后的 key 名称级，必须按照级联器选中值的顺序
 * @returns {Object} 包含转换后的 key-value 值
 */
export function handleDictCascaderValue(obj, modelKey, keyNames) {
  let selValArray = obj[modelKey] || [];
  let resObj = {};
  for (var i in selValArray) {
    resObj[keyNames[i]] = selValArray[i];
  }
  delete obj[modelKey];
  return Object.assign({}, obj, resObj);
}
