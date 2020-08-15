package com.ibeetl.admin.console.web;

import static com.ibeetl.admin.core.util.enums.FunctionTypeEnum.FN1;
import static com.ibeetl.admin.core.util.enums.FunctionTypeEnum.FN2;

import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.console.service.FunctionConsoleService;
import com.ibeetl.admin.console.service.RoleConsoleService;
import com.ibeetl.admin.console.web.dto.RoleDataAccessFunction;
import com.ibeetl.admin.console.web.query.RoleQuery;
import com.ibeetl.admin.console.web.query.RoleUserQuery;
import com.ibeetl.admin.console.web.vo.RoleDataFunctionVO;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.Query;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreFunction;
import com.ibeetl.admin.core.entity.CoreRole;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.entity.ElCascaderData;
import com.ibeetl.admin.core.rbac.DataAccess;
import com.ibeetl.admin.core.rbac.DataAccessFactory;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.AnnotationUtil;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * 角色管理功能相关
 */
@Slf4j
@RequestMapping("/admin/roles")
@RestController
public class RoleConsoleElController {

  @Autowired
  private RoleConsoleService roleConsoleService;

  @Autowired
  private FunctionConsoleService functionConsoleService;

  @Autowired
  CorePlatformService platformService;

  @Autowired
  DataAccessFactory dataAccessFactory;

  /**
   * 列表页、 分页数据
   */
  @GetMapping("/list")
  @Function("role.query")
  public JsonResult<PageQuery> list(RoleQuery condtion) {

    PageQuery page = condtion.getPageQuery();
    roleConsoleService.queryByCondtion(page);
    return JsonResult.success(page);
  }

  @GetMapping("/all")
  @Function("role.query")
  public JsonResult<List<CoreRole>> all() {

    List<CoreRole> list = roleConsoleService.queryAllPermissionList();
    return JsonResult.success(list);
  }

  /**
   * 获取列表查询条件
   */
  @PostMapping("/list/condition")
  @Function("role.query")
  public JsonResult<List<Map<String, Object>>> listCondtion() {

    List<Map<String, Object>> list =
        AnnotationUtil.getInstance().getAnnotations(Query.class, RoleQuery.class);
    return JsonResult.success(list);
  }

  /**
   * 保存
   */
  @PostMapping("/add")
  @Function("role.add")
  public JsonResult addRole(@Validated(ValidateConfig.ADD.class) @RequestBody CoreRole role) {

    CoreRole role1 = roleConsoleService.queryByCode(role.getCode());
    if (role1 != null) {
      return JsonResult.failMessage("用户编号已存在");
    }
    JsonResult result = new JsonResult();
    role.setCreateTime(new Date());
    roleConsoleService.save(role);
    platformService.clearFunctionCache();
    return JsonResult.success();
  }

  /**
   * 更新
   */
  @PutMapping("/update")
  @Function("role.edit")
  public JsonResult<String> update(
      @Validated(ValidateConfig.UPDATE.class) @RequestBody CoreRole role) {

    boolean success = roleConsoleService.update(role);

    if (success) {
      platformService.clearFunctionCache();
      return JsonResult.success();
    } else {
      return JsonResult.failMessage("保存失败");
    }
  }

  /**
   * 查询角色信息
   */
  @GetMapping("/view")
  @Function("role.query")
  public JsonResult<CoreRole> queryInfo(Long id) {

    CoreRole role = roleConsoleService.queryById(id);
    return JsonResult.success(role);
  }

  /**
   * (批量)删除
   *
   * @param ids
   *     角色id
   */
  @DeleteMapping("/delete")
  @Function("role.delete")
  public JsonResult delete(@RequestBodyPlus("ids") List<Long> ids) {

    roleConsoleService.deleteById(ids);
    return JsonResult.success();
  }

  /**
   * 查询角色下授权用户列表
   */
  @PostMapping("/user/list")
  @Function("role.user.query")
  public JsonResult<PageQuery<CoreUser>> userList(RoleUserQuery query) {

    PageQuery page = query.getPageQuery();
    PageQuery<CoreUser> pageQuery = roleConsoleService.queryRoleUser(page);
    return JsonResult.success(page);
  }

