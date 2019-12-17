package com.ibeetl.admin.core.dao;

import com.ibeetl.admin.core.entity.DictType;
import java.util.List;
import java.util.Map;

import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.annotatoin.SqlStatement;
import org.beetl.sql.core.mapper.BaseMapper;

import com.ibeetl.admin.core.entity.CoreDict;

/** 字典DAO接口 */
@SqlResource("core.coreDict")
public interface CoreDictDao extends BaseMapper<CoreDict> {

  /**
   * 查询某个类型下的字典集合
   *
   * @param type 字典类型
   * @return
   */
  List<CoreDict> findAllList(String type);

  /**
   * 查询某个类型下的字典集合<br/>
   * 主要用于提供给前端的下拉选择菜单使用，业务上请使用返回值为{@link CoreDict}的方法
   *
   * @param type 字典类型
   * @return
   */
  List<DictType> findAllDictType(String type);

  /**
   * 查询字段类型列表
   *
   * @param delFlag 删除标记
   * @return
   */
  @SqlStatement(returnType = Map.class)
  List<Map<String, String>> findTypeList(int delFlag);

  /**
   * 根据父节点Id查询子节点数据
   *
   * @param id 父节点id
   * @return
   */
  List<CoreDict> findChildByParent(Long id);

  int bathDelByValue(List<String> values);
}
