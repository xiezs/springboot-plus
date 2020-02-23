package com.ibeetl.admin.core.conf.beetl.resultmap;

import static cn.hutool.core.util.ClassUtil.isAssignable;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

/**
 * Class JavaFieldProperty : <br>
 * 描述：对一个java bean 中的字段的简单描述
 *
 * @author 一日看尽长安花 Created on 2020/2/10
 */
public class JavaFieldProperty {
  /** 对应的字段 */
  Field field;
  /** json映射中的key */
  String key;
  /*对应字段的属性描述*/
  PropertyDescriptor propertyDescriptor;
  /** 字段所在的bean type */
  Class beanType;
  /** 如果是集合字段，集合的泛型类型 */
  Type listElementType;
  /** 是否是集合字段 */
  boolean isList;

  static final JavaFieldProperty UNKOWN = new JavaFieldProperty();

  private JavaFieldProperty() {}

  public JavaFieldProperty(Field field) {
    this.field = field;
    Type genericType = this.getField().getGenericType();
    listElementType = TypeUtil.getTypeArgument(genericType);
    Class rawType = TypeUtil.getClass(genericType);
    if (isAssignable(Collection.class, rawType)) {
      isList = true;
    } else if (isAssignable(Set.class, rawType)) {
      isList = true;
    } else {
      isList = false;
    }
    beanType = this.getField().getDeclaringClass();
    propertyDescriptor = BeanUtil.getPropertyDescriptor(beanType, field.getName());
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public PropertyDescriptor getPropertyDescriptor() {
    return propertyDescriptor;
  }

  public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
    this.propertyDescriptor = propertyDescriptor;
  }

  public Class getBeanType() {
    return beanType;
  }

  public void setBeanType(Class beanType) {
    this.beanType = beanType;
  }

  public Type getListElementType() {
    return listElementType;
  }

  public void setListElementType(Type listElementType) {
    this.listElementType = listElementType;
  }

  public boolean isList() {
    return isList;
  }

  public void setIsList(boolean isList) {
    this.isList = isList;
  }
}
