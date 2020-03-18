package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.UserConsoleService;
import com.ibeetl.admin.console.util.VOUtil;
import com.ibeetl.admin.console.web.query.CoreUserElQuery;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.param.CoreUserParam;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.Arrays;
import java.util.Map;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/users")
@RestController
public class UserConsoleElController {

  @Autowired private UserConsoleService userConsoleService;

  @Autowired private CorePlatformService platformService;

  @Function("user.query")
  @GetMapping("/metadata")
  public JsonResult<Map> usersMetedata() {
    return JsonResult.success(VOUtil.resolveElColumn(CoreUserElQuery.class));
  }

  @Function("user.query")
  @GetMapping
  public JsonResult<PageQuery<CoreUser>> users(CoreUserParam coreUserParam) {
    PageQuery<CoreUser> allUsers = userConsoleService.getUsersByComplexSelect(coreUserParam);
    return JsonResult.success(allUsers);
  }

  @Function("user.add")
  @PostMapping
  public JsonResult<Long> addUser(@Validated(ValidateConfig.ADD.class) @RequestBody CoreUser user) {
    if (!platformService.isAllowUserName(user.getCode())) {
      return JsonResult.failMessage("不允许的注册名字 " + user.getCode());
    }
    userConsoleService.saveUser(user);
    return JsonResult.success(user.getId());
  }

  @Function("user.update")
  @PutMapping
  public JsonResult update(@Validated(ValidateConfig.UPDATE.class) @RequestBody CoreUser user) {
    boolean success = userConsoleService.updateTemplate(user);
    if (success) {
      this.platformService.clearFunctionCache();
      return JsonResult.success();
    } else {
      return JsonResult.failMessage("保存失败！");
    }
  }

  @Function("user.delete")
  @DeleteMapping
  public JsonResult delete(@RequestBodyPlus("ids") Long[] ids) {
    userConsoleService.batchDelSysUser(Arrays.asList(ids));
    return JsonResult.success();
  }
}
