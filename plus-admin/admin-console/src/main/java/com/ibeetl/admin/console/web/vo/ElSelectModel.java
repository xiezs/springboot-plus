package com.ibeetl.admin.console.web.vo;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * element 对应的select组件数据模型
 */
@Data
public class ElSelectModel {

  private Object id;

  private String label;

  private Object value;

  /**
   * 额外的数据
   */
  private Map<String, Object> tail=new HashMap<>(3);

}
