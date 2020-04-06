package com.ibeetl.admin.core.web.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibeetl.admin.core.annotation.Query;
import org.beetl.sql.core.engine.PageQuery;

import java.lang.reflect.Field;

/**
 * 子类继承此类获得翻页功能
 *
 * @author lijiazhi
 */
public class PageParam {
  private Integer page = null;
  private Integer limit = null;

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  /**
   * 以当前类中被{@link Query} 注解的字段为SQL参数
   * @author 一日看尽长安花
   * Created on  2020/3/26
   */
  @JsonIgnore
  public PageQuery getPageQuery() {
    Field[] fs = this.getClass().getDeclaredFields();
    for (Field f : fs) {
      Query query = f.getAnnotation(Query.class);
      if (query == null) {
        continue;
      }
      if (query.fuzzy()) {
        try {
          if (f.getType() == String.class) {
            f.setAccessible(true);
            Object o = f.get(this);
            if (o != null && !o.toString().isEmpty()) {
              f.set(this, "%" + o.toString() + "%");
            }
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    PageQuery query = new PageQuery();
    query.setParas(this);
    if (page != null) {
      query.setPageNumber(page);
      query.setPageSize(limit);
    }
    return query;
  }

  /**
   * 以当前类所有字段为SQL 参数
   *
   * @author 一日看尽长安花
   */
  @JsonIgnore
  public PageQuery createPageQuery() {
    PageQuery query = new PageQuery();
    query.setParas(this);
    if (page != null) {
      query.setPageNumber(page);
      query.setPageSize(limit);
    }
    return query;
  }
}
