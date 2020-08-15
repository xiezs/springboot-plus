package com.ibeetl.admin.console.service;

import static com.ibeetl.admin.core.service.CorePlatformService.MENU_FUNC_TREE_CACHE;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ibeetl.admin.console.dao.MenuConsoleDao;
import com.ibeetl.admin.core.dao.CoreRoleMenuDao;
import com.ibeetl.admin.core.entity.CoreMenu;
import com.ibeetl.admin.core.rbac.tree.MenuItem;
import com.ibeetl.admin.core.service.CoreBaseService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.PlatformException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuConsoleService extends CoreBaseService<CoreMenu> {

  @Autowired
  MenuConsoleDao menuDao;

  @Autowired
  CoreRoleMenuDao roleMenuDao;

  @Autowired
  CorePlatformService platformService;

  public void queryByCondtion(PageQuery<CoreMenu> query) {

    menuDao.queryByCondtion(query);
    handleStrDictValueFields(query.getList());
  }

  public Long saveMenu(CoreMenu menu) {

    CoreMenu query = new CoreMenu();
    query.setCode(menu.getCode());
    long queryCount = menuDao.templateCount(query);
    if (queryCount > 0) {
      throw new PlatformException("菜单编码已存在");
    }
    menu.setCreateTime(new Date());
    menu.setParentMenuId(menu.getParent().getId());
    menu.setFunctionId(menu.getRelationFunction().getId());
    menuDao.insert(menu, true);
    platformService.clearMenuCache();
    return menu.getId();
  }

  public void deleteMenu(Long menuId) {

    deleteMenuId(menuId);
  }

  public void batchDeleteMenuId(List<Long> menuIds) {

    for (Long id : menuIds) {
      deleteMenuId(id);
    }
    platformService.clearMenuCache();
  }

  public void updateMenu(CoreMenu menu) {

    menu.setParentMenuId(menu.getParent().getId());
    menu.setFunctionId(menu.getRelationFunction().getId());
    menuDao.updateById(menu);
    platformService.clearMenuCache();
  }

  public CoreMenu getMenu(Long menuId) {

    CoreMenu menu = menuDao.unique(menuId);
    platformService.clearMenuCache();
    return menu;
  }

  private void deleteMenuId(Long menuId) {

    MenuItem root = platformService.buildMenu();
    MenuItem fun = root.findChild(menuId);
    List<MenuItem> all = fun.findAllItem();
    // 也删除自身
    all.add(fun);
    realDeleteMenu(all);
  }

  private void realDeleteMenu(List<MenuItem> all) {

    List<Long> ids = new ArrayList<>(all.size());
    for (MenuItem item : all) {
      ids.add(item.getId());
      this.menuDao.deleteById(item.getId());
    }
    // 删除角色和菜单的关系
    roleMenuDao.deleteRoleMenu(ids);
  }

  /**
   * @return 菜单树，排序
   */
  @Cacheable(MENU_FUNC_TREE_CACHE)
  public List<CoreMenu> getMenusTree() {

    List<CoreMenu> coreMenuList = menuDao.selectMenuAndRelationFunction();
    System.out.println(coreMenuList);

    CoreMenu root = new CoreMenu();
    root.setId(0L);
    buildTree(root, coreMenuList);
    return root.getChildren();
  }

  /**
   * 深度优先算法递归构建菜单树，根据seq排序
   */
  private void buildTree(CoreMenu root, @NotNull List<CoreMenu> allNodes) {

    if (CollUtil.isEmpty(allNodes)) {
      return;
    }
    List<CoreMenu> childNodes =
        allNodes.stream()
            .filter(route -> ObjectUtil.equal(route.getParentMenuId(), root.getId()))
            .sorted()
            .collect(Collectors.toList());
    root.setChildren(childNodes);
    allNodes.removeAll(childNodes);
    List<CoreMenu> rootChildrenList = root.getChildren();
    for (CoreMenu coreFunction : rootChildrenList) {
      buildTree(coreFunction, allNodes);
    }
  }

}
