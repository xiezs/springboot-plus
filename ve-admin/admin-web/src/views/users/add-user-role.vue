<!--
 * @Author: 一日看尽长安花
 * @since: 2020-04-05 13:21:44
 * @LastEditTime: 2020-04-06 11:52:20
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="sp-edit-dialog">
    <el-dialog
      :fullscreen="true"
      :center="true"
      :destroy-on-close="false"
      :show-close="false"
      :title="title"
      :visible="visible"
      :close-on-click-modal="false"
      @open="openDialog"
      @close="closeDialog"
    >
      <div class="dialog-container">
        <el-form
          ref="editFormGP"
          :model="dialogData"
          label-position="right"
          label-width="10vw"
          class="sp-form"
        >
          <el-form-item label="用户名">
            <el-input v-model="model"></el-input>
          </el-form-item>
          <el-form-item label="用户账号">
            <el-input v-model="model"></el-input>
          </el-form-item>
          <el-form-item label="选择部门">
            <el-cascader
              :options=""
              v-model=""
              @change="">
            </el-cascader>
          </el-form-item>
          <el-form-item label="选择角色">
            <el-cascader
              :options=""
              v-model=""
              @change="">
            </el-cascader>
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
export default {
  name: 'AddUserRole',
  props: {
    title: {
      type: String,
      default: '添加角色'
    }
  },
  data() {
    return {
      visible: false,
      dialogData: {}
    };
  },
  methods: {
    openDialog() {
      this.visible = true;
    },
    closeDialog() {
      this.visible = false;
    },
    createUserRole() {}
  }
};
</script>
<style scoped></style>
