package resultmap;

import static cn.hutool.core.util.StrUtil.EMPTY;
import static java.util.Optional.ofNullable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** 网格映射数据结构： 包含一个网格头 {@link GridHeader}和多个网格行{@link GridRow} */
public class GridMapping implements Serializable {
  /** 映射id */
  String mappingId;

  /** 当前映射配置对应的class类型 */
  String resultType;

  /** 网格头 */
  GridHeader header;

  List<GridRow> nestedRows = CollUtil.newArrayList();

  public GridMapping(Map<String, Object> resultMapping) {
    JSON parse = JSONUtil.parse(resultMapping);
    String id = ofNullable(parse.getByPath("id")).orElse(EMPTY).toString();
    String resultType = ofNullable(parse.getByPath("mapping.resultType")).orElse(EMPTY).toString();
    Assert.notBlank(id, "result mapping must have [id].");
    Assert.notBlank(resultType, "result mapping must have [resultType].");
    this.mappingId = id;
    this.resultType = resultType;
    this.header = new GridHeader((Map<String, Object>) parse.getByPath("mapping"));
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

  public List<GridRow> getNestedRows() {
    return nestedRows;
  }

  public void setNestedRows(List<GridRow> nestedRows) {
    this.nestedRows = nestedRows;
  }

  public GridRow nextRow(){
    GridRow row = GridRow.generateRowByHeader(this.header);
    this.nestedRows.add(row);
    return row;
  }
}
