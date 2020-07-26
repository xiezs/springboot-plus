<!--
 * @Author: 一日看尽长安花
 * @since: 2020-07-19 16:33:05
 * @LastEditTime: 2020-07-26 14:43:29
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
          <el-form-item label="字典名称" required prop="name">
            <el-input
              v-model="data_.name"
              placeholder="请输入字典名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="字典值" required prop="value">
            <el-input
              v-model="data_.value"
              placeholder="请输入字典值"
            ></el-input>
          </el-form-item>
          <el-form-item label="字典类型名称" required prop="type_name">
            <el-input
              v-model="data_.type_name"
              placeholder="请输入字典类型名称"
            ></el-input>
          </el-form-item>
          <el-form-item label="字典类型" required prop="type">
            <el-input
              v-model="data_.type"
              placeholder="请输入字典类型"
            ></el-input>
          </el-form-item>
          <el-form-item label="上级字典" prop="parent">
            <el-input
              v-model.number="data_.parent"
              placeholder="请输入上级字典id"
              type="number"
            ></el-input>
          </el-form-item>

          <el-form-item label="序号" prop="sort">
            <el-input
              v-model.number="data_.sort"
              placeholder="请输入序号"
              type="number"
            ></el-input>
          </el-form-item>
          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="data_.remark"
              placeholder="请输入备注"
            ></el-input>
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
import { saveDictData, updateDictData } from '@/api/dicts';

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
      rules: {
        name: [{ required: true, message: '请输入字典名称' }],
        value: [{ required: true, message: '请输入字典值' }],
        type: [{ required: true, message: '请输入字典类型名称' }],
        type_name: [{ required: true, message: '请输入字典类型' }],
        sort: [{ type: 'number', message: '序号必须是数字' }]
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
      saveDictData(this.data_).then(res => {
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
      updateDictData(this.data_).then(res => {
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
