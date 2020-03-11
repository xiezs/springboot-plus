package com.ibeetl.admin.core.entity;

import java.util.Date;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.ibeetl.admin.core.util.ValidateConfig;

/**
 * 描述: 字典
 *
 * @author : xiandafu
 */
@Data
public class CoreDict extends BaseEntity {

  @NotNull(message = "ID不能为空", groups = ValidateConfig.UPDATE.class)
  @SeqID(name = ORACLE_CORE_SEQ_NAME)
  @AutoID
  private Long id;

  private String value; // 数据值
  // 删除标识
  @JsonIgnore protected Integer delFlag = 0;
  // 创建时间
  protected Date createTime;

  @NotBlank(message = "字典类型不能为空", groups = ValidateConfig.ADD.class)
  @JsonView(TypeListView.class)
  private String type; // 类型

  @JsonView(TypeListView.class)
  @NotBlank(message = "字典类型描述不能为空")
  private String typeName; // 类型描述

  @NotBlank(message = "字典值不能为空", groups = ValidateConfig.ADD.class)
  @NotBlank(message = "字典值名称不能为空")
  private String name; // 标签名

  private Integer sort; // 排序
  private Long parent; // 父Id
  private String remark; // 备注

  private List<CoreDict> children;

  public interface TypeListView {}
}
