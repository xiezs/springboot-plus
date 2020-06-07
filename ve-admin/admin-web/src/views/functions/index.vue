<!--
 * @Author: 一日看尽长安花
 * @since: 2020-05-30 12:53:38
 * @LastEditTime: 2020-06-07 19:37:11
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
            placeholder="请输入功能点名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="功能代码" prop="code">
          <el-input
            v-model="formModel.code"
            placeholder="请输入系统唯一功能点代码"
          ></el-input>
        </el-form-item>
        <el-form-item label="路由地址" prop="access_url">
          <el-input
            v-model="formModel.access_url"
            placeholder="请输入路由地址，可以输入动态路由"
          ></el-input>
          <!-- todo :disabled="actType === 'editor'"  理论上应该在创建后就禁止修改，但是开发时方便一下-->
        </el-form-item>
        <el-form-item label="组件位置">
          <el-input
            v-model="formModel.component"
            placeholder="请输入在前端中的组件地址，非页面可不填"
          ></el-input>
          <!-- todo :disabled="actType === 'editor'"  理论上应该在创建后就禁止修改，但是开发时方便一下-->
        </el-form-item>
        <el-form-item label="父功能" prop="parent">
          <el-input
            v-model="formModel.parent.name"
            placeholder="点击选择上一级功能点"
            readonly
            @focus="openSelectParentNodeLayer"
          ></el-input>
        </el-form-item>
        <el-form-item label="功能类型" prop="type">
          <el-select v-model="formModel.type" placeholder="请选择功能类型">
            <el-option label="导航访问" value="FN0"></el-option>
            <el-option label="功能点访问" value="FN1"></el-option>
            <el-option label="菜单访问" value="FN2"></el-option>
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
      :per-level-label="['parent']"
      :visible.sync="dialogVisible"
      :tree-data="treeData"
    >
    </sel-func-dialog>
  </div>
</template>
<script>
/** 功能点管理 */
import {
  funcs,
  createFuncNode,
  updateFuncNode,
  delFuncNodesByParent
} from '@/api/func';
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
      dialogVisible: false,
      rules: {
        name: { required: true, message: '请输入名称', trigger: 'blur' },
        code: { required: true, message: '请输入代码点', trigger: 'blur' },
        access_url: {
          required: true,
          message: '请输入访问路由，可使用动态路由',
          trigger: 'blur'
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
    removeNode(node, data) {
      const ids = this.getTreeBranchIds(node);
      this.$confirm('此操作将永久删除该功能点及其子功能点, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          delFuncNodesByParent({ ids: ids }).then(response => {
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
          createFuncNode(_that.formModel).then(response => {
            const { code, message, data } = { ...response };
            const _that = this;
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$message({
              message: '创建功能点成功',
              type: 'success',
              onClose: function(_instance) {
                // todo 可以用组件Mixin改写，或者直接挂载到Router原型上
                _that.$router.replace('/refresh');
                loading.close();
              }
            });
          });
        } else {
          updateFuncNode(_that.formModel).then(response => {
            const { code, message, data } = { ...response };
            const _that = this;
            const loading = this.$loading({
              lock: true,
              text: 'Loading',
              spinner: 'el-icon-loading',
              background: 'rgba(0, 0, 0, 0.7)'
            });
            this.$message({
              message: '更新功能点成功',
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
