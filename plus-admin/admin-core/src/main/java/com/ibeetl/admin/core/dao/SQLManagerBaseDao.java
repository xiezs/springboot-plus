package com.ibeetl.admin.core.dao;

import org.beetl.sql.core.mapper.BaseMapper;

/**
 * 只是作为专门获取数据源的SQLmanager的接口，避免多数据源中，每次使用名称指定注入SQLmanager。
 * 泛型不可少
 * */
public interface SQLManagerBaseDao extends BaseMapper<Object> {}
