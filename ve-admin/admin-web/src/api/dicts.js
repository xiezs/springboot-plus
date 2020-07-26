/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2020-07-26 15:22:32
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

/**
 * 字典数据管理页的数据
 * @param {*} params
 */
export function getDicts(params) {
  return request({
    url: '/dicts/list',
    method: 'get',
    params
  });
}
/**
 * 通过字典数据id获取指定字典数据数据
 * @param {*} params
 */
export function getDictById(params) {
  return request({
    url: `/dicts/${params.id}`,
    method: 'get'
  });
}

export function saveDictData(data) {
  return request({
    url: '/dicts/add',
    method: 'post',
    data
  });
}

export function updateDictData(data) {
  return request({
    url: '/dicts/update',
    method: 'put',
    data
  });
}
/**
 * 删除字典数据管理页面的数据
 * @param {*} data
 */
export function deleteDictData(data) {
  return request({
    url: '/dicts/delete',
    method: 'delete',
    data
  });
}
export function exportDictExcel(data) {
  return request({
    url: '/dicts/excel/export',
    method: 'get',
    data
  });
}

export function importExcel(data) {
  return request({
    url: '/dicts/excel/import',
    method: 'post',
    data
  });
}
