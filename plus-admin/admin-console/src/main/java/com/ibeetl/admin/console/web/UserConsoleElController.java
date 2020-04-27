package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.UserConsoleService;
import com.ibeetl.admin.console.util.VOUtil;
import com.ibeetl.admin.console.web.dto.UserExcelExportData;
import com.ibeetl.admin.console.web.query.CoreUserElQuery;
import com.ibeetl.admin.console.web.query.UserRoleQuery;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.entity.CoreUserRole;
import com.ibeetl.admin.core.file.FileItem;
import com.ibeetl.admin.core.file.FileService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.param.CoreUserParam;
import com.ibeetl.admin.core.util.PlatformException;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.web.JsonResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.beetl.sql.core.engine.PageQuery;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/users")
@RestController
public class UserConsoleElController {

  @Autowired private UserConsoleService userConsoleService;

  @Autowired private CorePlatformService platformService;

  @Autowired FileService fileService;

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

  @Function("user.query")
  @GetMapping("/{id}")
  public JsonResult<CoreUser> users(@PathVariable("id") Long id) {
    CoreUser coreUser = userConsoleService.queryUserById(id);
    return JsonResult.success(coreUser);
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

  /** 用户所有授权角色列表 */
  @GetMapping("/roles")
  @Function("user.role")
  @ResponseBody
  public JsonResult<List<CoreUserRole>> getRoleList(UserRoleQuery roleQuery) {
    List<CoreUserRole> list = userConsoleService.getUserRoles(roleQuery);
    return JsonResult.success(list);
  }

  /**
   * 用户添加授权角色页
   *
   * @return
   */
  @PostMapping("/roles")
  @Function("user.role")
  @ResponseBody
  public JsonResult saveUserRole(@Validated @RequestBody CoreUserRole userRole) {
    Date now = new Date();
    userRole.setCreateTime(now);
    this.userConsoleService.saveUserRole(userRole);
    this.platformService.clearFunctionCache();
    return JsonResult.success(userRole.getId());
  }

  /**
   * 删除用户角色授权
   *
   * @return
   */
  @DeleteMapping("/roles")
  @Function("user.role")
  @ResponseBody
  public JsonResult delUserRole(@RequestBodyPlus("ids") ArrayList<Long> ids) {
    userConsoleService.deleteUserRoles(ids);
    this.platformService.clearFunctionCache();
    return JsonResult.success();
  }

  @PostMapping("/excel/export")
  @Function("user.export")
  public JsonResult<String> export(@RequestBody CoreUserParam coreUserParam) {
    String excelTemplate = "excelTemplates/admin/user/user_collection_template.xls";
    List<UserExcelExportData> users = userConsoleService.queryExcel(coreUserParam);
    try (InputStream is =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(excelTemplate)) {
      if (is == null) {
        throw new PlatformException("模板资源不存在：" + excelTemplate);
      }
      FileItem item = fileService.createFileTemp("user_collection.xls");
      OutputStream os = item.openOutpuStream();
      Context context = new Context();
      context.putVar("users", users);
      JxlsHelper.getInstance().processTemplate(is, os, context);
      // 下载参考FileSystemContorller
      return JsonResult.success(item.getPath());
    } catch (IOException e) {
      throw new PlatformException(e.getMessage());
    }
  }
}
