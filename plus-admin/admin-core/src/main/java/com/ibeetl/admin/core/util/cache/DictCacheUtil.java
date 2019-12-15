package com.ibeetl.admin.core.util.cache;

import cn.hutool.core.lang.Assert;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.DictType;
import com.ibeetl.admin.core.service.CoreDictService;
import com.ibeetl.admin.core.util.SpringUtil;
import java.util.ArrayList;
import java.util.List;

public class DictCacheUtil {
  private static final Cache<String, Object> CACHE =
      Caffeine.newBuilder().maximumSize(1024).recordStats().build();

  public static DictType get(DictType dictType) {
    checkKey(dictType);
    CoreDictService bean = SpringUtil.getBean(CoreDictService.class);
    return (DictType)
        CACHE.get(
            getKey(dictType),
            key -> {
              CoreDict coreDict = bean.findCoreDict(dictType.getType(), dictType.getValue());
              return convertToDictType(coreDict);
            });
  }

  public static List<DictType> get(String type) {
    Assert.notBlank(type);
    CoreDictService bean = SpringUtil.getBean(CoreDictService.class);
    return (List<DictType>)
        CACHE.get(
            type,
            key -> {
              List<CoreDict> coreDictList = bean.findAllByType(type);
              return convertToDictTypeList(coreDictList);
            });
  }

  public static String getKey(DictType dictType) {
    return dictType.getType() + ":" + dictType.getValue();
  }

  public static void checkKey(DictType dictType) {
    Assert.notNull(dictType);
    Assert.notBlank(dictType.getType(), "dict type can't is blank.");
    Assert.notBlank(dictType.getValue(), "dict value can't is blank.");
  }

  public static DictType convertToDictType(CoreDict coreDict) {
    DictType dictType = new DictType();
    dictType.setType(coreDict.getType());
    dictType.setValue(coreDict.getValue());
    dictType.setName(coreDict.getName());
    return dictType;
  }

  public static List<DictType> convertToDictTypeList(List<CoreDict> coreDictList) {
    Assert.notNull(coreDictList);
    List<DictType> dictTypeList = new ArrayList<>(coreDictList.size());
    for (CoreDict coreDict : coreDictList) {
      dictTypeList.add(convertToDictType(coreDict));
    }
    return dictTypeList;
  }
}
