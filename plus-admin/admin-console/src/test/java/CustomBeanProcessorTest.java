import com.ibeetl.admin.ConsoleApplication;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import entity.CmsBlog;
import entity.FunctionTypeEnum;
import java.util.List;
import org.beetl.sql.core.SQLManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsoleApplication.class)
public class CustomBeanProcessorTest {

  @Autowired CoreFunctionDao coreFunctionDao;

  @Test
  public void maptest() {
    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    System.out.println(routesList);
  }

  @Test
  public void test() {
    SQLManager sqlManager = coreFunctionDao.getSQLManager();
    List<CmsBlog> cmsBlogList = sqlManager.lambdaQuery(CmsBlog.class).select();
    System.out.println(cmsBlogList);
    CmsBlog blog = new CmsBlog();
    blog.setTitle("test title");
    blog.setContent("test content");
    blog.setType(FunctionTypeEnum.FN1);
    blog.setCreateUserId(1L);
    sqlManager.lambdaQuery(CmsBlog.class).insert(blog);
  }

}
