package com.ibeetl.admin.console.service;

import com.ibeetl.admin.console.dao.UserConsoleDao;
import com.ibeetl.admin.console.exception.DeletedException;
import com.ibeetl.admin.console.exception.NoResourceException;
import com.ibeetl.admin.console.web.dto.UserExcelExportData;
import com.ibeetl.admin.console.web.query.UserRoleQuery;
import com.ibeetl.admin.core.conf.PasswordConfig.PasswordEncryptService;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.entity.CoreUserRole;
import com.ibeetl.admin.core.file.FileService;
import com.ibeetl.admin.core.rbac.tree.OrgItem;
import com.ibeetl.admin.core.service.CoreBaseService;
import com.ibeetl.admin.core.service.CoreDictService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.param.CoreUserParam;
import com.ibeetl.admin.core.util.PlatformException;
import com.ibeetl.admin.core.util.enums.CoreDictType;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import com.ibeetl.admin.core.util.enums.GeneralStateEnum;
import com.ibeetl.admin.core.util.enums.JobTypeEnum;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserConsoleService extends CoreBaseService<CoreUser> {

  @Autowired UserConsoleDao userConsoleDao;

  @Autowired FileService fileService;

  @Autowired PasswordEncryptService passwordEncryptService;

  @Autowired CoreDictService dictService;

  @Autowired CorePlatformService platformService;
  /**
   * 根据条件查询
   *
   * @param query
   */
  public void queryByCondtion(PageQuery<CoreUser> query) {
    PageQuery<CoreUser> ret = userConsoleDao.queryByCondtion(query);
    queryListAfter(ret.getList());
  }

  /**
   * 提供用户管理列表页数据
   *
   * @param coreUserParam
   */
  public PageQuery<CoreUser> getUsersByComplexSelect(CoreUserParam coreUserParam) {
    PageQuery<CoreUser> resultList =
        userConsoleDao.queryByCondtion(coreUserParam.createPageQuery());
    return resultList;
  }

  /**
   * 插入一条用户数据
   *
   * @param user
   */
  public void saveUser(CoreUser user) {
    CoreUser query = new CoreUser();
    query.setCode(user.getCode());
    CoreUser dbUser = userConsoleDao.templateOne(query);
    if (dbUser != null) {
      throw new PlatformException("保存用户信息失败,用户已经存在");
    }
    user.setCreateTime(new Date());
    user.setPassword(passwordEncryptService.password(user.getPassword()));
    user.setDelFlag(DelFlagEnum.NORMAL.getValue());
    userConsoleDao.insert(user, true);
    if (StringUtils.isNotEmpty(user.getAttachmentId())) {
      // 更新附件详细信息,关联到这个用户
      fileService.updateFile(user.getAttachmentId(), user.getName(), String.valueOf(user.getId()));
    }
  }

  /**
   * 根据用户id查询一条数据
   *
   * @param userId
   */
  public CoreUser queryUserById(Long userId) {
    return userConsoleDao.unique(userId);
  }

  /**
   * 更新用户 只更新不为空的字段
   *
   * @param user
   * @return
   */
  public int updateSysUser(CoreUser user) {
    return userConsoleDao.updateTemplateById(user);
  }

  /**
   * 删除用户
   *
   * @param userId 用户id
   */
  public void delSysUser(Long userId) {
    CoreUser user = queryUserById(userId);
    if (user == null) {
      throw new NoResourceException("用户不存在!");
    }
    if (user.getDelFlag() == DelFlagEnum.DELETED.getValue()) {
      throw new DeletedException("用户已被删除!");
    }
    user = new CoreUser();
    user.setId(userId);
    user.setDelFlag(DelFlagEnum.DELETED.getValue());
    userConsoleDao.updateTemplateById(user);
  }

  /**
   * 批量删除用户 <br>
   * 软删除 标记用户删除状态
   *
   * @param userIds 用户id
   */
  public void batchDelSysUser(List<Long> userIds) {
    try {
      userConsoleDao.batchDelUserByIds(userIds);
    } catch (Exception e) {
      throw new PlatformException("批量删除用户失败", e);
    }
  }

  /**
   * 批量停用用户 <br>
   * 标记用户状态 停用
   *
   * @param userIds 用户id
   */
  public void batchUpdateUserState(List<Long> userIds, GeneralStateEnum stateEnum) {
    userConsoleDao.batchUpdateUserState(userIds, stateEnum);
  }

  /**
   * 重置用户密码
   *
   * @param id
   * @param password
   */
  public int resetPassword(Long id, String password) {
    CoreUser user = new CoreUser();
    user.setId(id);
    user.setPassword(passwordEncryptService.password(password));
    user.setUpdateTime(new Date());
    return userConsoleDao.updateTemplateById(user);
  }

  public List<CoreUserRole> getUserRoles(UserRoleQuery roleQuery) {
    return userConsoleDao.queryUserRole(
        roleQuery.getUserId(), roleQuery.getOrgId(), roleQuery.getRoleId());
  }

  public JsonResult deleteUserRoles(List<Long> ids) {
    int delete =
        sqlManager.lambdaQuery(CoreUserRole.class).andIn(CoreUserRole::getId, ids).delete();
    if (delete > 0) {
      return JsonResult.success();
    }
    return JsonResult.fail();
  }

  public void saveUserRole(CoreUserRole userRole) {
    long queryCount = sqlManager.templateCount(userRole);
    if (queryCount > 0) {
      throw new PlatformException("已存在用户角色关系");
    }
    sqlManager.insert(userRole);
  }

  public List<UserExcelExportData> queryExcel(CoreUserParam coreUserParam) {
    PageQuery<CoreUser> query = coreUserParam.createPageQuery();
    // 取出全部符合条件的
    //    page.setPageSize(Integer.MAX_VALUE);
    //    page.setPageNumber(1);
    //    page.setTotalRow(Integer.MAX_VALUE);
    PageQuery<CoreUser> ret = userConsoleDao.queryByCondtion(query);
    List<CoreUser> list = ret.getList();
    OrgItem orgRoot = platformService.buildOrg();
    List<UserExcelExportData> items = new ArrayList<>();
    for (CoreUser user : list) {
      UserExcelExportData userItem = new UserExcelExportData();
      userItem.setCode(user.getCode());
      userItem.setId(user.getId());
      userItem.setName(user.getName());
      CoreDict dict = dictService.findCoreDict(CoreDictType.USER_STATE, user.getState().getValue());
      userItem.setStateText(dict.getName());

      String dictValue =
          Optional.ofNullable(user.getJobType1()).map(JobTypeEnum::getValue).orElse(null);
      if (StringUtils.isNotEmpty(dictValue)) {
        dict = dictService.findCoreDict(CoreDictType.JOB_TYPE, dictValue);
        userItem.setJobType1Text(dict.getName());
      }
      Long orgId = Optional.ofNullable(user.getOrg()).map(CoreOrg::getId).orElse(1L);
      String orgName = orgRoot.findChild(orgId).getName();
      userItem.setOrgText(orgName);
      items.add(userItem);
    }
    return items;
  }
}
