package com.ibeetl.admin.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.beetl.sql.core.mapper.internal.LambdaQueryAmi;
import org.beetl.sql.core.query.LambdaQuery;
import org.beetl.sql.core.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ibeetl.admin.core.dao.CoreDictDao;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import org.springframework.validation.annotation.Validated;

/**
 * 描述: 字典 service，包含常规字典和级联字典的操作。
 *
 * @author : xiandafu
 */
@Service
@Transactional
public class CoreDictService extends CoreBaseService<CoreDict> {

  @Autowired private CoreDictDao dictDao;

  @Autowired CorePlatformService platformService;

  @Autowired CoreDictService self;

  /**
   * 根据类型获取字典集合
   *
   * @param type 字典类型，
   * @return List
   */
  @Cacheable(value = CorePlatformService.DICT_CACHE_TYPE)
  public List<CoreDict> findAllByType(String type) {
    return dictDao.findAllList(type);
  }

  /**
   * 级联字典查询，必须提供一个字典类型
   *
   * @param type
   * @param value
   * @return
   */
  @Cacheable(value = CorePlatformService.DICT_CACHE_CHILDREN)
  public List<CoreDict> findAllByGroup(String type, String value) {
    List<CoreDict> list = self.findAllByType(type);
    return _search(list, value);
  }

  /**
   * Method findChildByParent ...<br>
   * 获取某类型或者某个父级下的字典列表，参数二选一即可，也可都有。不可都不存在 <br>
   *
   * @param parentId of type Long 父级id
   * @param type of type String 字典type
   * @return List<CoreDict>
   */
  @Cacheable(value = CorePlatformService.DICT_CACHE_CHILDREN)
  public List<CoreDict> findChildByParent(Long parentId, String type) {
    if (StrUtil.isBlank(type) && parentId == null) {
      return null;
    }
    List<CoreDict> coreDictList =
        dictDao
            .createLambdaQuery()
            .andEq(CoreDict::getParent, Query.filterNull(parentId))
            .andEq(CoreDict::getType, Query.filterEmpty(type))
            .andEq(CoreDict::getDelFlag, DelFlagEnum.NORMAL)
            .orderBy(CoreDict::getSort)
            .select();
    return coreDictList;
  }

  @Cacheable(value = CorePlatformService.DICT_CACHE_VALUE)
  public CoreDict findCoreDict(String type, String value) {
    List<CoreDict> list = self.findAllByGroup(type, value);
    if (list == null) {
      return null;
    }
    for (CoreDict dict : list) {
      if (dict.getValue().equals(value)) {
        return dict;
      }
    }

    return null;
  }

  /**
   * 将数据库中所有字典构建成一个树集。方便前端某些树组件快速获取数据.
   *
   * @author 一日看尽长安花
   * @return List<CoreDict>
   */
  @Cacheable(value = CorePlatformService.DICT_CACHE_TREE_LIST)
  public List<CoreDict> allCoreDictsTree() {
    List<CoreDict> coreDictList =
        dictDao.createLambdaQuery().andEq(CoreDict::getDelFlag, DelFlagEnum.NORMAL).select();
    CoreDict virtualRoot = new CoreDict();
    virtualRoot.setId(0L);
    buildListTree(virtualRoot, coreDictList);
    return virtualRoot.getChildren();
  }

  private void buildListTree(CoreDict parentDict, List<CoreDict> coreDictList) {
    long parentId = parentDict.getId();
    List<CoreDict> currentLevelDict = new ArrayList<>();
    List<CoreDict> dels = new ArrayList<>();
    for (CoreDict coreDict : coreDictList) {
      if (coreDict.getParent() == null || coreDict.getParent() == parentId) {
        currentLevelDict.add(coreDict);
        dels.add(coreDict);
      }
    }
    parentDict.setChildren(currentLevelDict);
    coreDictList.removeAll(dels);

    for (CoreDict nextLevelParent : currentLevelDict) {
      buildListTree(nextLevelParent, coreDictList);
    }
  }

  /**
   * Method findChildrenNodes sets the ${FIELD_NAME} of this CoreDictService object.
   *
   * <p>在一个完整的字典树里，找到指定的字典子树
   *
   * @param parentId 要搜寻字典列表的父id，根级的父id为null或者0
   * @param type 要搜寻的字典类型
   * @author 一日看尽长安花
   * @return List<CoreDict>
   */
  @Validated
  @Cacheable(value = CorePlatformService.DICT_CACHE_TREE_CHILDREN)
  public List<CoreDict> findChildrenNodes(Long parentId, @NotBlank String type) {
    parentId = parentId == null ? 0L : parentId;
    List<CoreDict> allDictTreeList = self.allCoreDictsTree();
    return breadthSearchChildrenNodes(allDictTreeList, parentId, type);
  }

  private List<CoreDict> breadthSearchChildrenNodes(
      List<CoreDict> searchDicts, Long parentId, @NotBlank String type) {
    List<CoreDict> _resultDicts = new ArrayList<>();
    for (CoreDict dict : searchDicts) {
      String dictType = dict.getType();
      Long parent = Optional.ofNullable(dict.getParent()).orElse(0L);
      if (parentId.equals(parent) && type.equals(dictType)) {
        _resultDicts.add(dict);
      }
    }

    if (_resultDicts.isEmpty()) {
      for (CoreDict dict : searchDicts) {
        List<CoreDict> dictChildren = dict.getChildren();
        _resultDicts = breadthSearchChildrenNodes(dictChildren, parentId, type);
        if (!_resultDicts.isEmpty()) {
          break;
        }
      }
    }

    return _resultDicts;
  }

  /*通过名字反向查找数据字典，通常用于excel导入*/
  public Map<String, CoreDict> mapDictByName(String type) {
    List<CoreDict> list = self.findAllByType(type);
    Map<String, CoreDict> map = new HashMap<String, CoreDict>();
    for (CoreDict dict : list) {
      map.put(dict.getName(), dict);
    }
    return map;
  }

  /*递归查找*/
  private List<CoreDict> _search(List<CoreDict> list, String value) {
    for (CoreDict dict : list) {
      if (dict.getValue().equals(value)) {
        return list;
      } else {
        List<CoreDict> children = findChildByParent(dict.getId(), null);
        if (children.isEmpty()) {
          continue;
        } else {
          List<CoreDict> ret = _search(children, value);
          if (ret != null) {
            return ret;
          }
        }
      }
    }
    return null;
  }

  /**
   * 查询字段类型列表
   *
   * @return
   */
  public List<Map<String, String>> findTypeList() {
    return dictDao.findTypeList(DelFlagEnum.NORMAL.getValue());
  }
}
