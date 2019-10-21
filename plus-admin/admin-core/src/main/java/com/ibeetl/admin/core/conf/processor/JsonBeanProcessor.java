package com.ibeetl.admin.core.conf.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.conf.resultmap.GridColumn;
import com.ibeetl.admin.core.conf.resultmap.GridHeader;
import com.ibeetl.admin.core.conf.resultmap.GridMapping;
import com.ibeetl.admin.core.conf.resultmap.GridRow;
import com.ibeetl.admin.core.util.CacheUtil;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

public class JsonBeanProcessor extends BeanProcessor {

  public JsonBeanProcessor(SQLManager sm) {
    super(sm);
  }

  /**
   * 网格化输出结果集
   *
   * @param resultSet
   * @throws SQLException
   */
  public void printfResultSet(ResultSet resultSet) throws SQLException {
    Map<String, List<Object>> map = MapUtil.<String, List<Object>>builder().build();
    ResultSetMetaData metaData = resultSet.getMetaData();
    int count = metaData.getColumnCount();
    int rn = 0;
    resultSet.absolute(0);
    while (resultSet.next()) {
      for (int i = 1; i <= count; i++) {
        String columnLabel = metaData.getColumnLabel(i);
        List<Object> objectList = map.getOrDefault(columnLabel, CollUtil.newArrayList());
        Object object = resultSet.getObject(i);
        objectList.add(object);
        map.put(columnLabel, objectList);
      }
      rn++;
    }
    resultSet.absolute(0);

    Set<String> keySet = map.keySet();
    for (String key : keySet) {
      System.out.printf("| %-32s ", key);
    }
    System.out.println();

    for (int i = 0; i < rn; i++) {
      Set<Entry<String, List<Object>>> entrySet = map.entrySet();
      for (Entry<String, List<Object>> entry : entrySet) {
        System.out.printf("| %-32s  ", entry.getValue().get(i));
      }
      System.out.println();
    }
  }
  /**
   * 将ResultSet映射为一个POJO对象
   *
   * @param rs
   * @param type
   * @return
   * @throws SQLException
   */
  @Override
  public <T> T toBean(String sqlId, ResultSet rs, Class<T> type) throws SQLException {
    List<T> results = new ArrayList<T>();

    PropertyDescriptor[] props = this.propertyDescriptors(type);
    ResultSetMetaData rsmd = rs.getMetaData();
    int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

    GridMapping mapping = (GridMapping) CacheUtil.get(sqlId);
    if (null == mapping) {
      /*无映射的情况下使用beetlsql默认自带的映射*/
      do {
        super.createBean(sqlId, rs, type, props, columnToProperty);
      } while (rs.next());
    } else {
      /*复杂结果集映射，取消TailBean的便利性*/
      rs.absolute(0);
      mapping.setNestedRows(null);
      fillMappingRow(sqlId, rs, mapping);
      results = convertMapping(mapping, type);
    }
    return CollUtil.getFirst(results);
  }

  /**
   * 将ResultSet映射为一个List&lt;POJO&gt;集合
   *
   * @param sqlId
   * @param rs
   * @param type
   * @param <T>
   * @return
   * @throws SQLException
   */
  @Override
  public <T> List<T> toBeanList(String sqlId, ResultSet rs, Class<T> type) throws SQLException {
    if (!rs.next()) {
      return new ArrayList<T>(0);
    }

    List<T> results = new ArrayList<T>();
    PropertyDescriptor[] props = this.propertyDescriptors(type);
    ResultSetMetaData rsmd = rs.getMetaData();
    int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

    GridMapping mapping = (GridMapping) CacheUtil.get(sqlId);
    if (null == mapping) {
      /*无映射的情况下使用beetlsql默认自带的映射*/
      do {
        results.add(super.createBean(sqlId, rs, type, props, columnToProperty));
      } while (rs.next());
    } else {
      /*复杂结果集映射，取消TailBean的便利性*/
      rs.absolute(0);
      mapping.getNestedRows().clear();
      fillMappingRow(sqlId, rs, mapping);
      results = convertMapping(mapping, type);
    }
    return results;
  }

