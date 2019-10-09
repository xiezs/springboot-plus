SELECT cu.CODE,
       cd.value,
       cd.name,
       cd.type,
       cd.type_name,
       cd.remark,
       cd2.VALUE,
       cd2.NAME,
       cd2.TYPE,
       cd2.TYPE_NAME,
       cd2.REMARK
FROM core_user cu
       left join core_dict cd on cd.VALUE = cu.JOB_TYPE0
       left join core_dict cd2 on cd2.VALUE = cu.JOB_TYPE1
where cu.ID = 1;

-- 获取id为1 的用户的所属组织
SELECT cur.ORG_ID
FROM core_user_role cur
WHERE cur.USER_ID = 1;

-- 获取id 为1 的用户的角色，需要通过org进行过滤
SELECT ROLE_ID
FROM core_user_role
WHERE USER_ID = 1
  AND ORG_ID = 1;

-- 获取所有路由表（路由表不单单包含菜单，还包括任意的请求路由）
-- 建立一个以function为中心的权限体系
select menu.id,
       menu.PARENT_MENU_ID      PARENT_ID,
       menu.NAME                title,
       menu.CODE                name,
       menu.ICON,
       ifnull(menu.SEQ, 999999) seq,
       func.ACCESS_URL          path,
       role_menu.ROLE_ID
from core_menu menu
       left join core_function func on func.ID = menu.FUNCTION_ID
       left join core_role_menu role_menu on role_menu.MENU_ID = menu.id;


-- 分为系统，导航，菜单。系统是顶部菜单，导航就是父菜单，菜单是导航的子菜单
select cm.*, cd.NAME, cd.TYPE_NAME
from core_menu cm
       join core_dict cd on cd.VALUE = cm.TYPE;

SELECT
  *
FROM core_dict CD;


-- ---------------------------菜单数据修改----------------------------
USE starter;

