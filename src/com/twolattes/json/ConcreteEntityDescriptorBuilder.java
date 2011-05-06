package com.twolattes.json;

import static com.twolattes.json.AbstractFieldDescriptor.GetSetFieldDescriptor.Type.GETTER;
import static com.twolattes.json.AbstractFieldDescriptor.GetSetFieldDescriptor.Type.SETTER;
import static com.twolattes.json.Json.string;
import static com.twolattes.json.Unification.getActualTypeArgument;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.twolattes.json.AbstractFieldDescriptor.DirectAccessFieldDescriptor;
import com.twolattes.json.AbstractFieldDescriptor.GetSetFieldDescriptor;
import com.twolattes.json.DescriptorFactory.EntityDescriptorStore;
import com.twolattes.json.types.JsonType;

class ConcreteEntityDescriptorBuilder {

  private static final Pattern GETTER_PATTERN = compile("(get|is)[A-Z0-9_].*");
  private static final Pattern SETTER_PATTERN = compile("set[A-Z0-9_].*");

  private final Map<Json.String, FieldDescriptor> fieldDescriptors =
    new HashMap<Json.String, FieldDescriptor>();

  @SuppressWarnings({"unchecked", "rawtypes"})
  EntityDescriptor<?> build(
      Class<?> entityClass,
      EntityDescriptorStore store,
      Map<Type, Class<? extends JsonType<?, ?>>> types,
      ConcreteEntityDescriptor<?> parent) {
    Map<String, Method[]> getterSetterPairsByFieldName =
      new HashMap<String, Method[]>();

    Value annotation;
    for (Field field : entityClass.getDeclaredFields()) {
      annotation = field.getAnnotation(Value.class);
      if (annotation != null) {
        field.setAccessible(true);
        add(buildFieldDescriptor(
              annotation,
              field.getGenericType(),
              "the field '" + field.getName() + "'",
              new DirectAccessFieldDescriptor(field),
              store,
              types));
      }
    }

    for (Method m : entityClass.getDeclaredMethods()) {
      annotation = m.getAnnotation(Value.class);
      if (annotation != null) {
        String name = m.getName();
        if (isGetterName(name)) {
          name = annotation.name().isEmpty() ? GETTER.name(m) : annotation.name();
          Method[] pair = getterSetterPairsByFieldName.get(name);
          if (pair == null) {
            pair = new Method[] {m, null};
            getterSetterPairsByFieldName.put(name, pair);
          } else if (pair[0] == null) {
            pair[0] = m;
          } else {
            throw new IllegalArgumentException("Non-unique getter for field: " + name);
          }
        } else if (isSetterName(name)) {
          name = annotation.name().isEmpty() ? SETTER.name(m) : annotation.name();
          Method[] pair = getterSetterPairsByFieldName.get(name);
          if (pair == null) {
            pair = new Method[] {null, m};
            getterSetterPairsByFieldName.put(name, pair);
          } else if (pair[1] == null) {
            pair[1] = m;
          } else {
            throw new IllegalArgumentException("Non-unique setter for field: " + name);
          }
        } else {
          throw new IllegalArgumentException(
              "@" + Value.class.getSimpleName() +
              " on method that is neither a getter nor a setter: " + name);
        }
      }
    }

    for (Map.Entry<String, Method[]> e : getterSetterPairsByFieldName.entrySet()) {
      Method getter = e.getValue()[0];
      Method setter = e.getValue()[1];

      if (getter == null) {
        throw new IllegalArgumentException(
            "No getter with @Value corresponding to " + setter);
      }
      if (getter.getParameterTypes().length > 0) {
        throw new IllegalArgumentException(
            getter + " should not take any parameters");
      }
      Value gValue = getter.getAnnotation(Value.class);

      if (setter != null) {
        if (setter.getParameterTypes().length != 1) {
          throw new IllegalArgumentException(
              setter + " should take exactly one parameter");
        }
        if (!setter.getReturnType().equals(Void.TYPE)) {
          throw new IllegalArgumentException(
              setter + " return type should be void");
        }
        if (!getter.getGenericReturnType().equals(setter.getGenericParameterTypes()[0])) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() +
              "(" + setter.getGenericParameterTypes()[0] + ") disagree on field type");
        }

        Value sValue = setter.getAnnotation(Value.class);
        if (!gValue.name().equals(sValue.name())) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value name: " +
              "\"" + gValue.name() + "\" != \"" + sValue.name() + "\"");
        }
        if (gValue.optional() != sValue.optional()) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value optional");
        }
        if (gValue.inline() != sValue.inline()) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value inline");
        }
        if (gValue.embed() != sValue.embed()) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value embed");
        }
        if (gValue.ordinal() != sValue.ordinal()) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value ordinal");
        }
        if (!gValue.type().equals(sValue.type())) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value type: " +
              gValue.type() + " != " + sValue.type());
        }
        if ((gValue.types().length > 0 || sValue.types().length > 0) &&
            !new HashSet(asList(gValue.types())).equals(new HashSet(asList(sValue.types())))) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value types: " +
              asList(gValue.types()) + " != " + asList(sValue.types()));
        }
        if ((gValue.views().length > 0 || sValue.views().length > 0) &&
            !new HashSet(asList(gValue.views())).equals(new HashSet(asList(sValue.views())))) {
          throw new IllegalArgumentException(
              getter + " and ." + setter.getName() + "(...) disagree on @Value views: " +
              asList(gValue.views()) + " != " + asList(sValue.views()));
        }
      }

      add(buildFieldDescriptor(
          gValue,
          getter.getGenericReturnType(),
          "the getter '" + getter.getName() + "'",
          new GetSetFieldDescriptor(getter, setter),
          store,
          types));
    }

    try {
      return new ConcreteEntityDescriptor(
          entityClass,
          new HashSet<FieldDescriptor>(fieldDescriptors.values()),
          parent);
    } finally {
      fieldDescriptors.clear();
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private FieldDescriptor buildFieldDescriptor(
      Value annotation,
      Type fieldType,
      String fieldDescriptionForExceptions,
      AbstractFieldDescriptor fieldDescriptor,
      EntityDescriptorStore store,
      Map<Type, Class<? extends JsonType<?, ?>>> availableTypes) {
    if (!annotation.name().isEmpty()) {
      fieldDescriptor.setJsonName(annotation.name());
    }
    if (annotation.ordinal()) {
      fieldDescriptor.setOrdinal(true);
    }
    for (String view : annotation.views()) {
      fieldDescriptor.addView(view);
    }

    availableTypes = getTypesAvailableForField(annotation, availableTypes);

    Descriptor entityDescriptor;
    Boolean inlineEntity = null;
    Boolean embedEntity = null;
    if (availableTypes.containsKey(fieldType)) {
      entityDescriptor = new UserTypeDescriptor(
          Instantiator.newInstance(availableTypes.get(fieldType)));
    } else {
      Pair<Descriptor, Entity> pair = new DescriptorFactory().create(
          fieldType, store, fieldDescriptor, availableTypes);
      entityDescriptor = pair.left;
      if (pair.right != null) {
        inlineEntity = pair.right.inline();
        embedEntity = pair.right.embed();
      }
    }

    boolean inline = annotation.inline() || (inlineEntity != null && inlineEntity);
    boolean embed = annotation.embed() || (embedEntity != null && embedEntity);

    if (inline) {
      if (!entityDescriptor.isInlineable()) {
        throw new IllegalArgumentException(
            "entity of " + fieldDescriptionForExceptions + " is not inlineable");
      }
    }
    if (embed) {
      if (entityDescriptor instanceof EntityDescriptor) {
        Set<FieldDescriptor> fieldDescriptors =
            new HashSet<FieldDescriptor>(((EntityDescriptor) entityDescriptor).getFieldDescriptors());

        fieldDescriptors.addAll(
            ((EntityDescriptor) entityDescriptor).getAllFieldDescriptors());

        if (entityDescriptor instanceof PolymorphicEntityDescriptor) {
          Json.String discriminatorName = string(
              ((PolymorphicEntityDescriptor) entityDescriptor).getDiscriminatorName());
          if (get(discriminatorName) != null) {
            throw new IllegalArgumentException(
                "entity of " + fieldDescriptionForExceptions + " is not embeddable "
                + "because of a conflict with the discriminator name '" + discriminatorName + "'");
          }
        }

        for (FieldDescriptor d : fieldDescriptors) {
          if (get(d, d.getJsonName()) != null) {
            throw new IllegalArgumentException(
                "entity of " + fieldDescriptionForExceptions + " is not embeddable "
                + "because of a conflict on field '" + fieldDescriptor.getJsonName() + "'");
          }
        }
      } else {
        throw new IllegalArgumentException(
            "entity of " + fieldDescriptionForExceptions + " is not embeddable");
      }
      // TODO throw exception if
      // - already inlined (add test)
    }

    if (inline && entityDescriptor instanceof EntityDescriptor) {
      fieldDescriptor.setDescriptor(
          new InlinedEntityDescriptor((EntityDescriptor) entityDescriptor));
    } else {
      fieldDescriptor.setDescriptor(entityDescriptor);
    }

    if (embed && entityDescriptor instanceof EntityDescriptor) {
      Set<FieldDescriptor> fieldDescriptors =
          ((EntityDescriptor) entityDescriptor).getFieldDescriptors();
      Set<FieldDescriptor> newFieldDescriptors = new HashSet<FieldDescriptor>();
      Iterator<FieldDescriptor> iterator = fieldDescriptors.iterator();
      while (iterator.hasNext()) {
        FieldDescriptor fd = iterator.next();
        if (fd instanceof AbstractFieldDescriptor) {
          iterator.remove();
          newFieldDescriptors.add(new OptionalFieldDescriptor(fd));
        }
      }
      fieldDescriptors.addAll(newFieldDescriptors);
      return annotation.optional() ?
          new OptionalFieldDescriptor(new EmbeddedFieldDescriptor(fieldDescriptor)) :
          new EmbeddedFieldDescriptor(fieldDescriptor);
    }

    return annotation.optional() ?
        new OptionalFieldDescriptor(fieldDescriptor) :
        fieldDescriptor;
  }

  @SuppressWarnings("unchecked")
  private Map<Type, Class<? extends JsonType<?, ?>>> getTypesAvailableForField(
      Value annotation,
      Map<Type, Class<? extends JsonType<?, ?>>> availableTypes) {
    if (annotation.type().equals(JsonType.class) &&
        annotation.types().length == 0) {
      return availableTypes;
    }
    Map<Type, Class<? extends JsonType<?, ?>>> types =
      new HashMap<Type, Class<? extends JsonType<?, ?>>>(availableTypes);
    if (!annotation.type().equals(JsonType.class)) {
      JsonType<?, ?> type = Instantiator.newInstance(annotation.type());
      types.put(
          getMarshalledType(type),
          (Class<? extends JsonType<?, ?>>) type.getClass());
    }
    for (Class<? extends JsonType<?, ?>> t : annotation.types()) {
      JsonType<?, ?> type = Instantiator.newInstance(t);
      types.put(
          getMarshalledType(type),
          (Class<? extends JsonType<?, ?>>) type.getClass());
    }
    return types;
  }

  private static Type getMarshalledType(JsonType<?, ?> type) {
    Type mType = getActualTypeArgument(type.getClass(), JsonType.class, 0);
    return mType instanceof ParameterizedType ?
        ((ParameterizedType) mType).getRawType() : mType;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private FieldDescriptor get(Json.String name) {
    for (Map.Entry<Json.String, FieldDescriptor> entry : fieldDescriptors.entrySet()) {
      if (name.equals(entry.getKey())) {
        return entry.getValue();
      }
      if (entry.getValue() instanceof EmbeddedFieldDescriptor) {
        Set<FieldDescriptor> set =
            ((EntityDescriptor) entry.getValue().getDescriptor()).getAllFieldDescriptors();
        for (FieldDescriptor current : set) {
          Json.String fieldDescriptorName = current.getJsonName().isEmpty() ?
              current.getFieldName() : current.getJsonName();
          if (name.equals(fieldDescriptorName)) {
            return current;
          }
        }
      }
    }
    return null;
  }

  private FieldDescriptor get(FieldDescriptor fieldDescriptor, Json.String jsonName) {
    return get(jsonName.isEmpty() ? fieldDescriptor.getFieldName() : jsonName);
  }

  private void add(FieldDescriptor fieldDescriptor) {
    Json.String name = fieldDescriptor.getJsonName().isEmpty() ?
        fieldDescriptor.getFieldName() : fieldDescriptor.getJsonName();
    if (fieldDescriptors.containsKey(name)) {
      throw new IllegalArgumentException(
          "Value with name " + name + " is described multiple times.");
    }
    fieldDescriptors.put(name, fieldDescriptor);
  }

  boolean isGetterName(String name) {
    return GETTER_PATTERN.matcher(name).matches();
  }

  boolean isSetterName(String name) {
    return SETTER_PATTERN.matcher(name).matches();
  }

}
