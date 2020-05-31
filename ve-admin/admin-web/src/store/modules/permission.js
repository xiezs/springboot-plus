/*
 * @Description: In User Settings Edit
 * @Author: your name
 * @Date: 2019-09-09 12:16:28
 * @LastEditTime: 2020-05-07 15:14:43
 * @LastEditors: 一日看尽长安花
 */
import { constantRoutes } from '@/router';
import { getRoutes } from '@/api/role';
import { default as asyncRoutesMap } from '@/router/maps/index';
import { deepClone, objectMerge } from '@/utils/index';
import { isExists, isNotExists } from '@/utils/object-util';

import Layout from '@/layout';

/**
 * Use meta.role to determine if the current user has permission
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role));
  } else {
    return true;
  }
}

/**
 * Filter asynchronous routing tables by recursion
 * 通过前端保留的路由映射表来生成路由表
 * 将前端路由表对应的路由覆盖至后端路由中，以后端路由表为主
 * @param routesMap 前端路由映射表
 * @param routes 后端路由表
 * @param roles 后台获取的个人用户信息携带的roles
 */
export function filterAsyncRoutes(routesMap, routes, roles) {
  let resRoutes = [];
  for (let route of routes) {
    // 对象展开符也常用于浅拷贝
    // 前端路由表
    let tempRoute = { ...route };
    // 后端路由表
    let tempRouteMap;
    // 从前端路由表中选出与当前后端路由信息相对应的那条路由信息
    for (let rm of routesMap) {
      if (
        isExists(rm.name) &&
        isExists(route.name) &&
        isExists(rm.path) &&
        isExists(route.path) &&
        (rm.path === route.path || rm.name === route.name)
      ) {
        // 优先path判断，是因为导航菜单的展开和收起是根据path判断的。
        tempRouteMap = { ...rm };
        break;
      }
    }

    // if (tempRouteMap && hasPermission(roles, tempRoute)) {
    if (tempRouteMap) {
      if (tempRoute.children) {
        tempRoute.children = filterAsyncRoutes(
          tempRouteMap.children,
          tempRoute.children,
          roles
        );
      }
      // 以后台路由表优先，相同属性覆盖前台路由映射.除去路由路径交由前台控制
      // 因为path有可能涉及到动态路由的书写 也就是类似： /user/:id 。
      // 这种path可以在组件中读取到传递的id，比较方便，所以交给前端控制。
      let tempPath = tempRouteMap.path;
      tempRouteMap = objectMerge(tempRouteMap, tempRoute);
      tempRouteMap.path = tempPath;
      resRoutes.push(tempRouteMap);
    }
  }
  return resRoutes;
}

export function handleComponent(routes) {
  for (let i in routes) {
    let _route = routes[i];
    if (_route.component && _route.component.trim().length > 0) {
      if (_route.component === 'layout') {
        _route.component = Layout;
      } else {
        /** 这里的异步不能直接在import里用template string写法，babel-eslint有bug*/
        /** 两种异步写法：
         * component = resolve => require([`@/views/modules/${URL}`], resolve)；
         * component = () => import(`@/${_route.component}`);
         */
        //这一步多余的赋值遍历必须存在，import的bug
        //无法直接使用模板字符
        const name = _route.component;
        _route.component = () => import(`@/${name}`);
      }
    }
    if (_route.children && _route.children.length > 0) {
      handleComponent(_route.children);
    }
  }
}

const state = {
  routes: [],
  addRoutes: []
};

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes;
    state.routes = constantRoutes.concat(routes);
  }
};

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise((resolve, reject) => {
      getRoutes()
        .then(response => {
          let asyncRoutes = response.data;
          handleComponent(asyncRoutes);
          asyncRoutes.push({ path: '*', redirect: '/404', hidden: true });
          commit('SET_ROUTES', asyncRoutes);
          resolve(asyncRoutes);
        })
        .catch(error => {
          reject(error);
        });
    });
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions
};
