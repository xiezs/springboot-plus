package com.ibeetl.admin.core.web;

import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.DictType;
import com.ibeetl.admin.core.service.CoreDictService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.service.DictTypeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class DictDataElController {

  @Autowired DictTypeService dictTypeService;

  /**
   * 根据字典类型type，获取所有字典数据并返回
   *
   * @param type
   * @return
   */
  @GetMapping
  public JsonResult<List<DictType>> dictList(String type) {
    List<DictType> list = dictTypeService.findAllList(type);
    return JsonResult.success(list);
  }
}
