/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2019-10-29 21:52:51
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import Vue from 'vue';

import Cookies from 'js-cookie';
import lodash from 'lodash';

import 'normalize.css/normalize.css'; // a modern alternative to CSS resets

import Element from 'element-ui';
import './styles/element-variables.scss';

import '@/styles/index.scss'; // global css

import App from './App';
import store from './store';
import router from './router';

import './icons'; // icon
import './permission'; // permission control
import './utils/error-log'; // error log

import * as filters from './filters'; // global filters

import request from '@/utils/request';

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
/*
import { mockXHR } from '../mock'
if (process.env.NODE_ENV === 'unknown env') {
  mockXHR()
}
*/

Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
});

// register global utility filters
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key]);
});

Vue.prototype.$lodash = lodash;
Vue.prototype.$axios = request;

Vue.config.productionTip = false;

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
});
