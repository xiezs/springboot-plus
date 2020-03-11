package com.ibeetl.admin.core.conf.springmvc.convert;

import cn.hutool.core.map.MapUtil;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToDictTypeEnumConverFactory implements ConverterFactory<String, DictTypeEnum> {
  private static final Map<Class, Converter> CONVERTERS = MapUtil.newHashMap();

  /**
   * 获取一个从 Integer 转化为 T 的转换器，T 是一个泛型，有多个实现
   *
   * @param targetType 转换后的类型
   * @return 返回一个转化器
   */
  @Override
  public <T extends DictTypeEnum> Converter<String, T> getConverter(Class<T> targetType) {
    Converter<String, T> converter = CONVERTERS.get(targetType);
    if (converter == null) {
      converter = new StringToDictTypeEnumConvert(targetType);
      CONVERTERS.put(targetType, converter);
    }
    return converter;
  }
}
