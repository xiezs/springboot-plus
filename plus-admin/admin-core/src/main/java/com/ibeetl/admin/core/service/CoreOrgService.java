package com.ibeetl.admin.core.service;

import com.ibeetl.admin.core.dao.CoreOrgDao;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoreOrgService {
  @Autowired CoreOrgDao coreOrgDao;
  @Autowired CoreOrgService self;

//  @Cacheable(value = CorePlatformService.ORG_CACHE_TREE_LIST)
  public List<CoreOrg> allCoreOrgsTree() {
    List<CoreOrg> coreOrgList =
        coreOrgDao.createLambdaQuery().andEq(CoreOrg::getDelFlag, DelFlagEnum.NORMAL).select();
    CoreOrg virtualRoot = new CoreOrg();
    virtualRoot.setId(0L);
    buildListTree(virtualRoot, coreOrgList);
    return virtualRoot.getChidren();
  }

  private void buildListTree(CoreOrg parentOrg, List<CoreOrg> coreOrgs) {
    Long parentId = parentOrg.getId();
    List<CoreOrg> currentLevelOrgs = new ArrayList<>();
    List<CoreOrg> dels = new ArrayList<>();
    for (CoreOrg coreOrg : coreOrgs) {
      Long tempId = Optional.ofNullable(coreOrg.getParentOrgId()).orElse(0L);
      if (tempId.equals(parentId)) {
        currentLevelOrgs.add(coreOrg);
        dels.add(coreOrg);
      }
    }
    parentOrg.setChidren(currentLevelOrgs);
    coreOrgs.removeAll(dels);

    for (CoreOrg nextLevelParent : currentLevelOrgs) {
      buildListTree(nextLevelParent, coreOrgs);
    }
  }

//  @Cacheable(value = CorePlatformService.ORG_CACHE_TREE_CHILDREN)
  public List<CoreOrg> findChildrenNodes(Long parentId) {
    parentId = parentId == null ? 0L : parentId;
    List<CoreOrg> allDictTreeList = self.allCoreOrgsTree();
    return breadthSearchChildrenNodes(allDictTreeList, parentId);
  }

  private List<CoreOrg> breadthSearchChildrenNodes(
      List<CoreOrg> searchOrgs, Long parentId) {
    List<CoreOrg> _resultOrgs = new ArrayList<>();
    for (CoreOrg org : searchOrgs) {
      Long parent = Optional.ofNullable(org.getParentOrgId()).orElse(0L);
      if (parentId.equals(parent)) {
        _resultOrgs.add(org);
      }
    }

    if (_resultOrgs.isEmpty()) {
      for (CoreOrg org : searchOrgs) {
        List<CoreOrg> dictChildren = org.getChidren();
        _resultOrgs = breadthSearchChildrenNodes(dictChildren, parentId);
        if (!_resultOrgs.isEmpty()) {
          break;
        }
      }
    }

    return _resultOrgs;
  }
}
