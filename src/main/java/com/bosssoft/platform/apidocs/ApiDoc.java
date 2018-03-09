package com.bosssoft.platform.apidocs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
public @interface ApiDoc {

    /**
     * result class for single
     * @return
     */
	Class<?> result() default Null.class;

    /**
     * result class for map
     */
	Class<?>[] value() default {};
	
	String[] key() default {};

    /**
     * request url
     */
	String url() default "";

    /**
     * request method
     */
	String method() default "get";

    final class Null{

    }
}
