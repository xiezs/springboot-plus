<!--
 * @Author: 一日看尽长安花
 * @since: 2020-07-12 16:32:51
 * @LastEditTime: 2020-08-02 13:47:08
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <div class="sp-view-page">
    <div class="sp-search-inline-pane">
      <el-form :inline="true" :model="searchData">
        <el-form-item label="编码">
          <el-input
            v-model="searchData.code"
            placeholder="请输入编码"
          ></el-input>
        </el-form-item>
        <el-form-item label="名称">
          <el-input
            v-model="searchData.name"
            placeholder="请输入名称"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="sp-opr-btn-group">
      <div class="sp-func-btn">
        <el-button class="el-icon-plus" type="primary" @click="addRecord">
          增加
        </el-button>
      </div>
      <div class="sp-func-btn">
        <el-button
          class="el-icon-delete-solid"
          type="primary"
          @click="delRecord"
        >
          删除
        </el-button>
      </div>
      <div class="sp-func-btn">
        <el-button
          class="el-icon-delete-solid"
          type="primary"
          @click="delRecord"
        >
          查看用户
        </el-button>
      </div>
      <div class="sp-search-btn">
        <el-button class="el-icon-search" type="primary" @click="onSearch">
          查询
        </el-button>
      </div>
    </div>
    <el-table
      ref="dataTable"
      :data="tableData"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="#" type="selection"> </el-table-column>
      <el-table-column prop="id" label="id"> </el-table-column>
      <el-table-column prop="code" label="机构代码"> </el-table-column>
      <el-table-column prop="name" label="机构名称"> </el-table-column>
      <el-table-column prop="parent_org_text" label="上一级机构">
      </el-table-column>
      <el-table-column prop="type_text" label="机构类型"> </el-table-column>
      <el-table-column prop="create_time" label="创建时间">
        <template slot-scope="scope">
          <span>
            {{ scope.row.create_time | parseTime('{y}-{m}-{d} {h}:{i}') }}
          </span>
        </template></el-table-column
      >
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">
            编辑
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="pageQuery.total > 0"
      :total="pageQuery.total"
      :page.sync="pageQuery.page"
      :limit.sync="pageQuery.limit"
      @pagination="getList"
    />
    <edit-popup :visible.sync="editVisible" :data="rowData"></edit-popup>
  </div>
</template>
<script>
import Pagination from '@/components/Pagination'; // secondary package based on el-pagination
import EditPopup from './edit_popup';

import { getOrgs, getOrgById, deleteOrgData } from '@/api/admin_orgs';
import { download } from '@/api/core_file';

export default {
  name: 'OrgManager',
  components: { Pagination, EditPopup },
  props: {},
  data() {
    return {
      searchData: {},
      tableData: [],
      loading: true,
      pageQuery: { page: 1, limit: 10, total: 0 },
      editVisible: false,
      rowData: {}
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      const queryParam = Object.assign({}, this.searchData, this.pageQuery);
      getOrgs(queryParam).then(res => {
        const { code, message, data, count, total } = { ...res };
        this.pageQuery.total = total;
        this.tableData = data;
      });
    },
    onSearch() {
      this.getList();
    },
    addRecord() {
      this.rowData = {};
      this.editVisible = true;
    },
    handleEdit(index, row) {
      this.rowData = row;
      this.editVisible = true;
    },
    handleDelete(index, row) {
      const Vue = this;
      this.$confirm('确定删除该角色？', '删除', {
        type: 'warning '
      })
        .then(() => {
          deleteOrgData({ ids: [row.id] })
            .then(result => {
              this.$nextTick(() => {
                this.$notify({
                  title: '成功',
                  message: '删除角色数据成功',
                  type: 'success',
                  duration: 2000,
                  onClose: function() {
                    Vue.$router.replace('/refresh');
                  }
                });
              });
            })
            .catch(err => {
              this.$nextTick(() => {
                this.$notify({
                  title: '失败',
                  message: '删除角色数据失败',
                  type: 'error',
                  duration: 2000
                });
              });
            });
        })
        .catch(action => {});
    },
    delRecord() {
      const _table = this.$refs.dataTable;
      const isSelection = _table.selection.length > 0 ? true : false;
      if (!isSelection) {
        this.$message({
          type: 'warning',
          message: '请选择数据'
        });
        return;
      }
      this.$confirm('确定删除所选角色？', '删除', {
        type: 'warning '
      }).then(() => {
        const _selList = _table.selection;
        const ids = _selList.map(item => item.id);
        deleteOrgData({ ids: ids }).then(response => {
          const { code, message, data } = { ...response };
          this.onSearch();
        });
      });
    }
  }
};
</script>
<style scoped>
.sp-view-page {
  padding: 5px 10px;
}
.sp-opr-btn-group {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 3px 0;
  column-gap: 5px;
}
.sp-func-btn {
}
.sp-search-btn {
  position: absolute;
  right: 5px;
}
</style>
