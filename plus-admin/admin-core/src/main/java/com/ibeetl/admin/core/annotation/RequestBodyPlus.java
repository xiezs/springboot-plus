package com.ibeetl.admin.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参见 {@link com.ibeetl.admin.core.conf.springmvc.resolve.RequestBodyPlusProcessor} 解析
 * @author 一日看尽长安花
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBodyPlus {
	
	/**
	 * 用一个json path 将json请求转换为被注解的参数的类型对象。<br/>
	 * 意图避免原本的{@link org.springframework.web.bind.annotation.RequestBody} 注解必须创建新的对象接收参数，降低项目的类数量<br/>
	 * 如果默认未空值，则整个json请求都将被转换为参数类型。<br/>
	 * 以参数类型为目标，有如下情况：<br/>
	 * Object : json str = {...} ；最终转换为Object<br/>
	 * Collect : json str = {...} -> [{...}] ; 以集合的泛型类型（如果泛型不存在，以Object为目标）为目标转换为集合中的一个对象（整个json作为一个元素）
	 * Object : json str = [{....}] ；最终转换为Object<br/>
	 * Collect : json str = [{...}] -> [{...}] ; 以集合的泛型类型（如果泛型不存在，以Object为目标）为目标转换为集合中的一个对象（整个json作为一个元素）<br/>
	 * 以上遵循{@link cn.hutool.json.JSONUtil} 的转换规则，忽视转换错误，不能转换时，返回一个空值对象
	 */
	String value() default "";
	
}
