import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
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
import resultmap.GridHeader;
import resultmap.GridMapping;
import resultmap.GridRow;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsoleApplication.class)
public class CustomBeanProcessorTest {

  @Autowired CoreFunctionDao coreFunctionDao;

  @Autowired
  @Qualifier("baseDataSourceSqlManagerFactoryBean")
  SQLManager sqlManager;

  static JSONObject resultMappping;

  @BeforeClass
  public static void init() {
    resultMappping = new JSONObject();
    resultMappping.put("id", "core_route_map");

    JSONObject routeMapping = new JSONObject();
    routeMapping.put("id", "id");
    routeMapping.put("parentId", "parent_id");
    routeMapping.put("path", "path");
    routeMapping.put("name", "name");
    routeMapping.put("seq", "seq");

    JSONObject metaMapping = new JSONObject();
    metaMapping.put("title", "title");
    metaMapping.put("icon", "icon");

    JSONArray roles = new JSONArray();
    JSONObject roleidMapping = new JSONObject();
    roleidMapping.put("id", "role_id");
    roles.add(roleidMapping);

    metaMapping.put("roles", roles);
    metaMapping.put("resultType", CoreRouteMeta.class.getCanonicalName());

    JSONObject testMapping = new JSONObject();
    testMapping.put("id", "test_id");
    testMapping.put("name", "name");
    testMapping.put("password", "password");
    testMapping.put("resultType", entity.Test.class.getCanonicalName());

    routeMapping.put("test", testMapping);
    routeMapping.put("meta", metaMapping);
    routeMapping.put("resultType", CoreRoute.class.getCanonicalName());

    resultMappping.put("mapping", routeMapping);
  }

  @Before
  public void beanProcessor() {
    JsonBeanProcessor jsonBeanProcessor = new JsonBeanProcessor(sqlManager);
    sqlManager.setDefaultBeanProcessors(jsonBeanProcessor);
    Map<Class, JavaSqlTypeHandler> typeHandlerMap =
        sqlManager.getDefaultBeanProcessors().getHandlers();
    /*Java bean的属性类型处理器，从数据库类型转化到属性Date类型*/
    typeHandlerMap.remove(Date.class);
    typeHandlerMap.put(Date.class, new DateTypeHandler());
    typeHandlerMap.put(ZonedDateTime.class, new ZonedDateTimeTypeHandler());
  }

  @Test
  public void maptest() {
    GridMapping gridMapping = new GridMapping(resultMappping);

    CacheUtil.put("Route_Mapping", gridMapping);

    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    System.out.println(routesList);
    System.out.println(JSONUtil.toJsonPrettyStr(resultMappping));
  }
}
