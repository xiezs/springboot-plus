package processor;

import cn.hutool.core.collection.CollUtil;
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

  protected void fillMappingRow(ResultSet resultSet, GridMapping mapping) throws SQLException {
    GridHeader header = mapping.getHeader();
    mapping.nextRow();
    /** flag：用来判断结果集是否应该 next()。可能后期应该改成CacheRowSet更好，这样可以直接操作结果集光标回退一行就行了。 */
    Boolean flag = true;
    while (true) {
      if (flag && !resultSet.next()) {
        break;
      }
      resultSet.previous();
      GridRow row;
      if (!flag) {
        row = mapping.nextRow();
      } else {
        row = CollUtil.getLast(mapping.getNestedRows());
      }
      flag = fillRowColumn(resultSet, header, row);
    }
  }
  /** flag：用来判断结果集是否 next() */
  protected boolean fillRowColumn(ResultSet resultSet, GridHeader header, GridRow row)
      throws SQLException {
    boolean flag = true;
    Map<String, Object> tempBeanMap = MapUtil.newHashMap();
    /*第一步、先处理当前可以处理的*/
    Map<String, String> javaToJdbcMap = header.getJavaToJdbcMap();
    Set<Entry<String, String>> entrySet = javaToJdbcMap.entrySet();
    for (Entry<String, String> entry : entrySet) {
      tempBeanMap.put(entry.getKey(), resultSet.getObject(entry.getValue()));
    }
    Integer rowKey = row.getRowKey();
    Integer calculateKey = GridColumn.calculateKey(tempBeanMap);
    if (ObjectUtil.notEqual(rowKey, calculateKey)) {
      if (ObjectUtil.isNull(row.getBelongColumn())) {
        flag = false;
      } else {
        GridRow generateRow = GridRow.generateRowByHeader(header);
        generateRow.setBelongColumn(row.getBelongColumn());

        GridColumn tempColumn = new GridColumn();
        tempColumn.setMappingHeader(header);
        tempColumn.setResultType(header.getResultType());
        tempColumn.setBelongRow(row);
        tempColumn.setBeanMap(tempBeanMap);

        generateRow.getNestedColumns().add(0, tempColumn);
        row.getBelongColumn().getNestedRows().add(generateRow);
        flag = true;
      }
    } else {
      List<GridHeader> nestedHeaders = header.getNestedHeaders();
      for (GridHeader nestedHeader : nestedHeaders) {
        row = row.getGridColumnByMappingHeader(nestedHeader).getBelongRow();
        flag = fillRowColumn(resultSet, nestedHeader, row);
      }
    }
    return flag;
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
