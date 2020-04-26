<!--
 * @Author: 一日看尽长安花
 * @since: 2020-04-06 19:31:15
 * @LastEditTime: 2020-04-26 12:40:27
 * @LastEditors: 一日看尽长安花
 * @Description: 使用时，必须指定一个key。不然多个组件之间会共用生命周期
 *  请保持jsonPaths和labels数组的长度一致，同时保持和级联器的层级个数一致
 *  如果jsonPaths是一个元素在其中，会提取出来作为唯一选中值。
 *  对于级联器的handleChange事件，会同时修改jsonPaths和labels中的节点值，
 *  确保级联器的效果和labels指定的传值参数
 -->
<template>
  <el-cascader
    :key="Math.random()"
    v-model="selectedValue"
    :props="props"
    :options="options"
    :clearable="true"
    :show-all-levels="false"
    :placeholder="placeholder"
    @change="handleChange"
  ></el-cascader>
</template>
<script>
export default {
  name: 'SpCascader',
  model: {
    prop: 'model',
    event: 'updateModel'
  },
  props: {
    /**
     * 因为插槽的传值是无法修改的，绕过这个机制
     * 插槽传值时往上加一层root节点就行了
     */
    model: {
      type: Object,
      required: true,
      default: function() {
        return {};
      }
    },
    /** 在model中表示级联器选中值的json path数组，参考job_type的使用 */
    jsonPaths: {
      type: Array,
      required: true,
      default: function() {
        return undefined;
      }
    },
    props: {
      type: Object,
      required: true,
      default: function() {
        return null;
      }
    },
    options: {
      type: Array,
      required: true,
      default: function() {
        return null;
      }
    },
    placeholder: {
      type: String,
      default: function() {
        return undefined;
      }
    },
    /**
     * 每一级对应的传值参数名，请按照顺序书写
     * */
    labels: {
      type: Array,
      required: true,
      default: function() {
        return null;
      }
    }
  },
  data() {
    return {
      selectedValue: []
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      for (let i = 0; i < this.jsonPaths.length; i++) {
        const jsonPath = this.jsonPaths[i];
        const val = this.$lodash.get(this.model, jsonPath);
        this.selectedValue.push(val);
      }
      if (this.selectedValue.length === 1) {
        this.selectedValue = this.selectedValue[0];
      }
    },
    handleChange() {
      let convertedValueObj = this.handleCascaderValue();
      let sp_cascader = this.model['sp_cascader'];
      convertedValueObj['sp_cascader'] = {
        ...convertedValueObj['sp_cascader'],
        ...sp_cascader
      };
      const updatedModel = this.$lodash.assignIn(
        {},
        this.model,
        convertedValueObj
      );
      this.$emit('updateModel', updatedModel);
    },
    handleCascaderValue() {
      const keyNames = this.labels;
      let selValArray = this.selectedValue || [];
      const index =
        selValArray.length - keyNames.length <= 0
          ? 0
          : selValArray.length - keyNames.length;
      let resObj = {};
      for (let i = index, j = 0; i < selValArray.length; i++, j++) {
        this.$lodash.set(resObj, 'sp_cascader.' + keyNames[j], selValArray[i]);
        this.$lodash.set(resObj, this.jsonPaths[j], selValArray[i]);
      }
      return resObj;
    }
  }
};
</script>
