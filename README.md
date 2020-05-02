# springboot-plus 基于vue-element-admin 改版 


### 开发说明

1. 技术栈说明
2. 前端配置架构说明
3. 后端配置架构说明
4. 自定义开发的<General-Page> 组件说明

#### 技术栈说明
> 主要前端技术：

1. Vue+Vuex+Vue Router 2.6 响应式js编程库
2. Element-UI 2.13 基于vue的饿了么UI组件
3. Axios 0.18.1 基于Promise异步编程的请求库
4. lodash 4.17.15 js工具库
5. tinyMCE 未来会使用到的富文本编辑器js库
6. qs 6.9.1 object转请求参数编码库
7. mockjs 1.0.1-beta3 前端接口mock数据库

> 主要后端技术（不列版本，保持更新最新版本号）：

1. springboot
2. beetl/beetlsql  dao框架
3. hutool 工具库
4. caffeine JVM缓存库
5. hikaricp 连接池
6. undertow 听说比tomcat好的服务器
7. hibernate-validator 参数校验
8. log4j2 日志框架
9. jose4j jwt库
10. jxls2 模板化excel导入导出库

#### 前端配置架构说明

&emsp; 前端项目使用了PanJia大神的vue-element-admin项目，若前端有bug，可以先到vue-element-admin项目查看。
```
主要目录：
src
    api：数据请求接口
    component：自定义公共vue组件
    router：静态路由表
    services：业务js代码
    view：功能页面
    utils：自定义工具库，建议优先使用lodash
```
约定：
> 前端代码中的object的key永远用下划线，在axios的拦截器（代码在src/util/request.js中）中复制一份转为驼峰式。后端永远返回下划线的object，已通过Jackson配置处理。
> 在axios中每次请求都将jwt的token携带了，后端保持五分钟内有效，超过五分钟没有任何请求。token失效，需要重新登录。
> 每个组件使用必须添加key属性，key不同，复用组件的生命周期才不会共用。简单使用Math.random()赋值。实际使用可以考虑实现snowflake等id赋值。

&emsp; 主要的配置：
   ** 动态路由：**
    `警告`，此功能的vue  bug让人有点懵，建议等我重新测试。凡是前端需要跳转链接页面的，都属于动态路由部分，目前使用动态路由需要完成数据库数据插入，不显示在菜单的路由也挂在到同Menu级别下；同时在前端代码中也需要填入静态路由表（在src/router/maps下的对应文件中）。动态路由代码的文件观看顺序：src/permission.js, src/store/modules/permission.js。
    原理：后台数据库存放路由/菜单，根据登录用户权限，查出路由树。此时路由树没有component属性，前端同时存放所有路由树，有component属性，在遍历后端传递的路由树在前端静态路由树中将component拼接到后端传递的路由树中。以此实现动态路由.。`注：这可能是vue的bug导致，经由另一个网友项目测试，vue官网的路由懒加载是可行的，但是在我项目中不可行。`
    **文件上传：**
    在 src/components/Upload/FileUpload.vue 中封装了element-ui的文件上传组件，使用axios实现的上传逻辑。可以直接使用该组件。
    **级联选择器：**
    封装elementUI的el-cascader，因为原本的级联器的选中值是一个数组形式的，这样还得处理选中值变成请求参数中的key/value形式，这样很麻烦，所以重新封装了一下。代码在src/components/Wrapper/Sp-cascader。使用可以参考views/user/add-user-role.vue
    **通用页面组件<General-Page>：**
    代码在src/components/GeneralPage目录。目的是为了后期代码生成，只生成后端代码，不用生成复杂的前端代码。可以主要参考view/user的实现。包括头部区域条件搜索，中间数据表格和分页，增删改查四个按钮。增加/修改的弹出编辑弹窗。预留了功能按钮组插槽、搜索条件插槽、编辑弹窗插槽，用于扩展自定义的功能和表单。


#### 后端配置架构说明
```
 admin-core ：基本entity、dao、service，主要配置，Platform service等核心功能
 admin-console：后台管理代码
```
**主要配置：**
    请求和响应的Jackson配置，驼峰式转下划线
    字典字段使用枚举，实现DictTypeEnum接口，参照StateEnum
    beetlsql主要配置：DictTypeEnum字段与数据库字段的类型转换；简单关联结果集映射，代码参照conf/beetl/JsonBeanProcessor。使用参照coreFunction.md。
    自定义注解 RequestBodyPlus，实现用jsonPath直接从json参数中提取值注入controller参数，减少不必要的实体类接收json参数。
    后台Jose4j实现jwt，代码在JoseJwtUtil。
    代码风格使用intellij-java-google-style.xml，idea可直接在code style中导入。

**约定**
    在旧版和新版的代码未完成更替完成时，controller和Service层类命名以ElController和ElService结尾。
