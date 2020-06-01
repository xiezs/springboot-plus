<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-06-01 22:02:17
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <!-- vue实例外创建 -->
  <div id="func-manager" class="sp-transfer_editor--two">
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
                v-if="data.id !== -1"
                plain
                type="primary"
                size="mini"
                @click="editNode(node, data)"
              >
                编辑
              </el-button>
              <el-button
                v-if="data.id !== -1"
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
      <el-form key="formKey" ref="form" :model="formModel" label-width="80px">
        <el-form-item label="功能名">
          <el-input v-model="formModel.name"></el-input>
        </el-form-item>
        <el-form-item label="功能代码">
          <el-input v-model="formModel.code"></el-input>
        </el-form-item>
        <el-form-item label="功能地址">
          <el-input
            v-model="formModel.access_url"
            :disabled="actType === 'editor'"
          ></el-input>
        </el-form-item>
        <el-form-item label="父功能">
          <el-input
            v-model="formModel.parent.name"
            readonly
            @focus="openSelectParentNodeLayer"
          ></el-input>
        </el-form-item>
        <el-form-item label="功能类型">
          <el-select v-model="formModel.type" placeholder="请选择功能类型">
            <el-option label="导航访问" value="FN0"></el-option>
            <el-option label="功能点访问" value="FN1"></el-option>
            <el-option label="菜单访问" value="FN2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="actType === 'create' ? createNode : updateNode"
          >保存</el-button
          >
          <el-button>取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <sel-func-dialog
      v-model="formModel"
      :per-level-label="['parent']"
      :visible.sync="dialogVisible"
      :tree-data="treeData"
    >
    </sel-func-dialog>
  </div>
</template>
<script>
/** 功能点管理 */
import { funcs } from '@/api/func';
import SelFuncDialog from './select_dialog';

export default {
  name: 'FunctionMange',
  components: { SelFuncDialog },
  props: {},
  data() {
    return {
      filterText: '',
      treeData: [],
      formModel: {
        parent: {
          id: undefined,
          name: undefined
        }
      },
      actType: 'create',
      dialogVisible: false
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  mounted() {
    funcs().then(res => {
      const { code, message, data } = { ...res };
      const vmNode = [
        {
          id: -1,
          name: '平台',
          children: data
        }
      ];
      this.treeData = vmNode;
    });
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    appendNode(node, data) {
      this.formModel = {};
      this.formModel.parent = data;
      this.actType = 'create';
    },
    editNode(node, data) {
      this.formModel = data;
      this.formModel.parent = node.parent.data;
      this.actType = 'editor';
    },
    removeNode(node, data) {},
    createNode() {},
    updateNode() {},
    openSelectParentNodeLayer() {
      console.log(Math.random());
      this.dialogVisible = true;
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
