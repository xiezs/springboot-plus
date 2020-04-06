package com.ibeetl.admin.core.conf.beetl.resultmap;

import java.sql.JDBCType;

public class DBColumnProperty {
  /** json映射中value */
  String value;
  /** SQL select 中 as 别名 */
  String columnLabel;

  /** 对应resultset列索引位置，从1开始 */
  int columnIndex;

  /** 对应的jdbc类型 */
  JDBCType jdbcType;

  /**当前resultset是否存在对应的列，不存在就不需要获取，避免报错*/
  boolean hasEffective;

  public DBColumnProperty(String columnLabel) {
    this.columnLabel = columnLabel;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getColumnLabel() {
    return columnLabel;
  }

  public void setColumnLabel(String columnLabel) {
    this.columnLabel = columnLabel;
  }

  public int getColumnIndex() {
    return columnIndex;
  }

  public void setColumnIndex(int columnIndex) {
    this.columnIndex = columnIndex;
  }

  public JDBCType getJdbcType() {
    return jdbcType;
  }

  public void setJdbcType(JDBCType jdbcType) {
    this.jdbcType = jdbcType;
  }

  public boolean isHasEffective() {
    return hasEffective;
  }

  public void setHasEffective(boolean hasEffective) {
    this.hasEffective = hasEffective;
  }
}
