<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime : 2020-01-09 23:16:28
 * @LastEditors  : 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="filter-container">
    <!-- 取消表单的提交动作 -->
    <el-form
      ref="filterForm"
      :size="size"
      :model="filterData"
      @submit.native.prevent
    >
      <!-- 循环元数据构建搜索面板：除了下拉框 -->
      <div
        v-for="(val, key) in metadata"
        :key="key"
        class="filter-item-container"
      >
        <el-form-item>
          <el-input
            v-if="judgeType(val.type, 'string')"
            v-model="filterData[key]"
            :placeholder="val.name"
            :clearable="true"
            style="width: 200px;"
            class="filter-item"
          />
          <div
            v-else-if="judgeType(val.type, 'date')"
            style="display: inline-block;position: relative;top: -0.3rem;"
          >
            <el-date-picker
              v-model="filterData[key + 'Start']"
              type="datetime"
              :placeholder="val.name + '开始时间'"
            >
            </el-date-picker>

            <el-date-picker
              v-model="filterData[key + 'End']"
              type="datetime"
              :placeholder="val.name + '结束时间'"
            >
            </el-date-picker>
          </div>
        </el-form-item>
      </div>
      <!-- 用于面板中的自定义表单，例如级联选择器，并通过作用域插槽的方式将数据传递给自定义表单 -->
      <slot name="filter-condition" :filter-data="filterData"> </slot>
      <div class="filter-item-container">
        <el-button
          :size="size"
          class="filter-item"
          type="primary"
          icon="el-icon-search"
          @click="filterSearch"
        >
          搜索
        </el-button>
      </div>
    </el-form>

    <div class="filter-btn-group">
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        :size="size"
        @click="handleCreate"
      >
        添加
      </el-button>
      <!-- 用于面板中的自定义功能按钮，例如导入导出按钮等，并通过作用域插槽的方式将数据传递给自定义表单 -->
      <slot name="operation-btn-group" :filter-data="filterData"> </slot>
    </div>
  </div>
</template>

<script>
import { equalsIgnoreCase } from '@/utils/str-util';

export default {
  name: 'SearchPane',
  props: {
    metadata: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  data() {
    return {
      size: 'mini',
      filterData: {}
    };
  },
  methods: {
    judgeType(str1, type) {
      return equalsIgnoreCase(str1, type);
    },
    filterSearch(filterData) {
      this.$refs['filterForm'].validate(valid => {
        if (valid) {
          this.$emit('filter-search', this.filterData);
        } else {
          this.$notify({
            title: 'Faild',
            message: 'Search Failded!',
            type: 'error',
            duration: 2000
          });
        }
      });
    },
    handleCreate() {
      this.$emit('handle-create');
    }
  }
};
</script>
