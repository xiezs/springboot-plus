<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-08-15 21:13:05
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <!-- vue实例外创建 -->
  <div id="menu-manager" class="sp-transfer_editor--two">
    <div class="sp-side_panel--left">
      <el-radio-group v-model="selectRoleId" size="medium" @change="changeRole">
        <el-row v-for="item in rolesDatas" :key="item.id">
          <el-col>
            <el-radio :key="item.id" :label="item.id" border>
              {{ item.name }}
            </el-radio>
          </el-col>
        </el-row>
      </el-radio-group>
    </div>
    <div class="sp-side_panel--right">
      <el-row v-for="(item, index) in roleDataFunctions" :key="index">
        <el-col>
          <span class="demonstration">{{ item.label }}</span>
          <el-cascader
            :ref="'cascader' + index"
            v-model="cascaderValues['val' + index]"
            :options="allOptions['options' + index]"
            clearable
            @change="changeSelect('cascader' + index)"
          ></el-cascader>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import {
  getRoles,
  getRoleDataFunctions,
  saveRoleDataFunction
} from '@/api/admin_roles';
export default {
  name: 'RoleDataFuncAuthManager',
  components: {},
  props: {},
  data() {
    return {
      rolesDatas: [],
      roleDataFunctions: [],
      selectRoleId: undefined,
      cascaderValues: {},
      allOptions: {}
    };
  },
  watch: {},
  mounted() {
    this.init();
  },
  methods: {
    changeRole(roleId) {
      this.getRoleDataFunctions(roleId);
    },
    init() {
      getRoles().then(res => {
        const { data, code, message } = { ...res };
        this.rolesDatas = data;
        if (data.length > 0) {
          this.selectRoleId = data[0].id;
          this.getRoleDataFunctions(this.selectRoleId);
        }
      });
    },
    getRoleDataFunctions(roleId) {
      getRoleDataFunctions({ role_id: roleId }).then(res => {
        const { data, code, message } = { ...res };
        this.roleDataFunctions = data;
        const _that = this;
        this.roleDataFunctions.forEach((v, i) => {
          _that.cascaderValues['val' + i] = v.accesses[0].tail.data_access_type;
          _that.allOptions['options' + i] = v.accesses;
        });
      });
    },
    changeSelect(refName) {
      const node = this.$refs[refName][0].getCheckedNodes()[0];
      const _that = this;
      if (node) {
        // 选中
        const _data = node.data;
        const params = {
          role_id: this.selectRoleId,
          fn_id: _data.tail.id,
          access_type: _data.value
        };
        saveRoleDataFunction(params).then(res => {
          const { code, message, data } = { ...res };
          _that.$message({
            message: '设置成功',
            type: 'success'
          });
        });
      } else {
        // 不选择
        _that.$message({
          message: '不允许为空',
          type: 'warn'
        });
      }
    }
  }
};
</script>
<style lang="scss" scoped>
.el-row {
  margin: 0.68rem;
}
.demonstration {
  color: #606266;
  margin-right: 1rem;
}
.sp-transfer_editor--two {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  margin: 10px;
  min-height: 84vh;
  max-height: 84vh;
}
%sp-side_panel--common {
  flex-grow: 1;
  flex-shrink: 1;
  flex-basis: 0%;
  box-shadow: 2px 2px 6px 1px #aaa;
  padding: 10px;
  margin: 0 10px;
}
.sp-side_panel--left {
  @extend %sp-side_panel--common;
  overflow-y: scroll;
}
.sp-side_panel--right {
  @extend %sp-side_panel--common;
  overflow-y: scroll;
}
</style>
