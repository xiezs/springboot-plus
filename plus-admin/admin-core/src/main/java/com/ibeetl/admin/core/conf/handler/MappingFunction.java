package com.ibeetl.admin.core.util.beetl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.core.util.CacheUtil;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.ResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.SQLResult;

public class MappingFunction implements Function {

  private static final StringWriter STRING_WRITER = new StringWriter();
  private static final StringTemplateResourceLoader STRING_TEMPLATE_RESOURCE_LOADER = new StringTemplateResourceLoader();

  @Override
  public Object call(Object[] paras, Context ctx) {
    String currentSqlId = ctx.getGlobal("_id").toString();
    Object cache = CacheUtil.get(currentSqlId);
    if(ObjectUtil.isNotNull(cache)){
      return StrUtil.EMPTY;
    }
    String sqlSegmentId = (String) paras[0];
    Map inputParas = ctx.globalVar;
    if (paras.length == 2) {
      Map map = (Map) paras[1];
      map.putAll(inputParas);
      inputParas = map;
    }

    SQLManager sm = (SQLManager) ctx.getGlobal("_manager");
    // 保留，免得被覆盖
    List list = (List) ctx.getGlobal("_paras");
    /*获取参数指定的sqlid所在的md文件名*/
    String file = this.getParentId(ctx);
    SQLResult result;
    if(sqlSegmentId.indexOf(".")==-1){
      /*同一个md文件的sql段*/
      result = sm.getSQLResult(file + "." + sqlSegmentId, inputParas, ctx);
    }else {
      /*另一个md文件的sql段*/
      result = sm.getSQLResult(sqlSegmentId, inputParas, ctx);
    }

    // 追加参数
    list.addAll(result.jdbcPara);
    ctx.set("_paras", list);

    GroupTemplate groupTemplate = sm.getBeetl().getGroupTemplate();
    Map rsMap =
        groupTemplate.runScript(
            result.jdbcSql, inputParas, STRING_WRITER, STRING_TEMPLATE_RESOURCE_LOADER);

    if (MapUtil.isNotEmpty(rsMap)) {
      /*TODO 待移到非测试代码中，重写为保持GridMapping，而不是保持Map*/
      CacheUtil.put(currentSqlId, rsMap.values().stream().findFirst().get());
    }

    return StrUtil.EMPTY;
  }

  private String getParentId(Context ctx) {
    String id = (String) ctx.getGlobal("_id");
    int index = id.lastIndexOf(".");
    String file = id.substring(0, index);
    return file;
  }
}
