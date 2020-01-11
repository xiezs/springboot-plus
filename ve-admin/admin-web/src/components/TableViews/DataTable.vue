<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 16:14:37
 * @LastEditTime: 2019-12-17 14:07:33
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="data-table">
    <!-- 数据表格的row是来自data绑定的集合数据，并不是每一列绑定的prop -->
    <el-table
      ref="dataTable"
      v-loading.lock="loading"
      :data="tabledata.data"
      style="width: 100%"
      :stripe="true"
      :highlight-current-row="true"
      :border="true"
      :fit="true"
    >
      <el-table-column :key="Math.random()" type="selection" width="55">
      </el-table-column>
      <el-table-column :key="Math.random()" type="index"></el-table-column>

      <el-table-column
        v-for="(val, key) in metadata"
        :key="key"
        :prop="val.type === 'dict' ? key + '.name' : key"
        :label="val.name"
        :sortable="val.sortable"
        :show-overflow-tooltip="true"
      >
      </el-table-column>

      <el-table-column :key="Math.random()" width="170" align="right">
        <template #header="slot">
          <el-input
            v-model="search"
            size="mini"
            :clearable="true"
            prefix-icon="el-icon-search"
            placeholder="输入关键字搜索"
            @input="searchTable"
          />
        </template>
        <template v-slot="slot">
          <el-button size="mini" @click="handleEdit(slot.$index, slot.row)">
            编辑
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(slot.$index, slot.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="tabledata.total > 0"
      :total="tabledata.total"
      :page.sync="queryParams.page"
      :limit.sync="queryParams.limit"
      @pagination="pagination"
    />
  </div>
</template>

<script>
import Pagination from './Pagination';

export default {
  name: 'DataTable',
  components: { Pagination },
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
    // 搜索数据表格中数据的方法
    searchMethod: {
      type: Function,
      default() {
        return this.table.data;
      }
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      search: '',
      queryParams: {
        page: 1,
        limit: 10
      },
      cloneTableData: null
    };
  },
  updated() {
    console.log('以下是元数据和结果数据');
    console.log(this.metadata);
    console.log(this.tabledata);
  },
  methods: {
    searchTable() {
      /*
      要用一个临时数据将当前页面的数据保存下来。
      不然 $emit 会将原数据修改，
      会造成第二次搜索是基于第一次搜索的结果数据进行搜索的
      */
      this.cloneTableData = !this.cloneTableData
        ? Object.assign({}, this.tabledata)
        : this.cloneTableData;

      let tempTableData = Object.assign({}, this.cloneTableData);
      tempTableData.data = this.searchMethod(this.search, tempTableData.data);
      this.$emit('update:tabledata', tempTableData);
    },
    pagination(pageParams) {
      this.queryParams = Object.assign({}, this.queryParams, pageParams);
      this.$emit('pagination', this.queryParams);
      this.cloneTable = null;
    },
    handleEdit(index, row) {
      /* index 行号；row 行数据 */
      this.$emit('handle-edit', index, row);
    },
    handleDelete(index, row) {
      this.$emit('delete-data', index, row);
    }
  }
};
</script>
