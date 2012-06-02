package com.reindeermobile.reindeerorm.entity.annotations;

import com.reindeermobile.reindeerutils.view.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Column {
	String name() default StringUtils.EMPTY_STRING;
}