  /**
   * 循环转换整个网格映射
   *
   * @param mapping
   * @param objType
   * @param <T>
   * @return
   */
  public <T> List<T> convertMapping(GridMapping mapping, Class<T> objType) throws SQLException {
    List<T> results = new ArrayList<T>();
    List<GridRow> mappingNestedRows = mapping.getNestedRows();
    for (GridRow mappingNestedRow : mappingNestedRows) {
      T obj = convertRow(mappingNestedRow, objType);
      results.add(obj);
    }
    return results;
  }

  /**
   * 递归转换整个网格行
   *
   * @param row
   * @param objType
   * @param <T>
   * @return
   */
  public <T> T convertRow(GridRow row, Class<T> objType) throws SQLException {
    T obj = super.newInstance(objType);

    List<GridColumn> nestedColumns = row.getNestedColumns();
    GridColumn curObjCol = nestedColumns.get(0);

    for (int i = 1; i < nestedColumns.size(); i++) {
      GridColumn nestedColumn = nestedColumns.get(i);
      GridHeader mappingHeader = nestedColumn.getMappingHeader();
      String resultType = mappingHeader.getResultType();

      List<GridRow> nestedRows = nestedColumn.getNestedRows();
      for (GridRow nestedRow : nestedRows) {
        if (StrUtil.isNotBlank(resultType)) {
          /*在映射中标明类型，表示是复杂类型*/
          Class nestedPropType = ClassUtil.loadClass(resultType);
          String nestedPropName = mappingHeader.getNestedPropName();
          boolean isCollection = mappingHeader.getIsCollection();
          Object resultObj = BeanUtil.getFieldValue(obj, nestedPropName);
          Object nestedPropObj = convertRow(nestedRow, nestedPropType);
          if (isCollection) {
            ((Collection) resultObj).add(nestedPropObj);
            resultObj = CollUtil.removeNull(((Collection) resultObj));
          } else {
            resultObj = nestedPropObj;
          }
          BeanUtil.setFieldValue(obj, nestedPropName, resultObj);
        } else {
          /*在映射中没有标明类型，表示是基本类型*/
          String nestedPropName = mappingHeader.getNestedPropName();
          boolean isCollection = mappingHeader.getIsCollection();
          Object resultObj = BeanUtil.getFieldValue(obj, nestedPropName);
          Map<String, Object> beanMap = nestedRow.getNestedColumns().get(0).getBeanMap();
          /*无法递归，表明是基本类型集合*/
          Object nestedPropObj = CollUtil.getFirst(beanMap.values());
          if (isCollection) {
            ((Collection) resultObj).add(nestedPropObj);
            resultObj = CollUtil.removeNull(((Collection) resultObj));
          } else {
            resultObj = nestedPropObj;
          }
          BeanUtil.setFieldValue(obj, nestedPropName, resultObj);
        }
      }
    }
    BeanUtil.fillBeanWithMap(curObjCol.getBeanMap(), obj, true, true);
    return obj;
  }
  /**
   * 填充整个网格映射mapping数据结构：通过网格头映射生成一个个网格行
   *
   * @param resultSet
   * @param mapping
   * @throws SQLException
   */
  protected void fillMappingRow(String sqlId, ResultSet resultSet, GridMapping mapping)
      throws SQLException {
    GridHeader header = mapping.getHeader();
    GridColumn column = new GridColumn();
    while (resultSet.next()) {
      List<GridRow> mappingNestedRows = mapping.getNestedRows();
      GridRow row = fillRowColumn(sqlId, resultSet, header, column);
      if (!mappingNestedRows.contains(row)) {
        mappingNestedRows.add(row);
      }
    }
  }
  /**
   * 填充网格行中的单元格映射结构。
   *
   * <p>网格行中存在一个个网格列，也对应着相应的网格头结构
   */
  protected GridRow fillRowColumn(
      String sqlId, ResultSet resultSet, GridHeader header, GridColumn column) throws SQLException {
    /*搜寻已经存在的row，如果没有，则插入一个*/
    Map<String, Object> beanMap = extractMapFromRs(sqlId, resultSet, header);
    Integer calculateKey = GridColumn.calculateKey(beanMap);
    GridRow row = column.findRowByKey(calculateKey);
    /*这里可能出现一个问题，始终第0个行是空白的*/
    if (ObjectUtil.isNull(row)) {
      /*生成一个新的*/
      row = GridRow.generateRowByHeader(header);
      column.getNestedRows().add(row);
      row.setBelongColumn(column);
    }

    List<GridHeader> nestedHeaders = header.getNestedHeaders();
    for (GridHeader nestedHeader : nestedHeaders) {
      GridColumn nestedColumn = row.findColumnByHeader(nestedHeader);
      List<GridRow> nestedRows = nestedColumn.getNestedRows();
      GridRow nestedRow = fillRowColumn(sqlId, resultSet, nestedHeader, nestedColumn);
      if (!nestedRows.contains(nestedRow)) {
        nestedRows.add(nestedRow);
      }
    }
    row.getNestedColumns().get(0).setBeanMap(beanMap);
    return row;
  }

