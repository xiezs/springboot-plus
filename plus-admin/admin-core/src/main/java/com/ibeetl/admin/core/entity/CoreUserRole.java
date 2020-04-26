package com.ibeetl.admin.core.entity;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

/*
 *  用户角色关系
 * gen by beetlsql 2016-11-22
 */
@Data
public class CoreUserRole extends BaseEntity {

  // 自增id
  @SeqID(name = "ORACLE_CORE_SEQ_NAME")
  @AutoID
  private Long id;
  // 授权机构id
  @NotNull(message = "授权机构不能为空")
  private Long orgId;
  // 授权角色id
  @NotNull(message = "授权角色不能为空")
  private Long roleId;
  // 用户id
  @NotNull(message = "授权用户不能为空")
  private Long userId;

  protected Date createTime;
}
