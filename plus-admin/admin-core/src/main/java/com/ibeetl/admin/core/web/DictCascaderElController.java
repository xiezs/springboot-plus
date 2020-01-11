package com.ibeetl.admin.core.web;

import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.ElCascaderData;
import com.ibeetl.admin.core.service.CoreDictService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典数据获取
 *
 * @author 一日看尽长安花
 */
@Slf4j
@RestController
@RequestMapping(value = "/core/dicts")
public class DictCascaderElController {

  @Autowired CoreDictService coreDictService;

  /**
   * Method obtainDcitsByCascader ... 获取子级字典列表。如果不传父级id，只获取type指定的列表而已。
   *
   * @param parentId of type Long 父级id
   * @param type of type String 子级字典列表type
   * @return JsonResult<List < ElCascaderData>>
   */
  @GetMapping
  public JsonResult<List<ElCascaderData>> obtainDcitsByCascader(Long parentId, String type) {
    if (StrUtil.isBlank(type) && parentId == null) {
      return JsonResult.failMessage("parentId and type should have at least one.");
    }
    List<CoreDict> childDictList = coreDictService.findChildByParent(parentId, type);
    return JsonResult.success(convertToCascaderData(childDictList));
  }

  private List<ElCascaderData> convertToCascaderData(List<CoreDict> coreDicts) {
    List<ElCascaderData> cascaderDataArrayList = new ArrayList<ElCascaderData>(coreDicts.size());
    for (CoreDict dict : coreDicts) {
      ElCascaderData data = new ElCascaderData();
      data.setId(dict.getId());
      data.setLabel(dict.getName());
      data.setValue(dict.getValue());
      cascaderDataArrayList.add(data);
    }
    return cascaderDataArrayList;
  }
}
