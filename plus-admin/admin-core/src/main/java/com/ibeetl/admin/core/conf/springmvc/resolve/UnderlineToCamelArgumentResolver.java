package com.ibeetl.admin.core.conf.springmvc.resolve;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import java.util.Iterator;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Class UnderlineToCamelArgumentResolver : <br>
 * 描述：处理请求参数的命名由下划线等转驼峰命名。<br>
 * 但是与另一个{@link DictTypeEnum} 枚举处理有冲突，所以交给前端处理命名转换
 *
 * @author 一日看尽长安花 Created on 2020/2/2
 */
public class UnderlineToCamelArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return true;
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer container,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {
    return handleParameterNames(parameter, webRequest);
  }

  private Object handleParameterNames(MethodParameter parameter, NativeWebRequest webRequest) {
    Object obj = ReflectUtil.newInstanceIfPossible(parameter.getParameterType());
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
    Iterator<String> paramNames = webRequest.getParameterNames();
    while (paramNames.hasNext()) {
      String paramName = paramNames.next();
      Object o = webRequest.getParameter(paramName);
      try {
        wrapper.setPropertyValue(StrUtil.toCamelCase(paramName), o);
      } catch (BeansException e) {
      }
    }
    return obj;
  }
}
