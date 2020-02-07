/*
 * @Author: 一日看尽长安花
 * @since: 2019-10-11 17:40:57
 * @LastEditTime : 2020-02-04 13:10:03
 * @LastEditors  : 一日看尽长安花
 * @Description:
 */
import lodash from 'lodash';

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

/**
 * 深度遍历object，改变key为驼峰式命名
 * @param {object} obj
 */
export function toCamelCaseObjectDeep(obj) {
  for (let _key in obj) {
    if (obj.hasOwnProperty(_key)) {
      let _val = obj[_key];
      let _valType = Object.prototype.toString.call(_val);
      if (_valType === '[object Array]') {
        _val = toCamelCaseArrayDeep(_val);
      } else if (_valType === '[object Object]') {
        _val = toCamelCaseObjectDeep(_val);
      }
      delete obj[_key];
      obj[lodash.camelCase(_key)] = _val;
    }
  }
  return obj;
}
/**
 * 深度遍历数组，改变其中object元素的key为驼峰式命名
 * @param {object} obj
 */
export function toCamelCaseArrayDeep(list) {
  for (let i in list) {
    const _valType = Object.prototype.toString.call(list[i]);
    if (_valType === '[object Object]') {
      list[i] = toCamelCaseObjectDeep(list[i]);
    } else if (_valType === '[object Array]') {
      list[i] = toCamelCaseArrayDeep(list[i]);
    }
  }
  return list;
}
/**
 * 深度遍历改变data中object 的key的命名为驼峰式命名
 * @param {Object/Array} data
 */
export function toCamelCaseDeep(data) {
  const _dataType = Object.prototype.toString.call(data);
  let changedKeyParams;
  if (_dataType === '[object Object]') {
    changedKeyParams = toCamelCaseObjectDeep(data);
  } else if (_dataType === '[object Array]') {
    changedKeyParams = toCamelCaseArrayDeep(data);
  }
  return changedKeyParams;
}
