<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2020-03-31 16:55:00
 * @LastEditors: 一日看尽长安花
 * @Description: 后台管理页面的自动生成，
 * 主要暴露了分页方法、数据表格搜索方法、条件查询方法、增删改方法
 * 以及 查询面板插槽，功能按钮组插槽，编辑对话框插槽
 -->
<template>
  <div>
    <search-pane
      ref="searchPaneGP"
      :metadata="metadata"
      @filter-search="filterSearch"
      @handle-create="handleCreate"
    >
      <template #filter-condition="{filterData:filterData}">
        <slot name="filter-condition" :filter-data="filterData"> </slot>
      </template>
      <template #operation-btn-group>
        <slot name="operation-btn-group"> </slot>
      </template>
    </search-pane>
    <data-table
      ref="dataTableGP"
      :loading="loading"
      :metadata="metadata"
      :tabledata="tabledata"
      :search-method="searchMethod"
      @update:tabledata="$emit('update:tabledata', $event)"
      @pagination="pagination"
      @handle-edit="handleEdit"
      @delete-data="deleteData"
    ></data-table>
    <detail-page
      ref="detailPageGP"
      :metadata="metadata"
      :dialog-visible.sync="dialogVisible"
      :dialog-title="dialogTitle"
      :operation-type="operationType"
      :dialog-data="dialogData"
      @create-data="$emit('create-data', $event)"
      @update-data="$emit('update-data', $event)"
    >
      <!-- #dialog-form-item等价v-slot:dialog-form-item 语法
      详情参照 解构插槽 Prop 章节 -->
      <template #dialog-form-item="{dialogData:dialogData}">
        <slot :dialog-data="dialogData" name="dialog-form-item"></slot>
      </template>
    </detail-page>
  </div>
</template>

<script>
import SearchPane from './SearchPane';
import DataTable from './DataTable';
import DetailPage from './components/DetailPage';

export default {
  name: 'GeneralPage',
  components: { SearchPane, DataTable, DetailPage },
  props: {
    // 表格元数据
    metadata: {
      type: Object,
      default() {
        return {};
      }
    },
    // 表格数据
    tabledata: {
      type: Object,
      default() {
        return {
          data: [],
          total: 0
        };
      }
    },
    // 加载中的遮罩层
    loading: {
      type: Boolean,
      default: true
    },
    searchMethod: {
      type: Function,
      default() {
        return this.table.data;
      }
    }
  },
  data() {
    return {
      // 查询参数：包括搜索和分页参数
      queryParams: {},
      dialogData: {},
      dialogVisible: false,
      // 对话框标题
      dialogTitle: '创建',
      // 对话框操作类型
      operationType: 'create'
    };
  },
  methods: {
    pagination(queryParams) {
      this.queryParams = Object.assign({}, queryParams);
      this.$emit('pagination', this.queryParams);
    },
    filterSearch(filterData) {
      this.queryParams = Object.assign(this.queryParams, filterData);
      this.$emit('filter-search', Object.assign({}, this.queryParams));
    },
    handleCreate() {
      this.operationType = 'create';
      this.dialogTitle = '创建';
      this.dialogVisible = true;
      this.dialogData = {};
    },
    handleEdit(index, row) {
      this.operationType = 'update';
      this.dialogTitle = '修改';
      this.dialogVisible = true;
      this.dialogData = Object.assign({}, row);
    },
    deleteData(index, row) {
      this.$emit('delete-data', index, row);
    }
  }
};
</script>
<style>
.filter-container {
  margin-top: 1rem;
}

.filter-item-container {
  display: inline-block;
  margin-left: 0.5em;
}

.filter-item-container .el-cascader {
  top: -4px;
}

.filter-btn-group {
  margin: 0.15em;
}
/* 搜索按钮的div容器 */
.sp-search-btn-container {
  margin-top: -1em;
}
.sp-search-btn-container .sp-search-btn-item {
  margin-left: 100em;
}
</style>
