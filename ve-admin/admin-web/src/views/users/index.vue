<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2020-04-27 15:02:24
 * @LastEditors: 一日看尽长安花
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
      <template #filter-condition="{filterData:slots}">
        <div class="filter-item-container">
          <el-form-item>
            <sp-cascader
              :key="Math.random()"
              v-model="slots.filterData"
              :json-paths="['org.id']"
              :labels="['org_id']"
              :props="orgIdCascaderProps"
              :options="orgIdCascaderProps.options"
              placeholder="部门"
            ></sp-cascader>
          </el-form-item>
        </div>
        <div class="filter-item-container">
          <el-form-item>
            <sp-cascader
              :key="Math.random()"
              v-model="slots.filterData"
              :json-paths="['job_type0.value', 'job_type1.value']"
              :labels="['job_type0', 'job_type1']"
              :props="jobTypeCascaderProps"
              :options="jobTypeCascaderProps.options"
              placeholder="岗位/职务"
            ></sp-cascader>
          </el-form-item>
        </div>
        <div class="filter-item-container">
          <el-form-item>
            <sp-cascader
              :key="Math.random()"
              v-model="slots.filterData"
              :json-paths="['state.value']"
              :labels="['state']"
              :props="stateCascaderProps"
              :options="stateCascaderProps.options"
              placeholder="状态"
            ></sp-cascader>
          </el-form-item>
        </div>
      </template>
      <!-- 往操作按钮组中添加自定义操作按钮 -->
      <template #operation-btn-group>
        <el-button
          class="filter-item"
          style="margin-left: 10px;"
          type="primary"
          size="mini"
          icon="el-icon-edit"
          @click="editUserRoles"
        >
          操作角色
        </el-button>
        <el-button
          class="filter-item"
          style="margin-left: 10px;"
          type="primary"
          size="mini"
          icon="el-icon-edit"
          @click="showChangePswAlert"
        >
          修改密码
        </el-button>
        <el-button
          class="filter-item"
          style="margin-left: 10px;"
          type="primary"
          icon="el-icon-edit"
          size="mini"
          @click="exportUserExcel"
        >
          导出Excel
        </el-button>
      </template>
      <!-- 编辑弹窗的插槽 -->
      <template #dialog-form-item="{dialogData:slots}">
        <el-form-item key="orgIdKey" label="所在机构" prop="org_id">
          <sp-cascader
            :key="Math.random()"
            v-model="slots.dialogData"
            :json-paths="['org.id']"
            :labels="['org_id']"
            :props="orgIdCascaderProps"
            :options="orgIdCascaderProps.options"
            placeholder="所在机构"
            class="sp-form-item"
          ></sp-cascader>
        </el-form-item>
        <el-form-item key="jobTypeKey" label="岗位职务" prop="job_type">
          <sp-cascader
            :key="Math.random()"
            v-model="slots.dialogData"
            :json-paths="['job_type0.value', 'job_type1.value']"
            :labels="['job_type0', 'job_type1']"
            :props="jobTypeCascaderProps"
            :options="jobTypeCascaderProps.options"
            placeholder="岗位职务"
            class="sp-form-item"
          ></sp-cascader>
        </el-form-item>
        <el-form-item key="stateKey" label="用户状态" prop="state">
          <sp-cascader
            :key="Math.random()"
            v-model="slots.dialogData"
            :json-paths="['state.value']"
            :labels="['state']"
            :props="stateCascaderProps"
            :options="stateCascaderProps.options"
            placeholder="用户状态"
            class="sp-form-item"
          ></sp-cascader>
        </el-form-item>

        <el-form-item key="fileKey" label="上传文档" prop="file">
          <file-upload
            :key="Math.random()"
            ref="fileUpload"
            :file-batch-id="slots.dialogData.attachment_id"
            :on-success="onUploadSuccess"
          >
            <el-button slot="trigger" size="small" type="primary">
              选取文件
            </el-button>
            <el-button
              style="margin-left: 10px;"
              size="small"
              type="success"
              @click="submitUpload"
            >
              上传到服务器
            </el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传word/pdf文档，且不超过2MB
            </div>
          </file-upload>
        </el-form-item>
      </template>
    </general-page>
    <change-password
      :key="Math.random()"
      ref="editDialog"
      :dialog-data="chgPwsDialogData"
      title="修改密码"
      :visible.sync="chgPwsVisible"
    >
    </change-password>
  </div>
</template>

<script>
import GeneralPage from '@/components/GeneralPage';
import FileUpload from '@/components/Upload/FileUpload';
import SpCascader from '@/components/Wrapper/SpCascader';
import ChangePassword from './change-password';

import {
  users,
  usersMetadata,
  saveUserData,
  updateUserData,
  deleteUserData,
  exportExcelUserData
} from '@/api/admin_users';
import { download } from '@/api/core_file';
import { immaditeLoadDicts } from '@/api/core_dict';
import { immaditeLoadOrgs } from '@/api/core_org';
import { layzyLoadDictTree, handleCascaderValue } from '@/services/dict';
import { layzyLoadOrgTree } from '@/services/org';

