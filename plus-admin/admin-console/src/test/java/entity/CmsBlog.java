package entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.AutoID;

@Data
public class CmsBlog {
  @AutoID
  private Long id;
  private String title;
  private String content;
  private Long createUserId;
  private FunctionTypeEnum type;
  private Long createTime;
}
