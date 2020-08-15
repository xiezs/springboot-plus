<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-08-15 15:09:07
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <!-- vue实例外创建 -->
  <div id="menu-manager" class="sp-transfer_editor--two">
    <div class="sp-side_panel--left">
      <el-radio-group
        v-model="formModel.roleId"
        size="medium"
        @change="changeRole"
      >
        <el-row v-for="item in rolesData" :key="item.id">
          <el-col>
            <el-radio :key="item.id" :label="item.id" border>
              {{ item.name }}
            </el-radio>
          </el-col>
        </el-row>
      </el-radio-group>
    </div>
    <div class="sp-side_panel--right">
      <el-input v-model="filterText" placeholder="输入关键字进行过滤">
      </el-input>
      <el-tree
        key="treeKey"
        ref="tree"
        :data="funcTreeData"
        node-key="id"
        show-checkbox
        default-expand-all
        highlight-current
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        @check="nodeSelected"
      >
        <template v-slot="{ node: node, data: data }">
          <div :class="['sp-tree_node', 'sp-tree_node_type--' + data.type]">
            <span>{{ data.name }}</span>
          </div>
        </template>
      </el-tree>
    </div>
  </div>
</template>
<script>
import {
  getRoles,
  getFunctionIdsByRole,
  updateRoleFunctions
} from '@/api/admin_roles';
import { funcs } from '@/api/admin_funcs';
export default {
  name: 'RoleFuncAuthManager',
  components: {},
  props: {},
  data() {
    return {
      filterText: '',
      rolesData: [],
      roleFunctionIds: [],
      funcTreeData: [],
      formModel: {
        roleId: undefined
      },
      rules: {}
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {
    this.getRoleList();
    this.getFuncTree();
  },
  methods: {
    changeRole(roleId) {
      getFunctionIdsByRole({ role_id: roleId }).then(res => {
        const { data, code, message } = { ...res };
        this.roleFunctionIds = data;
        this.$refs.tree.setCheckedKeys(this.roleFunctionIds);
      });
    },
    getRoleList() {
      getRoles().then(res => {
        const { data, code, message } = { ...res };
        this.rolesData = data;
      });
    },
    getFuncTree() {
      funcs().then(res => {
        const { code, message, data } = { ...res };
        const vmNode = [
          {
            id: -1,
            name: '平台',
            children: data
          }
        ];
        this.funcTreeData = vmNode;
      });
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    getTreeBranchIds(node) {
      let ids = [];
      ids.push(node.id);
      const _children = node.children;
      for (let i = 0; i < _children.length; i++) {
        const child = _children[i];
        let _child_ids = this.getTreeBranchIds(child);
        ids = ids.concat(_child_ids);
      }
      return ids;
    },
    nodeSelected(selectedNode, allSelectedNodes) {
      if (!this.formModel.roleId) {
        this.$message({
          message: '请选择一个角色',
          type: 'warning'
        });
        this.getTreeBranchIds(selectedNode).forEach((v, i) => {
          this.$refs.tree.setChecked(v, false, true);
        });
        return;
      }
      const selectedNodes = this.$refs.tree.getCheckedNodes(false, true);
      const functions = selectedNodes.map(n => {
        return { id: n.id, type: n.type, name: n.name };
      });
      updateRoleFunctions({
        role_id: this.formModel.roleId,
        functions: functions
      }).then(res => {
        const { code, message, data } = { ...res };
        this.$message({
          message: '设置成功',
          type: 'success'
        });
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.el-row {
  margin: 0.68rem;
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
.sp-tree_node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-left: 6px;
  border-radius: 5px;
}
%sp-tree_node_type {
  box-shadow: 1px 1px 1px 1px #e8f4ff;
}
.sp-tree_node_type--FN0 {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #ff03035e, transparent);
}
.sp-tree_node_type--FN1 {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #fff5035e, transparent);
}
.sp-tree_node_type--FN2 {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #033cff5e, transparent);
}
</style>
<style lang="scss">
.el-tree-node {
  margin-top: 5px;
}
</style>
