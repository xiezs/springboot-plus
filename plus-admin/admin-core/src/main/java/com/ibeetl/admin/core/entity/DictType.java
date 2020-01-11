package com.ibeetl.admin.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于数据表格的显示<br/>
 * pojo中字典字段必须使用该类定义<br>
 * 由{@link com.ibeetl.admin.core.service.CoreBaseService#processDictField} 方法转换<br/>
 */
@Data
@NoArgsConstructor
public class DictType {
  private String value;
  private String name;
  private String type;

  public DictType(String value) {
    this.value = value;
  }
  public DictType(String type, String value) {
    this(value);
    this.type = type;
  }
}
