package com.ibeetl.admin.core.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.beetl.sql.core.annotatoin.EnumMapping;

/**
 * 描述: 角色字典枚举
 *
 * @author : Administrator
 */
@EnumMapping("value")
public enum RoleTypeEnum implements DictTypeEnum<RoleTypeEnum> {

  /** 操作角色 */
  ACCESS("R0"),
  /** 工作流角色 */
  WORKFLOW("R1");

  private String value;

  RoleTypeEnum(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @JsonCreator
  @Override
  public RoleTypeEnum findEnum(String value) {
    RoleTypeEnum[] values = values();
    for (RoleTypeEnum typeEnum : values) {
      if (typeEnum.value.equals(value)) {
        return typeEnum;
      }
    }
    return null;
  }
}
