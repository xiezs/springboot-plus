package com.ibeetl.admin.core.conf.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ibeetl.admin.core.web.JsonResult;
import java.io.IOException;
import org.beetl.sql.core.engine.PageQuery;

/**
 * layui 前端要求后台返回的数据格式
 *
 * @author xiandafu
 */
public class CustomJsonResultSerializer extends JsonSerializer<JsonResult> {

  public CustomJsonResultSerializer() {}

  /** 处理 JsonResult 返回结果。自动分离分页信息，不需要手动在controller中分离 */
  @Override
  public void serialize(JsonResult value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();
    gen.writeObjectField("code", Integer.parseInt(value.getCode()));
    gen.writeStringField("message", value.getMessage());
    Object data = value.getData();
    if (data instanceof PageQuery) {
      PageQuery query = (PageQuery) (data);
      /*不同js前端插件会有不一样数据格式，所以返回两种命名*/
      gen.writeNumberField("count", query.getTotalRow());
      gen.writeNumberField("total", query.getTotalRow());
      gen.writeObjectField("data", query.getList());
    } else {
      gen.writeObjectField("data", data);
    }
    gen.writeEndObject();
  }
}
