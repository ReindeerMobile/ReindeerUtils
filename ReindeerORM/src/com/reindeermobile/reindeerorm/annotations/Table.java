package com.reindeermobile.reindeerorm.annotations;

import com.reindeermobile.reindeerorm.EntityManagerFactory;
import com.reindeermobile.reindeerutils.view.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Table {
	/**
	 * Table name without prefix. Can give the table prefix in
	 * datapase.properties file or with {@link Properties} parameter at
	 * {@link EntityManagerFactory#init(android.content.Context, Properties, Class...)}
	 * <br>
	 */
	String name() default StringUtils.EMPTY_STRING;
}