package com.ibeetl.admin.core.conf.beetl;

import cn.hutool.core.util.CharsetUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeetl.admin.core.conf.JacksonConfig;
import com.ibeetl.admin.core.conf.beetl.handler.DateTypeHandler;
import com.ibeetl.admin.core.conf.beetl.handler.DictTypeHandler;
import com.ibeetl.admin.core.conf.beetl.handler.ZonedDateTimeTypeHandler;
import com.ibeetl.admin.core.conf.beetl.processor.JsonBeanProcessor;
import com.ibeetl.admin.core.entity.DictType;
import com.ibeetl.admin.core.rbac.DataAccess;
import com.ibeetl.admin.core.rbac.DataAccessFactory;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.beetl.DictQueryFunction;
import com.ibeetl.admin.core.util.beetl.FileFunction;
import com.ibeetl.admin.core.util.beetl.FunAccessUrlFunction;
import com.ibeetl.admin.core.util.beetl.FunFunction;
import com.ibeetl.admin.core.util.beetl.MenuFunction;
import com.ibeetl.admin.core.util.beetl.OrgFunction;
import com.ibeetl.admin.core.util.beetl.RoleFunction;
import com.ibeetl.admin.core.util.beetl.SearchCondtionFunction;
import com.ibeetl.admin.core.util.beetl.SysFunctionTreeFunction;
import com.ibeetl.admin.core.util.beetl.UUIDFunction;
import com.ibeetl.admin.core.util.beetl.XXSDefenderFormat;
import com.ibeetl.admin.core.web.query.QueryParser;
import com.ibeetl.starter.BeetlSqlMutipleSourceCustomize;
import com.ibeetl.starter.BeetlTemplateCustomize;
import com.ibeetl.starter.ObjectMapperJsonUtil;
import java.io.UnsupportedEncodingException;
import java.sql.JDBCType;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.simulate.WebSimulate;
import org.beetl.sql.core.Interceptor;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.engine.SQLParameter;
import org.beetl.sql.core.engine.SQLPlaceholderST;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.ext.DebugInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureAfter(JacksonConfig.class)
public class BeetlConf {
  @Autowired Environment env;
  @Autowired CorePlatformService platFormService;

  @Autowired OrgFunction orgFunction;

  @Autowired SysFunctionTreeFunction sysFunctionTreeFunction;

  @Autowired DictQueryFunction dictDownQueryFunction;

  @Autowired RoleFunction roleFunction;
  @Autowired FileFunction fileFunction;

  @Autowired SearchCondtionFunction searchCondtionFunction;

  @Autowired DataAccessFactory dataAccessFactory;

  @Autowired ApplicationContext applicationContext;

  @Autowired FunFunction funFunction;

  @Autowired FunAccessUrlFunction funAccessUrlFunction;

  @Autowired MenuFunction menuFunction;

  @Bean
  public WebSimulate getWebSimulate(GroupTemplate gt, ObjectMapper objectMapper) {
    return new WebSimulate(gt, new ObjectMapperJsonUtil(objectMapper)) {
      @Override
      protected String getRenderPath(HttpServletRequest request) {
        String defaultRenderPath = request.getServletPath();
        return defaultRenderPath.replace(".do", ".html");
      }
    };
  }


  /**
   * 对BeetlSql 的多数据源中每个数据源单独自定义配置
   *
   * @return
   */
  @Bean
  public BeetlSqlMutipleSourceCustomize beetlSqlMutipleSourceCustomize() {
    SQLPlaceholderST.textFunList.add("mapping");
    return (dataSource, manager) -> {
      /*bean转换处理器*/
      JsonBeanProcessor jsonBeanProcessor = new JsonBeanProcessor(manager);
      manager.setDefaultBeanProcessors(jsonBeanProcessor);
      /*类型处理器*/
      Map<Class, JavaSqlTypeHandler> typeHandlerMap =
          manager.getDefaultBeanProcessors().getHandlers();
      /*Java bean的属性类型处理器，从数据库类型转化到属性Date类型*/
      typeHandlerMap.remove(Date.class);
      typeHandlerMap.put(Date.class, new DateTypeHandler());
      typeHandlerMap.put(ZonedDateTime.class, new ZonedDateTimeTypeHandler());
      typeHandlerMap.put(DictType.class, new DictTypeHandler());
      /*拦截器*/
      manager.setInters(
          new Interceptor[] {new StarterDebugInterceptor(), new TypeHandlerInterceptor()});
    };
  }

