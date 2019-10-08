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
-- todo 需要重写，因为没有包括父菜单
SELECT router.id,
       router.PARENT_ID,
       IFNULL(router.ACCESS_URL, '/error/404') path,
       router.NAME,
       menu.NAME                               title,
       menu.ICON,
       IFNULL(menu.SEQ, -9999)                 seq,
       (select from core_role_menu crm where crm.
FROM core_function router
         LEFT JOIN core_menu menu
                   ON menu.FUNCTION_ID = router.ID;

select router.id,
       router.PARENT_ID,
       IFNULL(router.ACCESS_URL, '/error/404') path,
       router.NAME,
       menu.NAME                               title,
       menu.ICON,
       IFNULL(menu.SEQ, -9999)                 seq,
       from core_menu cm left join core_function cf on cf.id=cm.function_id

union

select router.id,
       router.PARENT_ID,
       IFNULL(router.ACCESS_URL, '/error/404') path,
       router.NAME,
       menu.NAME                               title,
       menu.ICON,
       IFNULL(menu.SEQ, -9999)                 seq,
       from core_menu cm right join core_function cf on cf.id=cm.function_id;

-- 分为系统，导航，菜单。系统是顶部菜单，导航就是父菜单，菜单是导航的子菜单
select cm.*,
       cd.NAME,
       cd.TYPE_NAME
from core_menu cm
         join core_dict cd on cd.VALUE = cm.TYPE;


