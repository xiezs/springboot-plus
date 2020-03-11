package com.ibeetl.admin.core.util.enums;

/**
 * Interface DictTypeEnum ... 描述：只是一个标记接口，任何一个字典枚举类都要实现该接口<br>
 * 主要用于Jackson序列化处理
 *
 * @author Truma Created on 2020/1/12
 */
public interface DictTypeEnum<T> {
  String getValue();
  T findEnum(String value);
}
