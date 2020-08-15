/*
 * @Author: 一日看尽长安花
 * @since: 2020-01-13 23:12:07
 * @LastEditTime: 2020-03-04 16:22:10
 * @LastEditors: 一日看尽长安花
 * @Description: 与业务有关的非纯api的js
 */
import { getOrgsByParent } from '@/api/core_org';

/**
 * @description: 异步载入级联器的数据
 * @param {node} 参照cascader的lazyLoad同名参数
 * @param {resolve} 同上
 * @param {type} 第一级字典类型
 */
export function layzyLoadOrgTree(node, resolve) {
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
