package com.ibeetl.admin.core.entity;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import lombok.Data;

@Data
public class CoreRouteMeta {

  /** 路由展示在菜单中显示的标题 */
  private String title;

  /** 在菜单中显示的图标 */
  private String icon;

  /** 当前路由可以访问的角色 */
  private List<Long> roles = CollUtil.<Long>newArrayList();
}
