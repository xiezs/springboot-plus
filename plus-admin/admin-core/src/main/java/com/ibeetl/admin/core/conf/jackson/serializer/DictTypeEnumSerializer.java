package com.ibeetl.admin.core.conf.jackson.serializer;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import java.io.IOException;

public class DictTypeEnumSerializer extends JsonSerializer<DictTypeEnum> {

  @Override
  public void serialize(DictTypeEnum value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeStartObject();
    if (value instanceof Enum) {
      gen.writeObjectField("name", ReflectUtil.getFieldValue(value, "name"));
      gen.writeObjectField("value", ReflectUtil.getFieldValue(value, "value"));
      gen.writeObjectField("type", ReflectUtil.getFieldValue(value, "type"));
    }
    gen.writeEndObject();
  }
}
