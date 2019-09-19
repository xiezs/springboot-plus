import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.ConsoleApplication;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import com.ibeetl.admin.core.entity.CoreRouteMeta;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import resultmap.GridContainer;
import resultmap.GridHeader;
import resultmap.GridMapping;
import resultmap.GridRow;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsoleApplication.class)
public class CustomBeanProcessorTest {

  @Autowired CoreFunctionDao coreFunctionDao;

  static JSONObject resultMap;

  @BeforeClass
  public static void init() {
    resultMap = new JSONObject();
    resultMap.put("id", "core_route_map");

    JSONObject mapping = new JSONObject();
    mapping.put("id", "id");
    mapping.put("parentId", "parent_id");
    mapping.put("path", "path");
    mapping.put("name", "name");
    mapping.put("seq", "seq");

    JSONObject objMap = new JSONObject();
    objMap.put("title", "title");
    objMap.put("icon", "icon");

    JSONArray listMap = new JSONArray();
    JSONObject listInnerMap = new JSONObject();
    listInnerMap.put("id", "role_id");
    listMap.add(listInnerMap);
    objMap.put("roles", listMap);

    objMap.put("resultType", CoreRouteMeta.class.getCanonicalName());

    mapping.put("meta", objMap);
    mapping.put("resultType", CoreRoute.class.getCanonicalName());

    resultMap.put("mapping", mapping);
  }

  @Test
  public void maptest() {
    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    System.out.println(JSONUtil.toJsonPrettyStr(resultMap));
    GridMapping gridMapping = new GridMapping(resultMap);

    GridHeader gridHeader = gridMapping.getHeader();
    System.out.println(gridHeader);

    GridRow gridRow = new GridRow(gridHeader);
    System.out.println(gridRow);
    GridContainer gridContainer = new GridContainer();
    gridMapping.setContainer(gridContainer);
  }
}
