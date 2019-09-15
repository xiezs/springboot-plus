package com.ibeetl.admin.core.conf;

import static com.ibeetl.admin.core.util.HttpRequestLocal.ACCESS_CURRENT_USER;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.CoreUserService;
import com.ibeetl.admin.core.util.HttpRequestLocal;
import com.ibeetl.admin.core.util.JoseJwtUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Configuration
public class MVCConf implements WebMvcConfigurer, InitializingBean {

  public static final String DEFAULT_APP_NAME = "开发平台";

  /** 系统名称,可以在application.properties中配置 app.name=xxx */
  //    @Value("${app.name}")
  //    String appName;

  private String mvcTestPath;

  @Autowired private Environment env;

  @Autowired private CoreUserService userService;

  @Autowired private BeetlGroupUtilConfiguration beetlGroupUtilConfiguration;

  @Autowired private GroupTemplate groupTemplate;

  @Autowired private RequestMappingHandlerAdapter adapter;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(new SessionInterceptor(userService))
        .addPathPatterns("/**");
    // super.addInterceptors(registry);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**");
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    registry.addFormatter(new DateFormatter("yyyy-MM-dd"));
  }

  @Override
  public void afterPropertiesSet() {
    this.mvcTestPath = env.getProperty("mvc.test.path");
    Map<String, Object> var = new HashMap<>(5);
    String appName = env.getProperty("app.name");
    if (appName == null) {
      var.put("appName", DEFAULT_APP_NAME);
    }
    var.put("jsVer", System.currentTimeMillis());
    groupTemplate.setSharedVars(var);
    /*自定义参数解析器配置，自定义应该优先级最高*/
    List<HandlerMethodArgumentResolver> argumentResolvers = CollUtil.newArrayList();
    argumentResolvers.add(new RequestBodyPlusProcessor(adapter.getMessageConverters()));
    argumentResolvers.addAll(adapter.getArgumentResolvers());
    adapter.setArgumentResolvers(argumentResolvers);
  }
}

class SessionInterceptor implements HandlerInterceptor {

  CoreUserService userService;

  public SessionInterceptor(CoreUserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
		HttpRequestLocal.set(request);
    if (StrUtil.containsAny(request.getRequestURI(), "/login", "/error", "/logout")) {
    	return true;
		}

    String token = HttpRequestLocal.getAuthorization();
    Map<String, Object> payload = JoseJwtUtil.parsePayload(token);
    if (payload.isEmpty()) {
      /*验证失败，无效jwt*/
      return false;
    }

    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView) {
    /* do nothing */
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    /* do nothing */
  }
}

/** 自定义SpringMVC的controller参数注解 {@link RequestBodyPlus} 的注入解析，用json path 的方式注入json请求的参数 */
class RequestBodyPlusProcessor extends AbstractMessageConverterMethodProcessor {

  private static final ThreadLocal<String> bodyLocal = ThreadLocal.withInitial(() -> "{}");

  protected RequestBodyPlusProcessor(List<HttpMessageConverter<?>> converters) {
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
