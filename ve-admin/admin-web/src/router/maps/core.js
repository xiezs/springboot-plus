/*
前端路由映射表中单个路由映射全部具有的信息
  {
    "path": "/profile",
    "component": "Layout",
    "redirect": "/profile/index",
    "hidden": true,//非菜单路由需要设置
    "alwaysShow": true,//默认不设置
    "name": "router-name",
    "meta": {
      "noCache": true,//默认缓存
      "affix": true,
      "breadcrumb": false,
      "activeMenu": "/example/list"
    },
    "children": []
  }
*/
import Layout from '@/layout';

const coreRouter = [
  {
    path: '/profile',
    component: Layout,
    name: 'router-name',
    children: [],
  },
];

export default coreRouter;
