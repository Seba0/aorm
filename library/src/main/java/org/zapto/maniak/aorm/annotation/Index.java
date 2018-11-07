package org.zapto.maniak.aorm.annotation;

import java.lang.annotation.*;

import org.zapto.maniak.aorm.table.*;

/**
 * @author Seba_0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    String name() default TableUtils.DEFAULT;

    boolean unique() default false;

    String[] columns();
}
