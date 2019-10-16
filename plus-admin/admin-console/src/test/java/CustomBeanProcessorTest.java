import com.ibeetl.admin.ConsoleApplication;
import com.ibeetl.admin.core.dao.CoreFunctionDao;
import com.ibeetl.admin.core.entity.CoreRoute;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    for (int i = 0; i < 1000; i++) {
      Calendar instance = Calendar.getInstance();
      instance.setTimeInMillis(System.currentTimeMillis());
      instance.set(Calendar.DAY_OF_MONTH, 1);
      instance.set(Calendar.HOUR_OF_DAY, 0);
      instance.set(Calendar.MINUTE, 0);
      instance.set(Calendar.SECOND, 0);
      Date time = instance.getTime();
      System.out.println(time);
    }
  }
}
