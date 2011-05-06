package com.twolattes.json;

import static com.twolattes.json.MapDescriptor.shouldWrapKeys;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twolattes.json.types.JsonType;

class EntityMarshallerImpl<T> implements EntityMarshaller<T> {

  private final EntityDescriptor<T> descriptor;
  private final EntityDescriptor<T> collectionDescriptor;
  private final Class<T> clazz;

  @SuppressWarnings({"unchecked", "rawtypes"})
  EntityMarshallerImpl(
      Class<T> clazz,
      Map<Type, Class<? extends JsonType<?, ?>>> types) {
    this.clazz = clazz;
    Pair<? extends EntityDescriptor, Entity> pair = new DescriptorFactory()
      .create(clazz, new DescriptorFactory.EntityDescriptorStore(), types);
    this.descriptor = pair.left;
    this.collectionDescriptor =
        pair.right != null && pair.right.inline() ?
            new InlinedEntityDescriptor<T>(pair.left) :
            pair.left;
  }

  public Json.Object marshall(T entity) {
    return marshall(entity, null);
  }

  public Json.Object marshall(T entity, String view) {
    return (Json.Object) descriptor.marshall(entity, view);
  }

  public Json.Array marshallList(Collection<? extends T> entities) {
    return marshallList(entities, null);
  }

  public Json.Array marshallList(Collection<? extends T> entities, String view) {
    Json.Array a = Json.array();
    for (T entity : entities) {
      a.add(collectionDescriptor.marshall(entity, view));
    }
    return a;
  }

  public Json.Object marshallMap(Map<String, ? extends T> map) {
    return marshallMap(map, (String) null);
  }

  public Json.Object marshallMap(Map<String, ? extends T> map, String view) {
    Json.Object o = Json.object();
    for (String key : map.keySet()) {
      o.put(Json.string(key), collectionDescriptor.marshall(map.get(key), view));
    }
    return o;
  }

  public T unmarshall(Json.Value entity) {
    return unmarshall(entity, null);
  }

  public T unmarshall(Json.Value entity, String view) {
    return clazz.cast(descriptor.unmarshall(entity, view));
  }

  public List<T> unmarshallList(Json.Array array) {
    return unmarshallList(array, null);
  }

  public List<T> unmarshallList(Json.Array array, String view) {
    if (array.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> list = new ArrayList<T>(array.size());
    for (Json.Value value : array) {
      list.add(clazz.cast(collectionDescriptor.unmarshall(value, view)));
    }
    return list;
  }

  public Map<String, T> unmarshallMap(Json.Object object) {
    return unmarshallMap(object, (String) null);
  }

  public Map<String, T> unmarshallMap(Json.Object object, String view) {
    if (object.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, T> map = new HashMap<String, T>(object.size());
    for (Json.String key : object.keySet()) {
      map.put(
          key.getString(),
          clazz.cast(collectionDescriptor.unmarshall(object.get(key), view)));
    }
    return map;
  }

  public void unmarshallStream(Reader reader,
      final Marshaller.Generator<? super T> generator) throws IOException {
    Json.generate(reader, new Json.Generator() {
      public void yield(Json.Value value) {
        generator.yield(unmarshall(value));
      }
    });
  }

  public <K> Json.Object marshallMap(Map<K, ? extends T> map,
      Marshaller<K> keyMarshaller) {
    if (map.isEmpty()) {
      return Json.object();
    }
    boolean shouldWrapKeys = shouldWrapKeys(keyMarshaller);
    Json.Object o = Json.object();
    for (K key : map.keySet()) {
      Json.Value value = keyMarshaller.marshall(key);
      o.put(
          shouldWrapKeys ?
              Json.string(value.toString()) : (Json.String) value,
          descriptor.marshall(map.get(key), null));
    }
    return o;
  }

  public <K> Map<K, T> unmarshallMap(Json.Object object,
      Marshaller<K> keyMarshaller) {
    if (object.isEmpty()) {
      return Collections.emptyMap();
    }
    boolean shouldUnwrapKeys = shouldWrapKeys(keyMarshaller);
    Map<K, T> map = new HashMap<K, T>(object.size());
    for (Json.String key : object.keySet()) {
      map.put(
          keyMarshaller.unmarshall(
              shouldUnwrapKeys ? Json.fromString(key.getString()) : key),
          clazz.cast(descriptor.unmarshall(object.get(key), null)));
    }
    return map;
  }

}
