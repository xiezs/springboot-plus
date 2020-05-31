getAllRoutes
===

```sql

select menu.id,
       menu.PARENT_MENU_ID      parent_id,
       menu.NAME                title,
       menu.CODE                name,
       menu.ICON,
       ifnull(menu.SEQ, 999999) seq,
       func.ACCESS_URL          path,
       func.COMPONENT           component,
       role_menu.ROLE_ID
from core_menu menu
       left join core_function func on func.ID = menu.FUNCTION_ID and menu.DEL_FLAG=0
       left join core_role_menu role_menu on role_menu.MENU_ID = menu.id
       
```
@ mapping("RouteMapping");

RouteMapping
===
```javascript
    var route_mapping_var={
                      "id": "core_route_map",
                      "mapping": {
                          "resultType": "com.ibeetl.admin.core.entity.CoreRoute",
                          "path": "path",
                          "name": "name",
                          "component": "component",
                          "id": "id",
                          "parentId": "parent_id",
                          "seq": "seq",
                          "meta": {
                              "resultType": "com.ibeetl.admin.core.entity.CoreRouteMeta",
                              "icon": "icon",
                              "title": "title",
                              "roles": [
                                  {
                                      "id": "role_id"
                                  }
                              ]
                          }
                      }
                  };
```



