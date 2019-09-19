package com.ibeetl.admin.core.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibeetl.admin.core.conf.handler.DateTypeHandler;
import com.ibeetl.admin.core.conf.handler.ZonedDateTimeTypeHandler;
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
import com.ibeetl.starter.BeetlTemplateCustomize;
import com.ibeetl.starter.ObjectMapperJsonUtil;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.simulate.WebSimulate;
import org.beetl.sql.core.InterceptorContext;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.ext.DebugInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureAfter(JasonConfig.class)
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

  @Bean
  public SQLManager sqlManager(
      @Qualifier("baseDataSourceSqlManagerFactoryBean") SQLManager sqlManager) {
    Map<Class, JavaSqlTypeHandler> typeHandlerMap =
        sqlManager.getDefaultBeanProcessors().getHandlers();
    /*Java bean的属性类型处理器，从数据库类型转化到属性Date类型*/
    typeHandlerMap.remove(Date.class);
    typeHandlerMap.put(Date.class, new DateTypeHandler());
    typeHandlerMap.put(ZonedDateTime.class, new ZonedDateTimeTypeHandler());
    return sqlManager;
  }

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
          "abcd",
          new Function() {

            @Override
            public Boolean call(Object[] paras, Context ctx) {
              return true;
            }
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
                return new String(str.getBytes("iso8859-1"), "UTF-8");
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
    @Override
    protected boolean isSimple(String sqlId) {
      if (sqlId.indexOf("_gen_") != -1) {
        return true;
      } else {
        return false;
      }
    }

    @Override
    protected void simpleOut(InterceptorContext ctx) {
      return;
    }
  }
}