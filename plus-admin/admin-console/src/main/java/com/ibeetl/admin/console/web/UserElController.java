package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.util.VOUtil;
import com.ibeetl.admin.core.entity.CoreRoute;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.service.CoreUserService;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserElController {

  @Autowired private CoreUserService coreUserService;

  @GetMapping("/users/metedata")
  public JsonResult<Map> usersMetedata() {
    return JsonResult.success(VOUtil.resolveElColumn(CoreUser.class));
  }

  @GetMapping("/users")
  public JsonResult<List<CoreUser>> users(Integer page, Integer limit) {
    List<CoreUser> allUsers = coreUserService.getAllUsers(page, limit);
    return JsonResult.success(allUsers);
  }
}
