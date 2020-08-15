package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.MenuConsoleService;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreFunction;
import com.ibeetl.admin.core.entity.CoreMenu;
import com.ibeetl.admin.core.rbac.tree.MenuItem;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.ConvertUtil;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("admin/menus")
@RestController
public class MenuElController {
  

  @Autowired
  MenuConsoleService menuConsoleService;

  @Autowired
  CorePlatformService platformService;

  @GetMapping
  @Function("menu.query")
  public JsonResult<List<CoreMenu>> menusTree() {
    List<CoreMenu> menuTreeItems = menuConsoleService.getMenusTree();
    return JsonResult.success(menuTreeItems);
  }

  /**
   * 添加
   *
   * @param menu
   * @return
   */
  @PostMapping
  @Function("menu.save")
  @ResponseBody
  public JsonResult save(@Validated @RequestBody CoreMenu menu) {
    Long id = menuConsoleService.saveMenu(menu);
    return JsonResult.success(id);
  }

  /**
   * 更新
   *
   * @param menu
   * @return
   */
  @PutMapping
  @Function("menu.update")
  @ResponseBody
  public JsonResult update(@RequestBody CoreMenu menu) {

    menuConsoleService.updateMenu(menu);
    return JsonResult.success();
  }

  /**
   * 批量删除
   *
   * @param ids 菜单id集合
   * @return
   */
  @DeleteMapping
  @Function("menu.delete")
  @ResponseBody
  public JsonResult delete(@RequestBodyPlus("ids") ArrayList<Long> ids) {

    menuConsoleService.batchDeleteMenuId(ids);
    return JsonResult.success();
  }
}
