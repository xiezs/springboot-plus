package com.ibeetl.admin.core.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum StateTypeEnum implements DictTypeEnum<StateTypeEnum> {
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

  @JsonValue
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

  @JsonCreator
  @Override
  public StateTypeEnum findEnum(String value) {
    StateTypeEnum[] values = values();
    for (StateTypeEnum typeEnum : values) {
      if (typeEnum.value.equals(value)) {
        return typeEnum;
      }
    }
    return null;
  }
}
