<!--
 * @Author: 一日看尽长安花
 * @since: 2019-10-12 15:43:18
 * @LastEditTime: 2020-03-11 15:30:15
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
      <template #filter-condition="{filterData:filterData}">
        <div class="filter-item-container">
          <el-form-item>
            <el-cascader
              v-model="filterData['orgId']"
              :props="orgIdCascaderProps"
              :options="orgIdCascaderProps.options"
              :show-all-levels="false"
              clearable
              placeholder="部门"
            ></el-cascader>
          </el-form-item>
        </div>
        <div class="filter-item-container">
          <el-form-item>
            <el-cascader
              v-model="filterData['jobType']"
              :props="jobTypeCascaderProps"
              :options="jobTypeCascaderProps.options"
              clearable
              placeholder="岗位/职务"
            ></el-cascader>
          </el-form-item>
        </div>
        <div class="filter-item-container">
          <el-form-item>
            <el-cascader
              v-model="filterData['state']"
              :props="stateCascaderProps"
              :options="stateCascaderProps.options"
              clearable
              placeholder="状态"
            ></el-cascader>
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
      <!-- 编辑弹窗的插槽 -->
      <template #dialog-form-item="{dialogData:dialogData}">
        <el-form-item key="orgIdKey" label="所在机构" prop="org_id">
          <el-cascader
            v-model="dialogData['org_id_value']"
            :props="orgIdCascaderProps"
            :options="orgIdCascaderProps.options"
            :show-all-levels="false"
            clearable
            placeholder="所在机构"
            class="sp-form-item"
          ></el-cascader>
        </el-form-item>
        <el-form-item key="jobTypeKey" label="岗位职务" prop="job_type">
          <el-cascader
            v-model="dialogData['job_type_value']"
            :props="jobTypeCascaderProps"
            :options="jobTypeCascaderProps.options"
            :show-all-levels="false"
            clearable
            placeholder="岗位职务"
            class="sp-form-item"
          ></el-cascader>
        </el-form-item>
        <el-form-item key="stateKey" label="用户状态" prop="state">
          <el-cascader
            v-model="dialogData['state_value']"
            :props="stateCascaderProps"
            :options="stateCascaderProps.options"
            :show-all-levels="false"
            clearable
            placeholder="用户状态"
            class="sp-form-item"
          ></el-cascader>
        </el-form-item>

        <el-form-item key="fileKey" label="上传文档" prop="file">
          <file-upload>
            <el-button slot="trigger" size="small" type="primary">
              选取文件
            </el-button>
            <el-button style="margin-left: 10px;" size="small" type="success">
              上传到服务器
            </el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传jpg/png文件，且不超过500kb
            </div>
          </file-upload>
        </el-form-item>
      </template>
    </general-page>
  </div>
</template>

<script>
import GeneralPage from '@/components/GeneralPage';
import FileUpload from '@/components/Upload/FileUpload';

import { users, usersMetadata, saveUserData, updateUserData } from '@/api/user';
import { immaditeLoadDicts } from '@/api/dict';
import { immaditeLoadOrgs } from '@/api/org';
import { layzyLoadDictTree, handleCascaderValue } from '@/services/dict';
import { layzyLoadOrgTree } from '@/services/org';

export default {
  name: 'CoreUsersView',
  components: { GeneralPage, FileUpload },
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
      }
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
      this.loading = true;
      queryParams = handleCascaderValue(queryParams, 'orgId', ['org_id']);
      queryParams = handleCascaderValue(queryParams, 'jobType', [
        'job_type0',
        'job_type1'
      ]);
      queryParams = handleCascaderValue(queryParams, 'state', ['state']);
      users(queryParams)
        .then(result => {
          const { code, data } = { ...result };
          /**
           * 这一步是因为编辑弹窗在创建时，v-model不能使用json多级获取，不然会报错；
           * 所以将其处理为一级节点
           */
          for (let i in result.data) {
            result.data[i]['org_id_value'] = result.data[i]['org']['id'];
            result.data[i]['state_value'] = result.data[i]['state']['value'];
            if (result.data[i]['job_type0']) {
              result.data[i]['job_type_value'] =
                result.data[i]['job_type0']['value'];
            }
            if (result.data[i]['job_type1']) {
              result.data[i]['job_type_value'] =
                result.data[i]['job_type1']['value'];
            }
          }
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
      this.handleEditorDataObject(dialogData, 'org_id_value', {
        org_id: 'org.id'
      });
      this.handleEditorDataObject(dialogData, 'state_value', {
        state: 'state.value'
      });
      this.handleEditorDataObject(dialogData, 'job_type_value', {
        job_type0: 'job_type0.value',
        job_type1: 'job_type1.value'
      });
      saveUserData(dialogData)
        .then(result => {
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
      this.handleEditorDataObject(dialogData, 'org_id_value', {
        org_id: 'org.id'
      });
      this.handleEditorDataObject(dialogData, 'state_value', {
        state: 'state.value'
      });
      this.handleEditorDataObject(dialogData, 'job_type_value', {
        job_type0: 'job_type0.value',
        job_type1: 'job_type1.value'
      });
      updateUserData(dialogData)
        .then(result => {
          this.$refs['searchButton'].click();
          this.$nextTick(() => {
            this.$notify({
              title: '成功',
              message: '修改成功',
              type: 'success',
              duration: 2000
            });
          });
        })
        .catch(err => {
          this.$nextTick(() => {
            this.$notify({
              title: '失败',
              message: '修改失败',
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
