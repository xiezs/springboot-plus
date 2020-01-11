package com.ibeetl.admin.console.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import com.ibeetl.admin.core.annotation.ElColumn;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.ElCascaderData;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 视图对象工具类
 *
 * @author 一日看尽长安花
 */
public class VOUtil {

  /**
   * Method resolveElColumn ... todo 预计重新定义一个类用来转换，而不是用map
   *
   * @param cls of type Class ，一个专门存放元数据的ElQuery类
   * @return Map
   */
  public static Map resolveElColumn(Class cls) {

    MapBuilder<String, Map> mapBuilder = MapUtil.<String, Map>builder();
    Field[] declaredFields = cls.getDeclaredFields();
    for (Field field : declaredFields) {
      Map<String, Object> annotationValueMap =
          AnnotationUtil.getAnnotationValueMap(field, ElColumn.class);
      ElColumn elColumn = AnnotationUtil.getAnnotation(field, ElColumn.class);
      Optional.ofNullable(elColumn)
          .ifPresent(column -> mapBuilder.put(field.getName(), annotationValueMap));
    }
    return mapBuilder.build();
  }

}
