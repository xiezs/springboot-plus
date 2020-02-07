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
  private CmsBlogTypeEnum type;
  private Long createTime;
}
