/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime : 2020-01-11 20:47:10
 * @LastEditors  : 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function getDictsByParent(params) {
  return request({
    url: '/core/dicts',
    method: 'get',
    params: params
  });
}