export default {
  name: 'CoreUsersView',
  components: { GeneralPage, FileUpload, SpCascader, ChangePassword },
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
        options: []
      },
      jobTypeCascaderProps: {
        checkStrictly: true,
        options: []
      },
      stateCascaderProps: {
        checkStrictly: true,
        options: []
      },
      chgPwsDialogData: {},
      chgPwsVisible: false
    };
  },
  computed: {},
  mounted() {
    immaditeLoadDicts({
      parentId: 0,
      type: 'job_type'
    }).then(result => {
      const { code, data } = { ...result };
      this.jobTypeCascaderProps.options = data;
    });
    immaditeLoadDicts({
      parentId: 0,
      type: 'user_state'
    }).then(result => {
      const { code, data } = { ...result };
      this.stateCascaderProps.options = data;
    });

    immaditeLoadOrgs({
      parentId: 0
    }).then(result => {
      const { code, data } = { ...result };
      this.orgIdCascaderProps.options = data;
    });
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
      queryParams = handleCascaderValue(queryParams, [
        'state',
        'job_type0',
        'job_type1'
      ]);
      this.loading = true;
      const _lodash = this.$lodash;
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
      dialogData = handleCascaderValue(dialogData, [
        'state',
        'job_type0',
        'job_type1'
      ]);
      const Vue = this;
      saveUserData(dialogData)
        .then(result => {
          Vue.$children[0].$refs.searchPaneGP.$refs.searchButton.$emit('click');
          this.$nextTick(() => {
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            });
          });
        })
        .catch(err => {
          this.$nextTick(() => {
            this.$notify({
              title: '失败',
              message: '创建失败',
              type: 'error',
              duration: 2000
            });
          });
        });
    },
    updateData(dialogData) {
      dialogData = handleCascaderValue(dialogData, [
        'state',
        'job_type0',
        'job_type1'
      ]);
      const Vue = this;
      updateUserData(dialogData)
        .then(result => {
          /** 刷新数据表格数据，懒得重新写个方法挂载在组件上了 */
          Vue.$children[0].$refs.searchPaneGP.$refs.searchButton.$emit('click');
          /**
           *  将回调延迟到下次 DOM 更新循环之后执行。
           * 而数据更新就代表dom更新，所以如果创建成功，数据就会更新
           */
          Vue.$nextTick(() => {
            Vue.$notify({
              title: '成功',
              message: '修改成功',
              type: 'success',
              duration: 2000
            });
          });
        })
        .catch(err => {
          Vue.$nextTick(() => {
            Vue.$notify({
              title: '失败',
              message: '修改用户失败',
              type: 'error',
              duration: 2000
            });
          });
        });
    },
    handleEditorDataObject(dialogData, arrayKey, keyPathMap) {
      let _valType = Object.prototype.toString.call(dialogData[arrayKey]);
      if (_valType === '[object Array]') {
        dialogData = handleCascaderValue(
          dialogData,
          arrayKey,
          this.$lodash.keys(keyPathMap)
        );
      } else {
        for (let key in keyPathMap) {
          dialogData[key] = this.$lodash.get(dialogData, keyPathMap[key]);
        }
      }
      return dialogData;
    },
    deleteData(index, row) {
      /**todo  补上对话框 */
      const Vue = this;
      deleteUserData({ ids: [row.id] })
        .then(result => {
          /** 刷新数据表格数据，懒得重新写个方法挂载在组件上了 */
          Vue.$children[0].$refs.searchPaneGP.$refs.searchButton.$emit('click');
          /**
           *  将回调延迟到下次 DOM 更新循环之后执行。
           * 而数据更新就代表dom更新，所以如果创建成功，数据就会更新
           */
          this.$nextTick(() => {
            this.$notify({
              title: '成功',
              message: '删除用户成功',
              type: 'success',
              duration: 2000
            });
          });
        })
        .catch(err => {
          this.$nextTick(() => {
            this.$notify({
              title: '失败',
              message: '删除用户失败',
              type: 'error',
              duration: 2000
            });
          });
        });
    },
    submitUpload() {
      this.$refs.fileUpload.$refs.upload.submit();
    },
    onUploadSuccess(response, file, fileList) {
      const { code, message, data } = { ...response };
      if (data) {
        this.$children[0].$refs.detailPageGP.$props.dialogData.attachment_id =
          response.data;
      }
    },
    exportUserExcel() {
      let queryParams = this.$lodash.mapValues(
        this.$children[0].$refs.searchPaneGP.$data.filterData
      );
      /**
       * 导出Excel文件的两步：
       * 1、通过查询条件由后台生成Excel临时文件，返回临时文件path
       * 2、通过path去下载该文件
       */
      exportExcelUserData(queryParams).then(res => {
        const { code, data, message } = { ...res };
        console.log(`...download path is ${data}`);
        download({ path: data });
      });
    },
    editUserRoles() {
      const _table = this.$children[0].$refs.dataTableGP.$refs.dataTable;
      const isSelection = _table.selection.length > 0 ? true : false;
      if (!isSelection) {
        this.$message({
          type: 'warning',
          message: '请选择一行数据'
        });
        return;
      }
      const _selected = _table.selection[0];
      const _router = this.$router;
      _router.push({ name: 'ManagerUserRole', params: { id: _selected.id } });
    },
    showChangePswAlert() {
      const _table = this.$children[0].$refs.dataTableGP.$refs.dataTable;
      const isSelection = _table.selection.length > 0 ? true : false;
      if (!isSelection) {
        this.$message({
          type: 'warning',
          message: '请选择一行数据'
        });
        return;
      }
      const _selected = _table.selection[0];
      this.chgPwsVisible = true;
      this.chgPwsDialogData = _selected;
    }
  }
};
</script>
