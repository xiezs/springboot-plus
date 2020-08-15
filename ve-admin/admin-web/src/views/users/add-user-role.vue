<!--
 * @Author: 一日看尽长安花
 * @since: 2020-04-05 13:21:44
 * @LastEditTime: 2020-04-27 14:59:57
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="sp-edit-dialog">
    <el-dialog
      :fullscreen="true"
      :center="true"
      :destroy-on-close="true"
      :show-close="false"
      :title="title"
      :visible="visible"
      :close-on-click-modal="false"
      @close="closeDialog"
    >
      <div class="sp-dialog-container">
        <el-form
          ref="editForm"
          :model="dialogData_"
          label-position="right"
          label-width="10vw"
          class="sp-form"
        >
          <el-form-item label="用户名">
            <el-input v-model="dialogData_['name']" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="用户账号">
            <el-input v-model="dialogData_['code']" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="选择部门">
            <sp-cascader
              :key="Math.random()"
              v-model="dialogData_"
              :json-paths="['org_id']"
              :labels="['org_id']"
              :props="orgIdCascaderProps"
              :options="orgIdCascaderProps.options"
              placeholder="选择部门"
            ></sp-cascader>
          </el-form-item>
          <el-form-item label="选择角色">
            <sp-cascader
              :key="Math.random()"
              v-model="dialogData_"
              :json-paths="['role_id']"
              :labels="['role_id']"
              :props="roleIdCascaderProps"
              :options="roleIdCascaderProps.options"
              placeholder="选择角色"
            ></sp-cascader>
          </el-form-item>
        </el-form>
      </div>
      <template v-slot:footer>
        <div class="sp-dialog-footer">
          <el-button @click="closeDialog">
            取消
          </el-button>
          <el-button type="primary" @click="createUserRole">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import SpCascader from '@/components/Wrapper/SpCascader';
import { addUserRoles } from '@/api/admin_users';
import { handleCascaderValue } from '@/services/dict';
export default {
  name: 'AddUserRole',
  components: { SpCascader },
  props: {
    dialogData: {
      type: Object,
      default: function() {
        return {};
      }
    },
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '添加角色'
    },
    orgIdCascaderProps: {
      type: Object,
      default: function() {
        return {};
      }
    },
    roleIdCascaderProps: {
      type: Object,
      default: function() {
        return {};
      }
    }
  },
  data() {
    return {
      dialogData_: { ...this.dialogData }
    };
  },
  methods: {
    init() {
      this.$emit('update:visible', false);
    },
    closeDialog() {
      this.init();
    },
    createUserRole() {
      const Vue = this;
      handleCascaderValue(Vue.dialogData_, ['org_id', 'role_id']);
      let params = {
        user_id: Vue.dialogData_.id,
        name: Vue.dialogData_.name,
        code: Vue.dialogData_.code,
        org_id: Vue.dialogData_.org_id,
        role_id: Vue.dialogData_.role_id
      };
      addUserRoles(params).then(response => {
        const { code, message, data } = { ...response };
        Vue.$nextTick(() => {
          Vue.$notify({
            title: '成功',
            message: '授权角色成功',
            type: 'success',
            duration: 2000
          });
          Vue.init();
        });
      });
    }
  }
};
</script>
<style scoped>
.sp-dialog-container {
  margin: 0 auto;
  width: 50%;
}
.el-cascader {
  width: 100%;
}
</style>
