/*
 * @Author: 一日看尽长安花
 * @since: 2019-10-11 17:40:57
 * @LastEditTime: 2019-10-16 10:46:40
 * @LastEditors: 一日看尽长安花
 * @Description:
 */

/**
 * @description: obj存在为true。由于js存在undefined 和 null两种特殊的数据类型，认为从空间和引用指向上，只要有一个不存在则判断为不存在。
 * @param {Object}
 * @return {Boolean}
 */
export function isExists(obj) {
  return obj !== void 0 && obj !== null;
}

/**
 * @description: obj不存在为true。
 * @param {Object}
 * @return {Boolean}
 */
export function isNotExists(obj) {
  return obj === void 0 || obj === null;
}
