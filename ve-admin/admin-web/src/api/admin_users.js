/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-08-02 14:10:48
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function login(data) {
  return request({
    url: '/core/user/login',
    method: 'post',
    data
  });
}
/*用于vue-store中使用，获取当前用户的信息*/
export function getInfo(token) {
  return request({
    url: '/core/user/info',
    method: 'get'
    // params: { token }
  });
}

export function logout() {
  return request({
    url: '/core/user/logout',
    method: 'post'
  });
}

/**
 * 用户管理页的数据
 * @param {*} params
 */
export function users(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  });
}
/**
 * 通过用户id获取指定用户数据
 * @param {*} params
 */
export function getUserById(params) {
  return request({
    url: `/admin/users/${params.id}`,
    method: 'get'
  });
}

export function usersMetadata() {
  return request({
    url: '/admin/users/metadata',
    method: 'get'
  });
}

export function saveUserData(data) {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  });
}

export function updateUserData(data) {
  return request({
    url: '/admin/users',
    method: 'put',
    data
  });
}
/**
 * 删除用户管理页面的数据
 * @param {*} data
 */
export function deleteUserData(data) {
  return request({
    url: '/admin/users',
    method: 'delete',
    data
  });
}
/**
 * 导出用户管理页面中符合当前查询条件的数据Excel表格
 * @param {*} data
 */
export function exportExcelUserData(data) {
  return request({
    url: '/admin/users/excel/export',
    method: 'post',
    data
  });
}
/**
 * 获取用户管理-操作角色页面的数据
 * @param {*} params
 */
export function getUserRoles(params) {
  return request({
    url: '/admin/users/roles',
    method: 'get',
    params
  });
}
/**
 * 用户操作角色授权
 * @param {Object} data
 */
export function addUserRoles(data) {
  return request({
    url: '/admin/users/roles',
    method: 'post',
    data
  });
}

/**
 * 删除用户授权角色数据
 * @param {*} data 只包括ids一个数组
 */
export function deleteUserRoles(data) {
  return request({
    url: '/admin/users/roles',
    method: 'delete',
    data
  });
}

/**
 * 修改用户密码
 * @param {Object} data {id,password}
 */
export function changePassword(data) {
  return request({
    url: '/admin/users/changePassword',
    method: 'post',
    data
  });
}
