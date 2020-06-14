package com.ibeetl.admin.core.entity;

import com.ibeetl.admin.core.annotation.Dict;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.util.enums.CoreDictType;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;
import org.beetl.sql.core.annotatoin.UpdateIgnore;

/**
 * 系统菜单
 */
@NoArgsConstructor
@Data
public class CoreMenu extends BaseEntity implements Comparable {

  public static final String TYPE_SYSTEM = "MENU_S";

  public static final String TYPE_NAV = "MENU_N";

  public static final String TYPE_MENUITEM = "MENU_M";

  @NotNull(message = "ID不能为空", groups = ValidateConfig.UPDATE.class)
  @SeqID(name = ORACLE_CORE_SEQ_NAME)
  @AutoID
  protected Long id;

  // 创建时间
  @UpdateIgnore
  protected Date createTime;

  // 菜单代码
  @NotBlank(message = "菜单代码不能为空", groups = ValidateConfig.ADD.class)
  private String code;

  // 功能id
  private Long functionId;

  private CoreFunction relationFunction;

  // 类型  /*1 系统 2 导航 3 菜单项(与功能点有关)*/
  @NotNull(message = "菜单类型不能为空")
  @Dict(type = CoreDictType.MENU_TYPE)
  private String type;

  // 菜单名称
  @NotBlank(message = "菜单名称不能为空")
  private String name;

  // 上层菜单id
  @NotNull(message = "上层菜单不能为空")
  private Long parentMenuId;

  // 排序
  private Integer seq = Integer.MAX_VALUE;

  // 图标
  private String icon = "menu";

  private CoreMenu parent;

  private List<CoreMenu> children;

  private short delFlag;

  @Override
  public int compareTo(Object o) {

    if (!(o instanceof CoreMenu)) {
      throw new IllegalArgumentException("给定的对象不是同一个类型");
    }
    if (o == null) {
      throw new NullPointerException("给定的对象为NULL");
    }
    CoreMenu other = (CoreMenu) o;
//    <0 =0 >0
    if (this.seq == null) {
      this.seq = Integer.MAX_VALUE;
    }
    if (other.seq == null) {
      other.seq = Integer.MAX_VALUE;
    }
    return this.seq - other.seq;
  }

}
