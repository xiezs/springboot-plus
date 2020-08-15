package com.ibeetl.admin.core.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.annotation.Dict;
import com.ibeetl.admin.core.dao.SQLManagerBaseDao;
import com.ibeetl.admin.core.entity.CoreDict;
import com.ibeetl.admin.core.entity.DictType;
import com.ibeetl.admin.core.util.PlatformException;
import com.ibeetl.admin.core.util.cache.DictTypeCacheUtil;
import com.ibeetl.admin.core.util.enums.DelFlagEnum;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 描述:
 *
 * @author : xiandafu
 */
public class CoreBaseService<T> {

  @Autowired
  protected CoreDictService coreDictService;

  protected SQLManagerBaseDao sqlManagerBaseDao;

  protected SQLManager sqlManager;

  @Autowired
  public void setSqlManagerBaseDao(SQLManagerBaseDao sqlManagerBaseDao) {

    this.sqlManagerBaseDao = sqlManagerBaseDao;
    this.sqlManager = this.sqlManagerBaseDao.getSQLManager();
  }

  /**
   * 根据id查询对象，如果主键ID不存在
   */
  public T queryById(Object id) {

    T t = sqlManager.single(getCurrentEntityClassz(), id);
    handleStrDictValueField((Object) t);
    return t;
  }

  /**
   * 根据id查询
   *
   * @param classz
   *     返回的对象类型
   * @param id
   *     主键id
   */
  public T queryById(Class<T> classz, Object id) {

    T t = sqlManager.unique(classz, id);
    handleStrDictValueField((Object) t);
    return t;
  }

  /**
   * 新增一条数据
   *
   * @param model
   *     实体类
   */
  public boolean save(T model) {

    return sqlManager.insert(model, true) > 0;
  }

  /**
   * 删除数据（一般为逻辑删除，更新del_flag字段为1）
   */
  public boolean deleteById(List<Long> ids) {

    if (ids == null || ids.isEmpty()) {
      throw new PlatformException("删除数据ID不能为空");
    }

    for (Long id : ids) {
    }

    List<Object> list = new ArrayList<>();
    for (Long id : ids) {
      Map map = new HashMap();
      // always id,delFlag for pojo
      map.put("id", id);
      map.put("delFlag", DelFlagEnum.DELETED.getValue());

      list.add(map);
    }
    int[] count = sqlManager.updateBatchTemplateById(getCurrentEntityClassz(), list);
    int successCount = 0;
    for (int successFlag : count) {
      successCount += successFlag;
    }
    return successCount == ids.size();
  }

  public boolean deleteById(Long id) {

    Map map = new HashMap();
    // always id,delFlag for pojo
    map.put("id", id);
    map.put("delFlag", DelFlagEnum.DELETED.getValue());
    int ret = sqlManager.updateTemplateById(getCurrentEntityClassz(), map);
    return ret == 1;
  }

  /**
   * 根据id删除数据
   *
   * @param id
   *     主键值
   */
  public int forceDelete(Long id) {

    return sqlManager.deleteById(getCurrentEntityClassz(), id);
  }

  /**
   * 根据id删除数据
   *
   * @param id
   *     主键值
   */
  public int forceDelete(Class<T> classz, Long id) {

    return sqlManager.deleteById(classz, id);
  }

  /**
   * 更新，只更新不为空的字段
   */
  public boolean updateTemplate(T model) {

    return sqlManager.updateTemplateById(model) > 0;
  }

  /**
   * 更新所有字段
   */
  public boolean update(T model) {

    return sqlManager.updateById(model) > 0;
  }

  /**
   * 获取当前注入泛型T的类型
   *
   * @return 具体类型
   */
  @SuppressWarnings("unchecked")
  private Class<T> getCurrentEntityClassz() {

    return (Class<T>)
        ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public void handleStrDictValueFields(Collection<?> collection) {

    for (Object bean : collection) {
      handleStrDictValueField(bean);
    }
  }

  /**
   * 处理字典字符值，将字典名存放在{@link TailBean}中。字典需要{@link Dict}注解
   * @param bean
   */
  public void handleStrDictValueField(Object bean) {

    if (bean == null) {
      return;
    }

    if (!(bean instanceof TailBean)) {
      throw new PlatformException("指定的pojo" + bean.getClass() + " 不能获取数据字典，需要继承TailBean");
    }

    TailBean ext = (TailBean) bean;
    Class c = ext.getClass();
    do {
      Field[] fields = c.getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Dict.class)) {
          field.setAccessible(true);
          Dict dict = field.getAnnotation(Dict.class);

          try {
            String display = "";
            Object fieldValue = field.get(ext);
            if (fieldValue != null) {
              CoreDict dbDict = coreDictService.findCoreDict(dict.type(), fieldValue.toString());
              display = dbDict != null ? dbDict.getName() : null;
            }
            ext.set(field.getName() + dict.suffix(), display);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      c = c.getSuperclass();
    } while (c != TailBean.class);
  }

  public void handleDictTypeFields(PageQuery pageQuery) {

    this.handleDictTypeFields(pageQuery.getList());
  }

  public void handleDictTypeFields(Collection<?> collection) {

    for (Object o : collection) {
      this.handleDictTypeField(o);
    }
  }

  /**
   * 处理{@link DictType} 类型字段，将字典的name ,value,type存放在{@link TailBean}。字典需要{@link Dict}注解
   * @param object
   */
  public void handleDictTypeField(Object object) {

    Field[] fields = ReflectUtil.getFields(object.getClass());
    for (Field field : fields) {
      Class<?> fieldDeclaringClass = field.getType();
      if (!fieldDeclaringClass.getCanonicalName().equals(DictType.class.getCanonicalName())) {
        continue;
      }
      Dict dictAnnotation = field.getDeclaredAnnotation(Dict.class);
      String type = dictAnnotation.type();
      DictType dictType = (DictType) ReflectUtil.getFieldValue(object, field);
      if (dictType == null || StrUtil.isBlank(dictType.getValue())) {
        continue;
      }
      DictType key = new DictType(type, dictType.getValue());
      dictType = DictTypeCacheUtil.get(key);
      ReflectUtil.setFieldValue(object, field, dictType);
    }
  }

}
