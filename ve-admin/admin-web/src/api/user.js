/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2019-10-27 23:13:10
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  });
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'get'
    // params: { token }
  });
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  });
}

export function users(params) {
  return request({
    url: '/users',
    method: 'get',
    params: params
  });
}

export function usersMetadata() {
  return request({
    url: '/users/metadata',
    method: 'get'
  });
}
