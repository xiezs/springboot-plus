/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-03-05 15:29:10
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function layzyLoadOrgs(params) {
  return request({
    url: '/core/orgs/layzyLoad',
    method: 'get',
    params: params
  });
}

export function immaditeLoadOrgs(params) {
  return request({
    url: '/core/orgs/immediateLoad',
    method: 'get',
    params: params
  });
}
