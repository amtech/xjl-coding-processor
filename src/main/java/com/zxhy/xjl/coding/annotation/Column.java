package com.zxhy.xjl.coding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 数据库字段的定义
 * @author leasonlive
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface  Column {
	/**
	 * 列的名称
	 * @return
	 */
	public String name();
	/**
	 * 列的类型，如varchar(20),date,int(10)等
	 * @return
	 */
	public String type();
	/**
	 * 列是不是主键，默认是false
	 * @return
	 */
	public boolean pk() default false;
	
	/**
	 * 列是不是外键
	 * @return
	 */
	public Class fkDomain() default Object.class;
	/**
	 * 外键字符串的描述，首先建议使用fkClass，如果不能使用fkClass再用fk字符串描述
	 * @return
	 */
	public String fk() default "";
	
}
