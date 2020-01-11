package com.ibeetl.admin.core.util.enums;

import com.ibeetl.admin.core.annotation.ElColumn;

/** 注解{@link ElColumn#type()} 字段的常量值 */
public interface ElColumnType {
  /** 前端页面输入框 */
  String STRING = "string";

  /**
   * 不出现在搜索面板中，只出现在数据表格中。
   * 建议下拉选择器通过级联选择器实现
   * */
  String DICT = "dict";

  /** 前端页面的日期选择器 */
  String DATE = "date";
}
