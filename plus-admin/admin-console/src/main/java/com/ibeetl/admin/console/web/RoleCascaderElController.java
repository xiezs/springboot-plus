package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.CoreOrgConsoleElService;
import com.ibeetl.admin.core.entity.CoreRole;
import com.ibeetl.admin.core.entity.ElCascaderData;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.CoreRoleService;
import com.ibeetl.admin.core.util.enums.RoleTypeEnum;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色字典数据获取
 *
 * @author 一日看尽长安花
 */
@Slf4j
@RestController
@RequestMapping(value = "/core/roles")
public class RoleCascaderElController {

  @Autowired CorePlatformService corePlatformService;

  @Autowired CoreOrgConsoleElService coreOrgConsoleElService;

  @Autowired CoreRoleService coreRoleService;

  @GetMapping("/immediateLoad")
  public JsonResult<List<ElCascaderData>> immediateLoadCascaderDict() {
    List<CoreRole> childDictList = coreRoleService.getAllRoles(RoleTypeEnum.ACCESS.getValue());
    return JsonResult.success(coreRoleToElCascaderData(childDictList));
  }

  /**
   * .将字典树转成element级联器组件的格式
   *
   * @author 一日看尽长安花
   * @return List<ElCascaderData>
   */
  private List<ElCascaderData> coreRoleToElCascaderData(List<CoreRole> coreRoles) {
    List<ElCascaderData> elCascaderDatas = new ArrayList<ElCascaderData>(coreRoles.size());
    for (CoreRole role : coreRoles) {
      ElCascaderData data = new ElCascaderData();
      data.setId(role.getId());
      data.setLabel(role.getName());
      data.setValue(role.getId());
      elCascaderDatas.add(data);
    }
    return elCascaderDatas.isEmpty() ? null : elCascaderDatas;
  }
}
