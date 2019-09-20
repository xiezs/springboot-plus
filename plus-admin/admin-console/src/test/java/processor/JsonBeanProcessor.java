package processor;

import com.ibeetl.admin.core.util.CacheUtil;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.Tail;
import org.beetl.sql.core.annotatoin.builder.AttributeBuilderHolder;
import org.beetl.sql.core.annotatoin.builder.AttributeSelectBuilder;
import org.beetl.sql.core.db.ClassAnnotation;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.kit.BeanKit;
import org.beetl.sql.core.mapping.BeanProcessor;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;
import resultmap.GridMapping;

public class JsonBeanProcessor extends BeanProcessor {

  public JsonBeanProcessor(SQLManager sm) {
    super(sm);
  }

  /** 创建 一个新的对象，并从ResultSet初始化 */
  @Override
  protected <T> T createBean(
      String sqlId, ResultSet rs, Class<T> type, PropertyDescriptor[] props, int[] columnToProperty)
      throws SQLException {
    GridMapping gridMapping = (GridMapping) CacheUtil.get("Route_Mapping");

    T bean = this.newInstance(type);
    ResultSetMetaData meta = rs.getMetaData();
    TypeParameter tp = new TypeParameter(sqlId, this.dbName, type, rs, meta, 1);

    for (int i = 1; i < columnToProperty.length; i++) {
      // Array.fill数组为-1 ，-1则无对应name
      tp.setIndex(i);
      if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
        String key = rs.getMetaData().getColumnLabel(i);
        if ((dbType == DBStyle.DB_ORACLE || dbType == DBStyle.DB_SQLSERVER)
            && key.equalsIgnoreCase("beetl_rn")) {
          // sql server 特殊处理，sql'server的翻页使用了额外列作为翻页参数，需要过滤
          continue;
        }

        if (bean instanceof Tail) {
          Tail bean2 = (Tail) bean;
          Object value = noMappingValue(tp);
          key = this.nc.getPropertyName(type, key);
          bean2.set(key, value);
        } else {
          Method m = BeanKit.getTailMethod(type);
          // 使用指定方法赋值
          if (m != null) {

            Object value = noMappingValue(tp);
            key = this.nc.getPropertyName(type, key);
            try {
              m.invoke(bean, key, value);
            } catch (Exception ex) {
              throw new BeetlSQLException(BeetlSQLException.TAIL_CALL_ERROR, ex);
            }
          } else {
            // 忽略这个结果集
          }
        }
        continue;
      }

      // columnToProperty[i]取出对应的在PropertyDescriptor[]中的下标
      PropertyDescriptor prop = props[columnToProperty[i]];
      Class<?> propType = prop.getPropertyType();
      tp.setTarget(propType);
      ClassAnnotation ca = ClassAnnotation.getClassAnnotation(type);
      Object value = null;
      if (!ca.getColHandlers().isEmpty()) {
        AttributeBuilderHolder holder =
            (AttributeBuilderHolder) ca.getColHandlers().get(prop.getName());
        if (holder != null && holder.supportSelectMapping()) {
          value =
              ((AttributeSelectBuilder) holder.getInstance())
                  .toObject(this.sm, holder.getBeanAnnotaton(), sqlId, tp, prop);
          this.callSetter(bean, prop, value, propType);
          continue;
        }
      }
      JavaSqlTypeHandler handler = this.handlers.get(propType);
      if (handler == null) {
        handler = this.defaultHandler;
      }
      value = handler.getValue(tp);

      this.callSetter(bean, prop, value, propType);
    }

    return bean;
  }
}