INSERT INTO core_function (ID, CODE, NAME, ACCESS_URL, PARENT_ID, TYPE, CREATE_TIME)
  VALUES (22, 'permission', 'Permission', '/permission', 0, 'FN0', 1519868556)
  , (21, 'PagePermission', 'PagePermission', '/permission', 0, 'FN0', 1519868556)
  , (22, 'DirectivePermission', 'DirectivePermission', '/permission', 0, 'FN0', 1519868556)
  , (23, 'RolePermission', 'RolePermission', '/permission', 0, 'FN0', 1519868556)

  , (24, 'Icon', 'Icon', '/permission', 0, 'FN0', 1519868556)
  , (25, 'Icons', 'Icons', '/permission', 0, 'FN0', 1519868556)

  , (26, 'ComponentDemo', 'ComponentDemo', '/permission', 0, 'FN0', 1519868556)
  , (27, 'TinymceDemo', 'TinymceDemo', '/permission', 0, 'FN0', 1519868556)
  , (28, 'MarkdownDemo', 'MarkdownDemo', '/permission', 0, 'FN0', 1519868556)
  , (29, 'JsonEditorDemo', 'JsonEditorDemo', '/permission', 0, 'FN0', 1519868556)
  , (30, 'SplitpaneDemo', 'SplitpaneDemo', '/permission', 0, 'FN0', 1519868556)
  , (31, 'AvatarUploadDemo', 'AvatarUploadDemo', '/permission', 0, 'FN0', 1519868556)
  , (32, 'DropzoneDemo', 'DropzoneDemo', '/permission', 0, 'FN0', 1519868556)
  , (33, 'StickyDemo', 'StickyDemo', '/permission', 0, 'FN0', 1519868556)
  , (34, 'CountToDemo', 'CountToDemo', '/permission', 0, 'FN0', 1519868556)
  , (35, 'ComponentMixinDemo', 'ComponentMixinDemo', '/permission', 0, 'FN0', 1519868556)
  , (36, 'BackToTopDemo', 'BackToTopDemo', '/permission', 0, 'FN0', 1519868556)
  , (37, 'DragDialogDemo', 'DragDialogDemo', '/permission', 0, 'FN0', 1519868556)
  , (38, 'DragSelectDemo', 'DragSelectDemo', '/permission', 0, 'FN0', 1519868556)
  , (39, 'DndListDemo', 'DndListDemo', '/permission', 0, 'FN0', 1519868556)
  , (40, 'DragKanbanDemo', 'DragKanbanDemo', '/permission', 0, 'FN0', 1519868556)

  , (41, 'Charts', 'Charts', '/permission', 0, 'FN0', 1519868556)
  , (42, 'KeyboardChart', 'KeyboardChart', '/permission', 0, 'FN0', 1519868556)
  , (43, 'LineChart', 'LineChart', '/permission', 0, 'FN0', 1519868556)
  , (44, 'MixChart', 'MixChart', '/permission', 0, 'FN0', 1519868556)

  , (45, 'Nested', 'Nested', '/permission', 0, 'FN0', 1519868556)
  , (46, 'Menu1', 'Menu1', '/permission', 0, 'FN0', 1519868556)
  , (47, 'Menu1-1', 'Menu1-1', '/permission', 0, 'FN0', 1519868556)
  , (48, 'Menu1-2', 'Menu1-2', '/permission', 0, 'FN0', 1519868556)
  , (49, 'Menu1-2-1', 'Menu1-2-1', '/permission', 0, 'FN0', 1519868556)
  , (50, 'Menu1-2-2', 'Menu1-2-2', '/permission', 0, 'FN0', 1519868556)
  , (51, 'Menu1-3', 'Menu1-3', '/permission', 0, 'FN0', 1519868556)
  , (52, 'Menu2', 'Menu2', '/permission', 0, 'FN0', 1519868556)

  , (53, 'Table', 'Table', '/permission', 0, 'FN0', 1519868556)
  , (54, 'DynamicTable', 'DynamicTable', '/permission', 0, 'FN0', 1519868556)
  , (55, 'DragTable', 'DragTable', '/permission', 0, 'FN0', 1519868556)
  , (56, 'InlineEditTable', 'InlineEditTable', '/permission', 0, 'FN0', 1519868556)
  , (57, 'ComplexTable', 'ComplexTable', '/permission', 0, 'FN0', 1519868556)

  , (58, 'Example', 'Example', '/permission', 0, 'FN0', 1519868556)
  , (59, 'CreateArticle', 'CreateArticle', '/permission', 0, 'FN0', 1519868556)
  , (60, 'EditArticle', 'EditArticle', '/permission', 0, 'FN0', 1519868556)
  , (61, 'ArticleList', 'ArticleList', '/permission', 0, 'FN0', 1519868556)

  , (62, 'Tab', 'Tab', '/permission', 0, 'FN0', 1519868556)
  , (63, 'Tabs', 'Tabs', '/permission', 0, 'FN0', 1519868556)


  , (64, 'ErrorPages', 'ErrorPages', '/permission', 0, 'FN0', 1519868556)
  , (65, 'Page401', 'Page401', '/permission', 0, 'FN0', 1519868556)
  , (66, 'Page404', 'Page404', '/permission', 0, 'FN0', 1519868556)

  , (67, 'ErrorLog', 'ErrorLog', '/permission', 0, 'FN0', 1519868556)
  , (68, 'ErrorLogs', 'ErrorLogs', '/permission', 0, 'FN0', 1519868556)


  , (69, 'Excel', 'Excel', '/permission', 0, 'FN0', 1519868556)
  , (70, 'ExportExcel', 'ExportExcel', '/permission', 0, 'FN0', 1519868556)
  , (71, 'SelectExcel', 'SelectExcel', '/permission', 0, 'FN0', 1519868556)
  , (72, 'MergeHeader', 'MergeHeader', '/permission', 0, 'FN0', 1519868556)
  , (73, 'UploadExcel', 'UploadExcel', '/permission', 0, 'FN0', 1519868556)

  , (74, 'Zip', 'Zip', '/permission', 0, 'FN0', 1519868556)
  , (75, 'ExportZip', 'ExportZip', '/permission', 0, 'FN0', 1519868556)

  , (76, 'Pdf', 'Pdf', '/permission', 0, 'FN0', 1519868556)
  , (77, 'PDFS', 'PDFS', '/permission', 0, 'FN0', 1519868556)

  , (78, 'PdfDown', 'PdfDown', '/permission', 0, 'FN0', 1519868556)

  , (79, 'theme', 'theme', '/permission', 0, 'FN0', 1519868556)
  , (80, 'Themes', 'Themes', '/permission', 0, 'FN0', 1519868556)


  , (81, 'clipboard', 'clipboard', '/permission', 0, 'FN0', 1519868556)
  , (82, 'ClipboardDemo', 'ClipboardDemo', '/permission', 0, 'FN0', 1519868556)


  , (83, 'ExternalLink', 'ExternalLink', '/permission', 0, 'FN0', 1519868556)
  , (84, 'link', 'link', '/permission', 0, 'FN0', 1519868556);






