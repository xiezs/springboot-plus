package com.ibeetl.admin.core.conf;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import com.ibeetl.admin.core.conf.springmvc.convert.DateConditionalGenericConverter;
import com.ibeetl.admin.core.conf.springmvc.convert.StringToDictTypeEnumConverterFactory;
import com.ibeetl.admin.core.conf.springmvc.interceptor.HttpRequestInterceptor;
import com.ibeetl.admin.core.conf.springmvc.interceptor.SessionInterceptor;
import com.ibeetl.admin.core.conf.springmvc.resolve.RequestBodyPlusProcessor;
import com.ibeetl.admin.core.service.CoreUserService;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 切勿在此配置类中向SpringMVC中添加bean。 也就是不要 @Bean这类方法。 会出现无法ServletContext注入null，因为父接口的原因
 */
@Configuration
public class SpringWebMvcConfigurer implements WebMvcConfigurer, InitializingBean {

  public static final String DEFAULT_APP_NAME = "开发平台";

  /**
   * 系统名称,可以在application.properties中配置 app.name=xxx
   */
  //    @Value("${app.name}")
  //    String appName;

  private String mvcTestPath;

  @Autowired
  private Environment env;

  @Autowired
  private CoreUserService userService;

  @Autowired
  private BeetlGroupUtilConfiguration beetlGroupUtilConfiguration;

  @Autowired
  private GroupTemplate groupTemplate;

  @Autowired
  private RequestMappingHandlerAdapter adapter;

  /**
   * 添加拦截器
   *
   * @param registry
   *     拦截器的注册器
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(new HttpRequestInterceptor()).addPathPatterns("/**");
    registry
        .addInterceptor(new SessionInterceptor(userService))
        .excludePathPatterns("/**/login", "/**/error", "/**/logout")
        .addPathPatterns("/**");
  }

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    for (HttpMessageConverter<?> converter : converters) {
      if (converter instanceof MappingJackson2HttpMessageConverter) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
        mappingJackson2HttpMessageConverter.setDefaultCharset(CharsetUtil.CHARSET_UTF_8);
        List<MediaType> supportedMediaTypes = mappingJackson2HttpMessageConverter.getSupportedMediaTypes();
        List<MediaType> customMediaTypes = CollUtil.newArrayList(supportedMediaTypes);
        customMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        customMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        customMediaTypes.add(MediaType.parseMediaType("text/plain;charset=UTF-8"));
        customMediaTypes.add(MediaType.parseMediaType("application/xml;charset=UTF-8"));
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(customMediaTypes);
      }
    }
  }

  /**
   * 增加跨域映射
   *
   * @param registry
   *     跨域映射注册器
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry
        .addMapping("/**")
        .allowedOrigins("*")
        .allowCredentials(true)
        .allowedMethods(GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name());
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

  }

  /**
   * SpringMVC的请求响应消息的转换格式器
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {

    registry.addFormatter(new DateFormatter("yyyy-MM-dd HH:mm:ss"));
    registry.addFormatter(new DateFormatter("yyyy-MM-dd"));
    /*converter 在json传参时无效*/
    registry.addConverterFactory(new StringToDictTypeEnumConverterFactory());
    registry.addConverter(new DateConditionalGenericConverter());
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
