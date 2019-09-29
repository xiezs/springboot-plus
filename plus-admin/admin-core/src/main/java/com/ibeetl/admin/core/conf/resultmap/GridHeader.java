package com.ibeetl.admin.core.conf.resultmap;

import static cn.hutool.core.util.StrUtil.EMPTY;
import static cn.hutool.core.util.StrUtil.isNotBlank;
import static java.util.Optional.ofNullable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 网格头，保存了属性与列的映射关系，以及映射的java类型。<br>
 * 内嵌的网格头，内嵌网格头的映射java类型，是否是映射至List集合字段
 */
public class GridHeader implements Serializable {

  /** java属性名与数据库列名的对应。顺序：prop:column */
  Map<String, String> javaToJdbcMap;

  /** 映射java类型,如果该值为空，代表为基础类型，并且忽视javaToJdbcMap中的key，因为此刻是基本类型的List， */
  String resultType;

  /** 嵌套类型的网格头 */
  List<GridHeader> nestedHeaders = CollUtil.<GridHeader>newArrayList();

  /** 嵌套类型的网格头 */
  GridHeader parentHeader;

  /** 嵌套字段属性名 */
  String nestedPropName;

  /** 嵌套类型是否是一个集合 */
  boolean isCollection = false;

  /** 网格头所属的网格映射 */
  GridMapping belongMapping;

  public GridHeader(Map<String, Object> resultMapping) {
    javaToJdbcMap = new HashMap<String, String>();
    processResultMapping(resultMapping);
  }

  public Map<String, String> getJavaToJdbcMap() {
    return javaToJdbcMap;
  }

  public void setJavaToJdbcMap(Map<String, String> javaToJdbcMap) {
    this.javaToJdbcMap = javaToJdbcMap;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
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

  public String getNestedPropName() {
    return nestedPropName;
  }

  public void setNestedPropName(String nestedPropName) {
    this.nestedPropName = nestedPropName;
  }

  public boolean getIsCollection() {
    return isCollection;
  }

  public void setIsCollection(boolean isCollection) {
    this.isCollection = isCollection;
  }

  public GridMapping getBelongMapping() {
    return belongMapping;
  }

  public void setBelongMapping(GridMapping belongMapping) {
    this.belongMapping = belongMapping;
  }

  private void processResultMapping(Map<String, Object> resultMapping) {
    Set<Entry<String, Object>> entrySet = resultMapping.entrySet();
    this.setResultType(ofNullable(resultMapping.get("resultType")).orElse(EMPTY).toString());
    resultMapping.remove("resultType");
    for (Entry<String, Object> objectEntry : entrySet) {
      String key = objectEntry.getKey();
      Object value = objectEntry.getValue();
      Class<?> valClass = ClassUtil.getClass(value);
      if (List.class.isAssignableFrom(valClass)) {
        /*生成嵌套网格头，此嵌套网格头的类型对应集合字段*/
        Map<String, Object> nestedMapping =
            (Map<String, Object>) ((List) value).stream().findFirst().orElse(MapUtil.newHashMap(0));
        GridHeader nestedHeader = new GridHeader(nestedMapping);
        nestedHeader.setIsCollection(true);
        nestedHeader.setNestedPropName(key);
        nestedHeader.setBelongMapping(this.getBelongMapping());
        this.getNestedHeaders().add(nestedHeader);
        nestedHeader.setParentHeader(this);
      } else if (Map.class.isAssignableFrom(valClass)) {
        /*生成嵌套网格头，此嵌套网格头的类型对应单个对象字段*/
        Map<String, Object> nestedMapping = (Map<String, Object>) value;
        GridHeader nestedHeader = new GridHeader(nestedMapping);
        nestedHeader.setIsCollection(false);
        nestedHeader.setNestedPropName(key);
        nestedHeader.setBelongMapping(this.getBelongMapping());
        this.getNestedHeaders().add(nestedHeader);
        nestedHeader.setParentHeader(this);
      } else if (isNotBlank(key) || (null != value && isNotBlank(String.valueOf(value)))) {
        javaToJdbcMap.put(key, String.valueOf(value));
      }
    }
  }
}
