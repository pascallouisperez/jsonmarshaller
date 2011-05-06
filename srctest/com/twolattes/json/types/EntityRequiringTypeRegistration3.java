package com.twolattes.json.types;

import static com.twolattes.json.Json.number;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import com.twolattes.json.Entity;
import com.twolattes.json.Json;
import com.twolattes.json.Value;

@Entity
public class EntityRequiringTypeRegistration3 {

  @Value(optional = true, type = IdJsonType.class)
  Set<Id<EntityRequiringTypeRegistration3>> ids;

  Map<Id<Long>, Set<URI>> uriMap;

  // Note: URLType irrelevant, intentionally
  @Value(type = URIType.class, types = {IdJsonType.class, URLType.class})
  Map<Id<Long>, Set<URI>> getUriMap() {
    return uriMap;
  }

  @Value(type = URIType.class, types = {IdJsonType.class, URLType.class})
  void setUriMap(Map<Id<Long>, Set<URI>> map) {
    this.uriMap = map;
  }

  static class Id<T> {
    long id;
    Id(long id) {
      this.id = id;
    }
  }

  static class IdJsonType extends NullSafeType<Id<?>, Json.Number> {

    @Override
    protected Json.Number nullSafeMarshall(Id<?> entity) {
      return number(entity.id);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected Id<?> nullSafeUnmarshall(Json.Number number) {
      return new Id(number.getNumber().longValue());
    }

  }

}
