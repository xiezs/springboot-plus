/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-08-15 21:07:35
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

export function getFunctionIdsByRole(params) {
  return request({
    url: '/admin/roles/function/ids',
    method: 'get',
    params
  });
}

export function updateRoleFunctions(data) {
  return request({
    url: '/admin/roles/function/update',
    method: 'put',
    data
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

export function getRoleDataFunctions(params) {
  return request({
    url: '/admin/roles/data_function/list',
    method: 'get',
    params
  });
}

export function saveRoleDataFunction(data) {
  return request({
    url: '/admin/roles/data_function/updateDataAccess',
    method: 'post',
    data
  });
}
