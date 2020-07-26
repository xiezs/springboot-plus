<!--
 * @Author: 一日看尽长安花
 * @since: 2020-07-12 16:32:51
 * @LastEditTime: 2020-07-26 17:18:21
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <div class="sp-view-page">
    <div class="sp-search-inline-pane">
      <el-form :inline="true" :model="searchData">
        <el-form-item label="字典值">
          <el-input
            v-model="searchData.value"
            placeholder="请输入字典值"
          ></el-input>
        </el-form-item>
        <el-form-item label="字典名称">
          <el-input
            v-model="searchData.name"
            placeholder="请输入字典名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input
            v-model="searchData.typeName"
            placeholder="请输入字典类型"
          ></el-input>
        </el-form-item>
        <el-form-item label="上级字典">
          <el-input
            v-model.number="searchData.parent"
            placeholder="请输入上级字典"
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
        <file-upload
          class="upload-demo"
          action="/dicts/excel/import"
          :show-file-list="false"
          :auto-upload="true"
        >
          <el-button size="mini" class="el-icon-download" type="primary">
            导入
          </el-button>
        </file-upload>
      </div>
      <div class="sp-func-btn">
        <el-button class="el-icon-download" type="primary" @click="exportExcel">
          导出
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
      <el-table-column prop="name" label="字典名称"> </el-table-column>
      <el-table-column prop="value" label="字典值"> </el-table-column>
      <el-table-column prop="type_name" label="字典类型名称"> </el-table-column>
      <el-table-column prop="type" label="字典类型值"> </el-table-column>
      <el-table-column prop="sort" label="排序"> </el-table-column>
      <el-table-column prop="parent" label="父级id"> </el-table-column>
      <el-table-column prop="remark" label="备注"> </el-table-column>
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
import FileUpload from '@/components/Upload/FileUpload';
import Pagination from '@/components/Pagination'; // secondary package based on el-pagination
import EditPopup from './edit_popup';

import {
  getDicts,
  getDictById,
  deleteDictData,
  exportDictExcel,
  importExcel
} from '@/api/dicts';
import { download } from '@/api/file';

export default {
  name: 'DictManager',
  components: { Pagination, EditPopup, FileUpload },
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
      getDicts(queryParam).then(res => {
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
      this.$confirm('确定删除该字典？', '删除', {
        type: 'warning '
      })
        .then(() => {
          deleteDictData({ ids: [row.id] })
            .then(result => {
              this.$nextTick(() => {
                this.$notify({
                  title: '成功',
                  message: '删除字典数据成功',
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
                  message: '删除字典数据失败',
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
      this.$confirm('确定删除所选字典？', '删除', {
        type: 'warning '
      }).then(() => {
        const _selList = _table.selection;
        const ids = _selList.map(item => item.id);
        deleteDictData({ ids: ids }).then(response => {
          const { code, message, data } = { ...response };
          this.onSearch();
        });
      });
    },
    importExcel() {},
    exportExcel() {
      /**
       * 导出Excel文件的两步：
       * 1、通过查询条件由后台生成Excel临时文件，返回临时文件path
       * 2、通过path去下载该文件
       */
      exportDictExcel(this.searchData).then(res => {
        const { code, data, message } = { ...res };
        console.log(`...download path is ${data}`);
        download({ path: data });
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