  @GetMapping("/function/ids")
  @Function("role.function.query")
  public JsonResult<List<Long>> getFunctionIdByRole(Long roleId) {

    List<Long> list = functionConsoleService.getFunctionByRole(roleId);
    return JsonResult.success(list);
  }

  @GetMapping("/function/queryFunction")
  @Function("role.function.query")
  public JsonResult<List<RoleDataAccessFunction>> getQueryFunctionByRole(Long roleId) {

    List<RoleDataAccessFunction> list = functionConsoleService.getQueryFunctionByRole(roleId);
    return JsonResult.success(list);
  }

  @PutMapping("/function/update")
  @Function("role.function.edit")
  public JsonResult updateRoleFunctions(@RequestBodyPlus("roleId") Long roleId,
      @NotNull @RequestBodyPlus("functions") List<CoreFunction> functions) {
//数据库只保存菜单功能和功能点功能
    List<Long> all = functions.stream()
        .filter(v -> StrUtil.equalsAny(v.getType(), FN2.getValue(), FN1.getValue()))
        .map(CoreFunction::getId).collect(Collectors.toList());
    List<Long> addIds = new ArrayList<Long>();
    List<Long> delIds = new ArrayList<Long>();
    List<Long> dbs = functionConsoleService.getFunctionByRole(roleId);
    for (Long id : all) {
      if (!dbs.contains(id)) {
        addIds.add(id);
      }
    }
    for (Long id : dbs) {
      if (!all.contains(id)) {
        delIds.add(id);
      }
    }
    functionConsoleService.updateSysRoleFunction(roleId, addIds, delIds);
    return JsonResult.success();
  }

  @GetMapping("/data_function/list")
  @Function("role.function.updateDataAccess")
  public JsonResult<List<RoleDataFunctionVO>> functionDataList(
      Long roleId) {

    List<RoleDataAccessFunction> list = functionConsoleService.getQueryFunctionByRole(roleId);
    List<DataAccess> dataAccessList = dataAccessFactory.all();
    List<RoleDataFunctionVO> roleDataFunctionVOList = new ArrayList<>();
    for (RoleDataAccessFunction accessFunction : list) {
      RoleDataFunctionVO vo = new RoleDataFunctionVO();
      roleDataFunctionVOList.add(vo);
      List<ElCascaderData> elCascaderDataList = new ArrayList<>(dataAccessList.size());
      vo.setAccesses(elCascaderDataList);
      vo.setLabel(accessFunction.getName());
      for (DataAccess dataAccess : dataAccessList) {
        ElCascaderData cascaderData = new ElCascaderData();
        elCascaderDataList.add(cascaderData);
        cascaderData.setTail(accessFunction);
        cascaderData.setId(dataAccess.getType());
        cascaderData.setLabel(dataAccess.getName());
        cascaderData.setValue(dataAccess.getType());
      }
    }
    return JsonResult.success(roleDataFunctionVOList);
  }

  @PostMapping("/data_function/updateDataAccess")
  @Function("role.function.updateDataAccess")
  public JsonResult updateFunctionDataAccess(@RequestBodyPlus("roleId") Long roleId,
      @RequestBodyPlus("fnId") Long fnId, @RequestBodyPlus("accessType") Integer accessType) {

    RoleDataAccessFunction data = new RoleDataAccessFunction();
    data.setRoleId(roleId);
    data.setId(fnId);
    data.setDataAccessType(accessType);
    functionConsoleService.updateFunctionAccessByRole(Arrays.asList(data));
    return JsonResult.success();
  }

  /*后端模板渲染*/
  @PostMapping("/function/dataAccess.do")
  @Function("role.function.updateDataAccess")
  public ModelAndView datapage(Long roleId) {

    List<RoleDataAccessFunction> list = functionConsoleService.getQueryFunctionByRole(roleId);
    ModelAndView view = new ModelAndView("/admin/role/dataConfigPart.html");
    view.addObject("list", list);
    return view;
  }

}
