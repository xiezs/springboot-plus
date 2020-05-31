package com.ibeetl.admin.core.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum FunctionTypeEnum implements DictTypeEnum<FunctionTypeEnum> {
  FN0("FN0", "导航访问", "function_type"),
  FN1("FN1", "功能访问", "function_type"),
  FN2("FN2", "菜单访问", "function_type");

  private String value;

  private String name;

  private String type;

  FunctionTypeEnum(String value, String name, String type) {

    this.value = value;
    this.name = name;
    this.type = type;
  }

  @JsonValue
  public String getValue() {

    return value;
  }


  public void setValue(String value) {

    this.value = value;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public String getType() {

    return type;
  }

  public void setType(String type) {

    this.type = type;
  }

  @JsonCreator
  @Override
  public FunctionTypeEnum findEnum(String value) {

    FunctionTypeEnum[] values = values();
    for (FunctionTypeEnum typeEnum : values) {
      if (typeEnum.value.equals(value)) {
        return typeEnum;
      }
    }
    return null;
  }
}
