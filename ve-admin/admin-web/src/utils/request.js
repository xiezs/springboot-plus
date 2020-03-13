/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-09-09 12:16:28
 * @LastEditTime: 2020-03-08 15:59:43
 * @LastEditors: 一日看尽长安花
 */
import axios from 'axios';
import { MessageBox, Message } from 'element-ui';
import store from '@/store';
import { getToken, setToken } from '@/utils/auth';
import { toCamelCaseDeep } from '@/utils/object-util';

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 50000 // request timeout
});

// request interceptor
service.interceptors.request.use(
  config => {
    const method = config.method;
    /**get方法为params，post、put、delete为data */
    const params = config.params || config.data || null;
    /**下划线转为驼峰 */
    if (params) {
      const sourceParams = Object.assign({}, params);
      const changedKeyParams = Object.assign(
        {},
        toCamelCaseDeep(params),
        sourceParams
      );
      if (method === 'get') {
        config.params = changedKeyParams;
      } else {
        config.data = changedKeyParams;
      }
    }
    // do something before request is sent
    if (store.getters.token) {
      // let each request carry token
      // ['Authorization'] see to MDN explain about "HTTP Authorization"
      // please modify it according to the actual situation
      config.headers['Authorization'] = getToken();
    }
    return config;
  },
  error => {
    // do something with request error
    console.log('request err => ' + error); // for debug
    return Promise.reject(error);
  }
);

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
   */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  (response, b, c, d) => {
    const res = response.data;
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 200) {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      });

      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        MessageBox.confirm(
          'You have been logged out, you can cancel to stay on this page, or log in again',
          'Confirm logout',
          {
            confirmButtonText: 'Re-Login',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        ).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload();
          });
        });
      }
      /**将code非200返回码的情况转成错误传出去 */
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      /** 每次请求成功都要将授权码存放在cookie中，只要五分钟无动作登录授权便失效 */
      const authorization = response.headers['authorization'];
      setToken(authorization);
      return res;
    }
  },
  error => {
    console.log('response err ==> ' + error); // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(error);
  }
);

export default service;