  /**
   * 遍历网格头，由网格头的信息从结果集中读取值。<br>
   *
   * @param resultSet
   * @param header
   * @return
   */
  private Map<String, Object> extractMapFromRs(String sqlId, ResultSet resultSet, GridHeader header)
      throws SQLException {
    /*TODO  待处理TypeParameter和映射类型的注解的处理*/
    Map<String, Object> tempBeanMap = MapUtil.newHashMap();
    Map<String, Integer> columnLableToIndexMap = MapUtil.newHashMap();
    ResultSetMetaData metaData = resultSet.getMetaData();
    for (int i = 1; i <= metaData.getColumnCount(); i++) {
      String key = metaData.getColumnLabel(i);
      boolean isSpecialDbRn =
          (super.dbType == DBStyle.DB_ORACLE || super.dbType == DBStyle.DB_SQLSERVER)
              && key.equalsIgnoreCase("beetl_rn");
      if (isSpecialDbRn) {
        // sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
        continue;
      }
      columnLableToIndexMap.putIfAbsent(key, i);
    }
    String resultType = header.getResultType();
    Class objectClass =
        StrUtil.isNotBlank(resultType) ? ClassUtil.loadClass(resultType, false) : null;

    Map<String, String> javaToJdbcMap = header.getJavaToJdbcMap();
    Set<Entry<String, String>> entrySet = javaToJdbcMap.entrySet();
    for (Entry<String, String> entry : entrySet) {
      try {
        if (objectClass == null) {
          tempBeanMap.put(entry.getKey(), resultSet.getObject(entry.getValue()));
          continue;
        }

        Field declaredField = ClassUtil.getDeclaredField(objectClass, entry.getKey());
        Class fieldType = declaredField != null ? declaredField.getType() : null;

        Integer columnIndex = columnLableToIndexMap.getOrDefault(entry.getValue(), -1);
        columnIndex =
            columnIndex != -1
                ? columnIndex
                : columnLableToIndexMap.getOrDefault(entry.getValue().toUpperCase(), -1);
        columnIndex =
            columnIndex != -1
                ? columnIndex
                : columnLableToIndexMap.getOrDefault(entry.getValue().toLowerCase(), -1);

        TypeParameter typeParameter =
            new TypeParameter(sqlId, super.dbName, fieldType, resultSet, metaData, columnIndex);

        JavaSqlTypeHandler handler = super.getHandlers().get(fieldType);
        if (handler == null) {
          handler = super.getDefaultHandler();
        }

        tempBeanMap.put(entry.getKey(), handler.getValue(typeParameter));
      } catch (SQLException e) {
        /*大部分错误：从resultset中获取一个不存在的列，但可以忽视*/
      }
    }
    return tempBeanMap;
  }

  /**
   * 根据class取得属性描述PropertyDescriptor
   *
   * @param c
   * @return
   * @throws SQLException
   */
  private PropertyDescriptor[] propertyDescriptors(Class<?> c) throws SQLException {

    try {
      return BeanKit.propertyDescriptors(c);
    } catch (IntrospectionException e) {
      throw new SQLException("Bean introspection failed: " + e.getMessage());
    }
  }
}
