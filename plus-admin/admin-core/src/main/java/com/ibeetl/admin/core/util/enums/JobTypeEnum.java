package com.ibeetl.admin.core.util.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum JobTypeEnum implements DictTypeEnum {
  MANAGER("管理岗位", "JT_01", "job_type"),
  TECHNOLOGY("技术岗位", "JT_02", "job_type"),
  BOARD_OF_DIRECTORS("董事会", "JT_S_01", "job_sub_managment_type"),
  SECRETARY("秘书", "JT_S_02", "job_sub_managment_type"),
  TECHNICAL_MANAGER("技术经理", "JT_S_03", "job_dev_sub_type"),
  PROGRAMMER("程序员", "JT_S_04", "job_dev_sub_type");

  private String name;
  private String value;
  private String type;

  JobTypeEnum(String name, String value, String type) {
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
