/*
 * @Author: 一日看尽长安花
 * @since: 2020-05-31 14:38:23
 * @LastEditTime: 2020-06-13 12:42:08
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
    url: '/menus',
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
    url: '/menus',
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
    url: '/menus',
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
    url: '/menus',
    method: 'delete',
    params
  });
}
