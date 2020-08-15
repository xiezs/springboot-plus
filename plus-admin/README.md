# 功能列表
### 基础管理
    用户管理 - 已完成
    组织机构管理 - 已完成
    角色管理 - 已完成
    菜单项 - 已完成
    功能点管理 - 已完成
    字典数据管理 - 已完成
    角色功能授权
    角色数据授权

### 代码生成
    未开始

# 核心用户设计

以集团为一个最大组织,可切换集团。

以功能点为核心，菜单和请求构成可分配功能。
数据权限以组织为基准进行访问限制

# 开发规范
- controller以ElController结尾
- 以El开始的注解是前端视图类注解
- 所有的字典类型都从数据库中加载进缓存中。字典值以三种方式访问：
> 1. 以Dict注解的String类型字段（其值为字典value），调用 `CoreBaseService.handleStrDictValueField` 方法处理bean。其字典名称存入TailBean的ext中。
> 2. 以Dict注解的DictType类型字段，调用 `CoreBaseService.handleDictTypeField` 方法处理bean。其对应字典(name,value,type)，存入该字段
> 3. 继承DictTypeEnum的枚举类型字典类（参考JobTypeEnum），已做SpringMVC的参数和Jackson序列化处理 