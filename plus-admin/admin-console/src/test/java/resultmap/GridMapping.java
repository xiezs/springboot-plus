package resultmap;

import static cn.hutool.core.util.StrUtil.EMPTY;
import static java.util.Optional.ofNullable;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import java.util.Map;

/** 网格映射数据结构： 包含一个网格头 {@link GridHeader}和网格容器/体{@link GridContainer} */
public class GridMapping extends GridContainer {
  /** 映射id */
  String mappingId;

  /** 映射类型 */
  String resultType;

  /** 网格头 */
  GridHeader header;

  /** 网格容器 */
  GridContainer container;

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

  public GridContainer getContainer() {
    return container;
  }

  public void setContainer(GridContainer container) {
    this.container = container;
    this.container.setParentContainer(this);
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
}
