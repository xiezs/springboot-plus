package com.ibeetl.admin.core.service;

import com.ibeetl.admin.core.conf.PasswordConfig.PasswordEncryptService;
import com.ibeetl.admin.core.dao.CoreOrgDao;
import com.ibeetl.admin.core.dao.CoreUserDao;
import com.ibeetl.admin.core.dao.SQLManagerBaseDao;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.rbac.UserLoginInfo;
import com.ibeetl.admin.core.service.param.CoreUserParam;
import com.ibeetl.admin.core.util.PlatformException;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import com.ibeetl.admin.core.util.enums.GeneralStateEnum;
import com.ibeetl.admin.core.util.enums.StateTypeEnum;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CoreUserService extends CoreBaseService {
  @Autowired CoreUserDao userDao;

  @Autowired CoreOrgDao orgDao;

  @Autowired PasswordEncryptService passwordEncryptService;

  @Autowired SQLManagerBaseDao sqlManagerBaseDao;

  public UserLoginInfo login(String userName, String password) {
    CoreUser user =
        userDao
            .createLambdaQuery()
            .andEq(CoreUser::getCode, userName)
            .andEq(CoreUser::getPassword, passwordEncryptService.password(password))
            .single();
    if (user == null) {
      throw new PlatformException("用户" + userName + "不存在或者密码错误");
    }
    if (!user.getState().equals(StateTypeEnum.S1)) {
      throw new PlatformException("用户" + userName + "已经失效");
    }
    if (user.getDelFlag() == DelFlagEnum.DELETED.getValue()) {
      throw new PlatformException("用户" + userName + "已经删除");
    }
    Long orgId = user.getOrgId();
    List<CoreOrg> orgs = getUserOrg(user.getId(), orgId);
    UserLoginInfo loginInfo = new UserLoginInfo();
    loginInfo.setOrgs(orgs);
    loginInfo.setUser(user);
    return loginInfo;
  }

  public List<CoreOrg> getUserOrg(long userId, long orgId) {
    List<CoreOrg> orgs = orgDao.queryOrgByUser(userId);
    if (orgs.isEmpty()) {
      // 没有赋值任何角色，默认给一个所在部门
      CoreOrg userOrg = orgDao.unique(orgId);
      orgs.add(userOrg);
    }
    return orgs;
  }

  public List<CoreUser> getAllUsersByRole(String role) {

    return userDao.getUserByRole(role);
  }

  public PageQuery<CoreUser> getAllUsers(CoreUserParam coreUserParam) {
    PageQuery<CoreUser> pageQuery =
        sqlManagerBaseDao
            .getSQLManager()
            .lambdaQuery(CoreUser.class)
            .andGreatEq(
                CoreUser::getCreateTime, Query.filterNull(coreUserParam.getCreateTimeStart()))
            .andLessEq(CoreUser::getCreateTime, Query.filterNull(coreUserParam.getCreateTimeEnd()))
            .andLike(CoreUser::getName, Query.filterEmpty(coreUserParam.getName()))
            .andEq(CoreUser::getJobType0, Query.filterNull(coreUserParam.getJobType0()))
            .andEq(CoreUser::getJobType1, Query.filterNull(coreUserParam.getJobType1()))
            .andEq(CoreUser::getState, Query.filterNull(coreUserParam.getState()))
            .page(coreUserParam.getPage(), coreUserParam.getLimit());
    return pageQuery;
  }

  public CoreUser getUserByCode(String userName) {
    CoreUser user = new CoreUser();
    user.setCode(userName);
    return userDao.templateOne(user);
  }

  public void update(CoreUser user) {
    userDao.updateById(user);
  }

  public CoreOrg getOrgById(Long orgId) {
    return orgDao.unique(orgId);
  }

  public CoreUser getUserById(Long userId) {
    return userDao.unique(userId);
  }

  public List<String> getOrgCode(List<Long> orgIds) {
    return orgDao.queryAllOrgCode(orgIds);
  }
}
