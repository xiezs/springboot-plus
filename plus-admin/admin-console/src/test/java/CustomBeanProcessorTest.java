import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.ConsoleApplication;
import com.ibeetl.admin.core.conf.handler.DateTypeHandler;
import com.ibeetl.admin.core.conf.handler.ZonedDateTimeTypeHandler;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import com.ibeetl.admin.core.entity.CoreRouteMeta;
import com.ibeetl.admin.core.util.CacheUtil;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.beetl.core.GroupTemplate;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import processor.JsonBeanProcessor;
import resultmap.GridMapping;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsoleApplication.class)
public class CustomBeanProcessorTest {

  @Autowired CoreFunctionDao coreFunctionDao;

  @Autowired
  @Qualifier("baseDataSourceSqlManagerFactoryBean")
  SQLManager sqlManager;

  @Autowired GroupTemplate template;

  @Before
  public void beanProcessor() {
    JsonBeanProcessor jsonBeanProcessor = new JsonBeanProcessor(sqlManager);
    sqlManager.setDefaultBeanProcessors(jsonBeanProcessor);
  }

  @Test
  public void maptest() {
    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    System.out.println(routesList);
  }
}
