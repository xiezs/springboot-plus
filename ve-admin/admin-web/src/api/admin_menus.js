/*
 * @Author: 一日看尽长安花
 * @since: 2020-05-31 14:38:23
 * @LastEditTime: 2020-08-02 14:14:29
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

/**
 * 功能点管理的数据
 * @param {*} params
 */
export function menus(params) {
  return request({
    url: '/admin/menus',
    method: 'get',
    params
  });
}

/**
 * 功能点管理的数据
 * @param {*} params
 */
export function createMenuItem(params) {
  return request({
    url: '/admin/menus',
    method: 'post',
    params
  });
}
/**
 * 功能点管理的数据
 * @param {*} params
 */
export function updateMenuItem(params) {
  return request({
    url: '/admin/menus',
    method: 'put',
    params
  });
}
/**
 * 功能点管理的数据
 * @param {*} params
 */
export function delMenuItemsByParent(params) {
  return request({
    url: '/admin/menus',
    method: 'delete',
    params
  });
}
