package resultmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.List;

/**
 * 网格行，包含一个个网格列<br>
 * 一个网格行，对应着SQL select查询出的一个Java的逻辑对象
 */
public class GridRow implements Serializable {

  /** 包含的列,第0个列代表的是当前整个类的映射，从0列之后是内部对象字段的映射 */
  List<GridColumn> nestedColumns = CollUtil.<GridColumn>newArrayList();

  /** 所属的列 */
  GridColumn belongColumn;

  /** 映射对象的类型名 */
  String resultType;

  /** 通过每个列的key进行hash得到一个唯一row id */
  Integer rowKey;

  public GridRow() {}

  public List<GridColumn> getNestedColumns() {
    return nestedColumns;
  }

  public void setNestedColumns(List<GridColumn> nestedColumns) {
    this.nestedColumns = nestedColumns;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public Integer getRowKey() {
    return this.nestedColumns.get(0).getObjKey();
  }

  public GridColumn getBelongColumn() {
    return belongColumn;
  }

  public void setBelongColumn(GridColumn belongColumn) {
    this.belongColumn = belongColumn;
  }

  /**
   * 根据网格头，生成对应的网格行结构。 行套列或者列套行再套列两种。
   *
   * @return
   */
  public static GridRow generateRowByHeader(GridHeader gridHeader) {
    if (ObjectUtil.isNull(gridHeader)) {
      return null;
    }
    /*这是外层*/
    GridRow gridRow = new GridRow();
    /*这个列才是真正的存储数据值的*/
    GridColumn gridColumn = new GridColumn();

    gridRow.getNestedColumns().add(gridColumn);

    gridColumn.setBelongRow(gridRow);

    List<GridHeader> headers = gridHeader.getNestedHeaders();
    for (GridHeader header : headers) {
      /*这只是外部的一个容器性质的列，这个是内层*/
      GridColumn containerColumn = new GridColumn();
      GridRow nestedRow = generateRowByHeader(header);

      containerColumn.getNestedRows().add(nestedRow);
      nestedRow.setBelongColumn(containerColumn);

      gridRow.getNestedColumns().add(containerColumn);
      containerColumn.setBelongRow(gridRow);
      containerColumn.setMappingHeader(header);
    }

    gridColumn.setMappingHeader(gridHeader);
    gridColumn.setResultType(gridHeader.getResultType());
    gridRow.setResultType(gridHeader.getResultType());
    return gridRow;
  }

  public GridColumn findColumnByHeader(GridHeader header) {
    for (GridColumn column : this.nestedColumns) {
      if (ObjectUtil.equal(column.getMappingHeader(), header)) {
        return column;
      }
    }
    return null;
  }
}
