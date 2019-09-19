package resultmap;

import static java.util.Optional.ofNullable;

import cn.hutool.core.collection.CollUtil;
import java.util.List;

/** 网格行，包含一个个格子<br> */
public class GridRow {

  /** 包含的格子 */
  List<GridBox> gridBoxes = CollUtil.<GridBox>newArrayList();

  GridBox rowObjBox;

  /** 映射类型 */
  String resultType;

  String rowKey;

  GridContainer belongContainer;

  public GridRow() {}

  public GridRow(GridHeader gridHeader) {
    rowObjBox.setResultType(gridHeader.getResultType());
    rowObjBox.setBelongRow(this);
    generateGridRow(gridHeader);
  }

  public List<GridBox> getGridBoxes() {
    return gridBoxes;
  }

  public void setGridBoxes(List<GridBox> gridBoxes) {
    this.gridBoxes = gridBoxes;
  }

  public GridBox getRowObjBox() {
    return rowObjBox;
  }

  public void setRowObjBox(GridBox rowObjBox) {
    this.rowObjBox = rowObjBox;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getRowKey() {
    return rowKey;
  }

  public void setRowKey(String rowKey) {
    this.rowKey = rowKey;
  }

  public GridContainer getBelongContainer() {
    return belongContainer;
  }
  public void setBelongContainer(GridContainer belongContainer) {
    this.belongContainer = belongContainer;
  }

  /**
   * 根据网格头，生成对应的网格行结构
   *
   * @return
   */
  private GridBox generateGridRow(GridHeader gridHeader) {
    ofNullable(gridHeader.getNestedHeader())
        .ifPresent(
            nestedHeader -> {
              generateGridRow(nestedHeader);
            });

    GridBox gridBox = new GridBox();

    if (gridHeader.getIsCollection()) {
      GridContainer nestedContainer = new GridContainer();
      GridRow nestedRow = new GridRow();
      GridBox nestedBox = new GridBox();

      nestedContainer.getGridRows().add(nestedRow);
      nestedContainer.setParentContainer(gridBox);

      nestedRow.getGridBoxes().add(nestedBox);

      nestedBox.setBelongRow(nestedRow);
      nestedBox.setResultType(gridHeader.getResultType());

      /*嵌套和非嵌套只能存在一个*/
      gridBox.setNestedContainer(nestedContainer);
      gridBox.setBeanMap(null);
    } else {
      gridBox.setResultType(gridHeader.getResultType());
    }

    return gridBox;
  }
}
