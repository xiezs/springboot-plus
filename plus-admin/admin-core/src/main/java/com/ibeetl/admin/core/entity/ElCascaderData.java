package com.ibeetl.admin.core.entity;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/** element-ui前端级联器的数据格式类 */
@Data
@NoArgsConstructor
public class ElCascaderData {
  private Object id;
  private String label;
  private Object value;
  private List<ElCascaderData> children;
  private Object tail;
}
