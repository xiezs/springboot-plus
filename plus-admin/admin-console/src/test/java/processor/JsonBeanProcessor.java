package processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ibeetl.admin.core.util.CacheUtil;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapping.BeanProcessor;
import resultmap.GridColumn;
import resultmap.GridHeader;
import resultmap.GridMapping;
import resultmap.GridRow;

public class JsonBeanProcessor extends BeanProcessor {

  public JsonBeanProcessor(SQLManager sm) {
    super(sm);
  }

  @Override
  public <T> List<T> toBeanList(String sqlId, ResultSet rs, Class<T> type) throws SQLException {
    if (!rs.next()) {
      return new ArrayList<T>(0);
    }
    List<T> results = new ArrayList<T>();
    PropertyDescriptor[] props = this.propertyDescriptors(type);
    ResultSetMetaData rsmd = rs.getMetaData();
    int[] columnToProperty = this.mapColumnsToProperties(type, rsmd, props);

    results.add(this.createBean(sqlId, rs, type, props, columnToProperty));

    return results;
  }

  /** 创建 一个新的对象，并从ResultSet初始化 */
  @Override
  protected <T> T createBean(
      String sqlId, ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty)
      throws SQLException {
    GridMapping gridMapping = (GridMapping) CacheUtil.get("Route_Mapping");
    if (null == gridMapping) {
      return super.createBean(sqlId, rs, type, props, columnToProperty);
    }

    fillMappingRow(rs, gridMapping);
    System.out.println(gridMapping);
    return null;
  }

  public String print(GridColumn column) {
    List<GridRow> nestedRows = column.getNestedRows();
    String print = "";
    for (GridRow nestedRow : nestedRows) {
      List<GridColumn> nestedColumns = nestedRow.getNestedColumns();
      GridColumn gridColumn = nestedColumns.get(0);
      print = print.concat(gridColumn.getBeanMap().toString() + "\n\t");
      for (int i = 1; i < nestedColumns.size(); i++) {
        GridColumn tempCol = nestedColumns.get(i);
        if (tempCol.getBeanMap().isEmpty() && !tempCol.getNestedRows().isEmpty()) {
          /*是一个容器性质的列*/
          print = print.concat(print(tempCol));
        } else if (!tempCol.getBeanMap().isEmpty() && tempCol.getNestedRows().isEmpty()) {
          /*对象存储性质的列*/
          print = print.concat(tempCol.getBeanMap().toString() + "\n\t");
        }
      }
    }
    return print;
  }

  protected void fillMappingRow(ResultSet resultSet, GridMapping mapping) throws SQLException {
    GridHeader header = mapping.getHeader();
    GridColumn column = new GridColumn();

    /** flag：用来判断结果集是否应该通过 mapping.nextRow(); 生成新的行 */
    Boolean flag = true;
    while (resultSet.next()) {

      List<GridRow> mappingNestedRows = mapping.getNestedRows();
      GridRow row = fillRowColumn(resultSet, header, column);
      if (!mappingNestedRows.contains(row)) {
        mappingNestedRows.add(row);
      }
    }
    System.out.println(11);
  }
  /** flag：用来判断结果集是否 next() */
  protected GridRow fillRowColumn(ResultSet resultSet, GridHeader header, GridColumn column)
      throws SQLException {
    /*搜寻已经存在的row，如果没有，则插入一个*/
    Map<String, Object> beanMap = extractMapFromRs(resultSet, header);
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
      GridRow nestedRow = fillRowColumn(resultSet, nestedHeader, nestedColumn);
      if (!nestedRows.contains(nestedRow)) {
        nestedRows.add(nestedRow);
      }
    }

    row.getNestedColumns().get(0).setBeanMap(beanMap);
    return row;
  }

  private Map<String, Object> extractMapFromRs(ResultSet resultSet, GridHeader header) {
    Map<String, Object> tempBeanMap = MapUtil.newHashMap();
    /*第一步、先处理当前可以处理的*/
    Map<String, String> javaToJdbcMap = header.getJavaToJdbcMap();
    Set<Entry<String, String>> entrySet = javaToJdbcMap.entrySet();
    for (Entry<String, String> entry : entrySet) {
      try {
        tempBeanMap.put(entry.getKey(), resultSet.getObject(entry.getValue()));
      } catch (SQLException e) {
        /*普遍错误时从resultset中获取一个不存在的列，但可以忽视*/
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
