package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.util.VOUtil;
import com.ibeetl.admin.console.web.query.CoreUserElQuery;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.service.CoreUserService;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class CoreUserElController {

  @Autowired private CoreUserService coreUserService;

  @GetMapping("/users/metadata")
  public JsonResult<Map> usersMetedata() {
    return JsonResult.success(VOUtil.resolveElColumn(CoreUserElQuery.class));
  }

  @GetMapping("/users")
  public JsonResult<PageQuery<CoreUser>> users(@NotNull Integer page, @NotNull Integer limit) {
    PageQuery<CoreUser> allUsers = coreUserService.getAllUsers(page, limit);
    return JsonResult.success(allUsers);
  }
}
