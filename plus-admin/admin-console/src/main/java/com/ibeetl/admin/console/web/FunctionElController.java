package com.ibeetl.admin.console.web;

import com.ibeetl.admin.console.service.FunctionConsoleService;
import com.ibeetl.admin.core.annotation.Function;
import com.ibeetl.admin.core.entity.CoreFunction;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.web.JsonResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

}
