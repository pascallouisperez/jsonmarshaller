package com.twolattes.json;

import static com.twolattes.json.BigDecimalDescriptor.BIG_DECIMAL_DESC;
import static com.twolattes.json.BooleanDescriptor.BOOLEAN_DESC;
import static com.twolattes.json.BooleanDescriptor.BOOLEAN_LITERAL_DESC;
import static com.twolattes.json.ByteDescriptor.BYTE_DESC;
import static com.twolattes.json.ByteDescriptor.BYTE_LITERAL_DESC;
import static com.twolattes.json.CharacterDescriptor.CHARARACTER_DESC;
import static com.twolattes.json.CharacterDescriptor.CHAR_DESC;
import static com.twolattes.json.DoubleDescriptor.DOUBLE_DESC;
import static com.twolattes.json.DoubleDescriptor.DOUBLE_LITERAL_DESC;
import static com.twolattes.json.FloatDescriptor.FLOAT_DESC;
import static com.twolattes.json.FloatDescriptor.FLOAT_LITERAL_DESC;
import static com.twolattes.json.IntegerDescriptor.INTEGER_DESC;
import static com.twolattes.json.IntegerDescriptor.INT_DESC;
import static com.twolattes.json.LongDescriptor.LONG_DESC;
import static com.twolattes.json.LongDescriptor.LONG_LITERAL_DESC;
import static com.twolattes.json.ShortDescriptor.SHORT_DESC;
import static com.twolattes.json.ShortDescriptor.SHORT_LITERAL_DESC;
import static com.twolattes.json.StringDescriptor.STRING_DESC;
import static java.lang.String.format;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.twolattes.json.types.JsonType;

/**
 * An {@link EntityDescriptor} factory.
 */
class DescriptorFactory {

  private static final Map<Type, Descriptor<?, ?>> baseTypes = makeMap();
  private static Map<Type, Descriptor<?, ?>> makeMap() {
    Map<Type, Descriptor<?, ?>> map = new HashMap<Type, Descriptor<?, ?>>();
    map.put(Integer.TYPE, INT_DESC);
    map.put(Integer.class, INTEGER_DESC);
    map.put(Double.TYPE, DOUBLE_LITERAL_DESC);
    map.put(Double.class, DOUBLE_DESC);
    map.put(Short.TYPE, SHORT_LITERAL_DESC);
    map.put(Short.class, SHORT_DESC);
    map.put(Character.TYPE, CHAR_DESC);
    map.put(Character.class, CHARARACTER_DESC);
    map.put(Long.TYPE, LONG_LITERAL_DESC);
    map.put(Long.class, LONG_DESC);
    map.put(Boolean.TYPE, BOOLEAN_LITERAL_DESC);
    map.put(Boolean.class, BOOLEAN_DESC);
    map.put(Float.TYPE, FLOAT_LITERAL_DESC);
    map.put(Float.class, FLOAT_DESC);
    map.put(Byte.TYPE, BYTE_LITERAL_DESC);
    map.put(Byte.class, BYTE_DESC);
    map.put(String.class, STRING_DESC);
    map.put(BigDecimal.class, BIG_DECIMAL_DESC);
    return map;
  }

