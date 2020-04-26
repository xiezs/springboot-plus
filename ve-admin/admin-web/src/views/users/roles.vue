<!--
 * @Author: 一日看尽长安花
 * @since: 2020-03-29 16:00:50
 * @LastEditTime: 2020-04-23 14:06:39
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div class="sp-root-container">
    <el-form
      ref="searchForm"
      size="mini"
      :model="searchData"
      :inline="true"
      label-width="auto"
    >
      <el-row :gutter="10">
        <el-col :span="5">
          <el-form-item label="角色">
            <el-cascader
              v-model="searchData['roleId']"
              :props="roleIdCascaderProps"
              :options="roleIdCascaderProps.options"
              :show-all-levels="false"
              clearable
              placeholder="角色"
            ></el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="部门">
            <el-cascader
              v-model="searchData['orgId']"
              :props="orgIdCascaderProps"
              :options="orgIdCascaderProps.options"
              :show-all-levels="false"
              clearable
              placeholder="部门"
            ></el-cascader>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="10" type="flex" justify="end">
        <el-col :span="2">
          <el-form-item>
            <el-button
              type="primary"
              icon="el-icon-search"
              @click="filterSearch"
            >
              搜索
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="sp-btn-group">
      <el-button type="primary" icon="edit" @click="showDialog">增加</el-button>
      <el-button type="primary" icon="delete" @click="deleteUserRole">
        删除
      </el-button>
    </div>
    <el-table
      ref="dataTable"
      v-loading.lock="loading"
      :data="tableData"
      style="width: 100%"
    >
      <el-table-column :key="Math.random()" type="selection" width="55">
      </el-table-column>
      <el-table-column :key="Math.random()" type="index"></el-table-column>
      <el-table-column
        key="orgName"
        header-align="center"
        align="center"
        prop="orgName"
        label="机构名称"
      >
      </el-table-column>
      <el-table-column
        key="roleName"
        header-align="center"
        align="center"
        prop="roleName"
        label="角色名称"
      >
      </el-table-column>
    </el-table>
    <pagination
      v-show="tableData.total > 0"
      :total="tableData.total"
      :page.sync="queryParams.page"
      :limit.sync="queryParams.limit"
      @pagination="pagination"
    />
    <add-user-role
      :key="Math.random()"
      ref="editDialog"
      :dialog-data="dialogData"
      :title="dialogTitle"
      :visible.sync="visible"
      :org-id-cascader-props="orgIdCascaderProps"
      :role-id-cascader-props="roleIdCascaderProps"
    >
    </add-user-role>
  </div>
</template>
<script>
import Pagination from '@/components/Pagination';
import { immaditeLoadRoles } from '@/api/role';
import { immaditeLoadOrgs } from '@/api/org';
import { getUserRoles, getUserById } from '@/api/user';
import AddUserRole from './add-user-role';

export default {
  name: 'ManagerUserRole',
  components: { Pagination, AddUserRole },
  data() {
    return {
      id: this.$route.params.id,
      searchData: {},
      tableData: [],
      loading: false,
      roleIdCascaderProps: {
        checkStrictly: true,
        options: []
      },
      orgIdCascaderProps: {
        checkStrictly: true,
        options: []
      },
      queryParams: {
        page: 1,
        limit: 10
      },
      dialogTitle: '',
      dialogData: {},
      visible: false
    };
  },
  mounted() {
    immaditeLoadRoles().then(result => {
      const { code, data } = { ...result };
      this.roleIdCascaderProps.options = data;
    });
    immaditeLoadOrgs({
      parentId: 0
    }).then(result => {
      const { code, data } = { ...result };
      this.orgIdCascaderProps.options = data;
    });
    getUserRoles({
      userId: this.id,
      roleId: undefined,
      orgId: undefined
    }).then(result => {
      const { code, data } = { ...result };
      this.tableData = data;
    });
    getUserById({ id: this.id }).then(result => {
      const { code, data } = { ...result };
      this.dialogData = data;
    });
  },
  methods: {
    filterSearch() {},
    pagination(pageParams) {},
    showDialog() {
      this.dialogTitle = '创建角色';
      this.visible = true;
    },
    deleteUserRole() {
      const _table = this.$refs.dataTable;
      const isSelection = _table.selection.length > 0 ? true : false;
      if (!isSelection) {
        this.$message({
          type: 'warning',
          message: '请选择数据'
        });
        return;
      }
      const _selList = _table.selection;
      const ids = _selList.map(item => item.id);
    }
  }
};
</script>
<style scoped>
.sp-root-container {
  margin-top: 0.5em;
  margin-left: 0.5em;
}
</style>
