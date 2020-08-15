/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-08-02 14:16:39
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function layzyLoadDicts(params) {
  return request({
    url: '/core/dicts/layzyLoad',
    method: 'get',
    params: params
  });
}

export function immaditeLoadDicts(params) {
  return request({
    url: '/core/dicts/immediateLoad',
    method: 'get',
    params: params
  });
}
