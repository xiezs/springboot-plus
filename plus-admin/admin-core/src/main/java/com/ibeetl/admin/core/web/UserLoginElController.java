package com.ibeetl.admin.core.web;

import cn.hutool.core.map.MapUtil;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreRoute;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.rbac.UserLoginInfo;
import com.ibeetl.admin.core.rbac.UserRoleInfo;
import com.ibeetl.admin.core.service.RoleRoutesElService;
import com.ibeetl.admin.core.service.CoreUserService;
import com.ibeetl.admin.core.util.HttpRequestLocal;
import com.ibeetl.admin.core.util.JoseJwtUtil;
import com.ibeetl.admin.core.util.PlatformException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("core/user")
@RestController
public class UserLoginElController {

  @Autowired private RoleRoutesElService roleRoutesService;

  @Autowired private CoreUserService coreUserService;

  @GetMapping("/routes")
  public JsonResult<List<CoreRoute>> routes() {
    return JsonResult.success(roleRoutesService.getRoutes());
  }

  @GetMapping("/info")
  public JsonResult info() {
    CoreOrg currentOrg = HttpRequestLocal.getAccessCurrentOrg();
    CoreUser currentUser = HttpRequestLocal.getAccessCurrentUser();
    List<Long> roleIds = roleRoutesService.getAllRoleIds(currentUser.getId(), currentOrg.getId());
    UserRoleInfo userRoleInfo = new UserRoleInfo();
    userRoleInfo.setAvatar("avatar");
    userRoleInfo.setIntroduction("introduction");
    userRoleInfo.setName(currentUser.getName());
    userRoleInfo.setRoles(roleIds.toArray(new Long[0]));
    return JsonResult.success(userRoleInfo);
  }

  @PostMapping("/login")
  public JsonResult loginEle(
      @RequestBodyPlus("username") String username, @RequestBodyPlus("password") String password) {
    UserLoginInfo info = coreUserService.login(username, password);
    if (info == null) {
      throw new PlatformException("用户名密码错误");
    }
    CoreUser user = info.getUser();
    CoreOrg currentOrg = info.getOrgs().stream().findFirst().orElse(null);

    for (CoreOrg org : info.getOrgs()) {
      Long orgId = user.getOrgId();
      if (org.getId().equals(orgId)) {
        currentOrg = org;
        break;
      }
    }

    info.setCurrentOrg(currentOrg);
    // 记录登录信息到session
    HttpRequestLocal.setLoginerInfo(info.getUser(), info.getCurrentOrg(), info.getOrgs());
    Map<String, Object> resultMap =
        MapUtil.<String, Object>builder()
            .put("token", JoseJwtUtil.generateJwtToken(String.valueOf(user.getId())))
            .build();
    return JsonResult.success(resultMap);
  }

  @PostMapping("/logout")
  public JsonResult logout() {
    HttpRequestLocal.clearAllSession();
    return JsonResult.success();
  }
}
