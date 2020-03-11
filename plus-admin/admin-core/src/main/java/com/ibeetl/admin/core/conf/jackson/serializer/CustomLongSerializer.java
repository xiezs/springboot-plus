package com.ibeetl.admin.core.conf.jackson.serializer;

import cn.hutool.core.convert.Convert;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 修正jackson转换Long类型的一个bug： jackson的转换Long类型时，如果数值在Integer范围，会变成Integer类型，然后通过强制转换为Long时就会报错。
 * 这里采用安全的Long转换避免强制类型转换
 */
public class CustomLongSerializer extends StdSerializer<Object> {

  public CustomLongSerializer() {
    super(Object.class);
  }

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeNumber(Convert.toLong(value, null));
  }

  @Override
  public void serializeWithType(
      Object value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
      throws IOException {
    WritableTypeId typeIdDef =
        typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_NUMBER_INT));
    serialize(value, g, provider);
    typeSer.writeTypeSuffix(g, typeIdDef);
  }

  @Override
  public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
    return createSchemaNode("long", true);
  }

  @Override
  public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
      throws JsonMappingException {
    visitIntFormat(visitor, typeHint, NumberType.LONG);
  }
}
