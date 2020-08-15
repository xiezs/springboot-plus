package com.ibeetl.admin.console.web.vo;

import com.ibeetl.admin.core.entity.ElCascaderData;
import java.util.List;
import lombok.Data;

@Data
public class RoleDataFunctionVO {

  private String label;

  private List<ElCascaderData> accesses;

}
