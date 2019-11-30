<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime: 2019-10-17 17:41:15
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="dialog-container">
    <!-- 对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible="dialogVisible"
      :close-on-click-modal="false"
      @open="openDialog"
      @close="closeDialog"
    >
      <el-form
        ref="editForm"
        :rules="rules"
        :model="dialogData"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item
          v-for="(val, key) in metedata"
          :key="key"
          :label="val.name"
          :prop="key"
        >
          <el-input
            v-if="judgeType(val.type, 'string')"
            v-model="dialogData[key]"
            :placeholder="val.name"
            :clearable="true"
            style="width: 200px;"
            class="filter-item"
          />

          <el-date-picker
            v-else-if="judgeType(val.type, 'date')"
            v-model="dialogData[key]"
            type="datetime"
            :placeholder="val.name + '时间'"
          >
          </el-date-picker>

          <el-select
            v-else-if="judgeType(val.type, 'dict')"
            :placeholder="val.name"
            :clearable="true"
            class="filter-item"
          >
            <el-option />
          </el-select>
        </el-form-item>
        <!-- 给某些无法自动生成表单域的插槽，并将数据向插槽传递 -->
        <slot :dialog-data="dialogData" name="dialog-form-item"></slot>
      </el-form>
      <template v-slot:footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog">
            取消
          </el-button>
          <el-button
            type="primary"
            @click="operationType === 'create' ? createData() : updateData()"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { equalsIgnoreCase } from '@/utils/str-util';

export default {
  name: 'EditDialog',
  props: {
    metedata: {
      type: Object,
      default() {
        return {};
      }
    },
    dialogData: {
      type: Object,
      default() {
        return {};
      }
    },
    dialogTitle: {
      type: String,
      default: '创建'
    },
    rules: {
      type: Object,
      default() {
        return {};
      }
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    operationType: {
      type: String,
      default: 'create'
    }
  },
  data() {
    return {};
  },
  methods: {
    judgeType(str1, type) {
      return equalsIgnoreCase(str1, type);
    },
    createData() {
      this.$refs['editForm'].validate(valid => {
        if (valid) {
          this.$emit('create-data', this.dialogData);
          this.$nextTick(() => {
            this.$emit('update:dialogVisible', false);
            this.$notify({
              title: '成功',
              message: '添加成功',
              type: 'success',
              duration: 2000
            });
          });
        } else {
          this.$notify({
            title: '失败',
            message: '数据无法通过校验!',
            type: 'error',
            duration: 2000
          });
        }
      });
    },
    updateData() {
      this.$refs['editForm'].validate(valid => {
        if (valid) {
          this.$emit('update-data', this.dialogData);
          this.$nextTick(() => {
            this.$emit('update:dialogVisible', false);
            this.$notify({
              title: '成功',
              message: '修改成功',
              type: 'success',
              duration: 2000
            });
          });
        } else {
          this.$notify({
            title: '失败',
            message: '数据无法通过校验!',
            type: 'error',
            duration: 2000
          });
        }
      });
    },
    openDialog() {
      this.$emit('update:dialogVisible', true);
    },
    closeDialog() {
      this.$emit('update:dialogVisible', false);
    }
  }
};
</script>
<style scoped></style>
