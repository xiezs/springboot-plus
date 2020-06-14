
queryByCondtion
===============
* 根据条件查询

	select 
	@pageTag(){
	   m.*,f.NAME function_name,f.ACCESS_URL ,
	   p.name parent_menu_name
	@}
	from core_menu m left join core_function f on m.FUNCTION_ID=f.id  left join core_menu p on m.parent_menu_id = p.id
	where 1=1
	@if(!isEmpty(url)){
	    and  f.access_url like #'%'+url+"%"#
	@}
	
	@if(!isEmpty(code)){
	    and  m.code like #'%'+code+"%"#
	@}
	
	@if(!isEmpty(name)){
	    and  m.name like #'%'+name+"%"#
	@}
	
	@if(!isEmpty(parentMenuId)){
	    and  m.parent_menu_id = #parentMenuId#
	@}
	
	@pageIgnoreTag(){
	   order by m.id
	@}
	
selectMenuAndRelationFunction
=======
* 查询菜单和其关联的功能点

```sql
select 
    cm.id , cm.name , cm.code , cm.function_id , cm.icon , cm.parent_menu_id , 
    cm.seq , cm.type , cf.id func_id , cf.access_url func_access_url , cf.name func_name , 
    cf.type func_type
from core_menu cm
    join core_function cf on
        cf.id = cm.function_id
        and cm.del_flag = 0
        and cf.del_flag = 0
```
@ mapping("MenuFunctionMapping");


MenuFunctionMapping
===
* 菜单功能点结果集映射
```javascript
    var menu_func_mapping={
                      "id": "menu_func_map",
                      "mapping": {
                          "resultType": "com.ibeetl.admin.core.entity.CoreMenu",
                          "id": "id",
                          "name": "name",
                          "code": "code",
                          "functionId": "function_id",
                          "parentMenuId": "parent_menu_id",
                          "icon": "icon",
                          "seq": "seq",
                          "type": "type",
                          "relationFunction": {
                              "resultType": "com.ibeetl.admin.core.entity.CoreFunction",
                              "id": "func_id",
                              "name": "func_name",
                              "accessUrl": "func_access_url",
                              "type": "func_type"
                          }
                      }
                  };
```