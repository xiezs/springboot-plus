<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2019-11-04 21:00:21
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div>
    <search-pane
      :metedata="metedata"
      @filter-search="filterSearch"
      @handle-create="handleCreate"
    ></search-pane>
    <data-table
      :loading="loading"
      :metedata="metedata"
      :tabledata="tabledata"
      :search-method="searchMethod"
      @update:tabledata="$emit('update:tabledata', $event)"
      @pagination="pagination"
      @handle-edit="handleEdit"
      @delete-data="$emit('delete-data')"
    ></data-table>
    <edit-dialog
      :metedata="metedata"
      :dialog-visible.sync="dialogVisible"
      :dialog-title="dialogTitle"
      :operation-type="operationType"
      :dialog-data="dialogData"
      @create-data="$emit('create-data')"
      @update-data="$emit('update-data')"
    >
      <template #dialog-form-item="{dialogData:dialogData}"> </template>
    </edit-dialog>
  </div>
</template>

<script>
import SearchPane from './SearchPane';
import DataTable from './DataTable';
import EditDialog from './EditDialog';

export default {
  name: 'TableViews',
  components: { SearchPane, DataTable, EditDialog },
  props: {
    // 表格元数据
    metedata: {
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
    }
  }
};
</script>
