package com.ibeetl.admin.core.util;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil {

  @Autowired private ApplicationContext applicationContext;

  private static SpringUtil springUtil;

  @PostConstruct
  public void init() {
    springUtil=this;
    springUtil.applicationContext = this.applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return springUtil.applicationContext;
  }

  public static <T> T getBean(Class<T> cls, Object... args) {
    return getApplicationContext().getBean(cls, args);
  }

  public static <T> T getBean(Class<T> cls) {
    return getApplicationContext().getBean(cls);
  }
}
