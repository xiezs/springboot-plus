<!--
 * @Author: 一日看尽长安花
 * @since: 2020-04-27 09:46:14
 * @LastEditTime: 2020-04-27 18:27:34
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template id="changePassword">
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
          status-icon
          :rules="rules"
        >
          <el-form-item label="用户名">
            <el-input v-model="dialogData_['name']" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="用户账号">
            <el-input v-model="dialogData_['code']" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="dialogData_['password']"
              type="password"
            ></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm_password">
            <el-input
              v-model="dialogData_['confirm_password']"
              type="password"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <template v-slot:footer>
        <div class="sp-dialog-footer">
          <el-button @click="closeDialog">
            取消
          </el-button>
          <el-button type="primary" @click="changePassword">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { changePassword } from '@/api/user';
export default {
  name: 'ChangePassword',
  components: {},
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
    }
  },
  data() {
    var passwordValidator = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.dialogData_.confirm_password !== '') {
          this.$refs.editForm.validateField('confirm_password');
        }
        callback();
      }
    };
    var confirmPasswordValidator = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.dialogData_.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      dialogData_: { ...this.dialogData },
      rules: {
        password: [{ validator: passwordValidator, trigger: 'blur' }],
        confirm_password: [
          { validator: confirmPasswordValidator, trigger: 'blur' }
        ]
      }
    };
  },
  methods: {
    init() {
      this.$emit('update:visible', false);
    },
    closeDialog() {
      this.init();
    },
    changePassword() {
      debugger;
      const Vue = this;
      let params = {
        id: Vue.dialogData_.id,
        password: Vue.dialogData_.password
      };
      changePassword(params).then(response => {
        const { code, message, data } = { ...response };
        Vue.$nextTick(() => {
          Vue.$notify({
            title: '成功',
            message: '修改密码成功',
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
