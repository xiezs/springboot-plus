package resultmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
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
  Map<String, Object> beanMap;

  /** 映射类型 */
  String resultType;

  /** 数据库记录对应的唯一key，用 beanMap 中所有非null值的hashcode相加得出 */
  Integer objKey;

  /** 包含的网格行 */
  List<GridRow> nestedRows = CollUtil.<GridRow>newArrayList();

  /** 归属的网格行 */
  GridRow belongRow;

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
    int hs = 0;
    if (CollUtil.isNotEmpty(nestedRows)) {
      for (GridRow nestedRow : nestedRows) {
        hs = hs + nestedRow.getRowKey();
      }
    } else {
      Set<Entry<String, Object>> entrySet = beanMap.entrySet();
      for (Entry<String, Object> entry : entrySet) {
        if (ObjectUtil.isNull(entry.getValue())) continue;
        hs += entry.getValue().hashCode();
      }
    }
    hs = hs / RandomUtil.randomInt(100);
    objKey = hs;
    return objKey;
  }

  public void setObjKey(Integer objKey) {
    this.objKey = objKey;
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
}
