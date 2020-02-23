package com.ibeetl.admin.core.conf.beetl.resultmap;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * 网格列，结果集映射的java类型中的基本类型属性，都应该在一个列中。<br>
 * 非基本类型的属性字段，应该内嵌多行 <br>
 * 包含：值映射map、映射类型、最关键的obj key<br>
 * objkey 决定了数据库结果集的多条重复记录会被唯一记录成一个对象。
 */
public class GridCell implements Serializable {

  /** 对应的属性与数据库中值的映射，用于map to Bean 转换 */
  Map<String, Object> beanMap = MapUtil.newHashMap();

  /** 映射类型 */
  Class resultType;

  /** 一个单元格包含一个对象中的所有数据列，通过对这些数据列进行hash得到数据列的唯一性 */
  Integer hashKey;

  /** 包含的网格单元格 */
  List<GridCell> nestedCell = CollUtil.<GridCell>newArrayList();

  /** 归属的网格单元格 */
  GridCell parentCell;

  GridHeader relationGridHeader;

  /** 是否是作为容器 */
  boolean hasContainer;

  public Map<String, Object> getBeanMap() {
    return beanMap;
  }

  public void setBeanMap(Map<String, Object> beanMap) {
    this.hashKey = calculateHashKey(beanMap);
    this.beanMap = beanMap;
  }

  public Class getResultType() {
    return resultType;
  }

  public void setResultType(Class resultType) {
    this.resultType = resultType;
  }

  public Integer getHashKey() {
    return hashKey;
  }

  public void setHashKey(Integer hashKey) {
    this.hashKey = hashKey;
  }

  public boolean isHasContainer() {
    return hasContainer;
  }

  public void setHasContainer(boolean hasContainer) {
    this.hasContainer = hasContainer;
  }

  public static Integer calculateHashKey(Map<String, Object> map) {
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

  public List<GridCell> getNestedCells() {
    return nestedCell;
  }

  public void setNestedCell(List<GridCell> nestedCell) {
    this.nestedCell = nestedCell;
  }

  public GridCell getParentCell() {
    return parentCell;
  }

  public void setParentCell(GridCell parentCell) {
    this.parentCell = parentCell;
  }

  public void setRelationGridHeader(GridHeader relationGridHeader) {
    this.relationGridHeader = relationGridHeader;
  }

  public GridHeader getRelationGridHeader() {
    return this.relationGridHeader;
  }

  public GridCell addOrCreateNestedCell(GridHeader header, Map<String, Object> beanMap) {
    GridCell contentCell = new GridCell();
    contentCell.setHasContainer(false);
    contentCell.setRelationGridHeader(header);
    contentCell.setResultType(header.getResultType());
    contentCell.setBeanMap(beanMap);

    List<GridCell> realContainerCellList = this.getNestedCells();
    for (GridCell cell : realContainerCellList) {
      GridCell first = CollUtil.getFirst(cell.getNestedCells());
      if (first.getHashKey().equals(contentCell.getHashKey())) {
        return cell;
      }
    }

    GridCell realContainerCell = new GridCell();
    realContainerCell.setResultType(header.getResultType());
    realContainerCell.setParentCell(this);
    realContainerCell.setRelationGridHeader(header);
    realContainerCell.setHasContainer(true);

    realContainerCell.getNestedCells().add(contentCell);

    this.getNestedCells().add(realContainerCell);
    return realContainerCell;
  }

  public GridCell findVirtualCell(GridHeader header) {
    for (GridCell cell : this.nestedCell) {
      if (cell.getRelationGridHeader().equals(header)) {
        return cell;
      }
    }

    GridCell virtualCell = new GridCell();
    virtualCell.setParentCell(this);
    virtualCell.setRelationGridHeader(header);
    virtualCell.setHasContainer(true);

    this.getNestedCells().add(virtualCell);
    return virtualCell;
  }
}
