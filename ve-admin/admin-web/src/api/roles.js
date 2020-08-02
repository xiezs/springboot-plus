/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-08-02 12:57:52
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function getRoles(params) {
  return request({
    url: '/admin/roles/list',
    method: 'get',
    params
  });
}
export function getRoleById(params) {
  return request({
    url: `/admin/roles/${params.id}`,
    method: 'get'
  });
}

export function saveRoleData(data) {
  return request({
    url: '/admin/roles/add',
    method: 'post',
    data
  });
}

export function updateRoleData(data) {
  return request({
    url: '/admin/roles/update',
    method: 'put',
    data
  });
}
export function deleteRoleData(data) {
  return request({
    url: '/admin/roles/delete',
    method: 'delete',
    data
  });
}
