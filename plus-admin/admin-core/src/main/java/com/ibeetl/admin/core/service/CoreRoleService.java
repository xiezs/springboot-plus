package com.ibeetl.admin.core.service;

import com.ibeetl.admin.core.dao.CoreRoleDao;
import com.ibeetl.admin.core.entity.CoreRole;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述: 字典 service，包含常规字典和级联字典的操作。
 *
 * @author : xiandafu
 */
@Slf4j
@Service
@Transactional
public class CoreRoleService extends CoreBaseService<CoreRole> {

  @Autowired private CoreRoleDao roleDao;

  /**
   *
   * @param type ： R0 操作角色；R1 是工作流角色
   *
   * @author 一日看尽长安花
   */
  public List<CoreRole> getAllRoles(String type) {
    return roleDao
        .getSQLManager()
        .lambdaQuery(CoreRole.class)
        .andEq(CoreRole::getType, Query.filterEmpty(type))
        .select();
  }
}