  /**
   * Beetl模板引擎的自定义配置
   *
   * @return
   */
  @Bean
  public BeetlTemplateCustomize beetlTemplateCustomize() {
    return groupTemplate -> {
      groupTemplate.registerFunctionPackage("platform", platFormService);
      groupTemplate.registerFunctionPackage("queryCondtion", new QueryParser());
      groupTemplate.registerFunction("core.orgName", orgFunction);
      groupTemplate.registerFunction("core.functionName", funFunction);
      groupTemplate.registerFunction("core.funAccessUrl", funAccessUrlFunction);
      groupTemplate.registerFunction("core.menuName", menuFunction);
      groupTemplate.registerFunction("core.searchCondtion", searchCondtionFunction);
      groupTemplate.registerFunction("core.roles", roleFunction);
      groupTemplate.registerFunction("core.file", fileFunction);
      groupTemplate.registerFormat("xss", new XXSDefenderFormat());
      groupTemplate.registerFunction("uuid", new UUIDFunction());
      groupTemplate.registerFunctionPackage("dict", dictDownQueryFunction);
      // 模板页面判断是否有按钮权限,比如canAccess
      groupTemplate.registerFunction(
          "canAccess",
          (paras, ctx) -> {
            Long userId = platFormService.getCurrentUser().getId();
            Long orgId = platFormService.getCurrentOrgId();
            String functionCode = (String) paras[0];
            return platFormService.canAcessFunction(userId, orgId, functionCode);
          });

      groupTemplate.registerFunction(
          "env",
          new Function() {

            @Override
            public String call(Object[] paras, Context ctx) {
              String key = (String) paras[0];
              String value = env.getProperty(key);
              if (value != null) {
                return getStr(value);
              }
              if (paras.length == 2) {
                return (String) paras[1];
              }
              return null;
            }

            protected String getStr(String str) {
              try {
                return new String(str.getBytes(CharsetUtil.ISO_8859_1), CharsetUtil.UTF_8);
              } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
              }
            }
          });

      groupTemplate.registerFunction(
          "dataAccessList",
          new Function() {

            @Override
            public List<DataAccess> call(Object[] paras, Context ctx) {
              return dataAccessFactory.all();
            }
          });
    };
  }

  static class StarterDebugInterceptor extends DebugInterceptor {
    private Logger logger = LoggerFactory.getLogger("beetlsql");

    /**
     * 将内置生成的SQL排除打印
     *
     * @param sqlId
     * @return
     */
    @Override
    protected boolean isSimple(String sqlId) {
      //      if (sqlId.indexOf("_gen_") != -1) {
      //        return true;
      //      } else {
      //        return false;
      //      }
      return super.isSimple(sqlId);
    }
  }
}

/**
 * Class TypeHandlerInterceptor : <br>
 * 描述：预计做类型处理工厂拦截器
 *
 * @author 一日看尽长安花 Created on 2020/2/3
 */
class TypeHandlerInterceptor implements Interceptor {

  @Override
  public void before(InterceptorContext ctx) {
    List<SQLParameter> paras = ctx.getParas();
    for (SQLParameter para : paras) {
      if (para.value instanceof Date) {
        para.value = ((Date) para.value).getTime();
        para.setJdbcType(JDBCType.BIGINT.getVendorTypeNumber());
      }
    }
  }

  @Override
  public void after(InterceptorContext ctx) {}

  @Override
  public void exception(InterceptorContext ctx, Exception ex) {}
}
