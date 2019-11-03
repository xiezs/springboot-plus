package com.ibeetl.admin.core.conf.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.util.ValidateUtils;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Aspect
@Component
public class ServiceValidationAop {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Pointcut("@target(org.springframework.stereotype.Service)")
  public void service() {}

  @Pointcut("@annotation(org.springframework.validation.annotation.Validated)")
  public void validated() {}

  @Pointcut("@annotation(javax.validation.Valid)")
  public void valid() {}

  @Around("service() && (valid() || validated())")
  public Object doValidation(ProceedingJoinPoint point) throws Throwable {
    Object returnValue;
    /*被代理对象、目标对象*/
    Object target = point.getTarget();
    /*代理对象*/
    /*Object pointThis = point.getThis();*/
    /*获取代理方法签名*/
    Signature signature = point.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    /*通过方法签名获取到的method是代理对象中的，不具有目标method的方法信息*/
    Method targetMethod = methodSignature.getMethod();
    /*只能通过反射获取原目标方法的方法信息*/
    Method realMethod =
        target.getClass().getDeclaredMethod(signature.getName(), targetMethod.getParameterTypes());
    Object[] args = point.getArgs();
    /*获取验证组*/
    Class[] groups;
    groups =
        Optional.ofNullable(realMethod.getAnnotation(Validated.class))
            .map(Validated::value)
            .orElse(new Class[0]);
    /*使用hibernate-validator验证*/
    Set<ConstraintViolation<Object>> constraintViolations =
        ValidateUtils.validateMethod(target, realMethod, args, groups);
    if (CollUtil.isEmpty(constraintViolations)) {
      returnValue = point.proceed(args);
    } else {
      /*向上抛出验证失败的异常，交由统一异常管理处理*/
      throw new ConstraintViolationException(
          StrUtil.format("Service [{}] method parameter unvalidate", signature.getName()),
          constraintViolations);
    }
    return returnValue;
  }
}
