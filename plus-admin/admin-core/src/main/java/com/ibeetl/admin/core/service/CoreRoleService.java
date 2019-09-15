package com.ibeetl.admin.core.service;

import com.ibeetl.admin.core.dao.CoreRoleDao;
import com.ibeetl.admin.core.entity.CoreRole;
import com.ibeetl.admin.core.entity.CoreUserRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  public List<CoreRole> getAllRoles(String type) {
    CoreRole template = new CoreRole();
    template.setType(type);
    return roleDao.template(template);
  }
}
