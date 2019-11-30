<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2019-11-30 22:55:19
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <div>
    <table-views
      :metedata="metedata"
      :tabledata.sync="tabledata"
      :loading="loading"
      :search-method="searchMethod"
      @pagination="pagination"
      @filter-search="filterSearch"
      @create-data="createData"
      @delete-data="deleteData"
      @update-data="updateData"
    >
      <!-- todo 待解决的问题：样式问题。猜测原因：应该是样式穿透问题，不能是scope -->
      <!-- 往搜索栏中添加搜索条件 -->
      <template #filter-condition="{filterData:filterData}">
        <div class="filter-item-container">
          <el-form-item>
            <el-input
              v-model="filterData['test']"
              placeholder="test"
              :clearable="true"
              style="width: 200px;"
              class="filter-item"
            />
          </el-form-item>
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
    </table-views>
  </div>
</template>

<script>
import TableViews from '@/components/TableViews';
import { users, usersMetedata } from '@/api/user';

export default {
  name: 'Demo2',
  components: { TableViews },
  props: {},
  data() {
    return {
      // 整个页面的数据
      metedata: {},
      tabledata: {
        data: [],
        total: 0
      },
      loading: true
    };
  },
  computed: {},
  mounted() {
    this.obtainMetedata();
    this.obtainData({ page: 1, limit: 10 });
  },
  methods: {
    obtainMetedata() {
      this.loading = true;
      usersMetedata()
        .then(result => {
          const { code, data } = { ...result };
          this.metedata = Object.assign({}, data);
        })
        .catch(err => {})
        .finally(() => {
          this.loading = false;
        });
    },
    obtainData(queryParams) {
      this.loading = true;
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
