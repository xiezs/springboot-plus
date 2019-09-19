package processor;

import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.BeanProcessor;

public class JsonBeanProcessor extends BeanProcessor {

  public JsonBeanProcessor(SQLManager sm) {
    super(sm);
  }
}
