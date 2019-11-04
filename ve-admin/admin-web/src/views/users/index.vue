<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2019-11-04 23:25:52
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
    ></table-views>
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
          this.tabledata = Object.assign({}, data);
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
      this.obtainData(queryParams);
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
