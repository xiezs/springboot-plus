package com.ibeetl.admin.core.conf.beetl.resultmap;

import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Optional.ofNullable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 节点，保存了属性与列的映射关系，以及映射的java类型。<br>
 * 内嵌的节点，内嵌节点的映射java类型，是否是映射至List集合字段
 */
public class GridHeader implements Serializable {

  /** java属性名与数据库列名的对应。顺序：prop:column */
  Map<JavaFieldProperty, DBColumnProperty> fieldToColumnMap = new HashMap<>();

  /** 当前节点对应映射java类型，只有嵌套的是非基本类型和string才有值(也可以将包装类型赋值，为了简单处理，我并没有做) */
  Class resultType;

  /** 嵌套类型的子节点，可以改为map存储field：gridheader */
  List<GridHeader> nestedHeaders = CollUtil.<GridHeader>newArrayList();

  /** 嵌套类型的父节点 */
  GridHeader parentHeader;

  /**
   * 当前header所属上级的嵌套字段名。如果是第一级就说明没有嵌套，为null。
   * 保存这个信息，是为了在处理当前节点转换为pojo时，有个是否为数组的判断
   *  */
  JavaFieldProperty belongNestedField;

  /**
   * 节点所属的Gridmapping
   * 只是尽可能的保留上下文而已，可以设法用其它的外部信息类保持，不过也没有使用到它
   *  */
  GridMapping belongMapping;

  public GridHeader(Map<String, Object> resultMapping, GridMapping belongMapping) {
    this.belongMapping = belongMapping;
    processResultMapping(resultMapping);
  }

  public Map<JavaFieldProperty, DBColumnProperty> getFieldToColumnMap() {
    return fieldToColumnMap;
  }

  public void setFieldToColumnMap(Map<JavaFieldProperty, DBColumnProperty> fieldToColumnMap) {
    this.fieldToColumnMap = fieldToColumnMap;
  }

  public Class getResultType() {
    return resultType;
  }

  public void setResultType(Class resultType) {
    this.resultType = resultType;
  }

  public List<GridHeader> getNestedHeaders() {
    return nestedHeaders;
  }

  public void setNestedHeaders(List<GridHeader> nestedHeaders) {
    this.nestedHeaders = nestedHeaders;
  }

  public GridHeader getParentHeader() {
    return parentHeader;
  }

  public void setParentHeader(GridHeader parentHeader) {
    this.parentHeader = parentHeader;
  }

  public JavaFieldProperty getBelongNestedField() {
    return belongNestedField;
  }

  public void setBelongNestedField(JavaFieldProperty belongNestedField) {
    this.belongNestedField = belongNestedField;
  }

  public GridMapping getBelongMapping() {
    return belongMapping;
  }

  public void setBelongMapping(GridMapping belongMapping) {
    this.belongMapping = belongMapping;
  }

  /**
   * Method processResultMapping ...<br>
   * 根据json格式的映射mapping，转换成gridheader结构
   *
   * @param resultMapping of type Map<String, Object>
   */
  private void processResultMapping(Map<String, Object> resultMapping) {
    String resultType = ofNullable(resultMapping.get("resultType")).orElse("").toString();
    if (StrUtil.isNotBlank(resultType)) {
      this.setResultType(ClassUtil.loadClass(resultType));
    }
    resultMapping.remove("resultType");
    Set<Entry<String, Object>> entrySet = resultMapping.entrySet();
    for (Entry<String, Object> objectEntry : entrySet) {
      /*bean的字段名*/
      String key = objectEntry.getKey();
      /*key字段名对应的数据库列名或者为复杂类型的json object*/
      Object value = objectEntry.getValue();
      JavaFieldProperty keyField = JavaFieldProperty.UNKOWN;
      if (StrUtil.isNotBlank(resultType)) {
        keyField = new JavaFieldProperty(ReflectUtil.getField(this.getResultType(), key));
      }
      keyField.setKey(key);
      Class<?> valClass = ClassUtil.getClass(value);
      if (Collection.class.isAssignableFrom(valClass)) {
        /*生成嵌套节点，此嵌套的类型对应集合字段*/
        Map<String, Object> nestedMapping =
            (Map<String, Object>)
                ((Collection) value).stream().findFirst().orElse(MapUtil.newHashMap(0));
        GridHeader nestedHeader = new GridHeader(nestedMapping, this.getBelongMapping());
        nestedHeader.setParentHeader(this);
        nestedHeader.setBelongNestedField(keyField);
        this.getNestedHeaders().add(nestedHeader);
      } else if (Map.class.isAssignableFrom(valClass)) {
        /*生成嵌套节点，此嵌套的类型对应单个对象字段*/
        Map<String, Object> nestedMapping = (Map<String, Object>) value;
        GridHeader nestedHeader = new GridHeader(nestedMapping, this.getBelongMapping());
        nestedHeader.setParentHeader(this);
        nestedHeader.setBelongNestedField(keyField);
        this.getNestedHeaders().add(nestedHeader);
      } else if (isNotBlank(key)
          || (null != value && value instanceof String && isNotBlank(String.valueOf(value)))) {
        /*非集合，非map类型的字段*/
        DBColumnProperty columnProperty = new DBColumnProperty(String.valueOf(value));
        columnProperty.setValue(String.valueOf(value));
        fieldToColumnMap.put(keyField, columnProperty);
      }
    }
  }
}
