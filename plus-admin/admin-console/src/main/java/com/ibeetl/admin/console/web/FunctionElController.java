package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.FunctionConsoleService;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.annotation.RequestBodyPlus;
import com.ibeetl.admin.core.entity.CoreFunction;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能点管理
 *
 * @author 一日看尽长安花
 */
@Slf4j
@RestController
@RequestMapping("/functions")
public class FunctionElController {

  @Autowired
  CorePlatformService platformService;

  @Autowired
  private FunctionConsoleService functionConsoleService;

  /**
   * @return 功能点树
   */
  @GetMapping
  @Function("function.query")
  @ResponseBody
  public JsonResult<List<CoreFunction>> funcsTree() {

    List<CoreFunction> functionTreeNodes = functionConsoleService.getFuncTree();
    return JsonResult.success(functionTreeNodes);
  }

  @PostMapping
  @Function("function.add")
  public JsonResult<CoreFunction> addFunction(
      @Validated(ValidateConfig.ADD.class) @RequestBody CoreFunction function) {

    functionConsoleService.saveFunction(function);
    return JsonResult.success(function);
  }

  @PutMapping
  @Function("function.update")
  public JsonResult updateFunction(
      @Validated(ValidateConfig.UPDATE.class) @RequestBody CoreFunction function) {

    functionConsoleService.updateFunction(function);
    return JsonResult.success();
  }

  @DeleteMapping
  @Function("function.delete")
  public JsonResult deleteFunction(@RequestBodyPlus("ids") Long[] ids) {
    // 删除功能和所有子功能
    functionConsoleService.batchDeleteFunction(Arrays.asList(ids));
    return JsonResult.success();
  }

}
