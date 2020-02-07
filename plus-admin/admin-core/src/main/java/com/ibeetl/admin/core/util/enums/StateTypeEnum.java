package com.ibeetl.admin.core.util.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum StateTypeEnum implements DictTypeEnum {
  S0("禁用", "S0", "user_state"),
  S1("启用", "S1", "job_type");

  private String name;
  private String value;
  private String type;

  StateTypeEnum(String name, String value, String type) {
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
