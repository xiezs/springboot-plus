/*
 * @Author: 一日看尽长安花
 * @since: 2020-05-31 14:38:23
 * @LastEditTime: 2020-08-02 14:15:21
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

/**
 * 功能点管理的数据
 * @param {*} params
 */
export function funcs(params) {
  return request({
    url: '/admin/functions',
    method: 'get',
    params
  });
}

/**
 * 功能点管理的数据
 * @param {*} params
 */
export function createFuncNode(params) {
  return request({
    url: '/admin/functions',
    method: 'post',
    params
  });
}
/**
 * 功能点管理的数据
 * @param {*} params
 */
export function updateFuncNode(params) {
  return request({
    url: '/admin/functions',
    method: 'put',
    params
  });
}
/**
 * 功能点管理的数据
 * @param {*} params
 */
export function delFuncNodesByParent(params) {
  return request({
    url: '/admin/functions',
    method: 'delete',
    params
  });
}
