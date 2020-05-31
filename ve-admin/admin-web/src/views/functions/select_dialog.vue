<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-05-31 21:28:18
 * @LastEditors: 一日看尽长安花
 * @Description:
-->
<template>
  <el-dialog
    id="sel-func-dialog"
    title="功能点选择"
    :visible="visible"
    @update:visible="$emit('update:visible', $event)"
  >
    <el-input v-model="filterText" placeholder="输入关键字进行过滤"> </el-input>
    <el-tree
      :key="Math.random()"
      ref="tree"
      :data="treeData"
      node-key="id"
      :show-checkbox="true"
      default-expand-all
      :expand-on-click-node="false"
      :filter-node-method="filterNode"
    >
      <template v-slot="{ node: node, data: data }">
        <div :class="['sp-tree_node', 'sp-tree_node_type--' + data.type]">
          <span>{{ data.name }}</span>
        </div>
      </template>
    </el-tree>
    <div slot="footer" class="dialog-footer">
      <el-button
        type="primary"
        @click="dialogFormVisible = false"
      >确 定</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
/** 功能点管理 */
import { funcs } from '@/api/func';

export default {
  name: 'SelFuncDialog',
  components: {},
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    treeData: {
      type: Array,
      default: function() {
        return [];
      }
    }
  },
  data() {
    return {
      filterText: ''
    };
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    }
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    }
  }
};
</script>
<style lang="scss" scoped>
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
