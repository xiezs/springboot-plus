import { constantRoutes } from '@/router'
import { getRoutes } from '@/api/role'
import { default as asyncRoutesMap } from '@/router/maps/index'
import { deepClone, objectMerge, isNotNullAndNotUndefined } from '@/utils/index'

/**
 * Use meta.role to determine if the current user has permission
 * @param roles
 * @param route
 */
function hasPermission(roles, route) {
  if (route.meta && route.meta.roles) {
    return roles.some(role => route.meta.roles.includes(role))
  } else {
    return true
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
  let resRoutes = []
  for (let route of routes) {
    // 对象展开符也常用于浅拷贝
    // 前端路由表
    let tempRoute = { ...route }
    // 后端路由表
    let tempRouteMap
    // 从前端路由表中选出与当前后端路由信息相对应的那条路由信息
    for (let rm of routesMap) {
      if (
        isNotNullAndNotUndefined(rm.path) &&
        isNotNullAndNotUndefined(route.path) &&
        rm.path === route.path
      ) {
        tempRouteMap = { ...rm }
        break
      } else {
        // 在开发时期可以看到路由表的残缺，生产环境中建议用另外的日志记录器，或者统一删除console语句
        console.error(
          `【name:${route.name},path:${route.path}】前后端路由信息不相符`
        )
      }
    }

    if (tempRouteMap && hasPermission(roles, tempRoute)) {
      if (tempRoute.children) {
        tempRoute.children = filterAsyncRoutes(
          tempRouteMap.children,
          tempRoute.children,
          roles
        )
      }
      // 以后台路由表优先，相同属性覆盖前台路由映射.除去路由路径交由前台控制
      // 因为path有可能涉及到动态路由的书写 也就是类似： /user/:id 。
      // 这种path可以在组件中读取到传递的id，比较方便，所以交给前端控制。
      let tempPath = tempRouteMap.path
      tempRouteMap = objectMerge(tempRouteMap, tempRoute)
      tempRouteMap.path = tempPath
      resRoutes.push(tempRouteMap)
    }
  }
  return resRoutes
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise((resolve, reject) => {
      getRoutes()
        .then(response => {
          let accessedRoutes,
            asyncRoutes = response.data
          console.log(asyncRoutesMap)
          accessedRoutes = filterAsyncRoutes(
            deepClone(asyncRoutesMap),
            asyncRoutes,
            roles
          )
          debugger
          commit('SET_ROUTES', accessedRoutes)
          resolve(accessedRoutes)
        })
        .catch(error => {
          reject(error)
        })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
