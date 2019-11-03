package com.ibeetl.admin.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.ibeetl.admin.core.conf.SpringWebMvcConfigurer;

/**
 * 保留用户会话，以方便在业务代码任何地方调用 {@link SpringWebMvcConfigurer}
 *
 * @author lijiazhi
 */
@Component
public final class HttpRequestLocal {
  /*当前用户会话*/
  public static final String ACCESS_CURRENT_USER = "core:user";
  /*当前登录用户所在部门*/
  public static final String ACCESS_CURRENT_ORG = "core:currentOrg";
  /*用户可选部门*/
  public static final String ACCESS_USER_ORGS = "core:orgs";

  public HttpRequestLocal() {}

  private static final ThreadLocal<HttpServletRequest> requests =
      ThreadLocal.withInitial(() -> null);

  public static void setAccessCurrentOrg(CoreOrg org) {
    getHttpSession().setAttribute(ACCESS_CURRENT_ORG, org);
  }

  public static CoreOrg getAccessCurrentOrg() {
    return (CoreOrg) getHttpSession().getAttribute(ACCESS_CURRENT_ORG);
  }

  public static void setAccessUserOrgs(List<CoreOrg> orgs) {
    getHttpSession().setAttribute(ACCESS_USER_ORGS, orgs);
  }

  public static List<CoreOrg> getAccessUserOrgs() {
    return Convert.toList(CoreOrg.class, getHttpSession().getAttribute(ACCESS_USER_ORGS));
  }

  public static void setAccessCurrentUser(CoreUser user) {
    getHttpSession().setAttribute(ACCESS_CURRENT_USER, user);
  }

  public static CoreUser getAccessCurrentUser() {
    return (CoreUser) getHttpSession().getAttribute(ACCESS_CURRENT_USER);
  }

  public static void setLoginerInfo(CoreUser user, CoreOrg org, List<CoreOrg> orgs) {
    setAccessCurrentUser(user);
    setAccessCurrentOrg(org);
    setAccessUserOrgs(orgs);
    getHttpSession().setAttribute("clientIP", getIpAddr(get()));
  }

  public static void clearAllSession() {
    getHttpSession().removeAttribute(ACCESS_CURRENT_USER);
    getHttpSession().removeAttribute(ACCESS_CURRENT_ORG);
    getHttpSession().removeAttribute(ACCESS_USER_ORGS);
  }

  public static Cookie getCookieByName(String cookieName) {
    Assert.notNull(cookieName);
    Cookie[] cookies = get().getCookies();
    return Arrays.stream(cookies)
        .filter(cookie -> StrUtil.equals(cookie.getName(), cookieName))
        .findFirst()
        .orElse(null);
  }

  public static Object getSessionValue(String attr) {
    return getHttpSession().getAttribute(attr);
  }

  public static void setSessionValue(String attr, Object obj) {
    get().getSession().setAttribute(attr, obj);
  }

  public static String getAuthorization() {
    return get().getHeader(HttpHeaders.AUTHORIZATION);
  }

  public static Object getRequestValue(String attr) {
    return get().getAttribute(attr);
  }

  public static void setRequestValue(String attr, Object obj) {
    get().setAttribute(attr, obj);
  }

  /** @return 请求的uri，域名与参数之间的那部分 */
  public static String getRequestURI() {
    return get().getRequestURI();
  }

  /** @return 获取客户端ip */
  public static String getRequestIP() {
    return getIpAddr(get());
  }

  public static void set(HttpServletRequest request) {
    requests.set(request);
  }

  public static HttpServletRequest get() {
    return requests.get();
  }

  public static HttpSession getHttpSession() {
    return get().getSession();
  }

  /**
   * 获取当前网络ip
   *
   * @param request
   * @return
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
        // 根据网卡取本机配置的IP
        InetAddress inet = null;
        try {
          inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        ipAddress = inet.getHostAddress();
      }
    }
    // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
    if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length() = 15
      if (ipAddress.indexOf(",") > 0) {
        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
      }
    }
    return ipAddress;
  }
}
