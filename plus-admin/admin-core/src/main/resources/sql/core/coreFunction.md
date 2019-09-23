getAllRoutes
===

select router.id,
       router.PARENT_ID,
       ifnull(router.ACCESS_URL, '/error/404') path,
       router.NAME,
       menu.NAME                               title,
       menu.ICON,
       ifnull(menu.SEQ, 999999)                seq,
       crm.ROLE_ID
from core_function router
       left join core_menu menu on menu.FUNCTION_ID = router.ID
       left join core_role_menu crm on crm.MENU_ID = menu.id
order by router.ID
