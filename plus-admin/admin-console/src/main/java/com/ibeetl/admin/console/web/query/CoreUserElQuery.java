package com.ibeetl.admin.console.web.query;

import static com.ibeetl.admin.core.util.enums.ElColumnType.DATE;
import static com.ibeetl.admin.core.util.enums.ElColumnType.DICT;
import static com.ibeetl.admin.core.util.enums.ElColumnType.STRING;

import com.ibeetl.admin.core.annotation.ElColumn;
import java.util.Date;
import lombok.Data;

/**
 * Class CoreUserElQuery : <br>
 * 描述：用户管理页面的metadata元数据
 *
 * @author 一日看尽长安花 Created on 2019/12/29
 */
@Data
public class CoreUserElQuery {

  @ElColumn(
      name = "ID",
      type = STRING,
      isShowSearchPanel = false,
      isShowTablePanel = false,
      isShowEditorPanel = false)
  protected Long id;

  @ElColumn(name = "创建时间", type = DATE, isShowEditorPanel = false)
  protected Date createTime;

  @ElColumn(name = "用户名", type = STRING, isShowSearchPanel = false)
  private String code;

  @ElColumn(name = "姓名", type = STRING)
  private String name;

  @ElColumn(
      name = "机构",
      type = STRING,
      jsonPath = "org.name",
      isShowSearchPanel = false,
      isShowEditorPanel = false)
  private Long orgId;

  @ElColumn(name = "状态", type = DICT, isShowEditorPanel = false)
  private String state;

  @ElColumn(name = "职务", type = DICT, isShowEditorPanel = false)
  private String jobType0;

  @ElColumn(name = "岗位", type = DICT, isShowEditorPanel = false)
  private String jobType1;
}
