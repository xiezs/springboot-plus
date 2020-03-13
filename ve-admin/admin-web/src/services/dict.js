/*
 * @Author: 一日看尽长安花
 * @since: 2020-01-13 23:12:07
 * @LastEditTime: 2020-03-09 19:32:36
 * @LastEditors: 一日看尽长安花
 * @Description: 与业务有关的非纯api的js
 */
import { layzyLoadDicts, immaditeLoadDicts } from '@/api/dict';

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
 * @description: 将json中一个key值为数组的每个值转成指定的key-value，主要用于el中的级联选择器
 * @param {obj} 包含选中值的对象
 * @param {arrayKey} 数组的key
 * @param {keyNames} 转换后的 key 名称级，必须按照数组中每个值的顺序对应
 * @returns {Object} 包含转换后的 key-value 值
 */
export function handleCascaderValue(obj, arrayKey, keyNames) {
  let selValArray = obj[arrayKey] || [];
  let resObj = {};
  for (var i in selValArray) {
    if (keyNames.length === 1) {
      resObj[keyNames[0]] = selValArray[i];
    } else {
      resObj[keyNames[i]] = selValArray[i];
    }
  }
  delete obj[arrayKey];
  return Object.assign({}, obj, resObj);
}
