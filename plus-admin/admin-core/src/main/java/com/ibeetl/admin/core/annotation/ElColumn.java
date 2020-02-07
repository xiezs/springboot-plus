package com.ibeetl.admin.core.annotation;

import cn.hutool.core.util.StrUtil;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 前端 element-ui 中的数据表格与vo对象字段对应的元数据信息的注解
 *
 * @author 一日看尽长安花
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElColumn {

  /**
   * 数据表格列头显示名称
   *
   * @return
   */
  String name();

  /**
   * 该列的类型。查看 {@link com.ibeetl.admin.core.util.enums.ElColumnType}
   *
   * @return
   */
  String type();

  /**
   * 在数据表格中取json的path。参照json path技术。
   *
   * @return String
   */
  String jsonPath() default StrUtil.EMPTY;

  /**
   * 开启element-ui 中数据表格列的手动排序
   *
   * @return
   */
  boolean sortable() default false;

  /**
   * 是否在前端页面的查询条件区显示
   *
   * @return
   */
  boolean isShowSearchPanel() default true;

  /**
   * 是否在前端页面的数据表格列中显示
   *
   * @return
   */
  boolean isShowTablePanel() default true;

  /**
   * 是否在前端页面的编辑窗显示
   *
   * @return
   */
  boolean isShowEditorPanel() default true;

  /**
   * 是否在表单中是必填。暂时没做规则的生成功能，暂定没有
   *
   * @return
   */
  boolean required() default false;
}
