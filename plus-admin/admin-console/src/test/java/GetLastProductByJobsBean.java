import java.io.Serializable;
import lombok.Data;

@Data
public class GetLastProductByJobsBean implements Serializable {
  private String jobNo;
  private Integer operNo;
  private String createBy;
}