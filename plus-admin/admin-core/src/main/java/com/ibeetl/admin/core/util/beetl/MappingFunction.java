package com.ibeetl.admin.core.util.beetl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ibeetl.admin.core.util.CacheUtil;
import java.util.Map;
import org.beetl.core.Context;
import org.beetl.core.Function;

public class MappingFunction implements Function {
  @Override
  public Object call(Object[] paras, Context ctx) {
    Object sqlid = ctx.getGlobal("_id");
    if (ObjectUtil.isNull(CacheUtil.get(sqlid))){
      throw new RuntimeException(StrUtil.format("【{}】只能存在唯一一个映射", sqlid));
    }
    Map json = JSONUtil.parseObj(paras[0]);
    CacheUtil.put(sqlid, json);
    return StrUtil.EMPTY;
  }
}
