package entity;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum CmsBlogTypeEnum {
  FN0("FN0", "普通功能", "function_type"),
  FN1("FN1", "含数据权限", "function_type"),
  FN2("FN2", "菜单功能", "function_type");
  private String value;
  private String name;
  private String type;

  CmsBlogTypeEnum(String value, String name, String type) {
    this.value = value;
    this.name = name;
    this.type = type;
  }

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
}
