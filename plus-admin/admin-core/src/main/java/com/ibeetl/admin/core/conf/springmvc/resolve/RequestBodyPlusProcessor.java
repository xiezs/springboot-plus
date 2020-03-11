package com.ibeetl.admin.core.conf.springmvc.resolve;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

/** 自定义SpringMVC的controller方法的参数注解 {@link RequestBodyPlus} 的注入解析， 用json path 的方式注入json请求的参数 */
public class RequestBodyPlusProcessor extends AbstractMessageConverterMethodProcessor {

  private static final ThreadLocal<String> bodyLocal = ThreadLocal.withInitial(() -> "{}");

  public RequestBodyPlusProcessor(List<HttpMessageConverter<?>> converters) {
    super(converters);
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(RequestBodyPlus.class);
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws Exception {
    parameter = parameter.nestedIfOptional();
    /*非json请求过滤*/
    Class<?> parameterClass = parameter.getNestedParameterType();
    if (!StrUtil.containsAny(
        webRequest.getHeader(HttpHeaders.CONTENT_TYPE), MediaType.APPLICATION_JSON_VALUE)) {
      return ReflectUtil.newInstanceIfPossible(parameterClass);
    }

    HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
    Assert.state(servletRequest != null, "No HttpServletRequest");
    ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);

    StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();

    String jsonBody;
    try {
      String readBody = stringHttpMessageConverter.read(String.class, inputMessage);
      /*每一个参数的注入都会读取一次输入流，但是request的输入流不可重复读，所以需要保持下来*/
      if (StrUtil.isBlank(readBody)) {
        jsonBody = bodyLocal.get();
      } else {
        bodyLocal.set(readBody);
        jsonBody = bodyLocal.get();
      }
    } catch (IOException e) {
      logger.error("Can't read request body by input stream : {}", e);
      jsonBody = bodyLocal.get();
    }

    RequestBodyPlus requestBodyPlus = parameter.getParameterAnnotation(RequestBodyPlus.class);
    JSON json = JSONUtil.parse(jsonBody);
    Object parseVal = json.getByPath(requestBodyPlus.value(), parameterClass);
    if (parseVal instanceof Map) {
      JSONObject jsonObject = JSONUtil.parseObj(parseVal);
      parseVal = JSONUtil.toBean(jsonObject, parameter.getNestedGenericParameterType(), true);
    } else if (parseVal instanceof List) {
      JSONArray jsonArray = JSONUtil.parseArray(parseVal);
      Type parameterType = TypeUtil.getTypeArgument(parameter.getNestedGenericParameterType());
      Class parameterTypeClass =
          null == parameterType ? Object.class : ClassUtil.loadClass(parameterType.getTypeName());
      parseVal = JSONUtil.toList(jsonArray, parameterTypeClass);
    }
    return parseVal;
  }

  @Override
  public boolean supportsReturnType(MethodParameter returnType) {
    return false;
  }

  @Override
  public void handleReturnValue(
      Object returnValue,
      MethodParameter returnType,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest) {}
}
