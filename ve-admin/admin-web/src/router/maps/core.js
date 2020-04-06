/*
 * @Author: 一日看尽长安花
 * @since: 2019-09-15 15:22:58
 * @LastEditTime: 2020-03-29 16:00:28
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
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
    path: '/admin',
    name: '基础管理',
    component: Layout,
    meta: {},
    children: [
      {
        path: '/admin/user/index.do',
        name: '用户管理',
        component: () => import('@/views/users/index'),
        meta: {}
      },
      {
        path: '/admin/user/:id/role',
        name: 'ManagerUserRole',
        hidden: true,
        component: () => import('@/views/users/roles'),
        meta: {}
      },
      {
        path: '/admin/org/index.do',
        name: '组织机构管理',
        meta: {}
      },
      {
        path: '/admin/role/index.do',
        name: '角色管理',
        meta: {}
      },
      {
        path: '/admin/menu/index.do',
        name: '菜单项',
        meta: {}
      },
      {
        path: '/admin/function/index.do',
        name: '功能点管理',
        meta: {}
      },
      {
        path: '/admin/dict/index.do',
        name: '字典数据管理',
        meta: {}
      },
      {
        path: '/admin/role/function.do',
        name: '角色功能授权',
        meta: {}
      },
      {
        path: '/admin/role/data.do',
        name: '角色数据授权',
        meta: {}
      }
    ]
  },
  {
    path: '/code',
    name: '代码生成导航',
    component: Layout,
    meta: {},
    children: [
      {
        path: '/core/codeGen/project.do',
        name: '子系统生成',
        meta: {}
      },
      {
        path: '/core/codeGen/index.do',
        name: '代码生成',
        meta: {}
      }
    ]
  },
  {
    path: '/monitor',
    name: '监控管理',
    component: Layout,
    meta: {},
    children: [
      {
        path: '/admin/workflow/index.do',
        name: '流程监控',
        meta: {}
      },
      {
        path: '/admin/audit/index.do',
        name: '审计查询',
        meta: {}
      },
      {
        path: '/admin/blog/index.do',
        name: '博客测试',
        meta: {}
      }
    ]
  }
];

export default coreRouter;
