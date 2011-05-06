package com.twolattes.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.twolattes.json.types.JsonType;

/**
 * A JSON value.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.METHOD})
public @interface Value {
  /**
   * The JSON name. By default, the field name is chosen.
   */
  String name() default "";

  /**
   * Whether to inline the value or not. Only base types and single-valued
   * entities can be inlined. Unnecessary and meaningless for fields of an
   * {@link Entity @Entity} type that has {@link Entity#inline inline} set to
   * {@code true}.
   */
  boolean inline() default false;

  /**
   * A type that should be used to marshall and unmarshall this value.
   */
  @SuppressWarnings("rawtypes")
  Class<? extends JsonType> type() default JsonType.class;

  /**
   * Types that should be used to marshall and unmarshall this value.
   */
  Class<? extends JsonType<?, ?>>[] types() default {};

  /**
   * Whether the value is optional or not.
   */
  boolean optional() default false;

  /**
   * Views in which this value should be included.
   */
  String[] views() default {};

  /**
   * Whether or not to use the {@link Enum#ordinal()} value to represent
   * enum constants. Using an enum's ordinal produces terser JSON, but
   * is vulnerable to constant reordering.
   */
  boolean ordinal() default false;

  /**
   * Whether to embed the value or not. Only entities can be embedded.
   */
  boolean embed() default false;
}
