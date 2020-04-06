package com.ibeetl.admin.core.conf.beetl.resultmap;

import static cn.hutool.core.util.StrUtil.EMPTY;
import static java.util.Optional.ofNullable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** 网格映射数据结构： 包含一个网格头 {@link GridHeader}和一个虚拟网格单元格 */
public class GridMapping implements Serializable {
  /** 映射id */
  String mappingId;

  /** 当前映射配置对应的class类型 */
  String resultType;

  /** root节点 */
  GridHeader header;

  /** 虚拟root节点，为了递归算法需要一个虚拟root节点*/
  GridCell virtualCell;

  public GridMapping(Map<String, Object> resultMapping) {
    JSON parse = JSONUtil.parse(resultMapping);
    String id = ofNullable(parse.getByPath("id")).orElse(EMPTY).toString();
    String resultType = ofNullable(parse.getByPath("mapping.resultType")).orElse(EMPTY).toString();
    Assert.notBlank(id, "result mapping must have [id].");
    Assert.notBlank(resultType, "result mapping must have [resultType].");
    this.mappingId = id;
    this.resultType = resultType;
    this.setHeader(new GridHeader((Map<String, Object>) parse.getByPath("mapping"), this));
    this.virtualCell=new GridCell();
    this.virtualCell.setHasContainer(true);
    this.virtualCell.setRelationGridHeader(this.getHeader());
  }

  public GridHeader getHeader() {
    return header;
  }

  public void setHeader(GridHeader header) {
    this.header = header;
    this.header.setBelongMapping(this);
  }

  public String getMappingId() {
    return mappingId;
  }

  public void setMappingId(String mappingId) {
    this.mappingId = mappingId;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public GridCell getVirtualCell() {
    return virtualCell;
  }

  public void setVirtualCell(GridCell virtualCell) {
    this.virtualCell = virtualCell;
  }
}
