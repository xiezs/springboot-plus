<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-06-14 18:58:00
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <!-- vue实例外创建 -->
  <div id="menu-manager" class="sp-transfer_editor--two">
    <div class="sp-side_panel--left">
      <el-input v-model="filterText" placeholder="输入关键字进行过滤">
      </el-input>
      <el-tree
        key="treeKey"
        ref="tree"
        :data="treeData"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
      >
        <template v-slot="{ node: node, data: data }">
          <div :class="['sp-tree_node', 'sp-tree_node_type--' + data.type]">
            <span>{{ data.name }}</span>
            <span>
              <el-button
                plain
                type="primary"
                size="mini"
                @click="appendNode(node, data)"
              >
                添加
              </el-button>
              <el-button
                v-if="data.id !== 0"
                plain
                type="primary"
                size="mini"
                @click="editNode(node, data)"
              >
                编辑
              </el-button>
              <el-button
                v-if="data.id !== 0"
                plain
                type="primary"
                size="mini"
                @click="removeNode(node, data)"
              >
                删除
              </el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </div>
    <div class="sp-side_panel--right">
      <el-form
        key="formKey"
        ref="nodeForm"
        :rules="rules"
        :model="formModel"
        label-width="80px"
      >
        <el-form-item label="功能名" prop="name">
          <el-input
            v-model="formModel.name"
            placeholder="请输入菜单名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜单代码" prop="code">
          <el-input
            v-model="formModel.code"
            placeholder="请输入系统唯一菜单代码"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜单地址" prop="relation_function">
          <el-input
            v-model="formModel.relation_function.access_url"
            placeholder="请选择关联功能点"
            readonly
            @focus="openSelectRelationFunctionLayer"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜单图标">
          <el-input
            v-model="formModel.icon"
            placeholder="请输入已有的svg图标，询问前端"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜单序号">
          <el-input
            v-model="formModel.seq"
            placeholder="请输入本级中的菜单序号"
          ></el-input>
        </el-form-item>
        <el-form-item label="父菜单" prop="parent">
          <el-input
            v-model="formModel.parent.name"
            placeholder="点击选择上一级菜单"
            readonly
            @focus="openSelectParentNodeLayer"
          ></el-input>
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-select v-model="formModel.type" placeholder="请选择菜单类型">
            <el-option label="系统" value="MENU_S"></el-option>
            <el-option label="导航" value="MENU_N"></el-option>
            <el-option label="菜单" value="MENU_M"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveNode">保存</el-button>
          <el-button>取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <sel-func-dialog
      v-model="formModel"
      :per-level-label="['relation_function']"
      :visible.sync="selFuncDialogVisible"
      :tree-data="funcTreeData"
    >
    </sel-func-dialog>
    <sel-menu-dialog
      v-model="formModel"
      :per-level-label="['parent']"
      :visible.sync="selParentDialogVisible"
      :tree-data="treeData"
    >
    </sel-menu-dialog>
  </div>
</template>
<script>
/** 菜单管理 */
import {
  menus,
  createMenuItem,
  updateMenuItem,
  delMenuItemsByParent
} from '@/api/admin_menus';
import { funcs } from '@/api/admin_funcs';

import SelFuncDialog from '@/views/functions/select_dialog';
import SelMenuDialog from './select_dialog';

export default {
  name: 'MenuMange',
  components: { SelFuncDialog, SelMenuDialog },
  props: {},
  data() {
    return {
      filterText: '',
      treeData: [],
      funcTreeData: [],
      formModel: {
        parent: {
          id: undefined,
          name: undefined
        },
        relation_function: {
          id: undefined,
          name: undefined
        }
      },
      actType: 'create',
      selFuncDialogVisible: false,
      selParentDialogVisible: false,
      rules: {
        name: { required: true, message: '请输入名称', trigger: 'blur' },
        code: { required: true, message: '请输入代码点', trigger: 'blur' },
        relation_function: {
          type: 'object',
          required: true,
          fields: {
            access_url: {
              type: 'string',
              message: '请选择关联功能点',
              required: true
            }
          }
        },
        parent: {
          type: 'object',
          required: true,
          fields: {
            name: { type: 'string', message: '请选择父功能', required: true }
          }
        },
        type: {
          type: 'string',
          required: true,
          message: '请选择访问类型',
          trigger: 'change'
        }
      }
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {
    menus().then(res => {
      const { code, message, data } = { ...res };
      const vmNode = [
        {
          id: 0,
          name: '平台',
          children: data
        }
      ];
      this.treeData = vmNode;
    });
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
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    appendNode(node, data) {
      this.formModel = {
        parent: {
          id: undefined,
          name: undefined
        },
        relation_function: {
          id: undefined,
          name: undefined
        }
      };
      this.formModel.parent = data;
      this.actType = 'create';
    },
    editNode(node, data) {
      this.formModel = data;
      this.formModel.parent = node.parent.data;
      this.actType = 'editor';
    },
    removeNode(node, data) {
      const ids = this.getTreeBranchIds(node);
      this.$confirm('此操作将永久删除该菜单及其子菜单, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delMenuItemsByParent({ ids: ids }).then(response => {
            const { code, message, data } = { ...response };
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            const _that = this;
            this.$message({
              message: '删除成功',
              type: 'success',
              onClose: function(_instance) {
                _that.$router.replace('/refresh');
                loading.close();
              }
            });
          });
        })
        .catch(() => {});
    },
    getTreeBranchIds(node) {
      let ids = [];
      ids.push(node.data.id);
      const _children = node.childNodes;
      for (let i = 0; i < _children.length; i++) {
        const child = _children[i];
        let _child_ids = this.getTreeBranchIds(child);
        ids = ids.concat(_child_ids);
      }
      return ids;
    },
    saveNode() {
      const _that = this;
      _that.$refs.nodeForm.validate(valid => {
        if (!valid) {
          _that.$message.error('数据请填写完成');
          return false;
        }
        // 解除循环引用
        delete _that.formModel.parent.children;
        if (_that.actType === 'create') {
          _that.formModel.parent_menu_id = _that.formModel.parent.id;
          createMenuItem(_that.formModel).then(response => {
            const { code, message, data } = { ...response };
            const _that = this;
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$message({
              message: '创建菜单成功',
              type: 'success',
              onClose: function(_instance) {
                // todo 可以用组件Mixin改写，或者直接挂载到Router原型上
                _that.$router.replace('/refresh');
                loading.close();
              }
            });
          });
        } else {
          updateMenuItem(_that.formModel).then(response => {
            const { code, message, data } = { ...response };
            const _that = this;
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$message({
              message: '更新菜单成功',
              type: 'success',
              onClose: function(_instance) {
                // todo 可以用组件Mixin改写，或者直接挂载到Router原型上
                _that.$router.replace('/refresh');
                loading.close();
              }
            });
          });
        }
      });
    },
    openSelectParentNodeLayer() {
      this.selParentDialogVisible = true;
    },
    openSelectRelationFunctionLayer() {
      this.selFuncDialogVisible = true;
    }
  }
};
</script>
<style lang="scss" scoped>
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
.sp-tree_node_type--MENU_S {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #ff03035e, transparent);
}
.sp-tree_node_type--MENU_N {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #fff5035e, transparent);
}
.sp-tree_node_type--MENU_M {
  @extend %sp-tree_node_type;
  background: linear-gradient(to right, #033cff5e, transparent);
}
</style>
<style lang="scss">
.el-tree-node {
  margin-top: 5px;
}
</style>
