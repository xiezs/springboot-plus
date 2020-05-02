import cn.hutool.core.collection.CollUtil;
import com.ibeetl.admin.ConsoleApplication;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import entity.CmsBlog;
import entity.CmsBlogTypeEnum;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
    blog.setType(CmsBlogTypeEnum.FN1);
    blog.setCreateUserId(1L);
    sqlManager.lambdaQuery(CmsBlog.class).insert(blog);
  }

}