  /**
   * Gets a {@link Descriptor} for any generic type.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  Pair<Descriptor, Entity> create(
      Type t,
      EntityDescriptorStore store,
      FieldDescriptor fieldDescriptor,
      Map<Type, Class<? extends JsonType<?, ?>>> types) {
    Descriptor descriptor = baseTypes.get(t);
    if (descriptor != null) {
      return Pair.of(descriptor, null);
    }
    if (t instanceof Class) {
      Class<? extends JsonType> userType = types.get(t);
      if (userType != null) {
        return Pair.<Descriptor, Entity>of(
            new UserTypeDescriptor(Instantiator.newInstance(userType)), null);
      }
      Class c = (Class) t;
      if (c.isEnum()) {
        return Pair.<Descriptor, Entity>of(
            fieldDescriptor.useOrdinal() ?
              new EnumOrdinalDescriptor((Class<? extends Enum>) t) :
              new EnumNameDescriptor((Class<? extends Enum>) t),
            null);
      }
      if (c.getComponentType() != null) {
        return Pair.<Descriptor, Entity>of(
            new ArrayDescriptor(
                inlineEntityIfNecessary(create(
                    c.getComponentType(),
                    store, fieldDescriptor, types))),
            null);
      }
      if (Collection.class.isAssignableFrom(c)) {
        throw new IllegalArgumentException(
            "Collection must be parameterized, e.g. List<String>. "
                + "Type: " + t);
      }
      if (Map.class.isAssignableFrom(c)) {
        throw new IllegalArgumentException(
            "Map must be parameterized, e.g. Map<String, String>. "
                + "Type: " + t);
      }
      return Pair.<Descriptor, Entity>of(
          store.contains(c) ?
              new ProxyEntityDescriptor(c, store) :
              create(c, store, types).left,
          (Entity) c.getAnnotation(Entity.class));
    } else if (t instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) t;
      Class rawType = (Class) pt.getRawType();
      Class<? extends JsonType> userType = types.get(rawType);
      if (userType != null) {
        return Pair.<Descriptor, Entity>of(
            new UserTypeDescriptor(Instantiator.newInstance(userType)), null);
      }
      Type[] typeArgs = pt.getActualTypeArguments();
      if (Collection.class.isAssignableFrom(rawType)) {
        return Pair.<Descriptor, Entity>of(
            new CollectionDescriptor(
                rawType,
                inlineEntityIfNecessary(create(
                    typeArgs[0],
                    store, fieldDescriptor, types))),
            null);
      }
      if (Map.class.isAssignableFrom(rawType)) {
        return Pair.<Descriptor, Entity>of(
            new MapDescriptor(
                MapType.fromClass(rawType),
                create(typeArgs[0], store, fieldDescriptor, types).left,
                inlineEntityIfNecessary(
                    create(typeArgs[1], store, fieldDescriptor, types))),
            null);
      }
      return Pair.of(null, null);
    } else if (t instanceof GenericArrayType) {
      return Pair.<Descriptor, Entity>of(
          new ArrayDescriptor(
              inlineEntityIfNecessary(create(
                  ((GenericArrayType) t).getGenericComponentType(),
                  store, fieldDescriptor, types))),
          null);
    } else if (t instanceof TypeVariable) {
      throw new IllegalArgumentException("getters/setters cannot be generic");
    } else if (t instanceof WildcardType) {
      WildcardType wt = (WildcardType) t;
      Type[] upperBounds = wt.getUpperBounds();
      if (upperBounds.length > 1) {
        throw new IllegalStateException();
      }
      if (upperBounds[0].equals(Object.class)) {
        // We process only covariant structures.
        // List<? super A> is no better than List<Object>.
        throw new IllegalArgumentException("contravariant collection, wildcard: " + wt);
      }
      return create(upperBounds[0], store, fieldDescriptor, types);
    } else {
      throw new IllegalArgumentException("Unknown kind of type: " + t);
    }
  }

  /**
   * Creates an {@link EntityDescriptor} for an {link @Entity} class.
   */
  @SuppressWarnings("rawtypes")
  <T> Pair<? extends EntityDescriptor, Entity> create(
      Class<?> c,
      EntityDescriptorStore store,
      Map<Type, Class<? extends JsonType<?, ?>>> types) {

    // verifying that the class is an entity
    Entity annotation = c.getAnnotation(Entity.class);
    if (annotation == null) {
      throw new IllegalArgumentException(c + " is not an entity. Entities must"
          + " be annotated with @Entity.");
    }

    // entities must have a no-argument constructor
    Class<?> entityEnventuallyConstructed = c.isInterface() ?
        annotation.implementedBy() : c;
    try {
      entityEnventuallyConstructed.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(format(
          "%s does not have a no argument constructor", entityEnventuallyConstructed));
    }

    // may be creating it already
    if (store.contains(c)) {
      return Pair.of(store.get(c), annotation);
    }

    // weak reference
    store.put(c);

    // polymorphic entity
    int subclassesLength = annotation.subclasses().length;
    String discriminatorName = annotation.discriminatorName();
    if (subclassesLength > 0) {
      if (discriminatorName == null || discriminatorName.length() == 0) {
        throw new IllegalArgumentException(
            "The discriminatorName option must be used in conjunction of the " +
            "subclasses option: " + c);
      }

      // getting all the concrete descriptors
      Map<String, EntityDescriptor<?>> subclassesDescriptor =
          new HashMap<String, EntityDescriptor<?>>(subclassesLength);
      for (Class<?> subclass : annotation.subclasses()) {
        if (c.isAssignableFrom(subclass)) {
          ConcreteEntityDescriptor<Object> concreteEntityDescriptor =
              createConcreteEntityDescriptor(subclass, store, types);
          String discriminator = concreteEntityDescriptor.getDiscriminator();
          if (subclassesDescriptor.put(
              discriminator, concreteEntityDescriptor) != null) {
            throw new IllegalArgumentException(
                "The discriminator " + discriminator + " is already used by" +
                " the entity " + concreteEntityDescriptor.getReturnedClass() + ".");
          }
        } else {
          throw new IllegalArgumentException(
              "The class " + subclass + " is not a subclass of the" +
              " polymorphic entity " + c + ".");
        }
      }

      return Pair.of(
          createPolymorphicEntityDescriptor(c, store,
              new HashSet<EntityDescriptor<?>>(subclassesDescriptor.values())),
          annotation);
    } else if (discriminatorName != null && discriminatorName.length() > 0) {
      throw new IllegalArgumentException(
          "The subclasses option must be used in conjunction of the " +
          "discriminatorName option: " + c);
    } else {
      return Pair.of(
          createConcreteEntityDescriptor(c, store, types),
          annotation);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> ConcreteEntityDescriptor<T> createConcreteEntityDescriptor(
      Class<?> c,
      EntityDescriptorStore store,
      Map<Type, Class<? extends JsonType<?, ?>>> types) {
    // parent of the entity
    Class<?> parentClass = c.getSuperclass();
    ConcreteEntityDescriptor<?> parent = null;
    while (parentClass != null) {
      Entity parentAnnotation = parentClass.getAnnotation(Entity.class);
      if (parentAnnotation != null) {
        parent = createConcreteEntityDescriptor(parentClass, store, types);
        break;
      } else {
        ensureNoValueInNonEntity(parentClass, parentClass.getDeclaredFields());
        ensureNoValueInNonEntity(parentClass, parentClass.getDeclaredMethods());
      }
      parentClass = parentClass.getSuperclass();
    }

    // getting the descriptor
    EntityDescriptor<?> descriptor = new ConcreteEntityDescriptorBuilder()
      .build(c, store, types, parent);

    store.put(c, descriptor);

    // is this entity inlineable?
    if (c.getAnnotation(Entity.class).inline()) {
      if (!descriptor.isInlineable()) {
        throw new IllegalArgumentException(
            "entity  '" + descriptor.getReturnedClass() + "' is not inlineable." +
        " An entity is inlineable only if it has one property.");
      }
    }

    return (ConcreteEntityDescriptor<T>) descriptor;
  }

  private void ensureNoValueInNonEntity(
      Class<?> nonEntityClass, AccessibleObject[] accessibleObjects) {
    for (AccessibleObject object : accessibleObjects) {
      if (object.getAnnotation(Value.class) != null) {
        throw new IllegalArgumentException(format(
            "cannot have @%s on non-entity %s",
            Value.class.getSimpleName(),
            nonEntityClass));
      }
    }
  }

  private <T> EntityDescriptor<T> createPolymorphicEntityDescriptor(Class<?> c,
      EntityDescriptorStore store, Set<EntityDescriptor<?>> descriptors) {
    Entity annotation = c.getAnnotation(Entity.class);
    PolymorphicEntityDescriptor<T> descriptor = new PolymorphicEntityDescriptor<T>(
        c, annotation.discriminatorName(), descriptors);
    store.put(c, descriptor);
    return descriptor;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Descriptor inlineEntityIfNecessary(Pair<Descriptor, Entity> pair) {
    return pair.left instanceof EntityDescriptor && pair.right != null && pair.right.inline() ?
        new InlinedEntityDescriptor((EntityDescriptor) pair.left) :
        pair.left;
  }

  static class EntityDescriptorStore {
    private final Map<Class<?>, EntityDescriptor<?>> descriptors =
        new HashMap<Class<?>, EntityDescriptor<?>>();
    void put(Class<?> c) {
      descriptors.put(c, null);
    }
    void put(Class<?> c, EntityDescriptor<?> descriptor) {
      descriptors.put(c, descriptor);
    }
    boolean contains(Class<?> c) {
      return descriptors.containsKey(c);
    }
    @SuppressWarnings("rawtypes")
    EntityDescriptor get(final Class<?> c) {
      EntityDescriptor<?> descriptor = descriptors.get(c);
      if (descriptor == null) {
        throw new IllegalStateException("descriptor of " + c + " is weakly stored");
      }
      return descriptor;
    }
  }

}
