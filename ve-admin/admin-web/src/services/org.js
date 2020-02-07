/*
 * @Author: 一日看尽长安花
 * @since: 2020-01-13 23:12:07
 * @LastEditTime : 2020-02-05 17:04:59
 * @LastEditors  : 一日看尽长安花
 * @Description: 与业务有关的非纯api的js
 */
import { getOrgsByParent } from '@/api/org';

/**
 * @description: 异步载入级联器的数据
 * @param {node} 参照cascader的lazyLoad同名参数
 * @param {resolve} 同上
 * @param {type} 第一级字典类型
 */
export function loadOrgCascaderData(node, resolve) {
  const { root, data } = node;
  const { id } = data || {};
  let datas = [];
  getOrgsByParent({
    parentId: id
  })
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
 * @description: 组织机构是唯一级别的，只取最后一级的value。所以不像字典多个级别
 * @param {obj} 包含选中值的对象
 * @param {modelKey} 级联器 cascader 的 v-model 选中值的key名称
 * @param {keyName} 转换后的 key 名称，必须按照级联器选中值的顺序
 * @returns {Object} 包含转换后的 key-value 值
 */
export function handleOrgCascaderValue(obj, modelKey, keyName) {
  let selValArray = obj[modelKey] || [];
  obj[keyName] = selValArray[selValArray.length - 1];
  delete obj[modelKey];
  return obj;
}
