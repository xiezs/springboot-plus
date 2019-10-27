package com.ibeetl.admin.console.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.ibeetl.admin.core.annotation.ElColumn;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * 视图对象工具类
 *
 * @author 一日看尽长安花
 */
public class VOUtil {

  public static Map resolveElColumn(Class cls) {

    MapBuilder<String, Map> mapBuilder = MapUtil.<String, Map>builder();
    Field[] declaredFields = cls.getDeclaredFields();
    for (Field field : declaredFields) {
      Map<String, Object> annotationValueMap =
          AnnotationUtil.getAnnotationValueMap(field, ElColumn.class);
      ElColumn elColumn = AnnotationUtil.getAnnotation(field, ElColumn.class);
      Optional.ofNullable(elColumn)
          .ifPresent(
              column -> {
                if (column.visible()) {
                  annotationValueMap.remove("visible");
                  mapBuilder.put(field.getName(), annotationValueMap);
                }
              });
    }
    return mapBuilder.build();
  }
}
