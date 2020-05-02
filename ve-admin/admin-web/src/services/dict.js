/*
 * @Author: 一日看尽长安花
 * @since: 2020-01-13 23:12:07
 * @LastEditTime: 2020-04-27 15:00:42
 * @LastEditors: 一日看尽长安花
 * @Description: 与业务有关的非纯api的js
 */
import { layzyLoadDicts, immaditeLoadDicts } from '@/api/dict';
import { assignIn, get, omit } from 'lodash';

/**
 * @description: 异步载入级联器的数据
 * @param {node} 参照cascader的lazyLoad同名参数
 * @param {resolve} 同上
 * @param {type} 第一级字典类型
 */
export function layzyLoadDictTree(node, resolve, type) {
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
  layzyLoadDicts(reqParam)
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
 * @description: 用sp-cascader组件，其所有的参数值挂在sp_cascader节点下。
 * @param keyNames 级联属性key
 * @returns {Object}
 */
export function handleCascaderValue(obj, keyNames = []) {
  const sp_cascader = obj['sp_cascader'];
  assignIn(obj, sp_cascader);
  delete obj['sp_cascader'];
  delete obj['spCascader'];
  for (let i in keyNames) {
    const path = keyNames[i];
    const node = get(obj, path);
    const node_type = Object.prototype.toString.call(node);
    if (node_type === '[object Object]') {
      obj = omit(obj, path);
    }
  }
  return obj;
}
