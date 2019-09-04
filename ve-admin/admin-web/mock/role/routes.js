// Just a mock data，模拟后台发送的路由表

export const constantRoutes = []

export const asyncRoutes = [
  {
    path: '/table',
    name: 'Table',
    meta: {
      title: 'Table',
      icon: 'table'
    },
    children: [
      {
        path: 'complex-table',
        name: 'ComplexTable',
        meta: { title: 'Complex Table' }
      }
    ]
  }
]
