package com.ibeetl.admin.console.model.vo;

import com.ibeetl.admin.console.annotation.ElColumn;

public class Table {

  @ElColumn(name = "ID", type = "string", visible = false)
  private String id;

  @ElColumn(name = "名字", type = "string")
  private String name;

  @ElColumn(name = "地址", type = "string")
  private String address;

  @ElColumn(name = "日期", type = "date")
  private String date;

  @ElColumn(name = "网址", type = "string")
  private String url;
}
