package com.twolattes.json;

import static com.twolattes.json.Json.NULL;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Descriptor for {@link Map}s.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
final class MapDescriptor extends AbstractDescriptor<Map, Json.Object> {

  private final MapType mapType;
  private final Descriptor<Object, Json.Value> valueDescriptor;
  private final Descriptor<Object, Json.Value> keyDescriptor;

  MapDescriptor(
      MapType mapType,
      Descriptor<?, ?> keyDescriptor,
      Descriptor<?, ?> valueDescriptor) {
    super(Map.class, Json.Object.class);
    this.mapType = mapType;
    this.keyDescriptor = (Descriptor<Object, Json.Value>) keyDescriptor;
    this.valueDescriptor = (Descriptor<Object, Json.Value>) valueDescriptor;
  }

  @Override
  public boolean isInlineable() {
    return valueDescriptor.isInlineable();
  }

  @Override
  public String toString() {
    return "Map<" + keyDescriptor.toString()
        + "," + valueDescriptor.toString() + ">";
  }

  @Override
  public Class<?> getReturnedClass() {
    return mapType.toClass();
  }

  public Json.Object marshall(Map entity, String view) {
    if (entity == null) {
      return NULL;
    } else {
      boolean shouldWrapKeys = shouldWrapKeys(keyDescriptor);
      Map<Object, Object> map = entity;
      Json.Object o = Json.object();
      for (Entry<Object, Object> e : map.entrySet()) {
        Json.Value marshalledKey = keyDescriptor.marshall(e.getKey(), null);
        o.put(
            shouldWrapKeys ?
                Json.string(marshalledKey.toString()) :
                (Json.String) marshalledKey,
            valueDescriptor.marshall(e.getValue(), view));
      }
      return o;
    }
  }

  public Map<?, ?> unmarshall(Json.Object object, String view) {
    if (object.equals(NULL)) {
      return null;
    } else {
      boolean shouldUnwrapKeys = shouldWrapKeys(keyDescriptor);
      Map<Object, Object> map = mapType.newMap();
      Iterator<Json.String> i = object.keySet().iterator();
      while (i.hasNext()) {
        Json.String key = i.next();
        map.put(
            keyDescriptor.unmarshall(
                shouldUnwrapKeys ?
                    Json.fromString(key.getString()) : key, null),
            valueDescriptor.unmarshall(object.get(key), view));
      }
      return map;
    }
  }

  Descriptor<Object, Json.Value> getValueDescriptor() {
    return valueDescriptor;
  }

  static boolean shouldWrapKeys(Descriptor<?, ?> descriptor) {
    return !((Class<?>) descriptor.getMarshalledClass())
      .equals(Json.String.class);
  }

  static boolean shouldWrapKeys(Marshaller<?> marshaller) {
    if (marshaller instanceof DescriptorBackedMarshaller) {
      DescriptorBackedMarshaller<?, ?> castedMarshaller =
        (DescriptorBackedMarshaller<?, ?>) marshaller;
      return shouldWrapKeys(castedMarshaller.getDescriptor());
    }
    return true;
  }

}
