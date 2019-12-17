package com.ibeetl.admin.core.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专门用于前端UI下拉选择器的字典数据返回类型，与{@link CoreDict} 不同，它是业务中的类。
 * 用于数据库表的字典字段映射到本对象中的value字段<br>
 * pojo中字典字段必须使用该类定义<br>
 * 处理最终的字典值由{@link com.ibeetl.admin.core.service.CoreBaseService#processDictField} 方法提供<br/>
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
