package com.ibeetl.admin.core.service.vea;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.dao.CoreRoleDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import com.ibeetl.admin.core.entity.CoreUserRole;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class RoleRoutesService {

  @Autowired private CoreFunctionDao coreFunctionDao;

  @Autowired private CoreRoleDao coreRoleDao;

  public List<Long> getAllRoleIds(Long userId, Long orgId) {
    return coreRoleDao.getSQLManager().lambdaQuery(CoreUserRole.class).andEq("user_id", userId)
        .andEq("org_id", orgId).select(Long.class, "role_id").stream()
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   *
   * 前端路由映射表中单个路由映射全部具有的信息：
   *   { <br/>
   *     "path": "/profile", <br/>
   *     "component": "Layout", <br/>
   *     "redirect": "/profile/index", <br/>
   *     "hidden": true, <br/>
   *     "alwaysShow": true, <br/>
   *     "name": "router-name", <br/>
   *     "meta": { <br/>
   *       "noCache": true, <br/>
   *       "affix": true, <br/>
   *       "breadcrumb": false, <br/>
   *       "activeMenu": "/example/list" <br/>
   *     }, <br/>
   *     "children": [] <br/>
   *   } <br/>
   * 后端路由表中单个路由应该具有的信息： <br/>
   *   { <br/>
   *     "path": "/profile", <br/>
   *     "name": "router-name", <br/>
   *     "meta": { <br/>
   *       "title": "Profile", <br/>
   *       "roles": ["admin", "editor"], <br/>
   *       "icon": "user" <br/>
   *     }, <br/>
   *     "children": [] <br/>
   *   } <br/>
   *
   * @return 路由表格式
   * */
  public List<CoreRoute> getRoutes() {
    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    Map<Long, List<Long>> roleIdList =
        routesList.stream()
            .collect(
                Collectors.groupingBy(
                    CoreRoute::getId,
                    Collectors.mapping(
                        coreRoute -> Convert.toLong(coreRoute.getTails().get("roleId")),
                        Collectors.toList())));
    routesList = routesList.stream().distinct().sorted().collect(Collectors.toList());
    for (CoreRoute coreRoute : routesList) {
      coreRoute
          .getMeta()
          .getRoles()
          .addAll(CollUtil.<Long>removeNull(roleIdList.get(coreRoute.getId())));
    }
    CoreRoute root = new CoreRoute();
    root.setId(0L);
    buildRoutesTree(root, routesList);
    return CollUtil.getFirst(root.getChildren()).getChildren();
  }

  /**
   * 深度优先算法递归构建路由表
   *
   * @param root
   * @param allRoutes
   * @return
   */
  private void buildRoutesTree(CoreRoute root, @NotNull List<CoreRoute> allRoutes) {
    if (CollUtil.isEmpty(allRoutes)) return;
    List<CoreRoute> childRoutes =
        allRoutes.stream()
            .filter(route -> route.getParentId().equals(root.getId()))
            .collect(Collectors.toList());
    root.setChildren(childRoutes);
    allRoutes.removeAll(childRoutes);
    Collections.sort(root.getChildren());
    List<CoreRoute> rootChildrenList = root.getChildren();
    for (int i = 0; i < rootChildrenList.size(); i++) {
      buildRoutesTree(rootChildrenList.get(i), allRoutes);
    }
  }
}
