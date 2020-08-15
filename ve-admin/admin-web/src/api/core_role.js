/*
 * @Author: 一日看尽长安花
 * @since: 2019-12-01 11:03:53
 * @LastEditTime: 2020-08-02 14:08:43
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function getRoutes() {
  return request({
    url: '/core/user/routes',
    method: 'get'
  });
}

export function immaditeLoadRoles() {
  return request({
    url: '/core/roles/immediateLoad',
    method: 'get'
  });
}

export function addRole(data) {
  return request({
    url: '/role',
    method: 'post',
    data
  });
}

export function updateRole(id, data) {
  return request({
    url: `/role/${id}`,
    method: 'put',
    data
  });
}

export function deleteRole(id) {
  return request({
    url: `/role/${id}`,
    method: 'delete'
  });
}
