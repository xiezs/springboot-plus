<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime: 2019-12-17 14:41:21
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="dialog-container">
    <!--
      对话框：通过$emit 继续提交open和close事件改变对话框的显示和隐藏
    -->
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
          v-for="(val, key) in metadata"
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
        </el-form-item>
        <!-- 用于面板中的自定义表单元素，例如级联选择器，并通过作用域插槽的方式将数据传递给自定义表单 -->
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
    metadata: {
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
          /* 将回调延迟到下次 DOM 更新循环之后执行。
          而数据更新就代表dom更新，所以如果创建成功，数据就会更新 */
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
      /* 参照vue中 .sync 修饰符章节，方便的刷新父组件的dialogVisible值*/
      this.$emit('update:dialogVisible', true);
    },
    closeDialog() {
      this.$emit('update:dialogVisible', false);
    }
  }
};
</script>
<style></style>
