package com.ibeetl.admin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBodyPlus {
  /*写入一个json path。默认直接将json转换为被注解的参数的类型对象*/
  String value() default "";

  boolean required() default true;
}
