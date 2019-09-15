// 后台数据中的对应的路由表
/*
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
*/
import Layout from '@/layout'

const coreRouter = [
  {
    path: '/admin/user/index.do',
    name: '用户功能',
    component: Layout,
    alwaysShow: true,
    meta: {
      affix: true,
      title: '用户管理',
      icon: null,
      roles: [1, 173, 3]
    },
    children: []
  }
]


export default coreRouter
