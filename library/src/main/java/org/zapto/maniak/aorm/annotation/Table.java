package org.zapto.maniak.aorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zapto.maniak.aorm.table.TableUtils;

/**
 * @author Seba_0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {

    String name() default TableUtils.DEFAULT;

    int version() default 0;

    Index[] indexes() default {};
}
