package com.ibeetl.admin.core.web;

import cn.hutool.core.map.MapUtil;
import com.ibeetl.admin.core.util.JoseJwtUtil;
import java.util.Map;

/**
 * 描述: json格式数据返回对象，使用CustomJsonResultSerializer 来序列化
 *
 * @author : lijiazhi
 */
public class JsonResult<T> {

  /**
   * 建议标准http响应码
   * */
  private String code;

  /**
   * 自定义信息
   * */
  private String message;

  /**
   * 携带数据
   * */
  private T data;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "code=" + code + " message=" + message + " data=" + data;
  }

  public static <T> JsonResult<T> fail() {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.FAIL.getCode());
    ret.setMessage(JsonReturnCode.FAIL.getDesc());
    return ret;
  }

  public static <T> JsonResult<T> fail(T data) {
    JsonResult<T> ret = JsonResult.fail();
    ret.setData(data);
    return ret;
  }

  public static <T> JsonResult<T> failMessage(String msg) {
    JsonResult<T> ret = JsonResult.fail();
    ret.setMessage(msg);
    return ret;
  }

  public static <T> JsonResult<T> successMessage(String msg) {
    JsonResult<T> ret = JsonResult.success();
    ret.setMessage(msg);
    return ret;
  }

  public static <T> JsonResult<T> success() {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.SUCCESS.getCode());
    ret.setMessage(JsonReturnCode.SUCCESS.getDesc());
    return ret;
  }

  public static <T> JsonResult<T> success(T data) {
    JsonResult<T> ret = JsonResult.success();
    ret.setData(data);
    return ret;
  }

  public static <T> JsonResult<T> http404(T data) {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.NOT_FOUND.getCode());
    ret.setMessage(JsonReturnCode.NOT_FOUND.getDesc());
    ret.setData(data);
    return ret;
  }

  public static <T> JsonResult<T> http403(T data) {
    JsonResult<T> ret = new JsonResult<T>();
    ret.setCode(JsonReturnCode.ACCESS_ERROR.getCode());
    ret.setMessage(JsonReturnCode.ACCESS_ERROR.getDesc());
    ret.setData(data);
    return ret;
  }

}
