
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
--:
var route_mapping={
                      "mapping": {
                          "path": "path",
                          "meta": {
                              "roles": [
                                  {
                                      "id": "role_id"
                                  }
                              ],
                              "icon": "icon",
                              "title": "title",
                              "resultType": "com.ibeetl.admin.core.entity.CoreRouteMeta"
                          },
                          "name": "name",
                          "id": "id",
                          "resultType": "com.ibeetl.admin.core.entity.CoreRoute",
                          "parentId": "parent_id",
                          "seq": "seq"
                      },
                      "id": "core_route_map"
                  };
mapping(route_mapping);
-- 

