<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime : 2020-02-04 16:15:14
 * @LastEditors  : 一日看尽长安花
 * @Description:
 -->
<template>
  <div>
    <general-page
      :metadata="metadata"
      :tabledata.sync="tabledata"
      :loading="loading"
      :search-method="searchMethod"
      @pagination="pagination"
      @filter-search="filterSearch"
      @create-data="createData"
      @delete-data="deleteData"
      @update-data="updateData"
    >
      <!-- 往搜索栏中添加搜索条件 -->
      <template #filter-condition="{filterData:filterData}">
        <div class="filter-item-container">
          <el-cascader
            v-model="filterData['orgId']"
            :props="orgIdCascaderProps"
            :show-all-levels="false"
            clearable
            placeholder="部门"
          ></el-cascader>
        </div>
        <div class="filter-item-container">
          <el-cascader
            v-model="filterData['jobType']"
            :props="jobTypeCascaderProps"
            clearable
            placeholder="岗位/职务"
          ></el-cascader>
        </div>
        <div class="filter-item-container">
          <el-cascader
            v-model="filterData['state']"
            :props="stateCascaderProps"
            clearable
            placeholder="状态"
          ></el-cascader>
        </div>
      </template>
      <!-- 往操作按钮组中添加自定义操作按钮 -->
      <template #operation-btn-group>
        <el-button
          class="filter-item"
          style="margin-left: 10px;"
          type="primary"
          icon="el-icon-edit"
          size="mini"
        >
          测试
        </el-button>
      </template>
      <template #dialog-form-item="{dialogData:dialogData}">
        <el-form-item key="slotkey" label="测试插槽" prop="test">
          <el-input
            v-model="dialogData['test']"
            placeholder="test"
            :clearable="true"
            style="width: 200px;"
            class="filter-item"
          />
        </el-form-item>
      </template>
    </general-page>
  </div>
</template>

<script>
import GeneralPage from '@/components/GeneralPage';
import { users, usersMetadata } from '@/api/user';
import { loadDictCascaderData, handleDictCascaderValue } from '@/services/dict';
import { loadOrgCascaderData, handleOrgCascaderValue } from '@/services/org';

export default {
  name: 'CoreUsersView',
  components: { GeneralPage },
  props: {},
  data() {
    return {
      // 整个页面的数据
      metadata: {},
      tabledata: {
        data: [],
        total: 0
      },
      loading: true,
      orgIdCascaderProps: {
        checkStrictly: true,
        lazy: true,
        lazyLoad(node, resolve) {
          loadOrgCascaderData(node, resolve);
        }
      },
      jobTypeCascaderProps: {
        checkStrictly: true,
        lazy: true,
        lazyLoad(node, resolve) {
          loadDictCascaderData(node, resolve, 'job_type');
        }
      },
      stateCascaderProps: {
        checkStrictly: true,
        lazy: true,
        lazyLoad(node, resolve) {
          loadDictCascaderData(node, resolve, 'user_state');
        }
      }
    };
  },
  computed: {},
  mounted() {
    this.obtainMetadata();
    this.obtainData({ page: 1, limit: 10 });
  },
  methods: {
    obtainMetadata() {
      this.loading = true;
      usersMetadata()
        .then(result => {
          const { code, data } = { ...result };
          this.metadata = Object.assign({}, data);
        })
        .catch(err => {})
        .finally(() => {
          this.loading = false;
        });
    },
    obtainData(queryParams) {
      this.loading = true;
      queryParams = handleOrgCascaderValue(queryParams, 'orgId', 'org_id');
      queryParams = handleDictCascaderValue(queryParams, 'jobType', [
        'jobType0',
        'jobType1'
      ]);
      queryParams = handleDictCascaderValue(queryParams, 'state', ['state']);
      users(queryParams)
        .then(result => {
          const { code, data } = { ...result };
          this.tabledata = Object.assign({}, result);
        })
        .catch(err => {})
        .finally(() => {
          this.loading = false;
        });
    },
    pagination(queryParams) {
      this.obtainData(queryParams);
    },
    searchMethod(search, tableData) {
      return tableData.filter(
        d => !search || d.name.toLowerCase().includes(search.toLowerCase())
      );
    },
    filterSearch(queryParams) {
      this.obtainData(Object.assign({ page: 1, limit: 10 }, queryParams));
    },
    createData(dialogData) {
      this.$nextTick(() => {
        this.$notify({
          title: '成功',
          message: '添加成功',
          type: 'success',
          duration: 2000
        });
      });
    },
    updateData(dialogData) {
      this.$nextTick(() => {
        this.$notify({
          title: '成功',
          message: '修改成功',
          type: 'success',
          duration: 2000
        });
      });
    },
    deleteData(index, row) {
      this.$nextTick(() => {
        this.$notify({
          title: '成功',
          message: '删除成功',
          type: 'success',
          duration: 2000
        });
      });
    }
  }
};
</script>
