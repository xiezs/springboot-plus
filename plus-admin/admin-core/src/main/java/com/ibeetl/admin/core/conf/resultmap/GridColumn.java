package com.ibeetl.admin.core.conf.resultmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 网格列，结果集映射的java类型中的基本类型属性，都应该在一个列中。<br>
 * 非基本类型的属性字段，应该内嵌多行 <br>
 * 包含：值映射map、映射类型、最关键的obj key<br>
 * objkey 决定了数据库结果集的多条重复记录会被唯一记录成一个对象。
 */
public class GridColumn implements Serializable {

  /** 对应的属性与数据库中值的映射，用于map to Bean 转换 */
  Map<String, Object> beanMap = MapUtil.newHashMap();

  /** 映射类型 */
  String resultType;

  /** 一个网格列包含一个对象中的所有数据列，通过对这些数据列进行hash得到数据列的唯一性 */
  Integer objKey;

  /** 包含的网格行 */
  List<GridRow> nestedRows = CollUtil.<GridRow>newArrayList();

  /** 归属的网格行 */
  GridRow belongRow;

  GridHeader mappingHeader;

  public Map<String, Object> getBeanMap() {
    return beanMap;
  }

  public void setBeanMap(Map<String, Object> beanMap) {
    this.beanMap = beanMap;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public Integer getObjKey() {
    objKey = calculateKey(beanMap);
    return objKey;
  }

  public static Integer calculateKey(Map<String, Object> map) {
    if (MapUtil.isEmpty(map)) {
      return Integer.MIN_VALUE;
    }
    int hs = 0;
    Set<Entry<String, Object>> entrySet = map.entrySet();
    for (Entry<String, Object> entry : entrySet) {
      if (ObjectUtil.isNull(entry.getValue())) continue;
      hs += entry.getValue().hashCode();
    }
    return hs * 42;
  }

  public List<GridRow> getNestedRows() {
    return nestedRows;
  }

  public void setNestedRows(List<GridRow> nestedRows) {
    this.nestedRows = nestedRows;
  }

  public GridRow getBelongRow() {
    return belongRow;
  }

  public void setBelongRow(GridRow belongRow) {
    this.belongRow = belongRow;
  }

  public void setMappingHeader(GridHeader mappingHeader) {
    this.mappingHeader = mappingHeader;
  }

  public GridHeader getMappingHeader() {
    return this.mappingHeader;
  }

  public GridRow findRowByKey(Integer objKey) {
    for (GridRow row : this.nestedRows) {
      if (ObjectUtil.equal(row.getRowKey(), objKey)) {
        return row;
      }
    }
    return null;
  }
}
