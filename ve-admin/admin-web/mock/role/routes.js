// Just a mock data，模拟后台发送的路由表
/*
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
export const constantRoutes = []

export const asyncRoutes = [
  {
    path: '/permission',
    name: 'Permission',
    meta: {
      title: 'Permission',
      icon: 'lock',
      roles: ['admin', 'editor'] // you can set roles in root nav
    },
    children: [
      {
        path: 'page',
        name: 'PagePermission',
        meta: {
          title: 'Page Permission',
          roles: ['admin'] // or you can only set roles in sub nav
        }
      },
      {
        path: 'directive',
        name: 'DirectivePermission',
        meta: {
          title: 'Directive Permission'
          // if do not set roles, means: this page does not require permission
        }
      },
      {
        path: 'role',
        name: 'RolePermission',
        meta: {
          title: 'Role Permission',
          roles: ['admin']
        }
      }
    ]
  },

  {
    path: '/icon',
    children: [
      {
        path: 'index',
        name: 'Icons',
        meta: { title: 'Icons', icon: 'icon' }
      }
    ]
  },

  {
    path: '/components',
    name: 'ComponentDemo',
    meta: {
      title: 'Components',
      icon: 'component'
    },
    children: [
      {
        path: 'tinymce',
        name: 'TinymceDemo',
        meta: { title: 'Tinymce' }
      },
      {
        path: 'markdown',
        name: 'MarkdownDemo',
        meta: { title: 'Markdown' }
      },
      {
        path: 'json-editor',
        name: 'JsonEditorDemo',
        meta: { title: 'JSON Editor' }
      },
      {
        path: 'split-pane',
        name: 'SplitpaneDemo',
        meta: { title: 'SplitPane' }
      },
      {
        path: 'avatar-upload',
        name: 'AvatarUploadDemo',
        meta: { title: 'Upload' }
      },
      {
        path: 'dropzone',
        name: 'DropzoneDemo',
        meta: { title: 'Dropzone' }
      },
      {
        path: 'sticky',
        name: 'StickyDemo',
        meta: { title: 'Sticky' }
      },
      {
        path: 'count-to',
        name: 'CountToDemo',
        meta: { title: 'Count To' }
      },
      {
        path: 'mixin',
        name: 'ComponentMixinDemo',
        meta: { title: 'Component Mixin' }
      },
      {
        path: 'back-to-top',
        name: 'BackToTopDemo',
        meta: { title: 'Back To Top' }
      },
      {
        path: 'drag-dialog',
        name: 'DragDialogDemo',
        meta: { title: 'Drag Dialog' }
      },
      {
        path: 'drag-select',
        name: 'DragSelectDemo',
        meta: { title: 'Drag Select' }
      },
      {
        path: 'dnd-list',
        name: 'DndListDemo',
        meta: { title: 'Dnd List' }
      },
      {
        path: 'drag-kanban',
        name: 'DragKanbanDemo',
        meta: { title: 'Drag Kanban' }
      }
    ]
  },

  {
    path: '/charts',
    name: 'Charts',
    meta: {
      title: 'Charts',
      icon: 'chart'
    },
    children: [
      {
        path: 'keyboard',
        name: 'KeyboardChart',
        meta: { title: 'Keyboard Chart' }
      },
      {
        path: 'line',
        name: 'LineChart',
        meta: { title: 'Line Chart' }
      },
      {
        path: 'mix-chart',
        name: 'MixChart',
        meta: { title: 'Mix Chart' }
      }
    ]
  },

  {
    path: '/nested',
    name: 'Nested',
    meta: {
      title: 'Nested Routes',
      icon: 'nested'
    },
    children: [
      {
        path: 'menu1',
        name: 'Menu1',
        meta: { title: 'Menu 1' },
        children: [
          {
            path: 'menu1-1',
            name: 'Menu1-1',
            meta: { title: 'Menu 1-1' }
          },
          {
            path: 'menu1-2',
            name: 'Menu1-2',
            meta: { title: 'Menu 1-2' },
            children: [
              {
                path: 'menu1-2-1',
                name: 'Menu1-2-1',
                meta: { title: 'Menu 1-2-1' }
              },
              {
                path: 'menu1-2-2',
                name: 'Menu1-2-2',
                meta: { title: 'Menu 1-2-2' }
              }
            ]
          },
          {
            path: 'menu1-3',
            name: 'Menu1-3',
            meta: { title: 'Menu 1-3' }
          }
        ]
      },
      {
        path: 'menu2',
        name: 'Menu2',
        meta: { title: 'Menu 2' }
      }
    ]
  },

  {
    path: '/table',
    name: 'Table',
    meta: {
      title: 'Table',
      icon: 'table'
    },
    children: [
      {
        path: 'dynamic-table',
        name: 'DynamicTable',
        meta: { title: 'Dynamic Table' }
      },
      {
        path: 'drag-table',
        name: 'DragTable',
        meta: { title: 'Drag Table' }
      },
      {
        path: 'inline-edit-table',
        name: 'InlineEditTable',
        meta: { title: 'Inline Edit' }
      },
      {
        path: 'complex-table',
        name: 'ComplexTable',
        meta: { title: 'Complex Table' }
      }
    ]
  },

  {
    path: '/example',
    name: 'Example',
    meta: {
      title: 'Example',
      icon: 'example'
    },
    children: [
      {
        path: 'create',
        name: 'CreateArticle',
        meta: { title: 'Create Article', icon: 'edit' }
      },
      {
        path: 'edit/:id(\\d+)',
        name: 'EditArticle',
        meta: {
          title: 'Edit Article',
        }
      },
      {
        path: 'list',
        name: 'ArticleList',
        meta: { title: 'Article List', icon: 'list' }
      }
    ]
  },

  {
    path: '/tab',
    children: [
      {
        path: 'index',
        name: 'Tab',
        meta: { title: 'Tab', icon: 'tab' }
      }
    ]
  },

  {
    path: '/error',
    name: 'ErrorPages',
    meta: {
      title: 'Error Pages',
      icon: '404'
    },
    children: [
      {
        path: '401',
        name: 'Page401',
        meta: { title: '401' }
      },
      {
        path: '404',
        name: 'Page404',
        meta: { title: '404' }
      }
    ]
  },

  {
    path: '/error-log',
    children: [
      {
        path: 'log',
        name: 'ErrorLog',
        meta: { title: 'Error Log', icon: 'bug' }
      }
    ]
  },

  {
    path: '/excel',
    name: 'Excel',
    meta: {
      title: 'Excel',
      icon: 'excel'
    },
    children: [
      {
        path: 'export-excel',
        name: 'ExportExcel',
        meta: { title: 'Export Excel' }
      },
      {
        path: 'export-selected-excel',
        name: 'SelectExcel',
        meta: { title: 'Export Selected' }
      },
      {
        path: 'export-merge-header',
        name: 'MergeHeader',
        meta: { title: 'Merge Header' }
      },
      {
        path: 'upload-excel',
        name: 'UploadExcel',
        meta: { title: 'Upload Excel' }
      }
    ]
  },

  {
    path: '/zip',
    name: 'Zip',
    meta: { title: 'Zip', icon: 'zip' },
    children: [
      {
        path: 'download',
        name: 'ExportZip',
        meta: { title: 'Export Zip' }
      }
    ]
  },

  {
    path: '/pdf',
    children: [
      {
        path: 'index',
        name: 'PDF',
        meta: { title: 'PDF', icon: 'pdf' }
      }
    ]
  },
  {
    path: '/pdf/download',
    hidden: true
  },

  {
    path: '/theme',
    children: [
      {
        path: 'index',
        name: 'Theme',
        meta: { title: 'Theme', icon: 'theme' }
      }
    ]
  },

  {
    path: '/clipboard',
    children: [
      {
        path: 'index',
        name: 'ClipboardDemo',
        meta: { title: 'Clipboard', icon: 'clipboard' }
      }
    ]
  },

  {
    path: 'external-link',
    children: [
      {
        path: 'https://github.com/PanJiaChen/vue-element-admin',
        meta: { title: 'External Link', icon: 'link' }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]
