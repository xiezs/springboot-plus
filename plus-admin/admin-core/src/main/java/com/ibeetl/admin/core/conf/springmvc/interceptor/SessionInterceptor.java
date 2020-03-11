package com.ibeetl.admin.core.conf.springmvc.interceptor;

import com.ibeetl.admin.core.service.CoreUserService;
import com.ibeetl.admin.core.util.HttpRequestLocal;
import com.ibeetl.admin.core.util.JoseJwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

  CoreUserService userService;

  public SessionInterceptor(CoreUserService userService) {
    this.userService = userService;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String token = HttpRequestLocal.getAuthorization();
    boolean isExpiration = JoseJwtUtil.verifyJwtJson(token);
    if (isExpiration) {
      /*验证失败，无效jwt*/
      return false;
    } else {
      token = JoseJwtUtil.refreshIssuedAtTime(token);
      response.setHeader(HttpHeaders.AUTHORIZATION, token);
    }
    return true;
  }
}
