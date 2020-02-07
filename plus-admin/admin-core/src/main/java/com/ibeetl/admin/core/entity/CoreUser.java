package com.ibeetl.admin.core.entity;

import com.ibeetl.admin.core.annotation.ElColumn;
import com.ibeetl.admin.core.util.enums.ElColumnType;
import com.ibeetl.admin.core.util.enums.JobTypeEnum;
import com.ibeetl.admin.core.util.enums.StateTypeEnum;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibeetl.admin.core.annotation.Dict;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.util.enums.CoreDictType;

/*
 *   用户实体
 *
 */

@Data
public class CoreUser extends BaseEntity {

  @NotNull(message = "ID不能为空", groups = ValidateConfig.UPDATE.class)
  @SeqID(name = ORACLE_CORE_SEQ_NAME)
  @AutoID
  protected Long id;
  // 删除标识
  @JsonIgnore protected Integer delFlag = 0;
  // 创建时间
  protected Date createTime;

  // 登录名，编号
  @NotBlank(message = "用户编号不能为空", groups = ValidateConfig.ADD.class)
  @Null(message = "用户编号不能为空", groups = ValidateConfig.UPDATE.class)
  private String code;

  // 用户姓名
  @NotBlank(message = "用户名不能为空")
  private String name;

  /*机构id*/
  private Long orgId;

  /*机构名称*/
  private String orgName;

  // 密码
  @JsonIgnore private String password;

  @Dict(type = CoreDictType.USER_STATE)
  private StateTypeEnum state;

  // 扩展例子
  @Dict(type = "job_type")
  private JobTypeEnum jobType0;

  @Dict(type = "job_type")
  private JobTypeEnum jobType1;

  private Date updateTime;

  /*用户的个人资料附件，保存到Core_File 表里*/
  private String attachmentId;
}
