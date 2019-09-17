import cn.hutool.core.util.IdUtil;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsoleApplication.class)
public class BeanProcessor {

  @Autowired CoreFunctionDao coreFunctionDao;

  static JSONObject resultMap;

  @BeforeClass
  public static void init() {
    resultMap = new JSONObject();
    resultMap.put("id", "resultmap");
    JSONObject mapping = new JSONObject();
    mapping.put("id", "id");
    mapping.put("parentId", "parent_id");
    mapping.put("path", "path");
    mapping.put("name", "name");
    mapping.put("seq", "seq");

    JSONObject objMap = new JSONObject();
    objMap.put("title", "title");
    objMap.put("icon", "icon");
    objMap.put("resultType", CoreRouteMeta.class.getCanonicalName());
    mapping.put("obj_mapping" + IdUtil.fastSimpleUUID(), objMap);

    JSONArray listMap = new JSONArray();
    JSONObject listInnerMap = new JSONObject();
    listInnerMap.put("id", "role_id");
    listMap.add(listInnerMap);
    mapping.put("list_mapping" + IdUtil.fastSimpleUUID(), listMap);

    resultMap.put("map", mapping);
  }

  @Test
  public void maptest() {
    List<CoreRoute> routesList = coreFunctionDao.getAllRoutes();
    System.out.println(JSONUtil.toJsonStr(resultMap));
  }
}
