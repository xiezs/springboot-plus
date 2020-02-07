package com.ibeetl.admin.console.service;

import com.ibeetl.admin.core.dao.CoreOrgDao;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.service.CoreBaseService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import java.util.List;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreOrgConsoleElService extends CoreBaseService<CoreOrg> {
  @Autowired CoreOrgDao coreOrgDao;

  @Autowired
  CorePlatformService corePlatformService;

  /**
   * Method getOrgListByParent ...<br>
   * 可通过父级机构id获取下一级机构列表<br>
   * 如果不传父级机构，会判断是否是admin前缀的超级管理。<br>
   * 如果不是超级管理，则通过当前登录人登录时所选的机构id作为根级机构列表
   *
   * @param parentId of type Long
   * @return List<CoreOrg>
   */
  public List<CoreOrg> getOrgListByParent(Long parentId) {
    if (parentId == null || parentId == 0L) {
      if (corePlatformService.isCurrentSupperAdmin()) {
        parentId = 0L;
      } else {
        parentId = corePlatformService.getCurrentOrgId();
      }
    }
    List<CoreOrg> coreOrgList =
        coreOrgDao
            .createLambdaQuery()
            .andEq(CoreOrg::getParentOrgId, parentId)
            .andEq(CoreOrg::getDelFlag, DelFlagEnum.NORMAL)
            .select();
    return coreOrgList;
  }
}
