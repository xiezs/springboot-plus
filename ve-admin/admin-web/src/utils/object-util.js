/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-10-11 17:40:57
 * @LastEditTime: 2019-10-12 09:24:07
 * @LastEditors: Please set LastEditors
 */

/**
 * @description: obj不存在。由于js存在undefined 和 null两种特殊的数据类型，认为从空间和引用指向上，只要有一个不存在则判断为不存在。
 * @param {Object}
 * @return {Boolean}
 */
export function isExists(obj) {
  return void 0 === obj || null === obj;
}

/**
 * @description: obj存在
 * @param {Object}
 * @return {Boolean}
 */
export function isNotExists(obj) {
  return !isExists(obj);
}
