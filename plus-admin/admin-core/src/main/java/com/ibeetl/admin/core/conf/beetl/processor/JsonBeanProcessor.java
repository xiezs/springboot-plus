package com.ibeetl.admin.core.conf.beetl.processor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.conf.beetl.resultmap.DBColumnProperty;
import com.ibeetl.admin.core.conf.beetl.resultmap.GridCell;
import com.ibeetl.admin.core.conf.beetl.resultmap.GridHeader;
import com.ibeetl.admin.core.conf.beetl.resultmap.GridMapping;
import com.ibeetl.admin.core.conf.beetl.resultmap.JavaFieldProperty;
import com.ibeetl.admin.core.util.cache.CacheUtil;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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

    GridMapping mapping = null;
    if (StrUtil.isNotBlank(sqlId)) {
      mapping = (GridMapping) CacheUtil.get(sqlId);
    }
    if (mapping == null) {
      /*无映射的情况下使用beetlsql默认自带的映射*/
      do {
        T bean = super.createBean(sqlId, rs, type, props, columnToProperty);
        results.add(bean);
      } while (rs.next());
    } else {
      /*复杂结果集映射，取消TailBean的便利性*/
      rs.absolute(0);
      mapping.getVirtualCell().getNestedCells().clear();
      fillMappingNestedCells(sqlId, rs, mapping);
      results = parserMappingNestedCells(mapping);
    }
    return CollUtil.isNotEmpty(results) ? CollUtil.getFirst(results) : null;
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
        T bean = super.createBean(sqlId, rs, type, props, columnToProperty);
        results.add(bean);
      } while (rs.next());
    } else {
      /*复杂结果集映射，取消TailBean的便利性*/
      rs.absolute(0);
      mapping.getVirtualCell().getNestedCells().clear();
      fillMappingNestedCells(sqlId, rs, mapping);
      results = parserMappingNestedCells(mapping);
    }
    return results;
  }

  /**
   * 循环转换整个网格映射为最终的映射类型
   *
   * @param mapping 预先处理好的网格映射
   * @return
   */
  public List parserMappingNestedCells(GridMapping mapping) throws SQLException {
    List results = parserRealContainerCell(mapping.getVirtualCell());
    return results;
  }

  /**
   * 递归转换整个网格
   *
   * @param virtualCell
   * @return
   */
  public List parserRealContainerCell(GridCell virtualCell) throws SQLException {
    /*判断是单个对象还是集合*/
    GridHeader relationGridHeader = virtualCell.getRelationGridHeader();
    JavaFieldProperty firstLevelBelongNestedField = relationGridHeader.getBelongNestedField();
    List objList = new ArrayList();
    List<GridCell> realCellList = virtualCell.getNestedCells();
    for (GridCell realContainerCell : realCellList) {
      List<GridCell> virtualCellList = realContainerCell.getNestedCells();
      GridCell contentCell = virtualCellList.get(0);
      Object obj = this.createObjectFromResultSetMap(contentCell);
      for (int i = 1; i < virtualCellList.size(); i++) {
        GridCell tempVirtualCell = virtualCellList.get(i);
        JavaFieldProperty twoLevelBelongNestedField =
            tempVirtualCell.getRelationGridHeader().getBelongNestedField();
        Boolean isList =
            Optional.ofNullable(twoLevelBelongNestedField)
                .map(JavaFieldProperty::isList)
                .orElse(true);
        List beanList = parserRealContainerCell(tempVirtualCell);
        Object val;
        if (isList) {
          val = beanList.isEmpty() ? null : beanList;
        } else {
          val = beanList.isEmpty() ? null : beanList.get(0);
        }
        super.callSetter(
            obj,
            twoLevelBelongNestedField.getPropertyDescriptor(),
            val,
            twoLevelBelongNestedField.getField().getType());
      }
      objList.add(obj);
    }
    return objList;
  }

  private Object createObjectFromResultSetMap(GridCell contentCell) throws SQLException {
    Object bean;
    Map<String, Object> valueMap = contentCell.getBeanMap();
    GridHeader relationGridHeader = contentCell.getRelationGridHeader();
    Class resultType = relationGridHeader.getResultType();
    if (resultType == null) {
      /*无对应类，说明是一个基本类型或者包装类*/
      JavaFieldProperty belongNestedField = relationGridHeader.getBelongNestedField();
      Object tv = valueMap.values().stream().findFirst().orElse(null);
      Object convertVal;
      if (belongNestedField.isList()) {
        Type listElementType = belongNestedField.getListElementType();
        convertVal = Convert.convert(listElementType, tv);
      } else {
        Class<?> type = belongNestedField.getField().getType();
        convertVal = Convert.convert(type, tv);
      }
      return convertVal;
    }
    bean = super.newInstance(resultType);
    Map<JavaFieldProperty, DBColumnProperty> fieldToColumnMap =
        relationGridHeader.getFieldToColumnMap();
    Set<Entry<JavaFieldProperty, DBColumnProperty>> entrySet = fieldToColumnMap.entrySet();
    for (Entry<JavaFieldProperty, DBColumnProperty> entry : entrySet) {
      JavaFieldProperty javaFieldProperty = entry.getKey();
      Field field = javaFieldProperty.getField();
      Object value = valueMap.get(field.getName());
      super.callSetter(bean, javaFieldProperty.getPropertyDescriptor(), value, field.getType());
    }
    return bean;
  }
  /**
   * 填充整个网格映射mapping数据结构：通过网格头映射生成一个个网格单元。<br>
   * 并没有对大数据量结果集做处理。而且递归有深度限制，列数映射过多可能会出现递归堆栈溢出，不过应该极少出现。
   *
   * @param resultSet
   * @param mapping
   * @throws SQLException
   */
  protected void fillMappingNestedCells(String sqlId, ResultSet resultSet, GridMapping mapping)
      throws SQLException {
    if (!resultSet.next()) return;
    GridHeader header = mapping.getHeader();
    processJdbcColumn(header, resultSet);
    /*必须给一个虚假容器，为了实现递归*/
    do {
      fillNestedGridCell(sqlId, resultSet, header, mapping.getVirtualCell());
    } while (resultSet.next());
  }
  /** 填充网格映射中的单元格映射结构。 */
  protected void fillNestedGridCell(
      String sqlId, ResultSet resultSet, GridHeader header, GridCell virtualCell) {
    /*通过当前header获取对应的结果集列*/
    Map<String, Object> beanMap = extractMapFromRs(sqlId, resultSet, header);
    GridCell realContainerCell = virtualCell.findOrCreateNestedCell(header, beanMap);
    List<GridHeader> nestedHeaders = header.getNestedHeaders();
    for (GridHeader nestedHeader : nestedHeaders) {
      /*在 realContainerCell 中找到对应header的虚拟cell*/
      GridCell nestedVirtualCell = realContainerCell.findVirtualCell(nestedHeader);
      fillNestedGridCell(sqlId, resultSet, nestedHeader, nestedVirtualCell);
    }
  }

  /**
   * Method processJdbcColumn ...<br>
   * 根据当前sql语句的resultset 处理gridheader中的fieldToColumnMap。
   *
   * @param header of type GridHeader
   * @param resultSet of type ResultSet
   */
  private void processJdbcColumn(GridHeader header, ResultSet resultSet) {
    List<GridHeader> nestedHeaders = header.getNestedHeaders();
    for (GridHeader nestedHeader : nestedHeaders) {
      processJdbcColumn(nestedHeader, resultSet);
    }
    try {
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();
      List<String> effectiveColumnLabelList = new ArrayList<String>();
      for (int i = 1; i <= columnCount; i++) {
        effectiveColumnLabelList.add(metaData.getColumnLabel(i).toLowerCase());
      }
      Map<JavaFieldProperty, DBColumnProperty> fieldToColumnMap = header.getFieldToColumnMap();
      Set<Entry<JavaFieldProperty, DBColumnProperty>> entrySet = fieldToColumnMap.entrySet();
      for (Entry<JavaFieldProperty, DBColumnProperty> entry : entrySet) {
        DBColumnProperty dbColumnProperty = entry.getValue();
        String columnLabel = dbColumnProperty.getColumnLabel();
        boolean contains = effectiveColumnLabelList.contains(columnLabel.toLowerCase());
        if (!contains) {
          dbColumnProperty.setHasEffective(false);
          continue;
        }
        boolean isSpecialDbRn =
            (super.dbType == DBStyle.DB_ORACLE || super.dbType == DBStyle.DB_SQLSERVER)
                && columnLabel.equalsIgnoreCase("beetl_rn");
        if (isSpecialDbRn) {
          dbColumnProperty.setHasEffective(false);
          // sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
          continue;
        }
        int columnIndex = resultSet.findColumn(columnLabel);
        dbColumnProperty.setHasEffective(true);
        dbColumnProperty.setColumnIndex(columnIndex);
        dbColumnProperty.setJdbcType(JDBCType.valueOf(metaData.getColumnType(columnIndex)));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * 遍历网格头，由网格头的信息从结果集中读取值。<br>
   *
   * @param resultSet
   * @param header
   * @return
   */
  private Map<String, Object> extractMapFromRs(
      String sqlId, ResultSet resultSet, GridHeader header) {
    Map<String, Object> tempBeanMap = MapUtil.newHashMap();
    /*处理每个头部映射的bean类型与结果集的列的类型转换*/
    Map<JavaFieldProperty, DBColumnProperty> fieldToColumnMap = header.getFieldToColumnMap();
    Set<Entry<JavaFieldProperty, DBColumnProperty>> entrySet = fieldToColumnMap.entrySet();
    for (Entry<JavaFieldProperty, DBColumnProperty> entry : entrySet) {
      try {
        JavaFieldProperty javaFieldProperty = entry.getKey();
        DBColumnProperty dbColumnProperty = entry.getValue();
        if (!dbColumnProperty.isHasEffective()) {
          continue;
        }

        Class fieldType =
            Optional.ofNullable(javaFieldProperty)
                .map(JavaFieldProperty::getField)
                .map(Field::getType)
                .orElse(null);

        JavaSqlTypeHandler handler = super.getHandlers().get(fieldType);
        if (handler == null) {
          handler = super.getDefaultHandler();
        }

        TypeParameter typeParameter =
            new TypeParameter(
                sqlId,
                super.dbName,
                fieldType,
                resultSet,
                resultSet.getMetaData(),
                dbColumnProperty.getColumnIndex());

        tempBeanMap.put(javaFieldProperty.getKey(), handler.getValue(typeParameter));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    tempBeanMap.values().remove(null);
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
