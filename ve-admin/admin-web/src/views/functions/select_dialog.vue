<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-06-14 15:23:59
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
      ref="tree"
      :data="treeData"
      node-key="id"
      :label="label"
      :show-checkbox="true"
      default-expand-all
      :check-strictly="true"
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
      <el-button type="primary" @click="saveSelect">确 定</el-button>
    </div>
  </el-dialog>
</template>
<script>
/** 功能点管理 */
import { funcs } from '@/api/admin_funcs';

export default {
  name: 'SelFuncDialog',
  components: {},
  model: {
    prop: 'value',
    event: 'updateValue'
  },
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
    },
    label: {
      type: String,
      default: 'name'
    },
    perLevelLabel: {
      type: Array,
      required: true
    },
    value: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      filterText: ''
    };
  },
  watch: {
    filterText(nv, ov) {
      this.$refs.tree.filter(nv);
    }
  },
  methods: {
    filterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    saveSelect() {
      // 包括半选节点
      const selNodes = this.$refs.tree.getCheckedNodes(false, true);
      if (selNodes && selNodes.length === 0) {
        this.$emit('update:visible', false);
      }
      if (selNodes && selNodes.length !== 1) {
        this.$message({
          message: '只能选择一个节点',
          type: 'warning'
        });
        return;
      }
      let _node = this.$refs.tree.getNode(selNodes[0]);
      let resObj = {};
      for (let i = this.perLevelLabel.length - 1; i >= 0; i--) {
        const _label = this.perLevelLabel[i];
        this.$lodash.set(resObj, _label, _node.data);
        _node = _node.parent;
      }
      const updateValue = this.$lodash.assignIn({}, this.value, resObj);
      this.$emit('updateValue', updateValue);
      this.$emit('update:visible', false);
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
.el-dialog {
  height: 90%;
  overflow-y: scroll;
  top: -10vh;
  margin-bottom: -5vh;
}
.el-tree-node {
  margin-top: 5px;
}
</style>
