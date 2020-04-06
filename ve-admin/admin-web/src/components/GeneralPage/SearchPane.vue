<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime: 2020-03-31 16:31:49
 * @LastEditors: 一日看尽长安花
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
        v-for="(val, key) in visibleMetadata"
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
              v-model="filterData[key + '_start']"
              type="datetime"
              value-format="timestamp"
              :placeholder="val.name + '开始时间'"
            >
            </el-date-picker>

            <el-date-picker
              v-model="filterData[key + '_end']"
              type="datetime"
              value-format="timestamp"
              :placeholder="val.name + '结束时间'"
            >
            </el-date-picker>
          </div>
        </el-form-item>
      </div>
      <!-- 用于面板中的自定义表单，例如级联选择器，并通过作用域插槽的方式将数据传递给自定义表单 -->
      <slot name="filter-condition" :filter-data="filterData"> </slot>
      <div class="sp-search-btn-container">
        <el-button
          ref="searchButton"
          :size="size"
          class="sp-search-btn-item"
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
  computed: {
    // 计算属性的 getter
    visibleMetadata: function() {
      // `this` 指向 vm 实例
      let _metadata = {};
      let allowTypes = ['string', 'date'];
      for (let dict in this.metadata) {
        const t = this.metadata[dict];
        if (t.is_show_search_panel && allowTypes.includes(t.type)) {
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
