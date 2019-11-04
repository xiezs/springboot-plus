package com.ibeetl.admin.core.conf;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.apache.commons.digester3.RegexRules;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class SpringConfiguration {
  /**
   * 配置验证器
   *
   * @return validator
   */
  @Bean
  public Validator validator() {
    ValidatorFactory validatorFactory =
        Validation.byProvider(HibernateValidator.class)
            .configure()
            // 快速失败模式
            .failFast(true)
            // .addProperty( "hibernate.validator.fail_fast", "true" )
            .buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();
    return validator;
  }
  /**
   * 设置SpringMVC Controller方法参数验证器。 必须在controller类上使用 {@link
   * org.springframework.validation.annotation.Validated} 注解
   */
  @Bean
  public MethodValidationPostProcessor methodValidationPostProcessor(
      @Autowired Validator validator) {
    MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
    postProcessor.setValidator(validator);
    return postProcessor;
  }

  @Bean
  public DefaultErrorAttributes defaultErrorAttributes(){
    DefaultErrorAttributes defaultErrorAttributes = new DefaultErrorAttributes(true);
    return defaultErrorAttributes;
  }
}
