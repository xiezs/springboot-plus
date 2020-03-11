package com.ibeetl.admin.console.web;

import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.ElCascaderData;
import com.ibeetl.admin.console.service.CoreOrgConsoleElService;
import com.ibeetl.admin.core.service.CoreOrgService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典数据获取
 *
 * @author 一日看尽长安花
 */
@Slf4j
@RestController
@RequestMapping(value = "/core/orgs")
public class OrgCascaderElController {

  @Autowired CorePlatformService corePlatformService;

  @Autowired CoreOrgConsoleElService coreOrgConsoleElService;

  @Autowired CoreOrgService coreOrgService;

  @GetMapping("/immediateLoad")
  public JsonResult<List<ElCascaderData>> immediateLoadCascaderDict(Long parentId) {
    List<CoreOrg> childDictList = coreOrgService.findChildrenNodes(parentId);
    return JsonResult.success(coreOrgToElCascaderData(childDictList));
  }

  /**
   * .将字典树转成element级联器组件的格式
   *
   * @author 一日看尽长安花
   * @return List<ElCascaderData>
   */
  private List<ElCascaderData> coreOrgToElCascaderData(List<CoreOrg> coreDicts) {
    List<ElCascaderData> elCascaderDatas = new ArrayList<ElCascaderData>(coreDicts.size());
    for (CoreOrg org : coreDicts) {
      ElCascaderData data = new ElCascaderData();
      data.setId(org.getId());
      data.setLabel(org.getName());
      data.setValue(org.getId());
      List<ElCascaderData> children = coreOrgToElCascaderData(org.getChidren());
      data.setChildren(children);
      elCascaderDatas.add(data);
    }
    return elCascaderDatas.isEmpty() ? null : elCascaderDatas;
  }

  /**
   * Method obtainDcitsByCascader ...<br>
   * 获取机构级联。如果是admin超级管理，可以获取所有的机构。<br>
   * 如果不是，则根据当前登录人的机构id获取根级机构
   *
   * @param parentId of type Long 父级id
   * @return JsonResult<List < ElCascaderData>>
   */
  @GetMapping("/layzyLoad")
  public JsonResult<List<ElCascaderData>> obtainOrgsByCascader(Long parentId) {
    List<CoreOrg> coreOrgList = coreOrgConsoleElService.getOrgListByParent(parentId);
    return JsonResult.success(convertToCascaderData(coreOrgList));
  }

  private List<ElCascaderData> convertToCascaderData(List<CoreOrg> coreOrgs) {
    List<ElCascaderData> cascaderDataArrayList = new ArrayList<ElCascaderData>(coreOrgs.size());
    for (CoreOrg org : coreOrgs) {
      ElCascaderData data = new ElCascaderData();
      data.setId(org.getId());
      data.setLabel(org.getName());
      data.setValue(org.getId());
      cascaderDataArrayList.add(data);
    }
    return cascaderDataArrayList;
  }
}
