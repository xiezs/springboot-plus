package com.ibeetl.admin.core.conf.jackson.serializer;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import java.io.IOException;

public class DictTypeEnumDeserializer extends JsonDeserializer<DictTypeEnum> {


  @Override
  public DictTypeEnum deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    return null;
  }
}
