package com.ibeetl.admin.core.entity;

import java.util.Date;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.annotatoin.UpdateIgnore;

import com.ibeetl.admin.core.annotation.Dict;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.util.enums.CoreDictType;

@Data
public class CoreFunction extends BaseEntity {

  @NotNull(message = "ID不能为空", groups = ValidateConfig.UPDATE.class)
  @SeqID(name = ORACLE_CORE_SEQ_NAME)
  @AutoID
  protected Long id;

  @NotBlank
  private String code;

  @NotBlank
  private String name;

  private String accessUrl;

  private String component;

  @NotBlank
  private Long parentId;

  @UpdateIgnore
  private Date createTime;

  private short delFlag;

  @Dict(type = CoreDictType.FUNCTION_TYPE)
  @NotBlank
  private String type;

  private List<CoreFunction> children;

  public boolean hasParent() {

    return this.parentId != null && this.parentId > 0;
  }

}
