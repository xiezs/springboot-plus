/*
路由映射表，由路由名映射确定。
格式见最下方的注释
需要大改菜单表
 */
/* Layout */
import Layout from '@/layout'

/* Router Modules */
// import componentsRouter from './modules/components'
// import chartsRouter from './modules/charts'
// import tableRouter from './modules/table'
// import nestedRouter from './modules/nested'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 * 用来匹配后台生成的路由表，根据路由名称
 */
export const asyncRoutesMap = [
  {
    path: '/table',
    component: Layout,
    redirect: '/table/complex-table',
    alwaysShow: true,
    name: 'Table',
    meta: {
      noCache: true,
      affix: false,
      breadcrumb: false
    },
    children: [
      {
        path: 'complex-table',
        component: () => import('@/views/table/complex-table'),
        name: 'ComplexTable'
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]
/*
前端路由表中单个路由映射全部具有的信息
  {
    "path": "/profile",
    "component": "Layout",
    "redirect": "/profile/index",
    "hidden": true,
    "alwaysShow": true,
    "name": "router-name",
    "meta": {
      "noCache": true,
      "affix": true,
      "breadcrumb": false,
      "activeMenu": "/example/list"
    },
    "children": []
  }
后端路由表中单个路由应该具有的信息
  {
    "path": "/profile",
    "name": "router-name",
    "meta": {
      "title": "Profile",
      "roles": ["admin", "editor"],
      "icon": "user"
    },
    "children": []
  }




*/
