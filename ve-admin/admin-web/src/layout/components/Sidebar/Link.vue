<!--
 * @Author: 一日看尽长安花
 * @since: 2019-09-04 20:55:14
 * @LastEditTime: 2019-10-30 19:53:28
 * @LastEditors: 一日看尽长安花
 * @Description:
 -->
<template>
  <!-- eslint-disable vue/require-component-is -->
  <!-- 该is特性不要使用官网的v-bind:is -->
  <component v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script>
import { isExternal } from '@/utils/validate';

export default {
  props: {
    to: {
      type: String,
      required: true
    }
  },
  methods: {
    linkProps(url) {
      if (isExternal(url)) {
        return {
          is: 'a',
          href: url,
          target: '_blank',
          rel: 'noopener'
        };
      }
      return {
        is: 'router-link',
        to: url
      };
    }
  }
};
</script>
