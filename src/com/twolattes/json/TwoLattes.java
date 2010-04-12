package com.twolattes.json;

import static com.twolattes.json.BigDecimalDescriptor.BIG_DECIMAL_DESC;
import static com.twolattes.json.BooleanDescriptor.BOOLEAN_DESC;
import static com.twolattes.json.ByteDescriptor.BYTE_DESC;
import static com.twolattes.json.CharacterDescriptor.CHARARACTER_DESC;
import static com.twolattes.json.DoubleDescriptor.DOUBLE_DESC;
import static com.twolattes.json.FloatDescriptor.FLOAT_DESC;
import static com.twolattes.json.IntegerDescriptor.INT_DESC;
import static com.twolattes.json.Json.NULL;
import static com.twolattes.json.LongDescriptor.LONG_DESC;
import static com.twolattes.json.ShortDescriptor.SHORT_DESC;
import static com.twolattes.json.StringDescriptor.STRING_DESC;
import static com.twolattes.json.Unification.extractRawType;
import static com.twolattes.json.Unification.getActualTypeArgument;
import static java.lang.String.format;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.twolattes.json.JsonVisitor.Illegal;
import com.twolattes.json.types.JsonType;

/**
 * The entry point to the JsonMarshaller. Creates {@link Marshaller} instances
 * for specific types.
 */
public final class TwoLattes {

  /* don't instantiate */ private TwoLattes() {}

  static final JsonVisitor<Json.String> STRING_NOT_NULL = new Illegal<Json.String>() {
    @Override
    public Json.String caseString(Json.String string) {
      return string;
    }
  };

  static final JsonVisitor<Json.String> STRING_OR_NULL = new Illegal<Json.String>() {
    @Override
    public Json.String caseNull() {
      return NULL;
    }
    @Override
    public Json.String caseString(Json.String string) {
      return string;
    }
  };

  static final JsonVisitor<Json.Number> NUMBER_NOT_NULL = new Illegal<Json.Number>() {
    @Override
    public Json.Number caseNumber(Json.Number number) {
      return number;
    }
  };

  static final JsonVisitor<Json.Number> NUMBER_OR_NULL = new Illegal<Json.Number>() {
    @Override
    public Json.Number caseNull() {
      return NULL;
    }
    @Override
    public Json.Number caseNumber(Json.Number number) {
      return number;
    }
  };

  static final JsonVisitor<Json.Boolean> BOOLEAN_NOT_NULL = new Illegal<Json.Boolean>() {
    @Override
    public Json.Boolean caseBoolean(Json.Boolean bool) {
      return bool;
    }
  };

  static final JsonVisitor<Json.Boolean> BOOLEAN_OR_NULL = new Illegal<Json.Boolean>() {
    @Override
    public Json.Boolean caseNull() {
      return NULL;
    }
    @Override
    public Json.Boolean caseBoolean(Json.Boolean bool) {
      return bool;
    }
  };

  static final JsonVisitor<Json.Array> ARRAY_OR_NULL = new Illegal<Json.Array>() {
    @Override
    public Json.Array caseNull() {
      return NULL;
    }
    @Override
    public Json.Array caseArray(Json.Array array) {
      return array;
    }
  };

  static final JsonVisitor<Json.Object> OBJECT_OR_NULL = new Illegal<Json.Object>() {
    @Override
    public Json.Object caseNull() {
      return NULL;
    }
    @Override
    public Json.Object caseObject(Json.Object object) {
      return object;
    }
  };

  static final JsonVisitor<Json.Null> NULL_ONLY = new Illegal<Json.Null>() {
    @Override
    public Json.Null caseNull() {
      return NULL;
    }
  };

  private static final Map<Class<?>, Pair<? extends Descriptor<?, ?>, ? extends JsonVisitor<?>>> map = makeMap();
  private static Map<Class<?>, Pair<? extends Descriptor<?, ?>, ? extends JsonVisitor<?>>> makeMap() {
    Map<Class<?>, Pair<? extends Descriptor<?, ?>, ? extends JsonVisitor<?>>> map =
      new HashMap<Class<?>, Pair<? extends Descriptor<?, ?>, ? extends JsonVisitor<?>>>();
    map.put(Byte.class, Pair.of(BYTE_DESC, NUMBER_OR_NULL));
    map.put(Byte.TYPE, Pair.of(BYTE_DESC, NUMBER_NOT_NULL));
    map.put(Short.class, Pair.of(SHORT_DESC, NUMBER_OR_NULL));
    map.put(Short.TYPE, Pair.of(SHORT_DESC, NUMBER_NOT_NULL));
    map.put(Integer.class, Pair.of(INT_DESC, NUMBER_OR_NULL));
    map.put(Integer.TYPE, Pair.of(INT_DESC, NUMBER_NOT_NULL));
    map.put(Long.class, Pair.of(LONG_DESC, NUMBER_OR_NULL));
    map.put(Long.TYPE, Pair.of(LONG_DESC, NUMBER_NOT_NULL));
    map.put(BigDecimal.class, Pair.of(BIG_DECIMAL_DESC, NUMBER_OR_NULL));
    map.put(Float.class, Pair.of(FLOAT_DESC, NUMBER_OR_NULL));
    map.put(Float.TYPE, Pair.of(FLOAT_DESC, NUMBER_NOT_NULL));
    map.put(Double.class, Pair.of(DOUBLE_DESC, NUMBER_OR_NULL));
    map.put(Double.TYPE, Pair.of(DOUBLE_DESC, NUMBER_NOT_NULL));
    map.put(String.class, Pair.of(STRING_DESC, STRING_OR_NULL));
    map.put(Character.class, Pair.of(CHARARACTER_DESC, STRING_OR_NULL));
    map.put(Character.TYPE, Pair.of(CHARARACTER_DESC, STRING_NOT_NULL));
    map.put(Boolean.class, Pair.of(BOOLEAN_DESC, BOOLEAN_OR_NULL));
    map.put(Boolean.TYPE, Pair.of(BOOLEAN_DESC, BOOLEAN_NOT_NULL));
    return map;
  }

