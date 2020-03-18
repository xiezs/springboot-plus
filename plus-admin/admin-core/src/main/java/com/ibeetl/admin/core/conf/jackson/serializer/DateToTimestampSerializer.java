package com.ibeetl.admin.core.conf.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Date;

/**
 * Class DateToTimestampSerializer : <br>
 * 描述：在date序列化时间戳时，默认的jackson会将纳秒一起序列化，但是没必要。
 *
 * @author 一日看尽长安花 Created on 2020/3/13
 */
public class DateToTimestampSerializer extends JsonSerializer<Date> {

  private static final long NANO_MULTIPLE = 1000L;

  @Override
  public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeNumber(value.getTime() / NANO_MULTIPLE);
  }
}
