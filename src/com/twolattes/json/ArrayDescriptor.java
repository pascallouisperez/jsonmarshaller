package com.twolattes.json;

import java.lang.reflect.Array;

/**
 * A descriptor for arrays.
 */
class ArrayDescriptor extends AbstractDescriptor<Object, Json.Value> {
  private final Descriptor<Object, Json.Value> elementsDescriptor;

  @SuppressWarnings({"unchecked", "rawtypes"})
  ArrayDescriptor(Descriptor elementsDescriptor) {
    super(Array.class, Json.Array.class);
    this.elementsDescriptor = elementsDescriptor;
  }

  @Override
  public boolean isInlineable() {
    return elementsDescriptor.isInlineable();
  }

  public Json.Value marshall(Object entity, String view) {
    if (entity == null) {
      return Json.NULL;
    }
    Json.Array jsonArray = Json.array();
    int l = Array.getLength(entity);
    for (int i = 0; i < l; i++) {
      jsonArray.add(elementsDescriptor.marshallArray(entity, i, view));
    }
    return jsonArray;
  }

  public Object unmarshall(Json.Value object, String view) {
    if (Json.NULL.equals(object)) {
      return null;
    }
    Json.Array jsonArray = (Json.Array) object;
    Object array = Array.newInstance(
        elementsDescriptor.getReturnedClass(),
        jsonArray.size());
    int length = Array.getLength(array);
    for (int i = 0; i < length; i++) {
      elementsDescriptor.unmarshallArray(array, jsonArray.get(i), i, view);
    }
    return array;
  }

  @Override
  public String toString() {
    return elementsDescriptor + "[]";
  }

}