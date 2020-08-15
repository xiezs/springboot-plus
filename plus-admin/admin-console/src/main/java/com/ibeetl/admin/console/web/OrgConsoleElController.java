package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.OrgConsoleService;
import com.ibeetl.admin.console.service.UserConsoleService;
import com.ibeetl.admin.console.web.query.OrgQuery;
import com.ibeetl.admin.console.web.query.OrgUserQuery;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.Query;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.AnnotationUtil;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 描述: 组织机构 controller
 *
 * @author : xiandafu
 */
@Slf4j
@RequestMapping("/admin/orgs")
@Controller
public class OrgConsoleElController {

  @Autowired
  private OrgConsoleService orgConsoleService;

  @Autowired
  UserConsoleService userConsoleService;

  @Autowired
  CorePlatformService platformService;


  /**
   * 组织机构列表  分页
   *
   * @param condtion
   *     查询条件
   */
  @GetMapping("/list")
  @Function("org.query")
  @ResponseBody
  public JsonResult<PageQuery<CoreOrg>> list(OrgQuery condtion) {

    PageQuery page = condtion.getPageQuery();
    orgConsoleService.queryByCondtion(page);
    return JsonResult.success(page);
  }

  /**
   * 获取列表查询条件
   */
  @GetMapping("/list/condition")
  @Function("org.query")
  @ResponseBody
  public JsonResult<List<Map<String, Object>>> listCondtion() {

    List<Map<String, Object>> list = AnnotationUtil
        .getInstance().getAnnotations(Query.class, OrgQuery.class);
    return JsonResult.success(list);
  }

  /**
   * 保存数据
   */
  @PostMapping("/save")
  @Function("org.save")
  @ResponseBody
  public JsonResult<Long> save(@Validated(ValidateConfig.ADD.class) @RequestBody CoreOrg org,
      BindingResult result) {

    if (result.hasErrors()) {
      return JsonResult.failMessage(result.toString());

    }

    org.setCreateTime(new Date());
    orgConsoleService.save(org);
    platformService.clearOrgCache();
    return JsonResult.success(org.getId());
  }


  /**
   * 更新数据
   */
  @PutMapping("/update")
  @Function("org.update")
  @ResponseBody
  public JsonResult<String> update(
      @Validated(ValidateConfig.UPDATE.class) @RequestBody CoreOrg org) {

    boolean success = orgConsoleService.updateTemplate(org);
    if (success) {
      platformService.clearOrgCache();
      return JsonResult.successMessage("保存成功");
    } else {
      return JsonResult.failMessage("保存失败");
    }
  }


  /**
   * 删除组织机构
   *
   * @param ids
   *     组织id，多个用“,”隔开
   */
  @DeleteMapping("/delete")
  @Function("org.delete")
  @ResponseBody
  public JsonResult delete(@RequestBodyPlus("ids") List<Long> ids) {

    orgConsoleService.deleteById(ids);
    this.platformService.clearOrgCache();
    return JsonResult.success();
  }


  @GetMapping("/user/list")
  @Function("org.query")
  @ResponseBody
  public JsonResult<PageQuery<CoreUser>> getUsers(OrgUserQuery userQuery) {

    PageQuery<CoreUser> page = userQuery.getPageQuery();
    orgConsoleService.queryUserByCondition(page);
    return JsonResult.success(page);
  }

}
