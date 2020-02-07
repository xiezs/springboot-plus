package com.ibeetl.admin.console.util;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.annotation.ElColumn;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.ElCascaderData;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * 视图工具类
 *
 * @author 一日看尽长安花
 */
public class VOUtil {
  static final String JSON_PATH_KEY = "json_path";
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
      if (MapUtil.isEmpty(annotationValueMap)) continue;
      Set<Entry<String, Object>> entrySet = annotationValueMap.entrySet();
      MapBuilder<Object, Object> camelKeyValueMap = MapUtil.builder();
      for (Entry<String, Object> entry : entrySet) {
        String key = entry.getKey();
        Object value = entry.getValue();
        camelKeyValueMap.put(StrUtil.toUnderlineCase(key), value);
      }
      String camelKey = StrUtil.toUnderlineCase(field.getName());
      Map<Object, Object> buildMap = camelKeyValueMap.build();
      String jsonPath = buildMap.getOrDefault(JSON_PATH_KEY, StrUtil.EMPTY).toString();
      if (StrUtil.isBlank(jsonPath)) {
        jsonPath = camelKey;
      }
      buildMap.put(JSON_PATH_KEY, jsonPath);
      mapBuilder.put(camelKey, buildMap);
    }
    return mapBuilder.build();
  }
}
