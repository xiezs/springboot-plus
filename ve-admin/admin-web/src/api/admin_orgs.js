/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-08-02 14:12:55
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function getOrgs(params) {
  return request({
    url: '/admin/orgs/list',
    method: 'get',
    params
  });
}
export function getOrgById(params) {
  return request({
    url: `/admin/orgs/${params.id}`,
    method: 'get'
  });
}

export function saveOrgData(data) {
  return request({
    url: '/admin/orgs/add',
    method: 'post',
    data
  });
}

export function updateOrgData(data) {
  return request({
    url: '/admin/orgs/update',
    method: 'put',
    data
  });
}
export function deleteOrgData(data) {
  return request({
    url: '/admin/orgs/delete',
    method: 'delete',
    data
  });
}
