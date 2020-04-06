package com.ibeetl.admin.console.web.query;

import com.ibeetl.admin.core.web.query.PageParam;
import lombok.Data;

/**
 * 用户管理》操作角色
 * */
@Data
public class UserRoleQuery extends PageParam {

  private Long roleId;

  private Long orgId;

  private Long userId;
}
