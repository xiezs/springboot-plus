<!--
 * @Author: 一日看尽长安花
 * @since: 2020-03-08 11:03:14
 * @LastEditTime: 2020-03-17 20:54:30
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <el-upload
    ref="upload"
    :headers="headers"
    :multiple="multiple"
    :data="data"
    :name="name"
    :with-credentials="withCredentials"
    :show-file-list="showFileList"
    :drag="drag"
    :accept="accept"
    :on-preview="onPreview"
    :on-remove="onRemove"
    :on-success="onSuccess"
    :on-error="onError"
    :on-progress="onProgress"
    :on-change="onChange"
    :before-upload="beforeUpload"
    :before-remove="beforeRemove"
    :list-type="listType"
    :auto-upload="autoUpload"
    :file-list="fileList_d"
    :http-request="updateFiles"
    :disabled="disabled"
    :limit="limit"
    :on-exceed="onExceed"
    action="/core/file/uploadAttachment"
  >
    <template v-slot:default>
      <slot name="default"> </slot>
    </template>
    <template v-slot:trigger>
      <slot name="trigger"></slot>
    </template>
    <template v-slot:tip>
      <slot name="tip"></slot>
    </template>
  </el-upload>
</template>
<script>
import { getFileList } from '@/api/file';

export default {
  name: 'FileUpload',
  components: {},
  props: {
    fileBatchId: {
      type: String,
      default: null
    },
    bizType: {
      type: String,
      default: null
    },
    bizId: {
      type: String,
      default: null
    },
    headers: {
      type: Object,
      default() {
        return undefined;
      }
    },
    multiple: {
      type: Boolean,
      default: true
    },
    data: {
      type: Object,
      default() {
        return {
          fileBatchId: this.fileBatchId,
          bizType: this.bizType,
          bizId: this.bizId
        };
      }
    },
    name: {
      type: String,
      default: 'file'
    },
    withCredentials: {
      type: Boolean,
      default: false
    },
    showFileList: {
      type: Boolean,
      default: true
    },
    drag: {
      type: Boolean,
      default: false
    },
    accept: {
      type: String,
      default: ''
    },
    listType: {
      type: String,
      default: 'text'
    },
    autoUpload: {
      type: Boolean,
      default: false
    },
    fileList: {
      type: Array,
      default() {
        return [];
      }
    },
    disabled: {
      type: Boolean,
      default: false
    },
    limit: {
      type: Number,
      default: undefined
    },
    onPreview: {
      type: Function,
      default: undefined
    },
    onRemove: {
      type: Function,
      default: undefined
    },
    onSuccess: {
      type: Function,
      default: undefined
    },
    onError: {
      type: Function,
      default: undefined
    },
    onProgress: {
      type: Function,
      default: undefined
    },
    onChange: {
      type: Function,
      default: undefined
    },
    beforeUpload: {
      type: Function,
      default: undefined
    },
    beforeRemove: {
      type: Function,
      default: undefined
    },
    onExceed: {
      type: Function,
      default: undefined
    }
  },
  data() {
    return {
      fileList_d: this.fileList
    };
  },
  watch: {
    fileBatchId: {
      immediate: true,
      deep: true,
      handler: function(newVal, oldVal) {
        /** 只有当文件操作批次id与上一次不一样时进入 */
        this.loadFilelItems();
      }
    }
  },
  methods: {
    loadFilelItems() {
      if (!this.fileBatchId || this.fileBatchId.trim().length <= 0) {
        this.fileList_d = [];
        return;
      }
      getFileList({ fileBatchId: this.fileBatchId }).then(res => {
        const { code, data } = { ...res };
        this.fileList_d = data || [];
      });
    },
    updateFiles(params) {
      debugger;
      console.log(params);
    }
  }
};
</script>
