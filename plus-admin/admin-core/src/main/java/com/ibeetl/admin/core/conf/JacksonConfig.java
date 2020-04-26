package com.ibeetl.admin.core.conf;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ibeetl.admin.core.conf.jackson.serializer.CustomJsonResultSerializer;
import com.ibeetl.admin.core.conf.jackson.serializer.CustomLongSerializer;
import com.ibeetl.admin.core.conf.jackson.serializer.DateToTimestampSerializer;
import com.ibeetl.admin.core.conf.jackson.serializer.DictTypeEnumSerializer;
import com.ibeetl.admin.core.util.enums.DictTypeEnum;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.Date;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author 一日看尽长安花 */
@Configuration
public class JacksonConfig {
  @Bean
  @ConditionalOnMissingBean(ObjectMapper.class)
  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    /*objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));*/

    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    objectMapper.setSerializationInclusion(Include.NON_NULL);

    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    /*将date字段序列化为时间戳，此转换会转成纳秒级*/
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    /*map中date的也序列化为时间戳，此转换会转成纳秒级*/
    objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleModule simpleModule = new SimpleModule("SimpleModule", Version.unknownVersion());

    simpleModule.addSerializer(JsonResult.class, new CustomJsonResultSerializer());
    simpleModule.addSerializer(DictTypeEnum.class, new DictTypeEnumSerializer());
    simpleModule.addSerializer(Date.class, new DateToTimestampSerializer());
    /**
     * todo 关于jackson对于枚举的反序列化，只能通过注解完成了。。
     * 这些序列化接口配置都是无用的。自有一个{@link com.fasterxml.jackson.databind.deser.std.EnumDeserializer}
     * */
    //    simpleModule.addDeserializer(DictTypeEnum.class, new DictTypeEnumDeserializer());

    CustomLongSerializer longSerializer = new CustomLongSerializer();
    simpleModule.addSerializer(Long.class, longSerializer);
    simpleModule.addSerializer(Long.TYPE, longSerializer);

    objectMapper.registerModule(simpleModule);
    return objectMapper;
  }
}
