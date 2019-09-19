package resultmap;

import cn.hutool.core.collection.CollUtil;
import java.util.List;

/** 网格容器，仅仅存放网格行，也只存放网格行 */
public class GridContainer {

  List<GridRow> gridRows = CollUtil.newArrayList();

  int count;

  GridContainer parentContainer;

  public List<GridRow> getGridRows() {
    return gridRows;
  }

  public void setGridRows(List<GridRow> gridRows) {
    this.gridRows = gridRows;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public GridContainer getParentContainer() {
    return parentContainer;
  }

  public void setParentContainer(GridContainer parentContainer) {
    this.parentContainer = parentContainer;
  }
}
