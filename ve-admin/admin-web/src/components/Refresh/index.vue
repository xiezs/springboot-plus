<!--
 * @Author: 一日看尽长安花
 * @since: 2020-06-07 14:41:55
 * @LastEditTime: 2020-06-07 14:45:23
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<script>
export default {
  name: 'Refresh',
  beforeRouteEnter(to, from, next) {
    // 在渲染该组件的对应路由被 confirm 前调用
    // 不！能！获取组件实例 `this`
    // 因为当守卫执行前，组件实例还没被创建
    next(vm => {
      vm.$router.replace(from.path);
      // 跳到该路由页面后，再替换为from.path来源路径
    });
  }
};
</script>
