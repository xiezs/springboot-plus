/*
 * @Author: 一日看尽长安花
 * @since: 2020-05-31 14:38:23
 * @LastEditTime: 2020-05-31 14:45:16
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

/**
 * 用户管理页的数据
 * @param {*} params
 */
export function funcs(params) {
  return request({
    url: '/functions',
    method: 'get',
    params
  });
}
