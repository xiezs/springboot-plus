<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime: 2020-03-23 14:24:32
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <!--
      对话框：通过$emit 继续提交open和close事件改变对话框的显示和隐藏
    -->
  <el-dialog
    :fullscreen="true"
    :center="true"
    :destroy-on-close="false"
    :show-close="false"
    :title="dialogTitle"
    :visible="dialogVisible"
    :close-on-click-modal="false"
    @open="openDialog"
    @close="closeDialog"
  >
    <div class="dialog-container">
      <el-form
        ref="editFormGP"
        :rules="rules"
        :model="dialogData"
        label-position="right"
        label-width="10vw"
        class="sp-form"
      >
        <el-form-item
          v-for="(val, key) in visibleMetadata"
          :key="key"
          :label="val.name"
          :prop="key"
        >
          <el-input
            v-if="judgeType(val.type, 'string')"
            v-model="dialogData[key]"
            :placeholder="val.name"
            :clearable="true"
            class="sp-form-item"
          />

          <el-date-picker
            v-else-if="judgeType(val.type, 'date')"
            v-model="dialogData[key]"
            type="datetime"
            value-format="timestamp"
            :placeholder="val.name + '时间'"
            class="sp-form-item"
          >
          </el-date-picker>
        </el-form-item>
        <!-- 用于面板中的自定义表单元素，例如级联选择器，并通过作用域插槽的方式将数据传递给自定义表单 -->
        <slot :dialog-data="dialogData" name="dialog-form-item"></slot>
      </el-form>
    </div>
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
  computed: {
    // 计算属性的 getter
    visibleMetadata: function() {
      // `this` 指向 vm 实例
      let _metadata = {};
      for (let dict in this.metadata) {
        const t = this.metadata[dict];
        if (t.is_show_editor_panel) {
          _metadata[dict] = t;
        }
      }
      return _metadata;
    }
  },
  methods: {
    judgeType(str1, type) {
      return equalsIgnoreCase(str1, type);
    },
    createData() {
      this.$refs['editFormGP'].validate(valid => {
        if (valid) {
          this.$emit('create-data', this.dialogData);
          this.$emit('update:dialogVisible', false);
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
      this.$refs['editFormGP'].validate(valid => {
        if (valid) {
          this.$emit('update-data', this.dialogData);
          this.$emit('update:dialogVisible', false);
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
<style scope>
.sp-form {
  width: 50vw;
  margin: 0 auto;
}
.sp-form-item {
  width: 100%;
}
.el-date-editor.sp-form-item {
  width: 100%;
}
</style>
