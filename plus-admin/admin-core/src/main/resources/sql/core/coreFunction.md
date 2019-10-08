
getAllRoutes
===

```sql

select menu.id,
       menu.PARENT_MENU_ID      PARENT_ID,
       menu.NAME                title,
       menu.ICON,
       ifnull(menu.SEQ, 999999) seq,
       route.ACCESS_URL          path,
       route.NAME,
       role_menu.ROLE_ID
from core_menu menu
       left join core_function route on route.ID = menu.FUNCTION_ID
       left join core_role_menu role_menu on role_menu.MENU_ID = menu.id
where menu.TYPE!='MENU_S'

```
@ mapping("RouteMapping");

RouteMapping
===
```javascript
    var route_mapping_var={
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
```