  /**
   * Creates a {@link Marshaller} for a specific type.
   *
   * @param clazz the {@link Byte}, {@link Short}, {@link Integer},
   *   {@link Long}, {@link BigInteger}, {@link BigDecimal}, {@link Float},
   *   {@link Double}, {@link String}, or {@link Boolean} class, an {@link Enum}
   *   type, or a class annotated with {@link Entity @Entity}
   */
  public static <T> Marshaller<T> createMarshaller(Class<T> clazz) {
    return new Builder().createMarshaller(clazz);
  }

  /**
   * Creates an {@link EntityMarshaller} for a specific entity type.
   *
   * @param clazz A class annotated with {@link Entity @Entity}
   */
  public static <T> EntityMarshaller<T> createEntityMarshaller(Class<T> clazz) {
    return new Builder().createEntityMarshaller(clazz);
  }

  public static Builder withType(Class<? extends JsonType<?, ?>> clazz) {
    return new Builder().withType(clazz);
  }

  /**
   * {@link EntityMarshaller} builder.
   */
  public static class Builder {

    private Map<Type, Class<? extends JsonType<?, ?>>> types =
      Collections.<Type, Class<? extends JsonType<?, ?>>>emptyMap();

    public Builder withType(Class<? extends JsonType<?, ?>> clazz) {
      Class<?> rawType = extractRawType(
          getActualTypeArgument(clazz, JsonType.class, 0));
      if (rawType.equals(Array.class)) {
        throw new IllegalArgumentException(
            format(
                "%s overriding array's marshalling behavior cannot be registered",
                JsonType.class.getSimpleName()));
      }
      if (types.isEmpty()) {
        types = new HashMap<Type, Class<? extends JsonType<?, ?>>>();
      }
      types.put(rawType, clazz);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Marshaller<T> createMarshaller(Class<T> clazz) {
      Pair<? extends Descriptor<?, ?>, ? extends JsonVisitor<?>> p = map.get(clazz);
      if (p != null) {
        return new DescriptorBackedMarshaller(p.left, p.right);
      }
      Class<? extends JsonType<?, ?>> type = types.get(clazz);
      if (type != null) {
        return new DescriptorBackedMarshaller(
            new UserTypeDescriptor(Instantiator.newInstance(type)),
            getJsonVisitor((Class<? extends Json.Value>)
                getActualTypeArgument(type, JsonType.class, 1)));
      }
      if (Enum.class.isAssignableFrom(clazz)) {
        return new DescriptorBackedMarshaller(
            new EnumNameDescriptor((Class<? extends Enum>) clazz), STRING_OR_NULL);
      }
      return createEntityMarshaller(clazz);
    }

    public <T> EntityMarshaller<T> createEntityMarshaller(Class<T> clazz) {
      return new EntityMarshallerImpl<T>(clazz, types);
    }

    Class<?> get(Type type) {
      return types.get(type);
    }

    private static JsonVisitor<? extends Json.Value> getJsonVisitor(
        Class<? extends Json.Value> type) {
      if (Json.String.class.isAssignableFrom(type)) {
        return STRING_OR_NULL;
      }
      if (Json.Number.class.isAssignableFrom(type)) {
        return NUMBER_NOT_NULL;
      }
      if (Json.Boolean.class.isAssignableFrom(type)) {
        return BOOLEAN_OR_NULL;
      }
      if (Json.Object.class.isAssignableFrom(type)) {
        return OBJECT_OR_NULL;
      }
      if (Json.Array.class.isAssignableFrom(type)) {
        return ARRAY_OR_NULL;
      }
      if (Json.Null.class.isAssignableFrom(type)) {
        return NULL_ONLY;
      }
      throw new AssertionError("unknown JSON value type: " + type.getName());
    }

  }

}
