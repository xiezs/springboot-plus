package com.ibeetl.admin.core.conf;

import static cn.hutool.system.SystemUtil.LINE_SEPRATOR;
import static java.lang.String.format;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeetl.admin.core.util.FormFieldException;
import com.ibeetl.admin.core.util.PlatformException;
import com.ibeetl.admin.core.web.JsonResult;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * 自定义的全局错误页面
 *
 * @author lijiazhi
 */
@Controller
public class CustomErrorController extends AbstractErrorController {

  private static final String ERROR_PATH = "/error";

  private Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

  private final DefaultErrorAttributes defaultErrorAttributes;

  @Autowired ObjectMapper objectMapper;

  public CustomErrorController(DefaultErrorAttributes defaultErrorAttributes) {
    super(defaultErrorAttributes);
    this.defaultErrorAttributes = defaultErrorAttributes;
  }

  @RequestMapping(ERROR_PATH)
  public ModelAndView getErrorPath(HttpServletRequest request, HttpServletResponse response) {
    Throwable cause = getRealException(request);
    Map<String, Object> errorInfo = wrapErrorInfo(request);
    // 后台打印日志信息方方便查错
    prettyLog(errorInfo);
    response.setStatus(Convert.toInt(errorInfo.get("status")));
    if (cause instanceof PlatformException) {
      return hanlderPlatformException(errorInfo, response);
    } else if (cause instanceof ConstraintViolationException) {
      return hanlderConstraintViolationException(errorInfo, request, response);
    } else {
      return hanlderGeneralException(errorInfo, response);
    }
  }

  private ModelAndView hanlderGeneralException(
      Map<String, Object> errorInfo, HttpServletResponse response) {
    ModelAndView modelAndView = handlerHtml(errorInfo);
    if (modelAndView != null) {
      modelAndView.addObject("errorMessage", "服务器内部错误，请联系管理员");
    } else {
      handlerAjax(errorInfo, response);
    }
    return modelAndView;
  }

  private ModelAndView hanlderConstraintViolationException(
      Map<String, Object> errorInfo, HttpServletRequest request, HttpServletResponse response) {
    ModelAndView modelAndView = handlerHtml(errorInfo);
    if (modelAndView != null) {
      modelAndView.addObject("errorMessage", "服务器内部错误，请联系管理员");
      logger.error("方法参数校验失败，请查看上述详细信息");
    } else {
      if (Convert.toInt(errorInfo.get("status")).equals(HttpStatus.NOT_FOUND.value())) {
        writeJson(response, JsonResult.http404(errorInfo.get("path")));
      } else {
        ConstraintViolationException constraintViolationException =
            (ConstraintViolationException) getRealException(request);
        Set<ConstraintViolation<?>> constraintViolations =
            constraintViolationException.getConstraintViolations();
        List<String> messages = CollUtil.<String>newArrayList();
        for (ConstraintViolation<?> violation : constraintViolations) {
          String msg = violation.getPropertyPath().toString() + violation.getMessage();
          messages.add(msg);
        }
        writeJson(response, JsonResult.fail(messages));
      }
    }
    return modelAndView;
  }

  private ModelAndView hanlderPlatformException(
      Map<String, Object> errorInfo, HttpServletResponse response) {
    ModelAndView modelAndView = handlerHtml(errorInfo);
    if (modelAndView != null) {
      modelAndView.addObject("errorMessage", errorInfo.get("message"));
    } else {
      handlerAjax(errorInfo, response);
    }
    return modelAndView;
  }

  /**
   * 通用处理页面请求方法
   *
   * @param errorInfo
   * @return
   */
  protected ModelAndView handlerHtml(Map<String, Object> errorInfo) {
    ModelAndView view = null;
    if (!Convert.toBool(errorInfo.get("isAjax"))) {
      view = new ModelAndView("/error.html");
      view.addAllObjects(errorInfo);
      view.addObject("filedErrors", errorInfo.get("errors"));
      view.addObject("cause", errorInfo.get("exception"));
      view.addObject("requestPath", errorInfo.get("path"));
    }
    return view;
  }

  protected void handlerAjax(Map<String, Object> errorInfo, HttpServletResponse response) {
    if (Convert.toInt(errorInfo.get("status")).equals(HttpStatus.NOT_FOUND.value())) {
      writeJson(response, JsonResult.http404(errorInfo.get("path")));
    } else {
      writeJson(
          response, JsonResult.failMessage(errorInfo.getOrDefault("message", "无错误信息").toString()));
    }
  }

  /**
   * 提取errorAttributes 中的错误信息，包括：<br>
   * timestamp：时间<br>
   * status：http响应码<br>
   * error：响应码的原因<br>
   * exception：异常类名<br>
   * errors：controller可能的校验错误对象集合<br>
   * message：controller的错误信息<br>
   * trace: 异常的堆栈信息<br>
   * path：请求路径<br>
   *
   * @param request
   * @return
   */
  protected Map<String, Object> wrapErrorInfo(HttpServletRequest request) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(request, true);
    errorAttributes.put("isAjax", isJsonRequest(request));
    return Collections.unmodifiableMap(errorAttributes);
  }

  protected void prettyLog(Map errorInfo) {
    Object path = errorInfo.get("path");
    Object status = errorInfo.get("status");
    Object message = errorInfo.get("message");
    Object errors = errorInfo.get("errors");
    Object cause = errorInfo.get("exception");
    StringBuilder log = new StringBuilder();
    log.append(SystemUtil.get(LINE_SEPRATOR))
        .append(StrUtil.format("┏━━━━ response status: <{}> ━━━━", status))
        .append(SystemUtil.get(LINE_SEPRATOR))
        .append(StrUtil.format("┣━━━━ error message: <{}> ━━━━", message))
        .append(SystemUtil.get(LINE_SEPRATOR))
        .append(StrUtil.format("┣━━━━ error fileds: <{}> ━━━━", errors))
        .append(SystemUtil.get(LINE_SEPRATOR))
        .append(StrUtil.format("┣━━━━ error cause: <{}> ━━━━", cause))
        .append(SystemUtil.get(LINE_SEPRATOR))
        .append(StrUtil.format("┗━━━━ request path <{}> error log.━━━━", path));
    logger.error(log.toString());
  }

  /**
   * json请求，要么是.json后缀的请求，要么是http请求报文中规定的json请求
   *
   * @param request
   * @return
   */
  protected boolean isJsonRequest(HttpServletRequest request) {
    String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
    if (requestUri != null && requestUri.endsWith(".json")) {
      return true;
    } else {
      return (request.getHeader("Accept").contains("application/json")
          || (request.getHeader("X-Requested-With") != null
              && request.getHeader("X-Requested-With").contains("XMLHttpRequest")));
    }
  }

  /**
   * json响应的输出流方式
   *
   * @param response
   * @param error
   */
  protected void writeJson(HttpServletResponse response, JsonResult error) {
    response.addHeader(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
    response.addHeader(HttpHeaders.ACCEPT_CHARSET, CharsetUtil.UTF_8);
    try {
      response.getWriter().write(objectMapper.writeValueAsString(error));
    } catch (IOException e) {
      // ignore
    }
  }

  /**
   * 获取真正的异常，而不是被tomcat等包装的异常
   *
   * @param request
   * @return
   */
  protected Throwable getRealException(HttpServletRequest request) {
    Throwable error = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if (error != null) {
      while (error instanceof ServletException && error.getCause() != null) {
        error = error.getCause();
      }
    }
    return error;
  }

  @Override
  public String getErrorPath() {
    return ERROR_PATH;
  }
}
