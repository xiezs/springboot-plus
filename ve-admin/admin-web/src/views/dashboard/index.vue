<template>
  <div class="dashboard-container">
    <component :is="currentRole" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex';
import adminDashboard from './admin';
import editorDashboard from './editor';

export default {
  name: 'Dashboard',
  components: { adminDashboard, editorDashboard },
  data() {
    return {
      currentRole: 'adminDashboard',
    };
  },
  computed: {
    ...mapGetters(['roles']),
  },
  created() {
    // 默认任何一个账号进入都可以看仪表盘页面
    // if (!this.roles.includes(1)) {
    //   this.currentRole = 'editorDashboard';
    // }
  },
};
</script>
