package com.ibeetl.admin.core.rbac;

import lombok.Data;

@Data
public class UserRoleInfo {
  private String name;
  private String avatar;
  private Long[] roles;
  private String introduction;
}
