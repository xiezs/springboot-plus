package com.ibeetl.admin.core.service.param;

import com.ibeetl.admin.core.util.enums.JobTypeEnum;
import com.ibeetl.admin.core.util.enums.StateTypeEnum;
import com.ibeetl.admin.core.web.query.PageParam;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class CoreUserParam extends PageParam {
  Date createTimeStart;
  Date createTimeEnd;
  String name;
  String code;
  Long orgId;
  JobTypeEnum jobType0;
  JobTypeEnum jobType1;
  StateTypeEnum state;
}
