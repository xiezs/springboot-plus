/*
 * @Description: 字符串工具类
 * @Author: 一日看尽长安花
 * @Date: 2019-10-11 17:40:47
 * @LastEditTime: 2019-10-12 09:36:33
 * @LastEditors: Please set LastEditors
 */
import { isExists, isNotExists } from '@/utils/object-util';
import { isString, trim } from 'lodash';

/**
 * @description: 字符串为空串
 * @param {Object}
 * @return {Boolean}
 */
export function isBlank(str) {
  if (isNotExists(str)) {
    if (isString(str)) {
      if (trim(str).length === 0) {
        return true;
      } else {
        return false;
      }
    } else {
      throw 'str is not string type';
    }
  } else {
    throw 'str is null';
  }
}
/**
 * @description: 字符串不为空串
 * @param {Object}
 * @return {Boolean}
 */
export function isNotBlank(str) {
  if (isNotExists(str)) {
    if (isString(str)) {
      if (trim(str).length > 0) {
        return true;
      } else {
        return false;
      }
    } else {
      throw 'str is not string type';
    }
  } else {
    throw 'str is null';
  }
}
