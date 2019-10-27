/*
 * @Description: 字符串工具类
 * @Author: 一日看尽长安花
 * @Date: 2019-10-11 17:40:47
 * @LastEditTime: 2019-10-16 10:59:45
 * @LastEditors: 一日看尽长安花
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

/**
 * @description: 比较字符串
 * @param {String, String, Boolean}
 * @return {Boolean}
 */
export function equals(str1, str2, ignoreCase = false) {
  if (isNotExists(str1)) {
    return isNotExists(str2);
  }
  if (isNotExists(str2)) {
    return false;
  }
  if (ignoreCase) {
    return str1.toLowerCase() === str2.toLowerCase();
  } else {
    return str1 === str2;
  }
}

/**
 * @description: 比较字符串，比较时忽视大小写比较
 * @param {String, String}
 * @return {Boolean}
 */
export function equalsIgnoreCase(str1, str2) {
  return equals(str1, str2, true);
}
