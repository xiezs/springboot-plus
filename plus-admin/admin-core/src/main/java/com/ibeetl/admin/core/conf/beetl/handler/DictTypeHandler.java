package com.ibeetl.admin.core.conf.beetl.handler;

import cn.hutool.core.util.ObjectUtil;
import com.ibeetl.admin.core.entity.DictType;
import java.sql.SQLException;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

/**
 * 字典字段的映射处理器
 * */
public class DictTypeHandler extends JavaSqlTypeHandler {
  @Override
  public Object getValue(TypeParameter typePara) throws SQLException {
    if (ObjectUtil.isNull(typePara.getRs().getObject(typePara.getIndex()))) {
      return null;
    }
    String val = typePara.getRs().getString(typePara.getIndex());
    return new DictType(val);
  }
}
