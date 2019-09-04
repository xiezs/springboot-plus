import { constantRoutes } from '@/router'
import { getRoutes } from '@/api/role'
import { asyncRoutesMap } from '@/router/route_map'
import { deepClone, objectMerge } from '@/utils/index'

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
 * @param routes asyncRoutes
 * @param roles 后台获取的个人用户信息携带的roles
 */
export function filterAsyncRoutes(routesMap, routes, roles) {
  let resRoutes = []
  for (let route of routes) {
    // 对象展开符也常用于浅拷贝
    let tempRoute = { ...route }
    let tempRouteMap
    for (let rm of routesMap) {
      if (!rm.name || !route.name | !rm.path | !route.path) {
        console.error(`检查路由表中 ${rm.name} 信息`)
        continue
      }
      if (rm.name === route.name) {
        tempRouteMap = { ...rm }
        break
      }
    }
    debugger
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
  debugger
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
          accessedRoutes = filterAsyncRoutes(
            deepClone(asyncRoutesMap),
            asyncRoutes,
            roles
          )
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
