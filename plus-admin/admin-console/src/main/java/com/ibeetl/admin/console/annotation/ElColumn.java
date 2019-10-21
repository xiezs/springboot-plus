package com.ibeetl.admin.console.annotation;

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

  String name();

  String type();

  boolean sortable() default false;

  boolean visible() default true;

  boolean required() default false;
}
