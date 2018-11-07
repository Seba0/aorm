package org.zapto.maniak.aorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zapto.maniak.aorm.table.TableUtils;
import org.zapto.maniak.aorm.table.type.Collate;
import org.zapto.maniak.aorm.table.type.DateFormat;

/**
 * @author Seba_0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default TableUtils.DEFAULT;

    boolean unique() default false;

    boolean nullable() default true;

    Collate collate() default Collate.NOT_SET;

    DateFormat dateFormat() default DateFormat.UNIX;
}