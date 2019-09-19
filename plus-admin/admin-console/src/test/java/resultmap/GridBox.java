package resultmap;

import java.util.Map;

/**
 * 网格格子，结果集映射的java类型中的基本类型属性，都应该在一个格子中。<br/>
 * 非基本类型的属性字段，应该内嵌一个网格容器
 * <br/>
 * 包含：值映射map、映射类型、最关键的obj key<br/>
 * objkey 决定了数据库结果集的多条重复记录会被唯一记录成一个对象。
 * */
public class GridBox extends GridContainer {

  /** 对应的属性与数据库中值的映射，用于map to Bean 转换 */
  Map<String, Object> beanMap;

  /** 映射类型*/
  String resultType;

  /** 数据库记录对应的唯一key，用 beanMap 中所有非null值的hashcode相加得出*/
  String objKey;

  /** 嵌套的网格容器，说明结果集的映射中有个集合属性字段*/
  GridContainer nestedContainer;

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

  public String getObjKey() {
    return objKey;
  }

  public void setObjKey(String objKey) {
    this.objKey = objKey;
  }

  public GridContainer getNestedContainer() {
    return nestedContainer;
  }

  public void setNestedContainer(GridContainer nestedContainer) {
    this.nestedContainer = nestedContainer;
  }

  public GridRow getBelongRow() {
    return belongRow;
  }

  public void setBelongRow(GridRow belongRow) {
    this.belongRow = belongRow;
  }
}