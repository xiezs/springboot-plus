package com.ibeetl.admin.core.conf.springmvc.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * Class DateConditionalGenericConverter : <br/>
 * 描述：时间戳转日期{@link Date} ，由于前端可能传字符串的时间戳，所以用了这个条件转换器
 * @author 一日看尽长安花
 * Created on 2020/3/9
 */
public class DateConditionalGenericConverter implements ConditionalGenericConverter {
  @Override
  public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
    boolean targetTypeMatches =
        Optional.ofNullable(targetType.getResolvableType())
            .map(ResolvableType::resolve)
            .map(resolvableType -> resolvableType.equals(Date.class))
            .orElse(false);
    return targetTypeMatches;
  }

  @Override
  public Set<ConvertiblePair> getConvertibleTypes() {

    return CollUtil.newHashSet(
        new ConvertiblePair(String.class, Date.class), new ConvertiblePair(Long.class, Date.class));
  }

  @Override
  public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
    return DateUtil.date(Convert.toLong(source));
  }
}
