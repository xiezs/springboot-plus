package resultmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.List;

/**
 * 网格行，包含一个个网格列<br>
 * 一行一个对象，或者java类中的一个对象字段
 */
public class GridRow implements Serializable {

  /** 包含的列 */
  List<GridColumn> nestedColumns = CollUtil.<GridColumn>newArrayList();

  /** 所属的列 */
  GridColumn belongColumn;

  /** 映射对象的类型名 */
  String resultType;

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
    int hs = 0;
    for (GridColumn nestedColumn : nestedColumns) {
      hs = hs + nestedColumn.getObjKey();
    }
    rowKey = hs;
    return rowKey;
  }

  public void setRowKey(Integer rowKey) {
    this.rowKey = rowKey;
  }

  public GridColumn getBelongColumn() {
    return belongColumn;
  }

  public void setBelongColumn(GridColumn belongColumn) {
    this.belongColumn = belongColumn;
  }

  /**
   * 根据网格头，生成对应的网格行结构
   *
   * @return
   */
  public static GridRow generateRowByHeader(GridHeader gridHeader) {
    if (ObjectUtil.isNull(gridHeader)) {
      return null;
    }
    GridRow gridRow = new GridRow();
    GridColumn gridColumn = new GridColumn();
    gridRow.getNestedColumns().add(gridColumn);
    gridColumn.setBelongRow(gridRow);

    List<GridHeader> headers = gridHeader.getNestedHeaders();
    for (GridHeader header : headers) {
      GridColumn containerColumn = new GridColumn();
      GridRow nestedRow = generateRowByHeader(header);

      containerColumn.getNestedRows().add(nestedRow);
      nestedRow.setBelongColumn(containerColumn);

      gridRow.getNestedColumns().add(containerColumn);
      containerColumn.setBelongRow(gridRow);
    }

    gridColumn.setResultType(gridHeader.getResultType());
    gridRow.setResultType(gridHeader.getResultType());
    return gridRow;
  }
}
