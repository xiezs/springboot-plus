package com.ibeetl.admin.core.conf.springmvc.convert;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;

public class StringToDictTypeEnumConvert<T extends DictTypeEnum> implements Converter<String, T> {
  private Map<String, T> enumMap = MapUtil.newHashMap();

  public StringToDictTypeEnumConvert(Class<T> enumType) {
    T[] enums = enumType.getEnumConstants();
    for (T e : enums) {
      enumMap.put(e.getValue(), e);
    }
  }

  @Override
  public T convert(String source) {
    T t = enumMap.get(source);
    if (ObjectUtil.isNull(t)) {
      throw new IllegalArgumentException("无法匹配对应的枚举类型");
    }
    return t;
  }
}
