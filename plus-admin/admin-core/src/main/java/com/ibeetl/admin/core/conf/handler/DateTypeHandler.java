package com.ibeetl.admin.core.conf.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Date;
import org.beetl.sql.core.mapping.type.JavaSqlTypeHandler;
import org.beetl.sql.core.mapping.type.TypeParameter;

public class DateTypeHandler extends JavaSqlTypeHandler {
  @Override
  public Object getValue(TypeParameter typePara) throws SQLException {
    if (ObjectUtil.isNull(typePara.getRs().getObject(typePara.getIndex()))) {
      return null;
    }
    int columnType = typePara.getColumnType();
    if (Types.TIMESTAMP == columnType || Types.DATE == columnType) {
      Timestamp timestamp = typePara.getRs().getTimestamp(typePara.getIndex());
      return Date.from(Instant.ofEpochSecond(timestamp.getTime()));
    } else if (Types.BIGINT == columnType) {
      long timestamp = Convert.toLong(typePara.getRs().getLong(typePara.getIndex()), 0L);
      return Date.from(Instant.ofEpochSecond(timestamp));
    } else {
      return null;
    }
  }
}