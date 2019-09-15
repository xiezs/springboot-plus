package com.ibeetl.admin.core.entity;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;

/**
 * 对应前端页面的routes路由表。具体格式如下： { "path": "/profile", "name": "router-name", "meta": { "title":
 * "Profile", "roles": ["admin", "editor"], "icon": "user" }, "children": [] }
 *
 * @author 一日看尽长安花
 */
@Data
public class CoreRoute extends BaseEntity implements Comparable<CoreRoute> {

  private Long id;

  private Long parentId;

  /** 路由路径，完全参照vue router规范 */
  private String path;

  /** 路由名称，请确保唯一性 */
  private String name;

  /** 路由顺序 */
  private Long seq = -999L;

  /** 路由元数据信息 */
  private CoreRouteMeta meta;

  /** 子路由项 */
  private List<CoreRoute> children = CollUtil.newArrayList();

  public CoreRouteMeta getMeta() {
    if (ObjectUtil.isNotEmpty(this.meta)) return this.meta;
    Map<String, Object> metaMap = MapUtil.builder(getTails()).build();
    this.meta = BeanUtil.mapToBean(metaMap, CoreRouteMeta.class, true);
    return this.meta;
  }

  @Override
  public boolean equals(Object o) {
    if (o != null && o instanceof CoreRoute) {
      CoreRoute othRoute = (CoreRoute) o;
      if (this.getId().equals(othRoute.getId())) {
        return true;
      }
      if (this.getParentId().equals(othRoute.getParentId())) {
        return StrUtil.equals(this.getPath(), othRoute.getPath())
            && StrUtil.equals(this.getName(), othRoute.getName());
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, parentId, path, name);
  }

  @Override
  public int compareTo(CoreRoute o) {
    if (null == o) return -1;
    Long seq1 = this.getSeq() == null ? -999L : this.getSeq();
    Long seq2 = o.getSeq() == null ? -999L : o.getSeq();
    return seq1.compareTo(seq2);
  }
}
