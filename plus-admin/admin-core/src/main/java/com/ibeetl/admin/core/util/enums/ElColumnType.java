package com.ibeetl.admin.core.util.enums;

import com.ibeetl.admin.core.annotation.ElColumn;

/** 注解{@link ElColumn#type()} 字段的常量值 */
public interface ElColumnType {
  /** 前端页面输入框 */
  String STRING = "string";

  /** 前端页面下拉选择器 */
  String DICT = "dict";

  /** 前端页面的日期选择器 */
  String DATE = "date";
}
