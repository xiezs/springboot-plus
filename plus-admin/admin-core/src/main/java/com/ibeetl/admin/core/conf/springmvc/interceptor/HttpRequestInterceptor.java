package com.ibeetl.admin.core.conf.springmvc.interceptor;

import com.ibeetl.admin.core.util.HttpRequestLocal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class HttpRequestInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    HttpRequestLocal.set(request);
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    HttpRequestLocal.destory();
  }
}
