<!--
 * @Author: 一日看尽长安花
 * @since: 2020-07-19 16:33:05
 * @LastEditTime: 2020-08-02 13:17:58
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <!-- vue实例外创建 -->
  <div class="sp-edit-form-popup">
    <el-dialog
      ref="editDialog"
      :title="title"
      :visible.sync="visible"
      :destroy-on-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      @open="initDialog"
    >
      <div class="sp-edit-form">
        <el-form
          ref="editPopupForm"
          :model="data_"
          :rules="rules"
          label-width="120px"
        >
          <el-form-item label="角色名称" required prop="name">
            <el-input
              v-model="data_.name"
              placeholder="请输入角色名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="角色代码" required prop="code">
            <el-input
              v-model="data_.code"
              placeholder="请输入角色代码"
            ></el-input>
          </el-form-item>
          <el-form-item label="角色类型" required prop="type">
            <el-select v-model="data_.type" placeholder="请选择角色类型">
              <el-option
                v-for="item in roleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="sp-edit-form-btn">
          <el-button @click="close">取 消</el-button>
          <el-button type="primary" @click="submit">
            确 定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import { saveRoleData, updateRoleData } from '@/api/admin_roles';

export default {
  name: 'DictEditPopup',
  components: {},
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '编辑'
    },
    data: {
      type: Object,
      default: function() {
        return {};
      }
    }
  },
  data() {
    return {
      data_: {},
      roleOptions: [
        {
          value: 'R0',
          label: '操作角色'
        },
        {
          value: 'R1',
          label: '工作流角色'
        }
      ],
      rules: {
        name: [{ required: true, message: '请输入角色名称' }],
        code: [{ required: true, message: '请输入角色代码' }],
        type: [{ required: true, message: '请选择角色类型' }]
      }
    };
  },
  methods: {
    initDialog() {
      this.data_ = this.data;
      if (this.data_.id) {
        this.title = '编辑';
      } else {
        this.title = '新增';
      }
    },
    submit() {
      this.$refs['editPopupForm'].validate(valid => {
        if (valid) {
          if (this.data_.id) {
            this.updateData();
          } else {
            this.addData();
          }
        } else {
          return false;
        }
      });
    },
    addData() {
      const _that = this;
      saveRoleData(this.data_).then(res => {
        const { code, message, data } = { ...res };
        // 通过中间路由刷新当前路由
        _that.$nextTick(() => {
          _that.$notify({
            title: '成功',
            message: '添加成功',
            type: 'success',
            duration: 2000,
            onClose: function() {
              _that.$router.replace('/refresh');
            }
          });
        });
      });
    },
    updateData() {
      const _that = this;
      updateRoleData(this.data_).then(res => {
        const { code, message, data } = { ...res };
        // 通过中间路由刷新当前路由
        _that.$nextTick(() => {
          _that.$notify({
            title: '成功',
            message: '修改成功',
            type: 'success',
            duration: 2000,
            onClose: function() {
              _that.$router.replace('/refresh');
            }
          });
        });
      });
    },
    close() {
      this.$emit('update:visible', false);
    }
  }
};
</script>
