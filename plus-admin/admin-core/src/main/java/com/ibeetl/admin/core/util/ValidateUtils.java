package com.ibeetl.admin.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import org.hibernate.validator.HibernateValidator;

public class ValidateUtils {

  private static final Validator VALIDATOR =
      Validation.byProvider(HibernateValidator.class)
          .configure()
          .failFast(true)
          .buildValidatorFactory()
          .getValidator();

  /**
   * 验证bean
   *
   * @param <T> 被验证bean的类型
   * @param bean 被验证的bean
   * @param groups 验证组
   * @return
   */
  public static <T> Set<ConstraintViolation<T>> validateBean(T bean, Class<?>... groups) {
    Assert.notNull(groups, "groups can't null. Instead of empty groups" );
    Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(bean, groups);
    return constraintViolations;
  }

  /**
   * 验证方法入参
   *
   * @param <T> 所属对象的类型
   * @param object 方法所属对象
   * @param method 方法
   * @param parameterValues 方法入参值
   * @param groups 校验组
   * @return
   */
  public static <T> Set<ConstraintViolation<T>> validateMethod(
      T object, Method method, Object[] parameterValues, Class<?>... groups) {
    Assert.notNull(groups, "groups can't null. Instead of empty groups" );
    ExecutableValidator executableValidator = VALIDATOR.forExecutables();
    Set<ConstraintViolation<T>> constraintViolations =
        executableValidator.validateParameters(object, method, parameterValues, groups);
    return constraintViolations;
  }

  public static <T> List wrapConstraintViolations(
      Set<ConstraintViolation<T>> constraintViolations) {
    List errorMsgs = CollUtil.newArrayList();
    constraintViolations.stream()
        .forEach(
            violation -> {
              errorMsgs.add(violation.getMessage());
            });
    return errorMsgs;
  }
}
